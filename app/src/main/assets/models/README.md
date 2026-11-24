# 3D models (GLB) for the AR lab

Put the GLB files in this folder (`app/src/main/assets/models/`). The app looks for these exact names:

| Model in the app | File name |
|---|---|
| Bohr atom | `bohr_atom.glb` |
| Proton | `proton.glb` |
| Neutron | `neutron.glb` |
| Nuclear reaction | `nuclear_reaction.glb` |

The names are defined in `domain/model/ArModel.kt` (`glbFile`). When a file is present, the AR lab status card says
"model found"; when it is missing, the built-in interactive 3D drawing (`ui/screens/arlab/Atom3DViewer.kt`) is shown.
The app never crashes because of a missing file.

## Requirements for the files

- Format: glTF 2.0 binary (`.glb`), textures embedded.
- Size: keep each file under ~5 MB; ~20–50k triangles is plenty for a phone.
- Scale: 1 unit = 1 metre. A model about 0.2–0.3 m across looks right on a table in AR.
- Origin at the centre of the model, Y axis up.
- Free sources: Blender (export → glTF 2.0, "glTF Binary"), Sketchfab models with a CC licence (check the licence).

## Turning on real AR placement (camera view)

The project already declares ARCore as **optional** (`AndroidManifest.xml`) and checks support at runtime
(`util/ArAvailabilityChecker.kt`). To place GLB models in the camera view, add the free, open-source SceneView library:

1. In `gradle/libs.versions.toml` add `arsceneview = { group = "io.github.sceneview", name = "arsceneview", version = "<latest>" }`
   and `implementation(libs.arsceneview)` in `app/build.gradle.kts`.
2. Add `<uses-permission android:name="android.permission.CAMERA" />` to the manifest and request it at runtime.
3. In `ArLabScreen.kt`, when `state.arSupport == ArSupport.SUPPORTED && state.selectedHasGlb`, show an `ARScene`
   composable that loads `models/${state.selectedModel.glbFile}` with `modelLoader.createModelInstance(...)`,
   instead of `Atom3DViewer`. Keep `Atom3DViewer` as the fallback for every other case.

SceneView's API changes between major versions, so follow the sample for the version you install.

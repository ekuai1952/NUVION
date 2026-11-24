package kz.nuvion.app.domain.model

/**
 * Models offered in the AR lab. [glbFile] is looked up in app/src/main/assets/models/.
 * When the file is missing, the built-in interactive 3D drawing ([fallback]) is shown.
 */
enum class ArModel(val id: String, val glbFile: String, val fallback: AtomModelType) {
    BOHR_ATOM("bohr", "bohr_atom.glb", AtomModelType.BOHR),
    PROTON("proton", "proton.glb", AtomModelType.PROTON),
    NEUTRON("neutron", "neutron.glb", AtomModelType.NEUTRON),
    NUCLEAR_REACTION("reaction", "nuclear_reaction.glb", AtomModelType.FISSION);

    companion object {
        fun fromId(id: String?): ArModel = entries.firstOrNull { it.id == id } ?: BOHR_ATOM

        fun forLesson(type: AtomModelType): ArModel = when (type) {
            AtomModelType.PROTON -> PROTON
            AtomModelType.NEUTRON -> NEUTRON
            AtomModelType.FISSION, AtomModelType.REACTOR, AtomModelType.NUCLEUS -> NUCLEAR_REACTION
            else -> BOHR_ATOM
        }
    }
}

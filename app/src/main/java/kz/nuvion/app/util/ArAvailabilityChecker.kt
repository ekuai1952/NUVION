package kz.nuvion.app.util

import android.content.Context
import com.google.ar.core.ArCoreApk
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kz.nuvion.app.domain.model.ArModel

enum class ArSupport { CHECKING, SUPPORTED, UNSUPPORTED }

/** Checks ARCore support and which GLB files are bundled. Never throws. */
class ArAvailabilityChecker(context: Context) {

    private val appContext = context.applicationContext

    suspend fun checkArSupport(): ArSupport = try {
        var availability = ArCoreApk.getInstance().checkAvailability(appContext)
        var attempts = 0
        // The first call may return UNKNOWN_CHECKING while ARCore queries the device.
        while (availability.isTransient && attempts < MAX_ATTEMPTS) {
            delay(RETRY_DELAY_MS)
            availability = ArCoreApk.getInstance().checkAvailability(appContext)
            attempts++
        }
        if (availability.isSupported) ArSupport.SUPPORTED else ArSupport.UNSUPPORTED
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        ArSupport.UNSUPPORTED
    } catch (e: LinkageError) {
        ArSupport.UNSUPPORTED
    }

    /** Names of GLB files found in assets/models. */
    fun bundledModels(): Set<String> = try {
        appContext.assets.list(MODELS_DIR)?.toSet().orEmpty()
    } catch (e: Exception) {
        emptySet()
    }

    fun hasGlb(model: ArModel): Boolean = model.glbFile in bundledModels()

    companion object {
        const val MODELS_DIR = "models"
        private const val MAX_ATTEMPTS = 15
        private const val RETRY_DELAY_MS = 200L
    }
}

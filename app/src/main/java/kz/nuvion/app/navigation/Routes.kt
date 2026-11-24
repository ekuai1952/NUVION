package kz.nuvion.app.navigation

import android.net.Uri

object NavArgs {
    const val TOPIC_ID = "topicId"
    const val MATERIAL_ID = "materialId"
    const val TYPE = "type"
    const val MODEL = "model"
}

enum class ResultType(val route: String) {
    QUIZ("quiz"), PRACTICE("practice"), LESSON("lesson");

    companion object {
        fun fromRoute(value: String?): ResultType = entries.firstOrNull { it.route == value } ?: QUIZ
    }
}

/** Route patterns (for NavHost) and builders (for navigate calls). */
object Routes {
    const val HOME = "home"
    const val TOPICS = "topics"
    const val PROFILE = "profile"
    const val LESSON = "lesson/{${NavArgs.TOPIC_ID}}"
    const val QUIZ = "quiz/{${NavArgs.TOPIC_ID}}"
    const val PRACTICE = "practice/{${NavArgs.TOPIC_ID}}"
    const val LIBRARY = "library"
    const val LIBRARY_DETAIL = "library/{${NavArgs.MATERIAL_ID}}"
    const val MISSION_MAP = "missionMap"
    const val AR_LAB = "arLab?${NavArgs.MODEL}={${NavArgs.MODEL}}"
    const val RESULT = "result/{${NavArgs.TYPE}}/{${NavArgs.TOPIC_ID}}"

    fun lesson(topicId: String) = "lesson/${Uri.encode(topicId)}"
    fun quiz(topicId: String) = "quiz/${Uri.encode(topicId)}"
    fun practice(topicId: String) = "practice/${Uri.encode(topicId)}"
    fun libraryDetail(materialId: String) = "library/${Uri.encode(materialId)}"
    fun arLab(modelId: String? = null) =
        if (modelId == null) "arLab" else "arLab?${NavArgs.MODEL}=${Uri.encode(modelId)}"
    fun result(type: ResultType, topicId: String) = "result/${type.route}/${Uri.encode(topicId)}"
}

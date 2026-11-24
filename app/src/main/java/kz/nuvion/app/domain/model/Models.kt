package kz.nuvion.app.domain.model

/** Visual identity of a topic card. */
enum class TopicIcon { ATOM, WAVES, RADIATION, REACTION, REACTOR }

/** Illustrations drawn in lessons, library pages and the AR lab fallback viewer. */
enum class AtomModelType { THOMSON, RUTHERFORD, BOHR, PROTON, NEUTRON, NUCLEUS, FISSION, REACTOR, PHOTON, ISOTOPE }

data class Topic(
    val id: String,
    val order: Int,
    val title: LocalizedText,
    val subtitle: LocalizedText,
    val icon: TopicIcon
)

data class Lesson(
    val topicId: String,
    val title: LocalizedText,
    val xpReward: Int,
    val modelType: AtomModelType,
    val blocks: List<LessonBlock>
)

sealed interface LessonBlock {
    data class Heading(val text: LocalizedText) : LessonBlock
    data class Paragraph(val text: LocalizedText) : LessonBlock
    data class NumberedList(val items: List<LocalizedText>) : LessonBlock
    data class Formula(val formula: LocalizedText, val caption: LocalizedText? = null) : LessonBlock {
        constructor(formula: String, caption: LocalizedText? = null) : this(LocalizedText.same(formula), caption)
    }
    data class Illustration(val model: AtomModelType, val caption: LocalizedText) : LessonBlock
}

data class Quiz(
    val topicId: String,
    val xpReward: Int,
    /** Share of correct answers needed to pass (0..1). */
    val passThreshold: Float,
    val questions: List<Question>
)

data class Question(
    val id: String,
    val text: LocalizedText,
    val options: List<AnswerOption>,
    val explanation: LocalizedText
) {
    val correctOptionId: String get() = options.first { it.isCorrect }.id
}

data class AnswerOption(
    val id: String,
    val text: LocalizedText,
    val isCorrect: Boolean
)

data class PracticeProblem(
    val id: String,
    val topicId: String,
    val statement: LocalizedText,
    val given: List<LocalizedText>,
    val find: List<LocalizedText>,
    val formula: LocalizedText,
    val hint: LocalizedText,
    val solution: List<LocalizedText>,
    val answer: Double,
    /** Allowed relative error, e.g. 0.02 = 2 %. */
    val relativeTolerance: Double,
    val unit: LocalizedText,
    val xpReward: Int
)

enum class LibraryIcon { PROTON, NEUTRON, PHOTON, ISOTOPE, REACTOR, COMING_SOON }

data class Characteristic(val label: LocalizedText, val value: LocalizedText)

data class LibraryMaterial(
    val id: String,
    val title: LocalizedText,
    val icon: LibraryIcon,
    val available: Boolean,
    val definition: LocalizedText = LocalizedText.Empty,
    val characteristics: List<Characteristic> = emptyList(),
    val fact: LocalizedText = LocalizedText.Empty,
    val model: AtomModelType = AtomModelType.BOHR
)

enum class MissionState { COMPLETED, CURRENT, LOCKED }

data class Mission(
    val topicId: String,
    val order: Int,
    val title: LocalizedText,
    val state: MissionState,
    val progress: TopicProgress
)

data class QuizResult(
    val topicId: String,
    val lastScore: Int,
    val bestScore: Int,
    val total: Int,
    val passed: Boolean,
    val attempts: Int
)

/** Progress of one topic. A topic is complete after lesson + passed quiz + all problems solved. */
data class TopicProgress(
    val lessonCompleted: Boolean,
    val quizPassed: Boolean,
    val solvedProblems: Int,
    val totalProblems: Int
) {
    val isComplete: Boolean
        get() = lessonCompleted && quizPassed && solvedProblems >= totalProblems

    /** 0..1: lesson, quiz and practice each weigh one third. */
    val fraction: Float
        get() {
            val practice = if (totalProblems == 0) 1f else solvedProblems.toFloat() / totalProblems
            return ((if (lessonCompleted) 1f else 0f) + (if (quizPassed) 1f else 0f) + practice) / 3f
        }

    companion object {
        val Empty = TopicProgress(false, false, 0, 0)
    }
}

data class UserProgress(
    val totalXp: Int = 0,
    val completedLessons: Set<String> = emptySet(),
    val quizResults: Map<String, QuizResult> = emptyMap(),
    /** Keys are "topicId/problemId". */
    val solvedProblems: Set<String> = emptySet(),
    val unlockedMissions: Set<String> = emptySet(),
    val currentTopicId: String? = null,
    val lastRoute: String? = null,
    val lastRewardXp: Int = 0,
    val xpEvents: List<XpEvent> = emptyList()
) {
    fun solvedCount(topicId: String): Int = solvedProblems.count { it.startsWith("$topicId/") }

    fun isSolved(topicId: String, problemId: String): Boolean = "$topicId/$problemId" in solvedProblems

    fun topicProgress(topicId: String, totalProblems: Int) = TopicProgress(
        lessonCompleted = topicId in completedLessons,
        quizPassed = quizResults[topicId]?.passed == true,
        solvedProblems = solvedCount(topicId),
        totalProblems = totalProblems
    )

    companion object {
        fun problemKey(topicId: String, problemId: String) = "$topicId/$problemId"
    }
}

/** One XP award with its time (epoch millis). */
data class XpEvent(val amount: Int, val timestamp: Long)

/** Where "Continue" should take the learner inside a topic. */
enum class NextStep { LESSON, QUIZ, PRACTICE, DONE }

fun TopicProgress.nextStep(): NextStep = when {
    !lessonCompleted -> NextStep.LESSON
    !quizPassed -> NextStep.QUIZ
    solvedProblems < totalProblems -> NextStep.PRACTICE
    else -> NextStep.DONE
}

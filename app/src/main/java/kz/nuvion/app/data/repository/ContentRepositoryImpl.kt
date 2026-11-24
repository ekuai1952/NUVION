package kz.nuvion.app.data.repository

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kz.nuvion.app.data.source.ContentDataSource
import kz.nuvion.app.domain.model.LibraryMaterial
import kz.nuvion.app.domain.model.Lesson
import kz.nuvion.app.domain.model.PracticeProblem
import kz.nuvion.app.domain.model.Quiz
import kz.nuvion.app.domain.model.Topic
import kz.nuvion.app.domain.repository.ContentRepository

/**
 * Reads content from a [ContentDataSource]. To switch to a server, pass a remote data source
 * (REST/Firebase) here, optionally keeping the local one as an offline fallback.
 */
class ContentRepositoryImpl(
    private val dataSource: ContentDataSource,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : ContentRepository {

    override suspend fun getTopics(): List<Topic> =
        withContext(dispatcher) { dataSource.topics().sortedBy { it.order } }

    override suspend fun getTopic(topicId: String): Topic? =
        withContext(dispatcher) { dataSource.topics().firstOrNull { it.id == topicId } }

    override suspend fun getLesson(topicId: String): Lesson? =
        withContext(dispatcher) { dataSource.lessons().firstOrNull { it.topicId == topicId } }

    override suspend fun getQuiz(topicId: String): Quiz? =
        withContext(dispatcher) { dataSource.quizzes().firstOrNull { it.topicId == topicId } }

    override suspend fun getPracticeProblems(topicId: String): List<PracticeProblem> =
        withContext(dispatcher) { dataSource.practiceProblems().filter { it.topicId == topicId } }

    override suspend fun getLibraryMaterials(): List<LibraryMaterial> =
        withContext(dispatcher) { dataSource.libraryMaterials() }

    override suspend fun getLibraryMaterial(materialId: String): LibraryMaterial? =
        withContext(dispatcher) { dataSource.libraryMaterials().firstOrNull { it.id == materialId } }
}

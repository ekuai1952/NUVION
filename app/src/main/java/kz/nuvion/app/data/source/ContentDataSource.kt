package kz.nuvion.app.data.source

import kz.nuvion.app.data.demo.DemoLessons
import kz.nuvion.app.data.demo.DemoLibrary
import kz.nuvion.app.data.demo.DemoProblems
import kz.nuvion.app.data.demo.DemoQuizzes
import kz.nuvion.app.data.demo.DemoTopics
import kz.nuvion.app.domain.model.LibraryMaterial
import kz.nuvion.app.domain.model.Lesson
import kz.nuvion.app.domain.model.PracticeProblem
import kz.nuvion.app.domain.model.Quiz
import kz.nuvion.app.domain.model.Topic

/** Source of learning content. Implement this with Retrofit/Firebase to load content remotely. */
interface ContentDataSource {
    suspend fun topics(): List<Topic>
    suspend fun lessons(): List<Lesson>
    suspend fun quizzes(): List<Quiz>
    suspend fun practiceProblems(): List<PracticeProblem>
    suspend fun libraryMaterials(): List<LibraryMaterial>
}

/** Bundled demonstration content. */
class LocalContentDataSource : ContentDataSource {
    override suspend fun topics(): List<Topic> = DemoTopics.all
    override suspend fun lessons(): List<Lesson> = DemoLessons.all
    override suspend fun quizzes(): List<Quiz> = DemoQuizzes.all
    override suspend fun practiceProblems(): List<PracticeProblem> = DemoProblems.all
    override suspend fun libraryMaterials(): List<LibraryMaterial> = DemoLibrary.all
}

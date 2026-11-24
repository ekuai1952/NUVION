package kz.nuvion.app.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import kz.nuvion.app.domain.model.ArModel
import kz.nuvion.app.domain.model.NextStep
import kz.nuvion.app.ui.screens.arlab.ArLabRoute
import kz.nuvion.app.ui.screens.home.HomeRoute
import kz.nuvion.app.ui.screens.lesson.LessonRoute
import kz.nuvion.app.ui.screens.library.LibraryDetailRoute
import kz.nuvion.app.ui.screens.library.LibraryRoute
import kz.nuvion.app.ui.screens.missions.MissionMapRoute
import kz.nuvion.app.ui.screens.practice.PracticeRoute
import kz.nuvion.app.ui.screens.quiz.QuizRoute
import kz.nuvion.app.ui.screens.result.ResultRoute
import kz.nuvion.app.ui.screens.topics.TopicsRoute

private const val ANIM_MS = 280

@Composable
fun NuvionNavHost(
    onRouteChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    // Persist the last opened screen (with real arguments) for analytics / resume.
    LaunchedEffect(navController) {
        navController.currentBackStackEntryFlow.collect { entry ->
            val pattern = entry.destination.route ?: return@collect
            var route = pattern
            entry.arguments?.let { args ->
                listOf(NavArgs.TOPIC_ID, NavArgs.MATERIAL_ID, NavArgs.TYPE, NavArgs.MODEL).forEach { key ->
                    args.getString(key)?.let { value -> route = route.replace("{$key}", value) }
                }
            }
            onRouteChanged(route)
        }
    }

    NavHost(
        navController = navController,
        startDestination = Routes.HOME,
        modifier = modifier,
        enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start, tween(ANIM_MS)) + fadeIn(tween(ANIM_MS)) },
        exitTransition = { fadeOut(tween(ANIM_MS)) },
        popEnterTransition = { fadeIn(tween(ANIM_MS)) },
        popExitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End, tween(ANIM_MS)) + fadeOut(tween(ANIM_MS)) }
    ) {
        composable(Routes.HOME) {
            HomeRoute(
                onStart = { navController.navigate(Routes.TOPICS) { launchSingleTop = true } },
                onContinue = { topicId -> navController.navigate(Routes.lesson(topicId)) },
                onTopics = { navController.navigate(Routes.TOPICS) { launchSingleTop = true } },
                onArLab = { navController.navigate(Routes.arLab()) }
            )
        }

        composable(Routes.TOPICS) {
            TopicsRoute(
                onBack = { navController.popBackStack() },
                onOpenLesson = { navController.navigate(Routes.lesson(it)) },
                onOpenQuiz = { navController.navigate(Routes.quiz(it)) },
                onOpenPractice = { navController.navigate(Routes.practice(it)) },
                onOpenLibrary = { navController.navigate(Routes.LIBRARY) { launchSingleTop = true } },
                onOpenMap = { navController.navigate(Routes.MISSION_MAP) { launchSingleTop = true } }
            )
        }

        composable(
            Routes.LESSON,
            arguments = listOf(navArgument(NavArgs.TOPIC_ID) { type = NavType.StringType })
        ) {
            LessonRoute(
                onBack = { navController.popBackStack() },
                onOpenAr = { modelType -> navController.navigate(Routes.arLab(ArModel.forLesson(modelType).id)) },
                onOpenQuiz = { topicId -> navController.navigate(Routes.quiz(topicId)) }
            )
        }

        composable(
            Routes.QUIZ,
            arguments = listOf(navArgument(NavArgs.TOPIC_ID) { type = NavType.StringType })
        ) {
            QuizRoute(
                onBack = { navController.popBackStack() },
                onFinished = { topicId ->
                    navController.navigate(Routes.result(ResultType.QUIZ, topicId)) {
                        popUpTo(Routes.QUIZ) { inclusive = true }
                    }
                }
            )
        }

        composable(
            Routes.PRACTICE,
            arguments = listOf(navArgument(NavArgs.TOPIC_ID) { type = NavType.StringType })
        ) {
            PracticeRoute(
                onBack = { navController.popBackStack() },
                onFinished = { topicId ->
                    navController.navigate(Routes.result(ResultType.PRACTICE, topicId)) {
                        popUpTo(Routes.PRACTICE) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.LIBRARY) {
            LibraryRoute(
                onBack = { navController.popBackStack() },
                onOpenMaterial = { navController.navigate(Routes.libraryDetail(it)) }
            )
        }

        composable(
            Routes.LIBRARY_DETAIL,
            arguments = listOf(navArgument(NavArgs.MATERIAL_ID) { type = NavType.StringType })
        ) {
            LibraryDetailRoute(onBack = { navController.popBackStack() })
        }

        composable(Routes.MISSION_MAP) {
            MissionMapRoute(
                onBack = { navController.popBackStack() },
                onOpenStep = { topicId, step -> navController.navigate(stepRoute(topicId, step)) }
            )
        }

        composable(
            Routes.AR_LAB,
            arguments = listOf(
                navArgument(NavArgs.MODEL) {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) {
            ArLabRoute(onBack = { navController.popBackStack() })
        }

        composable(
            Routes.RESULT,
            arguments = listOf(
                navArgument(NavArgs.TYPE) { type = NavType.StringType },
                navArgument(NavArgs.TOPIC_ID) { type = NavType.StringType }
            )
        ) {
            ResultRoute(
                onRetry = { type, topicId ->
                    val route = if (type == ResultType.PRACTICE) Routes.practice(topicId) else Routes.quiz(topicId)
                    navController.navigate(route) { popUpTo(Routes.RESULT) { inclusive = true } }
                },
                onNextStep = { topicId, step ->
                    navController.navigate(stepRoute(topicId, step)) { popUpTo(Routes.RESULT) { inclusive = true } }
                },
                onTopics = { navController.backToTopics() },
                onMap = {
                    navController.navigate(Routes.MISSION_MAP) {
                        popUpTo(Routes.RESULT) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}

private fun stepRoute(topicId: String, step: NextStep): String = when (step) {
    NextStep.QUIZ -> Routes.quiz(topicId)
    NextStep.PRACTICE -> Routes.practice(topicId)
    NextStep.LESSON, NextStep.DONE -> Routes.lesson(topicId)
}

/** Returns to the topic list if it is in the back stack, otherwise opens it above home. */
private fun NavHostController.backToTopics() {
    if (!popBackStack(Routes.TOPICS, inclusive = false)) {
        navigate(Routes.TOPICS) {
            popUpTo(Routes.HOME)
            launchSingleTop = true
        }
    }
}

package com.example.stylecraft.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.stylecraft.ui.screens.*

object Routes {
    const val SPLASH = "splash"
    const val ONBOARDING = "onboarding"
    const val LOGIN = "login"
    const val SIGNUP = "signup"
    const val OTP = "otp"
    const val PASSWORD_RECOVERY = "password_recovery"
    const val CHANGE_PASSWORD = "change_password"
    const val HOME = "home"
    const val CAMERA_ACCESS = "camera_access"
    const val SCAN_FACE = "scan_face"
    const val SCAN_PROGRESS = "scan_progress"
    const val FACE_RESULT = "face_result"
    const val HAIRSTYLES = "hairstyles"
    const val UPLOAD_PHOTO = "upload_photo"
    const val HISTORY = "history"
    const val HAIRSTYLE_DETAIL = "hairstyle_detail"
    const val TRY_ON_HAIRSTYLE = "try_on_hairstyle"
    const val TRANSFORMATIONS = "transformations"
    const val SHARE_SAVE = "share_save"
    const val FAVORITES = "favorites"
    const val SUBSCRIPTION = "subscription"
    const val CONFIRM_FACE_SHAPE = "confirm_face_shape"
    const val CAPTURE_PHOTO = "capture_photo"
    const val RECOMMENDATIONS = "recommendations"
    const val EXPLORE_HAIRSTYLES = "explore_hairstyles"
    const val PROFILE = "profile"
    const val EDIT_PROFILE = "edit_profile"
    const val SETTINGS = "settings"
    const val NOTIFICATION_SETTINGS = "notification_settings"
    const val PRIVACY_POLICY = "privacy_policy"
    const val HELP_CENTER = "help_center"
    const val CONTACT_US = "contact_us"
}

@Composable
fun AppNavHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH,
        enterTransition = {
            slideInHorizontally(
                initialOffsetX = { it },
                animationSpec = tween(300)
            ) + fadeIn(animationSpec = tween(300))
        },
        exitTransition = {
            slideOutHorizontally(
                targetOffsetX = { -it },
                animationSpec = tween(300)
            ) + fadeOut(animationSpec = tween(300))
        },
        popEnterTransition = {
            slideInHorizontally(
                initialOffsetX = { -it },
                animationSpec = tween(300)
            ) + fadeIn(animationSpec = tween(300))
        },
        popExitTransition = {
            slideOutHorizontally(
                targetOffsetX = { it },
                animationSpec = tween(300)
            ) + fadeOut(animationSpec = tween(300))
        }
    ) {
        composable(Routes.SPLASH) { SplashScreen(navController) }
        composable(Routes.ONBOARDING) { OnboardingScreen(navController) }
        composable(Routes.LOGIN) { LoginScreen(navController) }
        composable(Routes.SIGNUP) { SignupScreen(navController) }
        composable(Routes.OTP) { OtpScreen(navController) }
        composable(Routes.PASSWORD_RECOVERY) { PasswordRecoveryScreen(navController) }
        composable(Routes.CHANGE_PASSWORD) { ChangePasswordScreen(navController) }
        composable(Routes.HOME) { HomeScreen(navController) }
        composable(Routes.CAMERA_ACCESS) { CameraAccessScreen(navController) }
        composable(Routes.SCAN_FACE) { CapturePhotoScreen(navController) }

        composable(
            route = "${Routes.SCAN_PROGRESS}/{imageUri}",
            arguments = listOf(
                navArgument("imageUri") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val imageUriArg = backStackEntry.arguments?.getString("imageUri")
            ScanProgressScreen(
                navController = navController,
                imageUriString = imageUriArg
            )
        }

        composable(
            route = "${Routes.FACE_RESULT}/{faceShape}",
            arguments = listOf(navArgument("faceShape") { type = NavType.StringType })
        ) { backStackEntry ->
            val faceShape = backStackEntry.arguments?.getString("faceShape")
            FaceResultScreen(navController = navController, faceShape = faceShape)
        }

        composable(
            route = "${Routes.HAIRSTYLES}/{faceShape}",
            arguments = listOf(navArgument("faceShape") { type = NavType.StringType })
        ) { backStackEntry ->
            val faceShape = backStackEntry.arguments?.getString("faceShape") ?: "OVAL"
            HairstylesScreen(navController = navController, faceShape = faceShape)
        }

        composable(Routes.UPLOAD_PHOTO) {
            UploadPhotoScreen(navController = navController)
        }
        composable(Routes.HISTORY) {
            HistoryScreen(navController)
        }
        composable(Routes.CAPTURE_PHOTO) {
            CapturePhotoScreen(navController)
        }
        composable(
            route = "${Routes.CONFIRM_FACE_SHAPE}/{faceShape}?imageUri={imageUri}",
            arguments = listOf(
                navArgument("faceShape") { type = NavType.StringType },
                navArgument("imageUri") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val faceShape = backStackEntry.arguments?.getString("faceShape")
            val imageUri = backStackEntry.arguments?.getString("imageUri")
            ConfirmFaceShapeScreen(navController, faceShape, imageUri)
        }
        composable(
            route = "${Routes.RECOMMENDATIONS}/{faceShape}",
            arguments = listOf(navArgument("faceShape") { type = NavType.StringType })
        ) { backStackEntry ->
            val faceShape = backStackEntry.arguments?.getString("faceShape") ?: "OVAL"
            RecommendationsScreen(navController, faceShape)
        }
        composable(
            route = "${Routes.EXPLORE_HAIRSTYLES}/{faceShape}",
            arguments = listOf(navArgument("faceShape") { type = NavType.StringType })
        ) { backStackEntry ->
            val faceShape = backStackEntry.arguments?.getString("faceShape") ?: "OVAL"
            ExploreHairstylesScreen(navController, faceShape)
        }
        composable(
            route = "${Routes.HAIRSTYLE_DETAIL}/{styleId}",
            arguments = listOf(navArgument("styleId") { type = NavType.StringType })
        ) { backStackEntry ->
            val styleId = backStackEntry.arguments?.getString("styleId") ?: "1"
            HairstyleDetailScreen(navController, styleId)
        }
        composable(Routes.TRANSFORMATIONS) {
            TransformationsScreen(navController)
        }

        composable(Routes.SHARE_SAVE) {
            ShareSaveScreen(navController)
        }
        composable(Routes.FAVORITES) {
            FavoritesScreen(navController)
        }
        composable(Routes.SUBSCRIPTION) {
            SubscriptionScreen(navController)
        }
        composable(
            route = "${Routes.TRY_ON_HAIRSTYLE}/{styleId}",
            arguments = listOf(
                navArgument("styleId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val styleId = backStackEntry.arguments?.getString("styleId") ?: "1"
            TryOnHairstyleScreen(navController, styleId)
        }
        composable(Routes.PROFILE) {
            ProfileScreen(navController = navController)
        }
        composable(Routes.EDIT_PROFILE) {
            EditProfileScreen(navController = navController)
        }
        composable(Routes.SETTINGS) {
            SettingsScreen(navController)
        }
        composable(Routes.NOTIFICATION_SETTINGS) {
            NotificationSettingsScreen(navController)
        }
        composable(Routes.PRIVACY_POLICY) { PrivacyPolicyScreen(navController) }
        composable(Routes.HELP_CENTER) { HelpCenterScreen(navController) }
        composable(Routes.CONTACT_US) { ContactUsScreen(navController) }
    }
}

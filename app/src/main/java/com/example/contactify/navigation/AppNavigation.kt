package com.example.contactify.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.contactify.screens.*
import com.example.contactify.viewmodel.ApiUserViewModel
import com.example.contactify.viewmodel.ContactViewModel

/**
 * Điều hướng toàn ứng dụng.
 * - Các tên route được khai báo tập trung tại [Routes].
 * - Mỗi composable() tương ứng một màn hình riêng biệt.
 * - ContactViewModel và ApiUserViewModel chia sẻ toàn bộ nav graph.
 */
@Composable
fun AppNavigation(isDarkMode: Boolean, onToggleDarkMode: () -> Unit) {

    val navController   = rememberNavController()
    val contactVM: ContactViewModel  = viewModel()
    val apiUserVM: ApiUserViewModel  = viewModel()

    NavHost(
        navController  = navController,
        startDestination = Routes.LOGIN
    ) {

        // ════════════════════════════════════════════════
        // XÁC THỰC
        // ════════════════════════════════════════════════

        composable(Routes.LOGIN) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Routes.CONTACTS) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Routes.REGISTER)
                }
            )
        }

        composable(Routes.REGISTER) {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(Routes.CONTACTS) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )
        }

        // ════════════════════════════════════════════════
        // DANH SÁCH CONTACT (màn hình chính)
        // ════════════════════════════════════════════════

        composable(Routes.CONTACTS) {
            ContactScreen(
                viewModel         = contactVM,
                isDarkMode        = isDarkMode,
                onToggleDarkMode  = onToggleDarkMode,
                onContactClick    = { contact -> navController.navigate(Routes.detail(contact.id)) },
                onEditContact     = { contact -> navController.navigate(Routes.edit(contact.id)) },
                onAddContact      = { navController.navigate(Routes.ADD) },
                onSearchClick     = { navController.navigate(Routes.SEARCH) },
                onGroupClick      = { navController.navigate(Routes.GROUP) },
                onFavoritesClick  = { navController.navigate(Routes.FAVORITES) },
                onStatisticsClick = { navController.navigate(Routes.STATISTICS) },
                onApiUsersClick   = { navController.navigate(Routes.API_USERS) }
            )
        }

        // ════════════════════════════════════════════════
        // THÊM LIÊN HỆ MỚI
        // ════════════════════════════════════════════════

        composable(Routes.ADD) {
            ContactAddScreen(
                viewModel  = contactVM,
                onSaved    = { navController.popBackStack() },
                onCancel   = { navController.popBackStack() }
            )
        }

        // ════════════════════════════════════════════════
        // CHI TIẾT LIÊN HỆ
        // ════════════════════════════════════════════════

        composable(
            route     = Routes.DETAIL,
            arguments = listOf(navArgument("contactId") { type = NavType.IntType })
        ) { backStack ->
            val contactId = backStack.arguments?.getInt("contactId") ?: return@composable
            ContactDetailScreen(
                contactId      = contactId,
                viewModel      = contactVM,
                onBack         = { navController.popBackStack() },
                onEdit         = { navController.navigate(Routes.edit(contactId)) }
            )
        }

        // ════════════════════════════════════════════════
        // SỬA LIÊN HỆ
        // ════════════════════════════════════════════════

        composable(
            route     = Routes.EDIT,
            arguments = listOf(navArgument("contactId") { type = NavType.IntType })
        ) { backStack ->
            val contactId = backStack.arguments?.getInt("contactId") ?: return@composable
            ContactEditScreen(
                contactId  = contactId,
                viewModel  = contactVM,
                onSaved    = { navController.popBackStack() },
                onCancel   = { navController.popBackStack() }
            )
        }

        // ════════════════════════════════════════════════
        // TÌM KIẾM
        // ════════════════════════════════════════════════

        composable(Routes.SEARCH) {
            SearchScreen(
                viewModel      = contactVM,
                onContactClick = { contact -> navController.navigate(Routes.detail(contact.id)) },
                onBack         = {
                    contactVM.clearSearchQuery()
                    navController.popBackStack()
                }
            )
        }

        // ════════════════════════════════════════════════
        // NHÓM LIÊN HỆ
        // ════════════════════════════════════════════════

        composable(Routes.GROUP) {
            GroupScreen(
                viewModel      = contactVM,
                onContactClick = { contact -> navController.navigate(Routes.detail(contact.id)) },
                onEditContact  = { contact -> navController.navigate(Routes.edit(contact.id)) },
                onBack         = { navController.popBackStack() }
            )
        }

        // ════════════════════════════════════════════════
        // YÊU THÍCH
        // ════════════════════════════════════════════════

        composable(Routes.FAVORITES) {
            FavoritesScreen(
                viewModel      = contactVM,
                onContactClick = { contact -> navController.navigate(Routes.detail(contact.id)) },
                onEditContact  = { contact -> navController.navigate(Routes.edit(contact.id)) },
                onBack         = { navController.popBackStack() }
            )
        }

        // ════════════════════════════════════════════════
        // THỐNG KÊ
        // ════════════════════════════════════════════════

        composable(Routes.STATISTICS) {
            StatisticsScreen(
                viewModel = contactVM,
                onBack    = { navController.popBackStack() }
            )
        }

        // ════════════════════════════════════════════════
        // API USERS — DANH SÁCH
        // ════════════════════════════════════════════════

        composable(Routes.API_USERS) {
            ApiUserListScreen(
                viewModel      = apiUserVM,
                onUserClick    = { user ->
                    apiUserVM.selectUser(user)
                    navController.navigate(Routes.apiUserDetail(user.id))
                },
                onAddUser      = { navController.navigate(Routes.API_USER_ADD) },
                onBack         = { navController.popBackStack() }
            )
        }

        // ════════════════════════════════════════════════
        // API USERS — CHI TIẾT
        // ════════════════════════════════════════════════

        composable(
            route     = Routes.API_USER_DETAIL,
            arguments = listOf(navArgument("userId") { type = NavType.IntType })
        ) { backStack ->
            val userId = backStack.arguments?.getInt("userId") ?: return@composable
            ApiUserDetailScreen(
                userId    = userId,
                viewModel = apiUserVM,
                onBack    = { navController.popBackStack() },
                onEdit    = { navController.navigate(Routes.apiUserEdit(userId)) },
                onDeleted = { navController.popBackStack() }
            )
        }

        // ════════════════════════════════════════════════
        // API USERS — THÊM MỚI
        // ════════════════════════════════════════════════

        composable(Routes.API_USER_ADD) {
            ApiUserAddScreen(
                viewModel = apiUserVM,
                onSaved   = { navController.popBackStack() },
                onCancel  = { navController.popBackStack() }
            )
        }

        // ════════════════════════════════════════════════
        // API USERS — SỬA
        // ════════════════════════════════════════════════

        composable(
            route     = Routes.API_USER_EDIT,
            arguments = listOf(navArgument("userId") { type = NavType.IntType })
        ) { backStack ->
            val userId = backStack.arguments?.getInt("userId") ?: return@composable
            ApiUserEditScreen(
                userId    = userId,
                viewModel = apiUserVM,
                onSaved   = { navController.popBackStack() },
                onCancel  = { navController.popBackStack() }
            )
        }
    }
}

package com.example.contactify.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.contactify.screens.ContactDetailScreen
import com.example.contactify.screens.ContactEditScreen
import com.example.contactify.screens.ContactScreen
import com.example.contactify.screens.GroupScreen
import com.example.contactify.screens.SearchScreen
import com.example.contactify.screens.StatisticsScreen
import com.example.contactify.viewmodel.ContactViewModel

object Routes {
    const val CONTACTS = "contacts"
    const val DETAIL = "detail/{contactId}"
    const val EDIT = "edit/{contactId}"
    const val SEARCH = "search"
    const val GROUP = "group"
    const val STATISTICS = "statistics"
}

@Composable
fun AppNavigation() {

    val navController = rememberNavController()

    // Một ViewModel dùng chung cho toàn bộ ứng dụng
    val viewModel: ContactViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Routes.CONTACTS
    ) {

        // =================================================
        // 1. DANH SÁCH CONTACT
        // =================================================
        composable(Routes.CONTACTS) {

            ContactScreen(
                viewModel = viewModel,

                // Click Contact → Chi tiết
                onContactClick = { contact ->
                    navController.navigate(
                        "detail/${contact.id}"
                    )
                },

                // Context Menu → Sửa
                onEditContact = { contact ->
                    navController.navigate(
                        "edit/${contact.id}"
                    )
                },

                // Dropdown Menu → Tìm kiếm
                onSearchClick = {
                    navController.navigate(Routes.SEARCH)
                },

                // Dropdown Menu → Nhóm
                onGroupClick = {
                    navController.navigate(Routes.GROUP)
                },

                // Dropdown Menu → Thống kê
                onStatisticsClick = {
                    navController.navigate(Routes.STATISTICS)
                }
            )
        }

        // =================================================
        // 2. CHI TIẾT CONTACT
        // =================================================
        composable(
            route = Routes.DETAIL,
            arguments = listOf(
                navArgument("contactId") {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->

            val contactId =
                backStackEntry.arguments?.getInt("contactId")

            val contact = viewModel.contacts.find {
                it.id == contactId
            }

            if (contact != null) {

                ContactDetailScreen(
                    contact = contact,

                    // Quay lại danh sách
                    onBack = {
                        navController.popBackStack()
                    },

                    // Sửa Contact
                    onEdit = {
                        navController.navigate(
                            "edit/${contact.id}"
                        )
                    }
                )
            }
        }

        // =================================================
        // 3. SỬA CONTACT
        // =================================================
        composable(
            route = Routes.EDIT,
            arguments = listOf(
                navArgument("contactId") {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->

            val contactId =
                backStackEntry.arguments?.getInt("contactId")

            val contact = viewModel.contacts.find {
                it.id == contactId
            }

            if (contact != null) {

                ContactEditScreen(
                    contact = contact,

                    // Lưu thay đổi
                    onSave = { name, phone, email, group ->

                        viewModel.updateContact(
                            id = contact.id,
                            name = name,
                            phone = phone,
                            email = email,
                            group = group
                        )

                        navController.popBackStack()
                    },

                    // Hủy
                    onCancel = {
                        navController.popBackStack()
                    }
                )
            }
        }

        // =================================================
        // 4. TÌM KIẾM
        // =================================================
        composable(Routes.SEARCH) {

            SearchScreen(
                viewModel = viewModel,

                onContactClick = { contact ->
                    navController.navigate(
                        "detail/${contact.id}"
                    )
                },

                onBack = {
                    navController.popBackStack()
                }
            )
        }

        // =================================================
        // 5. NHÓM CONTACT
        // =================================================
        composable(Routes.GROUP) {

            GroupScreen(
                viewModel = viewModel,

                onContactClick = { contact ->
                    navController.navigate(
                        "detail/${contact.id}"
                    )
                },

                onBack = {
                    navController.popBackStack()
                }
            )
        }

        // =================================================
        // 6. THỐNG KÊ
        // =================================================
        composable(Routes.STATISTICS) {

            StatisticsScreen(
                viewModel = viewModel,

                onBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}


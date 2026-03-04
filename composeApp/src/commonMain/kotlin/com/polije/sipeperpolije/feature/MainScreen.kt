package com.polije.sipeperpolije.feature

import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.createGraph
import androidx.navigation.toRoute
import com.polije.sipeperpolije.feature.dashboard.presentation.component.navigationItems
import com.polije.sipeperpolije.feature.dashboard.presentation.screen.DashboardScreen
import com.polije.sipeperpolije.feature.master.presentation.dosen.detail.screen.DetailDosenScreen
import com.polije.sipeperpolije.feature.master.presentation.dosen.list.screen.DosenScreen
import com.polije.sipeperpolije.feature.master.presentation.dosen.list.viewmodel.dosen.DosenUI
import com.polije.sipeperpolije.feature.master.presentation.jadwal.detail.DetailJadwalScreen
import com.polije.sipeperpolije.feature.master.presentation.jadwal.list.JadwalScreen
import com.polije.sipeperpolije.feature.master.presentation.matakuliah.detail.screen.MataKuliahDetailScreen
import com.polije.sipeperpolije.feature.master.presentation.matakuliah.list.presentation.screens.MataKuliahScreen
import com.polije.sipeperpolije.feature.master.presentation.settings.SettingsScreen
import com.polije.sipeperpolije.feature.master.presentation.settings.hari.edit.EditHariScreen
import kotlinx.serialization.Serializable

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    onLogout: () -> Unit
) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    var selectedNavigationIndex by rememberSaveable {
        mutableIntStateOf(0)
    }


    NavigationSuiteScaffold(
        modifier = modifier,
        navigationSuiteItems = {
            navigationItems.forEachIndexed { index, item ->
                item(
                    selected = selectedNavigationIndex == index,
                    onClick = {
                        selectedNavigationIndex = index
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true
                        }
                    },
                    icon = {
                        Icon(
                            imageVector = if (item.route == currentRoute) item.selectedIcon else item.icon,
                            contentDescription = item.label,
                        )
                    },
                    label = { Text(item.label) }
                )
            }
        }
    ) {
        val graph = navController.createGraph(startDestination = DashboardRoute.Dashboard.route) {
            composable(DashboardRoute.Dashboard.route) {
                DashboardScreen(onLogout = onLogout)
            }

            composable(DashboardRoute.Dosen.route) {
                val savedStateHandle = navController.currentBackStackEntry
                    ?.savedStateHandle

                val result = savedStateHandle
                    ?.getStateFlow("detail_result_dosen", false)
                    ?.collectAsState()

                DosenScreen(resultFromDetail = result?.value) {
                    navController.navigate(DetailDosen(it.id, it.nama, it.nidn))
                }
            }

            composable(DashboardRoute.MataKuliah.route) {
                val savedStateHandle = navController.currentBackStackEntry
                    ?.savedStateHandle

                val result = savedStateHandle
                    ?.getStateFlow("detail_result", false)
                    ?.collectAsState()


                MataKuliahScreen(resultFromDetail = result?.value) {
                    navController.navigate(
                        DetailMataKuliah(
                            it.id,
                            it.nama,
                            it.kode,
                            it.idPengampu,
                            it.namaPenampu,
                            it.sksTeori,
                            it.sksPraktek,
                            it.semester,
                            it.isActive
                        )
                    )
                }
            }

            composable(DashboardRoute.Jadwal.route) {
                JadwalScreen {
                    navController.navigate(DetailJadwal(it))
                }
            }

            composable(DashboardRoute.Settings.route) {
                SettingsScreen { navController.navigate(EditJadwalHariScreen) }
            }

            composable<DetailDosen> { backStackEntry ->
                val detailDosen: DetailDosen = backStackEntry.toRoute()
                DetailDosenScreen(
                    DosenUI(
                        detailDosen.id,
                        detailDosen.nama,
                        nidn = detailDosen.nidn
                    ), onNavigateBack = {
                        navController.popBackStack()
                    }, onSuccessAction = {
                        navController.previousBackStackEntry
                            ?.savedStateHandle
                            ?.set("detail_result_dosen", true)

                        navController.popBackStack()
                    }, onFailureAction = {
                        navController.previousBackStackEntry
                            ?.savedStateHandle
                            ?.set("detail_result_dosen", false)

                        navController.popBackStack()
                    })
            }

            composable<DetailMataKuliah> { backStackEntry ->
                val detailMataKuliah: DetailMataKuliah = backStackEntry.toRoute()
                MataKuliahDetailScreen(
                    detailMataKuliah.id,
                    onBackPressed = {
                        navController.popBackStack()
                    },
                    onSuccessAction = {
                        navController.previousBackStackEntry
                            ?.savedStateHandle
                            ?.set("detail_result", true)

                        navController.popBackStack()
                    },
                    onFailedAction = {
                        navController.previousBackStackEntry
                            ?.savedStateHandle
                            ?.set("detail_result", false)

                        navController.popBackStack()
                    }
                )
            }

            composable<DetailJadwal> { backStackEntry ->
                val detailJadwal: DetailJadwal = backStackEntry.toRoute()
                DetailJadwalScreen(id = detailJadwal.id)
            }

            composable<EditJadwalHariScreen> {
                EditHariScreen() {
                    navController.popBackStack()
                }
            }

        }

        NavHost(navController = navController, graph = graph)
    }
}

sealed class DashboardRoute(val route: String) {
    object Dashboard : DashboardRoute("dashboard")
    object Dosen : DashboardRoute("dosen")
    object MataKuliah : DashboardRoute("matkul")
    object Jadwal : DashboardRoute("jadwal")
    object Settings : DashboardRoute("settings")
}

@Serializable
data class DetailDosen(val id: Int, val nama: String, val nidn: String)

@Serializable
data class DetailMataKuliah(
    val id: Int,
    val nama: String,
    val kode: String,
    val idPengampu: Int? = null,
    val namaPengampu: String,
    val sksTeori: Int,
    val sksPraktek: Int,
    val semester: Int,
    val isActive: Boolean
)

@Serializable
data class DetailJadwal(val id: Int)

@Serializable
object EditJadwalHariScreen
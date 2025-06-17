package com.mariosayago.tfg_iiti

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mariosayago.tfg_iiti.view.screens.MachineDetailScreen
import com.mariosayago.tfg_iiti.view.screens.MachineListScreen
import com.mariosayago.tfg_iiti.view.theme.TfgiitiTheme
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.mariosayago.tfg_iiti.view.screens.IncidentDetailScreen
import com.mariosayago.tfg_iiti.view.screens.IncidentListScreen
import com.mariosayago.tfg_iiti.view.screens.MachineFormScreen
import com.mariosayago.tfg_iiti.view.screens.ProductListScreen
import com.mariosayago.tfg_iiti.view.screens.VisitScheduleScreen
import dagger.hilt.android.AndroidEntryPoint
import com.mariosayago.tfg_iiti.viewmodel.MachineViewModel
import androidx.compose.runtime.getValue
import com.mariosayago.tfg_iiti.view.screens.ClosedIncidentListScreen
import com.mariosayago.tfg_iiti.view.screens.IncidentFormScreen
import com.mariosayago.tfg_iiti.view.screens.ProductFormScreen
import com.mariosayago.tfg_iiti.view.screens.ScheduleFormScreen
import com.mariosayago.tfg_iiti.view.screens.SlotFormScreen
import com.mariosayago.tfg_iiti.view.screens.SlotListScreen


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TfgiitiTheme {
                // Creo el NavController
                val navController = rememberNavController()

                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text("TFG Mario") },
                            actions = {
                                TextButton(onClick = { navController.navigate("schedule_list") }) {
                                    Text("Visitas prog.")
                                }
                                TextButton(onClick = { navController.navigate("machine_list") }) {
                                    Text("Máquinas")
                                }
                                TextButton(onClick = { navController.navigate("product_list") }) {
                                    Text("Productos")
                                }
                                TextButton(onClick = { navController.navigate("incident_list") }) {
                                    Text("Incidencias")
                                }
                            }
                        )
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "schedule_list",
                        modifier = Modifier.padding(innerPadding)
                    ) {

                        //1. Visitas programadas

                        composable("schedule_list") {
                            VisitScheduleScreen(
                                onScheduleClick = { mid -> navController.navigate("machine_detail/$mid") },
                                onNewSchedule  = { navController.navigate("schedule_form") }
                            )
                        }
                        composable("schedule_form") {
                            ScheduleFormScreen(onDone={ navController.popBackStack() })
                        }



                        // 2. Mis máquinas
                        composable("machine_list") {
                            MachineListScreen(
                                onMachineClick = { MachinesId: Long ->
                                    navController.navigate("machine_detail/$MachinesId") // Navegar a la pantalla de detalle de máquina
                                },
                                onAddMachineClick = {
                                    // al navegar sin parámetro, caemos en defaultValue = -1L
                                    navController.navigate("machine_form/-1") // Navegar a la pantalla de añadir máquina
                                }
                            )
                        }

                        // 2. Alta / Edición de máquina
                        composable(
                            route = "machine_form/{machineId}",
                            arguments = listOf(
                                navArgument("machineId") {
                                    type = NavType.LongType
                                    defaultValue = -1L
                                }
                            )
                        ) { back ->
                            // el parámetro siempre existe: -1 → creación, >=0 → edición
                            val raw = back.arguments!!.getLong("machineId")
                            val id = raw.takeIf { it >= 0L }      // null si raw==-1
                            MachineFormScreen(
                                machineId = id,
                                onSave = { navController.popBackStack() }
                            )
                        }

                        // 2.3 Detalle máquina
                        composable(
                            "machine_detail/{machineId}",
                            arguments = listOf(navArgument("machineId") { type = NavType.LongType })
                        ) { back ->
                            // 2.3.1 Recuperamos el ID de la máquina
                            val id = back.arguments?.getLong("machineId") ?: return@composable
                            // 2.3.2 Recuperamos el ViewModel
                            val vm: MachineViewModel = hiltViewModel()
                            // 2.3.3 Recogemos la lista. Nunca falla aunque esté vacía
                            val mwspList by vm
                                .getMachineWithSlotAndProductList(id)
                                .collectAsState(initial = emptyList())
                            val detail = mwspList.firstOrNull()
                            // 2.3.4 Mostramos el detalle de la máquina
                            detail?.let { data ->
                                MachineDetailScreen(
                                    machineId = id,
                                    onEditClick = { mid ->
                                        navController.navigate("machine_form/$mid")
                                    },
                                    onDeleteClick = {
                                        // 1) Borramos primero
                                        vm.delete(data.machine)
                                        // 2) Navegamos de forma explícita a la lista (antes se cerraba la app con un popbackstack
                                        navController.navigate("machine_list") {
                                            popUpTo("machine_list") { inclusive = false }

                                        }
                                    },

                                    onEditSlotsClick = { mid ->
                                        navController.navigate("slot_list/$mid")
                                    },

                                    onNewIncidentClick = { mid ->
                                        navController.navigate("incident_form/$mid")  // aquí navegas a crear incidencia
                                    }
                                )
                            }
                        }

                        // 3. Slots
                        composable(
                            "slot_list/{machineId}",
                            arguments = listOf(navArgument("machineId") { type = NavType.LongType })
                        ) { back ->
                            val mId = back.arguments!!.getLong("machineId")
                            SlotListScreen(
                                machineId = mId,
                                onSlotClick = { slotId ->
                                    navController.navigate("slot_form/$mId/$slotId")
                                }
                            )
                        }
                        composable(
                            "slot_form/{machineId}/{slotId}",
                            arguments = listOf(
                                navArgument("machineId") { type = NavType.LongType },
                                navArgument("slotId") { type = NavType.LongType })
                        ) { back ->
                            val machineId = back.arguments!!.getLong("machineId")
                            val sId = back.arguments!!.getLong("slotId")
                            SlotFormScreen(
                                machineId = machineId,
                                slotId = sId,
                                onSave = { navController.popBackStack() },
                                onAddProduct = { navController.navigate("product_form") }
                            )
                        }


                        // 4. Mis productos
                        composable("product_list") {
                            ProductListScreen(
                                onAddProduct = { navController.navigate("product_form") },
                                onEditProduct = { id -> navController.navigate("product_form/$id") }
                            )
                        }

                        // 4.1 Alta / edición de productos
                        composable(
                            "product_form/{productId}",
                            arguments = listOf(navArgument("productId") {
                                type = NavType.LongType; defaultValue = -1L
                            })
                        ) { back ->
                            val idArg = back.arguments!!.getLong("productId")
                            val id = if (idArg >= 0L) idArg else null
                            ProductFormScreen(
                                productId = id,
                                onSave = { navController.popBackStack() }
                            )
                        }

                        // También ruta sin argumentos
                        composable("product_form") {
                            ProductFormScreen(
                                productId = null,
                                onSave = { navController.popBackStack() }
                            )
                        }

                        // 5. Incidencias abiertas
                        composable("incident_list") {
                            IncidentListScreen(
                                onIncidentClick = { incidentId: Long ->
                                    navController.navigate("incident_detail/$incidentId") // Navegar a la pantalla de detalle de incidencia
                                },
                                onViewClosed = { navController.navigate("incident_closed_list") }
                            )
                        }

                        // 5.1 Incidencias cerradas
                        composable("incident_closed_list") {
                            ClosedIncidentListScreen(
                                onIncidentClick = { incidentId ->
                                    navController.navigate("incident_detail/$incidentId")
                                }
                            )
                        }

                        // 5.2 Detalle de incidencia
                        composable(
                            "incident_detail/{incidentId}",
                            arguments = listOf(navArgument("incidentId") {
                                type = NavType.LongType
                            })
                        ) { back ->
                            val incidentId =
                                back.arguments?.getLong("incidentId") ?: return@composable
                            IncidentDetailScreen(
                                incidentId = incidentId,
                                onBack = { navController.popBackStack() }
                            )
                        }

                        // 5.3 Nueva incidencia
                        composable(
                            "incident_form/{machineId}",
                            arguments = listOf(
                                navArgument("machineId") { type = NavType.LongType }
                            )
                        ) { back ->
                            val mId = back.arguments!!.getLong("machineId")
                            IncidentFormScreen(
                                machineId = mId,
                                onSave = { navController.popBackStack() }
                            )
                        }


                    }

                }
            }
        }
    }
}

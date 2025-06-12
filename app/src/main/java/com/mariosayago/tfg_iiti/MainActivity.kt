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
import androidx.compose.runtime.LaunchedEffect
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
import com.mariosayago.tfg_iiti.model.relations.MachineWithSlots


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
                                onScheduleClick = { machineId: Long ->
                                    // aquí la navegación o lo que sea
                                    navController.navigate("machine_detail/$machineId")
                                }
                            )
                        }


                        // 2. Mis máquinas
                        composable("machine_list") {
                            MachineListScreen(
                                onMachineClick = { MachinesId: Long ->
                                    navController.navigate("machine_detail/$MachinesId") // Navegar a la pantalla de detalle de máquina
                                },
                                onAddMachineClick = { navController.navigate("machine_form") } // Navegar a la pantalla de añadir máquina
                            )
                        }

                        // 2.2 Añadir máquinas
                        composable("machine_form") {
                            MachineFormScreen(
                                onSave = { navController.popBackStack() } // Volver a la lista de máquinas al guardar nueva máquina
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
                                    machineId     = id,
                                    onEditClick   = { _ ->
                                        navController.navigate("machine_edit/$id")
                                    },
                                    onDeleteClick = {
                                        // 1) Borramos primero
                                        vm.delete(data.machine)
                                        // 2) Navegamos de forma explícita a la lista (antes se cerraba la app con un popbackstack
                                        navController.navigate("machine_list") {
                                            popUpTo("machine_list") { inclusive = false }

                                        }
                                    }
                                )
                            }
                        }
                        // 2.4 Editar máquina
                        composable("machine_edit/{machineId}",
                            arguments = listOf(navArgument("machineId") { type = NavType.LongType })
                        ) { back ->
                            val id = back.arguments!!.getLong("machineId")
                            MachineFormScreen(
                                machineId = id,
                                onSave = { navController.popBackStack() }
                            )
                        }

                        // 3. Mis productos
                        composable("product_list") {
                            ProductListScreen() // mostrar la lista de productos
                        }
                        // 4. Incidencias
                        composable("incident_list") {
                            IncidentListScreen(
                                onIncidentClick = { incidentId: Long ->
                                    navController.navigate("incident_detail/$incidentId") // Navegar a la pantalla de detalle de incidencia
                                }
                            )
                        }

                        // Detalle de incidencia
                        composable(
                            "incident_detail/{incidentId}",
                            arguments = listOf(navArgument("incidentId") {
                                type = NavType.LongType
                            })
                        ) { back ->
                            val incidentId = back.arguments?.getLong("incidentId") ?: return@composable
                            IncidentDetailScreen(incidentId = incidentId)
                        }
                    }

                    }
                }
            }
        }
    }

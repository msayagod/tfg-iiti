package com.mariosayago.tfg_iiti

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.ui.unit.sp

import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mariosayago.tfg_iiti.view.screens.MachineDetailScreen
import com.mariosayago.tfg_iiti.view.screens.MachineListScreen
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import com.mariosayago.tfg_iiti.view.components.BottomNavItem
import com.mariosayago.tfg_iiti.view.screens.ClosedIncidentListScreen
import com.mariosayago.tfg_iiti.view.screens.IncidentFormScreen
import com.mariosayago.tfg_iiti.view.screens.OperationFormScreen
import com.mariosayago.tfg_iiti.view.screens.OperationListScreen
import com.mariosayago.tfg_iiti.view.screens.ProductFormScreen
import com.mariosayago.tfg_iiti.view.screens.ReportDetailScreen
import com.mariosayago.tfg_iiti.view.screens.ReportFilterScreen
import com.mariosayago.tfg_iiti.view.screens.ScheduleFormScreen
import com.mariosayago.tfg_iiti.view.screens.SlotFormScreen
import com.mariosayago.tfg_iiti.view.screens.SlotListScreen
import com.mariosayago.tfg_iiti.view.theme.VendControlTheme

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth

import androidx.compose.material3.*
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color

import kotlinx.coroutines.launch

import androidx.compose.runtime.*
import com.mariosayago.tfg_iiti.view.components.NoRippleConfig


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VendControlTheme {
                // Creo el NavController
                val navController = rememberNavController()

                val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                val scope = rememberCoroutineScope()

                ModalNavigationDrawer(
                    drawerState = drawerState,
                    drawerContent = {
                        Surface( // ← Fondo más oscuro y opaco
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(0.5f), // ← Solo ocupa la mitad
                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.97f), // Fondo menos transparente
                            tonalElevation = 4.dp
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp),
                                horizontalAlignment = Alignment.Start
                            ) {
                                Image(
                                    painter = painterResource(R.drawable.vendcontrol_logo),
                                    contentDescription = "Logo VendControl",
                                    modifier = Modifier
                                        .height(64.dp)
                                        .padding(bottom = 16.dp)
                                )
                                Text(
                                    text = "TFG Mario Sayago Diez",
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                        }
                    },
                    gesturesEnabled = true
                ) {

                    Scaffold(
                        topBar = {
                            TopAppBar(
                                title = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Image(
                                            painter = painterResource(id = R.drawable.vendcontrol_logo),
                                            contentDescription = "Logo",
                                            modifier = Modifier
                                                .size(32.dp)
                                                .clickable { scope.launch { drawerState.open() } }
                                        )
                                        Spacer(Modifier.width(8.dp))
                                        Text("VendControl")
                                    }
                                }
                            )
                        },
                        bottomBar = {
                            // Envoltura para quitar el efecto ripple
                            CompositionLocalProvider(LocalRippleConfiguration provides NoRippleConfig) {
                                NavigationBar(
                                    containerColor = MaterialTheme.colorScheme.background // o el color que uses
                                ) {
                                    val navBackStackEntry =
                                        navController.currentBackStackEntryAsState()
                                    val currentRoute = navBackStackEntry.value?.destination?.route

                                    BottomNavItem.items.forEach { item ->
                                        val selected = currentRoute?.startsWith(item.route) == true

                                        // Escalado animado del icono
                                        val scale by animateFloatAsState(
                                            targetValue = if (selected) 1.3f else 1f,
                                            label = "IconScale"
                                        )


                                        NavigationBarItem(
                                            selected = selected,
                                            onClick = {
                                                navController.navigate(item.route) {
                                                    popUpTo(navController.graph.startDestinationId) {
                                                        saveState = true
                                                    }
                                                    launchSingleTop = true
                                                    restoreState = true
                                                }
                                            },
                                            icon = {
                                                Icon(
                                                    imageVector = item.icon,
                                                    contentDescription = item.label,
                                                    modifier = Modifier
                                                        .scale(scale),
                                                    tint = if (selected)
                                                        MaterialTheme.colorScheme.primary
                                                    else
                                                        MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            },
                                            label = {
                                                Text(
                                                    text = item.label,
                                                    fontSize = 10.sp,
                                                    color = if (selected)
                                                        MaterialTheme.colorScheme.primary
                                                    else
                                                        MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            },
                                            alwaysShowLabel = true,
                                            colors = NavigationBarItemDefaults.colors(
                                                indicatorColor = Color.Transparent  // Sin fondo de selección
                                            )
                                        )
                                    }
                                }
                            }
                        }

                    ) { innerPadding ->
                        NavHost(
                            navController = navController,
                            startDestination = "schedule_list",
                            modifier = Modifier
                                .padding(innerPadding)
                                .consumeWindowInsets(innerPadding)
                        ) {

                            //1. Visitas programadas

                            composable("schedule_list") {
                                VisitScheduleScreen(
                                    onScheduleClick = { mid -> navController.navigate("machine_detail/$mid") },
                                    onNewSchedule = { navController.navigate("schedule_form") }
                                )
                            }
                            composable("schedule_form") {
                                ScheduleFormScreen(onDone = { navController.popBackStack() })
                            }


                            // 2. Mis máquinas
                            composable("machine_list") {
                                MachineListScreen(
                                    onMachineClick = { machinesId: Long ->
                                        navController.navigate("machine_detail/$machinesId") // Navegar a la pantalla de detalle de máquina
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
                                arguments = listOf(navArgument("machineId") {
                                    type = NavType.LongType
                                })
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
                                            navController.navigate(
                                                "incident_form/{machineId}/{slotId}?visitId={visitId}"
                                            )  // aquí navegas a crear incidencia
                                        },
                                        onOperateMachine = { mid -> navController.navigate("operation_list/$mid") }
                                    )
                                }
                            }

                            // 2.4 Pantalla de Operar Máquina (lista de slots con botón “Operar”)
                            composable(
                                "operation_list/{machineId}/{visitId}",
                                arguments = listOf(
                                    navArgument("machineId") { type = NavType.LongType },
                                    navArgument("visitId") { type = NavType.LongType }
                                )
                            ) { back ->
                                val machineId = back.arguments!!.getLong("machineId")
                                val visitId = back.arguments!!.getLong("visitId")
                                OperationListScreen(
                                    machineId = machineId,
                                    visitId = visitId,
                                    onSlotClick = { slotId, visitId ->
                                        navController.navigate("operation_form/$slotId/$visitId")
                                    }
                                )
                            }


                            // 3. Formulario de entrada de operación
                            composable(
                                "operation_form/{slotId}/{visitId}",
                                arguments = listOf(
                                    navArgument("slotId") { type = NavType.LongType },
                                    navArgument("visitId") { type = NavType.LongType }
                                )
                            ) { back ->
                                val slotId = back.arguments!!.getLong("slotId")
                                val visitId = back.arguments!!.getLong("visitId")
                                OperationFormScreen(
                                    slotId = slotId,
                                    visitId = visitId,
                                    onDone = { navController.popBackStack() },
                                )
                            }


                            // 3. Slots
                            composable(
                                "slot_list/{machineId}",
                                arguments = listOf(navArgument("machineId") {
                                    type = NavType.LongType
                                })
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
                                "incident_form/{machineId}/{slotId}?visitId={visitId}",
                                arguments = listOf(
                                    navArgument("machineId") { type = NavType.LongType },
                                    navArgument("slotId") { type = NavType.LongType },
                                    navArgument("visitId") {
                                        type = NavType.LongType
                                    }
                                )
                            ) { back ->
                                val machineId = back.arguments!!.getLong("machineId")
                                val slotId = back.arguments!!.getLong("slotId")
                                val visitId = back.arguments!!.getLong("visitId")

                                IncidentFormScreen(
                                    machineId = machineId,
                                    slotId = slotId,
                                    visitId = visitId,
                                    onSave = { navController.popBackStack() }
                                )
                            }


                            // --- Reportes ---
                            // 1) Lista de filtros
                            composable("reports_list") {
                                ReportFilterScreen(
                                    onGenerate = { mId, from, to ->
                                        navController.navigate("report_detail/$mId/$from/$to")
                                    }
                                )
                            }

                            // 2) Pantalla de detalle
                            composable(
                                route = "report_detail/{machineId}/{fromDate}/{toDate}",
                                arguments = listOf(
                                    navArgument("machineId") { type = NavType.LongType },
                                    navArgument("fromDate") { type = NavType.StringType },
                                    navArgument("toDate") { type = NavType.StringType }
                                )
                            ) { back ->
                                ReportDetailScreen(
                                    machineId = back.arguments!!.getLong("machineId"),
                                    fromDate = back.arguments!!.getString("fromDate")!!,
                                    toDate = back.arguments!!.getString("toDate")!!
                                )
                            }


                        }

                    }
                }
            }
        }
    }
}

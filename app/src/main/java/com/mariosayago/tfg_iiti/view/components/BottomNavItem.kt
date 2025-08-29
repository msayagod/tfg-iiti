package com.mariosayago.tfg_iiti.view.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(val route: String, val icon: ImageVector, val label: String) {
    object Schedule : BottomNavItem("schedule_list", Icons.Filled.DateRange, "Visitas")
    object Machines : BottomNavItem("machine_list", Icons.Filled.Dns, "Máquinas")
    object Products : BottomNavItem("product_list", Icons.Filled.Inventory, "Productos")
    object Incidents : BottomNavItem("incident_list", Icons.Filled.ReportProblem, "Incidencias")
    object Reports : BottomNavItem("reports_list", Icons.Filled.BarChart, "Informes")

    companion object {
        val items = listOf(Schedule, Machines, Products, Incidents, Reports)
    }
}

package com.mariosayago.tfg_iiti.view.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mariosayago.tfg_iiti.viewmodel.VisitScheduleViewModel
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EditCalendar


// UI elements & theming
import androidx.compose.material3.*
import androidx.compose.material3.FloatingActionButton


import androidx.compose.ui.Alignment



@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun VisitScheduleScreen(
    onScheduleClick: (Long) -> Unit = {},
    onNewSchedule: () -> Unit = {},
    viewModel: VisitScheduleViewModel = hiltViewModel()
) {
    val schedules by viewModel.schedules.collectAsState()
    val showAll   by viewModel.showAll.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onNewSchedule) {
                Icon(Icons.Default.EditCalendar, contentDescription = "Programar visita")
            }
        }
    ) { innerPadding ->
        LazyColumn(modifier = Modifier.padding(innerPadding)) {
            items(schedules) { ui ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clickable { onScheduleClick(ui.machineId) }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(ui.nextVisitLabel, modifier = Modifier.weight(1f))
                    Text(ui.machineName, style = MaterialTheme.typography.bodyLarge)
                }
                HorizontalDivider()
            }

        }
    }
}

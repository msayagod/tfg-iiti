package com.mariosayago.tfg_iiti.view.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mariosayago.tfg_iiti.viewmodel.VisitScheduleViewModel


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun VisitScheduleScreen(
    onScheduleClick: (Long) -> Unit = {},
    viewModel: VisitScheduleViewModel = hiltViewModel()
) {
    val list by viewModel.schedules.collectAsState()
    LazyColumn {
        items(list) { ui ->
            Text(
                text = "${ui.nextVisit} — ${ui.machineName}",
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onScheduleClick(ui.machineId) }
                    .padding(16.dp)
            )
            HorizontalDivider()
        }
    }
}
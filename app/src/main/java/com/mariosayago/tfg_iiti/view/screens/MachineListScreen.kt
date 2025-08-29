package com.mariosayago.tfg_iiti.view.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mariosayago.tfg_iiti.model.entities.Machine
import com.mariosayago.tfg_iiti.viewmodel.MachineViewModel

@Composable
fun MachineListScreen(
    onMachineClick: (Long) -> Unit = {},
    onAddMachineClick: () -> Unit = {},
    viewModel: MachineViewModel = hiltViewModel()
) {
    val machines by viewModel.machines.collectAsState()

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAddMachineClick
            ) {
                Text("Añadir")
            }
        }
    ) { innerPadding ->
        LazyColumn(Modifier.padding(innerPadding)) {
            if (machines.isEmpty()) {
                item {
                    Box(
                        Modifier.fillMaxSize().padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No hay máquinas registradas.")
                    }
                }
            } else {
                items(machines) { m: Machine ->
                    Text(
                        text = "${m.name} — ${m.location}",
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onMachineClick(m.id) }
                            .padding(16.dp)
                    )
                    HorizontalDivider()
                }
            }
        }
    }
}

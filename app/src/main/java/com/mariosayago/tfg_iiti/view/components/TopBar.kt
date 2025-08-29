package com.mariosayago.tfg_iiti.view.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.mariosayago.tfg_iiti.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(onLogoClick: () -> Unit) {
    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.vendcontrol_logo),
                    contentDescription = "Logo de VendControl",
                    modifier = Modifier
                        .size(32.dp)
                        .clickable(onClick = onLogoClick)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("VendControl")
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    )
}


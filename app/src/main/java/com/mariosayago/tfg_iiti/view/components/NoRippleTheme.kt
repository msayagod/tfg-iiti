package com.mariosayago.tfg_iiti.view.components


import androidx.compose.material.ripple.RippleAlpha
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.RippleConfiguration
import androidx.compose.ui.graphics.Color


@OptIn(ExperimentalMaterial3Api::class)
val NoRippleConfig = RippleConfiguration(
    color = Color.Transparent,
    rippleAlpha = RippleAlpha(0f, 0f, 0f, 0f)
)


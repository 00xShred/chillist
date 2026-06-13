package com.codeberg.gabriel.chillist.data

import kotlinx.serialization.Serializable

@Serializable
data class AppShortcut(
    val packageName: String,
    val displayName: String,
    val customLabel: String? = null
)

@Serializable
data class WidgetPreferences(
    val selectedApps: List<AppShortcut> = emptyList(),
    val fontFamily: String = "sans-serif", // sans-serif, serif, monospace, sans-serif-light, sans-serif-condensed
    val fontSizeSp: Int = 18,
    val textCase: String = "lowercase", // lowercase, uppercase, original
    val alignment: String = "center", // left, center, right
    val verticalSpacingDp: Int = 12,
    val textColorHex: String = "#E0E0E0",
    val backgroundColorHex: String = "#000000",
    val backgroundOpacity: Float = 0.0f, // 0.0f = transparent
    val useDynamicColors: Boolean = false,
    val themePreset: String = "default", // default, gruvbox, catppuccin, tokyo-night, nord
    val showDividers: Boolean = false,
    val showDateTime: Boolean = false,
    val showSearch: Boolean = false,
    val showApps: Boolean = true
)

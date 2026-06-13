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
    val showDateTime: Boolean = false,
    val showSearch: Boolean = false,
    val showApps: Boolean = true
)

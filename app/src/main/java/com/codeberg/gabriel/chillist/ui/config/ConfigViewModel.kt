package com.codeberg.gabriel.chillist.ui.config

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.codeberg.gabriel.chillist.data.AppPreferencesRepository
import com.codeberg.gabriel.chillist.data.AppShortcut
import com.codeberg.gabriel.chillist.data.WidgetPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ConfigViewModel(
    private val repository: AppPreferencesRepository,
    private val packageManager: PackageManager
) : ViewModel() {

    val preferences: StateFlow<WidgetPreferences> = repository.widgetPreferencesFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = WidgetPreferences()
        )

    private val _installedApps = MutableStateFlow<List<AppShortcut>>(emptyList())
    val installedApps: StateFlow<List<AppShortcut>> = _installedApps.asStateFlow()

    init {
        loadInstalledApps()
    }

    private fun loadInstalledApps() {
        viewModelScope.launch {
            val apps = withContext(Dispatchers.IO) {
                val intent = Intent(Intent.ACTION_MAIN).apply {
                    addCategory(Intent.CATEGORY_LAUNCHER)
                }
                val resolveInfoList = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    packageManager.queryIntentActivities(intent, PackageManager.ResolveInfoFlags.of(0L))
                } else {
                    packageManager.queryIntentActivities(intent, 0)
                }
                resolveInfoList.map { resolveInfo ->
                    AppShortcut(
                        packageName = resolveInfo.activityInfo.packageName,
                        displayName = resolveInfo.loadLabel(packageManager).toString()
                    )
                }.distinctBy { it.packageName }
                 .sortedBy { it.displayName.lowercase() }
            }
            _installedApps.value = apps
        }
    }

    fun addApp(app: AppShortcut) {
        viewModelScope.launch {
            repository.updatePreferences { prefs ->
                if (prefs.selectedApps.any { it.packageName == app.packageName }) {
                    prefs
                } else {
                    prefs.copy(selectedApps = prefs.selectedApps + app)
                }
            }
        }
    }

    fun removeApp(packageName: String) {
        viewModelScope.launch {
            repository.updatePreferences { prefs ->
                prefs.copy(selectedApps = prefs.selectedApps.filter { it.packageName != packageName })
            }
        }
    }

    fun updateAppLabel(packageName: String, newLabel: String) {
        viewModelScope.launch {
            repository.updatePreferences { prefs ->
                val updatedApps = prefs.selectedApps.map {
                    if (it.packageName == packageName) {
                        it.copy(customLabel = newLabel.ifBlank { null })
                    } else {
                        it
                    }
                }
                prefs.copy(selectedApps = updatedApps)
            }
        }
    }

    fun moveAppUp(index: Int) {
        if (index <= 0) return
        viewModelScope.launch {
            repository.updatePreferences { prefs ->
                val list = prefs.selectedApps.toMutableList()
                val temp = list[index]
                list[index] = list[index - 1]
                list[index - 1] = temp
                prefs.copy(selectedApps = list)
            }
        }
    }

    fun moveAppDown(index: Int) {
        viewModelScope.launch {
            repository.updatePreferences { prefs ->
                val list = prefs.selectedApps.toMutableList()
                if (index >= list.lastIndex) return@updatePreferences prefs
                val temp = list[index]
                list[index] = list[index + 1]
                list[index + 1] = temp
                prefs.copy(selectedApps = list)
            }
        }
    }

    fun updateFontFamily(fontFamily: String) {
        viewModelScope.launch {
            repository.updatePreferences { it.copy(fontFamily = fontFamily) }
        }
    }

    fun updateFontSize(sizeSp: Int) {
        viewModelScope.launch {
            repository.updatePreferences { it.copy(fontSizeSp = sizeSp) }
        }
    }

    fun updateTextCase(textCase: String) {
        viewModelScope.launch {
            repository.updatePreferences { it.copy(textCase = textCase) }
        }
    }

    fun updateAlignment(alignment: String) {
        viewModelScope.launch {
            repository.updatePreferences { it.copy(alignment = alignment) }
        }
    }

    fun updateVerticalSpacing(spacingDp: Int) {
        viewModelScope.launch {
            repository.updatePreferences { it.copy(verticalSpacingDp = spacingDp) }
        }
    }

    fun updateTextColor(hex: String) {
        viewModelScope.launch {
            repository.updatePreferences { it.copy(textColorHex = hex) }
        }
    }

    fun updateBackgroundColor(hex: String) {
        viewModelScope.launch {
            repository.updatePreferences { it.copy(backgroundColorHex = hex) }
        }
    }

    fun updateBackgroundOpacity(opacity: Float) {
        viewModelScope.launch {
            repository.updatePreferences { it.copy(backgroundOpacity = opacity) }
        }
    }

    fun updateUseDynamicColors(use: Boolean) {
        viewModelScope.launch {
            repository.updatePreferences { it.copy(useDynamicColors = use) }
        }
    }

    fun updateThemePreset(preset: String) {
        viewModelScope.launch {
            repository.updatePreferences { prefs ->
                val updatedPrefs = when (preset) {
                    "gruvbox" -> prefs.copy(
                        themePreset = preset,
                        textColorHex = "#EBDBB2",
                        backgroundColorHex = "#282828",
                        useDynamicColors = false
                    )
                    "catppuccin" -> prefs.copy(
                        themePreset = preset,
                        textColorHex = "#CDD6F4",
                        backgroundColorHex = "#1E1E2E",
                        useDynamicColors = false
                    )
                    "tokyo-night" -> prefs.copy(
                        themePreset = preset,
                        textColorHex = "#C0CAF5",
                        backgroundColorHex = "#1A1B26",
                        useDynamicColors = false
                    )
                    "nord" -> prefs.copy(
                        themePreset = preset,
                        textColorHex = "#ECEFF4",
                        backgroundColorHex = "#2E3440",
                        useDynamicColors = false
                    )
                    "nothing" -> prefs.copy(
                        themePreset = preset,
                        textColorHex = "#FFFFFF",
                        backgroundColorHex = "#000000",
                        useDynamicColors = false,
                        fontFamily = "nothing",
                        showDateTime = true,
                        showDividers = true,
                        backgroundOpacity = 0.85f
                    )
                    "default" -> prefs.copy(
                        themePreset = preset,
                        textColorHex = "#E0E0E0",
                        backgroundColorHex = "#000000",
                        useDynamicColors = false
                    )
                    else -> prefs.copy(themePreset = preset)
                }
                updatedPrefs
            }
        }
    }

    fun updateShowDividers(show: Boolean) {
        viewModelScope.launch {
            repository.updatePreferences { it.copy(showDividers = show) }
        }
    }

    fun updateShowDateTime(show: Boolean) {
        viewModelScope.launch {
            repository.updatePreferences { it.copy(showDateTime = show) }
        }
    }

    fun updateShowSearch(show: Boolean) {
        viewModelScope.launch {
            repository.updatePreferences { it.copy(showSearch = show) }
        }
    }

    fun updateShowApps(show: Boolean) {
        viewModelScope.launch {
            repository.updatePreferences { it.copy(showApps = show) }
        }
    }

    class Factory(
        private val context: Context
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ConfigViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return ConfigViewModel(
                    AppPreferencesRepository(context.applicationContext),
                    context.packageManager
                ) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

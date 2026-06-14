package com.codeberg.gabriel.chillist.ui.config

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.toFontFamily
import androidx.compose.ui.res.painterResource
import com.codeberg.gabriel.chillist.R
import com.codeberg.gabriel.chillist.data.AppShortcut
import com.codeberg.gabriel.chillist.data.WidgetPreferences
import com.codeberg.gabriel.chillist.ui.theme.ChillistTheme

@Composable
fun ConfigScreen(viewModel: ConfigViewModel) {
    val prefs by viewModel.preferences.collectAsState()
    val installedApps by viewModel.installedApps.collectAsState()

    ConfigScreenContent(
        prefs = prefs,
        installedApps = installedApps,
        onMoveUp = { viewModel.moveAppUp(it) },
        onMoveDown = { viewModel.moveAppDown(it) },
        onRemove = { viewModel.removeApp(it) },
        onUpdateLabel = { pkg, label -> viewModel.updateAppLabel(pkg, label) },
        onAppSelected = { viewModel.addApp(it) },
        onUpdateFontFamily = { viewModel.updateFontFamily(it) },
        onUpdateFontSize = { viewModel.updateFontSize(it) },
        onUpdateTextCase = { viewModel.updateTextCase(it) },
        onUpdateAlignment = { viewModel.updateAlignment(it) },
        onUpdateVerticalSpacing = { viewModel.updateVerticalSpacing(it) },
        onUpdateTextColor = { viewModel.updateTextColor(it) },
        onUpdateBackgroundColor = { viewModel.updateBackgroundColor(it) },
        onUpdateBackgroundOpacity = { viewModel.updateBackgroundOpacity(it) },
        onUpdateUseDynamicColors = { viewModel.updateUseDynamicColors(it) },
        onUpdateThemePreset = { viewModel.updateThemePreset(it) },
        onUpdateShowDividers = { viewModel.updateShowDividers(it) },
        onUpdateShowDateTime = { viewModel.updateShowDateTime(it) },
        onUpdateShowSearch = { viewModel.updateShowSearch(it) },
        onUpdateShowApps = { viewModel.updateShowApps(it) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfigScreenContent(
    prefs: WidgetPreferences,
    installedApps: List<AppShortcut>,
    onMoveUp: (Int) -> Unit,
    onMoveDown: (Int) -> Unit,
    onRemove: (String) -> Unit,
    onUpdateLabel: (String, String) -> Unit,
    onAppSelected: (AppShortcut) -> Unit,
    onUpdateFontFamily: (String) -> Unit,
    onUpdateFontSize: (Int) -> Unit,
    onUpdateTextCase: (String) -> Unit,
    onUpdateAlignment: (String) -> Unit,
    onUpdateVerticalSpacing: (Int) -> Unit,
    onUpdateTextColor: (String) -> Unit,
    onUpdateBackgroundColor: (String) -> Unit,
    onUpdateBackgroundOpacity: (Float) -> Unit,
    onUpdateUseDynamicColors: (Boolean) -> Unit,
    onUpdateThemePreset: (String) -> Unit,
    onUpdateShowDividers: (Boolean) -> Unit,
    onUpdateShowDateTime: (Boolean) -> Unit,
    onUpdateShowSearch: (Boolean) -> Unit,
    onUpdateShowApps: (Boolean) -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var showAddAppSheet by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .statusBarsPadding()
                    .padding(top = 12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "chillist.",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontFamily = FontFamily.SansSerif,
                            fontWeight = FontWeight.Light,
                            letterSpacing = (-1).sp
                        ),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
                Text(
                    text = "curate your essential focus space",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 2.dp),
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )

                // Live Preview Card
                LivePreviewCard(prefs = prefs)

                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color.Transparent,
                    contentColor = MaterialTheme.colorScheme.primary,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = MaterialTheme.colorScheme.primary
                        )
                    },
                    divider = {}
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("My Apps", fontWeight = FontWeight.Medium) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("Modules", fontWeight = FontWeight.Medium) }
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = { Text("Aesthetics", fontWeight = FontWeight.Medium) }
                    )
                }
            }
        },
        floatingActionButton = {
            if (selectedTab == 0) {
                FloatingActionButton(
                    onClick = { showAddAppSheet = true },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.background,
                    shape = CircleShape
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add App Shortcut")
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            color = MaterialTheme.colorScheme.background
        ) {
            when (selectedTab) {
                0 -> MyAppsTab(
                    selectedApps = prefs.selectedApps,
                    onMoveUp = onMoveUp,
                    onMoveDown = onMoveDown,
                    onRemove = onRemove,
                    onUpdateLabel = onUpdateLabel
                )
                1 -> ModulesTab(
                    prefs = prefs,
                    onUpdateShowSearch = onUpdateShowSearch,
                    onUpdateShowApps = onUpdateShowApps,
                    onUpdateShowDateTime = onUpdateShowDateTime
                )
                2 -> AestheticsTab(
                    prefs = prefs,
                    onUpdateFontFamily = onUpdateFontFamily,
                    onUpdateFontSize = onUpdateFontSize,
                    onUpdateTextCase = onUpdateTextCase,
                    onUpdateAlignment = onUpdateAlignment,
                    onUpdateVerticalSpacing = onUpdateVerticalSpacing,
                    onUpdateTextColor = onUpdateTextColor,
                    onUpdateBackgroundColor = onUpdateBackgroundColor,
                    onUpdateBackgroundOpacity = onUpdateBackgroundOpacity,
                    onUpdateUseDynamicColors = onUpdateUseDynamicColors,
                    onUpdateThemePreset = onUpdateThemePreset,
                    onUpdateShowDividers = onUpdateShowDividers,
                    onUpdateShowDateTime = onUpdateShowDateTime
                )
            }
        }
    }

    if (showAddAppSheet) {
        AddAppBottomSheet(
            installedApps = installedApps,
            selectedApps = prefs.selectedApps,
            onDismiss = { showAddAppSheet = false },
            onAppSelected = { app ->
                onAppSelected(app)
                showAddAppSheet = false
            }
        )
    }
}

@Composable
fun LivePreviewCard(prefs: WidgetPreferences) {
    val isSystemDark = true // Force dark preview to match the app theme
    
    val widgetOpacity = prefs.backgroundOpacity
    val widgetBgColor = if (prefs.useDynamicColors) {
        if (isSystemDark) Color.Black else Color.White
    } else {
        try {
            Color(android.graphics.Color.parseColor(prefs.backgroundColorHex))
        } catch (e: Exception) {
            if (isSystemDark) Color.Black else Color.White
        }
    }.copy(alpha = widgetOpacity)

    val font = when (prefs.fontFamily) {
        "serif" -> FontFamily.Serif
        "monospace" -> FontFamily.Monospace
        "cursive" -> FontFamily.Cursive
        "nothing" -> androidx.compose.ui.text.font.Font(R.font.ndot47).toFontFamily()
        else -> FontFamily.SansSerif
    }

    val alignment = when (prefs.alignment) {
        "center" -> Alignment.CenterHorizontally
        "right" -> Alignment.End
        else -> Alignment.Start
    }

    val textColor = if (prefs.useDynamicColors) {
        MaterialTheme.colorScheme.primary
    } else {
        try {
            Color(android.graphics.Color.parseColor(prefs.textColorHex))
        } catch (e: Exception) {
            if (isSystemDark) Color.LightGray else Color.DarkGray
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 12.dp),
        shape = RoundedCornerShape(24.dp),
        border = if (widgetOpacity > 0.05f) {
            androidx.compose.foundation.BorderStroke(1.dp, textColor.copy(alpha = 0.15f))
        } else {
            androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.1f)) // subtle outline for visibility when transparent
        },
        colors = CardDefaults.cardColors(containerColor = widgetBgColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = alignment,
            verticalArrangement = Arrangement.Center
        ) {
            if (prefs.showDateTime) {
                Text(
                    text = "Monday, Oct 24",
                    style = TextStyle(
                        color = textColor.copy(alpha = 0.7f),
                        fontSize = (prefs.fontSizeSp - 4).sp,
                        fontFamily = font
                    ),
                    modifier = Modifier.padding(bottom = (prefs.verticalSpacingDp / 2).dp)
                )
            }

            if (prefs.showSearch) {
                Text(
                    text = "search...",
                    style = TextStyle(
                        color = textColor.copy(alpha = 0.5f),
                        fontSize = prefs.fontSizeSp.sp,
                        fontFamily = font
                    ),
                    modifier = Modifier.padding(vertical = (prefs.verticalSpacingDp / 2).dp)
                )
                if (prefs.showDividers && prefs.showApps && prefs.selectedApps.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .width(50.dp)
                            .height(1.dp)
                            .background(textColor.copy(alpha = 0.2f))
                            .padding(bottom = (prefs.verticalSpacingDp / 2).dp)
                    )
                }
            }

            if (prefs.showApps) {
                if (prefs.selectedApps.isEmpty()) {
                    Text(
                        text = "add essential apps",
                        style = TextStyle(
                            color = textColor.copy(alpha = 0.5f),
                            fontSize = prefs.fontSizeSp.sp,
                            fontFamily = font
                        )
                    )
                } else {
                    prefs.selectedApps.take(3).forEachIndexed { index, app ->
                        val label = app.customLabel ?: app.displayName
                        val displayTitle = when (prefs.textCase) {
                            "lowercase" -> label.lowercase()
                            "uppercase" -> label.uppercase()
                            else -> label
                        }

                        Text(
                            text = displayTitle,
                            modifier = Modifier.padding(vertical = (prefs.verticalSpacingDp / 2).dp),
                            style = TextStyle(
                                color = textColor,
                                fontSize = prefs.fontSizeSp.sp,
                                fontFamily = font
                            )
                        )

                        if (prefs.showDividers && index < 2 && index < prefs.selectedApps.lastIndex) {
                            Box(
                                modifier = Modifier
                                    .width(50.dp)
                                    .height(1.dp)
                                    .background(textColor.copy(alpha = 0.2f))
                            )
                        }
                    }
                    if (prefs.selectedApps.size > 3) {
                        Text(
                            text = "...",
                            style = TextStyle(
                                color = textColor.copy(alpha = 0.7f),
                                fontSize = prefs.fontSizeSp.sp,
                                fontFamily = font
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MyAppsTab(
    selectedApps: List<AppShortcut>,
    onMoveUp: (Int) -> Unit,
    onMoveDown: (Int) -> Unit,
    onRemove: (String) -> Unit,
    onUpdateLabel: (String, String) -> Unit
) {
    if (selectedApps.isEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Your list is empty",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Tap the '+' button to select essential shortcuts for your widget.",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )
        }
    } else {
        var editingPackageName by remember { mutableStateOf<String?>(null) }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            itemsIndexed(selectedApps) { index, app ->
                val isEditing = editingPackageName == app.packageName

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable { editingPackageName = if (isEditing) null else app.packageName },
                    colors = CardDefaults.cardColors(
                        containerColor = if (isEditing) 
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                        else 
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = app.customLabel ?: app.displayName,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Medium,
                                    color = if (app.customLabel != null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = app.packageName,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                )
                            }
                            
                            // Reordering Controls
                            IconButton(
                                onClick = { onMoveUp(index) },
                                enabled = index > 0
                            ) {
                                Icon(imageVector = Icons.Default.KeyboardArrowUp, contentDescription = "Move Up")
                            }
                            IconButton(
                                onClick = { onMoveDown(index) },
                                enabled = index < selectedApps.lastIndex
                            ) {
                                Icon(imageVector = Icons.Default.KeyboardArrowDown, contentDescription = "Move Down")
                            }
                            IconButton(
                                onClick = { onRemove(app.packageName) }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Remove App",
                                    tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f)
                                )
                            }
                        }

                        AnimatedVisibility(visible = isEditing) {
                            Column(modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)) {
                                Text(
                                    text = "Custom Label",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                OutlinedTextField(
                                    value = app.customLabel ?: "",
                                    onValueChange = { onUpdateLabel(app.packageName, it) },
                                    placeholder = { 
                                        Text(
                                            text = app.displayName, 
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                        ) 
                                    },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                                    ),
                                    trailingIcon = {
                                        if (app.customLabel != null) {
                                            IconButton(onClick = { onUpdateLabel(app.packageName, "") }) {
                                                Icon(
                                                    imageVector = Icons.Default.Close, 
                                                    contentDescription = "Clear", 
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ModulesTab(
    prefs: WidgetPreferences,
    onUpdateShowSearch: (Boolean) -> Unit,
    onUpdateShowApps: (Boolean) -> Unit,
    onUpdateShowDateTime: (Boolean) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 8.dp)
    ) {
        item {
            ConfigHeader(title = "Information Modules")
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    ModuleToggle(
                        title = "Show Date & Time",
                        subtitle = "Display the current day and time",
                        checked = prefs.showDateTime,
                        onCheckedChange = onUpdateShowDateTime
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    ModuleToggle(
                        title = "Show Search",
                        subtitle = "Quick access to system search",
                        checked = prefs.showSearch,
                        onCheckedChange = onUpdateShowSearch
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    ModuleToggle(
                        title = "Show App List",
                        subtitle = "Display your curated essential apps",
                        checked = prefs.showApps,
                        onCheckedChange = onUpdateShowApps
                    )
                }
            }
        }
    }
}

@Composable
fun ModuleToggle(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.titleSmall)
            Text(
                text = subtitle, 
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.background,
                checkedTrackColor = MaterialTheme.colorScheme.primary
            )
        )
    }
}

@Composable
fun AestheticsTab(
    prefs: WidgetPreferences,
    onUpdateFontFamily: (String) -> Unit,
    onUpdateFontSize: (Int) -> Unit,
    onUpdateTextCase: (String) -> Unit,
    onUpdateAlignment: (String) -> Unit,
    onUpdateVerticalSpacing: (Int) -> Unit,
    onUpdateTextColor: (String) -> Unit,
    onUpdateBackgroundColor: (String) -> Unit,
    onUpdateBackgroundOpacity: (Float) -> Unit,
    onUpdateUseDynamicColors: (Boolean) -> Unit,
    onUpdateThemePreset: (String) -> Unit,
    onUpdateShowDividers: (Boolean) -> Unit,
    onUpdateShowDateTime: (Boolean) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 8.dp)
    ) {
        item {
            ConfigHeader(title = "Typography")
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Font Family selection
                    Text("Font Family", style = MaterialTheme.typography.titleSmall)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("sans-serif", "serif", "monospace", "cursive", "nothing").forEach { fontName ->
                            val isSelected = prefs.fontFamily == fontName
                            SuggestionChip(
                                onClick = { onUpdateFontFamily(fontName) },
                                label = {
                                    Text(
                                        text = fontName,
                                        fontFamily = when (fontName) {
                                            "serif" -> FontFamily.Serif
                                            "monospace" -> FontFamily.Monospace
                                            "cursive" -> FontFamily.Cursive
                                            "nothing" -> androidx.compose.ui.text.font.Font(R.font.ndot47).toFontFamily()
                                            else -> FontFamily.SansSerif
                                        }
                                    )
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = SuggestionChipDefaults.suggestionChipColors(
                                    containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                    labelColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Text Case Selection
                    Text("Text Case", style = MaterialTheme.typography.titleSmall)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("lowercase", "uppercase", "original").forEach { cName ->
                            val isSelected = prefs.textCase == cName
                            TextButton(
                                onClick = { onUpdateTextCase(cName) },
                                modifier = Modifier
                                    .weight(1f)
                                    .background(
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                                        shape = RoundedCornerShape(12.dp)
                                    )
                            ) {
                                Text(
                                    text = cName,
                                    color = if (isSelected) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Font Size Slider
                    Text("Font Size: ${prefs.fontSizeSp} sp", style = MaterialTheme.typography.titleSmall)
                    Slider(
                        value = prefs.fontSizeSp.toFloat(),
                        onValueChange = { onUpdateFontSize(it.toInt()) },
                        valueRange = 12f..64f,
                        steps = 52,
                        colors = SliderDefaults.colors(
                            thumbColor = MaterialTheme.colorScheme.primary,
                            activeTrackColor = MaterialTheme.colorScheme.primary
                        )
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(12.dp))
            ConfigHeader(title = "Layout & Spacing")
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Alignment selection
                    Text("Alignment", style = MaterialTheme.typography.titleSmall)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("left", "center", "right").forEach { align ->
                            val isSelected = prefs.alignment == align
                            TextButton(
                                onClick = { onUpdateAlignment(align) },
                                modifier = Modifier
                                    .weight(1f)
                                    .background(
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                                        shape = RoundedCornerShape(12.dp)
                                    )
                            ) {
                                Text(
                                    text = align,
                                    color = if (isSelected) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Spacing Slider
                    Text("Vertical Spacing: ${prefs.verticalSpacingDp} dp", style = MaterialTheme.typography.titleSmall)
                    Slider(
                        value = prefs.verticalSpacingDp.toFloat(),
                        onValueChange = { onUpdateVerticalSpacing(it.toInt()) },
                        valueRange = 4f..64f,
                        steps = 60,
                        colors = SliderDefaults.colors(
                            thumbColor = MaterialTheme.colorScheme.primary,
                            activeTrackColor = MaterialTheme.colorScheme.primary
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Dividers Switch
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Show Divider Lines", style = MaterialTheme.typography.titleSmall)
                        Switch(
                            checked = prefs.showDividers,
                            onCheckedChange = { onUpdateShowDividers(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = MaterialTheme.colorScheme.background,
                                checkedTrackColor = MaterialTheme.colorScheme.primary
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Date/Time Switch
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Show Date & Time", style = MaterialTheme.typography.titleSmall)
                        Switch(
                            checked = prefs.showDateTime,
                            onCheckedChange = { onUpdateShowDateTime(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = MaterialTheme.colorScheme.background,
                                checkedTrackColor = MaterialTheme.colorScheme.primary
                            )
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(12.dp))
            ConfigHeader(title = "Theming & Palette")
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Wallpaper Matching
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Material You", style = MaterialTheme.typography.titleSmall)
                            Text(
                                "Match colors with your wallpaper",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                        }
                        Switch(
                            checked = prefs.useDynamicColors,
                            onCheckedChange = { onUpdateUseDynamicColors(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = MaterialTheme.colorScheme.background,
                                checkedTrackColor = MaterialTheme.colorScheme.primary
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Theme Presets
                    Text("Theme Presets", style = MaterialTheme.typography.titleSmall)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val presets = listOf(
                            "default" to "Default",
                            "nothing" to "Nothing",
                            "gruvbox" to "Gruvbox",
                            "catppuccin" to "Catppuccin",
                            "tokyo-night" to "Tokyo Night",
                            "nord" to "Nord"
                        )
                        presets.forEach { (id, name) ->
                            val isSelected = prefs.themePreset == id
                            SuggestionChip(
                                onClick = { onUpdateThemePreset(id) },
                                label = { Text(name) },
                                shape = RoundedCornerShape(12.dp),
                                colors = SuggestionChipDefaults.suggestionChipColors(
                                    containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                    labelColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                    }

                    AnimatedVisibility(
                        visible = !prefs.useDynamicColors,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Column {
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            // Text color
                            Text("Letters Color (Hex)", style = MaterialTheme.typography.titleSmall)
                            ColorPresets(
                                selectedColor = prefs.textColorHex,
                                onColorSelected = onUpdateTextColor,
                                isDark = true
                            )
                            OutlinedTextField(
                                value = prefs.textColorHex,
                                onValueChange = { if (it.length <= 7) onUpdateTextColor(it) },
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                                ),
                                modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Background color
                            Text("Background Color (Hex)", style = MaterialTheme.typography.titleSmall)
                            ColorPresets(
                                selectedColor = prefs.backgroundColorHex,
                                onColorSelected = onUpdateBackgroundColor,
                                isDark = false
                            )
                            OutlinedTextField(
                                value = prefs.backgroundColorHex,
                                onValueChange = { if (it.length <= 7) onUpdateBackgroundColor(it) },
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                                ),
                                modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Background Opacity
                    Text("Widget Background Opacity: ${(prefs.backgroundOpacity * 100).toInt()}%", style = MaterialTheme.typography.titleSmall)
                    Slider(
                        value = prefs.backgroundOpacity,
                        onValueChange = { onUpdateBackgroundOpacity(it) },
                        valueRange = 0f..1f,
                        colors = SliderDefaults.colors(
                            thumbColor = MaterialTheme.colorScheme.primary,
                            activeTrackColor = MaterialTheme.colorScheme.primary
                        )
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun ColorPresets(
    selectedColor: String,
    onColorSelected: (String) -> Unit,
    isDark: Boolean
) {
    val presets = if (isDark) {
        listOf("#E0E0E0", "#FFFFFF", "#FFCDD2", "#C8E6C9", "#BBDEFB", "#FFF9C4")
    } else {
        listOf("#303030", "#000000", "#B71C1C", "#1B5E20", "#0D47A1", "#F57F17")
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        presets.forEach { colorHex ->
            val color = try {
                Color(android.graphics.Color.parseColor(colorHex))
            } catch (e: Exception) {
                Color.Gray
            }
            
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(color)
                    .clickable { onColorSelected(colorHex) }
                    .then(
                        if (selectedColor.equals(colorHex, ignoreCase = true)) {
                            Modifier.background(
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                                shape = CircleShape
                            ).padding(4.dp).clip(CircleShape).background(color)
                        } else Modifier
                    )
            ) {
                if (selectedColor.equals(colorHex, ignoreCase = true)) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = if (isDark) Color.Black else Color.White,
                        modifier = Modifier.size(16.dp).align(Alignment.Center)
                    )
                }
            }
        }
    }
}

@Composable
fun ConfigHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.SemiBold,
            fontFamily = FontFamily.SansSerif
        ),
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(vertical = 4.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAppBottomSheet(
    installedApps: List<AppShortcut>,
    selectedApps: List<AppShortcut>,
    onDismiss: () -> Unit,
    onAppSelected: (AppShortcut) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    
    val filteredApps = remember(searchQuery, installedApps) {
        installedApps.filter { app ->
            app.displayName.contains(searchQuery, ignoreCase = true) ||
            app.packageName.contains(searchQuery, ignoreCase = true)
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.background,
        dragHandle = {}
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Select App",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Light)
                )
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close Picker")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search apps...") },
                leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null) },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredApps) { app ->
                    val isAlreadySelected = selectedApps.any { it.packageName == app.packageName }
                    
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(enabled = !isAlreadySelected) { onAppSelected(app) },
                        colors = CardDefaults.cardColors(
                            containerColor = if (isAlreadySelected) {
                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
                            } else {
                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            }
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = app.displayName,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = if (isAlreadySelected) {
                                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                                    } else {
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                    }
                                )
                                Text(
                                    text = app.packageName,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                                )
                            }
                            if (isAlreadySelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Selected",
                                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// Android Studio Interactive Design Editor Preview
@Preview(showBackground = true)
@Composable
fun ConfigScreenContentPreview() {
    val mockPrefs = WidgetPreferences(
        selectedApps = listOf(
            AppShortcut("com.android.phone", "Phone"),
            AppShortcut("com.android.chrome", "Chrome"),
            AppShortcut("com.whatsapp", "WhatsApp"),
            AppShortcut("com.android.camera", "Camera")
        ),
        fontFamily = "serif",
        fontSizeSp = 20,
        textCase = "lowercase",
        alignment = "center",
        verticalSpacingDp = 10,
        textColorHex = "#E0E0E0",
        backgroundOpacity = 0.1f,
        useDynamicColors = false,
        showDividers = true
    )

    val mockInstalled = listOf(
        AppShortcut("com.android.phone", "Phone"),
        AppShortcut("com.android.chrome", "Chrome"),
        AppShortcut("com.whatsapp", "WhatsApp"),
        AppShortcut("com.android.camera", "Camera"),
        AppShortcut("com.spotify.music", "Spotify"),
        AppShortcut("com.google.android.calendar", "Calendar")
    )

    ChillistTheme(darkTheme = true) {
        Surface(color = MaterialTheme.colorScheme.background) {
            ConfigScreenContent(
                prefs = mockPrefs,
                installedApps = mockInstalled,
                onMoveUp = {},
                onMoveDown = {},
                onRemove = {},
                onAppSelected = {},
                onUpdateFontFamily = {},
                onUpdateFontSize = {},
                onUpdateTextCase = {},
                onUpdateAlignment = {},
                onUpdateVerticalSpacing = {},
                onUpdateTextColor = {},
                onUpdateBackgroundColor = {},
                onUpdateBackgroundOpacity = {},
                onUpdateUseDynamicColors = {},
                onUpdateThemePreset = {},
                onUpdateShowDividers = {},
                onUpdateShowDateTime = {},
                onUpdateShowSearch = {},
                onUpdateShowApps = {},
                onUpdateLabel = { _, _ -> }
            )
        }
    }
}

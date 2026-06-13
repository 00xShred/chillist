package com.codeberg.gabriel.chillist.ui.config

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codeberg.gabriel.chillist.data.AppShortcut
import com.codeberg.gabriel.chillist.data.WidgetPreferences

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
        onAppSelected = { viewModel.addApp(it) }
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
    onAppSelected: (AppShortcut) -> Unit
) {
    var showAddAppSheet by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(top = 16.dp)
            ) {
                Text(
                    text = "chillist.",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Light,
                        letterSpacing = (-1).sp
                    ),
                    modifier = Modifier.padding(horizontal = 24.dp),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "curate your essential focus space",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp),
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )

                // Simple Preview
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                        .height(140.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Black)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            if (prefs.selectedApps.isEmpty()) {
                                Text(
                                    text = "add your essential apps",
                                    color = Color.LightGray
                                )
                            } else {
                                prefs.selectedApps.take(3).forEach { app ->
                                    Text(
                                        text = (app.customLabel ?: app.displayName).lowercase(),
                                        color = Color.White,
                                        modifier = Modifier.padding(vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddAppSheet = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.background,
                shape = CircleShape
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add App Shortcut")
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
            MyAppsTab(
                selectedApps = prefs.selectedApps,
                onMoveUp = onMoveUp,
                onMoveDown = onMoveDown,
                onRemove = onRemove,
                onUpdateLabel = onUpdateLabel
            )
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

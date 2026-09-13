package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Kitchen
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.PantryItem
import com.example.ui.components.AddEditProductDialog
import com.example.ui.components.AddShoppingItemDialog
import com.example.ui.components.PantryCard
import com.example.ui.components.ShoppingCard
import com.example.ui.theme.ExpiringAmber
import com.example.ui.theme.ExpiringAmberContainer
import com.example.ui.theme.ExpiringOnAmberContainer
import kotlinx.coroutines.launch

@Composable
fun PantryScreen(
    viewModel: PantryViewModel,
    modifier: Modifier = Modifier
) {
    val activeTab by viewModel.activeTab.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val pantryItems by viewModel.filteredPantryItems.collectAsStateWithLifecycle()
    val shoppingItems by viewModel.shoppingItems.collectAsStateWithLifecycle()
    val onlyExpiringFilter by viewModel.onlyExpiringFilter.collectAsStateWithLifecycle()
    val expiringCount by viewModel.expiringCount.collectAsStateWithLifecycle()
    val expiredCount by viewModel.expiredCount.collectAsStateWithLifecycle()

    var showAddPantryDialog by remember { mutableStateOf(false) }
    var itemToEdit by remember { mutableStateOf<PantryItem?>(null) }
    var showAddShoppingDialog by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.statusBars),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            // Large Prominent Add Button
            ExtendedFloatingActionButton(
                onClick = {
                    if (activeTab == AppTab.PANTRY) {
                        showAddPantryDialog = true
                    } else {
                        showAddShoppingDialog = true
                    }
                },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(28.dp)
                    )
                },
                text = {
                    Text(
                        text = if (activeTab == AppTab.PANTRY) "Agregar producto" else "Agregar a compra",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .testTag("fab_add_item")
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Top App Title Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.size(44.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Kitchen,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Mi Alacena",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 24.sp
                            ),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "Organiza tu despensa y compras",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Big Minimalist Navigation Tabs
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Tab 1: Alacena
                    val isPantryActive = activeTab == AppTab.PANTRY
                    Surface(
                        onClick = { viewModel.setActiveTab(AppTab.PANTRY) },
                        shape = RoundedCornerShape(16.dp),
                        color = if (isPantryActive) MaterialTheme.colorScheme.surface else Color.Transparent,
                        tonalElevation = if (isPantryActive) 3.dp else 0.dp,
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                            .testTag("tab_pantry")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = if (isPantryActive) Icons.Filled.Kitchen else Icons.Outlined.Inventory2,
                                contentDescription = null,
                                tint = if (isPantryActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Alacena",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = if (isPantryActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            if (pantryItems.isNotEmpty()) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = CircleShape,
                                    color = if (isPantryActive) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.outlineVariant
                                ) {
                                    Text(
                                        text = "${pantryItems.size}",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp),
                                        color = if (isPantryActive) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    // Tab 2: Lista de compras
                    val isShoppingActive = activeTab == AppTab.SHOPPING
                    Surface(
                        onClick = { viewModel.setActiveTab(AppTab.SHOPPING) },
                        shape = RoundedCornerShape(16.dp),
                        color = if (isShoppingActive) MaterialTheme.colorScheme.surface else Color.Transparent,
                        tonalElevation = if (isShoppingActive) 3.dp else 0.dp,
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                            .testTag("tab_shopping")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = if (isShoppingActive) Icons.Filled.ShoppingCart else Icons.Outlined.ShoppingCart,
                                contentDescription = null,
                                tint = if (isShoppingActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Compras",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = if (isShoppingActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            if (shoppingItems.isNotEmpty()) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = CircleShape,
                                    color = if (isShoppingActive) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.outlineVariant
                                ) {
                                    Text(
                                        text = "${shoppingItems.size}",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp),
                                        color = if (isShoppingActive) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // TAB CONTENT
            if (activeTab == AppTab.PANTRY) {
                PantryTabContent(
                    items = pantryItems,
                    searchQuery = searchQuery,
                    onSearchQueryChange = { viewModel.onSearchQueryChange(it) },
                    onlyExpiringFilter = onlyExpiringFilter,
                    onToggleExpiringFilter = { viewModel.toggleExpiringFilter() },
                    expiringCount = expiringCount,
                    expiredCount = expiredCount,
                    onEditItem = { itemToEdit = it },
                    onDeleteItem = { viewModel.deletePantryItem(it) },
                    onIncrement = { viewModel.adjustQuantity(it, +1) },
                    onDecrement = { viewModel.adjustQuantity(it, -1) },
                    onAddToShopping = {
                        viewModel.addPantryItemToShoppingList(it)
                        scope.launch {
                            snackbarHostState.showSnackbar("'${it.name}' agregado a la lista de compras")
                        }
                    },
                    onLoadSamples = { viewModel.loadSampleDataIfEmpty() }
                )
            } else {
                ShoppingTabContent(
                    items = shoppingItems,
                    onToggleCheck = { viewModel.toggleShoppingItem(it) },
                    onTransferToPantry = {
                        viewModel.transferShoppingToPantry(it)
                        scope.launch {
                            snackbarHostState.showSnackbar("'${it.name}' pasado a la alacena")
                        }
                    },
                    onDeleteItem = { viewModel.deleteShoppingItem(it) },
                    onClearChecked = { viewModel.clearCheckedShoppingItems() },
                    onQuickAdd = { name ->
                        viewModel.addShoppingItem(name)
                    }
                )
            }
        }
    }

    // Add / Edit Pantry Item Dialog
    if (showAddPantryDialog) {
        AddEditProductDialog(
            itemToEdit = null,
            onDismiss = { showAddPantryDialog = false },
            onSave = { name, quantity, expirationDate, category, notes ->
                viewModel.addPantryItem(name, quantity, expirationDate, category, notes)
                showAddPantryDialog = false
                scope.launch {
                    snackbarHostState.showSnackbar("'$name' guardado en la alacena")
                }
            }
        )
    }

    if (itemToEdit != null) {
        AddEditProductDialog(
            itemToEdit = itemToEdit,
            onDismiss = { itemToEdit = null },
            onSave = { name, quantity, expirationDate, category, notes ->
                itemToEdit?.let { current ->
                    viewModel.updatePantryItem(
                        current.copy(
                            name = name,
                            quantity = quantity,
                            expirationDate = expirationDate,
                            category = category,
                            notes = notes
                        )
                    )
                }
                itemToEdit = null
                scope.launch {
                    snackbarHostState.showSnackbar("Producto actualizado")
                }
            },
            onDelete = {
                itemToEdit?.let { current ->
                    viewModel.deletePantryItem(current)
                }
                itemToEdit = null
                scope.launch {
                    snackbarHostState.showSnackbar("Producto eliminado")
                }
            }
        )
    }

    // Add Shopping Item Dialog
    if (showAddShoppingDialog) {
        AddShoppingItemDialog(
            onDismiss = { showAddShoppingDialog = false },
            onSave = { name, quantity ->
                viewModel.addShoppingItem(name, quantity)
                showAddShoppingDialog = false
                scope.launch {
                    snackbarHostState.showSnackbar("'$name' agregado a la lista")
                }
            }
        )
    }
}

@Composable
fun PantryTabContent(
    items: List<PantryItem>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onlyExpiringFilter: Boolean,
    onToggleExpiringFilter: () -> Unit,
    expiringCount: Int,
    expiredCount: Int,
    onEditItem: (PantryItem) -> Unit,
    onDeleteItem: (PantryItem) -> Unit,
    onIncrement: (PantryItem) -> Unit,
    onDecrement: (PantryItem) -> Unit,
    onAddToShopping: (PantryItem) -> Unit,
    onLoadSamples: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Search Bar (Large and easy to touch)
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            placeholder = { Text("Buscar por nombre...") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Buscar",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchQueryChange("") }) {
                        Icon(imageVector = Icons.Default.Clear, contentDescription = "Borrar búsqueda")
                    }
                }
            },
            shape = RoundedCornerShape(16.dp),
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp)
                .testTag("search_pantry_input")
        )

        // Expiration Alert Banner if there are expiring or expired items
        val totalAlerts = expiredCount + expiringCount
        if (totalAlerts > 0) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .clickable { onToggleExpiringFilter() }
                    .testTag("expiry_alert_banner"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (expiredCount > 0) {
                        MaterialTheme.colorScheme.errorContainer
                    } else {
                        ExpiringAmberContainer
                    }
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = if (expiredCount > 0) Icons.Default.Warning else Icons.Outlined.WarningAmber,
                            contentDescription = null,
                            tint = if (expiredCount > 0) MaterialTheme.colorScheme.onErrorContainer else ExpiringOnAmberContainer,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            val alertTitle = if (expiredCount > 0 && expiringCount > 0) {
                                "$expiredCount caducado(s) y $expiringCount por caducar"
                            } else if (expiredCount > 0) {
                                "$expiredCount producto(s) caducado(s)"
                            } else {
                                "$expiringCount producto(s) por caducar pronto"
                            }
                            Text(
                                text = alertTitle,
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = if (expiredCount > 0) MaterialTheme.colorScheme.onErrorContainer else ExpiringOnAmberContainer
                            )
                            Text(
                                text = if (onlyExpiringFilter) "Mostrando sólo estos productos (toca para ver todos)" else "Toca para filtrar y revisarlos",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (expiredCount > 0) MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.8f) else ExpiringOnAmberContainer.copy(alpha = 0.8f)
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (expiredCount > 0) MaterialTheme.colorScheme.error else ExpiringAmber
                    ) {
                        Text(
                            text = if (onlyExpiringFilter) "Ver todos" else "Ver aviso",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        // Active Filter indicator chip if onlyExpiringFilter is ON but no banner
        if (onlyExpiringFilter && totalAlerts == 0) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Filtrado por: Por caducar",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                TextButton(onClick = onToggleExpiringFilter) {
                    Text("Mostrar todos")
                }
            }
        }

        // List of Pantry Items
        if (items.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.size(80.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Outlined.Inventory2,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = if (searchQuery.isNotEmpty()) {
                            "No se encontró '$searchQuery'"
                        } else if (onlyExpiringFilter) {
                            "¡Excelente! No hay productos por caducar."
                        } else {
                            "Tu alacena está vacía"
                        },
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = if (searchQuery.isNotEmpty()) {
                            "Intenta buscar con otro término."
                        } else if (onlyExpiringFilter) {
                            "Todos tus productos tienen fechas seguras."
                        } else {
                            "Agrega tus víveres y despensa con el botón '+' de abajo para controlar cantidad y caducidad."
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    if (searchQuery.isEmpty() && !onlyExpiringFilter) {
                        Spacer(modifier = Modifier.height(20.dp))
                        OutlinedButton(
                            onClick = onLoadSamples,
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.testTag("load_sample_pantry")
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Cargar productos de ejemplo", fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("pantry_list"),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 96.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(
                    items = items,
                    key = { it.id }
                ) { item ->
                    PantryCard(
                        item = item,
                        onEdit = { onEditItem(item) },
                        onDelete = { onDeleteItem(item) },
                        onIncrement = { onIncrement(item) },
                        onDecrement = { onDecrement(item) },
                        onAddToShoppingList = { onAddToShopping(item) }
                    )
                }
            }
        }
    }
}

@Composable
fun ShoppingTabContent(
    items: List<com.example.data.ShoppingItem>,
    onToggleCheck: (com.example.data.ShoppingItem) -> Unit,
    onTransferToPantry: (com.example.data.ShoppingItem) -> Unit,
    onDeleteItem: (com.example.data.ShoppingItem) -> Unit,
    onClearChecked: () -> Unit,
    onQuickAdd: (String) -> Unit
) {
    var quickItemName by remember { mutableStateOf("") }
    val checkedCount = items.count { it.isChecked }

    Column(modifier = Modifier.fillMaxSize()) {
        // Quick add row at top
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 1.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = quickItemName,
                    onValueChange = { quickItemName = it },
                    placeholder = { Text("Añadir a la lista (ej. Huevos)...") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("quick_shopping_input")
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        if (quickItemName.isNotBlank()) {
                            onQuickAdd(quickItemName.trim())
                            quickItemName = ""
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .height(54.dp)
                        .testTag("quick_shopping_add_button")
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Añadir")
                }
            }
        }

        // Action bar for checked items
        if (checkedCount > 0) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$checkedCount artículo(s) marcados",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
                TextButton(
                    onClick = onClearChecked,
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Icon(Icons.Default.DeleteSweep, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Borrar marcados", fontWeight = FontWeight.SemiBold)
                }
            }
        }

        // List
        if (items.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.size(80.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Outlined.ShoppingCart,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Tu lista de compras está vacía",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Escribe arriba lo que necesitas comprar, o presiona 'A la lista' en cualquier producto de tu alacena.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("shopping_list"),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 96.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(
                    items = items,
                    key = { it.id }
                ) { item ->
                    ShoppingCard(
                        item = item,
                        onToggleCheck = { onToggleCheck(item) },
                        onTransferToPantry = { onTransferToPantry(item) },
                        onDelete = { onDeleteItem(item) }
                    )
                }
            }
        }
    }
}

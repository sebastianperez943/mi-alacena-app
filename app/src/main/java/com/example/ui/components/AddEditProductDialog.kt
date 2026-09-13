package com.example.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.PantryItem
import com.example.util.ExpirationHelper

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddEditProductDialog(
    itemToEdit: PantryItem? = null,
    onDismiss: () -> Unit,
    onSave: (name: String, quantity: String, expirationDate: Long?, category: String, notes: String) -> Unit,
    onDelete: (() -> Unit)? = null
) {
    var name by remember { mutableStateOf(itemToEdit?.name ?: "") }
    var quantityNumber by remember {
        val qty = itemToEdit?.quantity ?: "1"
        val regex = Regex("""^(\d+)""")
        val match = regex.find(qty)
        val num = match?.groupValues?.get(1)?.toIntOrNull() ?: 1
        mutableStateOf(num)
    }
    var quantityUnit by remember {
        val qty = itemToEdit?.quantity ?: "1"
        val regex = Regex("""^\d+\s*(.*)$""")
        val match = regex.find(qty)
        mutableStateOf(match?.groupValues?.get(1)?.trim() ?: "")
    }

    var selectedCategory by remember { mutableStateOf(itemToEdit?.category ?: "Despensa") }
    var expirationDate by remember { mutableStateOf<Long?>(itemToEdit?.expirationDate) }
    var notes by remember { mutableStateOf(itemToEdit?.notes ?: "") }
    var nameError by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }

    val categories = listOf(
        "Despensa",
        "Lácteos",
        "Frescos",
        "Enlatados",
        "Bebidas",
        "Condimentos",
        "Limpieza",
        "Otro"
    )

    val unitOptions = listOf("unid.", "paquetes", "kg", "litros", "latas", "piezas")

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .padding(vertical = 16.dp),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (itemToEdit == null) "Agregar a la alacena" else "Editar producto",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Clear, contentDescription = "Cerrar")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Product Name Field
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        if (nameError && it.isNotBlank()) nameError = false
                    },
                    label = { Text("Nombre del producto *") },
                    placeholder = { Text("Ej. Leche, Arroz, Frijoles") },
                    singleLine = true,
                    isError = nameError,
                    supportingText = if (nameError) {
                        { Text("Por favor ingresa un nombre", color = MaterialTheme.colorScheme.error) }
                    } else null,
                    shape = RoundedCornerShape(14.dp),
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_product_name")
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Quantity Section
                Text(
                    text = "Cantidad",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Stepper
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(4.dp)
                        ) {
                            IconButton(
                                onClick = { if (quantityNumber > 1) quantityNumber-- },
                                modifier = Modifier.size(44.dp),
                                colors = IconButtonDefaults.iconButtonColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Icon(Icons.Default.Remove, contentDescription = "Restar")
                            }

                            Text(
                                text = "$quantityNumber",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )

                            IconButton(
                                onClick = { quantityNumber++ },
                                modifier = Modifier.size(44.dp),
                                colors = IconButtonDefaults.iconButtonColors(containerColor = MaterialTheme.colorScheme.primary)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Sumar", tint = MaterialTheme.colorScheme.onPrimary)
                            }
                        }
                    }

                    // Optional custom unit text
                    OutlinedTextField(
                        value = quantityUnit,
                        onValueChange = { quantityUnit = it },
                        label = { Text("Unidad / Detalle") },
                        placeholder = { Text("ej. litros, kg") },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Quick unit chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    unitOptions.forEach { unit ->
                        FilterChip(
                            selected = quantityUnit == unit,
                            onClick = { quantityUnit = if (quantityUnit == unit) "" else unit },
                            label = { Text(unit) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Expiration Date Section
                Text(
                    text = "Fecha de caducidad",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(6.dp))

                // Current expiration badge
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CalendarMonth,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = if (expirationDate != null) {
                                    ExpirationHelper.formatDisplayDate(expirationDate)
                                } else {
                                    "Sin fecha de caducidad"
                                },
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = if (expirationDate != null) FontWeight.Bold else FontWeight.Normal
                                )
                            )
                        }

                        if (expirationDate != null) {
                            TextButton(
                                onClick = { expirationDate = null },
                                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                            ) {
                                Text("Quitar")
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Fast preset chips for expiry
                Text(
                    text = "Accesos rápidos de caducidad:",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    FilterChip(
                        selected = false,
                        onClick = { expirationDate = ExpirationHelper.addDaysToToday(3) },
                        label = { Text("+3 días") }
                    )
                    FilterChip(
                        selected = false,
                        onClick = { expirationDate = ExpirationHelper.addDaysToToday(7) },
                        label = { Text("+1 sem") }
                    )
                    FilterChip(
                        selected = false,
                        onClick = { expirationDate = ExpirationHelper.addMonthsToToday(1) },
                        label = { Text("+1 mes") }
                    )
                    FilterChip(
                        selected = false,
                        onClick = { expirationDate = ExpirationHelper.addMonthsToToday(6) },
                        label = { Text("+6 meses") }
                    )
                    FilterChip(
                        selected = false,
                        onClick = { expirationDate = ExpirationHelper.addYearsToToday(1) },
                        label = { Text("+1 año") }
                    )
                    FilterChip(
                        selected = false,
                        onClick = { showDatePicker = true },
                        label = { Text("📅 Calendario...") },
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            labelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Category Section
                Text(
                    text = "Categoría",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(6.dp))

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    categories.forEach { cat ->
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick = { selectedCategory = cat },
                            label = { Text(cat) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Action Buttons: Guardar (Big Button)
                Button(
                    onClick = {
                        if (name.isBlank()) {
                            nameError = true
                        } else {
                            val fullQuantity = if (quantityUnit.isBlank()) {
                                "$quantityNumber"
                            } else {
                                "$quantityNumber $quantityUnit"
                            }
                            onSave(name, fullQuantity, expirationDate, selectedCategory, notes)
                        }
                    },
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("save_product_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text(
                        text = if (itemToEdit == null) "Guardar en alacena" else "Actualizar producto",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                    ) {
                        Text("Cancelar", fontWeight = FontWeight.SemiBold)
                    }

                    if (itemToEdit != null && onDelete != null) {
                        OutlinedButton(
                            onClick = onDelete,
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Eliminar", fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = expirationDate ?: System.currentTimeMillis()
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        expirationDate = ExpirationHelper.getStartOfDay(it)
                    }
                    showDatePicker = false
                }) {
                    Text("Aceptar", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

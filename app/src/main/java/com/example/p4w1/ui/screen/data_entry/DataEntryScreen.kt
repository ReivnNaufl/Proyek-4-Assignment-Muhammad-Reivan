package com.example.p4w1.ui.screen.data_entry

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.navigation.NavHostController
import com.example.p4w1.viewmodel.DataViewModel
import com.example.p4w1.data.JenisPenyakit
import com.example.p4w1.data.Satuan
import com.example.p4w1.ui.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DataEntryScreen(navController: NavHostController, viewModel: DataViewModel) {
    val context = LocalContext.current
    var kodeProvinsi by remember { mutableStateOf("") }
    var namaProvinsi by remember { mutableStateOf("") }
    var kodeKabupatenKota by remember { mutableStateOf("") }
    var namaKabupatenKota by remember { mutableStateOf("") }
    var total by remember { mutableStateOf("") }
    var satuan by remember { mutableStateOf("") }
    var tahun by remember { mutableStateOf("") }
    var expandedPenyakit by remember { mutableStateOf(false) }
    var selectedPenyakit by remember { mutableStateOf(JenisPenyakit.AIDS) }
    var expandedSatuan by remember { mutableStateOf(false) }
    var selectedSatuan by remember { mutableStateOf(Satuan.KASUS) }
    var pTextFieldSize by remember { mutableStateOf(Size.Zero) }
    var sTextFieldSize by remember { mutableStateOf(Size.Zero) }
    val pIcon = if (expandedPenyakit)
        Icons.Filled.KeyboardArrowUp
    else
        Icons.Filled.KeyboardArrowDown
    val sIcon = if (expandedPenyakit)
        Icons.Filled.KeyboardArrowUp
    else
        Icons.Filled.KeyboardArrowDown

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Input Data",
                style = MaterialTheme.typography.headlineMedium
            )
            OutlinedTextField(
                value = kodeProvinsi,
                onValueChange = { kodeProvinsi = it },
                label = { Text("Kode Provinsi") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = namaProvinsi,
                onValueChange = { namaProvinsi = it },
                label = { Text("Nama Provinsi") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = kodeKabupatenKota,
                onValueChange = { kodeKabupatenKota = it },
                label = { Text("Kode Kabupaten/Kota") },
                modifier = Modifier.fillMaxWidth()
            )
                OutlinedTextField(
                    value = namaKabupatenKota,
                    onValueChange = { namaKabupatenKota = it },
                    label = { Text("Nama Kabupaten/Kota") },
                    modifier = Modifier.fillMaxWidth()
                )
            OutlinedTextField(
                value = selectedPenyakit.displayName,
                onValueChange = { var mSelectedText = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .onGloballyPositioned { coordinates ->
                        // This value is used to assign to
                        // the DropDown the same width
                        pTextFieldSize = coordinates.size.toSize()
                    },
                label = {Text("Label")},
                trailingIcon = {
                    Icon(pIcon,"contentDescription",
                        Modifier.clickable { expandedPenyakit = !expandedPenyakit })
                }
            )
            DropdownMenu(
                expanded = expandedPenyakit,
                onDismissRequest = { expandedPenyakit = false }
            ) {
                JenisPenyakit.values().forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option.displayName) },
                        onClick = {
                            selectedPenyakit = option
                            expandedPenyakit = false
                        }
                    )
                }
            }


                OutlinedTextField(
                    value = total,
                    onValueChange = { total = it },
                    label = { Text("Total") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = selectedSatuan.displayName,
                    onValueChange = { var sSelectedText = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .onGloballyPositioned { coordinates ->
                            // This value is used to assign to
                            // the DropDown the same width
                            sTextFieldSize = coordinates.size.toSize()
                        },
                    label = {Text("Label")},
                    trailingIcon = {
                        Icon(sIcon,"contentDescription",
                            Modifier.clickable { expandedSatuan = !expandedSatuan })
                    }
                )
                DropdownMenu(
                    expanded = expandedSatuan,
                    onDismissRequest = { expandedSatuan = false }
                ) {
                    Satuan.values().forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option.displayName) },
                            onClick = {
                                selectedSatuan = option
                                expandedSatuan = false
                            }
                        )
                    }
                }
                    OutlinedTextField(
                        value = tahun,
                        onValueChange = { tahun = it },
                        label = { Text("Tahun") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Button(
                        onClick = {
                            // Memanggil fungsi insertData pada ViewModel
                            viewModel.insertData(
                                kodeProvinsi = kodeProvinsi,
                                namaProvinsi = namaProvinsi,
                                kodeKabupatenKota = kodeKabupatenKota,
                                namaKabupatenKota = namaKabupatenKota,
                                jenisPenyakit = selectedPenyakit,
                                total = total,
                                satuan = selectedSatuan,
                                tahun = tahun
                            )
                            kodeProvinsi = ""
                            namaProvinsi = ""
                            kodeKabupatenKota = ""
                            namaKabupatenKota = ""
                            selectedPenyakit = JenisPenyakit.AIDS
                            total = ""
                            tahun = ""
                            Toast.makeText(
                                context,
                                "Data berhasil ditambahkan!",
                                Toast.LENGTH_SHORT
                            ).show()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Submit Data")
                    }
                }
            }
        }



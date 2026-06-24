package com.example.a2566_dompetdigital

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

class DompetDigital {
    private var _saldo: Double = 500000.0
    val saldo: Double get() = _saldo

    // Inner Class yang memiliki akses eksklusif ke variabel private _saldo
    inner class OtorisatorKeamanan {
        fun eksekusiPembayaran(jumlah: Double, metode: MetodePembayaran): ResponTransaksi {
            val total = jumlah + metode.biayaAdmin
            val idRand = "TX-${(100000..999999).random()}"

            // Validasi limit transaksi tunggal > 2.000.000
            if (jumlah > 2000000.0) {
                return ResponTransaksi(idRand, jumlah, total, StatusTransaksi.LIMIT_TERLEWATI)
            }

            return if (_saldo >= total) {
                _saldo -= total // Langsung memotong private variable induk
                ResponTransaksi(idRand, jumlah, total, StatusTransaksi.SUKSES)
            } else {
                ResponTransaksi(idRand, jumlah, total, StatusTransaksi.GAGAL)
            }
        }
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                StrukPembayaranScreen()
            }
        }
    }
}


//@Composable
//fun StrukPembayaranScreen() {
//
//    val dompet = remember { DompetDigital() }
//
//    // Instansiasi objek Inner Class membutuhkan referensi dari Outer Class
//    val otorisator = remember { dompet.OtorisatorKeamanan() }
//    var inputNominal by remember { mutableStateOf("") }
//    var strukHasil by remember { mutableStateOf<ResponTransaksi?>(null) }
//    var strukDuplikat by remember { mutableStateOf<ResponTransaksi?>(null) }
//    val metodeTerpilih = remember { PembayaranQris() } // Contoh dari Modul 10
//
//    Column(modifier = Modifier.padding(16.dp)) {
//        Text(text = "Saldo Akun: Rp ${formatRupiah(dompet.saldo)}")
//        OutlinedTextField(
//            value = inputNominal,
//            onValueChange = { inputNominal = it },
//            label = { Text("Nominal Transaksi") }
//        )
//        Button(onClick = {
//            val nominal = inputNominal.toDoubleOrNull() ?: 0.0
//
//            // Memanggil fungsi inner class untuk mendapatkan data class respon
//            val hasil = otorisator.eksekusiPembayaran(nominal, metodeTerpilih)
//            strukHasil = hasil
//
//            // Eksperimen Fungsi Salin (Copy Utility): Membuat duplikat dengan status dimanipulasi
//            strukDuplikat = hasil.copy(status = StatusTransaksi.PENDING)
//        }) {
//            Text("PROSES SEKARANG")
//        }
//
//        // Render struk secara kondisional memanfaatkan properti Data & Enum Class
//        strukHasil?.let { struk ->
//            Card(modifier = Modifier.padding(top = 16.dp)) {
//                Column(modifier = Modifier.padding(8.dp)) {
//                    Text(text = "STRUK ASLI", color = Color.Gray)
//                    Text(text = "ID Transaksi: ${struk.idTransaksi}")
//                    Text(text = "Total Bayar: Rp ${formatRupiah(struk.totalBayar)}")
//                    // Mengambil warnaHex langsung dari properti Enum
//                    Text(
//                        text = "Status: ${struk.status.label}",
//                        color = Color(android.graphics.Color.parseColor(struk.status.warnaHex))
//                    )
//                }
//            }
//        }
//
//        // Menampilkan hasil eksperimen copy
//        strukDuplikat?.let { duplikat ->
//            Card(modifier = Modifier.padding(top = 8.dp)) {
//                Column(modifier = Modifier.padding(8.dp)) {
//                    Text(text = "DUPLIKAT (Eksperimen .copy())", color = Color.Gray)
//                    Text(text = "ID Transaksi: ${duplikat.idTransaksi}") // ID tetap sama
//                    Text(text = "Status Manipulasi: ${duplikat.status.label}")
//                }
//            }
//        }
//    }
//}

@Composable
fun StrukPembayaranScreen() {
    val dompet = remember { DompetDigital() }
    val otorisator = remember { dompet.OtorisatorKeamanan() }
    var inputNominal by remember { mutableStateOf("") }
    var strukHasil by remember { mutableStateOf<ResponTransaksi?>(null) }
    var strukDuplikat by remember { mutableStateOf<ResponTransaksi?>(null) }
    var saldoTampil by remember { mutableStateOf(dompet.saldo) }
    val metodeTerpilih = remember { PembayaranQris() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF0F2F5))
    ) {
        // Header full width
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF2D3E50))
                .padding(vertical = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "E-Receipt Console",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }

        // Konten scrollable
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Saldo Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF2D3E50), RoundedCornerShape(12.dp))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "SISA SALDO AKTIF",
                        color = Color(0xFFA0B0C0),
                        fontSize = 11.sp,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Rp ${formatRupiah(saldoTampil)}",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Input Nominal
            Column {
                Text(
                    text = "Masukkan Nominal",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF444444)
                )
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = inputNominal,
                    onValueChange = { inputNominal = it },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF2D3E50),
                        unfocusedBorderColor = Color(0xFFCCCCCC)
                    )
                )
            }

            // Tombol Proses
            Button(
                onClick = {
                    val nominal = inputNominal.toDoubleOrNull() ?: 0.0
                    val hasil = otorisator.eksekusiPembayaran(nominal, metodeTerpilih)
                    strukHasil = hasil
                    strukDuplikat = hasil.copy(status = StatusTransaksi.PENDING)
                    saldoTampil = dompet.saldo
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2D3E50))
            ) {
                Text(
                    text = "PROSES SEKARANG",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 0.5.sp
                )
            }

            // Struk Asli
            strukHasil?.let { struk ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFC0C8D0))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "Rincian Struk Pembayaran",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF333333),
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        HorizontalDivider(color = Color(0xFFE0E0E0))
                        Spacer(modifier = Modifier.height(8.dp))

                        StrukRow(label = "ID Transaksi", value = struk.idTransaksi, valueWeight = FontWeight.Medium)
                        StrukRow(label = "Nominal Dasar", value = "Rp ${formatRupiah(struk.nominalAwal)}")
                        StrukRow(label = "Biaya Penanganan", value = "Rp ${formatRupiah(struk.totalBayar - struk.nominalAwal)}")

                        Spacer(modifier = Modifier.height(4.dp))
                        HorizontalDivider(color = Color(0xFFE0E0E0))
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Total Potong Saldo",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF222222)
                            )
                            Text(
                                text = "Rp ${formatRupiah(struk.totalBayar)}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF2980B9)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            val badgeColor = Color(android.graphics.Color.parseColor(struk.status.warnaHex))
                            Box(
                                modifier = Modifier
                                    .background(badgeColor, RoundedCornerShape(20.dp))
                                    .padding(horizontal = 18.dp, vertical = 5.dp)
                            ) {
                                Text(
                                    text = struk.status.label,
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    letterSpacing = 0.5.sp
                                )
                            }
                        }
                    }
                }
            }

            // Struk Duplikat
            strukDuplikat?.let { duplikat ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFC0C8D0))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "Duplikat .copy() — Eksperimen",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF888888),
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        HorizontalDivider(color = Color(0xFFE0E0E0))
                        Spacer(modifier = Modifier.height(8.dp))

                        StrukRow(label = "ID Transaksi", value = duplikat.idTransaksi, valueWeight = FontWeight.Medium)

                        Spacer(modifier = Modifier.height(6.dp))
                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            val badgeColor = Color(android.graphics.Color.parseColor(duplikat.status.warnaHex))
                            Box(
                                modifier = Modifier
                                    .background(badgeColor, RoundedCornerShape(20.dp))
                                    .padding(horizontal = 18.dp, vertical = 5.dp)
                            ) {
                                Text(
                                    text = duplikat.status.label,
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    letterSpacing = 0.5.sp
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
fun StrukRow(
    label: String,
    value: String,
    valueWeight: FontWeight = FontWeight.Normal
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 12.sp, color = Color(0xFF666666))
        Text(text = value, fontSize = 12.sp, fontWeight = valueWeight, color = Color(0xFF333333))
    }
}

fun formatRupiah(nominal: Double): String {
    return "%,.0f".format(nominal).replace(',', '.')
}

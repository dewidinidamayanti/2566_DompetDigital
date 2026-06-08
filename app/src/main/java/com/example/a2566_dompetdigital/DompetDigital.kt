package com.example.dompetdigital

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.a2566_dompetdigital.MetodePembayaran
import com.example.a2566_dompetdigital.PembayaranPayLater
import com.example.a2566_dompetdigital.PembayaranQris
import com.example.a2566_dompetdigital.TransferBank
import java.text.NumberFormat
import java.util.Locale

// ── Model Data ───────────────────────────────────────────────
class DompetDigital {
    private var pin: String = "1234"

    var saldo by mutableDoubleStateOf(500000.0)
        private set

    fun topUp(jumlah: Double): String {
        return if (jumlah < 10000) {
            "*Gagal: Minimal Top Up Rp 10.000"
        } else if (saldo + jumlah > 10000000) {
            "*Gagal: Saldo maksimal Rp 10.000.000"
        } else {
            saldo += jumlah
            "Top up berhasil!"
        }
    }

    fun tarikTunai(jumlah: Double, pinInput: String): String {
        return if (pinInput != pin) {
            "*Gagal: PIN salah!"
        } else if (jumlah > saldo) {
            "*Gagal: Saldo tidak cukup!"
        } else {
            saldo -= jumlah
            "Tarik tunai berhasil!"
        }
    }

    fun lakukanTransaksi(jumlah: Double, metode: MetodePembayaran): String {
        if (jumlah > metode.limitMaksimal) {
            return "Gagal: Melebihi limit ${metode.namaMetode} (Maks: Rp ${metode.limitMaksimal})"
        }
        return if (saldo >= jumlah) {
            saldo -= jumlah
            metode.prosesBayar(jumlah)
        } else {
            "Gagal: Saldo tidak mencukupi untuk metode ${metode.namaMetode}"
        }
    }
}

// ── Helper ───────────────────────────────────────────────────
fun formatRupiah(amount: Double): String {
    val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
    return formatter.format(amount)
        .replace(",00", "")
        .replace("Rp", "Rp ")
}

// ── UI Composable ────────────────────────────────────────────
@Composable
fun EWalletScreen() {
    val dompet = remember { DompetDigital() }

    var inputJumlah by remember { mutableStateOf("") }
    var infoHasil by remember { mutableStateOf("Belum ada transaksi") }

    val opsiPembayaran: List<MetodePembayaran> = remember {
        listOf(
            PembayaranQris(),
            TransferBank("BCA"),
            TransferBank("Mandiri"),
            PembayaranPayLater()
        )
    }
    var metodeTerpilih by remember { mutableStateOf<MetodePembayaran>(opsiPembayaran[0]) }

    val blueColor = Color(0xFF2E86C1)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .background(blueColor),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Smart Pay Gateway",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Kartu Saldo
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 30.dp)
                .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9F9))
        ) {
            Column(
                modifier = Modifier
                    .padding(vertical = 24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "SISA SALDO DOMPET",
                    color = Color.Gray,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = formatRupiah(dompet.saldo),
                    color = if (dompet.saldo < 50000) Color.Red else Color(0xFF2C3E50),
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )
                if (dompet.saldo < 50000) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "⚠ Saldo menipis!",
                        color = Color.Red,
                        fontSize = 12.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Input Nominal
        OutlinedTextField(
            value = inputJumlah,
            onValueChange = { inputJumlah = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 30.dp),
            placeholder = { Text("Nominal Transaksi", color = Color.LightGray) },
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Pilih Metode Pembayaran
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 30.dp)
        ) {
            Text(
                text = "Pilih Metode Pembayaran:",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF2C3E50)
            )

            Spacer(modifier = Modifier.height(8.dp))

            opsiPembayaran.forEach { metode ->
                val isSelected = metodeTerpilih == metode

                val ikonWarna = when (metode) {
                    is PembayaranQris -> Color(0xFF1565C0)
                    is PembayaranPayLater -> Color(0xFFE65100)
                    else -> Color(0xFF2E7D32)
                }

                val badgeText = when (metode) {
                    is PembayaranQris -> "Instan"
                    is PembayaranPayLater -> "Tertunda"
                    else -> "Transfer"
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .border(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) blueColor else Color(0xFFE0E0E0),
                            shape = RoundedCornerShape(10.dp)
                        )
                        .background(
                            color = if (isSelected) Color(0xFFE3F2FD) else Color.White,
                            shape = RoundedCornerShape(10.dp)
                        )
                        .clickable { metodeTerpilih = metode }
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = isSelected,
                        onClick = { metodeTerpilih = metode },
                        colors = RadioButtonDefaults.colors(selectedColor = blueColor)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = metode.namaMetode,
                        fontSize = 14.sp,
                        color = ikonWarna,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                        modifier = Modifier.weight(1f)
                    )
                    Box(
                        modifier = Modifier
                            .background(
                                color = ikonWarna.copy(alpha = 0.12f),
                                shape = RoundedCornerShape(100.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = badgeText,
                            fontSize = 11.sp,
                            color = ikonWarna,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Tombol Bayar
        Button(
            onClick = {
                val nominal = inputJumlah.toDoubleOrNull() ?: 0.0
                infoHasil = dompet.lakukanTransaksi(nominal, metodeTerpilih)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp)
                .padding(horizontal = 30.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = blueColor)
        ) {
            Text("BAYAR SEKARANG", fontSize = 16.sp, color = Color.White)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Status Transaksi
        if (infoHasil.isNotEmpty()) {
            Text(
                text = "Status Transaksi:",
                fontSize = 13.sp,
                color = Color.Gray,
                modifier = Modifier.padding(horizontal = 30.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = infoHasil,
                color = if (infoHasil.startsWith("Gagal")) Color.Red else Color(0xFF1565C0),
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 30.dp)
            )
        }
    }
}
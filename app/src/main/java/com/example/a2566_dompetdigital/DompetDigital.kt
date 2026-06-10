package com.example.a2566_dompetdigital

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import java.text.NumberFormat
import java.util.Locale


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
            metode.mainkanSuaraNotifikasi() // Tugas No.2
            metode.prosesBayar(jumlah)
        } else {
            "Gagal: Saldo tidak mencukupi untuk metode ${metode.namaMetode}"
        }
    }
}


fun formatRupiah(amount: Double): String {
    val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
    return formatter.format(amount)
        .replace(",00", "")
        .replace("Rp", "Rp ")
}


@Composable
fun PaymentMethodCard(
    metode: MetodePembayaran,
    isSelected: Boolean,
    onClick: () -> Unit
) {

    val accentColor = Color(android.graphics.Color.parseColor(metode.warnaAksenHex))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        border = if (isSelected) BorderStroke(2.dp, accentColor)
        else BorderStroke(1.dp, Color(0xFFE0E0E0)),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                Color(android.graphics.Color.parseColor(metode.warnaAksenHex))
                    .copy(alpha = 0.08f)
            else Color(0xFFF8F9F9)
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {

                Text(
                    text = metode.namaMetode,
                    color = accentColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = metode.labelPetunjuk,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
            Spacer(modifier = Modifier.width(8.dp))

            Box(
                modifier = Modifier
                    .background(
                        color = accentColor.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(100.dp)
                    )
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = metode.dapatkanJenisBadge(),
                    fontSize = 11.sp,
                    color = accentColor,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}


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


    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .background(blueColor),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Polymorphic Payment UI",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }


        item {
            Spacer(modifier = Modifier.height(20.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9F9))
            ) {
                Column(
                    modifier = Modifier
                        .padding(vertical = 20.dp)
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
        }


        item {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "METODE PEMBAYARAN TERSEDIA",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Gray,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
        }


        items(opsiPembayaran) { metode ->
            PaymentMethodCard(
                metode = metode,
                isSelected = (metodeTerpilih == metode),
                onClick = { metodeTerpilih = metode }
            )
        }


        item {
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = inputJumlah,
                onValueChange = { inputJumlah = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                placeholder = { Text("Nominal", color = Color.LightGray) },
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    val nominal = inputJumlah.toDoubleOrNull() ?: 0.0
                    // Tugas No.3 — saldo berkurang polimorfik
                    infoHasil = dompet.lakukanTransaksi(nominal, metodeTerpilih)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp)
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = blueColor)
            ) {
                Text("PROSES TRANSAKSI", fontSize = 16.sp, color = Color.White)
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (infoHasil.isNotEmpty()) {
                Text(
                    text = "Status Transaksi:",
                    fontSize = 13.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = infoHasil,
                    color = if (infoHasil.startsWith("Gagal")) Color.Red
                    else Color(0xFF1565C0),
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
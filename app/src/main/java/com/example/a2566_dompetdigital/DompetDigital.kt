package com.example.a2566_dompetdigital

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import java.text.NumberFormat
import java.util.Locale

// Model data DompetDigital
class DompetDigital {
    private var pin: String = "1234"

    var saldo by mutableStateOf(150000.0)
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
}

// Fungsi pembantu format rupiah
fun formatRupiah(amount: Double): String {
    val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
    return formatter.format(amount)
        .replace(",00", "")
        .replace("Rp", "Rp ")
}

@Composable
fun WalletScreen() {
    val dompet = remember { DompetDigital() }

    var statusText by remember { mutableStateOf("") }
    var inputJumlah by remember { mutableStateOf("") }
    var inputPin by remember { mutableStateOf("") }

    val blueColor = Color(0xFF2E86C1)
    val orangeColor = Color(0xFFEB812A)
    val cardBgColor = Color(0xFFF8F9F9)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .background(blueColor),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "MyWallet v1.0",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(30.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 30.dp)
                .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = cardBgColor)
        ) {
            Column(
                modifier = Modifier
                    .padding(vertical = 30.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "TOTAL SALDO",
                    color = Color.Gray,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = formatRupiah(dompet.saldo),
                    color = if (dompet.saldo < 50000) Color.Red else Color(0xFF2C3E50),
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        OutlinedTextField(
            value = inputJumlah,
            onValueChange = { inputJumlah = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 45.dp),
            placeholder = {
                Text("Masukkan Jumlah", color = Color.LightGray)
            },
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = inputPin,
            onValueChange = { inputPin = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 45.dp),
            placeholder = {
                Text("Masukkan PIN", color = Color.LightGray)
            },
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(30.dp))

        Button(
            onClick = {
                val jumlah = inputJumlah.toDoubleOrNull() ?: 0.0
                statusText = dompet.topUp(jumlah)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp)
                .padding(horizontal = 30.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = blueColor)
        ) {
            Text("Top Up", fontSize = 16.sp, color = Color.White)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val jumlah = inputJumlah.toDoubleOrNull() ?: 0.0
                statusText = dompet.tarikTunai(jumlah, inputPin)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp)
                .padding(horizontal = 30.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = orangeColor)
        ) {
            Text("Tarik Tunai", fontSize = 16.sp, color = Color.White)
        }

        Spacer(modifier = Modifier.height(25.dp))

        if (statusText.isNotEmpty()) {
            Text(
                text = statusText,
                color = if (statusText.contains("berhasil")) {
                    Color(0xFF388E3C)
                } else {
                    Color.Red
                },
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 32.dp)
            )
        }
    }
}

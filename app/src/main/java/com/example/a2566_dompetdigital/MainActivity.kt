package com.example.a2566_dompetdigital

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt

class DompetDigital {
    private var _saldo: Double = 500000.0

    val saldo: Double
        get() = _saldo

    fun lakukanTransaksi(jumlah: Double, metode: MetodePembayaran): String {

        val totalBayar = jumlah + metode.biayaAdmin
        
        if (jumlah <= 0.0) {
            return "Gagal: Nominal transaksi harus lebih dari Rp 0"
        }

        if (_saldo < totalBayar) {
            return "Gagal: Saldo tidak mencukupi untuk total Rp ${formatRupiah(totalBayar)}"
        }

        _saldo -= totalBayar
        val hasilTransaksi = metode.prosesBayar(jumlah)
        metode.mainkanSuaraNotifikasi()
        

        return "$hasilTransaksi (Total Bayar: Rp ${formatRupiah(totalBayar)})"
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                PolymorphicPaymentScreen()
            }
        }
    }
}

@Composable
fun PolymorphicPaymentScreen() {
    val dompet = remember { DompetDigital() }
    val opsiMetode = remember {
        listOf(
            PembayaranQris(),
            TransferBank("BCA"),
            TransferBank("Mandiri"),
            PembayaranPromo()
        )
    }

    var metodeTerpilih by remember { mutableStateOf(opsiMetode[0]) }
    var nominal by remember { mutableStateOf("120000") }
    var saldoTampil by remember { mutableDoubleStateOf(dompet.saldo) }
    var statusTransaksi by remember { mutableStateOf("Belum ada transaksi") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF3F5F5))
    ) {
        PaymentHeader()

        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                PaymentSectionTitle()
            }

            items(opsiMetode) { metode ->
                PaymentMethodCard(
                    metode = metode,
                    isSelected = metodeTerpilih == metode,
                    onClick = { metodeTerpilih = metode }
                )
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Saldo Dompet: Rp ${formatRupiah(saldoTampil)}",
                    color = Color(0xFF1B3348),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 6.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Nominal",
                    color = Color(0xFF1B3348),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 6.dp)
                )

                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = nominal,
                    onValueChange = { nominal = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 6.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    )
                )

                Spacer(modifier = Modifier.height(26.dp))
                Button(
                    onClick = {
                        val jumlah = nominal.toDoubleOrNull() ?: 0.0
                        statusTransaksi = dompet.lakukanTransaksi(jumlah, metodeTerpilih)
                        saldoTampil = dompet.saldo
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 6.dp)
                        .height(48.dp),
                    shape = RoundedCornerShape(6.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1FA58A)
                    )
                ) {
                    Text(
                        text = "PROSES TRANSAKSI",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                StatusTransaksiCard(statusTransaksi)
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun PaymentHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF1FA58A))
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Smart Payment System",
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun PaymentSectionTitle() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 6.dp, vertical = 10.dp),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        border = BorderStroke(1.dp, Color(0xFFD5DCE2))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 18.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "METODE PEMBAYARAN TERSEDIA",
                color = Color(0xFF7C8A96),
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun PaymentMethodCard(
    metode: MetodePembayaran,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val accentColor = Color(metode.warnaAksenHex.toColorInt())

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 6.dp, vertical = 6.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        border = if (isSelected) BorderStroke(2.dp, accentColor) else BorderStroke(1.dp, Color(0xFFD5DCE2))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = buildAnnotatedString {
                        append(metode.namaMetode)
                        if (isSelected) {
                            withStyle(style = SpanStyle(color = Color.Red)) {
                                append(" (Selected)")
                            }
                        }
                    },
                    color = accentColor,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Admin: Rp ${formatRupiah(metode.biayaAdmin)}",
                    color = Color(0xFF6B7C88),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = metode.labelPetunjuk,
                color = Color(0xFF6B7C88),
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            BadgeMetode(
                text = metode.dapatkanJenisBadge(),
                color = accentColor
            )
        }
    }
}

@Composable
fun BadgeMetode(text: String, color: Color) {
    Box(
        modifier = Modifier
            .background(color, RoundedCornerShape(4.dp))
            .padding(horizontal = 8.dp, vertical = 5.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun StatusTransaksiCard(statusTransaksi: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 6.dp, vertical = 12.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = "Status Transaksi:",
                color = Color(0xFF1B3348),
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = statusTransaksi,
                color = Color(0xFF6B7C88),
                fontSize = 13.sp
            )
        }
    }
}

fun formatRupiah(nominal: Double): String {
    return "%,.0f".format(nominal).replace(',', '.')
}

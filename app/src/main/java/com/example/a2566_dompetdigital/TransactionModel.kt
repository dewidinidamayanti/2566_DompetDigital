package com.example.a2566_dompetdigital

// 1. Enum Class untuk membatasi status transaksi dengan properti visual bawaan
enum class StatusTransaksi(val label: String, val warnaHex: String) {
    SUKSES("Sukses", "#2ecc71"),
    PENDING("Pending", "#f1c40f"),
    GAGAL("Gagal", "#e74c3c"),
    LIMIT_TERLEWATI("Limit Gagal", "#d35400")
}
// 2. Data Class sebagai container data invoice bersih
data class ResponTransaksi(
    val idTransaksi: String,
    val nominalAwal: Double,
    val totalBayar: Double,
    val status: StatusTransaksi
)
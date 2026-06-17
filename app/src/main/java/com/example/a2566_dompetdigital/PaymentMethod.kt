package com.example.a2566_dompetdigital

import android.util.Log

private const val TAG_NOTIFIKASI_SUARA = "NotifikasiSuara"

abstract class MetodePembayaran(
    val namaMetode: String,
    open val biayaAdmin: Double
) {
    // Properti polimorfik untuk mengatur visualisasi di UI
    abstract val warnaAksenHex: String
    abstract val labelPetunjuk: String

    abstract fun prosesBayar(jumlah: Double): String

    abstract fun dapatkanJenisBadge(): String

    open fun mainkanSuaraNotifikasi() {
        Log.d(TAG_NOTIFIKASI_SUARA, "Ping!")
    }
}

class PembayaranQris : MetodePembayaran("QRIS Digital Pay", 500.0) {
    override val warnaAksenHex = "#9b59b6" // Warna Ungu Khas QRIS
    override val labelPetunjuk = "Scan QR menggunakan aplikasi e-wallet anda"

    override fun prosesBayar(jumlah: Double) = "Token QRIS diterbitkan sebesar Rp ${jumlah + biayaAdmin}"

    override fun dapatkanJenisBadge() = "Instan"

    override fun mainkanSuaraNotifikasi() {
        Log.d(TAG_NOTIFIKASI_SUARA, "QRIS Berhasil, Kasir Pintar!")
    }
}

class TransferBank(val bank: String) : MetodePembayaran("Bank $bank", 2500.0) {
    override val warnaAksenHex = "#2980b9" // Warna Biru Perbankan
    override val labelPetunjuk = "Gunakan nomor Virtual Account untuk transfer"

    override fun prosesBayar(jumlah: Double) = "VA $bank siap menerima transfer Rp ${jumlah + biayaAdmin}"

    override fun dapatkanJenisBadge() = "Manual VA"
}

class PembayaranPromo : MetodePembayaran("Promo Merdeka", 0.0) {
    override val biayaAdmin: Double = 0.0
    
    override val warnaAksenHex = "#e67e22"
    override val labelPetunjuk = "Gunakan kode promo untuk biaya admin gratis!"

    override fun prosesBayar(jumlah: Double): String {
        return "Promo Berhasil! Anda membayar Rp ${jumlah + biayaAdmin} tanpa biaya admin"
    }

    override fun dapatkanJenisBadge() = "PROMO"
}

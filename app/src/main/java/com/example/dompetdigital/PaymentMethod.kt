package com.example.dompetdigital

abstract class MetodePembayaran(val namaMetode: String) {
    abstract val limitMaksimal: Double
    abstract fun prosesBayar(jumlah: Double): String
}

class PembayaranQris : MetodePembayaran("QRIS") {
    override val limitMaksimal: Double = 2_000_000.0
    override fun prosesBayar(jumlah: Double): String {
        return "Token QRIS Berhasil Dibuat. Silakan scan untuk membayar Rp $jumlah"
    }
}

class TransferBank(val namaBank: String) : MetodePembayaran("Transfer Bank $namaBank") {
    override val limitMaksimal: Double = 10_000_000.0
    override fun prosesBayar(jumlah: Double): String {
        val nomorVA = (100000..999999).random()
        return "Transfer Rp $jumlah ke Virtual Account $namaBank: 8800$nomorVA"
    }
}

class PembayaranPayLater : MetodePembayaran("PayLater") {
    override val limitMaksimal: Double = 5_000_000.0
    private val biayaAdmin = 0.05
    override fun prosesBayar(jumlah: Double): String {
        val admin = jumlah * biayaAdmin
        val total = jumlah + admin
        return "PayLater disetujui! Jumlah: Rp $jumlah + Admin 5% (Rp $admin) = Total Rp $total"
    }
}
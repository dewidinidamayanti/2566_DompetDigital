package com.example.a2566_dompetdigital

abstract class MetodePembayaran(val namaMetode: String) {

    abstract val warnaAksenHex: String
    abstract val labelPetunjuk: String

    abstract fun dapatkanJenisBadge(): String

    open fun mainkanSuaraNotifikasi() {
        println("Ping!")
    }

    // Dari Modul 08
    abstract val limitMaksimal: Double
    abstract fun prosesBayar(jumlah: Double): String
}

class PembayaranQris : MetodePembayaran("QRIS") {
    override val warnaAksenHex = "#9b59b6"
    override val labelPetunjuk = "Scan barcode via galeri atau kamera smartphone"
    override val limitMaksimal: Double = 2_000_000.0

    override fun dapatkanJenisBadge(): String = "Instan"

    // Tugas No.2 — override khusus QRIS
    override fun mainkanSuaraNotifikasi() {
        println("QRIS Berhasil, Kasir Pintar!")
    }

    override fun prosesBayar(jumlah: Double): String =
        "Token QRIS Rp $jumlah diterbitkan."
}

class TransferBank(val namaBank: String) : MetodePembayaran("Bank $namaBank") {
    override val warnaAksenHex = "#2980b9"
    override val labelPetunjuk = "Salin nomor Virtual Account untuk transfer"
    override val limitMaksimal: Double = 10_000_000.0

    override fun dapatkanJenisBadge(): String = "Manual VA"

    override fun prosesBayar(jumlah: Double): String {
        val nomorVA = (100000..999999).random()
        return "Nomor VA $namaBank dibuat untuk nominal Rp $jumlah. VA: 8800$nomorVA"
    }
}

class PembayaranPayLater : MetodePembayaran("PayLater") {
    override val warnaAksenHex = "#e67e22"
    override val labelPetunjuk = "Bayar nanti, cicilan otomatis bulan depan"
    override val limitMaksimal: Double = 5_000_000.0

    override fun dapatkanJenisBadge(): String = "Tertunda"

    override fun prosesBayar(jumlah: Double): String {
        val admin = jumlah * 0.05
        val total = jumlah + admin
        return "PayLater disetujui! Jumlah: Rp $jumlah + Admin 5% (Rp $admin) = Total Rp $total"
    }
}
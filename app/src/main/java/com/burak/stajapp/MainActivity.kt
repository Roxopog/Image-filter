package com.burak.stajapp

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.burak.stajapp.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
public var asd = 0
class MainActivity : AppCompatActivity() {
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private var secilenGorsel: Uri? = null
    private var secilenBitmap: Bitmap? = null
    private lateinit var binding: ActivityMainBinding
    private var islenenBitmap: Bitmap? = null
    private var sonBitmap: Bitmap? = secilenBitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        registerLaunchers()

        binding.grayScaleButton.setOnClickListener {
            sonBitmap?.let {
                val grayscaleBitmap = makeGray(it)
                binding.imageView.setImageBitmap(grayscaleBitmap)
                islenenBitmap = grayscaleBitmap
                sonBitmap = grayscaleBitmap
            }
        }
        binding.extractRedButton.setOnClickListener {
            secilenBitmap?.let {
                val extractedBitmap = extract_red(it)
                binding.imageView.setImageBitmap(extractedBitmap)
                islenenBitmap = extractedBitmap
                sonBitmap = extractedBitmap
            }
        }

        binding.originalButton.setOnClickListener {
            secilenBitmap?.let {
                binding.imageView.setImageBitmap(it)
                islenenBitmap = it
            }
        }
        binding.redAndGray.setOnClickListener {
            secilenBitmap?.let {
                val extractedBitmap = redAndGray(it)
                binding.imageView.setImageBitmap(extractedBitmap)
                islenenBitmap = extractedBitmap
                sonBitmap = extractedBitmap
            }
        }
        binding.histogramButton.setOnClickListener {
            islenenBitmap?.let {
                val file = File(cacheDir, "image")
                file.outputStream().use { fos ->
                    it.compress(Bitmap.CompressFormat.PNG, 100, fos)
                }
                val intent = Intent(this, HistogramActivity::class.java)
                intent.putExtra("imagePath", file.absolutePath)
                startActivity(intent)
            } ?: Toast.makeText(this, "Önce bir resim işleyin.", Toast.LENGTH_SHORT).show()
        }



        binding.downloadButton.setOnClickListener {
            islenenBitmap?.let {
                saveImageToDownloads(it)
            } ?: Toast.makeText(this, "Önce bir resim işleyin.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun registerLaunchers() {
        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)
            } else {
                Snackbar.make(binding.root, "İzin verilmedi!", Snackbar.LENGTH_SHORT).show()
            }
        }

        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                secilenGorsel = result.data?.data
                try {
                    secilenBitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, secilenGorsel)
                    secilenBitmap?.let {
                        val originalWidth = it.width
                        val originalHeight = it.height
                        val originalPixelCount = originalHeight * originalWidth

                        val kucukBitmap = kucukBitMapOlustur(it, 512)
                        binding.imageView.setImageBitmap(secilenBitmap)

                        // Yeniden boyutlandırılmış görselin boyutlarını al ve başka bir TextView'e yazdır (pixelText)
                        val resizedWidth = kucukBitmap.width
                        val resizedHeight = kucukBitmap.height
                        val resizedPixelCount = resizedHeight * resizedWidth
                        binding.pixelText.text = "  Pixel boyutları Orijinal: Width: $resizedWidth, Height: $resizedHeight , thumbnail pixel count: $resizedPixelCount"
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun gorselSec(view: View) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_MEDIA_IMAGES)) {
                    Snackbar.make(view, "Bu izin gerekli, fotoğraf seçmek için izine ihtiyacımız var.", Snackbar.LENGTH_INDEFINITE).setAction(
                        "İzin ver", {
                            permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                        }
                    ).show()
                } else {
                    permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                }
            } else {
                val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    Snackbar.make(view, "Bu izin gerekli, fotoğraf seçmek için izine ihtiyacımız var.", Snackbar.LENGTH_INDEFINITE).setAction(
                        "İzin ver", {
                            permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                        }
                    ).show()
                } else {
                    permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                }
            } else {
                val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)
            }
        }
    }
    fun openHistogramActivity(view: View) {
        val intent = Intent(this, HistogramActivity::class.java)
        val byteArrayOutputStream = ByteArrayOutputStream()
        islenenBitmap?.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        intent.putExtra("processedBitmap", byteArray)
        startActivity(intent)
    }




    private fun kucukBitMapOlustur(kullanicininSectigiBitMap: Bitmap, maximumBoyut: Int): Bitmap {
        val width = kullanicininSectigiBitMap.width
        val height = kullanicininSectigiBitMap.height
        val oran: Double = width.toDouble() / height.toDouble()
        var yeniWidth: Int
        var yeniHeight: Int

        if (oran > 1) {
            // Görsel yatay.
            yeniWidth = maximumBoyut
            yeniHeight = (maximumBoyut / oran).toInt()
        } else {
            // Görsel dikey.
            yeniHeight = maximumBoyut
            yeniWidth = (maximumBoyut * oran).toInt()
        }

        return Bitmap.createScaledBitmap(kullanicininSectigiBitMap, yeniWidth, yeniHeight, true)
    }


    private fun extract_red(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val extractedBitmap = Bitmap.createBitmap(width, height, bitmap.config)

        for (x in 0 until width) {
            for (y in 0 until height) {
                val pixel = bitmap.getPixel(x, y)
                val red = Color.red(pixel)
                val green = Color.green(pixel)
                val blue = Color.blue(pixel)

                // Kırmızı dışındaki renkleri siyah yapma
                if (!(red > 150 && green < 50 && blue < 50)) {
                    extractedBitmap.setPixel(x, y, Color.BLACK)
                } else {
                    extractedBitmap.setPixel(x, y, Color.rgb(red, green, blue))
                }
            }
        }

        return extractedBitmap
    }
    private fun redAndGray(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        var extractedBitmap = Bitmap.createBitmap(width, height, bitmap.config)
        extractedBitmap = extract_red(bitmap)
        extractedBitmap = makeGray(extractedBitmap)
        return extractedBitmap
    }


    private fun makeGray(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val grayscaleBitmap = Bitmap.createBitmap(width, height, bitmap.config)

        for (i in 0 until height) {
            for (j in 0 until width) {
                val pixel = bitmap.getPixel(j, i)

                val red = (pixel shr 16) and 0xFF
                val green = (pixel shr 8) and 0xFF
                val blue = pixel and 0xFF

                val gray = ((red + green + blue) / 3).toInt()

                val newPixel = (0xFF shl 24) or (gray shl 16) or (gray shl 8) or gray
                grayscaleBitmap.setPixel(j, i, newPixel)
            }
        }

        return grayscaleBitmap
    }


    private fun saveImageToDownloads(bitmap: Bitmap) {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val filename = "image_$timestamp.png"

        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, filename)
            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
            put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000)
            put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis())
        }

        val resolver = contentResolver
        val uri: Uri? = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        if (uri != null) {
            var outputStream: OutputStream? = null
            try {
                outputStream = resolver.openOutputStream(uri)
                if (outputStream != null) {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                }
                outputStream?.flush()
                Toast.makeText(this, "Fotoğraf başarıyla galeriye kaydedildi.", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, "Fotoğraf kaydedilemedi. Hata: ${e.message}", Toast.LENGTH_LONG).show()
                println("Fotoğraf kaydedilemedi. Hata: ${e.message}")
            } finally {
                outputStream?.close()
            }
        }
    }
}

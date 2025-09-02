package com.cg128.aprendekotlindesdecero2.Menu.Maps

import android.net.Uri
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import kotlinx.coroutines.launch
import com.cg128.aprendekotlindesdecero2.R
import java.io.File
import java.io.FileOutputStream

class AddPlaceActivity : AppCompatActivity() {

    private lateinit var db: PlaceDatabase
    private var imageUri: Uri? = null

    private lateinit var etName: EditText
    private lateinit var etDescription: EditText
    private lateinit var etLatitude: EditText
    private lateinit var etLongitude: EditText
    private lateinit var ivPreview: ImageView
    private lateinit var btnSelectImage: Button
    private lateinit var btnSavePlace: Button
    private var imagePath: String = "" // Nuevo: path local de la imagen


    // Nuevo: launcher para seleccionar imágenes
    private val pickImage =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                imageUri = result.data?.data
                imageUri?.let {
                    imagePath = copyImageToInternalStorage(it) // Guardar path local
                }
                ivPreview.setImageURI(imageUri)
            }
        }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_place)

        db = Room.databaseBuilder(applicationContext, PlaceDatabase::class.java, "places.db").build()

        // Inicializar vistas
        etName = findViewById(R.id.etName)
        etDescription = findViewById(R.id.etDescription)
        etLatitude = findViewById(R.id.etLatitude)
        etLongitude = findViewById(R.id.etLongitude)
        ivPreview = findViewById(R.id.ivPreview)
        btnSelectImage = findViewById(R.id.btnSelectImage)
        btnSavePlace = findViewById(R.id.btnSavePlace)

        // Cargar datos si vienen de MainActivity (marcador seleccionado)
        val latitude = intent.getDoubleExtra("latitude", 0.0)
        val longitude = intent.getDoubleExtra("longitude", 0.0)
        val name = intent.getStringExtra("name") ?: ""
        val description = intent.getStringExtra("description") ?: ""

        etName.setText(name)
        etDescription.setText(description)
        etLatitude.setText(latitude.toString())
        etLongitude.setText(longitude.toString())

        // Usar el nuevo launcher en lugar de onActivityResult
        btnSelectImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            pickImage.launch(intent)
        }

        btnSavePlace.setOnClickListener {
            val place = Place(
                name = etName.text.toString(),
                description = etDescription.text.toString(),
                latitude = etLatitude.text.toString().toDoubleOrNull() ?: 0.0,
                longitude = etLongitude.text.toString().toDoubleOrNull() ?: 0.0,
                imageUri = imagePath
            )

            lifecycleScope.launch {
                db.placeDao().insert(place)
                finish()
            }
        }
    }

    private fun copyImageToInternalStorage(uri: Uri): String {
        val inputStream = contentResolver.openInputStream(uri) ?: return ""
        val file = File(filesDir, "place_${System.currentTimeMillis()}.jpg")
        val outputStream = FileOutputStream(file)
        inputStream.copyTo(outputStream)
        inputStream.close()
        outputStream.close()
        return file.absolutePath
    }

}

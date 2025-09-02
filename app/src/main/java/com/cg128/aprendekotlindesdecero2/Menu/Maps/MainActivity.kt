package com.cg128.aprendekotlindesdecero2.Menu.Maps

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.cg128.aprendekotlindesdecero2.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.osmdroid.bonuspack.location.GeocoderNominatim
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.infowindow.MarkerInfoWindow
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray

class MainActivity : AppCompatActivity() {

    private lateinit var map: MapView
    private lateinit var db: PlaceDatabase
    private var selectedMarker: Marker? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Configuration.getInstance().load(applicationContext, getSharedPreferences("prefs", MODE_PRIVATE))
        setContentView(R.layout.activity_main)

        db = Room.databaseBuilder(applicationContext, PlaceDatabase::class.java, "places.db").build()
        map = findViewById(R.id.map)
        map.setMultiTouchControls(true)

        val startPoint = GeoPoint(19.432608, -99.133209)
        map.controller.setZoom(10.0)
        map.controller.setCenter(startPoint)

        setupMapLongPress()
        loadPlacesFromDatabase()
        setupAddDeleteButtons()
        setupSearchBar()
    }

    // -------------------- MAP & DATABASE --------------------

    private fun setupMapLongPress() {
        val mapEventsReceiver = object : MapEventsReceiver {
            override fun singleTapConfirmedHelper(p: GeoPoint?) = false

            override fun longPressHelper(p: GeoPoint?): Boolean {
                if (p != null) {
                    selectedMarker = Marker(map).apply {
                        position = p
                        title = "Nuevo lugar"
                    }
                    map.overlays.add(selectedMarker)
                    map.invalidate()
                }
                return true
            }
        }
        map.overlays.add(MapEventsOverlay(mapEventsReceiver))
    }

    private fun loadPlacesFromDatabase() {
        lifecycleScope.launch {
            val places = db.placeDao().getAll()
            places.forEach { place ->
                val marker = Marker(map).apply {
                    position = GeoPoint(place.latitude, place.longitude)
                    title = place.name
                    subDescription = place.description
                    infoWindow = object : MarkerInfoWindow(R.layout.marker_info_window, map) {
                        override fun onOpen(item: Any?) {
                            val v = mView
                            v.findViewById<TextView>(R.id.tvTitle).text = place.name
                            v.findViewById<TextView>(R.id.tvDescription).text = place.description
                            val iv = v.findViewById<ImageView>(R.id.ivMarkerImage)
                            if (place.imageUri.isNotEmpty()) iv.setImageURI(Uri.parse(place.imageUri))
                            else iv.setImageResource(android.R.drawable.ic_menu_report_image)
                        }
                        override fun onClose() {}
                    }
                }
                marker.setOnMarkerClickListener { clickedMarker, _ ->
                    clickedMarker.showInfoWindow()
                    selectedMarker = clickedMarker
                    true
                }
                map.overlays.add(marker)
            }
            map.invalidate()
        }
    }

    private fun setupAddDeleteButtons() {
        findViewById<Button>(R.id.btnAddPlace).setOnClickListener {
            val intent = Intent(this, AddPlaceActivity::class.java)
            selectedMarker?.let { marker ->
                intent.putExtra("latitude", marker.position.latitude)
                intent.putExtra("longitude", marker.position.longitude)
                intent.putExtra("name", marker.title)
                intent.putExtra("description", marker.subDescription)
            }
            startActivity(intent)
        }

        findViewById<Button>(R.id.btnDeletePlace).setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                val places = db.placeDao().getAll()
                withContext(Dispatchers.Main) {
                    if (places.isNotEmpty()) {
                        val dialogView = layoutInflater.inflate(R.layout.dialog_delete_place, null)
                        val rvPlaces = dialogView.findViewById<RecyclerView>(R.id.rvPlaces)
                        rvPlaces.layoutManager = LinearLayoutManager(this@MainActivity)
                        val mutablePlaces = places.toMutableList()

                        val adapter = DeletePlaceAdapter(mutablePlaces) { place ->
                            mutablePlaces.remove(place)
                            deletePlace(place)
                            rvPlaces.adapter?.notifyDataSetChanged()
                        }
                        rvPlaces.adapter = adapter

                        AlertDialog.Builder(this@MainActivity)
                            .setView(dialogView)
                            .setNegativeButton("Cerrar") { dialog, _ -> dialog.dismiss() }
                            .show()
                    } else {
                        Toast.makeText(this@MainActivity, "No hay lugares agregados", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun deletePlace(place: Place) {
        CoroutineScope(Dispatchers.IO).launch {
            db.placeDao().delete(place)
            withContext(Dispatchers.Main) {
                Toast.makeText(this@MainActivity, "Lugar eliminado: ${place.name}", Toast.LENGTH_SHORT).show()
                map.overlays.removeAll { overlay -> overlay is Marker && overlay.title == place.name }
                map.invalidate()
            }
        }
    }

    // -------------------- SEARCH BAR --------------------

    private fun setupSearchBar() {
        val searchBar = findViewById<AutoCompleteTextView>(R.id.searchBar)

        val suggestionAdapter = ArrayAdapter<String>(
            this,
            android.R.layout.simple_dropdown_item_1line,
            mutableListOf()
        )
        searchBar.setAdapter(suggestionAdapter)
        searchBar.threshold = 1

        // Cada vez que se escribe algo en la barra
        searchBar.addTextChangedListener { editable ->
            val query = editable.toString()
            if (query.isNotEmpty()) {
                lifecycleScope.launch {
                    val suggestions = fetchOSMSuggestions(query).map { it.first } // nombres de lugares
                    withContext(Dispatchers.Main) {
                        suggestionAdapter.clear()
                        suggestionAdapter.addAll(suggestions)
                        suggestionAdapter.notifyDataSetChanged()
                        searchBar.showDropDown()
                    }
                }
            }
        }

        // Cuando el usuario selecciona una sugerencia
        searchBar.setOnItemClickListener { parent, _, position, _ ->
            val selectedName = parent.getItemAtPosition(position) as String
            lifecycleScope.launch {
                val selectedPoint = fetchOSMSuggestions(selectedName).firstOrNull()?.second
                if (selectedPoint != null) {
                    centerMapOnPoint(selectedPoint, selectedName)
                }
            }
        }


        // Cuando el usuario presiona Enter
        searchBar.setOnEditorActionListener { _, _, _ ->
            val query = searchBar.text.toString()
            if (query.isNotEmpty()) {
                lifecycleScope.launch {
                    val point = getPointForName(query)
                    if (point != null) centerMapOnPoint(point, query)
                    else Toast.makeText(this@MainActivity, "No se pudo localizar el lugar", Toast.LENGTH_SHORT).show()
                }
            }
            true
        }
    }

    // Centra el mapa y agrega marcador temporal
    private fun centerMapOnPoint(point: GeoPoint, title: String) {
        map.controller.setZoom(15.0)
        map.controller.setCenter(point)
        val tempMarker = Marker(map).apply {
            position = point
            this.title = title
        }
        map.overlays.add(tempMarker)
        map.invalidate()
    }

    // Devuelve un GeoPoint de un nombre, ya sea local o de OSM
    private suspend fun getPointForName(name: String): GeoPoint? {
        return getLocalPlacePoint(name) ?: getOSMPoint(name)
    }

    // Sugerencias dinámicas desde la base de datos local
    private suspend fun getLocalPlaceSuggestions(query: String): List<String> = withContext(Dispatchers.IO) {
        db.placeDao().getAll()
            .filter { it.name.contains(query, ignoreCase = true) || it.description.contains(query, ignoreCase = true) }
            .map { it.name }
    }

    // Sugerencias dinámicas desde Nominatim (ciudad, estado, país)
    private suspend fun getOSMSuggestions(query: String): List<String> = withContext(Dispatchers.IO) {
        try {
            val geocoder = GeocoderNominatim("MiApp")
            val addresses = geocoder.getFromLocationName(query, 10)
            addresses.mapNotNull { addr ->
                val components = listOf(addr.locality, addr.subAdminArea, addr.adminArea, addr.countryName)
                components.filterNotNull().joinToString(", ").takeIf { it.contains(query, ignoreCase = true) }
            }.distinct()
        } catch (e: Exception) { emptyList() }
    }

    // Convierte nombre a GeoPoint usando base de datos local
    private suspend fun getLocalPlacePoint(name: String): GeoPoint? = withContext(Dispatchers.IO) {
        db.placeDao().getAll().find { it.name.equals(name, ignoreCase = true) }
            ?.let { GeoPoint(it.latitude, it.longitude) }
    }

    // Convierte nombre a GeoPoint usando Nominatim
    private suspend fun getOSMPoint(name: String): GeoPoint? = withContext(Dispatchers.IO) {
        try {
            val geocoder = GeocoderNominatim("MiApp")
            val addresses = geocoder.getFromLocationName(name, 1)
            if (addresses.isNotEmpty()) GeoPoint(addresses[0].latitude, addresses[0].longitude)
            else null
        } catch (e: Exception) { null }
    }
    suspend fun fetchOSMSuggestions(query: String): List<Pair<String, GeoPoint>> = withContext(Dispatchers.IO) {
        try {
            val client = OkHttpClient()
            val url = "https://nominatim.openstreetmap.org/search?q=${query.replace(" ", "+")}&format=json&limit=5"
            val request = Request.Builder().url(url).header("User-Agent", "MiApp").build()
            val response = client.newCall(request).execute()
            val body = response.body?.string() ?: return@withContext emptyList()

            val jsonArray = JSONArray(body)
            val results = mutableListOf<Pair<String, GeoPoint>>()
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val name = obj.getString("display_name")
                val lat = obj.getDouble("lat")
                val lon = obj.getDouble("lon")
                results.add(name to GeoPoint(lat, lon))
            }
            results
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

}

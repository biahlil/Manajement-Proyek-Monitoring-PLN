package com.pln.monitoringpln.presentation.components

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker

@Composable
fun MapPicker(
    modifier: Modifier = Modifier,
    initialLocation: GeoPoint? = null,
    onLocationSelected: (Double, Double) -> Unit,
) {
    val context = LocalContext.current

    // Initialize OSMDroid configuration
    LaunchedEffect(Unit) {
        Configuration.getInstance().load(context, android.preference.PreferenceManager.getDefaultSharedPreferences(context))
    }

    val mapView = remember {
        MapView(context).apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
            controller.setZoom(15.0)
        }
    }

    // ... (rest of the code)

    AndroidView(
        factory = {
            mapView.apply {
                val startPoint = initialLocation ?: GeoPoint(-3.316694, 114.590111) // Default to Banjarmasin
                controller.setCenter(startPoint)

                // Add initial marker if location exists
                if (initialLocation != null) {
                    val marker = Marker(this)
                    marker.position = initialLocation
                    marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    overlays.add(marker)
                }

                // Add Tap Listener
                val mapEventsReceiver = object : MapEventsReceiver {
                    override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
                        p?.let { point ->
                            onLocationSelected(point.latitude, point.longitude)

                            // Update Marker
                            overlays.removeAll { it is Marker }
                            val marker = Marker(this@apply)
                            marker.position = point
                            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                            overlays.add(marker)
                            invalidate()
                        }
                        return true
                    }

                    override fun longPressHelper(p: GeoPoint?): Boolean {
                        return false
                    }
                }
                overlays.add(MapEventsOverlay(mapEventsReceiver))
            }
        },
        modifier = modifier,
    )
}

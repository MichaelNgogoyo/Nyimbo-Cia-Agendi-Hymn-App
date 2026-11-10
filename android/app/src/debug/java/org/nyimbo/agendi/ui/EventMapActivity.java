package org.nyimbo.agendi.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import org.nyimbo.agendi.BuildConfig;
import org.nyimbo.agendi.R;
import org.nyimbo.agendi.data.NyimboDatabase;
import org.nyimbo.agendi.model.EventEntity;
import java.util.List;

public class EventMapActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap map;
    private List<EventEntity> events;
    private TextView status;
    @Override protected void onCreate(@Nullable Bundle saved) {
        super.onCreate(saved); setContentView(R.layout.activity_map);
        SystemBarInsets.apply(this, findViewById(R.id.map_root));
        findViewById(R.id.map_back).setOnClickListener(v -> finish());
        status = findViewById(R.id.map_status);
        NyimboDatabase.get(this).events().all().observe(this, rows -> { events = rows; if (map != null) renderMarkers(); });
        if (BuildConfig.MAPS_API_KEY.trim().isEmpty()) {
            status.setText("Map preview needs a Google Maps API key. Event directions remain available from the Events list.");
            return;
        }
        status.setText("Tap an event marker for details and directions.");
        Fragment existing = getSupportFragmentManager().findFragmentByTag("event-map");
        SupportMapFragment mapFragment = existing instanceof SupportMapFragment ? (SupportMapFragment)existing : SupportMapFragment.newInstance();
        if (existing == null) getSupportFragmentManager().beginTransaction().replace(R.id.map_container, mapFragment, "event-map").commit();
        mapFragment.getMapAsync(this);
    }
    @Override public void onMapReady(@NonNull GoogleMap googleMap) { map = googleMap; map.getUiSettings().setMapToolbarEnabled(false); renderMarkers(); }
    private void renderMarkers() {
        if (map == null || events == null) return;
        map.clear(); LatLng first = null; int count = 0;
        for (EventEntity e : events) if (e.latitude != null && e.longitude != null) {
            LatLng point = new LatLng(e.latitude, e.longitude); if (first == null) first = point; count++;
            Marker marker = map.addMarker(new MarkerOptions().position(point).title((e.demo ? "DEMO · " : "") + e.title).snippet(e.eventDate + " · " + e.venue));
            if (marker != null) marker.setTag(e);
        }
        status.setText(count == 0 ? "No event has verified coordinates yet. Use Directions on an event card to search by venue." : count + " event location(s) shown");
        if (first != null) map.moveCamera(CameraUpdateFactory.newLatLngZoom(first, count == 1 ? 13f : 7f));
        map.setOnInfoWindowClickListener(marker -> { Object tag = marker.getTag(); if (tag instanceof EventEntity) directions((EventEntity)tag); });
    }
    private void directions(EventEntity e) {
        Uri uri = e.latitude != null && e.longitude != null ? Uri.parse("google.navigation:q=" + e.latitude + "," + e.longitude) : Uri.parse("geo:0,0?q=" + Uri.encode(e.venue == null ? e.title : e.venue));
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        if (e.latitude != null && e.longitude != null) intent.setPackage("com.google.android.apps.maps");
        if (intent.resolveActivity(getPackageManager()) == null) intent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=" + Uri.encode(e.venue == null ? e.title : e.venue)));
        try { startActivity(intent); } catch (Exception error) { Toast.makeText(this, "No maps application found.", Toast.LENGTH_SHORT).show(); }
    }
}

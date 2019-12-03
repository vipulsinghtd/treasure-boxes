package com.treasuredata.polestar;

import androidx.appcompat.app.AppCompatActivity;

import android.location.Location;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.treasuredata.android.TreasureData;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    final private static String TD_WRITE_KEY = "1/234567890abcdefghijklmnopqrstuvwxyz";
    final private static String TD_ENCRYPTION_KEY = "1234567890";
    final private static String NAO_SERVICE_API_KEY = "emulator";

    final private static LatLng MAP_CENTER_POSITION = new LatLng(37.4187416, -122.0999732);
    final private static boolean MAP_CAMERA_FIXED = true;
    final private static float MAP_ZOOM = 18.0f;

    private TreasureData td;
    private NaoLocationClient locationClient;
    private NaoGeofencingClient geofencingClient;

    private GoogleMap map;
    private Marker marker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initTreasureData();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        initNao();

        locationClient.startService();
        geofencingClient.startService();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        Marker marker = map.addMarker(new MarkerOptions()
                .position(MAP_CENTER_POSITION)
                .title("Marker"));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(MAP_CENTER_POSITION, MAP_ZOOM));
        this.map = map;
        this.marker = marker;
    }

    public void onChange(Location location) {
        LatLng position = new LatLng(location.getLatitude(), location.getLongitude());
        marker.setPosition(position);
        if (!MAP_CAMERA_FIXED) {
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(position, MAP_ZOOM));
        }
    }

    private void initTreasureData() {
        // @see https://docs.treasuredata.com/articles/android-sdk
        TreasureData.initializeApiEndpoint("https://in.treasuredata.com");
        TreasureData.initializeEncryptionKey(TD_ENCRYPTION_KEY);
        TreasureData.disableLogging();
        TreasureData.initializeSharedInstance(this, TD_WRITE_KEY);

        TreasureData td = TreasureData.sharedInstance();

        td.setDefaultDatabase("polestar");
        td.enableAutoAppendUniqId();
        td.enableAutoAppendModelInformation();
        td.enableAutoAppendAppInformation();
        td.enableAutoAppendLocaleInformation();

        this.td = td;
    }

    private void initNao() {
        NaoLocationClient locationClient = new NaoLocationClient();
        locationClient.setContext(this);
        locationClient.setNaoServiceApiKey(NAO_SERVICE_API_KEY);
        this.locationClient = locationClient;

        NaoGeofencingClient geofencingClient = new NaoGeofencingClient();
        geofencingClient.setContext(this);
        geofencingClient.setNaoServiceApiKey(NAO_SERVICE_API_KEY);
        this.geofencingClient = geofencingClient;
    }
}

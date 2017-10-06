package marcio.com.br.paradacertaprojeto;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        SQLiteDatabase bancoDados;

        try {
            bancoDados = openOrCreateDatabase("app", MODE_PRIVATE, null);


            Bundle bundle = getIntent().getExtras();
            String idLinha = bundle.getString("idLinha");



            Cursor cursor = bancoDados.rawQuery("SELECT * FROM coordenadas where idlinha =" + idLinha, null);

            int indiceColunaLatitude = cursor.getColumnIndex("latitude");
            int indiceColunaLongitude = cursor.getColumnIndex("longitude");
            int indiceColunaIdCoordenada = cursor.getColumnIndex("idcoordenada");

            PolylineOptions lineOptions = null;

            lineOptions = new PolylineOptions();
            cursor.moveToFirst();
            while (cursor != null) {

                double latitude = Double.parseDouble(cursor.getString(indiceColunaLatitude));
                double longitude = Double.parseDouble(cursor.getString(indiceColunaLongitude));
                String idcoordenada =cursor.getString(indiceColunaIdCoordenada);



                        lineOptions.add(new LatLng(latitude, longitude));
                        Polyline polyline1 = mMap.addPolyline(lineOptions);
                LatLng parada = new LatLng(latitude, longitude);
                mMap.addMarker(new MarkerOptions().position(parada).title(idcoordenada));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(parada));
                mMap.getUiSettings().setZoomControlsEnabled(true);
                float zoomnivel = 14.0f;
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(parada,zoomnivel));



                //Log.i("LogX", "latitude: " + cursor.getString(indiceColunaLatitude) + " longitude: " + cursor.getString(indiceColunaLongitude));
                cursor.moveToNext();

            }
            //botoes de zoom
            MarkerOptions marker = new MarkerOptions();
            mMap.addMarker(marker);

        } catch (Exception e) {
            e.printStackTrace();
        }



    }
}

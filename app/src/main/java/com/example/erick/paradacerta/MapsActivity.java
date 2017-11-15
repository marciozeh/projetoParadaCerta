package com.example.erick.paradacerta;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = MapsActivity.class.getSimpleName();
    private GoogleMap mMap;
    private static CameraPosition mCameraPosition;

        // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient mFusedLocationProviderClient;

    // A default location (Sydney, Australia) and default zoom to use when location permission is
    // not granted.
    private final LatLng mDefaultLocation = new LatLng(-30.0277, -51.2287);
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;

    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private Location mLastKnownLocation;

    // Keys for storing activity state.
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";

    //barra e botao de procurar
    Button mBtnFind;
    EditText etPlace;


    private ListView listaLinhas;
    private ArrayAdapter<String> itensAdaptador;
    private ArrayList<String> codigo;
    private ArrayList<String> nome;
    private ArrayList<String> idlinha;
    private ArrayList<String> resultado;
    static ArrayList<String> linhas;

    SQLiteDatabase bancoDados;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Retrieve location and camera position from saved instance state.
        if (savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }

        // Retrieve the content view that renders the map.
        setContentView(R.layout.activity_maps);


        // Construct a FusedLocationProviderClient.
       mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Build the map.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Getting reference to the find button
        mBtnFind = (Button) findViewById(R.id.btn_show);

        // Getting reference to EditText
        etPlace = (EditText) findViewById(R.id.et_place);

        // Setting click event listener for the find button
        mBtnFind.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Getting the place entered
                String location = etPlace.getText().toString();

                if(location==null || location.equals("")){
                    Toast.makeText(getBaseContext(), "No Place is entered", Toast.LENGTH_SHORT).show();
                    return;
                }

                String url = "https://maps.googleapis.com/maps/api/geocode/json?";

                try {
                    // encoding special characters like space in the user input place
                    location = URLEncoder.encode(location, "utf-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                String address = "address=" + location;

                String sensor = "sensor=false";

                // url , from where the geocoding data is fetched
                url = url + address + "&" + sensor;

                // Instantiating DownloadTask to get places from Google Geocoding service
                // in a non-ui thread
                DownloadTask downloadTask = new DownloadTask();

                // Start downloading the geocoding places
                downloadTask.execute(url);
            }
        });
        //listaLinhas = (ListView) findViewById(R.id.listviewid);
        //carregaLinhas();
    }

    /**
     * Saves the state of the map when the activity is paused.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, mMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, mLastKnownLocation);
            super.onSaveInstanceState(outState);
        }
    }

    /**
     * Manipulates the map when it's available.
     * This callback is triggered when the map is ready to be used.
     */
    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;

        // Prompt the user for permission.
        getLocationPermission();

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();

        // Get the current location of the device and set the position of the map.
        getDeviceLocation();

    }



    /**
     * Gets the current location of the device, and positions the map's camera.
     */
    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation = task.getResult();
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(mLastKnownLocation.getLatitude(),
                                            mLastKnownLocation.getLongitude()), 14.0f));

                            //Log.i(null,"pegando localizacao");
                            mostraparadas(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());


                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            mMap.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }


    /**
     * Prompts the user for permission to use the device location.
     */
    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    /**
     * Handles the result of the request for location permissions.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }


    /**
     * Updates the map's UI settings based on whether the user has granted location permission.
     */
    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void mostraLinhas() {
        try {

            String texto = "nada";
            //Log.i("Mostra Linha",texto);
            bancoDados = openOrCreateDatabase("appbanco.sqlite", MODE_PRIVATE, null);
            Cursor cursor = bancoDados.rawQuery("SELECT * FROM coordenadas where idlinha = 125", null);

            int indiceColunaLatitude = cursor.getColumnIndex("latitude");
            int indiceColunaLongitude = cursor.getColumnIndex("longitude");


            PolylineOptions lineOptions = null;

            lineOptions = new PolylineOptions();
            cursor.moveToFirst();
            while (cursor != null) {

                double latitude = Double.parseDouble(cursor.getString(indiceColunaLatitude));
                double longitude = Double.parseDouble(cursor.getString(indiceColunaLongitude));


                lineOptions.add(new LatLng(latitude, longitude));
                Polyline polyline1 = mMap.addPolyline(lineOptions);
                LatLng parada = new LatLng(latitude, longitude);
                mMap.addMarker(new MarkerOptions().position(parada).title("Parada x"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(parada));
                mMap.getUiSettings().setZoomControlsEnabled(true);
                float zoomnivel = 14.0f;
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(parada, zoomnivel));


                //Log.i("LogX", "latitude: " + cursor.getString(indiceColunaLatitude) + " longitude: " + cursor.getString(indiceColunaLongitude));
                cursor.moveToNext();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void mostraparadas(double latiAtual, double longiAtual) {

        try {
            bancoDados = openOrCreateDatabase("appbanco.sqlite", MODE_PRIVATE, null);

            Cursor cursor = bancoDados.rawQuery("SELECT * FROM coordenadas", null);
            cursor.moveToFirst();
            while (cursor != null) {

                int indiceColunaLatitude = cursor.getColumnIndex("latitude");
                int indiceColunaLongitude = cursor.getColumnIndex("longitude");
                int indiceColunaIdLinha = cursor.getColumnIndex("idlinha");

                double latiParada = Double.parseDouble(cursor.getString(indiceColunaLatitude));
                double longiParada = Double.parseDouble(cursor.getString(indiceColunaLongitude));
                int idLinha = Integer.parseInt(cursor.getString(indiceColunaIdLinha));


                if(distancia(latiAtual,longiAtual, latiParada, longiParada) <= 500) {
                    LatLng parada = new LatLng(latiParada, longiParada);
                    LatLng userLoc = new LatLng(latiAtual,longiAtual);
                    mMap.addMarker(new MarkerOptions().position(parada).title(Integer.toString(idLinha)));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(parada));
                    mMap.getUiSettings().setZoomControlsEnabled(true);
                    float zoomnivel = 14.0f;
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLoc, zoomnivel));
                }

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
        // calculo da distancia separado da função de marcadores de paradas próximas.
        private double distancia(double latiAtual, double longiAtual, double latiParada, double longiParada){
            double R = 6371e3; // metres
            double l1 = Math.toRadians(latiAtual);
            double l2 = Math.toRadians(latiParada);
            double del1 = Math.toRadians(latiParada - latiAtual);
            double del2 = Math.toRadians(longiParada - longiAtual);

            double a = Math.sin(del1/2) * Math.sin(del1/2) +
                    Math.cos(l1) * Math.cos(l2) *
                            Math.sin(del2/2) * Math.sin(del2/2);
            double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

            double d = R * c;
            return d;
        }

    //Carrega a lista de linhas disponíveis, nela será possível escolher a linha necessária para carregar as paradas a seguir.
    private void carregaLinhas() {

        try {

            bancoDados = openOrCreateDatabase("appbanco.sqlite", MODE_PRIVATE, null);

            Cursor cursor = bancoDados.rawQuery("SELECT * FROM linhas", null);

            int indiceColunaCodigo = cursor.getColumnIndex("codigo");
            int indiceColunaNome = cursor.getColumnIndex("nome");
            int indiceColunaId = cursor.getColumnIndex("idlinha");

            codigo = new ArrayList<String>();
            nome = new ArrayList<String>();
            idlinha = new ArrayList<String>();
            resultado = new ArrayList<String>();

            itensAdaptador = new ArrayAdapter<String>(getApplicationContext(),
                    android.R.layout.simple_list_item_1,
                    android.R.id.text1,
                    resultado);

            listaLinhas.setAdapter(itensAdaptador);

            linhas = new ArrayList<>();
            linhas.add("linha");

            listaLinhas.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String linhaid = idlinha.get(position);
                    Log.i("IDLinha", linhaid);
                    //carregaParadas(idlinha.get(position));

                    Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                    intent.putExtra("idLinha", linhaid);
                    startActivity(intent);
                }
            });

            cursor.moveToFirst();
            while (cursor != null) {

                codigo.add(cursor.getString(indiceColunaCodigo));
                nome.add(cursor.getString(indiceColunaNome));
                idlinha.add(cursor.getString(indiceColunaId));
                resultado.add(cursor.getString(indiceColunaCodigo) + " " + cursor.getString(indiceColunaNome));

                //Log.i("LogX","Código: " + cursor.getString(indiceColunaCodigo) + " Linha: " +cursor.getString(indiceColunaNome));
                cursor.moveToNext();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // carregará o mapa com as paradas carregadas.
    private void carregaParadas(String idLinha) {
        try {
            bancoDados = openOrCreateDatabase("app", MODE_PRIVATE, null);

            Cursor cursor = bancoDados.rawQuery("SELECT * FROM coordenadas where idlinha =" + idLinha, null);
            cursor.moveToFirst();
            while (cursor != null) {

                int indiceColunaLatitude = cursor.getColumnIndex("latitude");
                int indiceColunaLongitude = cursor.getColumnIndex("longitude");


                Log.i("LogX", "latitude: " + cursor.getString(indiceColunaLatitude) + " longitude: " + cursor.getString(indiceColunaLongitude));
                cursor.moveToNext();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // metodos da conversão de endereço para coordenadas
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);
            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while( ( line = br.readLine()) != null){
                sb.append(line);
            }

            data = sb.toString();
            br.close();

        }catch(Exception e){
            Log.d("Exception dl url", e.toString());
        }finally{
            iStream.close();
            urlConnection.disconnect();
        }

        return data;
    }
    /** A class, to download Places from Geocoding webservice */
    private class DownloadTask extends AsyncTask<String, Integer, String> {

        String data = null;

        // Invoked by execute() method of this object
        @Override
        protected String doInBackground(String... url) {
            try{
                data = downloadUrl(url[0]);
            }catch(Exception e){
                Log.d("Background Task",e.toString());
            }
            return data;
        }

        // Executed after the complete execution of doInBackground() method
        @Override
        protected void onPostExecute(String result){

            // Instantiating ParserTask which parses the json data from Geocoding webservice
            // in a non-ui thread
            ParserTask parserTask = new ParserTask();

            // Start parsing the places in JSON format
            // Invokes the "doInBackground()" method of the class ParseTask
            parserTask.execute(result);
        }
    }

    /** A class to parse the Geocoding Places in non-ui thread */
    class ParserTask extends AsyncTask<String, Integer, List<HashMap<String,String>>>{

        JSONObject jObject;

        // Invoked by execute() method of this object
        @Override
        protected List<HashMap<String,String>> doInBackground(String... jsonData) {

            List<HashMap<String, String>> places = null;
            GeocodeJSONParser parser = new GeocodeJSONParser();

            try{
                jObject = new JSONObject(jsonData[0]);

                /** Getting the parsed data as a an ArrayList */
                places = parser.parse(jObject);

            }catch(Exception e){
                Log.d("Exception",e.toString());
            }
            return places;
        }

        // Executed after the complete execution of doInBackground() method
        @Override
        protected void onPostExecute(List<HashMap<String,String>> list){

            // Clears all the existing markers
            mMap.clear();

            for(int i=0;i<list.size();i++){

                // Creating a marker
                MarkerOptions markerOptions = new MarkerOptions();

                // Getting a place from the places list
                HashMap<String, String> hmPlace = list.get(i);

                // Getting latitude of the place
                double lat = Double.parseDouble(hmPlace.get("lat"));

                // Getting longitude of the place
                double lng = Double.parseDouble(hmPlace.get("lng"));

                // Getting name
                String name = hmPlace.get("formatted_address");

                LatLng latLng = new LatLng(lat, lng);

                // Setting the position for the marker e muda a cor do marcador do destino
                markerOptions.position(latLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));

                // Setting the title for the marker
                markerOptions.title(name);

                // Placing a marker on the touched position
                mMap.addMarker(markerOptions);

                //mostrando paradas proximas ao endereco pedido
                mostraparadas(lat, lng);

                // Locate the first location
                if(i==0)
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
            }
        }
    }



}

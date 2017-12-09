package com.example.erick.paradacerta;

import android.app.Dialog;
import android.content.Context;
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
import android.view.LayoutInflater;
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

public class MapsActivity extends AppCompatActivity
        implements OnMapReadyCallback {

    private static final String TAG = MapsActivity.class.getSimpleName();
    private GoogleMap mMap;
    private static CameraPosition mCameraPosition;

    private ArrayList<String> paradaGPS;
    private ArrayList<String> paradaDestino;
    private ArrayList<String> listaLinhas;
    private ListView ListaLinhas;
    private ArrayAdapter<String> itensAdaptador1;
    static ArrayList<String> linhas1;
    ArrayList<String> resultado;
    String linhaString = null;
    double latitudeUser = 0;
    double longitudeUser = 0;

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

    Button BtnLista;
    Button BtnCadastro;

    SQLiteDatabase bancoDados;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }

        // Retrieve the content view that renders the map.
        setContentView(R.layout.activity_maps);

        BtnCadastro = (Button) findViewById(R.id.BtnCadastro);

        BtnCadastro.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(MapsActivity.this, CadastroActivity.class);
                startActivity(i);
            }
        });

        BtnLista = (Button) findViewById(R.id.BtnLista);

        BtnLista.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(MapsActivity.this, ListaActivity.class);
                startActivity(i);
            }
        });

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

//         Setting click event listener for the find button
        mBtnFind.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Getting the place entered
                String location = etPlace.getText().toString();

                if(location==null || location.equals("")){
                    Toast.makeText(getBaseContext(), "Nenhum endereço preenchido", Toast.LENGTH_SHORT).show();
                    return;
                }

                String url = "https://maps.googleapis.com/maps/api/geocode/json?";

                try {
                    // encoding special characters like space in the user input place
                    location = URLEncoder.encode(location, "utf-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                String address = "address="+"Porto Alegre" + location;

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

    }

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

        //recebe o parametro do listaactivity e carrega a rota da linha
        Bundle bundle = getIntent().getExtras();
        String idLinha = null;
        idLinha = bundle.getString("idLinha");

//         Prompt the user for permission. pede permissao ao usuario
        getLocationPermission();

//         Turn on the My Location layer and the related control on the map. faz update da localização
        updateLocationUI();

//         Get the current location of the device and set the position of the map. pega a localização
        getDeviceLocation(idLinha);


    }

    /**
     * Gets the current location of the device, and positions the map's camera.
     */
    private void getDeviceLocation(final String idLinha) {
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

                            if(idLinha==null) {
                                paradasGPS(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
                            }else{
                                pegaPosicao(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude(), idLinha);
                            }

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

    private void paradasGPS(double latiAtual, double longiAtual) {

        try {

            paradaGPS = new ArrayList<String>();

            bancoDados = openOrCreateDatabase("appbanco.sqlite", MODE_PRIVATE, null);

            Cursor cursor = bancoDados.rawQuery("SELECT * FROM coordenadas", null);
            cursor.moveToFirst();
            while (cursor != null) {

                int indiceColunaLatitude = cursor.getColumnIndex("latitude");
                int indiceColunaLongitude = cursor.getColumnIndex("longitude");
//                int indiceColunaIdLinha = cursor.getColumnIndex("idlinha");
                int indiceColunaNome = cursor.getColumnIndex("codigoNome");

                double latiParada = Double.parseDouble(cursor.getString(indiceColunaLatitude));
                double longiParada = Double.parseDouble(cursor.getString(indiceColunaLongitude));
//                int idLinha = Integer.parseInt(cursor.getString(indiceColunaIdLinha));
                String nome = (cursor.getString(indiceColunaNome));

                if(distancia(latiAtual,longiAtual, latiParada, longiParada) <= 500) {

                    LatLng parada = new LatLng(latiParada, longiParada);
                    LatLng userLoc = new LatLng(latiAtual,longiAtual);
                    mMap.addMarker(new MarkerOptions().position(parada).title(nome));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(parada));
                    mMap.getUiSettings().setZoomControlsEnabled(true);
                    float zoomnivel = 14.0f;
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLoc, zoomnivel));

                    paradaGPS.add(nome);
                }

                //Log.i("LogX", "latitude: " + cursor.getString(indiceColunaLatitude) + " longitude: " + cursor.getString(indiceColunaLongitude));
                cursor.moveToNext();
            }
            //botoes de zoom'
            MarkerOptions marker = new MarkerOptions();
            mMap.addMarker(marker);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void paradasDestino(double latiAtual, double longiAtual) {
        try {
            paradaDestino = new ArrayList<String>();
            bancoDados = openOrCreateDatabase("appbanco.sqlite", MODE_PRIVATE, null);

            Cursor cursor = bancoDados.rawQuery("SELECT * FROM coordenadas", null);
            cursor.moveToFirst();
            while (cursor != null) {

                int indiceColunaLatitude = cursor.getColumnIndex("latitude");
                int indiceColunaLongitude = cursor.getColumnIndex("longitude");

                int indiceColunaNome = cursor.getColumnIndex("codigoNome");

                double latiParada = Double.parseDouble(cursor.getString(indiceColunaLatitude));
                double longiParada = Double.parseDouble(cursor.getString(indiceColunaLongitude));
                String nome = (cursor.getString(indiceColunaNome));


                if(distancia(latiAtual,longiAtual, latiParada, longiParada) <= 500) {

                    LatLng parada = new LatLng(latiParada, longiParada);
                    LatLng userLoc = new LatLng(latiAtual,longiAtual);
                    mMap.addMarker(new MarkerOptions().position(parada).title(nome));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(parada));
                    mMap.getUiSettings().setZoomControlsEnabled(true);
                    float zoomnivel = 14.0f;
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLoc, zoomnivel));

                    paradaDestino.add(nome);
                }

                //Log.i("LogX", "latitude: " + cursor.getString(indiceColunaLatitude) + " longitude: " + cursor.getString(indiceColunaLongitude));
                cursor.moveToNext();
            }

            //botoes de zoom'
            MarkerOptions marker = new MarkerOptions();
            mMap.addMarker(marker);


        } catch (Exception e) {
            e.printStackTrace();
        }

        //abre um listview com as linhas em comum.
        listaLinhas();
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

    //metodo que compara as linhas do usuario e do destino
    private void listaLinhas(){

        listaLinhas = new ArrayList<String>();
        for(String n : paradaGPS) {
            if (paradaDestino.contains(n)) {
                if(!listaLinhas.contains(n)) {
                    listaLinhas.add(n);
                }
            }
        }


        final Dialog listDialog;
        listDialog = new Dialog(this);
        listDialog.setTitle("Select Item");
        final LayoutInflater li = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = li.inflate(R.layout.activity_lista, null, false);
        listDialog.setContentView(v);
        listDialog.setCancelable(true);
        //there are a lot of settings, for dialog, check them all out!

        ListView list1 = (ListView) listDialog.findViewById(R.id.listviewid);
        list1.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, listaLinhas));
        listDialog.show();
        list1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String linhaid = listaLinhas.get(position);
                getDeviceLocation(linhaid);
                listDialog.dismiss();
            }
        });
    }


    private void pegaPosicao(double latiAtual, double longiAtual,String idLinha){
        double lagiU = latiAtual;
        double logiU = longiAtual;
        proximaGPS(lagiU,logiU,idLinha);
    }

    //metodo que acha a parada mais próxima da linha selecionada do usuario
    private void proximaGPS(double latiAtual, double longiAtual, String idLinha){

        double distanciaMinima = 500;
        double distanciaMinima2 = 500;
        double latiProx = 0;
        double longProx = 0;
        double latiProxD = 0;
        double longProxD = 0;
        String nomelinha = null;

        try {
            bancoDados = openOrCreateDatabase("appbanco.sqlite", MODE_PRIVATE, null);


            Cursor cursor = bancoDados.rawQuery("SELECT * FROM coordenadas where codigoNome like'%"+idLinha+"%'",null);
            cursor.moveToFirst();


            int indiceColunaLatitude = cursor.getColumnIndex("latitude");
            int indiceColunaLongitude = cursor.getColumnIndex("longitude");
            int indiceColunaNome = cursor.getColumnIndex("codigoNome");

            cursor.moveToFirst();
            while (cursor != null) {

                double latiParada = Double.parseDouble(cursor.getString(indiceColunaLatitude));
                double longiParada = Double.parseDouble(cursor.getString(indiceColunaLongitude));
                String nome = (cursor.getString(indiceColunaNome));

                if (distancia(latiAtual, longiAtual, latiParada, longiParada) < distanciaMinima) {
                    latiProx = latiParada;
                    longProx = longiParada;
                    nomelinha = nome;
                    distanciaMinima = distancia(latiAtual, longiAtual, latiParada, longiParada);
                }

                if (distancia(latitudeUser, longitudeUser, latiParada, longiParada) < distanciaMinima2) {
                    latiProxD = latiParada;
                    longProxD = longiParada;
                    nomelinha = nome;
                    distanciaMinima2 = distancia(latitudeUser, longitudeUser, latiParada, longiParada);
                }

                cursor.moveToNext();

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        rotaLinha(latiProx, longProx, latiProxD, longProxD, nomelinha);
    }


    private void printaParada(Double lati, double longi, String nome){



        LatLng parada = new LatLng(lati, longi);

        //printa a parada mais próxima, para teste.
        mMap.addMarker(new MarkerOptions().position(parada).title(nome));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(parada));
        mMap.getUiSettings().setZoomControlsEnabled(true);
        float zoomnivel = 14.0f;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(parada, zoomnivel));
    }
    // carregará o mapa com as paradas carregadas.
    private void rotaLinha(double lati, double longi, double latiD, double longiD, String idLinha) {
        int flag = 0;
        int flag1 = 0;
        try {
            bancoDados = openOrCreateDatabase("appbanco.sqlite", MODE_PRIVATE, null);
            Cursor cursor = bancoDados.rawQuery("SELECT * FROM coordenadas where codigoNome like'%"+idLinha+"%'",null);
            cursor.moveToFirst();


            int indiceColunaLatitude = cursor.getColumnIndex("latitude");
            int indiceColunaLongitude = cursor.getColumnIndex("longitude");
            int indiceColunaNome = cursor.getColumnIndex("codigoNome");

            PolylineOptions lineOptions = null;

            lineOptions = new PolylineOptions();
            cursor.moveToFirst();
            while (cursor != null) {

                double latitude = Double.parseDouble(cursor.getString(indiceColunaLatitude));
                double longitude = Double.parseDouble(cursor.getString(indiceColunaLongitude));
                String nome = (cursor.getString(indiceColunaNome));

                //começa a printar a rota
                if ((latiD == latitude && longiD == longitude) && flag ==0 ){
                    flag = 1;
                }
                if ((lati == latitude && longi == longitude) && flag ==0 ){
                    flag = 1;
                }

                if (flag == 1){

                    //printa a rota
                    lineOptions.add(new LatLng(latitude, longitude));
                    Polyline polyline1 = mMap.addPolyline(lineOptions);
                    LatLng parada = new LatLng(latitude, longitude);

                    //printa as paradas
                    mMap.addMarker(new MarkerOptions().position(parada).title(nome));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(parada));
                    mMap.getUiSettings().setZoomControlsEnabled(true);
                    float zoomnivel = 14.0f;
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(parada, zoomnivel));
                }
                //para de printar a rota.
                if ((lati == latitude && longi == longitude) && flag ==1 ){
                    flag = 0;
                }
                if ((latiD == latitude && longiD == longitude) && flag ==1 ){
                    flag = 0;
                }

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
                paradasDestino(lat, lng);
                latitudeUser = lat;
                longitudeUser = lng;

                // Locate the first location
                if(i==0)
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
            }
        }
    }
}


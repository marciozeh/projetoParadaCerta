package com.example.erick.paradacerta;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

public class MapsRotaActivity extends AppCompatActivity
        implements OnMapReadyCallback {

    private static final String TAG = MapsRotaActivity.class.getSimpleName();
    private GoogleMap mMap;

    Button BtnLista;
    Button BtnCadastro;

    SQLiteDatabase bancoDados;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // Retrieve the content view that renders the map.
        setContentView(R.layout.activity_maps);

        BtnCadastro = (Button) findViewById(R.id.BtnCadastro);

        BtnCadastro.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(MapsRotaActivity.this, CadastroActivity.class);
                startActivity(i);
            }
        });

        BtnLista = (Button) findViewById(R.id.BtnLista);

        BtnLista.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(MapsRotaActivity.this, ListaActivity.class);
                startActivity(i);
            }
        });
        // Build the map.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);



  }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mMap != null) {
           super.onSaveInstanceState(outState);
        }
    }
    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;

        //recebe o parametro do listaactivity e carrega a rota da linha
        Bundle bundle = getIntent().getExtras();
        String idLinha = bundle.getString("idLinha");

        rotaLinha(idLinha);

    }

    // carregará o mapa com as paradas carregadas.
    private void rotaLinha(String idLinha) {
        try {
            bancoDados = openOrCreateDatabase("appbanco.sqlite", MODE_PRIVATE, null);

            Cursor cursor = bancoDados.rawQuery("SELECT * FROM coordenadas where idlinha =" + idLinha, null);
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
                cursor.moveToNext();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


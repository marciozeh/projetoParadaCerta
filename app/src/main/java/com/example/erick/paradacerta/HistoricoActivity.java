package com.example.erick.paradacerta;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class HistoricoActivity extends AppCompatActivity {

    private ListView listaLinhas;
    private ArrayList<String> idlinha;
    private ArrayAdapter<String> itensAdaptador;

    Button BtnMaps;
    Button BtnCadastro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historico);

        BtnCadastro = (Button) findViewById(R.id.BtnCadastro);

        BtnCadastro.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(HistoricoActivity.this, CadastroActivity.class);
                startActivity(i);
            }
        });

        BtnMaps = (Button) findViewById(R.id.BtnMaps);

        BtnMaps.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(HistoricoActivity.this, MapsActivity.class);
                startActivity(i);
            }
        });

        Bundle bundle = getIntent().getExtras();
        String linhaid = null;
        linhaid = bundle.getString("id");

        if(linhaid != null){
            Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
            intent.putExtra("idLinha", linhaid);
            startActivity(intent);
        }else {

            itensAdaptador = new ArrayAdapter<String>(getApplicationContext(),
                    android.R.layout.simple_list_item_1,
                    android.R.id.text1,
                    idlinha);

            listaLinhas = (ListView) findViewById(R.id.listviewid);
            listaLinhas.setAdapter(itensAdaptador);
        }

    }
}

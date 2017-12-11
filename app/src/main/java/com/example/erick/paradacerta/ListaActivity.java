package com.example.erick.paradacerta;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;


public class ListaActivity extends AppCompatActivity {

    private ListView listaLinhas;


    private ArrayAdapter<String> itensAdaptador;
    private ArrayList<String> codigo;
    private ArrayList<String> nome;
    private ArrayList<String> idlinha;
    private ArrayList<String> resultado;
    static ArrayList<String> linhas;

    Button BtnMaps;
    Button BtnCadastro;

    SQLiteDatabase bancoDados;


    Button mBtnFind;
    EditText etPlace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista);
        BtnCadastro = (Button) findViewById(R.id.BtnCadastro);

        BtnCadastro.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(ListaActivity.this, CadastroActivity.class);
                startActivity(i);
            }
        });

        BtnMaps = (Button) findViewById(R.id.BtnMaps);

        BtnMaps.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(ListaActivity.this, MapsActivity.class);
                startActivity(i);
            }
        });

        //recebe o parametro do listaactivity e carrega a rota da linha
        Bundle bundle = getIntent().getExtras();
        String linhaid = null;
        linhaid = bundle.getString("idLinha");

        if(linhaid != null){
            Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
            intent.putExtra("idLinha", linhaid);
            startActivity(intent);
        }else {
            listaLinhas = (ListView) findViewById(R.id.listviewid);
            carregaLinhas();
        }

        // Getting reference to the find button
        mBtnFind = (Button) findViewById(R.id.btn_show);

        // Getting reference to EditText
        etPlace = (EditText) findViewById(R.id.et_place);

//         Setting click event listener for the find button
        mBtnFind.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Getting the place entered

                try {
                String location = etPlace.getText().toString();

                if(location==null || location.equals("")){
                    Toast.makeText(getBaseContext(), "Nenhum endereço preenchido", Toast.LENGTH_SHORT).show();
                    return;
                }

                bancoDados = openOrCreateDatabase("appbanco.sqlite", MODE_PRIVATE, null);

                Cursor cursor = bancoDados.rawQuery("SELECT distinct codigoNome FROM coordenadas where codigoNome like '%" + location +"%'", null);

                int indiceColunaNome = cursor.getColumnIndex("codigoNome");

                resultado = new ArrayList<String>();

                itensAdaptador = new ArrayAdapter<String>(getApplicationContext(),
                        android.R.layout.simple_list_item_1,
                        android.R.id.text1,
                        resultado);

                listaLinhas = (ListView) findViewById(R.id.listviewid);
                listaLinhas.setAdapter(itensAdaptador);

                cursor.moveToNext();
                while (cursor != null) {

                    resultado.add(cursor.getString(indiceColunaNome));

                    //Log.i("LogX","Código: " + cursor.getString(indiceColunaCodigo) + " Linha: " +cursor.getString(indiceColunaNome));
                    cursor.moveToNext();
                }
                } catch (Exception e) {
                e.printStackTrace();
            }
            }
        });

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
                    //Log.i("IDLinha", linhaid);
                    //carregaParadas(idlinha.get(position));

                    Intent intent = new Intent(getApplicationContext(), MapsRotaActivity.class);
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
}

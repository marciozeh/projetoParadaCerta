package marcio.com.br.paradacertaprojeto;

import android.Manifest;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {


    static ArrayList<String> linhas;
    static ArrayAdapter arrayAdapter;
    public static String linhaid;


    private ListView listaLinhas;
    private ArrayAdapter<String> itensAdaptador;
    private ArrayList<String> codigo;
    private ArrayList<String> nome;
    private ArrayList<String> idlinha;


    //problema com a permissão, não funcinou comforme o tutorial

    String[] permissoes = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
    };

    SQLiteDatabase bancoDados;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //chama as permissoes, mas não funcionou!
        PermissionUtils.validate(this, 0, permissoes);

        listaLinhas = (ListView) findViewById(R.id.listviewid);
        carregaLinhas();


    }


    //Carrega a lista de linhas disponíveis, nela será possível escolher a linha necessária para carregar as paradas a seguir.
    private void carregaLinhas() {

        try {
            bancoDados = openOrCreateDatabase("app", MODE_PRIVATE, null);

            Cursor cursor = bancoDados.rawQuery("SELECT * FROM linhas", null);

            int indiceColunaCodigo = cursor.getColumnIndex("codigo");
            int indiceColunaNome = cursor.getColumnIndex("nome");
            int indiceColunaId = cursor.getColumnIndex("idlinha");

            codigo = new ArrayList<String>();
            nome = new ArrayList<String>();
            idlinha = new ArrayList<String>();

            itensAdaptador = new ArrayAdapter<String>(getApplicationContext(),
                    android.R.layout.simple_list_item_1,
                    android.R.id.text1,
                    nome);

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
                    intent.putExtra("idLinha",linhaid);
                    startActivity(intent);
                }
            });


            cursor.moveToFirst();
            while (cursor != null) {

                codigo.add(cursor.getString(indiceColunaCodigo));
                nome.add(cursor.getString(indiceColunaNome));
                idlinha.add(cursor.getString(indiceColunaId));
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


        /*
        try{
            //tabeta das linhas
            AssetManager assetManager = getResources().getAssets();
            InputStream inputStream = assetManager.open("newlinhas.csv");
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String linha;
            LinkedList<String> linhas = new LinkedList<String>();

            // banco abrir
            SQLiteDatabase bancoDados = openOrCreateDatabase("app", MODE_PRIVATE, null);

            bancoDados.execSQL("CREATE TABLE IF NOT EXISTS linhas (idlinha INT(5), nome VARCHAR (50), codigo VARCHAR(10), tipo VARCHAR(5))");

            String tabela ="linhas";
            String colunas ="idlinha, nome, codigo, tipo";
            String str1 = "INSERT INTO " + tabela + " (" + colunas + ") values(";
            String str2 = ");";

            while((linha = bufferedReader.readLine())!=null){
                //Imprime linha
                //Log.i("Print: ", paradas);

                StringBuilder sb = new StringBuilder(str1);
                String[] str = linha.split(";");
                sb.append(str[0] +"," );
                sb.append("'" + str[1] +"',");
                sb.append("'" + str[2] +"'," );
                sb.append("'" + str[3] +"'" );
                sb.append(str2);

                bancoDados.execSQL(sb.toString());
                //Imprime linha
                //Log.i("Append: ", sb.toString());

            }
            inputStream.close();
        }
        catch (Exception e ){
            e.printStackTrace();
        }
        */


        //tabeta das paradas
/*
        try {

            AssetManager assetManager = getResources().getAssets();
            InputStream inputStream = assetManager.open("newparadas.csv");
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String parada;
            LinkedList<String> paradas = new LinkedList<String>();

            // banco abrir
            SQLiteDatabase bancoDados = openOrCreateDatabase("app", MODE_PRIVATE, null);

            bancoDados.execSQL("CREATE TABLE IF NOT EXISTS paradas (idparada INT(5), codigo INT(5), longitude DOUBLE(10), latitude DOUBLE(10), terminal VARCHAR (2))");

            String tabela = "paradas";
            String colunas = "idparada, codigo, longitude, latitude, terminal";
            String str1 = "INSERT INTO " + tabela + " (" + colunas + ") values(";
            String str2 = ");";

            while ((parada = bufferedReader.readLine()) != null) {
                //Imprime linha
                //Log.i("Print: ", paradas);

                StringBuilder sb = new StringBuilder(str1);
                String[] str = parada.split(";");
                sb.append(str[0] + ",");
                sb.append(str[1] + ",");
                sb.append(str[2] + ",");
                sb.append(str[3] + ",");
                sb.append("'" + str[4] + "'");
                sb.append(str2);

                bancoDados.execSQL(sb.toString());
                //Imprime linha

            }

            String mensagem = "Pronto";
            Log.i("Concluído: ", mensagem);

            inputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
*/


    //Contrução do banco de dados, é aberta as tabelas e importadas para o banco, sendo feitas uma a uma.
/*
        try {
           //tabela de coordenadas

            AssetManager assetManager = getResources().getAssets();
            InputStream inputStream = assetManager.open("coordenadas.csv");
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String coordenada;
            LinkedList<String> coordenadas = new LinkedList<String>();


            // banco abrir
            SQLiteDatabase bancoDados = openOrCreateDatabase("app", MODE_PRIVATE, null);
            //Inserir tabela de coordenadas
            bancoDados.execSQL("CREATE TABLE IF NOT EXISTS coordenadas (idcoordenada INT(10), latitude DOUBLE(20), longitude DOUBLE(20), idlinha INT(5))");

            String tabela ="coordenadas";
            String colunas ="idcoordenada, latitude, longitude, idlinha";
            String str1 = "INSERT INTO " + tabela + " (" + colunas + ") values(";
            String str2 = ");";

            while((coordenada = bufferedReader.readLine())!=null) {
                //Imprime linha
                //Log.i("Print: ", coordenada);

                StringBuilder sb = new StringBuilder(str1);
                String[] str = coordenada.split(";");
                sb.append(str[0] + ",");
                sb.append(str[1] + ",");
                sb.append(str[2] + ",");
                sb.append(str[3]);
                sb.append(str2);
                //Insere no banco
                bancoDados.execSQL(sb.toString());
                //Imprime linha
                //Log.i("Append: ", sb.toString());

            }
            String mensagem = "Pronto";
            Log.i("Concluído: ", mensagem);
                inputStream.close();

            } catch (Exception e) {
                e.printStackTrace();
            }


*/


            /*Exibir o conteudo do banco
            Cursor cursor = bancoDados.rawQuery("SELECT * FROM coordenadas", null);

            int indiceColuneId = cursor.getColumnIndex("idcoordenada");
            int indiceColuneCodigo = cursor.getColumnIndex("latitude");
            int indiceColunelongitude = cursor.getColumnIndex("longitude");
            int indiceColunelatitude = cursor.getColumnIndex("idlinha");


            cursor.moveToFirst();

            while (cursor != null) {

                Log.i("Resultado - idcoord: ", cursor.getString(indiceColuneId));
                Log.i("Resultado - latitude: ", cursor.getString(indiceColuneCodigo));
                Log.i("Resultado - longitude: ", cursor.getString(indiceColunelongitude));
                Log.i("Resultado - idlinha: ", cursor.getString(indiceColunelatitude));
                cursor.moveToNext();
            }
            inputStream.close();
            */








            /*
            //tabeta das paradalinha
            AssetManager assetManager = getResources().getAssets();
            InputStream inputStream = assetManager.open("newparadalinha.csv");
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String paradalinha;
            LinkedList<String> paradalinhas = new LinkedList<String>();

            // banco abrir
            SQLiteDatabase bancoDados = openOrCreateDatabase("app", MODE_PRIVATE, null);

            /*bancoDados.execSQL("CREATE TABLE IF NOT EXISTS paradalinha (idlinha INT(5), idparada INT(5))");

            String tabela ="paradalinha";
            String colunas ="idlinha, idparada";
            String str1 = "INSERT INTO " + tabela + " (" + colunas + ") values(";
            String str2 = ");";

            while((paradalinha = bufferedReader.readLine())!=null){
                //Imprime linha
                //Log.i("Print: ", paradas);

                StringBuilder sb = new StringBuilder(str1);
                String[] str = paradalinha.split(";");
                sb.append(str[0] +"," );
                sb.append(str[1]);
                sb.append(str2);

                bancoDados.execSQL(sb.toString());
                //Imprime linha
                //Log.i("Append: ", sb.toString());

            }
            inputStream.close();
            */


            /*
            //Exibir o conteudo do banco
            AssetManager assetManager = getResources().getAssets();
            InputStream inputStream = assetManager.open("newparadalinha.csv");
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);


            SQLiteDatabase bancoDados = openOrCreateDatabase("app", MODE_PRIVATE, null);
            Cursor cursor = bancoDados.rawQuery("SELECT * FROM paradalinha", null);

            int indiceColuneId = cursor.getColumnIndex("idparada");
            int indiceColuneLinha = cursor.getColumnIndex("idlinha");


            cursor.moveToFirst();

            while (cursor != null) {

                Log.i("Resultado - idparada: ", cursor.getString(indiceColuneId));
                Log.i("Resultado - linha: ", cursor.getString(indiceColuneLinha));

                cursor.moveToNext();
            }


        } catch (IOException e) {
            e.printStackTrace();
        }*/

/*
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for (int result : grantResults) {
            if (result == PackageManager.PERMISSION_DENIED) {
                // Alguma permissão foi negada
                alertAndFinish();
                return;
            }
        }
        // Se chegou aqui está OK
    }

    private void alertAndFinish() {
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.app_name).setMessage("Para utilizar este aplicativo, você precisa aceitar as permissões.");
            // Add the buttons
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    finish();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }*/


}

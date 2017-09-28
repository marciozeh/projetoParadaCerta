package marcio.com.br.paradacertaprojeto;

import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        try {

            //tabela de coordenadas

            AssetManager assetManager = getResources().getAssets();
            InputStream inputStream = assetManager.open("coordenadas.csv");
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String linha;
            LinkedList<String> linhas = new LinkedList<String>();


            // banco abrir
            SQLiteDatabase bancoDados = openOrCreateDatabase("app", MODE_PRIVATE, null);
            /*Inserir tabela de coordenadas
            bancoDados.execSQL("CREATE TABLE IF NOT EXISTS coordenadas (idcoordenada INT(10), latitude DOUBLE(20), longitude DOUBLE(20), idlinha INT(5))");

            String tabela ="coordenadas";
            String colunas ="idcoordenada, latitude, longitude, idlinha";
            String str1 = "INSERT INTO " + tabela + " (" + colunas + ") values(";
            String str2 = ");";

            while((linha = bufferedReader.readLine())!=null){
                //Imprime linha
                //Log.i("Print: ", linha);

                StringBuilder sb = new StringBuilder(str1);
                String[] str = linha.split(";");
                sb.append(str[0] +"," );
                sb.append(str[1] +"," );
                sb.append(str[2] +"," );
                sb.append(str[3]);
                sb.append(str2);
                //Insere no banco
                bancoDados.execSQL(sb.toString());
                //Imprime linha
                //Log.i("Append: ", sb.toString());

            }*/

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
            }*/





/*
            //tabeta das paradas
            AssetManager assetManager = getResources().getAssets();
            InputStream inputStream = assetManager.open("newparadas.csv");
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String parada;
            LinkedList<String> paradas = new LinkedList<String>();

            // banco abrir
            SQLiteDatabase bancoDados = openOrCreateDatabase("app", MODE_PRIVATE, null);

            bancoDados.execSQL("CREATE TABLE IF NOT EXISTS paradas (idparada INT(5), codigo INT(5), longitude DOUBLE(10), latitude DOUBLE(10), terminal VARCHAR (2))");

            String tabela ="paradas";
            String colunas ="idparada, codigo, longitude, latitude, terminal";
            String str1 = "INSERT INTO " + tabela + " (" + colunas + ") values(";
            String str2 = ");";

            while((parada = bufferedReader.readLine())!=null){
                //Imprime linha
                //Log.i("Print: ", paradas);

                StringBuilder sb = new StringBuilder(str1);
                String[] str = parada.split(";");
                sb.append(str[0] +"," );
                sb.append(str[1] +"," );
                sb.append(str[2] +"," );
                sb.append(str[3] +"," );
                sb.append("'" + str[4] +"'" );
                sb.append(str2);

                bancoDados.execSQL(sb.toString());
                //Imprime linha
                //Log.i("Append: ", sb.toString());
            }

*/


            //Mensagem de concluído com sucesso.

            String texto = "concluido";
            //Log.i("Concluído: ", texto);

/*
            //Exibir o conteudo do banco
            Cursor cursor = bancoDados.rawQuery("SELECT * FROM paradas", null);

            int indiceColuneId = cursor.getColumnIndex("idparada");
            int indiceColuneCodigo = cursor.getColumnIndex("codigo");
            int indiceColunelongitude = cursor.getColumnIndex("longitude");
            int indiceColunelatitude = cursor.getColumnIndex("latitude");
            int indiceColuneterminal = cursor.getColumnIndex("terminal");

            cursor.moveToFirst();

            while (cursor != null) {

                Log.i("Resultado - idparada: ", cursor.getString(indiceColuneId));
                Log.i("Resultado - codigo: ", cursor.getString(indiceColuneCodigo));
                Log.i("Resultado - longitude: ", cursor.getString(indiceColunelongitude));
                Log.i("Resultado - latitude: ", cursor.getString(indiceColunelatitude));
                Log.i("Resultado - terminal: ", cursor.getString(indiceColuneterminal));
                cursor.moveToNext();
            }
            */

            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

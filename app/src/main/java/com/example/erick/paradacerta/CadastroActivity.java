package com.example.erick.paradacerta;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class CadastroActivity extends AppCompatActivity {

    private ImageView ivFoto;
    private TextView tvEmail, tvId, tvCpf, tvDtNascimento;
    private Button btnLogOut;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseUser mFirebaseUser;

    Button BtnLista;
    Button BtnMaps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        inicializarComponetes();
        inicializarFirebase();
//        clickButton();

        BtnMaps = (Button) findViewById(R.id.BtnMaps);

        BtnMaps.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(CadastroActivity.this, MapsActivity.class);
                startActivity(i);
            }
        });

        BtnLista = (Button) findViewById(R.id.BtnLista);

        BtnLista.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(CadastroActivity.this, ListaActivity.class);
                startActivity(i);
            }
        });



//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
//
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
//                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        drawer.setDrawerListener(toggle);
//        toggle.syncState();
//
//        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
//        navigationView.setNavigationItemSelectedListener(this);
    }

//    private void clickButton() {
//        btnLogOut.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                LogOut();
//            }
//        });
//    }
//
//    private void LogOut() {
//        mFirebaseAuth.signOut();
//        LoginManager.getInstance().logOut();
//        finish();
//    }

    private void inicializarFirebase() {
        mFirebaseAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                mFirebaseUser = firebaseAuth.getCurrentUser();
                if (mFirebaseUser != null){
                    exibirDados(mFirebaseUser);

                }else{
                    finish();
                }
            }
        };
    }

    private void exibirDados(FirebaseUser mFirebaseUser) {
        tvEmail.setText(mFirebaseUser.getEmail());
        tvId.setText(mFirebaseUser.getUid());
        Glide.with(CadastroActivity.this).load(mFirebaseUser.getPhotoUrl()).into(ivFoto);
    }

    private void inicializarComponetes() {
        ivFoto = (ImageView) findViewById(R.id.ivFoto);
        tvEmail = (TextView)  findViewById(R.id.tvEmail);
        tvId = (TextView)  findViewById(R.id.tvId);
        tvCpf = (TextView)  findViewById(R.id.tvCpf);
        tvDtNascimento = (TextView)  findViewById(R.id.tvDtNascimento);
//        btnLogOut = (Button) findViewById(R.id.btnLogOut);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
    }

//    @Override
//    public void onBackPressed() {
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        if (drawer.isDrawerOpen(GravityCompat.START)) {
//            drawer.closeDrawer(GravityCompat.START);
//        } else {
//            super.onBackPressed();
//        }
//    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.maps, menu);
//        return true;
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

//    @SuppressWarnings("StatementWithEmptyBody")
//    @Override
//    public boolean onNavigationItemSelected(MenuItem item) {
//        // Handle navigation view item clicks here.
//        int id = item.getItemId();
//        switch (id){
//            case R.id.nav_maps_activity:
//                Intent h = new Intent(CadastroActivity.this,MapsActivity.class);
//                startActivity(h);
//                break;
//            case R.id.nav_cadastro:
//                Intent i = new Intent(CadastroActivity.this,CadastroActivity.class);
//                startActivity(i);
//                break;
//        }
//
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        drawer.closeDrawer(GravityCompat.START);
//        return true;
//    }
}

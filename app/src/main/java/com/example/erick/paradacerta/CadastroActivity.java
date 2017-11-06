package com.example.erick.paradacerta;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        inicializarComponetes();
        inicializarFirebase();
        clickButton();
    }

    private void clickButton() {
        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogOut();
            }
        });
    }

    private void LogOut() {
        mFirebaseAuth.signOut();
        LoginManager.getInstance().logOut();
        finish();
    }

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
        btnLogOut = (Button) findViewById(R.id.btnLogOut);
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
}

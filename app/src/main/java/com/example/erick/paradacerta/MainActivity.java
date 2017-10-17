package com.example.erick.paradacerta;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;


public class MainActivity extends AppCompatActivity {

    private LoginButton loginButton;
    private FirebaseAuth fireBaseAuth;
    private CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inicializarComponente();
        inicializarFirebaseCallback();
        clickButton();
    }

    private void clickButton() {
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(LoginResult loginResult) {
                firebaseLogin(loginResult.getAccessToken());

                if (AccessToken.getCurrentAccessToken() != null) {
                    firebaseLogin(loginResult.getAccessToken());
                }

            }

            @Override
            public void onCancel() {
                alert("Operação Cancelada");
            }

            @Override
            public void onError(FacebookException error) {
                alert("Erro no login com o Facebook");
            }
        });

    }

    private void inicializarFirebaseCallback() {
        fireBaseAuth = FirebaseAuth.getInstance();
        callbackManager = CallbackManager.Factory.create();

    }

    private void inicializarComponente() {
        loginButton = (LoginButton) findViewById(R.id.btnLogin);
        loginButton.setReadPermissions("email","public_profile");
    }
    private void firebaseLogin(AccessToken accessToken) {
        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        fireBaseAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Intent i = new Intent(MainActivity.this,MapsActivity.class);
                    startActivity(i);
                }else{
                    alert("Erro de Autenticação com o Firebase");

                }
            }
        });
    }

    private void alert(String s) {
        Toast.makeText(this,s,Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

}

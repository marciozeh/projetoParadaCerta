package com.example.erick.paradacerta;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;


public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private LoginButton loginButton;
    private FirebaseAuth fireBaseAuth;
    private CallbackManager callbackManager;

    private GoogleApiClient googleApiClient;

    private SignInButton signInButton;

    public static final int SIGN_IN_CODE = 777;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        try {
            new DataBaseHelper(this).createDataBase();
        } catch (IOException e) {
            e.printStackTrace();
        }


        inicializarComponente();
        inicializarFirebaseCallback();
        clickButton();

        //Inicia o componente
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        //Autenticação do gmail
        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        //Cria o Button utilizando a autenticação
        signInButton = (SignInButton) findViewById(R.id.signInButton);
        signInButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
                startActivityForResult(intent, SIGN_IN_CODE);
            }
        });
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

    //Alert para passar a mensagem de resultado
    private void alert(String s) {
        Toast.makeText(this,s,Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SIGN_IN_CODE){
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }

        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    //Inicia a sessão redirecionando para a activity do maps
    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()){
            goMapsScreen();
        }else {
            Toast.makeText(this, "Nào iniciou a sessão",Toast.LENGTH_SHORT).show();
        }
    }

    //Redireciona para a activity do maps
    private void goMapsScreen() {
        Intent intent = new Intent(this, MapsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

}

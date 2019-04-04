package guc.edu.thermonitor;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button bl;
    private EditText email, password;
    private Button bl2;
    private ProgressDialog loadingDialog;
    private FirebaseAuth firebaseAuth;
    private TextView forgotpassword;
    SignInButton button;
    GoogleApiClient GoogleApiClient;
    public static final int SIGN_IN_CODE=777;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        bl = (Button) findViewById(R.id.login);
        bl2 = (Button) findViewById(R.id.button);
        email = (EditText) findViewById(R.id.email);
        button=(SignInButton)findViewById(R.id.sign_in_button);
        forgotpassword=(TextView)findViewById(R.id.forgotpassword);
        firebaseAuth = FirebaseAuth.getInstance();
        password = (EditText) findViewById(R.id.password);
       // AdView  mAdView = findViewById(R.id.adView);
       // AdRequest adRequest = new AdRequest.Builder().build();
       // mAdView.loadAd(adRequest);
        loadingDialog = new ProgressDialog(this);
        bl.setOnClickListener(this);
        bl2.setOnClickListener(this);
        forgotpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,ForgotPasswordActivity.class));
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(GoogleApiClient);
                startActivityForResult(signInIntent,SIGN_IN_CODE);

            }
        });
// Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("359486822120-7737qdnoa5uar7f4jrk4kemg818jbn8r.apps.googleusercontent.com")
                .requestEmail()
                .build();



    GoogleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this,new GoogleApiClient.OnConnectionFailedListener() {
        public void onConnectionFailed(ConnectionResult connectionResult){
            Toast.makeText(MainActivity.this,"Connection Failed",Toast.LENGTH_LONG).show();

        }


    }).addApi(Auth.GOOGLE_SIGN_IN_API,gso).build();

    }




    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == SIGN_IN_CODE) {
            loadingDialog.setMessage("Please Wait...");
            loadingDialog.setCanceledOnTouchOutside(true);
            loadingDialog.show();
           // Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
//
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()){
            GoogleSignInAccount account=result.getSignInAccount();
            firebaseAuthWithGoogle(account);
            Toast.makeText(MainActivity.this,"",Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(MainActivity.this,"Couldn't Get Authentication Results",Toast.LENGTH_LONG).show();
            loadingDialog.dismiss();
        }


    }


    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information

                           FirebaseUser user = firebaseAuth.getCurrentUser();
                            Intent intent =new Intent(MainActivity.this,listActivity.class);
                            startActivity(intent);
                            Toast.makeText(getApplicationContext(),"Logged in successfully",Toast.LENGTH_LONG).show();
                            loadingDialog.hide();

                        } else {
                         Toast.makeText(getApplicationContext(),"Could not log in",Toast.LENGTH_LONG).show();
                         loadingDialog.hide();
                        }

                        // ...
                    }
                });

    }



    public void Signin() {

         String mail = email.getText().toString().trim();
        String pass = password.getText().toString().trim();
        if (TextUtils.isEmpty(mail) ){
            showmessage("PLEASE ENTER EMAIL");
            return;
        }
        if( TextUtils.isEmpty(pass)) {
            // Toast.makeText(this,"PLEASE VERIFY ALL FIELDS",Toast.LENGTH_LONG).show();
            showmessage("PLEASE ENTER PASSSWORD");
            return;
                }
        loadingDialog.setMessage("PLEASE WAIT.......");
        loadingDialog.show();
        firebaseAuth.signInWithEmailAndPassword(mail, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    if (firebaseAuth.getCurrentUser().isEmailVerified()) {
                        loadingDialog.hide();
                        showmessage("SIGNED IN SUCCESSFULLY");
                        Intent in = new Intent(MainActivity.this, listActivity.class);
                        startActivity(in);
                        //onStart();
                        //    finish();
                    }
                    else
                    {
                        Toast.makeText(MainActivity.this,"PLEASE VERIFY YOUR MAIL",Toast.LENGTH_LONG).show();
                    }
                }

                else {
                    loadingDialog.hide();
                    showmessage("COULD NOT SIGN IN . PLEASE TRY AGAIN " + task.getException().getMessage());
                }
                loadingDialog.dismiss();
            }
        });

    }

   // public void updateUI() {
       // startActivity(listActivity);
     //   Intent in = new Intent(MainActivity.this, listActivity.class);
       // startActivity(in);

    //}
    //      else
    //    {
    //      Toast.makeText(MainActivity.this,"SORRY,EMAIL OR PASSWORD IS INCORRECT",Toast.LENGTH_LONG).show();
    // }
    //}
    public void onClick(View v) {
      if(v==bl2) {


          Intent in = new Intent(MainActivity.this, RegisterActivity.class);
          startActivity(in);

      }
      if(v==bl){
      Signin();
      }
    }

   // public void OpenRegister() {
     //   Intent in = new Intent(MainActivity.this, RegisterActivity.class);
      //  startActivity(in);
   // }
    public void showmessage(String text) {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
    }

   //public void onStart() {
 //      super.onStart();
     //  FirebaseUser user = firebaseAuth.getCurrentUser();
      // if (user != null) {
        //   Intent in = new Intent(MainActivity.this, listActivity.class);
         //  startActivity(in);
       //}
  // }
}

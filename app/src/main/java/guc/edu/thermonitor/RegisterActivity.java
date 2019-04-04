package guc.edu.thermonitor;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.CircularProgressDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private Button button1, button2;
    private EditText text1, text2, text3;
    private ProgressDialog ProgressDialog;
    //   private ProgressBar loadingbar;
    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        firebaseAuth = FirebaseAuth.getInstance();
        //     loadingbar = new ProgressBar(this);
        button1 = (Button) findViewById(R.id.Register);
        button2 = (Button) findViewById(R.id.buttonn);
        text1 = (EditText) findViewById(R.id.email);
        text2 = (EditText) findViewById(R.id.password);
        text3 = (EditText) findViewById(R.id.confirmpassword);
        ProgressDialog = new ProgressDialog(this);
        //   button1.setVisibility(View.VISIBLE);
        // loadingbar.setVisibility(View.INVISIBLE);
        //  final String email = text1.getText().toString();
        // final String password = text2.getText().toString();
        //    final String confirmpassword = text3.getText().toString();
        button1.setOnClickListener(this);
        button2.setOnClickListener(this);

        //   if (email.isEmpty() || password.isEmpty() || confirmpassword.isEmpty() || !password.equalsIgnoreCase(confirmpassword)) {
        //     Toast.makeText(this, "PLEASE VERIFY ALL FIELDS", Toast.LENGTH_LONG).show();
        //    button1.setVisibility(View.VISIBLE);
        //   loadingbar.setVisibility(View.INVISIBLE);
        //} else {

        //  registeruser();
        // }
        //  button2.setOnClickListener(new View.OnClickListener() {
        //    @Override
        //  public void onClick(View v) {
        //    OpenMainActivity();
        //}
        //});

    }


    private void registeruser() {
        String email = text1.getText().toString().trim();
        String password = text2.getText().toString().trim();
        String confirmpass = text3.getText().toString().trim();

        if (text1.getText().toString().isEmpty() || text2.getText().toString().isEmpty()||  text3.getText().toString().isEmpty() ) {
            Toast.makeText(this, "PLEASE VERIFY ALL FIELDS", Toast.LENGTH_LONG).show();
            return;
        }

        if(!(text3.getText().toString().equals(text2.getText().toString()))){
            Toast.makeText(this, "PASSWORD AND CONFIRM PASSWORD DO NOT MATCH", Toast.LENGTH_LONG).show();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
          Toast.makeText(this,"PLEASE ENTER VALID EMAIL ADDREESS",Toast.LENGTH_LONG).show();
        }

        //    button1.setVisibility(View.VISIBLE);
        //  loadingbar.setVisibility(View.INVISIBLE);
        //    if (TextUtils.isEmpty(email)) {
        //      showmessage("Please Enter Email");

        //}
        //if (TextUtils.isEmpty(password)) {
        //  showmessage("Please Enter Password");
        //}
        //if (TextUtils.isEmpty(confirmpassword)) {
        //  showmessage("Please Enter Confirmpassword");
        // }
        //if (!password.equalsIgnoreCase(confirmpassword)) {
        // showmessage("Please Verify All fields");
        // }


        ProgressDialog.setMessage("Registering User.......");
        ProgressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    firebaseAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                ProgressDialog.hide();
                                showmessage("REGISTERED SUCCESSFULLY,PLEASE CHECK YOUR EMAIL");

                                    updateUI();
                                    finish();


                        }
                        else{
                            Toast.makeText(RegisterActivity.this,task.getException().getMessage(),Toast.LENGTH_LONG).show();
                            }
                        }

                    });


                } else {
                    ProgressDialog.hide();
                    showmessage("COULD NOT REGISTER . PLEASE TRY AGAIN" + task.getException().getMessage());
                }
                ProgressDialog.dismiss();
            }


        });


    }


    private void updateUI() {
        Intent intentt = new Intent(RegisterActivity.this, MainActivity.class);
        startActivity(intentt);

    }


    private void showmessage(String text) {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
    }

    public void onClick(View view) {
        if (view == button1) {
            registeruser();
        }
        if (view == button2) {
            Intent in = new Intent(RegisterActivity.this, MainActivity.class);
            startActivity(in);
        }
    }












}
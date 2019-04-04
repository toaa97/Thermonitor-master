package guc.edu.thermonitor;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DeviceDetailActivity extends AppCompatActivity {
   Button delete;
   Button logout;
   FirebaseUser auth;
   ProgressDialog progrees;
   private GoogleApiClient mGoogleSignInClient;
   //private TextView MsgTxt;
   private TextView celsius;
   private TextView fahrenheit;
   private FirebaseDatabase firebaseDatabase;
   private DatabaseReference myRef;
   //private DatabaseReference mChildResference = mRootReference.child("message");
   String currentEsp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_detail);
        Intent intent = getIntent();
        currentEsp=intent.getStringExtra("name");
        celsius = (TextView) findViewById(R.id.txtC);
        fahrenheit = (TextView) findViewById(R.id.txtF);
        delete = (Button)findViewById(R.id.buttondelete);
        logout=(Button)findViewById(R.id.buttonlogout);
        auth= FirebaseAuth.getInstance().getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        myRef = firebaseDatabase.getReference();
        progrees=new ProgressDialog(this);


        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             FirebaseUser  user= FirebaseAuth.getInstance().getCurrentUser();


            if (user!=null){
                progrees.setMessage("Deactiviting ......please wait");
                progrees.show();
                user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            progrees.hide();
                            Toast.makeText(DeviceDetailActivity.this,"Account Deactivated",Toast.LENGTH_LONG).show();
                            finish();
                            startActivity(new Intent(DeviceDetailActivity.this,MainActivity.class));
                        }
                        else {
                            progrees.hide();
                            Toast.makeText(DeviceDetailActivity.this, "Account cannot be Deactivated", Toast.LENGTH_LONG).show();
                        }
                    }
                });



            }




            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 FirebaseAuth.getInstance().signOut();
                 startActivity(new Intent(DeviceDetailActivity.this,MainActivity.class));
            }
        });


        readFromDatabase();


    }

    //getting user
    @Override
    protected void onStart() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("874551466482-tjvm46r9lsvnj1k5k4qefs8ocmj5po1r.apps.googleusercontent.com")
                .requestEmail()
                .build();
        mGoogleSignInClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        mGoogleSignInClient.connect();
        super.onStart();
    }

    public void readFromDatabase() {
        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                showData(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("file", "Failed to read value.", error.toException());
            }
        });
    }

    private void showData(DataSnapshot dataSnapshot) {
        for ( DataSnapshot ds : dataSnapshot.getChildren() ) {
            if(currentEsp.equals("MyESPT")) {
                Temperature temperature = new Temperature();
                temperature.setCelsius(ds.child("MyESPT").getValue(Temperature.class).getCelsius());
                temperature.setFahrenheit(ds.child("MyESPT").getValue(Temperature.class).getFahrenheit());
                celsius.setText(temperature.getCelsius());
                fahrenheit.setText(temperature.getFahrenheit());


            }
           else{
                Temperature temperature = new Temperature();
                temperature.setCelsius(ds.child("MyESPR").getValue(Temperature.class).getCelsius());
                temperature.setFahrenheit(ds.child("MyESPR").getValue(Temperature.class).getFahrenheit());
                celsius.setText(temperature.getCelsius());
                fahrenheit.setText(temperature.getFahrenheit());
            }

        }
    }
}

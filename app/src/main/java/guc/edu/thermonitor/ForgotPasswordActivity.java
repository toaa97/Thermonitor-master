package guc.edu.thermonitor;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {
    private EditText forgotpassword;
    private Button resetpassword;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        forgotpassword=(EditText)findViewById(R.id.emailforgotpassword);
        resetpassword=(Button)findViewById(R.id.buttonforgotpassword);
        firebaseAuth=firebaseAuth.getInstance();
        resetpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String useremail=forgotpassword.getText().toString().trim();
                if(useremail.equals("")){
                    Toast.makeText(ForgotPasswordActivity.this,"Enter your Email",Toast.LENGTH_LONG).show();
                }
                else {
                    firebaseAuth.sendPasswordResetEmail(useremail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                         if (task.isSuccessful()){
                             Toast.makeText(ForgotPasswordActivity.this,"Password reset email sent",Toast.LENGTH_LONG).show();
                             finish();
                             startActivity(new Intent(ForgotPasswordActivity.this,MainActivity.class));

                         }
                         else{
                             Toast.makeText(ForgotPasswordActivity.this,"Error in Sending Password reset",Toast.LENGTH_LONG).show();
                         }
                        }
                    });
                }

            }
        });

    }
}

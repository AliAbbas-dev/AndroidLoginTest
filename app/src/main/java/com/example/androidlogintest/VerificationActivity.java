package com.example.androidlogintest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import es.dmoral.toasty.Toasty;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.tuyenmonkey.mkloader.MKLoader;

import java.util.concurrent.TimeUnit;

public class VerificationActivity extends AppCompatActivity {

    TextView resend,textView;
    EditText otp;
    Button verify;
    MKLoader loader;
    String number,id;

    private FirebaseAuth mAuth;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        mAuth=FirebaseAuth.getInstance();
        databaseReference= FirebaseDatabase.getInstance().getReference("PhoneNumber");
        number=getIntent().getStringExtra("number");
        sendVerificationCode();

        resend=findViewById(R.id.resendcode);
        textView=findViewById(R.id.textview);
        textView.setText("Please type the verification code sent\n to "+number);
        otp=findViewById(R.id.otp);
        loader=findViewById(R.id.loader);
        verify=findViewById(R.id.verify);
        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (TextUtils.isEmpty(otp.getText().toString())){
                    Toasty.info(VerificationActivity.this, "Enter Verification Code", Toast.LENGTH_SHORT, true).show();
                }
                else if (otp.getText().toString().replace(" ","").length()!=6){
                    Toasty.error(VerificationActivity.this, "Enter Verification Code", Toast.LENGTH_SHORT, true).show();
                }
                else{

                    loader.setVisibility(View.VISIBLE);
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(id, otp.getText().toString().replace(" ",""));
                    signInWithPhoneAuthCredential(credential);
                }
            }
        });
        resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendVerificationCode();
            }
        });
    }

    private void sendVerificationCode() {

        new CountDownTimer(60000,1000) {
            @Override
            public void onTick(long l) {
                resend.setText("Resend After "+l/1000);
                resend.setEnabled(false);
            }

            @Override
            public void onFinish() {

                resend.setText(" Resend OTP");
                resend.setEnabled(true);
            }
        }.start();

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                number,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onCodeSent(@NonNull String id, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        VerificationActivity.this.id = id;
                    }

                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        signInWithPhoneAuthCredential(phoneAuthCredential);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {

                        Toasty.error(VerificationActivity.this, "Verification Failed", Toast.LENGTH_SHORT, true).show();
                    }
                });        // OnVerificationStateChangedCallbacks
    }
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                                loader.setVisibility(View.GONE);
                                startActivity(new Intent(VerificationActivity.this, MainActivity.class));
                                finish();

                               PhoneNumber num= new PhoneNumber(number);
                                FirebaseDatabase.getInstance().getReference("PhoneNumber")
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .setValue(num).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override

                                    public void onComplete(@NonNull Task<Void> task) {

                                    }
                                });
                            FirebaseUser user = task.getResult().getUser();
                        }
                        else {
                            Toasty.error(VerificationActivity.this, "Verification Failed", Toast.LENGTH_SHORT, true).show();

                            }
                        }
                });
    }


}

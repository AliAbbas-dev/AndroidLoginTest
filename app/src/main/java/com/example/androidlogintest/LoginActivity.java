package com.example.androidlogintest;

import androidx.appcompat.app.AppCompatActivity;
import es.dmoral.toasty.Toasty;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.hbb20.CountryCodePicker;
public class LoginActivity extends AppCompatActivity {

    private CountryCodePicker ccp;
    EditText user_no;
    Button submit;
    FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        user_no = findViewById(R.id.user_no);
        submit = findViewById(R.id.submit);
        ccp = (CountryCodePicker) findViewById(R.id.ccp);
        ccp.registerCarrierNumberEditText(user_no);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (TextUtils.isEmpty(user_no.getText().toString().trim())){
                    Toasty.info(LoginActivity.this, "Enter Phone Number", Toast.LENGTH_SHORT, true).show();
                }
                else if (user_no.getText().toString().replace(" ","").length()!=10){
                    Toasty.error(LoginActivity.this, "Enter Valid Phone Number", Toast.LENGTH_SHORT, true).show();
                }
                else{
                    Intent intent=new Intent(LoginActivity.this, VerificationActivity.class);
                    intent.putExtra("number",ccp.getFullNumberWithPlus().replace(" ",""));
                    startActivity(intent);
                    finish();
                }
            }

        });
    }
}





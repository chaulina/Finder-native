package com.finder.gofrendi.finder;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void onButtonLoginClick(View view){
        AppBackEnd backEnd = new AppBackEnd(this);
        EditText editTextLoginEmail = (EditText) findViewById(R.id.editText_login_email);
        EditText editTextLoginPassword = (EditText) findViewById(R.id.editText_login_password);
        String email = String.valueOf(editTextLoginEmail.getText());
        String password = String.valueOf(editTextLoginPassword.getText());
        if(backEnd.loginByEmail(email, password)){
            Log.d("my.loginActivity", "success");
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
        else{
            Log.d("my.loginActivity", "failed");
            String errorMessage = backEnd.getErrorMessage();
            Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_LONG);
            toast.show();
        }
    }

}

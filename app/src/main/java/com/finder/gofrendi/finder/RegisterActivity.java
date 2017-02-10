package com.finder.gofrendi.finder;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
    }

    public void onButtonRegisterClick(View view){
        AppBackEnd backEnd = new AppBackEnd(this);
        EditText editTextRegisterEmail = (EditText) findViewById(R.id.editText_register_email);
        EditText editTextRegisterPassword = (EditText) findViewById(R.id.editText_register_password);
        EditText editTextRegisterName = (EditText) findViewById(R.id.editText_register_name);
        String email = String.valueOf(editTextRegisterEmail.getText());
        String password = String.valueOf(editTextRegisterPassword.getText());
        String name = String.valueOf(editTextRegisterName.getText());
        if(backEnd.register(email, name, password)){
            Log.d("my.registerActivity", "success");
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
        else{
            Log.d("my.registerActivity", "failed");
            String errorMessage = backEnd.getErrorMessage();
            Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_LONG);
            toast.show();
        }
    }
}

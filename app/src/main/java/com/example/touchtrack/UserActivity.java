package com.example.touchtrack;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class UserActivity extends AppCompatActivity {
    private TextView username;
    private TextView error_msg;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_username);
        username = (TextView) findViewById(R.id.user_name);
        error_msg = (TextView) findViewById(R.id.error_message);
        error_msg.setVisibility(View.INVISIBLE);
    }

    public void button_press(View view){
        String text;
        String name;
        text = ((Button) view).getText().toString();
        name = username.getText().toString();
        if(name.isEmpty() || name.trim().isEmpty()){
            error_msg.setVisibility(View.VISIBLE);
            return;
        }

        // Train case
        if(text.equals("Train")){
            Intent intent = new Intent(this, TrainActivity.class);
            intent.putExtra("USERNAME", name);
            //getIntent().getStringExtra("USERNAME");
            startActivity(intent);
        }
        // Authenticate case
        else{

        }
    }
}


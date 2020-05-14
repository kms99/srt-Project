package com.example.srt_project;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class Food_info_Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_info);

        TextView name_text = (TextView)findViewById(R.id.intent_food_name);
        TextView num_text = (TextView)findViewById(R.id.intent_food_num);

        Bundle bundle = getIntent().getExtras();
        name_text.setText(bundle.getString("name"));
        num_text.setText(bundle.getString("num"));

    }
}

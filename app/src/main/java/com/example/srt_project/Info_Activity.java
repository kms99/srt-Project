package com.example.srt_project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Info_Activity extends AppCompatActivity {

    private Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_);
        mContext = this;
        EditText username = (EditText)findViewById(R.id.add_username);
        Button setButton = (Button)findViewById(R.id.set_button);
        setButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!username.getText().toString().equals("")){
                    SharedPref_id.setString(mContext,"user",username.getText().toString());
                    Intent intent = new Intent(Info_Activity.this,HomeActivity.class);
                    startActivity(intent);
                    finish();
                }
                else{
                    username.setError("사용자 이름을 입력해주세요.");
                }
            }
        });
    }
}

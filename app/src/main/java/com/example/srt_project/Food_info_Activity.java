package com.example.srt_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DownloadManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

public class Food_info_Activity extends AppCompatActivity {

    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    StorageReference ref;
    private String getname;
    private TextView food_expiration;
    private ImageView imageView;
    private String name, url, expiration, tagName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_info);

        TextView food_name = (TextView)findViewById(R.id.food_name);
        TextView food_user = (TextView)findViewById(R.id.user);
        food_expiration = (TextView)findViewById(R.id.expiration);
        imageView = (ImageView)findViewById(R.id.food_img);

        Bundle bundle = getIntent().getExtras();
        getname = bundle.getString("name");
        food_name.setText(bundle.getString("name"));
        food_user.setText(bundle.getString("user"));

        parser();


    }

    private void parser(){

        // 내부 xml파일이용시
        InputStream inputStream = getResources().openRawResource(R.raw.info_food);
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader reader = new BufferedReader(inputStreamReader);
        XmlPullParserFactory factory = null;
        XmlPullParser xmlParser = null;
        boolean check = false;

        boolean isName = false, isUrl = false, isExpiration = false;
        try {
            factory = XmlPullParserFactory.newInstance();
            xmlParser = factory.newPullParser();
            xmlParser.setInput(reader);
            int eventType = xmlParser.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT){
                switch (eventType){
                    case XmlPullParser.START_TAG :
                        tagName = xmlParser.getName();
                        switch (tagName) {
                            case "name":
                                isName = true;
                                break;
                            case "url" :
                                isUrl = true;
                                break;
                            case "expiration" :
                                isExpiration = true;
                                break;
                        }
                        break;

                    case XmlPullParser.TEXT:
                        if(isName == true) {
                            if(xmlParser.getText().equals(getname)){
                                check=true;
                                name = xmlParser.getText();
                                Log.d("TAG!!",xmlParser.getText());
                            }
                        }
                        if(isUrl == true) {
                            if(check==true){
                                url = xmlParser.getText();
                                Log.d("TAG!!",xmlParser.getText());
                            }
                        }
                        if(isExpiration == true) {
                            if(check==true){
                                expiration = xmlParser.getText();
                                Log.d("TAG!!",xmlParser.getText());
                            }
                        }
                        break;

                    case XmlPullParser.END_TAG:
                        tagName = xmlParser.getName();
                        switch (tagName) {
                            case "name":
                                isName = false;
                                break;
                            case "url" :
                                isUrl = false;
                                check=false;
                                break;
                            case "expiration" :
                                isExpiration = false;
                                break;
                        }

                        break;
                }
                try {
                    eventType = xmlParser.next();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } finally{
            try{
                if(reader !=null) reader.close();
                if(inputStreamReader !=null) inputStreamReader.close();
                if(inputStream !=null) inputStream.close();

                Glide.with(Food_info_Activity.this).load(url).into(imageView);
                food_expiration.setText(expiration+"일");
            }catch(Exception e2){
                e2.printStackTrace();
            }
        }

    }

}

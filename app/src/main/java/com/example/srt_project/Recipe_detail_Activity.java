package com.example.srt_project;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class Recipe_detail_Activity extends AppCompatActivity {

    private ImageView recipe_img;
    private TextView recipe_name;
    private TextView material1;
    private TextView material2;
    private TextView recipe;

    public String recipe_img_Url;
    public String recipeName;
    public String recipeId;


    public String material_url;
    public String step_url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        recipe_img = (ImageView)findViewById(R.id.recipe_img);
        recipe_name = (TextView)findViewById(R.id.recipe_name);
        material1 = (TextView)findViewById(R.id.material1);
        material2 = (TextView)findViewById(R.id.material2);
        recipe = (TextView)findViewById(R.id.recipe);

        material_url= "http://211.237.50.150:7080/openapi/2e736f9835dcd6eeb1f1ce6042c471dc83d0385b1dfec20d8a062611836c2340/xml/Grid_20150827000000000227_1/1/1000";
        step_url="http://211.237.50.150:7080/openapi/2e736f9835dcd6eeb1f1ce6042c471dc83d0385b1dfec20d8a062611836c2340/xml/Grid_20150827000000000228_1/1/1000";

        Bundle bundle = getIntent().getExtras();
        recipeId = bundle.getString("recipe_id");
        recipeName = bundle.getString("recipe_name");
        recipe_img_Url = bundle.getString("recipe_img");

        recipe_name.setText(recipeName);
        Glide.with(Recipe_detail_Activity.this).load(recipe_img_Url).into(recipe_img);

        //네트워크 파싱
        new Description_material().execute();
        new Description_step().execute();

    }

    private class Description_material extends AsyncTask<Void, Void, Void>{
        ArrayList<String> material_list = new ArrayList<String>();
        String material_text1="";
        String material_text2="";
        @Override
        protected Void doInBackground(Void... voids) {
            try{
                Document doc = Jsoup.connect(material_url).get();
                Elements materials = doc.select("row");
                for(Element material : materials){
                    if(recipeId.equals(material.select("RECIPE_ID").text()))
                    {
                        material_list.add(material.select("IRDNT_NM").text()+" "+material.select("IRDNT_CPCTY").text()+"\n");
                    }
                }

                for(int i =0; i<material_list.size(); i++){
                    if(i%2==0){
                        material_text1+=material_list.get(i);
                    }
                    else{
                        material_text2+=material_list.get(i);
                    }
                }

            } catch (IOException e){
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }
        @Override
        protected void onPostExecute(Void result){
            material1.setText(material_text1);
            material2.setText(material_text2);
            //recipy.setText(step_text);

        }
    }




    private class Description_step extends AsyncTask<Void, Void, Void>{
        ArrayList<String> step_list = new ArrayList<String>();
        String step_text = "";
        @Override
        protected Void doInBackground(Void... voids) {
            try{
                Document doc = Jsoup.connect(step_url).get();
                Elements steps = doc.select("row");
                for(Element step : steps){
                    if(recipeId.equals(step.select("RECIPE_ID").text()))
                    {
                        step_list.add(step.select("COOKING_NO").text()+". "+step.select("COOKING_DC").text()+"\n\n");
                    }
                }

                for(int i =0; i<step_list.size(); i++){
                    step_text +=step_list.get(i);
                }

            } catch (IOException e){
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }
        @Override
        protected void onPostExecute(Void result){
            recipe.setText(step_text);

        }
    }
}

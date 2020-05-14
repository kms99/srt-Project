package com.example.srt_project;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Food_list_fragment extends Fragment{
    ViewGroup viewGroup;
    private RecyclerView myFirestoreList;
    public FirebaseFirestore firebaseFirestore;
    private ArrayList <String[]> food_list;
    public SwipeRefreshLayout swipeRefreshLayout;
    public FloatingActionButton floatingActionButton;
    public RecyclerAdapter adapter;
    public String recipe_url;

    private Context mContext;
    HashMap <String,Object> set_recipe;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewGroup = (ViewGroup) inflater.inflate(R.layout.recyclerview_fragment,container,false);
        mContext=this.getContext();
        food_list = new ArrayList<String[]>();
        firebaseFirestore = FirebaseFirestore.getInstance();
        swipeRefreshLayout=viewGroup.findViewById(R.id.swipe_layout);
        //식재료 api
        recipe_url="http://211.237.50.150:7080/openapi/2e736f9835dcd6eeb1f1ce6042c471dc83d0385b1dfec20d8a062611836c2340/xml/Grid_20150827000000000227_1/1/1000";
        // 식재료 가져오기
        DocumentReference docRef = firebaseFirestore.collection(SharedPref_id.getString(mContext,"ThisRef")).document("food");
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Iterator<String> foods = document.getData().keySet().iterator();
                        Iterator<Object> food_nums = document.getData().values().iterator();
                        while (foods.hasNext()){
                            String food = foods.next();
                            String food_num = food_nums.next().toString();
                            food_list.add(new String[]{food,food_num});
                        }
                        //리스트 갱신
                        new Description().execute();
                                myFirestoreList = viewGroup.findViewById(R.id.firestore_list);
                                myFirestoreList.setLayoutManager(new LinearLayoutManager(getActivity())) ;
                                adapter = new RecyclerAdapter(food_list) ;
                                myFirestoreList.setAdapter(adapter) ;
                    }
                }
            }
        });

        //식재료 추가
        floatingActionButton=viewGroup.findViewById(R.id.floating);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                View dialogView = getLayoutInflater().inflate(R.layout.add_dialog,null);
                final EditText add_foodName = (EditText)dialogView.findViewById(R.id.add_food);
                final EditText add_foodNum = (EditText)dialogView.findViewById(R.id.add_num);
                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setView(dialogView);
                builder.setPositiveButton("저장", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String input_foodName = add_foodName.getText().toString();
                        Integer input_num = Integer.parseInt(add_foodNum.getText().toString());

                        firebaseFirestore.collection(SharedPref_id.getString(mContext,"ThisRef")).document("food")
                                .update(input_foodName,input_num)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                                        ft.detach(Food_list_fragment.this).attach(Food_list_fragment.this).commit();
                                        final Handler handler = new Handler();
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                myFirestoreList.setAdapter(adapter);
                                                swipeRefreshLayout.setRefreshing(false);
                                            }
                                        }, 500);
                                        final AlertDialog.Builder success_builder = new AlertDialog.Builder(getActivity());
                                        success_builder.setMessage("성공적으로 추가되었습니다.");
                                        success_builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        });
                                        AlertDialog sucessAlert = success_builder.create();
                                        sucessAlert.show();
                                    }
                                });
                       dialog.dismiss();
                    }
                });
                builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });


        //식재료 리스트 생성
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.detach(Food_list_fragment.this).attach(Food_list_fragment.this).commit();
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        myFirestoreList.setAdapter(adapter);
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 500);
            }
        });





        return viewGroup;
    }

    //레시피 갱신
    private class Description extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            try{
                Document doc = Jsoup.connect(recipe_url).get();
                Elements search_Origns = doc.select("row");
                set_recipe= new HashMap<>();

                for(Element search_Orign : search_Origns){


                        for (int i = 0; i < food_list.size(); i++) {
                            if (food_list.get(i)[0].equals(search_Orign.select("IRDNT_NM").text())) {
                                Log.d("please", search_Orign.select("RECIPE_ID").text());
                                set_recipe.put(search_Orign.select("RECIPE_ID").text(), null);
                            }
                        }

                }

                for(int i = 0; i<set_recipe.size();i++){
                    Log.d("please",set_recipe.toString());
                }
                firebaseFirestore.collection(SharedPref_id.getString(mContext,"ThisRef"))
                        .document("recipe")
                        .delete();
                firebaseFirestore.collection(SharedPref_id.getString(mContext,"ThisRef"))
                        .document("recipe")
                        .set(set_recipe);



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

        }
    }


    public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

        private ArrayList<String[]> mData = null ;

        // 아이템 뷰를 저장하는 뷰홀더 클래스.
        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView name_text ;
            TextView num_text;

            ViewHolder(final View itemView) {
                super(itemView) ;


                // 뷰 객체에 대한 참조. (hold strong reference)
                name_text = itemView.findViewById(R.id.name_text) ;
                num_text = itemView.findViewById(R.id.editional_text) ;

                itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        final AlertDialog.Builder delete_builder = new AlertDialog.Builder(getActivity());
                        delete_builder.setMessage("삭제하시겠습니까?");
                        delete_builder.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DocumentReference docRef = firebaseFirestore.collection(SharedPref_id.getString(mContext,"ThisRef")).document("food");
                                Map<String,Object> updates = new HashMap<>();
                                updates.put(mData.get(getAdapterPosition())[0], FieldValue.delete());
                                docRef.update(updates).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                                        ft.detach(Food_list_fragment.this).attach(Food_list_fragment.this).commit();
                                        final Handler handler = new Handler();
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                myFirestoreList.setAdapter(adapter);
                                                swipeRefreshLayout.setRefreshing(false);
                                            }
                                        }, 500);
                                        Log.d("success","good");

                                    }
                                });
                                //레시피 갱신
                                dialog.dismiss();
                            }
                        });
                        delete_builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        AlertDialog sucessAlert = delete_builder.create();
                        sucessAlert.show();
                        return true;
                    }
                });


                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int pos = getAdapterPosition();
                        if (pos != RecyclerView.NO_POSITION){
                            Intent intent = new Intent(getActivity(), Food_info_Activity.class);
                            intent.putExtra("name",mData.get(pos)[0]);
                            intent.putExtra("num",mData.get(pos)[1]);
                            startActivity(intent);
                            notifyItemChanged(pos);
                        }

                    }
                });
            }
        }

        // 생성자에서 데이터 리스트 객체를 전달받음.
        RecyclerAdapter(ArrayList<String[]> list) {
            mData = list ;
        }

        // onCreateViewHolder() - 아이템 뷰를 위한 뷰홀더 객체 생성하여 리턴.
        @Override
        public RecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Context context = parent.getContext() ;
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;

            View view = inflater.inflate(R.layout.list_item_single, parent, false) ;
            RecyclerAdapter.ViewHolder vh = new RecyclerAdapter.ViewHolder(view) ;

            return vh ;
        }

        // onBindViewHolder() - position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시.
        @Override
        public void onBindViewHolder(RecyclerAdapter.ViewHolder holder, int position) {
            String name = "이름 : "+mData.get(position)[0] ;
            String num = "수량 : "+mData.get(position)[1] ;

            holder.name_text.setText(name) ;
            holder.num_text.setText(num) ;
        }

        // getItemCount() - 전체 데이터 갯수 리턴.
        @Override
        public int getItemCount() {
            return mData.size() ;
        }
    }
}

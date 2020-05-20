package com.example.srt_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

public class SelectRefPageActivity extends AppCompatActivity {

    Button download;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    StorageReference ref;

    private FloatingActionButton floatingActionButton;
    private Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_page);
        mContext = this;

        // 50-58 add
        download = findViewById(R.id.download);

        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                download();
            }
        });


        Bundle bundle = getIntent().getExtras();
        if(bundle.getString("update").equals("false")){
            Toast.makeText(mContext,"더 이상 등록할 수 없습니다.",Toast.LENGTH_SHORT).show();
        }
        else if (bundle.getString("update").equals("false1")){
            Toast.makeText(mContext,"이미 등록되어 있습니다.",Toast.LENGTH_SHORT).show();
        }
        else if (bundle.getString("update").equals("successIntro")){
            Toast.makeText(mContext,"로딩 성공",Toast.LENGTH_SHORT).show();
        }
        else if (bundle.getString("update").equals("true")){
            Toast.makeText(mContext,"등록 성공",Toast.LENGTH_SHORT).show();
        }

        RecyclerView ref_view = findViewById(R.id.ref_list);
        ArrayList<String> list = new ArrayList<>();

        if(!SharedPref_id.getString(mContext,"myid0").equals("")){
            list.add(SharedPref_id.getString(mContext, "myid0"));
        }
        if(!SharedPref_id.getString(mContext,"myid1").equals("")){
            list.add(SharedPref_id.getString(mContext, "myid1"));
        }
        if(!SharedPref_id.getString(mContext,"myid2").equals("")){
            list.add(SharedPref_id.getString(mContext, "myid2"));
        }
        if(!SharedPref_id.getString(mContext,"myid3").equals("")){
            list.add(SharedPref_id.getString(mContext, "myid3"));
        }

        ref_view.setLayoutManager(new GridLayoutManager(this,2)) ;
        SimpleTextAdapter adapter = new SimpleTextAdapter(list) ;
        ref_view.setAdapter(adapter) ;

        floatingActionButton=findViewById(R.id.ref_floating);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectRefPageActivity.this, QR_rec_Activity.class);
                startActivity(intent);
            }
        });
    }


    public class SimpleTextAdapter extends RecyclerView.Adapter<SimpleTextAdapter.ViewHolder> {

        private ArrayList<String> mData = null ;

        // 아이템 뷰를 저장하는 뷰홀더 클래스.
        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView textView1 ;

            ViewHolder(View itemView) {
                super(itemView) ;

                // 뷰 객체에 대한 참조. (hold strong reference)
                textView1 = itemView.findViewById(R.id.ref_text) ;

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        int pos = getAdapterPosition();
                        if (pos != RecyclerView.NO_POSITION){
                            Intent intent = new Intent(SelectRefPageActivity.this, HomeActivity.class);
                            SharedPref_id.setString(mContext, "ThisRef", mData.get(pos));
                            startActivity(intent);
                            notifyItemChanged(pos);
                        }
                    }
                });
            }
        }

        // 생성자에서 데이터 리스트 객체를 전달받음.
        SimpleTextAdapter(ArrayList<String> list) {
            mData = list ;
        }

        // onCreateViewHolder() - 아이템 뷰를 위한 뷰홀더 객체 생성하여 리턴.
        @Override
        public SimpleTextAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Context context = parent.getContext() ;
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;

            View view = inflater.inflate(R.layout.list_ref, parent, false) ;
            SimpleTextAdapter.ViewHolder vh = new SimpleTextAdapter.ViewHolder(view) ;

            return vh ;
        }

        // onBindViewHolder() - position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시.
        @Override
        public void onBindViewHolder(SimpleTextAdapter.ViewHolder holder, int position) {
            String text = mData.get(position) ;
            holder.textView1.setText(text) ;
        }

        // getItemCount() - 전체 데이터 갯수 리턴.
        @Override
        public int getItemCount() {
            return mData.size() ;
        }
    }

    public void download(){

        storageReference = firebaseStorage.getInstance().getReference();
        ref = storageReference.child("result/food.txt");

        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String url = uri.toString();
                downloadFile(SelectRefPageActivity.this, "food", "txt", DIRECTORY_DOWNLOADS,url);


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    public void downloadFile(SelectRefPageActivity context, String fileName, String fileExtension, String destinationDirectory, String url){
        DownloadManager downloadManager = (DownloadManager) context.
                getSystemService(HomeActivity.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);

        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalFilesDir(context, destinationDirectory, fileName + fileExtension);

        downloadManager.enqueue(request);

    }


}



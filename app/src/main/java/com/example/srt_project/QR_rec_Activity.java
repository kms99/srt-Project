package com.example.srt_project;

import androidx.appcompat.app.AppCompatActivity;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.widget.TextView;
import android.widget.Toast;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import github.nisrulz.qreader.QRDataListener;
import github.nisrulz.qreader.QREader;

public class QR_rec_Activity extends AppCompatActivity {
    private SurfaceView surfaceView;
    private QREader qrEader;
    private String id_value;
    private TextView txt_result;
    private Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_q_r_rec_);
        mContext = this;
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.CAMERA)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        setupCamera();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        Toast.makeText(QR_rec_Activity.this,"You must enable this permission",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                    }
                }).check();
    }

    private void setupCamera() {
        surfaceView = (SurfaceView)findViewById(R.id.cameraView);
        setupQREader();

    }

    private void setupQREader() {
        qrEader = new QREader.Builder(this, surfaceView, new QRDataListener() {
            @Override
            public void onDetected(String data) {
                FindFireStore findFireStore = new FindFireStore();
                boolean check = findFireStore.getCollection(data);
                if (check == true) {
                    if (SharedPref_id.getString(mContext, "myid0").equals("")) {
                        SharedPref_id.setString(mContext, "myid0", data);
                        Intent intent = new Intent(QR_rec_Activity.this, SelectRefPageActivity.class);
                        intent.putExtra("update",true);
                        startActivity(intent);
                        finish();
                    }
                    else if (SharedPref_id.getString(mContext, "myid1").equals("")) {
                        boolean check_qr=false;
                        for(int i = 0; i<1; i ++){
                            if(SharedPref_id.getString(mContext, "myid"+i).equals(data)){
                                check_qr=true;
                                break;
                            }
                        }
                        if(check_qr==true){
                            Intent intent = new Intent(QR_rec_Activity.this, SelectRefPageActivity.class);
                            intent.putExtra("update","false1");
                            startActivity(intent);
                            finish();
                        }else {
                            SharedPref_id.setString(mContext, "myid1", data);
                            Intent intent = new Intent(QR_rec_Activity.this, SelectRefPageActivity.class);
                            intent.putExtra("update", "true");
                            startActivity(intent);
                            finish();
                        }
                    }

                    else if (SharedPref_id.getString(mContext, "myid2").equals("")) {
                        boolean check_qr=false;
                        for(int i = 0; i<2; i ++){
                            if(SharedPref_id.getString(mContext, "myid"+i).equals(data)){
                                check_qr=true;
                                break;
                            }
                        }
                        if(check_qr==true){
                            Intent intent = new Intent(QR_rec_Activity.this, SelectRefPageActivity.class);
                            intent.putExtra("update","false1");
                            startActivity(intent);
                            finish();
                        }else {
                            SharedPref_id.setString(mContext, "myid2", data);
                            Intent intent = new Intent(QR_rec_Activity.this, SelectRefPageActivity.class);
                            intent.putExtra("update", "true");
                            startActivity(intent);
                            finish();
                        }
                    }

                    else if (SharedPref_id.getString(mContext, "myid3").equals("")) {
                        boolean check_qr=false;
                        for(int i = 0; i<3; i ++){
                            if(SharedPref_id.getString(mContext, "myid"+i).equals(data)){
                                check_qr=true;
                                break;
                            }
                        }
                        if(check_qr==true){
                            Intent intent = new Intent(QR_rec_Activity.this, SelectRefPageActivity.class);
                            intent.putExtra("update","false1");
                            startActivity(intent);
                            finish();
                        }else {
                            SharedPref_id.setString(mContext, "myid3", data);
                            Intent intent = new Intent(QR_rec_Activity.this, SelectRefPageActivity.class);
                            intent.putExtra("update", "true");
                            startActivity(intent);
                            finish();
                        }
                    }
                    else{
                        Intent intent = new Intent(QR_rec_Activity.this, SelectRefPageActivity.class);
                        intent.putExtra("update","false");
                        startActivity(intent);
                        finish();
                    }
                }
            }

        }).facing(QREader.BACK_CAM)
                .enableAutofocus(true)
                .height(surfaceView.getHeight())
                .width(surfaceView.getWidth())
                .build();


    }

    @Override
    protected void onResume() {
        super.onResume();
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.CAMERA)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        if(qrEader != null){
                            qrEader.initAndStart(surfaceView);
                        }

                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        Toast.makeText(QR_rec_Activity.this,"You must enable this permission",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                    }
                }).check();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.CAMERA)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        if(qrEader != null){
                            qrEader.releaseAndCleanup();
                        }
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        Toast.makeText(QR_rec_Activity.this,"You must enable this permission",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                    }
                }).check();
    }
}



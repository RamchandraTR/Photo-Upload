package com.example.photoupload;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.zxing.Result;

import java.io.File;
import java.io.IOException;

public class ScanerActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private ZXingScannerView zXingScannerView;
    private static final int PERMISSION_REQUEST_CODE = 1;
    private static  final int pic_id = 123;
    String currentImagePath = null;
    String picName = null;
    Sharedpref sharedpref;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scaner);
        sharedpref = new Sharedpref(this);
        ViewGroup contentFrame = (ViewGroup) findViewById(R.id.content_frame);
        zXingScannerView = new ZXingScannerView(this);
        contentFrame.addView(zXingScannerView);
        if(ActivityCompat.checkSelfPermission(ScanerActivity.this,Manifest.permission.CAMERA)!=PackageManager.PERMISSION_GRANTED){
            checkpermission();
        }

    }

    @Override
    public void handleResult(final Result result) {
        //picName = result.getText();
        sharedpref.setImagename(result.getText());
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("Employee verification")
                .setMessage("Is this "+ result.getText())
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        captureImage(result.getText());

                    }
                });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = getIntent();
                finish();
                startActivity(intent);

            }
        });
        builder.show();

        //Toast.makeText(getApplicationContext(),result.getText().toString(),Toast.LENGTH_LONG).show();

    }
    @Override
    protected void onPause() {
        super.onPause();
        zXingScannerView.stopCamera();
    }

    @Override
    protected void onResume() {
        super.onResume();
        zXingScannerView.setResultHandler(this);
        zXingScannerView.startCamera();
    }
    public void checkpermission(){
        if(ActivityCompat.checkSelfPermission(this,Manifest.permission.CAMERA)!=PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(ScanerActivity.this,Manifest.permission.CAMERA)){
                AlertDialog.Builder builder=new AlertDialog.Builder(this)
                        .setTitle("camera")
                        .setMessage("camera")
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(ScanerActivity.this,new String[]
                                        {Manifest.permission.CAMERA},PERMISSION_REQUEST_CODE);

                            }
                        });
                builder.show();
            }else {
                ActivityCompat.requestPermissions(ScanerActivity.this,new String[]
                        {Manifest.permission.CAMERA},PERMISSION_REQUEST_CODE);

            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case PERMISSION_REQUEST_CODE :{
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    if(ContextCompat.checkSelfPermission(ScanerActivity.this,Manifest.permission.CAMERA)==PackageManager.PERMISSION_GRANTED){

                    }else {
                        checkpermission();
                    }
                }

            }
        }
    }
    public void captureImage(String name){
        Log.d("NAME",name);
        File imageFile= null;
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        //if(intent.resolveActivity(getPackageManager())!=null){

            try {
                imageFile = getImageFile(name);

                Log.d("IMAGEFILE",imageFile.toString());
            } catch (IOException e) {
                Log.d("ERROR",e.toString());
                e.printStackTrace();
            }
            if(imageFile!=null)
            {
                Uri imageUri = FileProvider.getUriForFile(this,"com.example.android.fileprovider",imageFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
                startActivityForResult(intent,pic_id);

            }






        //startActivityForResult(intent,pic_id);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == pic_id){
            if(resultCode == RESULT_OK){
                //Bitmap bitmap = (Bitmap)data.getExtras().get("data");
                Intent intent = new Intent(this,ImageActivity.class);
                intent.putExtra("imagepath",currentImagePath);
                intent.putExtra("picname",picName);
                Log.d("CURRENTPATH",currentImagePath.toString());
                startActivity(intent);


            }
            else {
                Toast.makeText(getApplicationContext(),"cancelled",Toast.LENGTH_LONG).show();
            }
        }

    }

    private File getImageFile(String name) throws IOException {

         String imageName = "jpg_"+name;
         File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

         File imageFile = File.createTempFile(imageName,".jpg",storageDir);
         currentImagePath = imageFile.getAbsolutePath();
         return imageFile;
    }
}




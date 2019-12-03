package com.example.photoupload;

import androidx.appcompat.app.AppCompatActivity;
import okhttp3.OkHttpClient;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class ImageActivity extends AppCompatActivity {
    ImageView imageView;
    Button upload, cancel;
    Bitmap bitmap;
    Sharedpref sharedpref;
    ProgressBar progressBar;
    private  final static String Baseurl = "http://192.168.43.122/trproject/connection.php";
    private String picname=null;
    OkHttpClient okHttpClient=new OkHttpClient().newBuilder()
            .readTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(120,TimeUnit.SECONDS)
            .connectTimeout(120,TimeUnit.SECONDS)
            .build();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        AndroidNetworking.initialize(getApplicationContext());
        sharedpref= new Sharedpref(this);
        imageView = findViewById(R.id.imageView);
        progressBar = findViewById(R.id.progressBar2);
        upload = findViewById(R.id.button);
        cancel = findViewById(R.id.button3);
        Intent intent = getIntent();
        picname = intent.getStringExtra("picname");
        String path =  intent.getStringExtra("imagepath");
         bitmap = BitmapFactory.decodeFile(path);
        try {
            ExifInterface exif = new ExifInterface(path);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
            Log.d("EXIF", "Exif: " + orientation);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
            }
            else if (orientation == 3) {
                matrix.postRotate(180);
            }
            else if (orientation == 8) {
                matrix.postRotate(270);
            }
            bitmap = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);

        } catch (IOException e) {
            e.printStackTrace();
        }
        imageView.setImageBitmap(bitmap);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent1 = new Intent(getApplicationContext(),ScanerActivity.class);
                startActivity(intent1);
                finish();
            }
        });

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upload(bitmap);
            }
        });
    }
    public void upload(Bitmap bitmap)
    {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
         bitmap.compress(Bitmap.CompressFormat.JPEG,100,outputStream);
         byte[] bytes = outputStream.toByteArray();
         String encoded = Base64.encodeToString(bytes,Base64.DEFAULT);
         Log.d("encode",encoded);
         Log.d("picname",sharedpref.getImagename());
        AndroidNetworking.post(Baseurl)
                .addBodyParameter("name",sharedpref.getImagename())
                .addBodyParameter("image",encoded)
                .setPriority(Priority.MEDIUM)
                .setOkHttpClient(okHttpClient)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("response",response.toString());
                    }

                    @Override
                    public void onError(ANError anError) {

                        Log.d("error",anError.toString());

                    }
                });




    }

}

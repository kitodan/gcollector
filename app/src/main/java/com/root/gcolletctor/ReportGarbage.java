package com.root.gcolletctor;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.kosalgeek.android.photoutil.CameraPhoto;
import com.kosalgeek.android.photoutil.GalleryPhoto;
import com.kosalgeek.android.photoutil.ImageBase64;
import com.kosalgeek.android.photoutil.ImageLoader;
import com.kosalgeek.genasync12.AsyncResponse;
import com.kosalgeek.genasync12.EachExceptionsHandler;
import com.kosalgeek.genasync12.PostResponseAsyncTask;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.util.HashMap;

public class ReportGarbage extends AppCompatActivity {
    private final String TAG=this.getClass().getName();

    ImageView ivCamera, ivGallery, ivUpload,ivImage;
    CameraPhoto cameraPhoto;
    GalleryPhoto galleryPhoto;
    final int CAMERA_REQUEST = 13323;
    final int GALLERY_REQUEST=22131;
    String selectedPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_garbage);

        cameraPhoto = new CameraPhoto(getApplicationContext());
        galleryPhoto=new GalleryPhoto(getApplicationContext());

        ivCamera = (ImageView) findViewById(R.id.ivCamera);
        ivImage = (ImageView) findViewById(R.id.ivImage);
        ivGallery = (ImageView) findViewById(R.id.ivGallery);
        ivUpload = (ImageView) findViewById(R.id.ivUpload);

        ivCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                try {

                    startActivityForResult(cameraPhoto.takePhotoIntent(), CAMERA_REQUEST);
                    cameraPhoto.addToGallery();
                } catch (IOException e) {
                    Toast.makeText(getApplicationContext(),
                            "Something wrong while taking the photo", Toast.LENGTH_SHORT).show();
                }
            }
        });
        ivGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(galleryPhoto.openGalleryIntent(),GALLERY_REQUEST);

            }
        });
        ivUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Bitmap bitmap=ImageLoader.init().from(selectedPhoto).requestSize(1024,1024).getBitmap();
                    String encodedImage = ImageBase64.encode(bitmap);
                    Log.d(TAG, encodedImage);

                    HashMap<String, String> postData = new HashMap<String, String>();
                    postData.put("image",encodedImage);

                    PostResponseAsyncTask task= new PostResponseAsyncTask(getApplicationContext(), postData, new AsyncResponse() {
                        @Override
                        public void processFinish(String s) {
                            if (s.contains("uploaded_success")){
                                Toast.makeText(getApplicationContext(),"Image Uploaded Successfully.",
                                        Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Toast.makeText(getApplicationContext(),"Error While Uploading.",
                                        Toast.LENGTH_SHORT).show();

                            }

                        }
                    });
                    task.execute("https://files.000webhost.com/upload.php");
                    task.setEachExceptionsHandler(new EachExceptionsHandler() {
                        @Override
                        public void handleIOException(IOException e) {
                            Toast.makeText(getApplicationContext(), "Cannot connect to server.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void handleMalformedURLException(MalformedURLException e) {
                            Toast.makeText(getApplicationContext(), "URL Error.",
                                    Toast.LENGTH_SHORT).show();

                        }

                        @Override
                        public void handleProtocolException(ProtocolException e) {
                            Toast.makeText(getApplicationContext(), "Protocal Error.",
                                    Toast.LENGTH_SHORT).show();

                        }

                        @Override
                        public void handleUnsupportedEncodingException(UnsupportedEncodingException e) {
                            Toast.makeText(getApplicationContext(), "Encoding Error.",
                                    Toast.LENGTH_SHORT).show();

                        }
                    });

                } catch (FileNotFoundException e) {
                    Toast.makeText(getApplicationContext(),
                            "Something wrong while encoding the photo", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode==CAMERA_REQUEST){
                String photoPath=cameraPhoto.getPhotoPath();
                selectedPhoto=photoPath;
                Bitmap bitmap=null;
                try {
                    bitmap= ImageLoader.init().from(photoPath).requestSize(512,512).getBitmap();
                    ivImage.setImageBitmap(bitmap);

                } catch (FileNotFoundException e) {
                    Toast.makeText(getApplicationContext(),
                            "Something wrong while loading the photo", Toast.LENGTH_SHORT).show();
                }

            }
            else if(requestCode == GALLERY_REQUEST){
                Uri uri=data.getData();
                galleryPhoto.setPhotoUri(uri);
                String photoPath=galleryPhoto.getPath();
                selectedPhoto=photoPath;
                try {
                    Bitmap bitmap= ImageLoader.init().from(photoPath).requestSize(512,512).getBitmap();
                    ivImage.setImageBitmap(bitmap);

                } catch (FileNotFoundException e) {
                    Toast.makeText(getApplicationContext(),
                            "Something wrong while choosing the photo", Toast.LENGTH_SHORT).show();
                }
            }

        }
    }

}





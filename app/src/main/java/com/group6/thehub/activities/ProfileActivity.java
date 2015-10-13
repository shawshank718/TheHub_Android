package com.group6.thehub.activities;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.group6.thehub.R;
import com.group6.thehub.Rest.models.UserDetails;
import com.group6.thehub.Rest.responses.UserResponse;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import retrofit.mime.TypedFile;

public class ProfileActivity extends AppCompatActivity implements UserResponse.ImageUploadResponseListener {

    private Toolbar toolbar;

    private CollapsingToolbarLayout collapsingToolbarLayout;

    private UserDetails userDetails;

    private final int GALLERY_ACTIVITY_CODE = 200;

    private final int RESULT_CROP = 400;

    private String picturePath;

    public static String cropImagePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/TheHub/";

    public String finalPath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        toolbar = (Toolbar) findViewById(R.id.anim_toolbar);
        setSupportActionBar(toolbar);

        userDetails = UserResponse.getUserDetails(this);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle(userDetails.getFirstName()+ " " +userDetails.getLastName());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

//        Intent gallery_Intent = new Intent(getApplicationContext(), GalleryUtil.class);
//        startActivityForResult(gallery_Intent, GALLERY_ACTIVITY_CODE);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id==android.R.id.home) {
           goBack();
        }

        return super.onOptionsItemSelected(item);
    }

    private void goBack() {
        finish();
        overridePendingTransition(0,R.anim.push_right_out);
    }

    @Override
    public void onBackPressed() {
        goBack();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_ACTIVITY_CODE) {
            if(resultCode == AppCompatActivity.RESULT_OK){
                picturePath = data.getStringExtra("picturePath");
                //perform Crop on the Image Selected from Gallery
                performCrop(picturePath);
            }
        }

        if (requestCode == RESULT_CROP ) {
            if(resultCode == AppCompatActivity.RESULT_OK){
                Uri uri = data.getData();
                File f = new File(uri.getPath());
                Toast.makeText(this, "Image saved to -"+f.getAbsolutePath(), Toast.LENGTH_SHORT).show();
                prepareImageForUpload(finalPath);
//                if (selectedBitmap!= null) {
//                    saveImageToExternalStorage(selectedBitmap);
//                } else {
//                    Toast.makeText(this, "File couldn't be cropped", Toast.LENGTH_SHORT).show();
//                }

            }
        }
    }

    private void performCrop(String picUri) {
        Bitmap bmp = null;
        try {
            //Start Crop Activity

            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            // indicate image type and Uri
            File f = new File(picUri);
            Uri contentUri = Uri.fromFile(f);
            bmp = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentUri);

            int width=bmp.getWidth();
            int height=bmp.getHeight();

            cropIntent.setDataAndType(contentUri, "image/*");
            // set crop properties
            cropIntent.putExtra("crop", "true");
            // indicate aspect of desired crop
            cropIntent.putExtra("aspectX", 16);
            cropIntent.putExtra("aspectY", 9);
            // indicate output X and Y

            cropIntent.putExtra("outputX", 854);
            cropIntent.putExtra("outputY", 480);
            boolean crop = true;
            if(width < 1024 || height < 576){
                if(width <= height){
                    cropIntent.putExtra("outputX", (height*16)/9);
                    cropIntent.putExtra("outputY", height);
                    Log.v("cropImage", "outputX=" + width + " outputY=" + width);
                }else{
                    cropIntent.putExtra("outputX", (width*9)/16);
                    cropIntent.putExtra("outputY", width);
                    Log.v("cropImage","outputX="+height+" outputY="+height);
                }
            }else{
                if(width < 854 || height < 480) {

                    Toast.makeText(this, "Image is too small. Please choose another image.", Toast.LENGTH_LONG).show();
                    crop = false;
                } else {
                    cropIntent.putExtra("outputX", 854);
                    cropIntent.putExtra("outputY", 480);
                }
            }

            if (crop) {
                // retrieve data on return
                finalPath = cropImagePath+System.currentTimeMillis()+".jpg";
                File file = new File(finalPath);
                cropIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                cropIntent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
                cropIntent.putExtra("return-data", false);
                //saveImageToExternalStorage(Bitmap.createScaledBitmap(bmp, 854, 480, true));
                // start the activity - we handle returning in onActivityResult
                startActivityForResult(cropIntent, RESULT_CROP);
            }

        }
        // respond to users whose devices do not support the crop action
        catch (ActivityNotFoundException anfe) {
            // display an error message
            String errorMessage = "your device doesn't support the crop action! The full image will be uploaded.";
            Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
            toast.show();
            prepareImageForUpload(picUri);
        } catch (FileNotFoundException e) {
            Toast toast = Toast.makeText(this, "File not found", Toast.LENGTH_SHORT);
            toast.show();
        } catch (IOException e) {
            Toast toast = Toast.makeText(this, "File not found", Toast.LENGTH_SHORT);
            toast.show();
        }
    }


    public void prepareImageForUpload(String path) {

        File file = new File(path);
        UserResponse.uploadImageToServer(this, new TypedFile("image/*", file), userDetails.getUserId());

    }

    @Override
    public void onImageUpload(UserDetails userDetails) {
        this.userDetails = userDetails;
        Toast.makeText(this, R.string.image_success, Toast.LENGTH_LONG).show();
        UserResponse.saveUserDetails(this, userDetails);

    }

    @Override
    public void onFail(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}

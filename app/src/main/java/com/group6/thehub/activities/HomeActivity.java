package com.group6.thehub.activities;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.group6.thehub.AppHelper;
import com.group6.thehub.R;
import com.group6.thehub.Rest.models.UserDetails;
import com.group6.thehub.Rest.responses.UserResponse;
import com.quinny898.library.persistentsearch.SearchBox;
import com.quinny898.library.persistentsearch.SearchResult;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;


import de.hdodenhof.circleimageview.CircleImageView;
import retrofit.mime.TypedFile;


public class HomeActivity extends AppCompatActivity implements View.OnClickListener, UserResponse.ImageUploadResponseListener {

    private static final String LOG_TAG = "HomeActivity";

    private AppHelper appHelper;

    private DrawerLayout mDrawerLayout;

    private ActionBarDrawerToggle mDrawerToggle;
    private NavigationView navigationView;

    private CircleImageView profileImgView;

    private TextView tvEmail;

    private TextView tvName;

    private final int GALLERY_ACTIVITY_CODE=200;

    private final int RESULT_CROP = 400;

    private String picturePath;
    private UserDetails userDetails;

    public static String cropImagePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/TheHub/";

    public String finalPath = "";

    private  Toolbar toolbar;

    private SearchBox search;

    private ImageView imgTint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        appHelper = new AppHelper(this);
        toolbar = (Toolbar) findViewById(R.id.appBar);
        setSupportActionBar(toolbar);
        search = (SearchBox) findViewById(R.id.searchbox);
        setUpSearchBox();
        imgTint = (ImageView) findViewById(R.id.imgTint);
        userDetails = UserResponse.getUserDetails(this);
        if (userDetails != null) {
            setTitle("Hi " +userDetails.getFirstName());
        }

        //Initializing NavigationView
        navigationView = (NavigationView) findViewById(R.id.navigation_view);

        profileImgView = (CircleImageView) navigationView.findViewById(R.id.profile_image);
        tvEmail = (TextView) navigationView.findViewById(R.id.tvEmail);
        tvName = (TextView) navigationView.findViewById(R.id.tvName);

        if(tvName != null) {
            tvEmail.setText(userDetails.getEmail());
            tvName.setText(userDetails.getFirstName()+" "+userDetails.getLastName());

        }

        if (userDetails.getImage().getImageUrl() == null) {
            profileImgView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    //Start Activity To Select Image From Gallery
                    Intent gallery_Intent = new Intent(getApplicationContext(), GalleryUtil.class);
                    startActivityForResult(gallery_Intent, GALLERY_ACTIVITY_CODE);

                }
            });
        } else {
            Picasso.with(this).load(appHelper.END_POINT+userDetails.getImage().getImageUrl()).into(profileImgView);
        }

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                //Checking if the item is in checked state or not, if not make it in checked state
                if(menuItem.isChecked()) menuItem.setChecked(false);
                else menuItem.setChecked(true);

                //Closing drawer on item click
                mDrawerLayout.closeDrawers();

                switch (menuItem.getItemId()) {

                    case R.id.profile:
                        return true;
                    case R.id.logout:
                        UserResponse.callLogout(getApplicationContext());
                        Intent intent = new Intent(getApplicationContext(), SignInSignUpActivity.class);
                        appHelper.slideDownPushDown(intent);
                        finish();
                        return true;

                    default:
                        return true;
                }
            }
        });

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle =new ActionBarDrawerToggle(this, mDrawerLayout, toolbar,R.string.drawer_open, R.string.drawer_close){
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
//                if (!mUserLearnedDrawer) {
//                    mUserLearnedDrawer = true;
//                    appHelper.saveToUserPrefs(USER_LEARNED_DRAWER, mUserLearnedDrawer+"");
//                }
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };

        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

        mDrawerLayout.setDrawerListener(mDrawerToggle);


//        drawerFragment = (NavigationDrawerFragment) getSupportFragmentManager()
//                .findFragmentById(R.id.fragment_navigation_drawer);
//
//        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), toolbar, userDetails);




    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_search) {
            openSearch();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void setUpSearchBox() {
        search.setSearchListener(new SearchBox.SearchListener() {
            @Override
            public void onSearchOpened() {
                imgTint.setVisibility(View.VISIBLE);
            }

            @Override
            public void onSearchCleared() {

            }

            @Override
            public void onSearchClosed() {
                closeSearch();
            }

            @Override
            public void onSearchTermChanged() {

            }

            @Override
            public void onSearch(String s) {

            }
        });
    }

    protected void closeSearch() {
        search.hideCircularly(this);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                imgTint.setVisibility(View.GONE);
                toolbar.setVisibility(View.VISIBLE);
            }
        }, 500);
    }

    public void openSearch() {
        toolbar.setVisibility(View.GONE);
        search.revealFromMenuItem(R.id.action_search, this);
        for (int x = 0; x < 10; x++) {
            SearchResult option = new SearchResult("Result "
                    + Integer.toString(x), ContextCompat.getDrawable(this, R.drawable.ic_history_black_24dp));
            search.addSearchable(option);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

        }
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
                    Log.v("cropImage","outputX="+width+" outputY="+width);
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
        Picasso.with(this).load(appHelper.END_POINT+userDetails.getImage().getImageUrl()).into(profileImgView);
        profileImgView.setClickable(false);

    }

    @Override
    public void onFail(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}

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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.group6.thehub.AppHelper;
import com.group6.thehub.R;
import com.group6.thehub.Rest.models.Course;
import com.group6.thehub.Rest.models.Language;
import com.group6.thehub.Rest.models.UserDetails;
import com.group6.thehub.Rest.responses.UserResponse;
import com.group6.thehub.views.AspectRatioImageView;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import retrofit.mime.TypedFile;

public class ProfileActivity extends AppCompatActivity implements UserResponse.ImageUploadResponseListener, View.OnClickListener, UserResponse.UserDetailsQueryListener {

    private Toolbar toolbar;

    private CollapsingToolbarLayout collapsingToolbarLayout;

    private UserDetails userDetails;

    private final int GALLERY_ACTIVITY_CODE = 200;

    private final int RESULT_CROP = 400;

    private String picturePath;

    public static String cropImagePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/TheHub/";

    public String finalPath = "";

    private AspectRatioImageView imgHeader;
    private ImageButton imgEdit;
    private TextView tvQualValue;
    private EditText etQualValue;
    private LinearLayout lilyCourses;
    private AutoCompleteTextView etCourseValue;
    private LinearLayout lilyLanguages;
    private AutoCompleteTextView etLangValue;
    private TextView tvPhoneValue;
    private EditText etPhoneValue;
    private TextView tvEmailValue;
    private EditText etEmailValue;

    private String qualification;
    private String email;
    private String phone;
    private boolean inEditMode;
    private boolean isMine = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        toolbar = (Toolbar) findViewById(R.id.anim_toolbar);
        setSupportActionBar(toolbar);

        userDetails = UserResponse.getUserDetails(this);

        int userId = getIntent().getIntExtra("userId", -1);
        if (userId == userDetails.getUserId()) {
            isMine =true;
            invalidateOptionsMenu();
        } else {
            UserResponse.retrieveUserDetails(this, userId);
        }


        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle(userDetails.getFirstName() + " " + userDetails.getLastName());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        imgHeader = (AspectRatioImageView) findViewById(R.id.img_header);
        imgEdit = (ImageButton) findViewById(R.id.img_edit);
        tvQualValue = (TextView) findViewById(R.id.tvQualValue);
        etQualValue = (EditText) findViewById(R.id.etQualValue);
        lilyCourses = (LinearLayout) findViewById(R.id.lilyCourses);
        etCourseValue = (AutoCompleteTextView) findViewById(R.id.etCourseValue);
        lilyLanguages = (LinearLayout) findViewById(R.id.lilyLanguages);
        etLangValue = (AutoCompleteTextView) findViewById(R.id.etLangValue);
        tvPhoneValue = (TextView) findViewById(R.id.tvPhoneValue);
        etPhoneValue = (EditText) findViewById(R.id.etPhoneValue);
        tvEmailValue = (TextView) findViewById(R.id.tvEmailValue);
        etEmailValue = (EditText) findViewById(R.id.etEmailValue);

        imgEdit.setOnClickListener(this);

        setupDetails(userDetails);

    }

    private void setupDetails(UserDetails userDetails) {
        Picasso.with(this).setLoggingEnabled(true);
        Picasso.with(this).load(AppHelper.END_POINT+userDetails.getImage().getImageUrl()).placeholder(R.drawable.bg_signup_signin).error(R.drawable.bg_signup_signin).into(imgHeader);
        if (userDetails.getQualification() == null) {
            tvQualValue.setVisibility(View.GONE);
        } else {
            tvQualValue.setText(userDetails.getQualification());
        }
        addLanguages(userDetails.getLanguages());
        addCourses(userDetails.getCourses());
        tvEmailValue.setText(userDetails.getEmail());
    }

    private void addCourses(ArrayList<Course> courses) {
        if (!courses.isEmpty()) {

            for (Course course: courses) {
                addCourseItem(course);
            }
        }
    }

    private void addCourseItem(Course course) {
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.course_item, lilyCourses, false);
        CourseItemViewHolder holder = new CourseItemViewHolder();
        holder.tvItemValue = (TextView) view.findViewById(R.id.tvItemValue);
        holder.imgClear = (ImageButton) view.findViewById(R.id.imgClear);
        holder.imgClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("ProfileActivity", "clear Clicked");
                lilyCourses.removeView((View) v.getParent());
            }
        });
        if (inEditMode) {
            holder.imgClear.setVisibility(View.VISIBLE);
        }
        holder.tvItemValue.setText(course.getCourseCode()+": "+course.getCourseName());
        view.setTag(holder);
        lilyCourses.addView(view);
    }

    private void addLanguages(ArrayList<Language> languages) {
        if (!languages.isEmpty()) {

            for (Language language: languages) {
                addLanguageItem(language);
            }
        }
    }

    private void addLanguageItem(Language language) {
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.course_item, lilyCourses, false);
        CourseItemViewHolder holder = new CourseItemViewHolder();
        holder.tvItemValue = (TextView) view.findViewById(R.id.tvItemValue);
        holder.imgClear = (ImageButton) view.findViewById(R.id.imgClear);
        holder.imgClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("ProfileActivity", "clear Clicked");
                lilyLanguages.removeView((View) v.getParent());
            }
        });
        if (inEditMode) {
            holder.imgClear.setVisibility(View.VISIBLE);
        }
        holder.tvItemValue.setText(language.getEnglishName());
        view.setTag(holder);
        lilyLanguages.addView(view);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile, menu);

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        if (isMine) {
            menu.getItem(0).setVisible(false);
            menu.getItem(1).setVisible(true);
            menu.getItem(2).setVisible(false);
        } else {
            menu.getItem(0).setVisible(true);
            menu.getItem(1).setVisible(false);
            menu.getItem(2).setVisible(false);
            return true;
        }

        if (inEditMode) {
            menu.getItem(0).setVisible(false);
            menu.getItem(1).setVisible(false);
            menu.getItem(2).setVisible(true);
            return true;
        }



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

        if (id == R.id.action_edit) {
            showEditMode();
        }

        return super.onOptionsItemSelected(item);
    }

    private void showEditMode() {
        inEditMode = true;
        invalidateOptionsMenu();

        qualification = tvQualValue.getText().toString();
        phone = tvPhoneValue.getText().toString();
        email = tvEmailValue.getText().toString();

        imgEdit.setVisibility(View.VISIBLE);
        tvQualValue.setVisibility(View.GONE);
        etQualValue.setVisibility(View.VISIBLE);
        etQualValue.setText(qualification);
        for (int i = 0; i < lilyCourses.getChildCount(); i++) {
            View view = lilyCourses.getChildAt(i);
            CourseItemViewHolder holder = (CourseItemViewHolder) view.getTag();
            holder.imgClear.setVisibility(View.VISIBLE);
        }
        etCourseValue.setVisibility(View.VISIBLE);
        for (int i = 0; i < lilyLanguages.getChildCount(); i++) {
            View view = lilyLanguages.getChildAt(i);
            CourseItemViewHolder holder = (CourseItemViewHolder) view.getTag();
            holder.imgClear.setVisibility(View.VISIBLE);
        }
        etLangValue.setVisibility(View.VISIBLE);
        tvPhoneValue.setVisibility(View.GONE);
        tvEmailValue.setVisibility(View.GONE);
        etPhoneValue.setVisibility(View.VISIBLE);
        etPhoneValue.setText(phone);
        etEmailValue.setVisibility(View.VISIBLE);
        etEmailValue.setText(email);
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
//                Toast.makeText(this, "Image saved to -"+f.getAbsolutePath(), Toast.LENGTH_SHORT).show();
                prepareImageForUpload(finalPath);
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
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            // indicate output X and Y
            boolean crop = true;
            if(width < 500 || height < 500) {
                Toast.makeText(this, "Image is too small. Please choose another image.", Toast.LENGTH_LONG).show();
                crop = false;
            } else {
                cropIntent.putExtra("outputX", 500);
                cropIntent.putExtra("outputY", 500);
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
        Picasso.with(this).load(AppHelper.END_POINT+userDetails.getImage().getImageUrl()).placeholder(R.drawable.bg_signup_signin).error(R.drawable.bg_signup_signin).into(imgHeader);
    }

    @Override
    public void onDetailsRetrieved(UserDetails userDetails) {

    }

    @Override
    public void onDetailsRetrieveFail(String message) {

    }

    @Override
    public void onUploadFail(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.img_edit:
                Intent gallery_Intent = new Intent(getApplicationContext(), GalleryUtil.class);
                startActivityForResult(gallery_Intent, GALLERY_ACTIVITY_CODE);
                break;
        }
    }

    class CourseItemViewHolder {

        TextView tvItemValue;
        ImageButton imgClear;

    }
}

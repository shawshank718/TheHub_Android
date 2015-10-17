package com.group6.thehub.activities;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.group6.thehub.AppHelper;
import com.group6.thehub.R;
import com.group6.thehub.Rest.models.Course;
import com.group6.thehub.Rest.models.CourseDetails;
import com.group6.thehub.Rest.models.Language;
import com.group6.thehub.Rest.models.LanguageDetails;
import com.group6.thehub.Rest.models.UserDetails;
import com.group6.thehub.Rest.responses.CourseResponse;
import com.group6.thehub.Rest.responses.LangaugesResponse;
import com.group6.thehub.Rest.responses.UserResponse;
import com.group6.thehub.views.AspectRatioImageView;
import com.squareup.picasso.Picasso;

import org.parceler.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit.mime.TypedFile;

public class ProfileActivity extends AppCompatActivity implements UserResponse.ImageUploadResponseListener, View.OnClickListener, UserResponse.UserDetailsQueryListener, LangaugesResponse.LanguageDetailsListener, CourseResponse.CourseDetailsListener {

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
    private Button btnReqSession;

    private String qualification;
    private String phone;
    private boolean inEditMode;
    private boolean isMine = false;

    private List<Language> existingLanguages = new ArrayList<>();
    private List<String> addedLanguages = new ArrayList<>();
    private List<String> deletedLaguages = new ArrayList<>();
    private List<Language> languages = new ArrayList<>();
    private List<String> languageNames = new ArrayList<>();
    private ArrayAdapter<String> languageAdapter;

    private List<Course> existingCourses = new ArrayList<>();
    private List<String> addedCourses = new ArrayList<>();
    private List<String> deletedCourses = new ArrayList<>();
    private List<Course> courses = new ArrayList<>();
    private List<String> courseCodes = new ArrayList<>();
    private ArrayAdapter<String> coursesAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        toolbar = (Toolbar) findViewById(R.id.anim_toolbar);
        setSupportActionBar(toolbar);

        userDetails = UserResponse.getUserDetails(this);

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);

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
        btnReqSession = (Button) findViewById(R.id.btn_req_session);

        imgEdit.setOnClickListener(this);

        int userId = getIntent().getIntExtra("userId", -1);
        if (userId == userDetails.getUserId()) {
            isMine =true;
            invalidateOptionsMenu();
            btnReqSession.setVisibility(View.GONE);
            setupDetails(userDetails);
        } else {
            UserResponse.retrieveUserDetails(this, userId);
        }

        LangaugesResponse.loadAllLanguages(this);
        CourseResponse.loadAllCourses(this);

    }

    private void setupDetails(UserDetails userDetails) {
        collapsingToolbarLayout.setTitle(userDetails.getFirstName() + " " + userDetails.getLastName());
        Picasso.with(this).setLoggingEnabled(true);
        Picasso.with(this).load(AppHelper.END_POINT+userDetails.getImage().getImageUrl()).placeholder(R.drawable.bg_signup_signin).error(R.drawable.bg_signup_signin).into(imgHeader);
        if (userDetails.getQualification() == null) {
            tvQualValue.setVisibility(View.GONE);
        } else {
            tvQualValue.setText(userDetails.getQualification());
        }
        addLanguages(userDetails.getLanguages());
        addCourses(userDetails.getCourses());
        tvPhoneValue.setText(userDetails.getPhone());
        tvEmailValue.setText(userDetails.getEmail());
        existingLanguages = userDetails.getLanguages();
        existingCourses = userDetails.getCourses();
    }

    private void addCourses(ArrayList<Course> courses) {
        lilyCourses.removeAllViews();
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
                Course removedCourse = ((Course) v.getTag());
                if (deletedCourses == null) {
                    deletedCourses = new ArrayList<String>();
                }
                deletedCourses.add(removedCourse.getCourseCode());
                existingCourses.remove(removedCourse);
                lilyCourses.removeView((View) v.getParent());
            }
        });
        if (inEditMode) {
            holder.imgClear.setVisibility(View.VISIBLE);
        }
        holder.tvItemValue.setText(course.getCourseCode() + ": " + course.getCourseName());
        holder.course = course;
        holder.imgClear.setTag(course);
        view.setTag(holder);
        lilyCourses.addView(view);
    }

    private void addLanguages(ArrayList<Language> languages) {
        lilyLanguages.removeAllViews();
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
                Language removedLanguage = ((Language) v.getTag());
                if (deletedLaguages == null) {
                    deletedLaguages = new ArrayList<String>();
                }
                deletedLaguages.add(removedLanguage.getShortName());
                existingLanguages.remove(removedLanguage);
                lilyLanguages.removeView((View) v.getParent());
            }
        });
        if (inEditMode) {
            holder.imgClear.setVisibility(View.VISIBLE);
        }
        holder.tvItemValue.setText(language.getEnglishName());
        holder.language = language;
        holder.imgClear.setTag(language);
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

        if (id == R.id.action_save) {
            saveChangedDetails();
        }

        return super.onOptionsItemSelected(item);
    }

    private void saveChangedDetails() {
        final Context context = this;
        final String qualification = etQualValue.getText().toString().trim();
        final String phone = etPhoneValue.getText().toString().trim();
        ArrayList<String> emptyFields = new ArrayList<>();
        if (qualification.isEmpty()) {
            emptyFields.add("Qualification");
        }
        if (phone.isEmpty()) {
            emptyFields.add("Phone");
        }
        String message = "";
        if (emptyFields.isEmpty()) {
            UserResponse.updateUserDetails(this, userDetails.getUserId(), phone, qualification, addedLanguages, deletedLaguages, addedCourses, deletedCourses);
            showViewMode();
        } else if (emptyFields.size() == 1) {
            message = emptyFields.get(0)+" is empty. Are you sure want to save it?";
        } else {
            message = "The fields "+ StringUtils.join(emptyFields,", ")+ " are empty. Are you sure you want to save them?";
        }
        if (!message.isEmpty()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(message);
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    UserResponse.updateUserDetails(context, userDetails.getUserId(), phone, qualification, addedLanguages, deletedLaguages, addedCourses, deletedCourses);
                    showViewMode();
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.create().show();
        }
        try {
            addedLanguages.clear();
            deletedLaguages.clear();
        } catch (NullPointerException e) {
            System.err.println(e.getMessage());
        }
    }

    private void showViewMode() {
        inEditMode = false;
        invalidateOptionsMenu();

        imgEdit.setVisibility(View.GONE);
        tvQualValue.setVisibility(View.VISIBLE);
        etQualValue.setVisibility(View.GONE);
        for (int i = 0; i < lilyCourses.getChildCount(); i++) {
            View view = lilyCourses.getChildAt(i);
            CourseItemViewHolder holder = (CourseItemViewHolder) view.getTag();
            holder.imgClear.setVisibility(View.INVISIBLE);
        }
        etCourseValue.setVisibility(View.GONE);
        for (int i = 0; i < lilyLanguages.getChildCount(); i++) {
            View view = lilyLanguages.getChildAt(i);
            CourseItemViewHolder holder = (CourseItemViewHolder) view.getTag();
            holder.imgClear.setVisibility(View.INVISIBLE);
        }
        etLangValue.setVisibility(View.GONE);
        tvPhoneValue.setVisibility(View.VISIBLE);
        etPhoneValue.setVisibility(View.GONE);
    }

    private void showEditMode() {
        inEditMode = true;
        invalidateOptionsMenu();

        qualification = tvQualValue.getText().toString();
        phone = tvPhoneValue.getText().toString();

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
        etPhoneValue.setVisibility(View.VISIBLE);
        etPhoneValue.setText(phone);
    }

    private void goBack() {
        finish();
        overridePendingTransition(0, R.anim.push_right_out);
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
        this.userDetails = userDetails;
        setupDetails(userDetails);
    }

    @Override
    public void onDetailsUpdated(UserDetails userDetails) {
        this.userDetails = userDetails;
        UserResponse.saveUserDetails(this, userDetails);
        setupDetails(userDetails);
    }

    @Override
    public void onDetailsQueryFail(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onUploadFail(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void languagesRetrieved(LanguageDetails languageDetails) {
        this.languages = languageDetails.getLanguages();
        this.languageNames = languageDetails.getEnglishNames();
        setUpLanguagesAdapter();
    }

    @Override
    public void languageDetailsFail(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private void setUpLanguagesAdapter() {
        languageAdapter = new ArrayAdapter(this, R.layout.autocomplete_item, languageNames);
        etLangValue.setAdapter(languageAdapter);
        etLangValue.setThreshold(2);
        etLangValue.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                etLangValue.setText("");
                String chosenName = (String) parent.getItemAtPosition(position);
                Language chosenLanguage = languages.get(languageNames.indexOf(chosenName));
                if (existingLanguages.contains(chosenLanguage)) {
                    Toast.makeText(getApplicationContext(), "That language has already been added", Toast.LENGTH_SHORT).show();
                } else {
                    addLanguageItem(chosenLanguage);
                    existingLanguages.add(chosenLanguage);
                    if (addedLanguages == null) {
                        addedLanguages = new ArrayList<String>();
                    }
                    addedLanguages.add(chosenLanguage.getShortName());
                }
            }
        });

    }

    @Override
    public void coursesRetrieved(CourseDetails courseDetails) {
        this.courses = courseDetails.getCourses();
        this.courseCodes = courseDetails.getCourseCodes();
        setUpCoursesAdapter();
    }

    @Override
    public void courseDetailsFail(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private void setUpCoursesAdapter() {
        coursesAdapter = new ArrayAdapter(this, R.layout.autocomplete_item, courseCodes);
        etCourseValue.setAdapter(coursesAdapter);
        etCourseValue.setThreshold(2);
        etCourseValue.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                etCourseValue.setText("");
                String chosenCode = (String) parent.getItemAtPosition(position);
                Course chosenCourse = courses.get(courseCodes.indexOf(chosenCode));
                if (existingCourses.contains(chosenCourse)) {
                    Toast.makeText(getApplicationContext(), "That language has already been added", Toast.LENGTH_SHORT).show();
                } else {
                    addCourseItem(chosenCourse);
                    existingCourses.add(chosenCourse);
                    if (addedLanguages == null) {
                        addedLanguages = new ArrayList<String>();
                    }
                    addedCourses.add(chosenCourse.getCourseCode());
                }
            }
        });
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
        Language language;
        Course course;

    }
}

package com.group6.thehub.activities;

import android.content.Intent;
import android.os.Bundle;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.group6.thehub.AppHelper;
import com.group6.thehub.R;
import com.group6.thehub.Rest.models.Course;
import com.group6.thehub.Rest.models.CourseDetails;
import com.group6.thehub.Rest.models.UserDetails;
import com.group6.thehub.Rest.responses.CourseResponse;
import com.group6.thehub.Rest.responses.UserResponse;
import com.quinny898.library.persistentsearch.SearchBox;
import com.quinny898.library.persistentsearch.SearchResult;
import com.squareup.picasso.Picasso;


import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class HomeActivity extends AppCompatActivity implements View.OnClickListener, CourseResponse.CourseDetailsListener{

    private static final String LOG_TAG = "HomeActivity";

    private DrawerLayout mDrawerLayout;

    private ActionBarDrawerToggle mDrawerToggle;
    private NavigationView navigationView;

    private CircleImageView profileImgView;

    private TextView tvEmail;

    private TextView tvName;

    private UserDetails userDetails;

    private  Toolbar toolbar;

    private SearchBox searchBox;

    private ImageView imgTint;
    private boolean isSearchOpened;

    private String curSearchTerm = "";
    private String preSearchTerm = "";

    private List<Course> courses = new ArrayList<>();
    private List<String> courseCodes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        toolbar = (Toolbar) findViewById(R.id.appBar);
        setSupportActionBar(toolbar);
        searchBox = (SearchBox) findViewById(R.id.searchbox);
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

        tvEmail.setText(userDetails.getEmail());
        tvName.setText(userDetails.getFirstName() + " " + userDetails.getLastName());
        Picasso.with(this)
                .load(AppHelper.END_POINT+userDetails.getImage().getImageUrl())
                .placeholder(R.drawable.circle_placeholder_76dp)
                .error(R.drawable.circle_placeholder_76dp)
                .into(profileImgView);


        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                //Closing drawer on item click
                mDrawerLayout.closeDrawers();
                Intent intent;
                switch (menuItem.getItemId()) {

                    case R.id.profile:
                        intent = new Intent(getApplicationContext(), ProfileActivity.class);
                        intent.putExtra("userId", userDetails.getUserId());
                        AppHelper.slideInStayStill(intent);
                        return true;
                    case R.id.logout:
                        UserResponse.callLogout(getApplicationContext());
                        intent = new Intent(getApplicationContext(), SignInSignUpActivity.class);
                        AppHelper.slideDownPushDown(intent);
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

        CourseResponse.loadAllCourses(this);
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
        searchBox.setLogoText("");
        searchBox.setHint("Search by unit of study");
        searchBox.setSearchListener(new SearchBox.SearchListener() {
            @Override
            public void onSearchOpened() {
                isSearchOpened = true;
                imgTint.setVisibility(View.VISIBLE);
                searchBox.hideResults();
            }

            @Override
            public void onSearchCleared() {

            }

            @Override
            public void onSearchClosed() {
                isSearchOpened = false;
                closeSearch();
            }

            @Override
            public void onSearchTermChanged(String s) {
                if (s.length() > 2) {
                    searchBox.updateResults();
                }
            }

            @Override
            public void onSearch(String s) {
                Log.d(LOG_TAG, "on search " + s);
            }

            @Override
            public void onResultClick(SearchResult searchResult) {

            }
        });
    }

    protected void closeSearch() {
        searchBox.hideCircularly(this);
        imgTint.setVisibility(View.GONE);
    }

    public void openSearch() {
        searchBox.revealFromMenuItem(R.id.action_search, this);
        for (int i = 0; i < courses.size(); i++) {
            String courseName = courses.get(i).getCourseName();
            String courseCode = courseCodes.get(i);
            SearchResult option = new SearchResult(courseCode+" - "+courseName);
            searchBox.addSearchable(option);
        }

//        for (int x = 0; x < 10; x++) {
//            SearchResult option = new SearchResult("Result "
//                    + Integer.toString(x), ContextCompat.getDrawable(this, R.drawable.ic_history_black_24dp));
//            searchBox.addSearchable(option);
//        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

        }
    }

    @Override
    public void coursesRetrieved(CourseDetails courseDetails) {
        this.courses = courseDetails.getCourses();
        this.courseCodes = courseDetails.getCourseCodes();
    }

    @Override
    public void courseDetailsFail(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}

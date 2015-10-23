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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.group6.thehub.AppHelper;
import com.group6.thehub.R;
import com.group6.thehub.Rest.models.Course;
import com.group6.thehub.Rest.models.CourseDetails;
import com.group6.thehub.Rest.models.UserDetails;
import com.group6.thehub.Rest.responses.CourseResponse;
import com.group6.thehub.Rest.responses.FavoritesResponse;
import com.group6.thehub.Rest.responses.UserResponse;
import com.quinny898.library.persistentsearch.SearchBox;
import com.quinny898.library.persistentsearch.SearchResult;
import com.squareup.picasso.Picasso;


import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class HomeActivity extends AppCompatActivity implements View.OnClickListener, CourseResponse.CourseDetailsListener, NavigationView.OnNavigationItemSelectedListener, FavoritesResponse.FavoritesListener{

    private static final String LOG_TAG = "HomeActivity";
    private final String SEARCH_TYPE = "search_type";
    private final String COURSE_SEARCH = "course_search";
    private final String FAVORITES = "favorites";

    private DrawerLayout mDrawerLayout;
    AppHelper appHelper = new AppHelper(this);
    private ActionBarDrawerToggle mDrawerToggle;
    private NavigationView navigationView;

    private CircleImageView profileImgView;

    private TextView tvEmail;

    private TextView tvName;

    private UserDetails userDetails;

    private  Toolbar toolbar;
    private SearchBox searchBox;
    private ImageView imgTint;
    private Button btnMore;
    private LinearLayout lilyFavs;
    private RelativeLayout reltvFavs;

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
        lilyFavs = (LinearLayout) findViewById(R.id.lily_favs);
        reltvFavs = (RelativeLayout) findViewById(R.id.reltv_favs);
        btnMore = (Button) findViewById(R.id.btn_more);
        btnMore.setOnClickListener(this);
        userDetails = UserResponse.getUserDetails(this);


        //Initializing NavigationView
        navigationView = (NavigationView) findViewById(R.id.navigation_view);

        profileImgView = (CircleImageView) navigationView.findViewById(R.id.profile_image);
        tvEmail = (TextView) navigationView.findViewById(R.id.tvEmail);
        tvName = (TextView) navigationView.findViewById(R.id.tvName);

        updateUserDetails();

        navigationView.setNavigationItemSelectedListener(this);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar,R.string.drawer_open, R.string.drawer_close){
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

        FavoritesResponse.getFavorites(this, userDetails.getUserId());
        CourseResponse.loadAllCourses(this);
    }

    public void updateUserDetails() {
        setTitle("Hi " +userDetails.getFirstName());
        tvEmail.setText(userDetails.getEmail());
        tvName.setText(userDetails.getFirstName() + " " + userDetails.getLastName());
        Picasso.with(this)
                .load(AppHelper.END_POINT+userDetails.getImage().getImageUrl())
                .placeholder(R.drawable.circle_placeholder_76dp)
                .error(R.drawable.circle_placeholder_76dp)
                .into(profileImgView);
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
                searchBox.hideResults();
                if (s.length() > 2) {
                    searchBox.updateResults();
                }
            }

            @Override
            public void onSearch(String s) {

            }

            @Override
            public void onResultClick(SearchResult searchResult) {
                String searchCourseCode = (String) searchResult.getTag();
                searchBox.setSearchString("");
                goToSearchResultActivity(searchCourseCode);
            }
        });
        for (int i = 0; i < courses.size(); i++) {
            String courseName = courses.get(i).getCourseName();
            String courseCode = courseCodes.get(i);
            SearchResult option = new SearchResult(courseCode+" - "+courseName);
            option.setTag(courseCode);
            searchBox.addSearchable(option);
        }
        searchBox.setSearchFilter(new SearchBox.SearchFilter() {
            @Override
            public boolean onFilter(SearchResult searchResult, String searchTerm) {
                return searchResult.title.toLowerCase().contains(searchTerm.toLowerCase());
            }
        });
    }

    protected void closeSearch() {
        searchBox.hideCircularly(this);
        imgTint.setVisibility(View.GONE);
    }

    public void openSearch() {
        searchBox.revealFromMenuItem(R.id.action_search, this);
        searchBox.hideResults();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        if (id == R.id.btn_more) {
            goToFavorites();
        }
    }

    private void goToFavorites() {
        Bundle bundle = new Bundle();
        bundle.putString(SEARCH_TYPE, FAVORITES);
        AppHelper.slideInStayStill(this, SearchResultsActivity.class, bundle);
    }

    @Override
    public void coursesRetrieved(CourseDetails courseDetails) {
        this.courses = courseDetails.getCourses();
        this.courseCodes = courseDetails.getCourseCodes();
        setUpSearchBox();
    }

    @Override
    public void courseDetailsFail(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private void goToSearchResultActivity(String searchCourseCode) {
        Bundle bundle = new Bundle();
        bundle.putString(SEARCH_TYPE, COURSE_SEARCH);
        bundle.putString("search", searchCourseCode);
        AppHelper.slideInStayStill(this, SearchResultsActivity.class, bundle);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        mDrawerLayout.closeDrawers();
        Intent intent;
        switch (menuItem.getItemId()) {

            case R.id.profile:
                Bundle bundle = new Bundle();
                bundle.putInt("userId", userDetails.getUserId());
                AppHelper.slideInStayStill(this, ProfileActivity.class, bundle);
                return true;
            case R.id.logout:
                UserResponse.callLogout(getApplicationContext());
                intent = new Intent(this, SignInSignUpActivity.class);
                appHelper.slideDownPushDown(intent);
                finish();
                return true;

            default:
                return true;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        this.userDetails = UserResponse.getUserDetails(this);
        updateUserDetails();
        FavoritesResponse.getFavorites(this, userDetails.getUserId());
    }

    @Override
    public void onFavoritesRetrieved(List<UserDetails> users) {
        setUpFavorites(users);
    }

    @Override
    public void onFavoriteActionSuccess(String message) {

    }

    @Override
    public void onFavoriteCallFail(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private void setUpFavorites(List<UserDetails> users) {
        if(users.size() <= 3) {
            btnMore.setVisibility(View.GONE);
        } else {
            btnMore.setVisibility(View.VISIBLE);
        }
        int length = users.size() > 3 ? 3 : users.size();
        for (int i = 0; i < length; i++) {
            lilyFavs.getChildAt(i).setVisibility(View.VISIBLE);
            lilyFavs.getChildAt(i).setTag(users.get(i));
            lilyFavs.getChildAt(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    favClicked((UserDetails) v.getTag());
                }
            });
            FavoritesViewHolder fvh = new FavoritesViewHolder(lilyFavs.getChildAt(i));
            fvh.tvName.setText(users.get(i).getFullName());
            Picasso.with(this)
                    .load(AppHelper.END_POINT+users.get(i).getImage().getImageUrl())
                    .placeholder(R.drawable.ic_account_circle_grey_48dp)
                    .error(R.drawable.ic_account_circle_grey_48dp)
                    .into(fvh.imgUser);
        }
    }

    private void favClicked(UserDetails user) {
        Bundle bundle = new Bundle();
        bundle.putInt("userId", user.getUserId());
        AppHelper.slideInStayStill(this, RequestSessionActivity.class, bundle);
    }

    private class FavoritesViewHolder {

        ImageView imgUser;
        TextView tvName;

        public FavoritesViewHolder(View view) {
            imgUser = (ImageView) view.findViewById(R.id.profile_image);
            tvName = (TextView) view.findViewById(R.id.tv_name);
        }

    }

}

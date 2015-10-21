package com.group6.thehub.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.group6.thehub.R;
import com.group6.thehub.Rest.models.Course;
import com.group6.thehub.Rest.models.CourseDetails;
import com.group6.thehub.Rest.models.UserSearchDetails;
import com.group6.thehub.Rest.responses.CourseResponse;
import com.group6.thehub.Rest.responses.UserSearchResponse;
import com.group6.thehub.UserSearchResponseAdapter;
import com.quinny898.library.persistentsearch.SearchBox;
import com.quinny898.library.persistentsearch.SearchResult;

import java.util.ArrayList;
import java.util.List;

public class SearchResultsActivity extends AppCompatActivity implements CourseResponse.CourseDetailsListener, UserSearchResponse.UserSearchResponseListener {

    private String searchCourseCode;
    private Toolbar toolbar;
    private SearchBox searchBox;
    private ImageView imgTint;
    private RecyclerView rvSearchResults;
    private UserSearchResponseAdapter adapter;
    private boolean isSearchOpened;
    private List<Course> courses = new ArrayList<>();
    private List<String> courseCodes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        toolbar = (Toolbar) findViewById(R.id.appBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        searchCourseCode = getIntent().getStringExtra("search");
        setTitle(searchCourseCode);
        searchBox = (SearchBox) findViewById(R.id.searchbox);
        setUpSearchBox();
        imgTint = (ImageView) findViewById(R.id.imgTint);
        rvSearchResults = (RecyclerView)findViewById(R.id.rv_search_results);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rvSearchResults.setLayoutManager(llm);
        CourseResponse.loadAllCourses(this);
        UserSearchResponse.searchByCourseCode(this, searchCourseCode);

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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search_results, menu);
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

        if (id == R.id.action_search) {
            openSearch();
            return true;
        }

        return super.onOptionsItemSelected(item);
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
    public void coursesRetrieved(CourseDetails courseDetails) {
        this.courses = courseDetails.getCourses();
        this.courseCodes = courseDetails.getCourseCodes();
    }

    @Override
    public void courseDetailsFail(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onUserSearchSuccess(UserSearchDetails userSearchDetails) {
        adapter = new UserSearchResponseAdapter(this, userSearchDetails.getUsers());
        rvSearchResults.setAdapter(adapter);
    }

    @Override
    public void onUserSearchFailed(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}

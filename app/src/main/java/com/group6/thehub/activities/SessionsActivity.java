package com.group6.thehub.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.group6.thehub.R;
import com.group6.thehub.Rest.models.Session;
import com.group6.thehub.Rest.models.UserDetails;
import com.group6.thehub.Rest.responses.SessionResponse;
import com.group6.thehub.Rest.responses.UserResponse;
import com.group6.thehub.SessionResultsAdapter;

import java.util.ArrayList;
import java.util.List;

public class SessionsActivity extends AppCompatActivity implements SessionResponse.SessionResponseListner {

    private UserDetails user_cur;
    private List<Session> sessions;
    private RecyclerView rv_sessions;
    private Toolbar toolbar;
    private SessionResultsAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sessions);
        toolbar = (Toolbar) findViewById(R.id.appBar);
        setSupportActionBar(toolbar);
        setTitle("My Sessions");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        user_cur = UserResponse.getUserDetails(this);
        rv_sessions = (RecyclerView)findViewById(R.id.rv_sessions);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv_sessions.setLayoutManager(llm);
        SessionResponse.getUserSessions(this, user_cur.getUserId());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sessions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.home) {
            goBack();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void goBack() {
        finish();
        overridePendingTransition(0, R.anim.push_right_out);
    }

    @Override
    public void onSessionsRetrieved(List<Session> sessions) {
        this.sessions = sessions;
        loadDataIntoView();
    }

    @Override
    public void onSessionRetrieved(Session session) {

    }

    @Override
    public void onSessionActionSuccess(Session session) {

    }

    @Override
    public void onSessionFails(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private void loadDataIntoView() {
        adapter = new SessionResultsAdapter(this, sessions);
        rv_sessions.setAdapter(adapter);
    }
}

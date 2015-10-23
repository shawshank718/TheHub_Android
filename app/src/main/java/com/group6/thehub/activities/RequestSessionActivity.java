package com.group6.thehub.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.group6.thehub.AppHelper;
import com.group6.thehub.R;
import com.group6.thehub.Rest.models.Course;
import com.group6.thehub.Rest.models.Location;
import com.group6.thehub.Rest.models.Session;
import com.group6.thehub.Rest.models.UserDetails;
import com.group6.thehub.Rest.responses.LocationResponse;
import com.group6.thehub.Rest.responses.SessionResponse;
import com.group6.thehub.Rest.responses.UserResponse;
import com.group6.thehub.fragments.DateTimePickerDialogFragment;
import com.parse.ParsePush;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class RequestSessionActivity extends AppCompatActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener, UserResponse.UserDetailsQueryListener, LocationResponse.LocationResponseListener, SessionResponse.SessionResponseListner {

    private static final String LOG_TAG = "RequestSessionActivity";

    private int year_x, month_x, day_x, year_cur, month_cur, day_cur;
    private static final int DATEPICKER_DIALOG_ID = 0;
    private static final int TIMEPICKER_DIALOG_ID = 1;

    private Toolbar toolbar;
    private CircleImageView profile_image_cur, profile_image_oth;
    private TextView tv_name_cur, tv_name_oth;
    private Button btn_date, btn_start_time, btn_end_time, btn_action;
    private Spinner spn_course,spn_location;
    private DateTimePickerDialogFragment dateTimePickerDialogFragment;

    private UserDetails user_cur;
    private UserDetails user_oth;
    private int user_oth_id;
    private int session_id;
    private boolean fromStart;
    private boolean fromEnd;
    private Session session;
    private List<Location> locations;
    private List<Course> courses;
    private List<String> locationNames;
    private List<String> courseCodes;
    private ArrayAdapter<String> adapter_locations;
    private ArrayAdapter<String> adapter_courses;

    /*POST arguments*/
    private int studentId;
    private int tutorId;
    private String courseCode;
    private int locationId;
    private long startTime;
    private long endTime;
    private String action;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_session);
        instantiateViews();
        setUpToolbar();
        user_cur = UserResponse.getUserDetails(this);
        session_id = getIntent().getIntExtra("sessionId", -1);
        if(session_id != -1) {
            SessionResponse.getSession(this, session_id);
        } else {
            user_oth_id = getIntent().getIntExtra("userId", -1);
            setUpViews();
        }


    }

    private void instantiateViews() {
        toolbar = (Toolbar) findViewById(R.id.appBar);
        profile_image_cur = (CircleImageView) findViewById(R.id.profile_image_cur);
        tv_name_cur = (TextView) findViewById(R.id.tv_name_cur);
        profile_image_oth = (CircleImageView) findViewById(R.id.profile_image_oth);
        tv_name_oth = (TextView) findViewById(R.id.tv_name_oth);
        btn_date = (Button) findViewById(R.id.btn_date);
        btn_start_time = (Button) findViewById(R.id.btn_start_time);
        btn_end_time = (Button) findViewById(R.id.btn_end_time);
        spn_course = (Spinner) findViewById(R.id.spn_course);
        spn_location = (Spinner) findViewById(R.id.spn_location);
        btn_action = (Button) findViewById(R.id.btn_action);
        btn_date.setOnClickListener(this);
        btn_start_time.setOnClickListener(this);
        btn_end_time.setOnClickListener(this);
        btn_action.setOnClickListener(this);
    }

    private void setUpViews() {
        setupDateView();
        setUpTimeView();
        setupProfileView();
        setupLocations();
        setUpActionButton();
    }

    private void setUpActionButton() {
        btn_action.setText("REQUEST SESSION");
        btn_action.setTag("CREATE");
    }

    private void setUpToolbar() {
        setSupportActionBar(toolbar);
        setTitle("Session");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void setupDateView() {
        final Calendar calendar = Calendar.getInstance();
        year_cur = calendar.get(Calendar.YEAR);
        month_cur = calendar.get(Calendar.MONTH)+1;
        day_cur = calendar.get(Calendar.DAY_OF_MONTH);
        btn_date.setText(day_cur + "/" + month_cur + "/" + year_cur);
    }

    private void setUpTimeView() {
        btn_start_time.setText("5:00 PM");
        btn_end_time.setText("6:00 PM");
    }

    private void setupProfileView() {
        setUpCurrentUser();
        setUpOtherUser();
    }

    private void setupLocations() {
        LocationResponse.getLocations(this);
    }

    private void setUpCurrentUser() {
        tv_name_cur.setText(user_cur.getFullName());
        Picasso.with(this)
                .load(AppHelper.END_POINT+user_cur.getImage().getImageUrl())
                .placeholder(R.drawable.ic_account_circle_grey_48dp)
                .error(R.drawable.ic_account_circle_grey_48dp)
                .into(profile_image_cur);
    }

    private void setUpOtherUser() {
        UserResponse.retrieveUserDetails(this, user_oth_id, user_cur.getUserId());
    }

    private void updateViewsWithOtherUser() {
        tv_name_oth.setText(user_oth.getFullName());
        Picasso.with(this)
                .load(AppHelper.END_POINT+user_oth.getImage().getImageUrl())
                .placeholder(R.drawable.ic_account_circle_grey_48dp)
                .error(R.drawable.ic_account_circle_grey_48dp)
                .into(profile_image_oth);
    }

    private void updateViewWithCourses() {
        courseCodes = new ArrayList<>();
        for (int i = 0; i < courses.size(); i++) {
            courseCodes.add(courses.get(i).getCourseCode());
        }
        adapter_courses = new ArrayAdapter<String>(this, R.layout.spinner_item_general, courseCodes);
        adapter_courses.setDropDownViewResource(R.layout.spinner_item_general_dropdown);
        spn_course.setAdapter(adapter_courses);
    }

    private void updateViewWithLocations() {
        locationNames = new ArrayList<>();
        for (int i = 0; i < locations.size(); i++) {
            locationNames.add(locations.get(i).getLocationName());
        }
        adapter_locations = new ArrayAdapter<String>(this, R.layout.spinner_item_general, locationNames);
        adapter_locations.setDropDownViewResource(R.layout.spinner_item_general_dropdown);
        spn_location.setAdapter(adapter_locations);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_request_session, menu);
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

    @Override
    public void onBackPressed() {
        if (dateTimePickerDialogFragment != null && dateTimePickerDialogFragment.isVisible()) {
        } else {
            goBack();
        }
        super.onBackPressed();
    }

    private void goBack() {
        finish();
        overridePendingTransition(0, R.anim.push_right_out);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.btn_date) {
            showDatePickerDialog();
        }

        if (id == R.id.btn_start_time) {
            showStartTimePickerDialog();
        }

        if (id == R.id.btn_end_time) {
            showEndTimePickerDialog();
        }

        if (id == R.id.btn_action) {
            performRelevantAction(v);
        }


    }

    private void showDatePickerDialog() {
        dateTimePickerDialogFragment = DateTimePickerDialogFragment.newInstance(DATEPICKER_DIALOG_ID);
        dateTimePickerDialogFragment.show(getSupportFragmentManager(), "date_picker");
    }

    private void showStartTimePickerDialog() {
        fromStart = true;
        fromEnd = false;
        dateTimePickerDialogFragment = DateTimePickerDialogFragment.newInstance(TIMEPICKER_DIALOG_ID);
        dateTimePickerDialogFragment.show(getSupportFragmentManager(), "time_picker_start");
    }

    private void showEndTimePickerDialog() {
        fromStart = false;
        fromEnd = true;
        dateTimePickerDialogFragment = DateTimePickerDialogFragment.newInstance(TIMEPICKER_DIALOG_ID);
        dateTimePickerDialogFragment.show(getSupportFragmentManager(), "time_picker_end");
    }

    private void performRelevantAction(View v) {
        if (v.getTag() == "CREATE") {
            studentId = user_cur.getUserId();
            tutorId = user_oth.getUserId();
            locationId = (int) spn_location.getSelectedItemId()+1;
            courseCode = spn_course.getSelectedItem().toString();
            SessionResponse.createSession(this, studentId, tutorId, locationId, courseCode, startTime, endTime, "CREATE");
        }
        sendPushNotification();
    }

    private void sendPushNotification() {
        ParsePush push = new ParsePush();
        push.setChannel(user_oth.getEmail());
        push.setMessage(user_cur+ " is requesting a session for the course "+spn_course.getSelectedItem().toString());
        push.sendInBackground();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        year_x = year;
        month_x = monthOfYear+1;
        day_x = dayOfMonth;
        String d = day_x<10?"0"+day_x:day_x+"";
        String m = month_x<10?"0"+month_x:month_x+"";
        btn_date.setText(d + "/" + m + "/" + year_x);
        String s = btn_date.getText().toString()+" "+btn_start_time.getText().toString();
        startTime = convertToEpoch(s);
        String e = btn_date.getText().toString()+" "+btn_end_time.getText().toString();
        endTime = convertToEpoch(e);
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        String am_pm = "";

        Calendar datetime = Calendar.getInstance();
        datetime.set(Calendar.HOUR_OF_DAY, hourOfDay);
        datetime.set(Calendar.MINUTE, minute);

        if (datetime.get(Calendar.AM_PM) == Calendar.AM)
            am_pm = "AM";
        else if (datetime.get(Calendar.AM_PM) == Calendar.PM)
            am_pm = "PM";

        String hours_to_show = (datetime.get(Calendar.HOUR) == 0) ?"12":datetime.get(Calendar.HOUR)+"";
        String mins_to_show = (datetime.get(Calendar.MINUTE) < 10) ? "0"+datetime.get(Calendar.MINUTE): datetime.get(Calendar.MINUTE)+"";
        if (fromStart) {
            btn_start_time.setText(hours_to_show+":"+mins_to_show+" "+am_pm);
            String d = btn_date.getText().toString()+" "+btn_start_time.getText().toString();
            startTime = convertToEpoch(d);
        }
        if (fromEnd) {
            btn_end_time.setText(hours_to_show+":"+mins_to_show+" "+am_pm);
            String d = btn_date.getText().toString()+" "+btn_end_time.getText().toString();
            endTime = convertToEpoch(d);
        }

    }

    @Override
    public void onDetailsRetrieved(UserDetails userDetails) {
        user_oth = userDetails;
        this.courses = user_oth.getCourses();
        updateViewsWithOtherUser();
        updateViewWithCourses();
    }

    @Override
    public void onDetailsUpdated(UserDetails userDetails) {

    }

    @Override
    public void onDetailsQueryFail(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private long convertToEpoch(String str) {
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
        Date date = null;
        try {
            date = df.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long epoch = date.getTime()/1000;
        return epoch;
    }

    @Override
    public void onLocationsRetrieved(List<Location> locations) {
        this.locations = locations;
        updateViewWithLocations();
    }

    @Override
    public void onLocationFail(String message) {

    }

    @Override
    public void onSessionsRetrieved(List<Session> sessions) {

    }

    @Override
    public void onSessionRetrieved(Session session) {
        this.session = session;
        if (session.getStudentid() == user_cur.getUserId()) {
            user_oth_id = session.getTutorId();
        } else {
            user_oth_id = session.getStudentid();
        }
        instantiateViews();
        setUpViews();

    }

    @Override
    public void onSessionActionSuccess(Session session) {
        Toast.makeText(this, "A request has been sent to the tutor.", Toast.LENGTH_LONG).show();
        goBack();
    }

    @Override
    public void onSessionFails(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}

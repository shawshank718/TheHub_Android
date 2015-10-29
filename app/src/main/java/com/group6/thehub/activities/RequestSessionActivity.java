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
import android.widget.LinearLayout;
import android.widget.RatingBar;
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

public class RequestSessionActivity extends AppCompatActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener, UserResponse.UserDetailsQueryListener, LocationResponse.LocationResponseListener, SessionResponse.SessionResponseListner, RatingBar.OnRatingBarChangeListener {

    private static final String LOG_TAG = "RequestSessionActivity";

    private int year_x, month_x, day_x, year_cur, month_cur, day_cur;
    private static final int DATEPICKER_DIALOG_ID = 0;
    private static final int TIMEPICKER_DIALOG_ID = 1;

    private Toolbar toolbar;
    private CircleImageView profile_image_cur, profile_image_oth;
    private TextView tv_name_cur, tv_name_oth, tv_status_info;
    private Button btn_date, btn_start_time, btn_end_time, btn_action;
    private Spinner spn_course,spn_location;
    private RatingBar rbar_tutor;
    private DateTimePickerDialogFragment dateTimePickerDialogFragment;
    private LinearLayout lily_user_cur, lily_user_oth;

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
    private boolean is_student = false;
    private int rating;

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
        tv_status_info = (TextView) findViewById(R.id.tv_status_info);
        rbar_tutor = (RatingBar) findViewById(R.id.rbar_tutor);
        lily_user_cur = (LinearLayout) findViewById(R.id.lily_profile_cur);
        lily_user_oth = (LinearLayout) findViewById(R.id.lily_profile_oth);
        btn_date.setOnClickListener(this);
        btn_start_time.setOnClickListener(this);
        btn_end_time.setOnClickListener(this);
        btn_action.setOnClickListener(this);
        lily_user_cur.setOnClickListener(this);
        lily_user_oth.setOnClickListener(this);
        rbar_tutor.setOnRatingBarChangeListener(this);

        setUpToolbar();
    }

    private void setUpViews() {
        setupDateView();
        setUpTimeView();
        setupProfileView();
        setupLocations();
        setUpActionButton();
    }

    private void setUpActionButton() {
        if (session_id == -1) {
            btn_action.setText("REQUEST SESSION");
            btn_action.setTag("CREATE");
        } else {
            btn_date.setClickable(false);
            btn_start_time.setClickable(false);
            btn_end_time.setClickable(false);
            spn_course.setEnabled(false);
            spn_location.setEnabled(false);
            if (is_student) {
                String status = session.getStatus();
                switch (status) {
                    case "P":
                        tv_status_info.setText("Waiting for the tutors decision.");
                        tv_status_info.setVisibility(View.VISIBLE);
                        btn_action.setVisibility(View.GONE);
                        break;
                    case "A":
                        tv_status_info.setText("The tutor has accepted your session");
                        tv_status_info.setVisibility(View.VISIBLE);
                        btn_action.setVisibility(View.GONE);
                        break;
                    case "C":
                        tv_status_info.setText("This session has ended");
                        tv_status_info.setVisibility(View.VISIBLE);
                        btn_action.setText("Submit rating");
                        btn_action.setTag("RATING");
                        rbar_tutor.setVisibility(View.VISIBLE);
                        break;
                    case "R":
                        tv_status_info.setText("This session has been rated");
                        tv_status_info.setVisibility(View.VISIBLE);
                        rbar_tutor.setVisibility(View.VISIBLE);
                        rbar_tutor.setRating(session.getRating());
                        rbar_tutor.setIsIndicator(true);
                        btn_action.setVisibility(View.GONE);
                }
            } else {
                String status = session.getStatus();
                switch (status) {
                    case "P":
                        btn_action.setText("ACCEPT SESSION");
                        btn_action.setTag("ACCEPT");
                        break;
                    case "A":
                        tv_status_info.setText("You have accepted this session");
                        tv_status_info.setVisibility(View.VISIBLE);
                        if (System.currentTimeMillis() / 1000 > session.getEndTime()) {
                            btn_action.setText("FINISH SESSION");
                            btn_action.setTag("FINISH");
                        } else {
                            btn_action.setVisibility(View.GONE);
                        }

                        break;
                    case "C":
                        tv_status_info.setText("This session has ended and needs to be rated.");
                        tv_status_info.setVisibility(View.VISIBLE);
                        btn_action.setVisibility(View.GONE);
                        break;
                    case "R":
                        tv_status_info.setText("This session has been rated");
                        tv_status_info.setVisibility(View.VISIBLE);
                        rbar_tutor.setVisibility(View.VISIBLE);
                        rbar_tutor.setRating(session.getRating());
                        rbar_tutor.setIsIndicator(true);
                        btn_action.setVisibility(View.GONE);
                }
            }
        }

    }

    private void setUpToolbar() {
        setSupportActionBar(toolbar);
        setTitle("Session");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void setupDateView() {
        if (session_id == -1) {
            final Calendar calendar = Calendar.getInstance();
            year_cur = calendar.get(Calendar.YEAR);
            month_cur = calendar.get(Calendar.MONTH)+1;
            day_cur = calendar.get(Calendar.DAY_OF_MONTH);
            btn_date.setText(day_cur + "/" + month_cur + "/" + year_cur);
        } else {
            String date = AppHelper.getDate(session.getStartTime());
            btn_date.setText(date);
        }

    }

    private void setUpTimeView() {
        if (session_id == -1) {
            btn_start_time.setText("5:00 PM");
            btn_end_time.setText("6:00 PM");
            String d = btn_date.getText().toString()+" "+btn_start_time.getText().toString();
            startTime = convertToEpoch(d);
            String e = btn_date.getText().toString()+" "+btn_end_time.getText().toString();
            endTime = convertToEpoch(e);
        } else {
            btn_start_time.setText(AppHelper.getTime(session.getStartTime()));
            btn_end_time.setText(AppHelper.getTime(session.getEndTime()));
        }

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
        if (session_id == -1) {
            UserResponse.retrieveUserDetails(this, user_oth_id, user_cur.getUserId());
        } else {
            onDetailsRetrieved(user_oth);
        }

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
        if (session_id != -1) {
            spn_course.setSelection(courseCodes.indexOf(session.getCourseCode()));
        }
    }

    private void updateViewWithLocations() {
        locationNames = new ArrayList<>();
        for (int i = 0; i < locations.size(); i++) {
            locationNames.add(locations.get(i).getLocationName());
        }
        adapter_locations = new ArrayAdapter<String>(this, R.layout.spinner_item_general, locationNames);
        adapter_locations.setDropDownViewResource(R.layout.spinner_item_general_dropdown);
        spn_location.setAdapter(adapter_locations);
        if (session_id != -1) {
            spn_location.setSelection(session.getLocationId()-1);
        }
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

        if (id == R.id.lily_profile_cur) {
            Bundle bundle = new Bundle();
            bundle.putInt("userId", user_cur.getUserId());
            AppHelper.slideInStayStill(this, ProfileActivity.class, bundle);
        }

        if (id == R.id.lily_profile_oth) {
            Bundle bundle = new Bundle();
            bundle.putInt("userId", user_oth.getUserId());
            AppHelper.slideInStayStill(this, ProfileActivity.class, bundle);
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
        if (v.getTag().equals("CREATE")) {
            studentId = user_cur.getUserId();
            tutorId = user_oth.getUserId();
            locationId = (int) spn_location.getSelectedItemId()+1;
            courseCode = spn_course.getSelectedItem().toString();
            if (startTime < System.currentTimeMillis()/1000) {
                Toast.makeText(this, "Start time cannot be in the past. please choose again.", Toast.LENGTH_LONG).show();
                return;
            }
            if (startTime > endTime) {
                Toast.makeText(this, "End time cannot be before start time. please choose again.", Toast.LENGTH_LONG).show();
                return;
            }
            SessionResponse.createSession(this, studentId, tutorId, locationId, courseCode, startTime, endTime, "CREATE");
        }

        if (v.getTag().equals("ACCEPT")) {
            SessionResponse.acceptFinishSession(this, session_id, "ACCEPT");
        }

        if (v.getTag().equals("FINISH")) {
            SessionResponse.acceptFinishSession(this, session_id, "FINISH");
        }

        if (v.getTag().equals("RATING")) {
            if (rating > 0)
                SessionResponse.rateSession(this, session_id, rating, session.getTutorId(), "RATE");
            else
                Toast.makeText(this, "Rating required to submit.", Toast.LENGTH_LONG).show();
        }
    }

    private void sendPushNotification() {
        String status = session.getStatus();
        String studentChannel = session.getStudent().getChannel();
        String tutorChannel = session.getTutor().getChannel();
        String message = "";
        String title = "";
        int sessionId = session.getSessionId();
        switch (status) {
            case "P":
                title = "Request received! ";
                message = " You have received a request from "+user_cur.getFullName();
                AppHelper.sendPushNotification(tutorChannel, message, title, sessionId);
                break;
            case "A":
                title = "Request accepted! ";
                message = " "+user_cur.getFullName()+" has accepted your request.";
                AppHelper.sendPushNotification(studentChannel, message, title, sessionId);
                break;
            case "C":
                title = "Session finished! ";
                message = " Your session with "+user_cur.getFullName()+" has ended. Rate this session to help the tutor.";
                AppHelper.sendPushNotification(studentChannel, message, title, sessionId);
                break;
            case "R":
                title = "Session rated! ";
                message = " "+user_cur.getFullName()+" has rated you.";
                AppHelper.sendPushNotification(tutorChannel, message, title, sessionId);
                break;
        }
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
        if (is_student) {
            this.courses = user_oth.getCourses();
        } else {
            this.courses = user_cur.getCourses();
        }
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
        this.session_id = session.getSessionId();
        this.session = session;
        user_oth = (session.getStudentId() == user_cur.getUserId()) ? session.getTutor(): session.getStudent();
        is_student = (session.getStudentId() == user_cur.getUserId()) ? true : false;
        setUpViews();
    }

    @Override
    public void onSessionActionSuccess(Session session) {
        this.session_id = session.getSessionId();
        this.session = session;
        user_oth = (session.getStudentId() == user_cur.getUserId()) ? session.getTutor(): session.getStudent();
        is_student = (session.getStudentId() == user_cur.getUserId()) ? true : false;
        sendPushNotification();
        setUpViews();
        Log.d(LOG_TAG,"Session action success");
    }

    @Override
    public void onSessionFails(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRatingChanged(RatingBar ratingBar, float rating, boolean b) {
        this.rating = (int) rating;
    }
}

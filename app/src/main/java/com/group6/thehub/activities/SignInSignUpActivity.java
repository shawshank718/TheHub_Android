package com.group6.thehub.activities;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.group6.thehub.AppHelper;
import com.group6.thehub.R;
import com.group6.thehub.Rest.models.UserDetails;
import com.group6.thehub.Rest.responses.UserResponse;
import com.group6.thehub.fragments.SignInFragment;
import com.group6.thehub.fragments.SignUpFragment;
import com.parse.ParseException;
import com.parse.ParsePush;
import com.parse.SaveCallback;

public class SignInSignUpActivity extends AppCompatActivity implements SignInFragment.OnFragmentInteractionListener, SignUpFragment.OnFragmentInteractionListener, UserResponse.UserVerificationListener {

    private AppHelper appHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_sign_up);
        appHelper = new AppHelper(this);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.primary_color_dark));
        }

        if (UserResponse.checkUserLogin(this)) {
            goToHome(UserResponse.getUserDetails(this));
        } else {
            if (savedInstanceState == null) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.container, new SignInFragment())
                        .commit();
            }
        }


    }

    @Override
    public void openSignIn(View view) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, new SignInFragment())
                .commit();
    }

    @Override
    public void callSignUp(String firstName, String lastName, String email, String password, String type, long time) {
        UserResponse.callRegisterUser(this, firstName, lastName, email, password, type, time);

    }

    @Override
    public void openSignupFragment(View view) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, new SignUpFragment())
                .commit();
    }

    @Override
    public void callSignIn(String email, String password) {
        UserResponse.callLoginUser(this, email, password);
    }

    @Override
    public void onRegistrationComplete(UserDetails userDetails) {
        String email = userDetails.getEmail();
        ParsePush.subscribeInBackground(email, new SaveCallback() {
            @Override
            public void done(ParseException e) {
                Log.d("SignInActivity", "subscribed");
            }
        });
        goToHome(userDetails);
    }

    @Override
    public void onSignInComplete(UserDetails userDetails) {
        String channel = userDetails.getFullName()+""+userDetails.getUserId();
        ParsePush.subscribeInBackground(channel, new SaveCallback() {
            @Override
            public void done(ParseException e) {
                Log.d("SignInActivity", "subscribed");
            }
        });
        goToHome(userDetails);
    }


    public void goToHome(UserDetails userDetails) {
        UserResponse.saveUserDetails(this, userDetails);
        Intent intent = new Intent(this, HomeActivity.class);
        appHelper.slideUpPushUp(intent);
        finish();
    }

    @Override
    public void onFail(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

}

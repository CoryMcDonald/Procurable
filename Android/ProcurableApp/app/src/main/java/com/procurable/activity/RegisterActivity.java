package com.procurable.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.procurable.capstone.R;
import com.procurable.constants.Constants;
import com.procurable.domain.request.RegisterRequest;
import com.procurable.domain.response.Department;
import com.procurable.domain.response.GenericResponse;
import com.procurable.service.ProcurableService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private EditText mPassword1View;
    private EditText mPassword2View;
    private View mProgressView;
    private View mRegisterFormView;
    private Context context = this;
    private TextView mEmailView;
    private Toolbar toolbar;
    private Spinner spinner;
    private Integer[] ids;
    private Integer selectedDepartment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        setUpToolbar();
        setUpSpinner();
        // Set up the login form.
        mEmailView = (TextView) findViewById(R.id.register_email);

        mPassword1View = (EditText) findViewById(R.id.password1);
        mPassword2View = (EditText) findViewById(R.id.password2);

        Button mEmailRegisterButton = (Button) findViewById(R.id.email_register_button);
        mEmailRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegister();
            }
        });
        mRegisterFormView = findViewById(R.id.register_form);
        mProgressView = findViewById(R.id.register_progress);
    }

    private void attemptRegister() {

        // Reset errors.
        mEmailView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPassword1View.getText().toString();
        String confirmPassword = mPassword2View.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            mPassword1View.setError(getString(R.string.error_password_empty));
            focusView = mPassword1View;
            cancel = true;
        }
        if (TextUtils.isEmpty(confirmPassword)) {
            mPassword2View.setError(getString(R.string.error_confirm_password_empty));
            focusView = mPassword2View;
            cancel = true;
        }
        if (!password.equalsIgnoreCase(confirmPassword)) {
            mPassword1View.setError(getString(R.string.error_password_mismatch));
            mPassword2View.setError(getString(R.string.error_password_mismatch));
            focusView = mPassword1View;
            cancel = true;
        }
        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            ProcurableService procurableService = Constants.retrofit.create(ProcurableService.class);
            RegisterRequest registerRequest = new RegisterRequest(email, password, confirmPassword, selectedDepartment);
            Call<GenericResponse> call = procurableService.register(registerRequest);
            call.enqueue(new Callback<GenericResponse>() {
                @Override
                public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {
                    if (response.body().getSucceeded()) {
                        Intent intent = new Intent(RegisterActivity.this, SearchActivity.class);
                        startActivity(intent);
                    } else {
                        showProgress(false);
                        //TODO: Decide if I want this to be a factory
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                                context);
                        alertDialogBuilder.setTitle("Error ");
                        alertDialogBuilder
                                .setMessage(response.body().getError())
                                .setCancelable(false)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                    }
                                });
                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                    }
                }

                @Override
                public void onFailure(Call<GenericResponse> call, Throwable t) {
                    showProgress(false);

                }
            });
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mRegisterFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private void setUpToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Register");
    }

    public void setUpSpinner() {

        spinner = (Spinner) findViewById(R.id.spinner);
        final List<String> list = new ArrayList<String>();
        ProcurableService procurableService = Constants.retrofit.create(ProcurableService.class);
        Call<List<Department>> call = procurableService.departments();
        call.enqueue(new Callback<List<Department>>() {
            @Override
            public void onResponse(Call<List<Department>> call, Response<List<Department>> response) {
                ids = new Integer[response.body().size()];
                for (int i = 0; i < response.body().size(); i++) {
                    list.add(response.body().get(i).getName());
                    ids[i] = response.body().get(i).getID();
                }
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(RegisterActivity.this,
                        android.R.layout.simple_spinner_item, list);
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        selectedDepartment = ids[position];
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                spinner.setAdapter(dataAdapter);
            }

            @Override
            public void onFailure(Call<List<Department>> call, Throwable t) {

            }
        });


    }


}

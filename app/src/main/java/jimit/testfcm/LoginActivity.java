package jimit.testfcm;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.JsonElement;

import java.util.HashMap;
import java.util.Map;

import jimit.testfcm.utils.Toaster;
import jimit.testfcm.utils.network.NetworkUtils;
import jimit.testfcm.utils.network.RetrofitServiceGenerator;
import jimit.testfcm.utils.storage.Prefs;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();
    private static final String PASSWORD_PATTERN = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{8,20})";

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView, mUsernameView;
    private View mProgressView;
    private View mLoginFormView;

    private boolean isRegister = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toaster.init(this);
        // Set up the login form.
        mEmailView = findViewById(R.id.email);
        mUsernameView = findViewById(R.id.username);

        mPasswordView = findViewById(R.id.password);

        Button mEmailSignInButton = findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                isRegister = false;
                attemptLogin();
            }
        });

        Button mRegisterButton = findViewById(R.id.register);
        mRegisterButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                isRegister = true;
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        // Reset errors.
        mUsernameView.setError(null);
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String username = mUsernameView.getText().toString();
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.

        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(username)) {
            mUsernameView.setError(getString(R.string.error_field_required));
            focusView = mUsernameView;
            cancel = true;
        } else if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
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
            attemptLogin(username, email, password);
        }
    }

    private boolean isEmailValid(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isPasswordValid(String password) {
        return password.length() >= 8 /*&& password.matches(PASSWORD_PATTERN)*/;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
    }

    private void attemptLogin(String username, String email, String password) {
        if (NetworkUtils.haveNetworkConnection(this)) {
            try {
                IUser client = RetrofitServiceGenerator.createService(IUser.class);
                Callback<JsonElement> callback = new Callback<JsonElement>() {
                    @Override
                    public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                        if (BuildConfig.DEBUG)
                            Log.d(TAG, "onResponse: " + response.body());
                        try {
                            String loginKey = response.body().getAsJsonObject().get(Config.KEY_LOGIN).getAsString();
                            Prefs.setEncryptString(LoginActivity.this, Config.KEY_LOGIN, loginKey);
                            startActivity(new Intent(LoginActivity.this, FcmActivity.class));
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toaster.shortToast(getString(R.string.error_internal));
                        } finally {
                            showProgress(false);
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonElement> call, Throwable t) {
                        showProgress(false);
                        Toaster.shortToast(getString(R.string.error_internal));
                    }
                };

                Call<JsonElement> call;
                Map<String, String> requestBody = new HashMap<>();
                requestBody.put("username", username);
                requestBody.put("email", email);
                if (isRegister) {
                    requestBody.put("password1", password);
                    requestBody.put("password2", password);
                    call = client.login(requestBody);
                } else {
                    requestBody.put("password", password);
                    call = client.register(requestBody);
                }
                
                call.enqueue(callback);
            } catch (Exception e) {
                showProgress(false);
                Toaster.shortToast("Internal Error. Please try again after sometime");
            }
        } else {
            showProgress(false);
            Toaster.shortToast("Internet not available");
        }
    }
}


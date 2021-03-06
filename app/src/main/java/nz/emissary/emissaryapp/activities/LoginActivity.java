package nz.emissary.emissaryapp.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import nz.emissary.emissaryapp.Constants;
import nz.emissary.emissaryapp.R;
import nz.emissary.emissaryapp.User;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;
    private static final int REQUEST_SIGNUP = 1;
    public static final String AUTH_TOKEN_EXTRA = "authToken";

    final Firebase ref = new Firebase(Constants.FIREBASE_BASE);

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    //private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private EditText mPasswordConfirmView;

    private Button mSignupLink;
    private Button mEmailSignInButton;
    private TextView mForgotPasswordLink;
    private LinearLayout nameLayout;
    private EditText mPhoneView;

    private AutoCompleteTextView mFirstName;
    private AutoCompleteTextView mLastName;

    private View mProgressView;
    private View mLoginFormView;
    private boolean signup;

    private TextView mBecomeDriverTextView;
    private CheckBox mBecomeDriverCheckView;

    private int count = 0;

    protected Toolbar toolbar;

    private User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        TextView tv = (TextView) findViewById(R.id.toolbar_title);
        Typeface face = Typeface.createFromAsset(getAssets(),
                "fonts/emissary_font_main.ttf");
        tv.setTypeface(face);

        // Set up the login form.
        signup = false;
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        mFirstName = (AutoCompleteTextView) findViewById(R.id.first_name);
        mLastName = (AutoCompleteTextView) findViewById(R.id.last_name);
        populateAutoComplete();

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        mBecomeDriverTextView = (TextView)findViewById(R.id.become_driver_text);
        mBecomeDriverCheckView = (CheckBox) findViewById(R.id.become_driver);
        mBecomeDriverCheckView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    mBecomeDriverTextView.setVisibility(View.VISIBLE);
                }else{
                    mBecomeDriverTextView.setVisibility(View.GONE);
                }
            }
        });

        mPasswordConfirmView = (EditText) findViewById(R.id.password_confirm);
        nameLayout = (LinearLayout) findViewById(R.id.name_fields);

        mPhoneView = (EditText) findViewById(R.id.phone);

        mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mForgotPasswordLink = (TextView) findViewById(R.id.forgot_password_link);
        mForgotPasswordLink.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder =
                        new AlertDialog.Builder(LoginActivity.this, R.style.MyAlertDialogStyle2);

                LayoutInflater inflater = getLayoutInflater();
                final View dialogView = inflater.inflate(R.layout.dialog_reset_password, null);
                builder.setView(dialogView);

                final EditText edt = (EditText) dialogView.findViewById(R.id.edit1);

                builder.setTitle(R.string.reset_email_dialog_title);
                builder.setPositiveButton("Reset Password", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ref.resetPassword(edt.getText().toString(), new Firebase.ResultHandler() {
                            @Override
                            public void onSuccess() {
                                Toast t = Toast.makeText(getApplicationContext(), "Password reset email sent!", Toast.LENGTH_SHORT);
                                t.show();
                            }
                            @Override
                            public void onError(FirebaseError firebaseError) {
                                Toast t = Toast.makeText(getApplicationContext(), "Error. Please try again.", Toast.LENGTH_SHORT);
                                t.show();
                            }
                        });
                    }
                });
                builder.setNegativeButton("Cancel", null);

                AppCompatDialog dialog = builder.create();
                dialog.show();
            }
        });

        mSignupLink = (Button) findViewById(R.id.link_signup);
        mSignupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!signup) {
                    mPhoneView.setVisibility(View.VISIBLE);
                    nameLayout.setVisibility(View.VISIBLE);
                    mPasswordConfirmView.setVisibility(View.VISIBLE);
                    mBecomeDriverCheckView.setVisibility(View.VISIBLE);
                    mEmailSignInButton.setText(R.string.action_signup);
                    mSignupLink.setText("Return to login");
                    mFirstName.requestFocus();
                    signup = true;
                }else{
                    mPhoneView.setVisibility(View.GONE);
                    nameLayout.setVisibility(View.GONE);
                    mPasswordConfirmView.setVisibility(View.GONE);
                    mBecomeDriverCheckView.setVisibility(View.GONE);
                    mBecomeDriverTextView.setVisibility(View.GONE);
                    mBecomeDriverCheckView.setChecked(false);
                    mEmailSignInButton.setText(R.string.action_sign_in);
                    mSignupLink.setText("Register");
                    mEmailView.requestFocus();
                    signup = false;
                }
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {
                Log.d("EMISSARY", "OPLK");
            }
        }
    }

    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }

        getLoaderManager().initLoader(0, null, this);
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(mEmailView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        /*if (mAuthTask != null) {
            return;
        }
        */

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        String passwordConfirm = mPasswordConfirmView.getText().toString();

        String phone = mPhoneView.getText().toString();

        String firstName = mFirstName.getText().toString();
        String lastName = mLastName.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check in passwords match.
        if (signup && !password.equals(passwordConfirm)){
            mPasswordConfirmView.setError(getString(R.string.error_password_mismatch));
            focusView = mPasswordConfirmView;
            cancel = true;
        }

        // Check for a phone number
        if (signup && TextUtils.isEmpty(phone)){
            mPhoneView.setError(getString(R.string.error_field_required));
            focusView = mPhoneView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        //Check for first and lastname
        if (signup && TextUtils.isEmpty(firstName)){
            mFirstName.setError(getString(R.string.error_field_required));
            focusView = mFirstName;
            cancel = true;
        }
        if (signup && TextUtils.isEmpty(firstName)){
            mLastName.setError(getString(R.string.error_field_required));
            focusView = mLastName;
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
            //mAuthTask = new UserLoginTask(email, password);
            //mAuthTask.execute((Void) null);

            login(email, password, firstName, lastName, phone);
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        //return password.length() > 4;
        return true;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
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
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'vector_profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }


    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    private void login(final String email, String password, final String firstName, final String lastName, final String phone){
        if (signup){
            ref.createUser(email, password, new Firebase.ValueResultHandler<Map<String, Object>>() {
                @Override
                public void onSuccess(Map<String, Object> result) {
                    boolean driverIsChecked = mBecomeDriverCheckView.isChecked();
                    mBecomeDriverTextView.setVisibility(View.GONE);
                    mBecomeDriverCheckView.setChecked(false);
                    String uid = result.get("uid").toString();

                    Firebase firebaseUser = ref.child(Constants.FIREBASE_USERS_BASE_CHILD).child(uid);

                    user = new User();
                    user.setEmail(email);
                    user.setFirstName(firstName);
                    user.setLastName(lastName);
                    user.setPhone(phone);
                    if (driverIsChecked){
                        user.setIsDriver(Constants.DRIVER_PENDING);
                    }else{
                        user.setIsDriver(Constants.DRIVER_NO);
                    }

                    firebaseUser.setValue(user);

                    showProgress(false);
                    mPhoneView.setVisibility(View.GONE);
                    nameLayout.setVisibility(View.GONE);
                    mPasswordConfirmView.setVisibility(View.GONE);
                    mEmailSignInButton.setText(R.string.action_sign_in);
                    mSignupLink.setText("Register");
                    signup = false;
                    Toast.makeText(LoginActivity.this, "Account created!", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(FirebaseError firebaseError) {
                    showProgress(false);

                    nameLayout.setVisibility(View.VISIBLE);
                    mPhoneView.setVisibility(View.VISIBLE);
                    mEmailView.setVisibility(View.VISIBLE);
                    mPasswordView.setVisibility(View.VISIBLE);
                    mPasswordConfirmView.setVisibility(View.VISIBLE);

                    mSignupLink.setText("Return to login");
                    mFirstName.requestFocus();
                    signup = true;

                    String errorMsg = "";

                    switch(firebaseError.getCode()){
                        case FirebaseError.EMAIL_TAKEN:
                            errorMsg = "The specified email is already in use";
                            break;
                        case FirebaseError.DISCONNECTED:
                            errorMsg = "Operation aborted due to network disconnect";
                            break;
                        case FirebaseError.INVALID_EMAIL:
                            errorMsg = "The specified email is not a valid email";
                            break;
                        case FirebaseError.LIMITS_EXCEEDED:
                            errorMsg = "Limits exceeded.";
                            break;
                        case FirebaseError.NETWORK_ERROR:
                            errorMsg = "The operation could not be performed due to a network error";
                            break;
                        default:
                            errorMsg = "An account creation error has occurred";
                    }

                    Toast.makeText(LoginActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                }
            });
        }else {
            ref.authWithPassword(email, password, new Firebase.AuthResultHandler() {

                @Override
                public void onAuthenticated(final AuthData authData) {
                    // Authentication just completed successfully :)

                    Date d = new Date();
                    final Long lastLogin = d.getTime();

                    final Firebase firebaseUser = ref.child(Constants.FIREBASE_USERS_BASE_CHILD).child(authData.getUid());
                    firebaseUser.child("lastLoginDate").setValue("" + lastLogin);

                    firebaseUser.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            User user = dataSnapshot.getValue(User.class);
                            int isDriver = user.getIsDriver();

                            if (isDriver == Constants.DRIVER_NO){
                                Intent result = new Intent(LoginActivity.this, ViewMyListingsActivity.class);
                                setResult(RESULT_OK, result);
                                result.putExtra(AUTH_TOKEN_EXTRA, authData.getToken());
                                finish();
                            }else if (isDriver == Constants.DRIVER_PENDING) {
                                Intent result = new Intent(LoginActivity.this, SetupDriverAccount.class)
                                        .putExtra("user_id", dataSnapshot.getKey())
                                        .putExtra("user_name", user.getFirstName())
                                        .putExtra("user_phone", user.getPhone());;
                                setResult(RESULT_OK, result);
                                result.putExtra(AUTH_TOKEN_EXTRA, authData.getToken());
                                finish();
                            }else if (isDriver == Constants.DRIVER_YES){
                                Intent result = new Intent(LoginActivity.this, ViewPublicListingsActivity.class);
                                setResult(RESULT_OK, result);
                                result.putExtra(AUTH_TOKEN_EXTRA, authData.getToken());
                                finish();
                            }
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {

                        }
                    });
                }

                @Override
                public void onAuthenticationError(FirebaseError firebaseError) {
                    showProgress(false);
                    mPasswordView.setError(getString(R.string.error_incorrect_password));
                    mPasswordView.requestFocus();
                    // there was an error
                    Log.d("EmissaryAppDEBUG", "Cannot login user. " + firebaseError);
                }
            });
        }
    }
}



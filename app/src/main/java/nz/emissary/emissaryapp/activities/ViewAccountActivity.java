package nz.emissary.emissaryapp.activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import org.w3c.dom.Text;

import nz.emissary.emissaryapp.R;
import nz.emissary.emissaryapp.User;

/**
 * Created by Simon on 3/03/2016.
 */
public class ViewAccountActivity extends AppCompatActivity {

    TextView usernameView;
    TextView emailView;
    TextView firstNameView;
    TextView lastNameView;
    TextView phoneView;

    TextView driverId;

    TextView changePasswordView;
    TextView viewMyFeedbackView;

    Button logoutButton;

    ProgressBar progressBar;
    Firebase mRef;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_account);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mRef = new Firebase("https://emissary.firebaseio.com");

        usernameView = (TextView) findViewById(R.id.username);
        emailView = (TextView) findViewById(R.id.email);
        logoutButton = (Button) findViewById(R.id.logout_button);
        firstNameView = (TextView) findViewById(R.id.first_name);
        lastNameView = (TextView) findViewById(R.id.last_name);
        changePasswordView = (TextView) findViewById(R.id.password_change);
        progressBar = (ProgressBar) findViewById(R.id.updateProgressBar);
        phoneView = (TextView) findViewById(R.id.phone);
        viewMyFeedbackView = (TextView) findViewById(R.id.my_feedback_link);
        driverId = (TextView) findViewById(R.id.driver_id);

        emailView.setText(mRef.getAuth().getProviderData().get("email").toString());

        Firebase currentFirebaseUser = new Firebase("https://emissary.firebaseio.com/users/" + mRef.getAuth().getUid());
        currentFirebaseUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);
                firstNameView.setText(user.getFirstName());
                lastNameView.setText(user.getLastName());
                phoneView.setText(user.getPhone());
                driverId.setText(mRef.getAuth().getUid());
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        viewMyFeedbackView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ViewMyFeedbackActivity.class);
                v.getContext().startActivity(intent);
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Firebase ref = new Firebase("https://emissary.firebaseio.com");
                ref.unauth();
                // FLAG_ACTIVITY_CLEAR_TASK only works on API 11, so if the user
                // logs out on older devices, we'll just exit.
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    Intent intent = new Intent(ViewAccountActivity.this,
                            HomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                            | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } else {
                    finish();
                }
            }
        });

        changePasswordView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder =
                        new AlertDialog.Builder(ViewAccountActivity.this, R.style.MyAlertDialogStyle2);

                LayoutInflater inflater = getLayoutInflater();
                final View dialogView = inflater.inflate(R.layout.change_password_dialog, null);
                builder.setView(dialogView);

                final EditText oldPasswordView = (EditText) dialogView.findViewById(R.id.old_password);
                final EditText newPasswordView = (EditText) dialogView.findViewById(R.id.new_password);
                final EditText newPasswordConfirmView = (EditText) dialogView.findViewById(R.id.new_password_confirm);

                builder.setTitle(R.string.change_password_dialog_title);
                builder.setPositiveButton("Change Password", null);
                builder.setNegativeButton("Cancel", null);

                final AlertDialog tempDialog = builder.create();

                tempDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {
                        Button b = tempDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                        b.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View view) {
                                if (newPasswordView.getText().toString().equals(newPasswordConfirmView.getText().toString())){
                                    progressBar.setVisibility(View.VISIBLE);
                                    Firebase ref = new Firebase("https://emissary.firebaseio.com");
                                    ref.changePassword(emailView.getText().toString(), oldPasswordView.getText().toString(), newPasswordView.getText().toString(), new Firebase.ResultHandler() {
                                        @Override
                                        public void onSuccess() {
                                            Toast t = Toast.makeText(getApplicationContext(), "Password sucessfully changed!", Toast.LENGTH_SHORT);
                                            t.show();
                                            progressBar.setVisibility(View.GONE);
                                        }
                                        @Override
                                        public void onError(FirebaseError firebaseError) {
                                            Log.d("EMISSARY", firebaseError.toString());
                                            Toast t = Toast.makeText(getApplicationContext(), "Make sure the old password is correct", Toast.LENGTH_SHORT);
                                            t.show();
                                            progressBar.setVisibility(View.GONE);
                                        }
                                    });
                                    tempDialog.dismiss();
                                }else{
                                    newPasswordView.setError("Passwords do not match");
                                    newPasswordView.requestFocus();
                                    newPasswordConfirmView.setText("");
                                }
                            }
                        });
                    }
                });

                AppCompatDialog dialog  = tempDialog;
                dialog.show();
            }
        });
    }


}
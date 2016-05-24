package nz.emissary.emissaryapp.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.Date;

import nz.emissary.emissaryapp.Constants;
import nz.emissary.emissaryapp.R;
import nz.emissary.emissaryapp.User;

public class SplashActivity extends Activity {

    private final int SPLASH_DISPLAY_LENGTH = 1000;

    Firebase ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        TextView tv = (TextView) findViewById(R.id.logo_text_drawable);
        Typeface face = Typeface.createFromAsset(getAssets(),
                "fonts/emissary_font_main.ttf");
        tv.setTypeface(face);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ref = new Firebase(Constants.FIREBASE_BASE);
                ref.addAuthStateListener(new Firebase.AuthStateListener() {
                    @Override
                    public void onAuthStateChanged(AuthData authData) {
                        if (authData == null) {
                            Intent loginActivity = new Intent(SplashActivity.this, LoginActivity.class);
                            SplashActivity.this.startActivity(loginActivity);
                        }else{
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
                                        Intent result = new Intent(SplashActivity.this, ViewMyListingsActivity.class);
                                        SplashActivity.this.startActivity(result);
                                    }else if (isDriver == Constants.DRIVER_PENDING) {
                                        Intent result = new Intent(SplashActivity.this, SetupDriverAccount.class);
                                        SplashActivity.this.startActivity(result);
                                    }else if (isDriver == Constants.DRIVER_YES){
                                        Intent result = new Intent(SplashActivity.this, ViewPublicListingsActivity.class);
                                        SplashActivity.this.startActivity(result);
                                    }
                                }

                                @Override
                                public void onCancelled(FirebaseError firebaseError) {

                                }
                            });
                        }
                        SplashActivity.this.finish();
                    }
                });
            }
        }, SPLASH_DISPLAY_LENGTH);

    }
}

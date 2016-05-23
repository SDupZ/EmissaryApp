package nz.emissary.emissaryapp.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.redbooth.WelcomeCoordinatorLayout;

import nz.emissary.emissaryapp.R;

/**
 * Created by Simon on 3/03/2016.
 */
public class SetupDriverAccount extends AppCompatActivity {
    private ProgressBar progressBar;
    private String userId;

    private ImageView carView;
    private ImageView vanView;
    private ImageView motorcycleView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_setup_driver_account);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(Color.TRANSPARENT);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        final WelcomeCoordinatorLayout coordinatorLayout
                = (WelcomeCoordinatorLayout)findViewById(R.id.coordinator);
        coordinatorLayout.addPage(
                R.layout.pager_driver_account_initial,
                R.layout.pager_driver_account_verify_phone,
                R.layout.pager_driver_account_add_vehicle,
                R.layout.pager_driver_account_payment,
                R.layout.pager_driver_account_complete);

        Intent intent = getIntent();
        if(intent != null && intent.hasExtra("user_id") && intent.hasExtra("user_name")) {
            userId = intent.getStringExtra("object_id");
            String firstName = intent.getStringExtra("user_name");
            String phoneNumber = intent.getStringExtra("user_phone");
            ((TextView)coordinatorLayout.findViewById(R.id.welcome_text)).setText("Welcome " + firstName);
            ((TextView)coordinatorLayout.findViewById(R.id.phone_number_view)).setText(phoneNumber);

        }

        carView = (ImageView) coordinatorLayout.findViewById(R.id.vehicle_car);
        vanView = (ImageView) coordinatorLayout.findViewById(R.id.vehicle_van);
        motorcycleView = (ImageView) coordinatorLayout.findViewById(R.id.vehicle_motorcycle);

        carView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
        vanView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
        motorcycleView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
    }

    public void onImageSelected(View view){
        if (view.getId() == R.id.vehicle_car){
            carView.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
            vanView.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.colorGreyedOut));
            motorcycleView.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.colorGreyedOut));
        }else if (view.getId() == R.id.vehicle_motorcycle){
            carView.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.colorGreyedOut));
            vanView.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.colorGreyedOut));
            motorcycleView.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
        }else if (view.getId() == R.id.vehicle_van){
            carView.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.colorGreyedOut));
            vanView.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
            motorcycleView.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.colorGreyedOut));
        }
    }
}
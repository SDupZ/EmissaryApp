package nz.emissary.emissaryapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import nz.emissary.emissaryapp.R;

/**
 * Created by Simon on 3/03/2016.
 */
public class ViewMyDashboardActivity extends AppCompatActivity {

    View viewCompletedDeliveries;
    View paymentView;
    View feedbackView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_my_dashboard);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        viewCompletedDeliveries = findViewById(R.id.trip_history_settings_view);
        paymentView = findViewById(R.id.payment_settings_view);
        feedbackView = findViewById(R.id.feedback_settings_view);

        viewCompletedDeliveries.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ViewMyCompletedListingsActivity.class);
                startActivity(intent);
            }
        });

        feedbackView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ViewMyFeedbackActivity.class);
                startActivity(intent);
            }
        });
    }

}
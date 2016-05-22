package nz.emissary.emissaryapp.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.firebase.client.Query;
import com.firebase.ui.FirebaseRecyclerAdapter;
import com.redbooth.WelcomeCoordinatorLayout;

import nz.emissary.emissaryapp.Constants;
import nz.emissary.emissaryapp.Feedback;
import nz.emissary.emissaryapp.R;

/**
 * Created by Simon on 3/03/2016.
 */
public class SetupDriverAccount extends AppCompatActivity {
    private ProgressBar progressBar;
    private String userId;

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
        coordinatorLayout.addPage(R.layout.pager_driver_account_one, R.layout.pager_driver_account_two);

        Intent intent = getIntent();
        if(intent != null && intent.hasExtra("user_id") && intent.hasExtra("user_name")) {
            userId = intent.getStringExtra("object_id");
            String firstName = intent.getStringExtra("user_name");
            ((TextView)coordinatorLayout.findViewById(R.id.welcome_text)).setText("Welcome " + firstName);
        }
    }
}
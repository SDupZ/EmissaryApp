package nz.emissary.emissaryapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;

import org.w3c.dom.Text;

import nz.emissary.emissaryapp.Constants;
import nz.emissary.emissaryapp.Delivery;
import nz.emissary.emissaryapp.R;

/**
 * Created by Simon on 3/03/2016.
 */
public class ViewDriverStatisticsActivity extends AppCompatActivity {

    private int deliveriesCompleted;
    private double totalDistance;
    private double longestJourney;

    TextView deliveriesCompletedView;
    TextView totalDistanceView;
    TextView longestJourneyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_driver_statistics);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        deliveriesCompletedView = (TextView) findViewById(R.id.completed_deliveries_view);
        totalDistanceView = (TextView) findViewById(R.id.distance_travelled_view);
        longestJourneyView = (TextView) findViewById(R.id.longest_journey_view);

        deliveriesCompleted = 0;
        totalDistance = 0;
        longestJourney = 0;

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        final Firebase mRef = new Firebase(Constants.FIREBASE_DELIVERIES_UNLISTED);
        Query queryRef = mRef.orderByChild("driver").equalTo(mRef.getAuth().getUid());

        queryRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot snapshot, String previousChild) {
                Delivery d = snapshot.getValue(Delivery.class);

                deliveriesCompleted += 1;
                totalDistance += d.getDistance();
                longestJourney = longestJourney > d.getDistance() ? longestJourney : d.getDistance();

                deliveriesCompletedView.setText("" + deliveriesCompleted);
                totalDistanceView.setText("" + totalDistance + "km");
                longestJourneyView.setText("" + longestJourney + "km");
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

    }

}
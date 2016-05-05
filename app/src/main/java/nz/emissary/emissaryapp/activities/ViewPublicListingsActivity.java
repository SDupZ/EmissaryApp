package nz.emissary.emissaryapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.Query;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.ui.FirebaseRecyclerAdapter;

import nz.emissary.emissaryapp.Constants;
import nz.emissary.emissaryapp.CustomFirebaseListingsAdapter;
import nz.emissary.emissaryapp.R;


/**
 * Created by Simon on 1/03/2016.
 */

public class ViewPublicListingsActivity extends BaseActivity{

    static final int REQUEST_AUTH_TOKEN = 0;

    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        progressBar = (ProgressBar) findViewById(R.id.updateProgressBar);
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Firebase ref = new Firebase(Constants.FIREBASE_BASE);
        ref.addAuthStateListener(new Firebase.AuthStateListener() {
            @Override
            public void onAuthStateChanged(AuthData authData) {
                if (authData == null) {
                    Intent intentWithToken = new Intent(ViewPublicListingsActivity.this, LoginActivity.class);
                    startActivity(intentWithToken);
                }
            }
        });
    }


    protected int getLayoutResource(){
        return R.layout.activity_view_public_listings;
    }

    public static class DeliveryListFragment extends Fragment {

        private RecyclerView mRecyclerView;
        private RecyclerView.LayoutManager mLayoutManager;

        public static DeliveryListFragment newInstance(int sectionNumber) {
            DeliveryListFragment fragment = new DeliveryListFragment();
            Bundle args = new Bundle();
            fragment.setArguments(args);
            return fragment;
        }

        public DeliveryListFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_delivery_list, container, false);

            mRecyclerView = (RecyclerView) rootView.findViewById(R.id.my_recycler_view);
            mRecyclerView.setHasFixedSize(true);

            // use a linear layout manager
            mLayoutManager = new LinearLayoutManager(getActivity());
            mRecyclerView.setLayoutManager(mLayoutManager);

            final TextView noDeliveriesTextView = (TextView) rootView.findViewById(R.id.no_deliveries_text_view);
            noDeliveriesTextView.setVisibility(View.VISIBLE);


            GeoFire geoFire = new GeoFire(new Firebase("https://emissary.firebaseio.com/delivery_geofire/pickup_location"));
            // creates a new query around [37.7832, -122.4056] with a radius of 0.6 kilometers
            GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(-36.718880, 174.729048), 10);

            CustomFirebaseListingsAdapter adapter = new CustomFirebaseListingsAdapter(geoQuery);
            mRecyclerView.setAdapter(adapter);

            mRecyclerView.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
                @Override
                public void onChildViewAttachedToWindow(View view) {
                    ((ViewPublicListingsActivity)getActivity()).progressBar.setVisibility(View.GONE);
                    noDeliveriesTextView.setVisibility(View.GONE);
                }
                @Override
                public void onChildViewDetachedFromWindow(View view) {

                }
            });

            return rootView;
        }



    }
}

package nz.emissary.emissaryapp.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.Query;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.ui.FirebaseRecyclerAdapter;

import org.w3c.dom.Text;

import java.util.ArrayList;

import nz.emissary.emissaryapp.Constants;
import nz.emissary.emissaryapp.CustomFirebaseListingsAdapter;
import nz.emissary.emissaryapp.R;


/**
 * Created by Simon on 1/03/2016.
 */

public class ViewPublicListingsActivity extends BaseActivity{

    static final int REQUEST_AUTH_TOKEN = 0;

    ProgressBar progressBar;

    TextView sortedByInfoText;
    TextView filteredByInfoText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progressBar = (ProgressBar) findViewById(R.id.updateProgressBar);
        progressBar.setVisibility(View.VISIBLE);

        sortedByInfoText = (TextView) findViewById(R.id.sorted_by_info_text_view);
        filteredByInfoText = (TextView) findViewById(R.id.filtered_by_info_text_view);

        DeliveryListFragment child = (DeliveryListFragment) getSupportFragmentManager().findFragmentById(R.id.delivery_list_fragment);

        sortedByInfoText.setText(Html.fromHtml("Sorted by: <b>Alphabetical</b>"));
        filteredByInfoText.setText(Html.fromHtml("Filtering: <b>" + child.radius  + "km</b> from <b>Current Location<b>"));
    }

    public void setFilterTextDistance(String distanceText, String locationText){
        filteredByInfoText.setText(Html.fromHtml("Filtering: <b>" + distanceText  + "km</b> from <b>" + locationText + " <b>"));
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_public_listings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_location:
                AlertDialog.Builder builder =
                        new AlertDialog.Builder(ViewPublicListingsActivity.this, R.style.MyAlertDialogStyle2);

                LayoutInflater inflater = getLayoutInflater();
                final View dialogView = inflater.inflate(R.layout.dialog_filter_location, null);
                builder.setView(dialogView);

                final EditText radiusView = (EditText) dialogView.findViewById(R.id.radius);

                builder.setTitle("Search radius around current location");
                builder.setPositiveButton("Confirm", null);
                builder.setNegativeButton("Cancel", null);

                final AlertDialog tempDialog = builder.create();

                tempDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {
                        Button b = tempDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                        b.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String radiusString = radiusView.getText().toString().trim();
                                boolean error = false;
                                if (radiusString == ""){
                                    error = true;
                                }else{
                                    try {
                                        double radius = Double.parseDouble(radiusString);
                                        setFilterTextDistance("" + radius, "Current Location");
                                        ((DeliveryListFragment)getSupportFragmentManager().findFragmentById(R.id.delivery_list_fragment)).adapter.updateLocation(radius);
                                    }catch(NumberFormatException e){
                                        error = true;
                                    }
                                }
                                progressBar.setVisibility(View.GONE);
                                if (error){
                                    radiusView.setError("Must be a valid number");
                                    radiusView.requestFocus();
                                    radiusView.setText("");
                                }else{
                                    tempDialog.dismiss();
                                }
                            }
                        });
                    }
                });
                AppCompatDialog dialog  = tempDialog;
                dialog.show();
                return true;

            case R.id.action_sort:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    protected int getLayoutResource(){
        return R.layout.activity_view_public_listings;
    }

    public static class DeliveryListFragment extends Fragment{

        public CustomFirebaseListingsAdapter adapter;
        public double radius;
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

            radius = Constants.DEFAULT_FILTER_RADIUS;

            mRecyclerView = (RecyclerView) rootView.findViewById(R.id.my_recycler_view);
            mRecyclerView.setHasFixedSize(true);

            // use a linear layout manager
            mLayoutManager = new LinearLayoutManager(getActivity());
            mRecyclerView.setLayoutManager(mLayoutManager);

            final TextView noDeliveriesTextView = (TextView) rootView.findViewById(R.id.no_deliveries_text_view);
            noDeliveriesTextView.setVisibility(View.VISIBLE);


            GeoFire geoFire = new GeoFire(new Firebase("https://emissary.firebaseio.com/delivery_geofire/pickup_location"));
            // creates a new query around [37.7832, -122.4056] with a radius of 0.6 kilometers
            GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(-36.718880, 174.729048), radius);

            adapter = new CustomFirebaseListingsAdapter(geoQuery);
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

package nz.emissary.emissaryapp.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.geofire.GeoLocation;

import nz.emissary.emissaryapp.Constants;
import nz.emissary.emissaryapp.CustomFirebaseListingsAdapter;
import nz.emissary.emissaryapp.Delivery;
import nz.emissary.emissaryapp.R;


/**
 * Created by Simon on 1/03/2016.
 */

public class ViewPublicListingsActivity extends BaseActivity{

    static final int REQUEST_AUTH_TOKEN = 0;

    ProgressBar progressBar;

    TextView sortedByInfoText;
    TextView filteredByInfoText;

    View currentlySelected;

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

    public void setFilterTextDistance(String region){
        filteredByInfoText.setText(Html.fromHtml("Filtering: <b>" + region  + "</b>"));
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
        switch (item.getItemId()) {
            // Filter items
            case R.id.action_location:
                AlertDialog.Builder builder =
                        new AlertDialog.Builder(ViewPublicListingsActivity.this, R.style.MyAlertDialogStyle2);

                LayoutInflater inflater = getLayoutInflater();
                final View dialogView = inflater.inflate(R.layout.dialog_filter_location, null);
                builder.setView(dialogView);

                final EditText radiusView = (EditText) dialogView.findViewById(R.id.radius);

                builder.setTitle("Filter by:");
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
                                double radius = 0;
                                boolean error = false;
                                GeoLocation location = null;
                                String locationString = "";
                                switch(currentlySelected.getId()) {
                                    case R.id.radio_all:
                                        radius = Constants.LOCATION_RADIUS_AUCKLAND_ALL;
                                        location = Constants.LOCATION_COORD_AUCKLAND_ALL;
                                        setFilterTextDistance("All");
                                        break;
                                    case R.id.radio_auckland_all:
                                        radius = Constants.LOCATION_RADIUS_AUCKLAND_ALL;
                                        location = Constants.LOCATION_COORD_AUCKLAND_ALL;
                                        setFilterTextDistance("All Auckland");
                                        break;
                                    case R.id.radio_current_location:
                                        String radiusString = radiusView.getText().toString().trim();
                                        if (radiusString == ""){
                                            error = true;
                                        }else{
                                            try {
                                                radius = Double.parseDouble(radiusString);
                                                location = Constants.LOCATION_COORD_DEFAULT;
                                                setFilterTextDistance("" + radius, "Current Location");
                                            }catch(NumberFormatException e){
                                                error = true;
                                            }
                                         }
                                        break;
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

            //Sort Items
            case R.id.action_sort:
                AlertDialog.Builder builder2 =
                        new AlertDialog.Builder(ViewPublicListingsActivity.this, R.style.MyAlertDialogStyle2);

                LayoutInflater inflater2 = getLayoutInflater();
                final View dialogView2 = inflater2.inflate(R.layout.dialog_sort_deliveries, null);
                builder2.setView(dialogView2);

                builder2.setTitle("Sort by:");
                builder2.setPositiveButton("Confirm", null);
                builder2.setNegativeButton("Cancel", null);

                final AlertDialog tempDialog2 = builder2.create();

                tempDialog2.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {
                        Button b = tempDialog2.getButton(AlertDialog.BUTTON_POSITIVE);
                        b.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                DeliveryListFragment frag = ((DeliveryListFragment) getSupportFragmentManager().findFragmentById(R.id.delivery_list_fragment));
                                switch(currentlySelected.getId()) {
                                    case R.id.radio_sort_alphabetical:
                                        frag.adapter.setComparator(new Delivery.AlphabeticalComparator());
                                        break;

                                    case R.id.radio_sort_total_distance:
                                        frag.adapter.setComparator(new Delivery.TotalDistanceComparator());
                                        break;
                                }
                                tempDialog2.dismiss();
                            }
                        });
                    }
                });
                AppCompatDialog dialog2  = tempDialog2;
                dialog2.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        if (checked)
            currentlySelected = view;
    }

    public void onCheckboxClicked(View view) {
        boolean checked = ((CheckBox) view).isChecked();
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

            adapter = new CustomFirebaseListingsAdapter();
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

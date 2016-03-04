package nz.emissary.emissaryapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import nz.emissary.emissaryapp.R;

/**
 * Created by Simon on 3/03/2016.
 */
public class CreateDeliveryActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_delivery);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        // Connected to Google Play services!
        // The good stuff goes here.
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // The connection has been interrupted.
        // Disable any UI components that depend on Google APIs
        // until onConnected() is called.
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // This callback is important for handling errors that
        // may occur while attempting to connect with Google.
        //
        // More about this in the 'Handle Connection Failures' section.
    }

    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    //CreateDelivery fragment
    public static class CreateDeliveryFragment extends Fragment implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

        TextView pickupDateTextView;
        TextView pickupTimeOneTextView;
        TextView pickupTimeTwoTextView;
        TextView pickupLocationTextView;

        TextView dropoffDateTextView;
        TextView dropoffTimeOneTextView;
        TextView dropoffTimeTwoTextView;
        TextView dropOffLocationTextView;

        int timeDialog;     // 1 = pickup, 2 = dropoff
        int dateDialog;     // 1 = pickup time one, 2 = pickup time two, 3 = dropoff time one, ...

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static CreateDeliveryFragment newInstance(int sectionNumber) {
            CreateDeliveryFragment fragment = new CreateDeliveryFragment();
            Bundle args = new Bundle();
            fragment.setArguments(args);
            return fragment;
        }

        public CreateDeliveryFragment() {
        }

        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            if (resultCode == RESULT_OK) {
                if (requestCode == 1 || requestCode == 2) {
                    Place place = PlacePicker.getPlace(getActivity(), data);
                    String placeMsg = place.getAddress().toString();

                    if (requestCode == 1){
                        pickupLocationTextView.setText(placeMsg);
                    }else if (requestCode == 2){
                        dropOffLocationTextView.setText(placeMsg);
                    }
                }
            }

        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            final View rootView = inflater.inflate(R.layout.create_delivery_fragment, container, false);

            this.timeDialog = 1;
            this.dateDialog = 1;

            Calendar now = Calendar.getInstance();
            final DatePickerDialog dpd = DatePickerDialog.newInstance(
                    this,
                    now.get(Calendar.YEAR),
                    now.get(Calendar.MONTH),
                    now.get(Calendar.DAY_OF_MONTH)
            );

            final TimePickerDialog tpd = TimePickerDialog.newInstance(
                    this,
                    now.get(Calendar.HOUR),
                    0,
                    DateFormat.is24HourFormat(this.getActivity())
            );

            final EditText deliveryName = (EditText)rootView.findViewById(R.id.create_delivery_name);
            final EditText deliveryNotes = (EditText)rootView.findViewById(R.id.create_delivery_notes);
            pickupLocationTextView = (TextView)rootView.findViewById(R.id.create_delivery_pickup_location);
            dropOffLocationTextView = (TextView)rootView.findViewById(R.id.create_delivery_dropoff_location);

            final TextView title_5 = (TextView)rootView.findViewById(R.id.title_5);
            final TextView title_dropoff_5 = (TextView)rootView.findViewById(R.id.title_dropoff_5);

            pickupDateTextView = (TextView) rootView.findViewById(R.id.create_delivery_pickup_date);
            dropoffDateTextView = (TextView) rootView.findViewById(R.id.create_delivery_dropoff_date);

            pickupDateTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dateDialog = 1;
                    dpd.show(getActivity().getFragmentManager(), "Datepickerdialog");
                }
            });
            dropoffDateTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dateDialog = 2;
                    dpd.show(getActivity().getFragmentManager(), "Datepickerdialog");
                }
            });

            int year = now.get(Calendar.YEAR);
            int monthOfYear = now.get(Calendar.MONTH);
            int dayOfMonth = now.get(Calendar.DAY_OF_MONTH);

            String dateText = "Today (" + dayOfMonth +"/"+(monthOfYear+1)+"/"+ year + ")";
            pickupDateTextView.setText(dateText);
            dropoffDateTextView.setText(dateText);

            pickupTimeOneTextView = (TextView) rootView.findViewById(R.id.create_delivery_pickup_time_one);
            pickupTimeOneTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    timeDialog = 1;
                    tpd.show(getActivity().getFragmentManager(), "Timepickerdialog");
                }
            });

            pickupTimeTwoTextView = (TextView) rootView.findViewById(R.id.create_delivery_pickup_time_two);
            pickupTimeTwoTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    timeDialog = 2;
                    tpd.show(getActivity().getFragmentManager(), "Timepickerdialog");
                }
            });

            dropoffTimeOneTextView = (TextView) rootView.findViewById(R.id.create_delivery_dropoff_time_one);
            dropoffTimeOneTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    timeDialog = 3;
                    tpd.show(getActivity().getFragmentManager(), "Timepickerdialog");
                }
            });

            dropoffTimeTwoTextView = (TextView) rootView.findViewById(R.id.create_delivery_dropoff_time_two);
            dropoffTimeTwoTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    timeDialog = 4;
                    tpd.show(getActivity().getFragmentManager(), "Timepickerdialog");
                }
            });


            pickupLocationTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        int PLACE_PICKER_REQUEST = 1;
                        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                        startActivityForResult(builder.build(getActivity()), PLACE_PICKER_REQUEST);
                    } catch (GooglePlayServicesRepairableException e) {
                        e.printStackTrace();
                    } catch (GooglePlayServicesNotAvailableException e) {
                        e.printStackTrace();
                    }
                }
            });

            dropOffLocationTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        int PLACE_PICKER_REQUEST = 2;
                        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                        startActivityForResult(builder.build(getActivity()), PLACE_PICKER_REQUEST);
                    } catch (GooglePlayServicesRepairableException e) {
                        e.printStackTrace();
                    } catch (GooglePlayServicesNotAvailableException e) {
                        e.printStackTrace();
                    }
                }
            });


            SwitchCompat specificTimeToggle = (SwitchCompat) rootView.findViewById(R.id.specific_time_toggle_pickup);

            specificTimeToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        title_5.setVisibility(View.VISIBLE);
                        pickupTimeTwoTextView.setVisibility(View.VISIBLE);
                    } else {
                        title_5.setVisibility(View.GONE);
                        pickupTimeTwoTextView.setVisibility(View.GONE);
                    }
                }
            });

            SwitchCompat specificTimeToggleDropoff = (SwitchCompat) rootView.findViewById(R.id.specific_time_toggle_dropff);

            specificTimeToggleDropoff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        title_dropoff_5.setVisibility(View.VISIBLE);
                        dropoffTimeTwoTextView.setVisibility(View.VISIBLE);
                    } else {
                        title_dropoff_5.setVisibility(View.GONE);
                        dropoffTimeTwoTextView.setVisibility(View.GONE);
                    }
                }
            });


            final FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    fab.setEnabled(false);
                    String name = deliveryName.getText().toString();
                    String pickupLocation = pickupLocationTextView.getText().toString();
                    String dropOffLocation = dropOffLocationTextView.getText().toString();
                    String notes = deliveryNotes.getText().toString();


                    Firebase ref = new Firebase("https://emissary.firebaseio.com");
                    Firebase postRef = ref.child("deliveries");
                    Map<String, String> post1 = new HashMap<String, String>();
                    post1.put("name", name);
                    post1.put("pickupLocation", pickupLocation);
                    post1.put("dropOffLocation", dropOffLocation);
                    post1.put("notes", notes);
                    postRef.push().setValue(post1);
                }
            });
            return rootView;
        }

        @Override
        public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
            Calendar now = Calendar.getInstance();

            String dateText = "";
            if (now.get(Calendar.YEAR) == year && now.get(Calendar.MONTH) == monthOfYear && now.get(Calendar.DAY_OF_MONTH) == dayOfMonth){
                dateText = "Today (" + dayOfMonth +"/"+(monthOfYear+1)+"/"+ year + ")";
            }else if(now.get(Calendar.YEAR) == year && now.get(Calendar.MONTH) == monthOfYear && now.get(Calendar.DAY_OF_MONTH) == (dayOfMonth-1)) {
                dateText = "Tomorrow (" + dayOfMonth+"/"+(monthOfYear+1)+"/"+ year + ")";
            }else{
                dateText = "" + dayOfMonth +"/"+(monthOfYear+1)+"/"+ year;
            }

            if (dateDialog == 1) {
                pickupDateTextView.setText(dateText);
            }else{
                dropoffDateTextView.setText(dateText);
            }
        }

        @Override
        public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {
            Calendar cal = Calendar.getInstance();
            cal.set(cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),cal.get(Calendar.DAY_OF_MONTH)
                    ,hourOfDay,minute,second);
            SimpleDateFormat fmt = new SimpleDateFormat("h:mm");
            String dateString = fmt.format(cal.getTime());

            dateString = (hourOfDay >= 12) ? (dateString + "pm") : (dateString + "am");
            if (timeDialog == 1) {
                pickupTimeOneTextView.setText(dateString);
            }else if (timeDialog == 2){
                pickupTimeTwoTextView.setText(dateString);
            }else if (timeDialog == 3){
                dropoffTimeOneTextView.setText(dateString);
            }else if (timeDialog == 4){
                dropoffTimeTwoTextView.setText(dateString);
            }

        }
    }

}
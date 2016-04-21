package nz.emissary.emissaryapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
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

import nz.emissary.emissaryapp.Delivery;
import nz.emissary.emissaryapp.R;
import nz.emissary.emissaryapp.User;

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
    public static class CreateDeliveryFragment extends Fragment{

        DatePickerDialog pickupDPD;
        DatePickerDialog dropoffDPD;
        TimePickerDialog pickupTPD;
        TimePickerDialog dropoffTPD;

        TextView pickupDateTextView;
        TextView pickupTimeTextView;
        TextView pickupLocationTextView;

        TextView dropoffDateTextView;
        TextView dropoffTimeTextView;
        TextView dropOffLocationTextView;

        int pickupYear, pickupMonth, pickupDay, pickupHourOfDay, pickupMinute, pickupSecond;
        int dropoffYear, dropoffMonth, dropoffDay, dropoffHourOfDay, dropoffMinute, dropoffSecond;

        User currentUser;
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

            Calendar now = Calendar.getInstance();
            pickupDPD = DatePickerDialog.newInstance(
                    new DatePickerDialog.OnDateSetListener() {
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

                            pickupDateTextView.setText(dateText);
                            pickupYear = year;
                            pickupMonth = monthOfYear;
                            pickupDay = dayOfMonth;
                        }
                    },
                    now.get(Calendar.YEAR),
                    now.get(Calendar.MONTH),
                    now.get(Calendar.DAY_OF_MONTH)
            );

            dropoffDPD = DatePickerDialog.newInstance(
                    new DatePickerDialog.OnDateSetListener() {
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

                            dropoffDateTextView.setText(dateText);
                            dropoffYear = year;
                            dropoffMonth = monthOfYear;
                            dropoffDay = dayOfMonth;
                        }
                    },
                    now.get(Calendar.YEAR),
                    now.get(Calendar.MONTH),
                    now.get(Calendar.DAY_OF_MONTH)
            );

            pickupTPD = TimePickerDialog.newInstance(
                    new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {
                            Calendar cal = Calendar.getInstance();
                            cal.set(cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),cal.get(Calendar.DAY_OF_MONTH)
                                    ,hourOfDay,minute,second);
                            SimpleDateFormat fmt = new SimpleDateFormat("h:mm");
                            String dateString = fmt.format(cal.getTime());

                            dateString = (hourOfDay >= 12) ? (dateString + "pm") : (dateString + "am");

                            pickupTimeTextView.setText(dateString);
                            pickupHourOfDay = hourOfDay;
                            pickupMinute = minute;
                            pickupSecond = second;
                        }
                    },
                    now.get(Calendar.HOUR),
                    0,
                    DateFormat.is24HourFormat(this.getActivity())
            );

            pickupTPD.setTitle("Pickup Time");

            dropoffTPD = TimePickerDialog.newInstance(
                    new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {
                            Calendar cal = Calendar.getInstance();
                            cal.set(cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),cal.get(Calendar.DAY_OF_MONTH)
                                    ,hourOfDay,minute,second);
                            SimpleDateFormat fmt = new SimpleDateFormat("h:mm");

                            String dateString = fmt.format(cal.getTime());

                            dateString = (hourOfDay >= 12) ? (dateString + "pm") : (dateString + "am");
                            dropoffTimeTextView.setText(dateString);
                            dropoffHourOfDay = hourOfDay;
                            dropoffMinute = minute;
                            dropoffSecond = second;

                        }
                    },
                    now.get(Calendar.HOUR),
                    0,
                    DateFormat.is24HourFormat(this.getActivity())
            );

            final EditText deliveryName = (EditText)rootView.findViewById(R.id.create_delivery_name);
            final EditText deliveryNotes = (EditText)rootView.findViewById(R.id.create_delivery_notes);

            pickupLocationTextView = (TextView)rootView.findViewById(R.id.create_delivery_pickup_location);
            dropOffLocationTextView = (TextView)rootView.findViewById(R.id.create_delivery_dropoff_location);

            pickupDateTextView = (TextView) rootView.findViewById(R.id.create_delivery_pickup_date);
            dropoffDateTextView = (TextView) rootView.findViewById(R.id.create_delivery_dropoff_date);

            pickupDateTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pickupDPD.show(getActivity().getFragmentManager(), "Datepickerdialog");
                }
            });

            dropoffDateTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dropoffDPD.show(getActivity().getFragmentManager(), "Datepickerdialog");
                }
            });

            int year = now.get(Calendar.YEAR);
            int monthOfYear = now.get(Calendar.MONTH);
            int dayOfMonth = now.get(Calendar.DAY_OF_MONTH);

            String dateText = "Today (" + dayOfMonth +"/"+(monthOfYear+1)+"/"+ year + ")";

            pickupDateTextView.setText(dateText);
            dropoffDateTextView.setText(dateText);

            pickupTimeTextView = (TextView) rootView.findViewById(R.id.create_delivery_pickup_time);
            pickupTimeTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pickupTPD.show(getActivity().getFragmentManager(), "Timepickerdialog");
                }
            });

            dropoffTimeTextView = (TextView) rootView.findViewById(R.id.create_delivery_dropoff_time);
            dropoffTimeTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dropoffTPD.show(getActivity().getFragmentManager(), "Timepickerdialog");
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

            final Firebase ref = new Firebase("https://emissary.firebaseio.com");

            final Firebase currentFirebaseUser = ref.child("users").child(ref.getAuth().getUid());
            currentFirebaseUser.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    currentUser = dataSnapshot.getValue(User.class);
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

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

                    Calendar cal1 = Calendar.getInstance();
                    cal1.set(pickupYear, pickupMonth, pickupDay, pickupHourOfDay, pickupMinute, pickupSecond);

                    Calendar cal2 = Calendar.getInstance();
                    cal2.set(dropoffYear, dropoffMonth, dropoffDay, dropoffHourOfDay, dropoffMinute, dropoffSecond);

                    String pickupTime = "" + cal1.getTimeInMillis();
                    String dropoffTime = "" + cal2.getTimeInMillis();

                    Delivery myDelivery = new Delivery();
                    myDelivery.setListingName(name);
                    myDelivery.setPickupLocation(pickupLocation);
                    myDelivery.setDropoffLocation(dropOffLocation);
                    myDelivery.setNotes(notes);
                    myDelivery.setOriginalLister(ref.getAuth().getUid());
                    myDelivery.setPickupTime(pickupTime);
                    myDelivery.setDropoffTime(dropoffTime);
                    myDelivery.setCreatedAt(System.currentTimeMillis() / 1000.0);

                    Firebase postRef = ref.child("deliveries");
                    Firebase newPostRef = postRef.push();
                    newPostRef.setValue(myDelivery);

                    final String deliveryId = newPostRef.getKey();
                    currentUser.addNewListing(deliveryId);
                    currentFirebaseUser.setValue(currentUser, new Firebase.CompletionListener(){
                        @Override
                        public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                            Intent result = new Intent(getActivity(), HomeActivity.class);
                            getActivity().setResult(RESULT_OK, result);
                            getActivity().finish();
                        }
                    });
                }
            });

            return rootView;
        }
    }

}
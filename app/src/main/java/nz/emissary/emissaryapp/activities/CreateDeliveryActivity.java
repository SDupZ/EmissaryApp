package nz.emissary.emissaryapp.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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

import nz.emissary.emissaryapp.Constants;
import nz.emissary.emissaryapp.Delivery;
import nz.emissary.emissaryapp.R;
import nz.emissary.emissaryapp.User;

/**
 * Created by Simon on 3/03/2016.
 */
public class CreateDeliveryActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient mGoogleApiClient;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_delivery);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        progressBar = (ProgressBar) findViewById(R.id.updateProgressBar);


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

        com.borax12.materialdaterangepicker.time.TimePickerDialog pickupTPTRange;
        com.borax12.materialdaterangepicker.time.TimePickerDialog dropoffTPTRange;

        TextView pickupDateTextView;
        TextView pickupTimeTextView;
        TextView pickupLocationTextView;

        TextView dropoffDateTextView;
        TextView dropoffTimeTextView;
        TextView dropOffLocationTextView;

        LinearLayout pickupDateContainerView;
        LinearLayout pickupTimeContainerView;
        LinearLayout pickupLocationContainerView;

        LinearLayout dropoffDateContainerView;
        LinearLayout dropoffTimeContainerView;
        LinearLayout dropoffLocationContainerView;

        TextView pickupSelectTimeRangeView;
        TextView dropoffSelectTimeRangeView;

        int pickupYear, pickupMonth, pickupDay, pickupHourOfDay, pickupMinute, pickupSecond;
        int pickupHourOfDayEnd, pickupMinuteEnd, pickupSecondEnd;

        int dropoffYear, dropoffMonth, dropoffDay, dropoffHourOfDay, dropoffMinute, dropoffSecond;
        int dropoffHourOfDayEnd, dropoffMinuteEnd, dropoffSecondEnd;

        String pickupTimeType = "";
        String dropoffTimeType = "";

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
            final View rootView = inflater.inflate(R.layout.fragment_create_delivery, container, false);

            Calendar now = Calendar.getInstance();

            final EditText deliveryName = (EditText)rootView.findViewById(R.id.create_delivery_name);
            final EditText deliveryNotes = (EditText)rootView.findViewById(R.id.create_delivery_notes);

            final TextView pickupASAPTextView = (TextView) rootView.findViewById(R.id.pickup_asap_text_view);
            final TextView dropoffASAPTextView = (TextView) rootView.findViewById(R.id.dropoff_asap_text_view);

            final SwitchCompat pickupASAPToggle = (SwitchCompat) rootView.findViewById(R.id.pickup_asap_toggle);
            final SwitchCompat dropoffASAPToggle = (SwitchCompat) rootView.findViewById(R.id.dropoff_asap_toggle);

            final ImageView pickupDateImageView = (ImageView) rootView.findViewById(R.id.create_delivery_pickup_time_image);
            final ImageView pickupTimeImageView = (ImageView) rootView.findViewById(R.id.create_delivery_pickup_date_image);
            final ImageView dropoffTimeImageView = (ImageView) rootView.findViewById(R.id.create_delivery_dropoff_time_image);
            final ImageView dropoffDateImageView = (ImageView) rootView.findViewById(R.id.create_delivery_dropoff_date_image);

            final TextView pickupCardView = (TextView) rootView.findViewById(R.id.pickup_details_card_title);
            final TextView dropoffCardView = (TextView) rootView.findViewById(R.id.dropoff_details_card_title);

            pickupLocationTextView = (TextView)rootView.findViewById(R.id.create_delivery_pickup_location);
            dropOffLocationTextView = (TextView)rootView.findViewById(R.id.create_delivery_dropoff_location);

            pickupDateTextView = (TextView) rootView.findViewById(R.id.create_delivery_pickup_date);
            dropoffDateTextView = (TextView) rootView.findViewById(R.id.create_delivery_dropoff_date);

            pickupDateContainerView         = (LinearLayout)rootView.findViewById(R.id.create_delivery_pickup_date_container);
            pickupTimeContainerView         = (LinearLayout)rootView.findViewById(R.id.create_delivery_pickup_time_container);
            pickupLocationContainerView     = (LinearLayout)rootView.findViewById(R.id.create_delivery_pickup_location_container);

            dropoffDateContainerView        = (LinearLayout)rootView.findViewById(R.id.create_delivery_dropoff_date_container);
            dropoffTimeContainerView        = (LinearLayout)rootView.findViewById(R.id.create_delivery_dropoff_time_container);
            dropoffLocationContainerView    = (LinearLayout)rootView.findViewById(R.id.create_delivery_dropoff_location_container);

            pickupSelectTimeRangeView       = (TextView)rootView.findViewById(R.id.create_delivery_select_time_range_pickup);
            dropoffSelectTimeRangeView      = (TextView)rootView.findViewById(R.id.create_delivery_select_time_range_dropoff);

            pickupASAPToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        pickupTimeContainerView.setEnabled(false);
                        pickupDateContainerView.setEnabled(false);
                        pickupSelectTimeRangeView.setEnabled(false);
                        pickupDateTextView.setTextColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.colorGreyedOut));
                        pickupTimeTextView.setTextColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.colorGreyedOut));
                        pickupDateImageView.setColorFilter(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.colorGreyedOut));
                        pickupTimeImageView.setColorFilter(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.colorGreyedOut));
                        pickupSelectTimeRangeView.setTextColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.colorGreyedOut));
                        pickupASAPTextView.setTypeface(null, Typeface.BOLD);
                        pickupTimeType = Constants.TIME_ASAP;
                    } else {
                        pickupTimeContainerView.setEnabled(true);
                        pickupDateContainerView.setEnabled(true);
                        pickupSelectTimeRangeView.setEnabled(true);
                        pickupDateTextView.setTextColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.colorNormalTextView));
                        pickupTimeTextView.setTextColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.colorNormalTextView));
                        pickupDateImageView.setColorFilter(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.colorNormalTextView));
                        pickupTimeImageView.setColorFilter(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.colorNormalTextView));
                        pickupSelectTimeRangeView.setTextColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.colorAccent));
                        pickupASAPTextView.setTypeface(null, Typeface.NORMAL);
                        pickupTimeType = "";
                    }
                }
            });

            dropoffASAPToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        dropoffTimeContainerView.setEnabled(false);
                        dropoffDateContainerView.setEnabled(false);
                        dropoffSelectTimeRangeView.setEnabled(false);
                        dropoffDateTextView.setTextColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.colorGreyedOut));
                        dropoffTimeTextView.setTextColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.colorGreyedOut));
                        dropoffDateImageView.setColorFilter(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.colorGreyedOut));
                        dropoffTimeImageView.setColorFilter(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.colorGreyedOut));
                        dropoffSelectTimeRangeView.setTextColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.colorGreyedOut));
                        dropoffASAPTextView.setTypeface(null, Typeface.BOLD);
                        dropoffTimeType = Constants.TIME_ASAP;
                    } else {
                        dropoffTimeContainerView.setEnabled(true);
                        dropoffDateContainerView.setEnabled(true);
                        dropoffSelectTimeRangeView.setEnabled(true);
                        dropoffDateTextView.setTextColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.colorNormalTextView));
                        dropoffTimeTextView.setTextColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.colorNormalTextView));
                        dropoffDateImageView.setColorFilter(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.colorNormalTextView));
                        dropoffTimeImageView.setColorFilter(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.colorNormalTextView));
                        dropoffSelectTimeRangeView.setTextColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.colorAccent));
                        dropoffASAPTextView.setTypeface(null, Typeface.NORMAL);
                        dropoffTimeType = "";
                    }
                }
            });

            pickupSelectTimeRangeView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pickupTPTRange.show(getActivity().getFragmentManager(), "Timepickerdialog");
                }
            });

            dropoffSelectTimeRangeView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    dropoffTPTRange.show(getActivity().getFragmentManager(), "Timepickerdialog");
                }
            });


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
                            pickupTimeType = Constants.TIME_SPECIFIC;
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
                            dropoffTimeType = Constants.TIME_SPECIFIC;
                        }
                    },
                    now.get(Calendar.HOUR),
                    0,
                    DateFormat.is24HourFormat(this.getActivity())
            );

            pickupTPTRange = com.borax12.materialdaterangepicker.time.TimePickerDialog.newInstance(new com.borax12.materialdaterangepicker.time.TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(com.borax12.materialdaterangepicker.time.RadialPickerLayout view, int hourOfDay, int minute, int hourOfDayEnd, int minuteEnd) {
                    Calendar cal = Calendar.getInstance();
                    cal.set(cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),cal.get(Calendar.DAY_OF_MONTH)
                            ,hourOfDay,minute,0);
                    SimpleDateFormat fmt = new SimpleDateFormat("h:mm");

                    Calendar cal2 = Calendar.getInstance();
                    cal2.set(cal2.get(Calendar.YEAR),cal2.get(Calendar.MONTH),cal2.get(Calendar.DAY_OF_MONTH)
                            ,hourOfDayEnd,minuteEnd,0);
                    SimpleDateFormat fmt2 = new SimpleDateFormat("h:mm");

                    String timeOne = fmt.format(cal.getTime());
                    String timeTwo = fmt2.format(cal2.getTime());

                    timeOne = (hourOfDay >= 12) ? (timeOne + "pm") : (timeOne + "am");
                    timeTwo = (hourOfDay >= 12) ? (timeTwo + "pm") : (timeTwo + "am");

                    String time = "Between " + timeOne + " & "+timeTwo;
                    pickupTimeTextView.setText(time);

                    pickupHourOfDay = hourOfDay;
                    pickupMinute = minute;
                    pickupSecond = 0;
                    pickupHourOfDayEnd = hourOfDayEnd;
                    pickupMinuteEnd = minuteEnd;
                    pickupSecondEnd = 0;
                    pickupTimeType = Constants.TIME_RANGE;
                }
            },now.get(Calendar.HOUR_OF_DAY),now.get(Calendar.MINUTE),false);

            dropoffTPTRange = com.borax12.materialdaterangepicker.time.TimePickerDialog.newInstance(new com.borax12.materialdaterangepicker.time.TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(com.borax12.materialdaterangepicker.time.RadialPickerLayout view, int hourOfDay, int minute, int hourOfDayEnd, int minuteEnd) {
                    Calendar cal = Calendar.getInstance();
                    cal.set(cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),cal.get(Calendar.DAY_OF_MONTH)
                            ,hourOfDay,minute,0);
                    SimpleDateFormat fmt = new SimpleDateFormat("h:mm");

                    Calendar cal2 = Calendar.getInstance();
                    cal2.set(cal2.get(Calendar.YEAR),cal2.get(Calendar.MONTH),cal2.get(Calendar.DAY_OF_MONTH)
                            ,hourOfDayEnd,minuteEnd,0);
                    SimpleDateFormat fmt2 = new SimpleDateFormat("h:mm");

                    String timeOne = fmt.format(cal.getTime());
                    String timeTwo = fmt2.format(cal2.getTime());

                    timeOne = (hourOfDay >= 12) ? (timeOne + "pm") : (timeOne + "am");
                    timeTwo = (hourOfDay >= 12) ? (timeTwo + "pm") : (timeTwo + "am");

                    String time = "Between "+timeOne + " & "+timeTwo;

                    dropoffTimeTextView.setText(time);
                    dropoffHourOfDay = hourOfDay;
                    dropoffMinute = minute;
                    dropoffSecond = 0;
                    dropoffHourOfDayEnd = hourOfDayEnd;
                    dropoffMinuteEnd = minuteEnd;
                    dropoffSecondEnd = 0;
                    dropoffTimeType = Constants.TIME_RANGE;
                }
            },now.get(Calendar.HOUR_OF_DAY),now.get(Calendar.MINUTE),false);

            pickupDateContainerView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deliveryName.clearFocus();
                    deliveryNotes.clearFocus();
                    pickupDateContainerView.requestFocus();
                    pickupDPD.show(getActivity().getFragmentManager(), "Datepickerdialog");
                }
            });

            dropoffDateContainerView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deliveryName.clearFocus();
                    deliveryNotes.clearFocus();
                    dropoffDateContainerView.requestFocus();
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
            pickupTimeContainerView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deliveryName.clearFocus();
                    deliveryNotes.clearFocus();
                    pickupTimeContainerView.requestFocus();
                    pickupTPD.show(getActivity().getFragmentManager(), "Timepickerdialog");
                }
            });

            dropoffTimeTextView = (TextView) rootView.findViewById(R.id.create_delivery_dropoff_time);
            dropoffTimeContainerView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deliveryName.clearFocus();
                    deliveryNotes.clearFocus();
                    dropoffTimeContainerView.requestFocus();

                    dropoffTPD.show(getActivity().getFragmentManager(), "Timepickerdialog");
                }
            });

            pickupLocationContainerView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deliveryName.clearFocus();
                    deliveryNotes.clearFocus();
                    pickupLocationContainerView.requestFocus();
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

            dropoffLocationContainerView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deliveryName.clearFocus();
                    deliveryNotes.clearFocus();
                    dropoffLocationContainerView.requestFocus();
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
                    ((CreateDeliveryActivity)getActivity()).progressBar.setVisibility(View.VISIBLE);
                    String name = deliveryName.getText().toString();
                    String pickupLocation = pickupLocationTextView.getText().toString();
                    String dropOffLocation = dropOffLocationTextView.getText().toString();
                    String notes = deliveryNotes.getText().toString();

                    String pickupTime = "";
                    String dropoffTime = "";
                    String pickupTimeEnd = "";
                    String dropoffTimeEnd = "";

                    boolean error = false;
                    deliveryName.setError(null);
                    pickupLocationTextView.setError(null);
                    dropOffLocationTextView.setError(null);

                    if (name.trim().equals("")){
                        //Check for a valid name
                        error = true;
                        deliveryName.setError("Delivery name is required.");
                        deliveryName.requestFocus();
                    }else if(pickupLocation.trim().equals("Select pickup location")){
                        //Check for a pickup location
                        error = true;
                        pickupLocationTextView.setError("A pickup location is required");
                        pickupLocationTextView.requestFocus();
                    }else if(dropOffLocation.trim().equals("Select dropoff location")) {
                        //Check for a pickup location
                        error = true;
                        dropOffLocationTextView.setError("A dropoff location is required");
                        dropOffLocationTextView.requestFocus();
                    }

                    if (pickupTimeType == Constants.TIME_ASAP){
                        pickupTime = Constants.TIME_ASAP;

                    }else if(pickupTimeType == Constants.TIME_SPECIFIC){
                        Calendar cal = Calendar.getInstance();
                        cal.set(pickupYear, pickupMonth, pickupDay, pickupHourOfDay, pickupMinute, pickupSecond);

                        pickupTime = Constants.TIME_SPECIFIC + Constants.TIME_TOKEN + cal.getTimeInMillis();
                    }else if(pickupTimeType == Constants.TIME_RANGE){
                        Calendar cal = Calendar.getInstance();
                        cal.set(pickupYear, pickupMonth, pickupDay, pickupHourOfDay, pickupMinute, pickupSecond);
                        Calendar cal1 = Calendar.getInstance();
                        cal1.set(pickupYear, pickupMonth, pickupDay, pickupHourOfDayEnd, pickupMinuteEnd, 0);

                        pickupTime = Constants.TIME_RANGE + Constants.TIME_TOKEN + cal.getTimeInMillis() + Constants.TIME_TOKEN  + cal1.getTimeInMillis();
                    }else{
                        if (!error) {
                            error = true;
                            pickupCardView.requestFocus();
                            Toast t = Toast.makeText(getContext(), "A pickup location is required", Toast.LENGTH_SHORT);
                            t.show();
                        }
                    }

                    if (dropoffTimeType == Constants.TIME_ASAP){
                        dropoffTime = Constants.TIME_ASAP;

                    }else if(dropoffTimeType == Constants.TIME_SPECIFIC){
                        Calendar cal = Calendar.getInstance();
                        cal.set(dropoffYear, dropoffMonth, dropoffDay, dropoffHourOfDay, dropoffMinute, dropoffSecond);

                        dropoffTime = Constants.TIME_SPECIFIC + Constants.TIME_TOKEN + cal.getTimeInMillis();
                    }else if(dropoffTimeType == Constants.TIME_RANGE){
                        Calendar cal = Calendar.getInstance();
                        cal.set(dropoffYear, dropoffMonth, dropoffDay, dropoffHourOfDay, dropoffMinute, dropoffSecond);
                        Calendar cal1 = Calendar.getInstance();
                        cal1.set(dropoffYear, dropoffMonth, dropoffDay, dropoffHourOfDayEnd, dropoffMinuteEnd, 0);

                        dropoffTime = Constants.TIME_RANGE + Constants.TIME_TOKEN + cal.getTimeInMillis() + Constants.TIME_TOKEN  + cal1.getTimeInMillis();
                    }else{
                        //ERROR
                        if (!error) {
                            error = true;
                            dropoffCardView.requestFocus();
                            Toast t = Toast.makeText(getContext(), "A dropoff location is required", Toast.LENGTH_SHORT);
                            t.show();
                        }
                    }

                    if (!error) {

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
                        currentFirebaseUser.setValue(currentUser, new Firebase.CompletionListener() {
                            @Override
                            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                                Intent result = new Intent(getActivity(), ViewMyListingsActivity.class);
                                getActivity().setResult(RESULT_OK, result);
                                getActivity().finish();
                            }
                        });
                    }else{
                        ((CreateDeliveryActivity)getActivity()).progressBar.setVisibility(View.GONE);
                        fab.setEnabled(true);
                    }
                }
            });

            return rootView;
        }
    }

}
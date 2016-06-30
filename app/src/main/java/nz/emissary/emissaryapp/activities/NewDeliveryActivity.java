package nz.emissary.emissaryapp.activities;

import android.animation.ArgbEvaluator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.github.paolorotolo.appintro.AppIntro;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;

import org.w3c.dom.Text;

import nz.emissary.emissaryapp.Constants;
import nz.emissary.emissaryapp.R;

/**
 * Created by Simon on 2/06/2016.
 */
public class NewDeliveryActivity extends AppIntro {

    protected int vehicleType;

    private Place pickupPlace;
    private Place dropoffPlace;
    protected double estimatedDistance;

    @Override
    public void init(Bundle savedInstanceState) {
        this.estimatedDistance = -1;
        pickupPlace = null;
        dropoffPlace = null;
        addSlide(new PagerNewDeliveryInitial());
        addSlide(new PagerNewDeliveryVehicleType());
        addSlide(new PagerNewDeliveryLocation());
        addSlide(new PagerNewDeliveryPickupTime());
        addSlide(new PagerNewDeliveryDropoffTime());
        addSlide(new PagerNewDeliveryPayment());
        addSlide(new PagerNewDeliveryComplete());

        setBarColor(ContextCompat.getColor(getApplicationContext(), R.color.colorBarColor));
    }

    @Override
    public void onSkipPressed() {
    }

    @Override
    public void onDonePressed() {
        //Do validation checks here
    }

    @Override
    public void onSlideChanged() {
        // Do something when the slide changes.
    }

    @Override
    public void onNextPressed() {
        // Do something when users tap on Next button.
    }

    public static class PagerNewDeliveryInitial extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            final View rootView = inflater.inflate(R.layout.pager_new_delivery_initial, container, false);
            return rootView;
        }
    }

    protected void setPickupPlace(Place pickupPlace){
        this.pickupPlace = pickupPlace;
        if (this.dropoffPlace != null){
            this.estimatedDistance = Constants.DISTANCE_ESTIMATE_FACTOR * getRoughDistance(this.pickupPlace.getLatLng(), this.dropoffPlace.getLatLng());
        }
    }

    protected void setDropoffPlace(Place dropoffPlace){
        this.dropoffPlace = dropoffPlace;
        if (this.pickupPlace != null){
            this.estimatedDistance = Constants.DISTANCE_ESTIMATE_FACTOR *getRoughDistance(this.pickupPlace.getLatLng(), this.dropoffPlace.getLatLng());
        }
    }

    protected Place getPickupPlace(){
        return this.pickupPlace;
    }

    protected Place getDropoffPlace(){
        return this.dropoffPlace;
    }

    private double getRoughDistance(LatLng start, LatLng end){
        double radius = 6371;           // Radius of the earth in km
        double dLat = deg2rad(end.latitude - start.latitude);  // deg2rad below
        double dLon = deg2rad(end.longitude - start.longitude);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2)
                        + Math.cos(deg2rad(start.latitude))
                        * Math.cos(deg2rad(end.latitude))
                        * Math.sin(dLon/2) * Math.sin(dLon/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double d = radius * c; // Distance in km
        return d;
    }

    private double deg2rad(double deg) {
        return deg * (Math.PI/180);
    }

    //**********************************************************************************************
    //** Vehicle Type
    //**********************************************************************************************
    public static class PagerNewDeliveryVehicleType extends Fragment implements View.OnClickListener{
        private static final String DATA_SELECTED = "nz.co.emissary.setupdriveraccount.selected";

        ImageView carView;
        ImageView vanView;
        ImageView motorcycleView;

        int pictureSelected;


        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setRetainInstance(true);
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

            if(savedInstanceState != null) {
                pictureSelected = savedInstanceState.getInt(DATA_SELECTED);
            }
        }

        @Override
        public void onSaveInstanceState(Bundle outState) {
            super.onSaveInstanceState(outState);
            outState.putInt(DATA_SELECTED, pictureSelected);
        }

        @Override
        public void onStart() {
            super.onStart();

            if (pictureSelected == 1){
                setColorOfImage(R.id.vehicle_motorcycle);
            }else if (pictureSelected == 3){
                setColorOfImage(R.id.vehicle_van);
            }else{
                setColorOfImage(R.id.vehicle_car);
            }
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            final View rootView = inflater.inflate(R.layout.pager_new_delivery_vehicle_type, container, false);

            carView = (ImageView) rootView.findViewById(R.id.vehicle_car);
            vanView = (ImageView) rootView.findViewById(R.id.vehicle_van);
            motorcycleView = (ImageView) rootView.findViewById(R.id.vehicle_motorcycle);

            carView.setOnClickListener(this);
            vanView.setOnClickListener(this);
            motorcycleView.setOnClickListener(this);
            return rootView;
        }
        @Override
        public void onClick(View view) {
            setColorOfImage(view.getId());
        }

        public void setColorOfImage(int id) {
            if (id == R.id.vehicle_car) {
                pictureSelected = 2;
                ((NewDeliveryActivity)getActivity()).vehicleType = Constants.VEHICLE_CAR;
                carView.setColorFilter(null);
                vanView.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorGreyedOut));
                motorcycleView.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorGreyedOut));
            } else if (id == R.id.vehicle_motorcycle) {
                pictureSelected = 1;
                ((NewDeliveryActivity)getActivity()).vehicleType = Constants.VEHICLE_MOTORBIKE;
                carView.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorGreyedOut));
                vanView.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorGreyedOut));
                motorcycleView.setColorFilter(null);
            } else if (id == R.id.vehicle_van) {
                pictureSelected = 3;
                ((NewDeliveryActivity)getActivity()).vehicleType = Constants.VEHICLE_VAN;
                carView.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorGreyedOut));
                vanView.setColorFilter(null);
                motorcycleView.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorGreyedOut));
            }
        }
    }

    //**********************************************************************************************
    //** Location
    //**********************************************************************************************
    public static class PagerNewDeliveryLocation extends Fragment implements
            GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{
        private static final String DATA_PICKUP = "nz.co.emissary.setupdriveraccount.pickuplocation";
        private static final String DATA_DROPOFF= "nz.co.emissary.setupdriveraccount.dropofflocation";

        private GoogleApiClient mGoogleApiClient;
        private TextView pickupLocationTextView;
        private TextView dropOffLocationTextView;
        private LinearLayout pickupLocationContainerView;
        private LinearLayout dropoffLocationContainerView;

        String pickupText;
        String dropoffText;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setRetainInstance(true);
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

            if(savedInstanceState != null) {
                pickupText = savedInstanceState.getString(DATA_PICKUP);
                dropoffText = savedInstanceState.getString(DATA_DROPOFF);
            }
        }

        @Override
        public void onSaveInstanceState(Bundle outState) {
            super.onSaveInstanceState(outState);
            outState.putString(DATA_PICKUP, pickupText);
            outState.putString(DATA_DROPOFF, dropoffText);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            final View rootView = inflater.inflate(R.layout.pager_new_delivery_location, container, false);

            mGoogleApiClient = new GoogleApiClient
                    .Builder(getContext())
                    .addApi(Places.GEO_DATA_API)
                    .addApi(Places.PLACE_DETECTION_API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();

            pickupLocationTextView = (TextView)rootView.findViewById(R.id.create_delivery_pickup_location);
            dropOffLocationTextView = (TextView)rootView.findViewById(R.id.create_delivery_dropoff_location);
            pickupLocationContainerView     = (LinearLayout)rootView.findViewById(R.id.create_delivery_pickup_location_container);
            dropoffLocationContainerView    = (LinearLayout)rootView.findViewById(R.id.create_delivery_dropoff_location_container);

            pickupLocationContainerView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
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

            return rootView;
        }

        @Override
        public void onStart() {
            super.onStart();
            if (pickupText != null && !pickupText.equals("")){
                pickupLocationTextView.setText(pickupText);
            }
            if (dropoffText != null && !dropoffText.equals("")){
                dropOffLocationTextView.setText(dropoffText);
            }
            mGoogleApiClient.connect();
        }

        @Override
        public void onStop() {
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

        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            if (resultCode == RESULT_OK) {
                if (requestCode == 1 || requestCode == 2) {
                    Place place = PlacePicker.getPlace(getActivity(), data);
                    String placeMsg = place.getAddress().toString();

                    if (requestCode == 1){
                        ((NewDeliveryActivity)getActivity()).setPickupPlace(place);
                        pickupLocationTextView.setText(placeMsg);
                        pickupText = placeMsg;
                    }else if (requestCode == 2){
                        ((NewDeliveryActivity)getActivity()).setDropoffPlace(place);
                        dropOffLocationTextView.setText(placeMsg);
                        dropoffText = placeMsg;
                    }
                }
            }

        }
    }


    //**********************************************************************************************
    //** Pickup Time
    //**********************************************************************************************
    public static class PagerNewDeliveryPickupTime extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            final View rootView = inflater.inflate(R.layout.pager_new_delivery_pickup_time, container, false);
            return rootView;
        }
    }


    //**********************************************************************************************
    //** Dropoff Time
    //**********************************************************************************************
    public static class PagerNewDeliveryDropoffTime extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            final View rootView = inflater.inflate(R.layout.pager_new_delivery_dropoff_time, container, false);
            return rootView;
        }
    }

    //**********************************************************************************************
    //** Payment
    //**********************************************************************************************
    public static class PagerNewDeliveryPayment extends Fragment {

        SeekBar priceSlider;
        EditText priceView;
        TextView recommendedPriceView;

        private int maxPrice;
        private int currentPrice;
        private double recommendedPrice = 50;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            final View rootView = inflater.inflate(R.layout.pager_new_delivery_payment, container, false);

            this.priceSlider= (SeekBar) rootView.findViewById(R.id.price_slider);
            this.priceView = (EditText) rootView.findViewById(R.id.price_view);
            this.recommendedPriceView = (TextView) rootView.findViewById(R.id.recommended_price);

            this.currentPrice = 0;
            this.maxPrice = 300;

            this.priceSlider.setProgress(0);
            priceView.setText(String.valueOf(this.currentPrice));

            priceSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    currentPrice = (int)(maxPrice * Math.pow((progress / 100.0), 4));
                    priceView.setText("$" + String.valueOf(currentPrice));

                    float percentage = ((float)(progress / recommendedPrice));

                    if (percentage > 1){
                        percentage = 1;
                    }

                    int decimalColor = (Integer) new ArgbEvaluator().evaluate(percentage, 0xB71C1C, 0x689F38);
                    String hexColour = "#" + Integer.toHexString(decimalColor);
                    priceView.setTextColor(Color.parseColor(hexColour));
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });

            return rootView;
        }

        @Override
        public void onStart() {
            super.onStart();

            if (((NewDeliveryActivity)getActivity()).estimatedDistance != -1){
                double distance = ((NewDeliveryActivity)getActivity()).estimatedDistance;
                int recommendedPrice = (int)Constants.getRecommendedPrice(distance);
                if (recommendedPrice == 0){
                    recommendedPrice = 1;
                }
                this.recommendedPrice = recommendedPrice;

                recommendedPriceView.setText("~ $" + String.valueOf(recommendedPrice));
            }

        }
    }

    //**********************************************************************************************
    //** Complete
    //**********************************************************************************************
    public static class PagerNewDeliveryComplete extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            final View rootView = inflater.inflate(R.layout.pager_new_delivery_complete, container, false);
            return rootView;
        }
    }
}

package nz.emissary.emissaryapp.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.paolorotolo.appintro.AppIntro;
import com.redbooth.WelcomeCoordinatorLayout;

import nz.emissary.emissaryapp.R;

/**
 * Created by Simon on 3/03/2016.
 */
public class SetupDriverAccount extends AppIntro {
    private String userId;

    @Override
    public void init(Bundle savedInstanceState) {
        addSlide(new PagerDriverAccountInitial());
        addSlide(new PagerDriverAccountVerifyPhone());
        addSlide(new PagerDriverAccountAddVehicle());
        addSlide(new PagerDriverAccountPayment());
        addSlide(new PagerDriverAccountComplete());

        setBarColor(ContextCompat.getColor(getApplicationContext(), R.color.colorBarColor));
    }

    @Override
    public void onSkipPressed() {
        // Do something when users tap on Skip button.
        Intent result = new Intent(SetupDriverAccount.this, ViewMyListingsActivity.class);
        startActivity(result);
        finish();
    }

    @Override
    public void onDonePressed() {
        // Do something when users tap on Done button.
        Intent result = new Intent(SetupDriverAccount.this, ViewPublicListingsActivity.class);
        startActivity(result);
        finish();
    }

    @Override
    public void onSlideChanged() {
        // Do something when the slide changes.


    }

    @Override
    public void onNextPressed() {
        // Do something when users tap on Next button.
    }

    public static class PagerDriverAccountInitial extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            final View rootView = inflater.inflate(R.layout.pager_driver_account_initial, container, false);

            Intent intent = getActivity().getIntent();
            if(intent != null && intent.hasExtra("user_id")
                    && intent.hasExtra("user_name")
                    && intent.hasExtra("user_phone")) {
                String firstName = intent.getStringExtra("user_name");
                ((TextView) rootView.findViewById(R.id.welcome_text)).setText("Hi " + firstName + ", ");
            }

            return rootView;
        }
    }

    public static class PagerDriverAccountVerifyPhone extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            final View rootView = inflater.inflate(R.layout.pager_driver_account_verify_phone, container, false);

            Intent intent = getActivity().getIntent();
            if(intent != null && intent.hasExtra("user_id")
                    && intent.hasExtra("user_name")
                    && intent.hasExtra("user_phone")) {
                String phoneNumber = intent.getStringExtra("user_phone");
                ((TextView) rootView.findViewById(R.id.phone_number_view)).setText(phoneNumber);
            }

            return rootView;
        }
    }

    public static class PagerDriverAccountAddVehicle extends Fragment implements View.OnClickListener{
        private static final String DATA_LICENSE = "nz.co.emissary.setupdriveraccount.license";
        private static final String DATA_SELECTED = "nz.co.emissary.setupdriveraccount.selected";

        ImageView carView;
        ImageView vanView;
        ImageView motorcycleView;

        EditText licenseView;

        int pictureSelected;
        String licenseNumber;


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
                licenseNumber = savedInstanceState.getString(DATA_LICENSE);
            }
        }

        @Override
        public void onSaveInstanceState(Bundle outState) {
            super.onSaveInstanceState(outState);
            outState.putInt(DATA_SELECTED, pictureSelected);
            outState.putString(DATA_LICENSE, licenseView.getText().toString());

            Log.d("EMISSARY", "" + pictureSelected + ":" + licenseView.getText().toString());
        }

        @Override
        public void onStart() {
            super.onStart();
            licenseView.setText(licenseNumber);

            if (pictureSelected == 1){
                setColorOfImage(R.id.vehicle_motorcycle);
            }else if (pictureSelected == 2){
                setColorOfImage(R.id.vehicle_car);
            }else{
                setColorOfImage(R.id.vehicle_van);
            }
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            final View rootView = inflater.inflate(R.layout.pager_driver_account_add_vehicle, container, false);

            licenseView = (EditText) rootView.findViewById(R.id.vehicle_license);
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
                carView.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorAccent));
                vanView.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorGreyedOut));
                motorcycleView.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorGreyedOut));
            } else if (id == R.id.vehicle_motorcycle) {
                pictureSelected = 1;
                carView.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorGreyedOut));
                vanView.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorGreyedOut));
                motorcycleView.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorAccent));
            } else if (id == R.id.vehicle_van) {
                pictureSelected = 3;
                carView.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorGreyedOut));
                vanView.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorAccent));
                motorcycleView.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorGreyedOut));
            }
        }
    }

    public static class PagerDriverAccountPayment extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            final View rootView = inflater.inflate(R.layout.pager_driver_account_payment, container, false);

            return rootView;
        }
    }

    public static class PagerDriverAccountComplete extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            final View rootView = inflater.inflate(R.layout.pager_driver_account_complete, container, false);

            return rootView;
        }
    }
}
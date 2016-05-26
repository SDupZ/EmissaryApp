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
                ((TextView) rootView.findViewById(R.id.welcome_text)).setText("Welcome " + firstName);
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
        ImageView carView;
        ImageView vanView;
        ImageView motorcycleView;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            final View rootView = inflater.inflate(R.layout.pager_driver_account_add_vehicle, container, false);

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
            if (view.getId() == R.id.vehicle_car){
                carView.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorAccent));
                vanView.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorGreyedOut));
                motorcycleView.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorGreyedOut));
            }else if (view.getId() == R.id.vehicle_motorcycle){
                carView.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorGreyedOut));
                vanView.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorGreyedOut));
                motorcycleView.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorAccent));
            }else if (view.getId() == R.id.vehicle_van){
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
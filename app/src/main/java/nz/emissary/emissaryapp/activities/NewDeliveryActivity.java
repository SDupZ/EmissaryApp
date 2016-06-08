package nz.emissary.emissaryapp.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.paolorotolo.appintro.AppIntro;

import nz.emissary.emissaryapp.R;

/**
 * Created by Simon on 2/06/2016.
 */
public class NewDeliveryActivity extends AppIntro {

    @Override
    public void init(Bundle savedInstanceState) {
        addSlide(new PagerNewDeliveryInitial());
        addSlide(new PagerNewDeliveryVehicleType());
        addSlide(new PagerNewDeliveryLocation());
        addSlide(new PagerNewDeliveryPickupLocation());
        addSlide(new PagerNewDeliveryDropoffLocation());
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

    public static class PagerNewDeliveryLocation extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            final View rootView = inflater.inflate(R.layout.pager_new_delivery_location, container, false);
            return rootView;
        }
    }

    public static class PagerNewDeliveryVehicleType extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            final View rootView = inflater.inflate(R.layout.pager_new_delivery_vehicle_type, container, false);
            return rootView;
        }
    }

    public static class PagerNewDeliveryPickupLocation extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            final View rootView = inflater.inflate(R.layout.pager_new_delivery_pickup_time, container, false);
            return rootView;
        }
    }

    public static class PagerNewDeliveryDropoffLocation extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            final View rootView = inflater.inflate(R.layout.pager_new_delivery_dropoff_time, container, false);
            return rootView;
        }
    }

    public static class PagerNewDeliveryPayment extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            final View rootView = inflater.inflate(R.layout.pager_new_delivery_payment, container, false);
            return rootView;
        }
    }

    public static class PagerNewDeliveryComplete extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            final View rootView = inflater.inflate(R.layout.pager_new_delivery_complete, container, false);
            return rootView;
        }
    }
}

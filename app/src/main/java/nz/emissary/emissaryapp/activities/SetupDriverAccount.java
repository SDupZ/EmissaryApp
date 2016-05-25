package nz.emissary.emissaryapp.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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

        setBarColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
    }

    @Override
    public void onSkipPressed() {
        // Do something when users tap on Skip button.
    }

    @Override
    public void onDonePressed() {
        // Do something when users tap on Done button.
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

            return rootView;
        }
    }

    public static class PagerDriverAccountVerifyPhone extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            final View rootView = inflater.inflate(R.layout.pager_driver_account_verify_phone, container, false);

            return rootView;
        }
    }

    public static class PagerDriverAccountAddVehicle extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            final View rootView = inflater.inflate(R.layout.pager_driver_account_add_vehicle, container, false);

            return rootView;
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
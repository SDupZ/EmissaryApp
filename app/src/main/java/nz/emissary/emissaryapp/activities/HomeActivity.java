package nz.emissary.emissaryapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.ui.FirebaseRecyclerAdapter;

import nz.emissary.emissaryapp.Delivery;
import nz.emissary.emissaryapp.R;


/**
 * Created by Simon on 1/03/2016.
 */

public class HomeActivity extends BaseActivity{

    static final int REQUEST_AUTH_TOKEN = 0;
    Firebase ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, CreateDeliveryActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        ref = new Firebase("https://emissary.firebaseio.com");
        ref.addAuthStateListener(new Firebase.AuthStateListener() {
            @Override
            public void onAuthStateChanged(AuthData authData) {
                if (authData == null) {
                    Intent intentWithToken = new Intent(HomeActivity.this, LoginActivity.class);
                    startActivityForResult(intentWithToken, REQUEST_AUTH_TOKEN);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == REQUEST_AUTH_TOKEN) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                // The user picked a contact.
                // The Intent's data Uri identifies which contact was selected.
                // Do something with the contact here (bigger example below)
                Log.d("EMISSARY", "Logged IN");
            }
        }
    }

    protected int getLayoutResource(){
        return R.layout.activity_home;
    }

    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    //DeliveryListFragment fragment
    public static class DeliveryListFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        private RecyclerView mRecyclerView;
        private RecyclerView.LayoutManager mLayoutManager;

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static DeliveryListFragment newInstance(int sectionNumber) {
            DeliveryListFragment fragment = new DeliveryListFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public DeliveryListFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.delivery_list_fragment, container, false);

            mRecyclerView = (RecyclerView) rootView.findViewById(R.id.my_recycler_view);
            mRecyclerView.setHasFixedSize(true);

            // use a linear layout manager
            mLayoutManager = new LinearLayoutManager(getActivity());
            mRecyclerView.setLayoutManager(mLayoutManager);

            final Firebase mRef = new Firebase("https://emissary.firebaseio.com/deliveries");

            FirebaseRecyclerAdapter<Delivery, ViewHolder> adapter =
                    new FirebaseRecyclerAdapter<Delivery, ViewHolder>(Delivery.class,R.layout.delivery_list_view,ViewHolder.class,mRef){

                        @Override
                        protected void populateViewHolder(ViewHolder viewHolder, Delivery d, final int i) {
                            viewHolder.mDeliveryName.setText(d.getListingName());
                            viewHolder.mDeliveryPickupTime.setText(d.getNotes());

                            viewHolder.mView.setOnTouchListener(new View.OnTouchListener() {
                                @Override
                                public boolean onTouch(View v, MotionEvent event) {
                                    if (event.getAction() == MotionEvent.ACTION_CANCEL) {
                                        v.setSelected(false);
                                        return true;
                                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                                        Log.d("Emissary", "Position" + i);
                                        v.setSelected(false);
                                        return true;
                                    } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
                                        v.setSelected(true);
                                        return true;
                                    }
                                    return false;
                                }
                            });
                        }

                    };


            //This adapter is used to show a progress bar
            adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                @Override
                public void onChanged() {
                    super.onChanged();

                }
            });
            mRecyclerView.setAdapter(adapter);

            return rootView;
        }

        public static class ViewHolder extends RecyclerView.ViewHolder{
            View mView;

            public TextView mDeliveryName;
            public TextView mDeliveryPickupTime;

            public ViewHolder(View v) {
                super(v);
                mView = v;
                mDeliveryName = (TextView) v.findViewById(R.id.list_item_delivery_name);
                mDeliveryPickupTime = (TextView) v.findViewById(R.id.list_item_pickup_time);

                v.setClickable(true);
            }
        }

    }
}

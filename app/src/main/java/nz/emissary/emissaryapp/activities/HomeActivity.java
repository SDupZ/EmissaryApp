package nz.emissary.emissaryapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.client.AuthData;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.ui.FirebaseRecyclerAdapter;

import nz.emissary.emissaryapp.Constants;
import nz.emissary.emissaryapp.Delivery;
import nz.emissary.emissaryapp.R;


/**
 * Created by Simon on 1/03/2016.
 */

public class HomeActivity extends BaseActivity{

    static final int REQUEST_AUTH_TOKEN = 0;
    static final int CREATE_DELIVERY = 1;
    Firebase ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, CreateDeliveryActivity.class);
                startActivityForResult(intent, CREATE_DELIVERY);
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
                    startActivity(intentWithToken);
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
        }else if (requestCode == CREATE_DELIVERY){
            if (resultCode == RESULT_OK) {
                Snackbar snackbar = Snackbar.make(findViewById(R.id.home_coordinator_layout), "New delivery created!", Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        }
    }

    protected int getLayoutResource(){
        return R.layout.activity_home;
    }

    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    //MyListingsFragment fragment
    public static class DeliveryListFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */

        private RecyclerView mRecyclerView;
        private RecyclerView.LayoutManager mLayoutManager;
        private ProgressBar progressBar;

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
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
            View rootView = inflater.inflate(R.layout.delivery_list_fragment, container, false);

            progressBar = (ProgressBar) rootView.findViewById(R.id.updateProgressBar);
            mRecyclerView = (RecyclerView) rootView.findViewById(R.id.my_recycler_view);
            mRecyclerView.setHasFixedSize(true);

            // use a linear layout manager
            mLayoutManager = new LinearLayoutManager(getActivity());
            mRecyclerView.setLayoutManager(mLayoutManager);

            final Firebase mRef = new Firebase("https://emissary.firebaseio.com/deliveries");
            Query queryRef = mRef.orderByChild("status").equalTo(Constants.STATUS_LISTED);

            final FirebaseRecyclerAdapter<Delivery, ViewHolder> adapter =
                    new FirebaseRecyclerAdapter<Delivery, ViewHolder>(Delivery.class,R.layout.delivery_list_view,ViewHolder.class,queryRef){
                        @Override
                        protected void populateViewHolder(ViewHolder viewHolder, Delivery d, final int i) {
                            viewHolder.mDeliveryName.setText(d.getListingName());
                            viewHolder.mDeliveryNotes.setText(d.getNotes());
                            viewHolder.mDeliveryPickupTime.setText(Constants.convertTime(d.getPickupTime()));
                            viewHolder.mDeliveryDropoffTime.setText(Constants.convertTime(d.getDropoffTime()));

                            viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(v.getContext(), ViewItemActivity.class)
                                            .putExtra("object_id", getRef(i).getKey());
                                    v.getContext().startActivity(intent);
                                }
                            });
                        }

                    };

            adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {

                @Override
                public void onItemRangeInserted(int positionStart, int itemCount) {
                    super.onItemRangeInserted(positionStart, itemCount);
                    progressBar.setVisibility(View.GONE);
                    adapter.unregisterAdapterDataObserver(this);
                }
            });

            mRecyclerView.setAdapter(adapter);

            return rootView;
        }

        public static class ViewHolder extends RecyclerView.ViewHolder{
            View mView;

            public TextView mDeliveryName;
            public TextView mDeliveryNotes;
            public TextView mDeliveryPickupTime;
            public TextView mDeliveryDropoffTime;


            public ViewHolder(View v) {
                super(v);
                mView = v;
                mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
                mDeliveryName = (TextView) v.findViewById(R.id.list_item_delivery_name);
                mDeliveryNotes = (TextView) v.findViewById(R.id.list_item_notes);
                mDeliveryPickupTime = (TextView) v.findViewById(R.id.list_item_pickup_time);
                mDeliveryDropoffTime = (TextView) v.findViewById(R.id.list_item_dropoff_time);
                v.setClickable(true);
            }
        }

    }
}

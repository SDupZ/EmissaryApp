package nz.emissary.emissaryapp.activities;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.firebase.client.Query;
import com.firebase.ui.FirebaseRecyclerAdapter;

import nz.emissary.emissaryapp.Constants;
import nz.emissary.emissaryapp.Delivery;
import nz.emissary.emissaryapp.R;

/**
 * Created by Simon on 8/03/2016.
 */
public class ViewMyListingsActivity extends BaseActivity{

    static final int CREATE_DELIVERY = 1;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        progressBar = (ProgressBar) findViewById(R.id.updateProgressBar);
        progressBar.setVisibility(View.VISIBLE);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ViewMyListingsActivity.this, CreateDeliveryActivity.class);
                startActivityForResult(intent, CREATE_DELIVERY);
            }
        });
    }

    protected int getLayoutResource(){
        return R.layout.activity_view_my_listings;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == CREATE_DELIVERY){
            if (resultCode == RESULT_OK) {
                Snackbar snackbar = Snackbar.make(findViewById(R.id.view_my_listings_coordinator_layout), "New delivery created!", Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        }
    }

    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    //DeliveryListFragment fragment
    public static class MyListingsFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private RecyclerView mRecyclerView;
        private RecyclerView.LayoutManager mLayoutManager;

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static MyListingsFragment newInstance(int sectionNumber) {
            MyListingsFragment fragment = new MyListingsFragment();
            Bundle args = new Bundle();
            fragment.setArguments(args);
            return fragment;
        }

        public MyListingsFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_delivery_list, container, false);

            mRecyclerView = (RecyclerView) rootView.findViewById(R.id.my_recycler_view);
            mRecyclerView.setHasFixedSize(true);

            // use a linear layout manager
            mLayoutManager = new LinearLayoutManager(getActivity());
            mRecyclerView.setLayoutManager(mLayoutManager);

            final TextView noDeliveriesTextView = (TextView) rootView.findViewById(R.id.no_deliveries_text_view);
            noDeliveriesTextView.setVisibility(View.VISIBLE);


            final Firebase mRef = new Firebase(Constants.FIREBASE_DELIVERIES_ACTIVE);
            Query queryRef = mRef.orderByChild("originalLister").equalTo(mRef.getAuth().getUid());

            final FirebaseRecyclerAdapter<Delivery, ViewHolder> adapter =
                    new FirebaseRecyclerAdapter<Delivery, ViewHolder>(Delivery.class,R.layout.listview_public_listings,ViewHolder.class,queryRef){
                        @Override
                        protected void populateViewHolder(final ViewHolder viewHolder, Delivery d, final int i) {

                            Drawable background = Constants.getStatusBackgroundDrawable(d.getStatus(), getContext(), false);
                            if (background != null) {
                                viewHolder.mView.findViewById(R.id.list_view_root).setBackground(background);
                            }

                            viewHolder.mDeliveryName.setText(d.getListingName());
                            viewHolder.mDeliveryPickupTime.setText(Constants.getEasyToUnderstandDateTimeString(d.getPickupTime()));
                            viewHolder.mDeliveryDropoffTime.setText(Constants.getEasyToUnderstandDateTimeString(d.getDropoffTime()));
                            viewHolder.mDeliveryPickupLocation.setText(d.getPickupLocation());
                            viewHolder.mDeliveryDropoffLocation.setText(d.getDropoffLocation());
                            viewHolder.mDeliveryDistance.setText("" + d.getDistance());
                            viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(v.getContext(), ViewItemStatusActivity.class)
                                            .putExtra("object_id", getRef(viewHolder.getLayoutPosition()).getKey());
                                    v.getContext().startActivity(intent);
                                }
                            });

                        }

                    };

            mRecyclerView.setAdapter(adapter);

            mRecyclerView.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
                @Override
                public void onChildViewAttachedToWindow(View view) {
                    ((ViewMyListingsActivity)getActivity()).progressBar.setVisibility(View.GONE);
                    noDeliveriesTextView.setVisibility(View.GONE);
                }

                @Override
                public void onChildViewDetachedFromWindow(View view) {

                }
            });

            return rootView;
        }

        public static class ViewHolder extends RecyclerView.ViewHolder{
            View mView;

            public TextView mDeliveryName;
            public TextView mDeliveryPickupTime;
            public TextView mDeliveryDropoffTime;
            public TextView mDeliveryPickupLocation;
            public TextView mDeliveryDropoffLocation;
            public TextView mDeliveryDistance;

            public ViewHolder(View v) {
                super(v);
                mView = v;
                mDeliveryName = (TextView) v.findViewById(R.id.list_item_delivery_name);
                mDeliveryPickupTime = (TextView) v.findViewById(R.id.list_item_pickup_time);
                mDeliveryDropoffTime = (TextView) v.findViewById(R.id.list_item_dropoff_time);
                mDeliveryPickupLocation = (TextView) v.findViewById(R.id.list_item_pickup_location);
                mDeliveryDropoffLocation = (TextView) v.findViewById(R.id.list_item_dropoff_location);
                mDeliveryDistance = (TextView) v.findViewById(R.id.list_item_distance);

                v.setClickable(true);
            }
        }

    }
}

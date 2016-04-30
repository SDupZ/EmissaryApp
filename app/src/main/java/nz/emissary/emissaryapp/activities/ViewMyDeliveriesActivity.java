package nz.emissary.emissaryapp.activities;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
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
public class ViewMyDeliveriesActivity extends BaseActivity{

    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progressBar = (ProgressBar) findViewById(R.id.updateProgressBar);
        progressBar.setVisibility(View.VISIBLE);

    }

    protected int getLayoutResource(){
        return R.layout.activity_view_my_deliveries;
    }

    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    //DeliveryListFragment fragment
    public static class MyDeliveriesFragment extends Fragment {
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
        public static MyDeliveriesFragment newInstance(int sectionNumber) {
            MyDeliveriesFragment fragment = new MyDeliveriesFragment();
            Bundle args = new Bundle();
            fragment.setArguments(args);
            return fragment;
        }

        public MyDeliveriesFragment() {
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

            final Firebase mRef = new Firebase("https://emissary.firebaseio.com/deliveries");
            Query queryRef = mRef.orderByChild("driver").equalTo(mRef.getAuth().getUid());

            final FirebaseRecyclerAdapter<Delivery, ViewHolder> adapter =
                    new FirebaseRecyclerAdapter<Delivery, ViewHolder>(Delivery.class,R.layout.listview_public_listings,ViewHolder.class,queryRef){

                        @Override
                        protected void populateViewHolder(ViewHolder viewHolder, Delivery d, final int i) {

                            Drawable background = Constants.getStatusBackgroundDrawable(d.getStatus(), getContext(), true);
                            if (background != null) {
                                viewHolder.mView.findViewById(R.id.list_view_root).setBackground(background);
                            }

                            viewHolder.mDeliveryName.setText(d.getListingName());
                            viewHolder.mDeliveryPickupTime.setText(Constants.getEasyToUnderstandDateTimeString(d.getPickupTime()));
                            viewHolder.mDeliveryDropoffTime.setText(Constants.getEasyToUnderstandDateTimeString(d.getDropoffTime()));

                            viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(v.getContext(), DriverEditItemActivity.class)
                                            .putExtra("object_id", getRef(i).getKey());
                                    v.getContext().startActivity(intent);
                                }
                            });
                        }

                    };


            mRecyclerView.setAdapter(adapter);

            mRecyclerView.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
                @Override
                public void onChildViewAttachedToWindow(View view) {
                    ((ViewMyDeliveriesActivity)getActivity()).progressBar.setVisibility(View.GONE);
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

            public ViewHolder(View v) {
                super(v);
                mView = v;
                mDeliveryName = (TextView) v.findViewById(R.id.list_item_delivery_name);
                mDeliveryPickupTime = (TextView) v.findViewById(R.id.list_item_pickup_time);
                mDeliveryDropoffTime = (TextView) v.findViewById(R.id.list_item_dropoff_time);

                v.setClickable(true);
            }
        }

    }
}

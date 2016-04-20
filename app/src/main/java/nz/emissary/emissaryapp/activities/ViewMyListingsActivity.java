package nz.emissary.emissaryapp.activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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

    protected int getLayoutResource(){
        return R.layout.activity_view_my_listings;
    }

    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    //DeliveryListFragment fragment
    public static class MyListingsFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private ProgressBar progressBar;
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
            View rootView = inflater.inflate(R.layout.delivery_list_fragment, container, false);

            progressBar = (ProgressBar) rootView.findViewById(R.id.updateProgressBar);

            mRecyclerView = (RecyclerView) rootView.findViewById(R.id.my_recycler_view);
            mRecyclerView.setHasFixedSize(true);

            // use a linear layout manager
            mLayoutManager = new LinearLayoutManager(getActivity());
            mRecyclerView.setLayoutManager(mLayoutManager);

            final Firebase mRef = new Firebase("https://emissary.firebaseio.com/deliveries");
            Query queryRef = mRef.orderByChild("originalLister").equalTo(mRef.getAuth().getUid());

            final FirebaseRecyclerAdapter<Delivery, ViewHolder> adapter =
                    new FirebaseRecyclerAdapter<Delivery, ViewHolder>(Delivery.class,R.layout.listings_list_view,ViewHolder.class,queryRef){
                        @Override
                        protected void populateViewHolder(ViewHolder viewHolder, Delivery d, final int i) {

                            Drawable background = Constants.getStatusBackgroundDrawable(d.getStatus(), getContext(), false);
                            if (background != null) {
                                viewHolder.mView.findViewById(R.id.list_view_root).setBackground(background);
                            }

                            viewHolder.mDeliveryName.setText(d.getListingName());
                            viewHolder.mDeliveryNotes.setText(d.getNotes());
                            viewHolder.mDeliveryPickupTime.setText(Constants.convertTime( Long.parseLong(d.getPickupTime())));
                            viewHolder.mDeliveryDropoffTime.setText(Constants.convertTime( Long.parseLong(d.getDropoffTime())));

                            viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(v.getContext(), ViewItemStatusActivity.class)
                                            .putExtra("object_id", getRef(i).getKey());
                                    v.getContext().startActivity(intent);
                                }
                            });

                        }

                    };


            //This adapter is used to show a progress bar
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
                mDeliveryName = (TextView) v.findViewById(R.id.list_item_delivery_name);
                mDeliveryNotes = (TextView) v.findViewById(R.id.list_item_notes);
                mDeliveryPickupTime = (TextView) v.findViewById(R.id.list_item_pickup_time);
                mDeliveryDropoffTime = (TextView) v.findViewById(R.id.list_item_dropoff_time);

                v.setClickable(true);
            }
        }

    }
}

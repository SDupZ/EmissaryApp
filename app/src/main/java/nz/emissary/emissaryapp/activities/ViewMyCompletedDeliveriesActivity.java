package nz.emissary.emissaryapp.activities;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
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
public class ViewMyCompletedDeliveriesActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_my_completed_deliveries);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        final Firebase mRef = new Firebase(Constants.FIREBASE_DELIVERIES_UNLISTED);
        Query queryRef = mRef.orderByChild("driver").equalTo(mRef.getAuth().getUid());

        final FirebaseRecyclerAdapter<Delivery, ViewHolder> adapter =
                new FirebaseRecyclerAdapter<Delivery, ViewHolder>(Delivery.class,R.layout.listview_public_listings,ViewHolder.class,queryRef){
                    @Override
                    protected void populateViewHolder(final ViewHolder viewHolder, Delivery d, final int i) {
                        Drawable background = Constants.getStatusBackgroundDrawable(d.getStatus(), getApplicationContext(), false);
                        if (background != null) {
                            viewHolder.mView.findViewById(R.id.list_view_root).setBackground(background);
                        }

                        viewHolder.mDeliveryName.setText(d.getListingName());
                        viewHolder.mDeliveryPickupTime.setText(Constants.getEasyToUnderstandDateTimeString(d.getPickupTime()));
                        viewHolder.mDeliveryDropoffTime.setText(Constants.getEasyToUnderstandDateTimeString(d.getDropoffTime()));
                        viewHolder.mDeliveryPickupLocation.setText(d.getPickupLocationShort());
                        viewHolder.mDeliveryDropoffLocation.setText(d.getDropoffLocationShort());
                        viewHolder.mDeliveryDistance.setText(Constants.getDistanceString(d.getDistance()));

                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(v.getContext(), ViewCompletedItemActivity.class)
                                        .putExtra("object_id", getRef(viewHolder.getLayoutPosition()).getKey());
                                v.getContext().startActivity(intent);
                            }
                        });
                    }

                };
        mRecyclerView.setAdapter(adapter);
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

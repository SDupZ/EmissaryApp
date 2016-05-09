package nz.emissary.emissaryapp;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.Query;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;

import java.util.ArrayList;
import java.util.List;

import nz.emissary.emissaryapp.activities.ViewItemActivity;

/**
 * Created by Simon on 3/05/2016.
 */
public class CustomFirebaseListingsAdapter extends RecyclerView.Adapter<CustomFirebaseListingsAdapter.ViewHolder> {

    FirebaseArray mSnapshots;

    public CustomFirebaseListingsAdapter(GeoQuery geoQuery){

        final Firebase mRef = new Firebase(Constants.FIREBASE_DELIVERIES_ACTIVE);
        Query queryRef = mRef.orderByChild("status").equalTo(Constants.STATUS_LISTED);

        mSnapshots = new FirebaseArray(queryRef, geoQuery);
        mSnapshots.setOnChangedListener(new FirebaseArray.OnChangedListener() {
            @Override
            public void onChanged(EventType type, int index, int oldIndex) {
                switch (type) {
                    case Added:
                        notifyItemInserted(index);
                        break;
                    case Changed:
                        notifyItemChanged(index);
                        break;
                    case Removed:
                        notifyItemRemoved(index);
                        break;
                    case Moved:
                        notifyItemMoved(oldIndex, index);
                        break;
                    default:
                        throw new IllegalStateException("Incomplete case statement");
                }
            }
        });
    }

    public void updateLocation(GeoLocation center, double radius){
        mSnapshots.updateGeoQuery(center, radius);
    }
    public void updateLocation(double radius){
        mSnapshots.updateGeoQuery(null, radius);
    }

    public void cleanup() {
        mSnapshots.cleanup();
    }

    @Override
    public int getItemCount() {
        return mSnapshots.getCount();
    }

    public Delivery getItem(int position) {
        return parseSnapshot(mSnapshots.getItem(position));
    }

    protected Delivery parseSnapshot(DataSnapshot snapshot) {
        return snapshot.getValue(Delivery.class);
    }

    public Firebase getRef(int position) { return mSnapshots.getItem(position).getRef(); }

    @Override
    public long getItemId(int position) {
        // http://stackoverflow.com/questions/5100071/whats-the-purpose-of-item-ids-in-android-listview-adapter
        return mSnapshots.getItem(position).getKey().hashCode();
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.listview_public_listings, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        final Delivery d = getItem(position);
        viewHolder.mDeliveryName.setText(d.getListingName());
        viewHolder.mDeliveryPickupTime.setText(Constants.getEasyToUnderstandDateTimeString(d.getPickupTime()));
        viewHolder.mDeliveryDropoffTime.setText(Constants.getEasyToUnderstandDateTimeString(d.getDropoffTime()));
        viewHolder.mDeliveryPickupLocation.setText(d.getPickupLocationShort());
        viewHolder.mDeliveryDropoffLocation.setText(d.getDropoffLocationShort());
        viewHolder.mDeliveryDistance.setText(Constants.getDistanceString(d.getDistance()));
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
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
            mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("EMISSARY", "" + getLayoutPosition());
                    Intent intent = new Intent(v.getContext(), ViewItemActivity.class)
                            .putExtra("object_id", getRef(getLayoutPosition()).getKey());
                    v.getContext().startActivity(intent);
                }
            });
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

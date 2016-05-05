package nz.emissary.emissaryapp;

import android.os.Debug;
import android.util.Log;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;

import java.util.ArrayList;

/**
 * This class implements an array-like collection on top of a Firebase location.
 */
class FirebaseArray implements GeoQueryEventListener, ValueEventListener{

    public interface OnChangedListener {
        enum EventType { Added, Changed, Removed, Moved }
        void onChanged(EventType type, int index, int oldIndex);
    }

    private Query mQuery;
    private GeoQuery mGeoQuery;
    private OnChangedListener mListener;
    private ArrayList<DataSnapshot> mSnapshots;
    Firebase mRef;

    public FirebaseArray(Query ref, GeoQuery geoRef) {
        mRef = new Firebase(Constants.FIREBASE_DELIVERIES_ACTIVE);
        mQuery = ref;
        mGeoQuery = geoRef;
        mSnapshots = new ArrayList<DataSnapshot>();

        //mQuery.addChildEventListener(this);
        mGeoQuery.addGeoQueryEventListener(this);
    }

    public void cleanup() {
        mQuery.removeEventListener(this);
    }

    public int getCount() {
        return mSnapshots.size();

    }
    public DataSnapshot getItem(int index) {
        return mSnapshots.get(index);
    }

    private int getIndexForKey(String key) {
        int index = 0;
        for (DataSnapshot snapshot : mSnapshots) {
            if (snapshot.getKey().equals(key)) {
                return index;
            } else {
                index++;
            }
        }
        throw new IllegalArgumentException("Key not found");
    }

    public void setOnChangedListener(OnChangedListener listener) {
        mListener = listener;
    }
    protected void notifyChangedListeners(OnChangedListener.EventType type, int index) {
        notifyChangedListeners(type, index, -1);
    }
    protected void notifyChangedListeners(OnChangedListener.EventType type, int index, int oldIndex) {
        if (mListener != null) {
            mListener.onChanged(type, index, oldIndex);
        }
    }

    // GEO Query Listeners

    @Override
    public void onKeyEntered(String key, GeoLocation location) {
        Log.d("EMISSARY", String.format("Key %s entered the search area at [%f,%f]", key, location.latitude, location.longitude));
        mRef.child(key).addValueEventListener(this);
    }

    @Override
    public void onKeyExited(String key) {
        Log.d("EMISSARY", String.format("Key %s is no longer in the search area", key));
    }

    @Override
    public void onKeyMoved(String key, GeoLocation location) {
        Log.d("EMISSARY", String.format("Key %s moved within the search area to [%f,%f]", key, location.latitude, location.longitude));
    }

    @Override
    public void onGeoQueryReady() {
        Log.d("EMISSARY", "All initial data has been loaded and events have been fired!");
    }

    @Override
    public void onGeoQueryError(FirebaseError error) {
        Log.d("EMISSARY", "There was an error with this query: " + error);
    }

    //------------------------Value event listener-----------------------
    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        int index = getCount();

        if (mSnapshots.contains(dataSnapshot)){
            mSnapshots.remove(dataSnapshot);
        }

        for (DataSnapshot s : mSnapshots) {
            String s1 = s.getValue(Delivery.class).getListingName();
            String s2 = dataSnapshot.getValue(Delivery.class).getListingName();

            if (s1.compareTo(s2) > 0){
                index = getIndexForKey(s.getKey());
                break;
            }
        }
        mSnapshots.add(index, dataSnapshot);
        notifyChangedListeners(OnChangedListener.EventType.Added, index);
    }

    @Override
    public void onCancelled(FirebaseError firebaseError) {

    }

}
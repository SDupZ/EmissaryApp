package nz.emissary.emissaryapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
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
            View rootView = inflater.inflate(R.layout.delivery_list_fragment, container, false);

            mRecyclerView = (RecyclerView) rootView.findViewById(R.id.my_recycler_view);
            mRecyclerView.setHasFixedSize(true);

            // use a linear layout manager
            mLayoutManager = new LinearLayoutManager(getActivity());
            mRecyclerView.setLayoutManager(mLayoutManager);

            final Firebase mRef = new Firebase("https://emissary.firebaseio.com/deliveries");
            Query queryRef = mRef.orderByChild("driver").equalTo(mRef.getAuth().getUid());

            final FirebaseRecyclerAdapter<Delivery, ViewHolder> adapter =
                    new FirebaseRecyclerAdapter<Delivery, ViewHolder>(Delivery.class,R.layout.my_deliveries_list_view,ViewHolder.class,queryRef){
                        @Override
                        protected void populateViewHolder(ViewHolder viewHolder, Delivery d, final int i) {

                            if (d.getStatus() == Constants.STATUS_ACCEPTED){
                                viewHolder.mView.findViewById(R.id.list_view_root).setBackground(getResources().getDrawable(R.drawable.selector_row_accepted));
                            }else if (d.getStatus() == Constants.STATUS_DELIVERED){
                                viewHolder.mView.findViewById(R.id.list_view_root).setBackground(getResources().getDrawable(R.drawable.selector_row_delivered));
                            }else if (d.getStatus() == Constants.STATUS_CANCELLED){
                                viewHolder.mView.findViewById(R.id.list_view_root).setBackground(getResources().getDrawable(R.drawable.selector_row_cancelled));
                            }

                            viewHolder.mDeliveryName.setText(d.getListingName());
                            viewHolder.mDeliveryPickupTime.setText(d.getNotes());

                            viewHolder.mView.setOnTouchListener(new View.OnTouchListener() {
                                @Override
                                public boolean onTouch(View v, MotionEvent event) {
                                    if (event.getAction() == MotionEvent.ACTION_CANCEL) {
                                        v.setSelected(false);
                                        return true;
                                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                                        v.setSelected(false);
                                        Intent intent = new Intent(v.getContext(), EditItemActivity.class)
                                                .putExtra("object_id", getRef(i).getKey());
                                        v.getContext().startActivity(intent);

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

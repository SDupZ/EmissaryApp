package nz.emissary.emissaryapp;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import nz.emissary.emissaryapp.activities.ViewItemActivity;

/**
 * Created by Simon on 1/11/2015.
 */
public class DeliveryListRecyclerAdapter extends RecyclerView.Adapter<DeliveryListRecyclerAdapter.ViewHolder> {
    private int selected;
    private List<Object> mDataset;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnTouchListener{
        Object object;
        int position;

        public TextView mDeliveryName;
        public TextView mDeliveryPickupTime;

        public ViewHolder(View v) {
            super(v);
            mDeliveryName = (TextView) v.findViewById(R.id.list_item_delivery_name);
            mDeliveryPickupTime = (TextView) v.findViewById(R.id.list_item_pickup_time);

            v.setClickable(true);
            v.setOnTouchListener(this);
        }
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_CANCEL){
                v.setSelected(false);
                selected = -1;
                return true;
            } else if(event.getAction() == MotionEvent.ACTION_UP && position == selected){
                v.setSelected(false);
                selected = -1;
                Intent intent = new Intent(v.getContext(), ViewItemActivity.class)
                        .putExtra("object_id", "STUB");
                v.getContext().startActivity(intent);
                return true;
            }else if(event.getAction() == MotionEvent.ACTION_DOWN && selected == -1){
                v.setSelected(true);
                selected = position;
                return true;
            }
            return false;
        }

        public void bindDeal(Object object, int position){
            this.position = position;
            this.object = object;

            mDeliveryName.setText("STUB");
            mDeliveryPickupTime.setText("STUB");
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public DeliveryListRecyclerAdapter(List<Object> myDataset) {
        mDataset = myDataset;
        selected = -1;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public DeliveryListRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.delivery_list_view, parent, false);
        // set the view's size, margins, paddings and layout parameters

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.bindDeal(mDataset.get(position), position);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
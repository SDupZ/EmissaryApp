package nz.emissary.emissaryapp.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.firebase.ui.FirebaseRecyclerAdapter;

import org.w3c.dom.Text;

import nz.emissary.emissaryapp.Constants;
import nz.emissary.emissaryapp.Delivery;
import nz.emissary.emissaryapp.Feedback;
import nz.emissary.emissaryapp.R;
import nz.emissary.emissaryapp.User;

/**
 * Created by Simon on 3/03/2016.
 */
public class ViewMyFeedbackActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_my_feedback);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        final Firebase mRef = new Firebase("https://emissary.firebaseio.com/feedback");
        Query queryRef = mRef.orderByChild("userId").equalTo(mRef.getAuth().getUid());

        final FirebaseRecyclerAdapter<Feedback, ViewHolder> adapter =
                new FirebaseRecyclerAdapter<Feedback, ViewHolder>(Feedback.class,R.layout.my_feedback_list_view,ViewHolder.class,queryRef){
                    @Override
                    protected void populateViewHolder(ViewHolder viewHolder, Feedback f, final int i) {
                        viewHolder.mRatingView.setRating((float)f.getRating());
                        viewHolder.mFeedbackTextView.setText(f.getFeedbackMessage());
                        String time = Constants.TIME_SPECIFIC + Constants.TIME_TOKEN + f.getFeedbackPostTime();
                        viewHolder.mFeedbackPostTimeView.setText(Constants.convertTime(time));

                        String message = f.isFeedbackIsForDriver() ? "Delivery Driver" : "Original Lister";
                        viewHolder.mFeedbackRoleView.setText(message);
                    }

                };
        mRecyclerView.setAdapter(adapter);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        View mView;

        public RatingBar mRatingView;
        public TextView mFeedbackTextView;
        public TextView mFeedbackPostTimeView;
        public TextView mFeedbackRoleView;

        public ViewHolder(View v) {
            super(v);
            mView = v;
            mRatingView = (RatingBar) v.findViewById(R.id.list_item_rating);
            mFeedbackTextView = (TextView) v.findViewById(R.id.list_item_feedback_text);
            mFeedbackPostTimeView = (TextView) v.findViewById(R.id.list_item_feedback_date);
            mFeedbackRoleView = (TextView) v.findViewById(R.id.list_item_feedback_role_text);
        }
    }


}
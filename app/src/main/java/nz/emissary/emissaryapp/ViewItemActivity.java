package nz.emissary.emissaryapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Simon on 3/03/2016.
 */
public class ViewItemActivity extends BaseActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        if(intent != null && intent.hasExtra("object_id")) {

            final TextView nameView = ((TextView)findViewById(R.id.item_name));
            final TextView pickupLocationView = ((TextView)findViewById(R.id.item_pickup_location));
            final TextView dropOffLocationView = ((TextView)findViewById(R.id.item_drop_off_location));
            final TextView notesView = ((TextView)findViewById(R.id.item_notes));

            final AppCompatButton acceptDeliveryButton = (AppCompatButton) findViewById(R.id.accept_delivery);


            //----------------Load the object from the local database---------------
            String objectID = intent.getStringExtra("object_id");

            //----------------Accept a delivery---------------
            acceptDeliveryButton.setOnClickListener(this);
        }
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_view_item;
    }

    @Override
    public void onClick(View v) {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(ViewItemActivity.this, R.style.MyAlertDialogStyle);
        builder.setTitle("Driver Confirmation");
        builder.setMessage(getResources().getString(R.string.confirm_dialog));
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setNegativeButton("Cancel", null);

        AppCompatDialog dialog = builder.create();
        dialog.show();
    }

}
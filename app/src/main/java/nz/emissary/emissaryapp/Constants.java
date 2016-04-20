package nz.emissary.emissaryapp;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Simon on 8/03/2016.
 */
public class Constants {
    final public static int STATUS_LISTED               = 0;
    final public static int STATUS_ACCEPTED             = 100;
    final public static int STATUS_PICKED_UP            = 200;

    final public static int STATUS_DELIVERED_NO_FB      = 300;
    final public static int STATUS_DELIVERED_D_FB       = 301;
    final public static int STATUS_DELIVERED_L_FB       = 302;

    final public static int STATUS_COMPLETE             = 400;
    final public static int STATUS_CANCELLED            = -1;

    final public static int MINIMUM_MESSAGE_REQUEST_LENGTH  = 10;

    public static String convertTime(long time){
        Date date = new Date(time);
        Format format = new SimpleDateFormat("yyyy/MM/dd - HH:mm");
        return format.format(date);
    }

    public static String getStatusDescription(int status, Context context, boolean isForDriver){
        if (isForDriver) {
            if (status == STATUS_LISTED){
                return context.getResources().getString(R.string.status_listed_driver);
            }else if (status == STATUS_ACCEPTED){
                return context.getResources().getString(R.string.status_accepted_driver);
            }else if (status == STATUS_PICKED_UP){
                return context.getResources().getString(R.string.status_picked_up_driver);
            }else if (status == STATUS_DELIVERED_NO_FB){
                return context.getResources().getString(R.string.status_delivered_no_fb_driver);
            }else if (status == STATUS_DELIVERED_D_FB){
                return context.getResources().getString(R.string.status_delivered_d_fb_driver);
            }else if (status == STATUS_DELIVERED_L_FB){
                return context.getResources().getString(R.string.status_delivered_l_fb_driver);
            }else if (status == STATUS_COMPLETE) {
                return context.getResources().getString(R.string.status_complete_driver);
            }else{
                return context.getResources().getString(R.string.status_unknown_driver);
            }
        }else{
            if (status == STATUS_LISTED){
                return context.getResources().getString(R.string.status_listed);
            }else if (status == STATUS_ACCEPTED){
                return context.getResources().getString(R.string.status_accepted);
            }else if (status == STATUS_PICKED_UP){
                return context.getResources().getString(R.string.status_picked_up);
            }else if (status == STATUS_DELIVERED_NO_FB){
                return context.getResources().getString(R.string.status_delivered_no_fb);
            }else if (status == STATUS_DELIVERED_D_FB){
                return context.getResources().getString(R.string.status_delivered_d_fb);
            }else if (status == STATUS_DELIVERED_L_FB){
                return context.getResources().getString(R.string.status_delivered_l_fb);
            }else if (status == STATUS_COMPLETE) {
                return context.getResources().getString(R.string.status_complete);
            }else{
                return context.getResources().getString(R.string.status_unknown);
            }
        }
    }

    //Note returns null if background should not change
    public static Drawable getStatusBackgroundDrawable(int status, Context context, boolean isForDriver){
        if (status == STATUS_LISTED){
            return null;
        }else if (status == STATUS_ACCEPTED){
            return ContextCompat.getDrawable(context, R.drawable.selector_row_accepted);
        }else if (status == STATUS_PICKED_UP){
            return ContextCompat.getDrawable(context, R.drawable.selector_row_picked_up);
        }else if (status == STATUS_DELIVERED_NO_FB){
            return ContextCompat.getDrawable(context, R.drawable.selector_row_awaiting_feedback);
        }else if (status == STATUS_DELIVERED_D_FB){
            return (isForDriver ? ContextCompat.getDrawable(context, R.drawable.selector_row_awaiting_feedback)
            : ContextCompat.getDrawable(context, R.drawable.selector_row_complete));
        }else if (status == STATUS_DELIVERED_L_FB){
            return (isForDriver ? ContextCompat.getDrawable(context, R.drawable.selector_row_complete)
                    : ContextCompat.getDrawable(context, R.drawable.selector_row_awaiting_feedback));
        }else if (status == STATUS_COMPLETE) {
            return ContextCompat.getDrawable(context, R.drawable.selector_row_complete);
        }else if (status == STATUS_CANCELLED){
            return ContextCompat.getDrawable(context, R.drawable.selector_row_cancelled);
        }else{
            return null;
        }
    }

    public static String getNextStatusMessageForLister(int currentStatus, Context context){
        String message = "";
        if (currentStatus == Constants.STATUS_ACCEPTED) {
            message = "" + context.getResources().getString(R.string.driver_update_confirm) + "\n\n\"" + Constants.getStatusDescription(Constants.STATUS_PICKED_UP, context, false) + "\"";
        }else if(currentStatus == Constants.STATUS_PICKED_UP){
            message = "" + context.getResources().getString(R.string.driver_update_confirm) + "\n\n\""+ Constants.getStatusDescription(Constants.STATUS_DELIVERED_NO_FB, context, false) + "\"";
        }
        return message;
    }

    public static int getNextStatus(int currentStatus){
        switch (currentStatus){
            case (Constants.STATUS_ACCEPTED):
                return Constants.STATUS_PICKED_UP;
            case Constants.STATUS_PICKED_UP:
                return Constants.STATUS_DELIVERED_NO_FB;
            default:
                return Constants.STATUS_ACCEPTED;
        }
    }

    public static String getUpdateButtonText(int currentStatus, Context context) {
        switch (currentStatus) {
            case (Constants.STATUS_ACCEPTED):
                return context.getResources().getString(R.string.driver_button_picked_up);
            case Constants.STATUS_PICKED_UP:
                return context.getResources().getString(R.string.driver_button_complete);
            default:
                return context.getResources().getString(R.string.driver_button_unknown);
        }
    }
}

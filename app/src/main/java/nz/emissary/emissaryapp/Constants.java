package nz.emissary.emissaryapp;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Simon on 8/03/2016.
 */
public class Constants {
    final public static String FIREBASE_BASE = "https://emissary.firebaseio.com";

    final public static String FIREBASE_USERS = "https://emissary.firebaseio.com/users";
    final public static String FIREBASE_USERS_BASE_CHILD = "users";

    final public static String FIREBASE_MESSAGES= "https://emissary.firebaseio.com/messages/";

    final public static String FIREBASE_FEEDBACK = "https://emissary.firebaseio.com/feedback";
    final public static String FIREBASE_FEEDBACK_BASE_CHILD = "feedback";

    final public static String FIREBASE_DELIVERIES_ACTIVE = "https://emissary.firebaseio.com/deliveries_active";
    final public static String FIREBASE_DELIVERIES_ACTIVE_BASE_CHILD = "deliveries_active";

    final public static String FIREBASE_DELIVERIES_PENDING = "https://emissary.firebaseio.com/deliveries_pending";
    final public static String FIREBASE_DELIVERIES_PENDING_BASE_CHILD = "deliveries_pending";

    final public static String FIREBASE_DELIVERIES_UNLISTED = "https://emissary.firebaseio.com/deliveries_unlisted";
    final public static String FIREBASE_DELIVERIES_UNLISTED_BASE_CHILD = "deliveries_unlisted";


    final public static String TIME_ASAP = "ASAP";
    final public static String TIME_SPECIFIC = "SPECIFIC";
    final public static String TIME_RANGE = "RANGE";
    final public static String TIME_TOKEN = ":";

    final public static int STATUS_LISTED               = 0;
    final public static int STATUS_ACCEPTED             = 100;
    final public static int STATUS_PICKED_UP            = 200;

    final public static int STATUS_DELIVERED_NO_FB      = 300;
    final public static int STATUS_DELIVERED_D_FB       = 301;
    final public static int STATUS_DELIVERED_L_FB       = 302;

    final public static int STATUS_COMPLETE             = 400;
    final public static int STATUS_CANCELLED            = -1;

    final public static int MINIMUM_MESSAGE_REQUEST_LENGTH  = 10;

    public static String getFullDateTimeString(String timeString){
        String[] resultArray = timeString.split(Constants.TIME_TOKEN);

        if (resultArray.length == 1 && resultArray[0].equals(Constants.TIME_ASAP)){
            return "ASAP";
        }else if(resultArray.length == 2 && resultArray[0].equals(Constants.TIME_SPECIFIC)){
            try {
                Long time = Long.parseLong(resultArray[1]);
                Calendar date = Calendar.getInstance();
                date.setTimeInMillis(time);

                Date d = date.getTime();

                Format format = new SimpleDateFormat("yyyy/MM/dd - HH:mm");
                String result = format.format(d);

                return result;
            }catch (NumberFormatException e){
                return "ERROR";
            }
        }else if(resultArray.length == 3 && resultArray[0].equals(Constants.TIME_RANGE)){
            try {
                Long timeBegin = Long.parseLong(resultArray[1]);
                Long timeEnd = Long.parseLong(resultArray[2]);

                Calendar date = Calendar.getInstance();
                date.setTimeInMillis(timeBegin);
                Date d = date.getTime();

                Calendar date1 = Calendar.getInstance();
                date.setTimeInMillis(timeEnd);
                Date d1 = date1.getTime();

                Format format = new SimpleDateFormat("yyyy/MM/dd");
                Format format2 = new SimpleDateFormat("HH:mm");

                String result1 = format.format(d);
                String result2 = format2.format(d);
                String result3 = format2.format(d1);

                return result1 + " - Between " + result2 + " & " + result3;
            }catch (NumberFormatException e){
                return "ERROR";
            }
        }else{
            Log.d("EMISSARY", timeString);
            return "ERROR";
        }
    }



    public static String getEasyToUnderstandDateTimeString(String timeString){
        String[] resultArray = timeString.split(Constants.TIME_TOKEN);

        if (resultArray.length == 1 && resultArray[0].equals(Constants.TIME_ASAP)){
            return "ASAP";
        }else if(resultArray.length == 2 && resultArray[0].equals(Constants.TIME_SPECIFIC)){
            try {
                Long time = Long.parseLong(resultArray[1]);
                Calendar date = Calendar.getInstance();
                date.setTimeInMillis(time);
                Date d = date.getTime();

                Format format = new SimpleDateFormat("EEE d 'at' h:ma");
                if (date.get(Calendar.MINUTE) == 0){
                    format = new SimpleDateFormat("EEE d 'at' ha");
                }

                String result = format.format(d);

                return result;
            }catch (NumberFormatException e){
                return "ERROR";
            }
        }else if(resultArray.length == 3 && resultArray[0].equals(Constants.TIME_RANGE)){
            try {
                Long timeBegin = Long.parseLong(resultArray[1]);
                Long timeEnd = Long.parseLong(resultArray[2]);

                Calendar date = Calendar.getInstance();
                date.setTimeInMillis(timeBegin);
                Date d = date.getTime();

                Calendar date1 = Calendar.getInstance();
                date.setTimeInMillis(timeEnd);
                Date d1 = date.getTime();

                Format format = new SimpleDateFormat("EEE d");

                Format format2 = new SimpleDateFormat("h:ma");
                if (date.get(Calendar.MINUTE) == 0){
                    format2 = new SimpleDateFormat("ha");
                }

                Format format3 = new SimpleDateFormat("h:ma");
                if (date.get(Calendar.MINUTE) == 0){
                    format3 = new SimpleDateFormat("ha");
                }

                String result1 = format.format(d);
                String result2 = format2.format(d);
                String result3 = format3.format(d1);

                return result1 + " - Between " + result2 + " & " + result3;
            }catch (NumberFormatException e){
                return "ERROR";
            }
        }else{
            Log.d("EMISSARY", timeString);
            return "ERROR";
        }
    }

    public static String getTimeStamp(String timeString){
        String[] resultArray = timeString.split(Constants.TIME_TOKEN);

        if(resultArray.length == 2 && resultArray[0].equals(Constants.TIME_SPECIFIC)){
            try {
                Long time = Long.parseLong(resultArray[1]);
                Calendar date = Calendar.getInstance();
                date.setTimeInMillis(time);
                Date d = date.getTime();

                Format format = new SimpleDateFormat("d - ka");
                String result = format.format(d);

                return result;
            }catch (NumberFormatException e){
                return "ERROR";
            }
        }else {
            return "ERROR";
        }
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

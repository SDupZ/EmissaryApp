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
    final public static int STATUS_LISTED         = 0;
    final public static int STATUS_ACCEPTED       = 100;
    final public static int STATUS_PICKED_UP      = 200;
    final public static int STATUS_DELIVERED      = 300;
    final public static int STATUS_CANCELLED      = -1;

    public static String convertTime(long time){
        Date date = new Date(time);
        Format format = new SimpleDateFormat("yyyy/MM/dd - HH:mm");
        return format.format(date);
    }

    public static String getStatusDescription(int status, Context context, boolean isForDriver){
        if (isForDriver) {
            switch (status) {
                case STATUS_LISTED:
                    return context.getResources().getString(R.string.status_listed_driver);
                case STATUS_ACCEPTED:
                    return context.getResources().getString(R.string.status_accepted_driver);
                case STATUS_PICKED_UP:
                    return context.getResources().getString(R.string.status_picked_up_driver);
                case STATUS_DELIVERED:
                    return context.getResources().getString(R.string.status_delivered_driver);
                default:
                    return context.getResources().getString(R.string.status_unknown_driver);
            }
        }else{
            switch (status) {
                case STATUS_LISTED:
                    return context.getResources().getString(R.string.status_listed);
                case STATUS_ACCEPTED:
                    return context.getResources().getString(R.string.status_accepted);
                case STATUS_PICKED_UP:
                    return context.getResources().getString(R.string.status_picked_up);
                case STATUS_DELIVERED:
                    return context.getResources().getString(R.string.status_delivered);
                default:
                    return context.getResources().getString(R.string.status_unknown);
            }
        }
    }

    //Note returns null if background should not change
    public static Drawable getStatusBackgroundDrawable(int status, Context context){
        switch (status){
            case 0:
                return null;
            case 100:
                return ContextCompat.getDrawable(context, R.drawable.selector_row_accepted);
            case 200:
                return ContextCompat.getDrawable(context, R.drawable.selector_row_picked_up);
            case 300:
                return ContextCompat.getDrawable(context, R.drawable.selector_row_delivered);
            case -1:
                return ContextCompat.getDrawable(context, R.drawable.selector_row_cancelled);
            default:
                return null;

        }
    }
}

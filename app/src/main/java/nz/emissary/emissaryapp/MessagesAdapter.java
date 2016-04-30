package nz.emissary.emissaryapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Simon on 27/04/2016.
 */
public class MessagesAdapter extends ArrayAdapter<SimpleMessage> {

    public MessagesAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public MessagesAdapter(Context context,List<SimpleMessage> items) {
        super(context, 0, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.message_view, null);
        }

        SimpleMessage p = getItem(position);

        if (p != null) {
            TextView tt1 = (TextView) v.findViewById(R.id.message_content);
            TextView tt2 = (TextView) v.findViewById(R.id.time_stamp);

            tt1.setText(p.getMessage());
            tt2.setText(Constants.getTimeStamp(Constants.TIME_SPECIFIC + Constants.TIME_TOKEN + p.getTimeStamp()));
        }

        return v;
    }

}
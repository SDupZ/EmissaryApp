package nz.emissary.emissaryapp;

import com.firebase.client.Firebase;

/**
 * Created by Simon on 4/03/2016.
 */
public class EmissaryApplication extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Firebase.setAndroidContext(this);
    }

}

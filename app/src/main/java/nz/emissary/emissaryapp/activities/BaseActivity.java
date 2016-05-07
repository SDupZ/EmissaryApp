package nz.emissary.emissaryapp.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import nz.emissary.emissaryapp.Constants;
import nz.emissary.emissaryapp.R;
import nz.emissary.emissaryapp.User;

/**
 * Created by Simon on 2/03/2016.
 */
public abstract class BaseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    protected Toolbar toolbar;

    private TextView username;
    private TextView email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Firebase mRef = new Firebase("https://emissary.firebaseio.com");
        setContentView(getLayoutResource());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");

        TextView tv = (TextView) findViewById(R.id.toolbar_title);
        Typeface face = Typeface.createFromAsset(getAssets(),
                "fonts/emissary_font_main.ttf");

        tv.setTypeface(face);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        View headerLayout = navigationView.inflateHeaderView(R.layout.nav_header_main);
        headerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);

                /// / Handle the camera action
                Intent intent = new Intent(getApplicationContext(), ViewAccountActivity.class);
                startActivity(intent);
            }
        });
        navigationView.setNavigationItemSelectedListener(this);

        username = (TextView) headerLayout.findViewById(R.id.username);
        email = (TextView) headerLayout.findViewById(R.id.email);

        Firebase currentFirebaseUser = new Firebase(Constants.FIREBASE_USERS).child(mRef.getAuth().getUid());
        currentFirebaseUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user == null) {
                    mRef.unauth();
                    Intent loginActivity = new Intent(BaseActivity.this, LoginActivity.class);
                    BaseActivity.this.startActivity(loginActivity);
                    finish();
                }else{
                    username.setText("Hi " + user.getFirstName().trim() + "!");
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        email.setText(mRef.getAuth().getProviderData().get("email").toString());
    }

    protected abstract int getLayoutResource();

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.view_public_listings) {
            Intent intent = new Intent(getApplicationContext(), ViewPublicListingsActivity.class);
            startActivity(intent);
        } else if (id == R.id.listed) {
            Intent intent = new Intent(getApplicationContext(), ViewMyListingsActivity.class);
            startActivity(intent);
        }else if (id ==R.id.current_deliveries){
            Intent intent = new Intent(getApplicationContext(), ViewMyDeliveriesActivity.class);
            startActivity(intent);
        }else if (id ==R.id.contact_us){
            Intent intent = new Intent(getApplicationContext(), ContactUsActivity.class);
            startActivity(intent);
        }else if (id ==R.id.previous_listings){
            Intent intent = new Intent(getApplicationContext(), ViewMyCompletedListingsActivity.class);
            startActivity(intent);
        }else if (id ==R.id.create_delivery){
            Intent intent = new Intent(getApplicationContext(), CreateDeliveryActivity.class);
            startActivity(intent);
        }else if (id ==R.id.view_driver_dashboard){
            //TODO implement this
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}

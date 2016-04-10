package nz.emissary.emissaryapp.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import nz.emissary.emissaryapp.R;

public class SplashActivity extends Activity implements View.OnTouchListener{

    private final int SPLASH_DISPLAY_LENGTH = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        TextView tv = (TextView) findViewById(R.id.logo_text_drawable);
        Typeface face = Typeface.createFromAsset(getAssets(),
                "fonts/emissary_font_main.ttf");

        tv.setTypeface(face);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent mainIntent = new Intent(SplashActivity.this, HomeActivity.class);
                SplashActivity.this.startActivity(mainIntent);
                SplashActivity.this.finish();
            }
        }, SPLASH_DISPLAY_LENGTH);

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        Intent mainIntent = new Intent(SplashActivity.this, HomeActivity.class);
        SplashActivity.this.startActivity(mainIntent);
        SplashActivity.this.finish();
        return false;
    }
}

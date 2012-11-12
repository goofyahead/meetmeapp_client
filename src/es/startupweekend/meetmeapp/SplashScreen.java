package es.startupweekend.meetmeapp;
import es.startupweekend.meetmeapp.R;

import es.startupweekened.preferences.MeetMePreferences;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;

public class SplashScreen extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
          @Override
          public void run() {
              MeetMePreferences mPreferences = new MeetMePreferences(getApplicationContext());
              if (mPreferences.isUserRegistered()){
                  // Go to main screen
                  startActivity(new Intent(SplashScreen.this,MainActivity.class));
              }
              else {
                  // Go to Register screen
                  startActivity(new Intent(SplashScreen.this,RegisterActivity.class));
              }
          }
        }, 1500);
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_splash_screen, menu);
        return true;
    }
}

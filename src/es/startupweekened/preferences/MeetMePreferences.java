package es.startupweekened.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class MeetMePreferences {

    private static final String USER_REGISTERED = "user_registered";

    private SharedPreferences prefs;

    public MeetMePreferences(Context context) {
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public boolean isUserRegistered (){
        return prefs.getBoolean(USER_REGISTERED, false);
    }

    public void setUserRegistered(boolean userRegistered) {
        prefs.edit().putBoolean(USER_REGISTERED, userRegistered).commit();
    }
}

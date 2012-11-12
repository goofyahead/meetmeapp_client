package es.startupweekened.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class MeetMePreferences {

    private static final String USER_REGISTERED = "user_registered";
    private static final String USER_NAME = "user_name";
    private static final String USER_ID = "user_id";

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

    public String getUserName() {
        return prefs.getString(USER_NAME, "");
    }

    public void setUserName(String userName) {
        prefs.edit().putString(USER_NAME, userName).commit();
    }
    
    public String getUserId() {
        return prefs.getString(USER_ID, "");
    }

    public void setUserId(String userId) {
        prefs.edit().putString(USER_ID, userId).commit();
    }
}

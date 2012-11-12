package es.startupweekend.meetmeapp;

import java.util.List;

import com.bump.api.BumpAPIIntents;
import com.bump.api.IBumpAPI;

import es.startupweekend.adapters.ImageAdapter;
import es.startupweekend.api.MeetmeApi;
import es.startupweekend.api.MeetmeApiInterface;
import es.startupweekend.meetmeapp.R;
import es.startupweekend.model.User;
import es.startupweekened.preferences.MeetMePreferences;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

public class MainActivity extends Activity {

    private GridView gridView;
    private long timestamp;
    private Spinner spinner;
    private ArrayAdapter<CharSequence> adapter;
    private ImageAdapter imageAdapter;
    private IBumpAPI api;
    private Button buton;
    private String TAG = MainActivity.this.getClass().getName();

    private final ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder binder) {
            final IBinder myBinder = binder;
            Log.i("BumpTest", "onServiceConnected");
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    try {
                        api = IBumpAPI.Stub.asInterface(myBinder);
                        MeetMePreferences prefs = new MeetMePreferences(getApplicationContext());
                        api.configure("dd50a9abd3764df1910936d25566e6a4", prefs.getUserName());
                        Log.d(TAG, "registered with: " + prefs.getUserName());
                    } catch (RemoteException e) {
                        Log.w("BumpTest", e);
                    }
                    return null;
                }
            }.execute();

            Log.d("Bump Test", "Service connected");
        }

        @Override
        public void onServiceDisconnected(ComponentName className) {
            Log.d("Bump Test", "Service disconnected");
        }
    };

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            MeetMePreferences prefs = new MeetMePreferences(getApplicationContext());
            try {
                if (action.equals(BumpAPIIntents.DATA_RECEIVED)) {
                    final Dialog dialog = new Dialog(context);
                    dialog.setContentView(R.layout.dialog_meet);
                    dialog.setTitle("Conectado!");

                    ImageView image = (ImageView) dialog.findViewById(R.id.foto_dialog);
                    final String id = new String(intent.getByteArrayExtra("data"));
                    List<User> usuarios = imageAdapter.getPersonas();
                    for (User user : usuarios) {
                        if (user.getUserId().equals(id)) {
                            image.setImageBitmap(user.getImage());
                        }
                    }

                    Button dialogButton = (Button) dialog.findViewById(R.id.ok_dismiss);
                    // if button is clicked, close the custom dialog
                    dialogButton.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                    new AsyncTask<Void, Void, Void>() {

                        @Override
                        protected Void doInBackground(Void... params) {
                            MeetMePreferences prefs = new MeetMePreferences(getApplicationContext());
                            MeetmeApi mApi = new MeetmeApi();
                            mApi.addConnection(prefs.getUserId(), id);
                            return null;
                        }
                    }.execute();

                    dialog.show();

                    Log.i("Bump Test", "Conected with: " + api.userIDForChannelID(intent.getLongExtra("channelID", 0)));
                    Log.i("Bump Test", "Data: " + new String(intent.getByteArrayExtra("data")));
                } else if (action.equals(BumpAPIIntents.MATCHED)) {
                    long channelID = intent.getLongExtra("proposedChannelID", 0);
                    Log.i("Bump Test", "Matched with: " + api.userIDForChannelID(channelID));
                    api.confirm(channelID, true);
                    Log.i("Bump Test", "Confirm sent");
                } else if (action.equals(BumpAPIIntents.CHANNEL_CONFIRMED)) {
                    long channelID = intent.getLongExtra("channelID", 0);
                    Log.i("Bump Test", "Channel confirmed with " + api.userIDForChannelID(channelID));
                    api.send(channelID, prefs.getUserId().getBytes());
                } else if (action.equals(BumpAPIIntents.NOT_MATCHED)) {
                    Log.i("Bump Test", "Not matched.");
                } else if (action.equals(BumpAPIIntents.CONNECTED)) {
                    Log.i("Bump Test", "Connected to Bump...");
                    api.enableBumping();
                }
            } catch (RemoteException e) {
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bindService(new Intent(IBumpAPI.class.getName()), connection, Context.BIND_AUTO_CREATE);
        Log.i("BumpTest", "boot");

        IntentFilter filter = new IntentFilter();
        filter.addAction(BumpAPIIntents.CHANNEL_CONFIRMED);
        filter.addAction(BumpAPIIntents.DATA_RECEIVED);
        filter.addAction(BumpAPIIntents.NOT_MATCHED);
        filter.addAction(BumpAPIIntents.MATCHED);
        filter.addAction(BumpAPIIntents.CONNECTED);
        registerReceiver(receiver, filter);

        gridView = (GridView) findViewById(R.id.gridview);
        spinner = (Spinner) findViewById(R.id.spinner_main);
        adapter = ArrayAdapter.createFromResource(this, R.array.categories_none, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        gridView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                ((User) imageAdapter.getItem(arg2)).getUserId();
                
                if (((User) imageAdapter.getItem(arg2)).getConnected()) {
                    String number = "tel:" + ((User)imageAdapter.getItem(arg2)).getExtraData().toString().trim();
                    Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse(number)); 
                    startActivity(callIntent);
                } else {
                    Toast toast = Toast.makeText(MainActivity.this,"No estais conectados" ,Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        });

        new AsyncTask<Void, Void, Void>() {
            private List<User> users;

            @Override
            protected Void doInBackground(Void... params) {
                timestamp = System.currentTimeMillis();
                MeetmeApiInterface api = new MeetmeApi();
                users = api.getUsers();
                Log.d("Timing", "Time used: " + (System.currentTimeMillis() - timestamp));
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                imageAdapter = new ImageAdapter(users, getApplicationContext());
                gridView.setAdapter(imageAdapter);

                spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                        ((ImageAdapter) gridView.getAdapter()).filterBy(spinner.getItemAtPosition(arg2).toString());
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> arg0) {
                        // TODO Auto-generated method stub
                    }
                });
                super.onPostExecute(result);
            }
        }.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    public void onStart() {
        Log.i("BumpTest", "onStart");
        super.onStart();
    }

    public void onRestart() {
        Log.i("BumpTest", "onRestart");
        super.onRestart();
    }

    public void onResume() {
        Log.i("BumpTest", "onResume");
        super.onResume();
    }

    public void onPause() {
        Log.i("BumpTest", "onPause");
        super.onPause();
    }

    public void onStop() {
        Log.i("BumpTest", "onStop");
        super.onStop();
    }

    public void onDestroy() {
        Log.i("BumpTest", "onDestroy");
        unbindService(connection);
        unregisterReceiver(receiver);
        super.onDestroy();
    }
}

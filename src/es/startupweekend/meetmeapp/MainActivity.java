package es.startupweekend.meetmeapp;

import java.util.List;

import es.startupweekend.adapters.ImageAdapter;
import es.startupweekend.api.MeetmeApi;
import es.startupweekend.api.MeetmeApiInterface;
import es.startupweekend.meetmeapp.R;
import es.startupweekend.model.User;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.Spinner;

public class MainActivity extends Activity {

    private GridView gridView;
    private long timestamp;
    private Spinner spinner;
    private ArrayAdapter<CharSequence> adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gridView = (GridView) findViewById(R.id.gridview);
        spinner = (Spinner) findViewById(R.id.spinner_main);
        adapter = ArrayAdapter.createFromResource(this, R.array.categories_none,
                android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

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
                gridView.setAdapter(new ImageAdapter(users, getApplicationContext()));
                
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
}

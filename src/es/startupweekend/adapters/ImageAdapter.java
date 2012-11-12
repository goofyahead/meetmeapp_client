package es.startupweekend.adapters;

import java.util.LinkedList;
import java.util.List;

import es.startupweekend.api.MeetmeApi;
import es.startupweekend.meetmeapp.R;

import es.startupweekend.model.User;
import es.startupweekened.preferences.MeetMePreferences;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ImageAdapter extends BaseAdapter {
    private List<User> personasOriginal;
    private List<User> filtered = new LinkedList<User>();
    private LayoutInflater layoutInflater;
    private MeetmeApi api;
    MeetMePreferences prefs;

    public List<User> getPersonas() {
        return personasOriginal;
    }

    public ImageAdapter(List<User> personas, Context context) {
        api = new MeetmeApi();
        prefs = new MeetMePreferences(context);
        this.personasOriginal = personas;
        for (User user : personasOriginal) {
            filtered.add(user);
        }
        this.layoutInflater = LayoutInflater.from(context);
    }

    public void filterBy(String filter) {
        filtered.clear();
        if (!filter.equalsIgnoreCase("none")) {
            for (User user : personasOriginal) {
                if (user.getCategory().equalsIgnoreCase(filter)) {
                    filtered.add(user);
                }
            }
        } else {
            for (User user : personasOriginal) {
                filtered.add(user);
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return filtered.size();
    }

    @Override
    public Object getItem(int position) {
        return filtered.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int positionEvent = position;
        View pictureHolder = layoutInflater.inflate(R.layout.image_detail, null);
        ImageView picture = (ImageView) pictureHolder.findViewById(R.id.picture);
        final ImageView contacted = (ImageView) pictureHolder.findViewById(R.id.contacted);
        TextView category = (TextView) pictureHolder.findViewById(R.id.category);
        TextView name = (TextView) pictureHolder.findViewById(R.id.name);

        picture.setImageBitmap(getBitmapFromBase64(filtered.get(position).getImageRaw()));
        category.setText(filtered.get(position).getCategory());
        name.setText(filtered.get(position).getName());
        contacted.setVisibility(View.INVISIBLE);

        new AsyncTask<Void, Void, Void>() {
            private List<String> connections;

            @Override
            protected Void doInBackground(Void... params) {
                String id = prefs.getUserId();
                connections = api.getConnections(prefs.getUserId());
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                filtered.get(positionEvent).setConnected(false);
                for (String idu : connections) {
                    if (filtered.get(positionEvent).getUserId().equals(idu)) {
                        filtered.get(positionEvent).setConnected(true);
                    }
                }

                if (filtered.get(positionEvent).getConnected() == true) {
                    contacted.setVisibility(View.VISIBLE);
                }
                super.onPostExecute(result);
            }

        }.execute();
        return pictureHolder;
    }

    public Bitmap getBitmapFromBase64(String inputUrl) {
        byte[] decodedByte = Base64.decode(inputUrl, 0);
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }
}

package es.startupweekend.adapters;

import java.util.List;

import com.example.meetmeapp.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ImageAdapter extends BaseAdapter{
    private List <String> personas;
    private Context context;
    private LayoutInflater layoutInflater;

    public ImageAdapter (List <String> personas, Context context) {
        this.personas = personas;
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return personas.size();
    }

    @Override
    public Object getItem(int position) {
        return personas.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View pictureHolder = layoutInflater.inflate(R.layout.image_detail, null);
        ImageView picture = (ImageView) pictureHolder.findViewById(R.id.picture);
        ImageView contacted = (ImageView) pictureHolder.findViewById(R.id.contacted);
        TextView category = (TextView) pictureHolder.findViewById(R.id.category);
        TextView name = (TextView) pictureHolder.findViewById(R.id.name);
        return pictureHolder;
    }
}

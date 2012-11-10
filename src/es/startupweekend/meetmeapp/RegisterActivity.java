package es.startupweekend.meetmeapp;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.example.meetmeapp.R;

import es.startupweekened.preferences.MeetMePreferences;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;

public class RegisterActivity extends Activity {

    private static final String JPEG_FILE_PREFIX = "pre";
    private static final String JPEG_FILE_SUFFIX = "jpg";
    private static final String TAG = RegisterActivity.class.getName();
    protected static final int ACTION_CODE = 1;
    private ImageView takePicture;
    private Button register;
    private File f;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        takePicture = (ImageView) findViewById(R.id.imageView1);
        register = (Button) findViewById(R.id.register_button);
        Spinner spinner = (Spinner) findViewById(R.id.spinner1);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.categories,
                android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        takePicture.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    f = createImageFile();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                startActivityForResult(takePictureIntent, ACTION_CODE);
            }
        });

        register.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                Bitmap bm = BitmapFactory.decodeFile(f.getAbsolutePath());
                Bitmap scaled = Bitmap.createScaledBitmap(bm, 400, 400, true);
                ByteArrayOutputStream output = new ByteArrayOutputStream();
                scaled.compress(Bitmap.CompressFormat.JPEG, 100, output);
                byte[] bytes = output.toByteArray();
                String base64Image = Base64.encodeToString(bytes, Base64.NO_WRAP);

                Log.d(TAG, "Image is: " + base64Image);

                // call with name, type, phone, image, etc
                MeetMePreferences mPreferences = new MeetMePreferences(getApplicationContext());
                mPreferences.setUserRegistered(true);
                startActivity(new Intent(RegisterActivity.this, MainActivity.class));
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Display display = getWindowManager().getDefaultDisplay();
        int width = display.getWidth();
        int height = display.getHeight();
        Bitmap bm = BitmapFactory.decodeFile(f.getAbsolutePath());
        Bitmap scaled = Bitmap.createScaledBitmap(bm, width, height / 3, false);
        takePicture.setImageBitmap(scaled);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_register, menu);
        return true;
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = JPEG_FILE_PREFIX + timeStamp + "_";
        File image = File.createTempFile(imageFileName, JPEG_FILE_SUFFIX, getAlbumDir());
        return image;
    }

    private File getAlbumDir() {
        File storageDir = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            if (storageDir != null) {
                if (!storageDir.mkdirs()) {
                    if (!storageDir.exists()) {
                        Log.d(TAG, "failed to create directory CameraSample");
                        return null;
                    }
                }
            }
        } else {
            Log.v(getString(R.string.app_name), "External storage is not mounted READ/WRITE.");
        }
        return storageDir;
    }

}

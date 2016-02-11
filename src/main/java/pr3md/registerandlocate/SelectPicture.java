package pr3md.registerandlocate;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.os.Environment;
import android.view.View;
import android.content.Intent;
import android.widget.ImageView;
import android.widget.TextView;
import android.net.Uri;
import android.database.Cursor;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.io.File;


public class SelectPicture extends AppCompatActivity {
    ImageView userImage;
    int TAKE_PHOTO_CODE = 0;
    int PICK_IMAGE_REQUEST = 1;
    Uri pickedImage;
    String filePath1;
    String username;
    private String mCameraFileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_picture);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        username = getIntent().getExtras().getString("user");
        userImage = (ImageView) findViewById(R.id.profilePic);
        TextView welcomeMsg = (TextView)findViewById(R.id.textView4);
        welcomeMsg.setText("Hello, "+username+" Please select an option");

    }

    public void CapturePicture(View v) {
        Date date = new Date();
        DateFormat df = new SimpleDateFormat("-mm-ss");

        String newPicFile = "IMG-"+ df.format(date) + ".jpg";
        filePath1 = Environment.getExternalStorageDirectory() + "/"+ newPicFile;
        File outFile = new File(filePath1);

        Uri outputFileUri = Uri.fromFile( outFile );



        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
        startActivityForResult(cameraIntent, TAKE_PHOTO_CODE);
    }

    public void SelectFromGallery(View v) {

        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(i, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TAKE_PHOTO_CODE && resultCode == RESULT_OK) {

            Bitmap photo = (Bitmap) data.getExtras().get("data");
            Log.d("stored", filePath1);
            userImage.setImageBitmap(photo);

        }
        else if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            // Let's read picked image data - its URI
            pickedImage = data.getData();
            // Let's read picked image path using content resolver
            String[] filePath = { MediaStore.Images.Media.DATA };

            Cursor cursor = getContentResolver().query(pickedImage, filePath, null, null, null);
            cursor.moveToFirst();
            String imagePath = cursor.getString(cursor.getColumnIndex(filePath[0]));
            filePath1 = imagePath;
            // Now we need to set the GUI ImageView data with data read from the picked file.
            userImage.setImageBitmap(BitmapFactory.decodeFile(imagePath));

            Log.d("pick", filePath1);
            // At the end remember to close the cursor or you will end with the RuntimeException!
            cursor.close();


        } else {
            //No activityResponse
        }

    }

    public void continueToMaps(View v) {
        Intent redirect = new Intent(SelectPicture.this, UserLocation.class);
        String filePathTemp = filePath1;

        redirect.putExtra("fileURL", filePathTemp);
        startActivity(redirect);
    }


}
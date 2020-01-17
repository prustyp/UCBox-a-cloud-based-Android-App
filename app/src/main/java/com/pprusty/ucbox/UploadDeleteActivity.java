package com.pprusty.ucbox;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.pprusty.ucbox.metadatamanager.CloudStates;
import com.pprusty.ucbox.metadatamanager.MetadataManager;
import com.pprusty.ucbox.uploadmanager.UploadManager;

import java.io.File;

/**
 * The class for activity_upload_delete
 */
public class UploadDeleteActivity extends AppCompatActivity {

    private TextView filePathDisplay;
    private Button uploadButton;

    private Button backButton;
    private Button deleteButton;
    private ImageView imageUploadDelete;
    private Animation animation;


    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_delete);

        assert getSupportActionBar() != null;   //null check
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);   //show back button

        //get the file path
        Intent intent = getIntent();
        final String filePath = intent.getStringExtra("filePath");

        this.filePathDisplay = findViewById(R.id.filePathView);
        this.filePathDisplay.setText(filePath);

        //Set animations for the image
        this.imageUploadDelete=findViewById(R.id.imageView);

        animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.animation_fade_in_out);
        imageUploadDelete.startAnimation(animation);

        //Set upload button on click listener
        this.uploadButton = findViewById(R.id.udActivityUploadButton);
        this.uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callUploadManagerToUpload(filePathDisplay.getText().toString());

            }
        });
        //set delete button onClick Listener
        this.deleteButton = findViewById(R.id.udActivityDeleteButton);
        this.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MetadataManager metadataManager = new MetadataManager(view.getContext());
                CloudStates cloudStates = metadataManager.getCloudStatus(filePath);
                if(cloudStates == CloudStates.UPLOADED || cloudStates == CloudStates.DOWNLOADED) {
                    File file = new File(filePath);
                    file.delete();
                    Toast.makeText(view.getContext(), "File has been deleted", Toast.LENGTH_SHORT).show();
                    metadataManager.setCloudStatus(CloudStates.DELETED, filePath);

                } else {
                    Toast toast = Toast.makeText(view.getContext(), "Not allowed", Toast.LENGTH_SHORT);
                    TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                    v.setTextColor(Color.RED);
                    toast.show();
                }

            }
        });


    }

    /**
     * Back button
     * @return
     */
    @Override
    public boolean onSupportNavigateUp(){
        Intent homeActivityIntent = new Intent(this, HomeActivity.class);
        finish();
        startActivity(homeActivityIntent);
        finish();
        return true;
    }


    /**
     * Method for calling upload manager to upload the files
     * @param filePath
     */
    public void callUploadManagerToUpload(String filePath) {
        UploadManager uploadManager = new UploadManager(filePath, this);
        uploadManager.uploadFile();


    }

}

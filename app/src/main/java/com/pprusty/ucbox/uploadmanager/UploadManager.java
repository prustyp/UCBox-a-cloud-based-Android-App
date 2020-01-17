package com.pprusty.ucbox.uploadmanager;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.widget.TextView;
import android.widget.Toast;

import com.pprusty.ucbox.metadatamanager.CloudStates;
import com.pprusty.ucbox.metadatamanager.MetadataManager;

import java.io.File;

/**
 * Manages the upload of file and does the following activities
 * 1. Checks for the status of the files before uploading them to avoid exceptions
 * 2. Initiates upload via BlobWriter
 * 3. Sets the status of the file to Uploading.
 */
public class UploadManager {

    private String filePath;
    private Context androidCurrentContext;

    /**
     * Parameterized constructor
     * @param filePath The path of the file to be uploaded
     * @param androidCurrentContext The context of the activity calling for upload of a file.
     */
    public UploadManager(String filePath, Context androidCurrentContext)  {

        this.filePath = filePath;
        this.androidCurrentContext = androidCurrentContext;
    }

    /**
     * Initiates the upload of a file to cloud
     */
    public void uploadFile() {

        MetadataManager metadataManager = new MetadataManager(this.androidCurrentContext);
        //Get the status of the file from the sqlite db
        CloudStates cloudStates = metadataManager.getCloudStatus(this.filePath);
        //If the status is not NOT_FOUND, means that the file in already present in cloud
        if(cloudStates != CloudStates.NOT_FOUND) {
            Toast toast = Toast.makeText(androidCurrentContext, "File already in Cloud", Toast.LENGTH_SHORT);
            TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
            v.setTextColor(Color.RED);
            toast.show();
            return;
        }
        //Get the blob name from the metadata manager by putting a new row in the sqlite db
        String blobName = metadataManager.putNewEntry(this.filePath, (new File(this.filePath)).length());
        //Creates blob writer object
        BlobWriter blobWriter = new BlobWriter(this.filePath, blobName, androidCurrentContext);
        //Sets the status of the file to uploading
        metadataManager.setCloudStatus(CloudStates.UPLOADING, this.filePath);
        //Starts the upload
        blobWriter.execute();
        Toast.makeText(androidCurrentContext, "Starting Upload", Toast.LENGTH_SHORT).show();
        if(blobWriter.getStatus() == AsyncTask.Status.FINISHED) {
            Toast.makeText(androidCurrentContext, "Finished Upload", Toast.LENGTH_SHORT).show();
        }
    }

}

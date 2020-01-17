package com.pprusty.ucbox.uploadmanager;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.blob.BlobContainerPermissions;
import com.microsoft.azure.storage.blob.BlobContainerPublicAccessType;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;
import com.pprusty.ucbox.AzureStorageConstants;
import com.pprusty.ucbox.metadatamanager.CloudStates;
import com.pprusty.ucbox.metadatamanager.MetadataManager;

import java.io.File;
import java.io.FileInputStream;

/**
 * This is an AsyncTask class which is responsible for asynchronously uploading the file
 * to cloud.
 */
public class BlobWriter extends AsyncTask<String, Void, Void> {


    private String filePath;
    private Context currentAndroidContext;
    private String blobName;
    @Override
    protected Void doInBackground(String... strings) {
        try {
            File fileObject = new File(this.filePath);
            //URL
            CloudStorageAccount account = CloudStorageAccount.parse(AzureStorageConstants.STORAGE_CONNECTION_STRING);

            // Create a blob service client
            CloudBlobClient blobClient = account.createCloudBlobClient();

            // Get a reference to a container
            // The container name must be lower case
            CloudBlobContainer container = blobClient.getContainerReference(AzureStorageConstants.CONTAINER_NAME);

            // Create the container if it does not exist
            container.createIfNotExists();

            // Make the container public
            // Create a permissions object
            BlobContainerPermissions containerPermissions = new BlobContainerPermissions();

            // Include public access in the permissions object
            containerPermissions.setPublicAccess(BlobContainerPublicAccessType.CONTAINER);

            // Set the permissions on the container
            container.uploadPermissions(containerPermissions);

            // Get a reference to a blob in the container
            CloudBlockBlob blob1 = container.getBlockBlobReference(this.blobName);
            FileInputStream fis = new FileInputStream(fileObject);

            // Upload the file to blob in cloud
            blob1.upload(fis, fileObject.length());


        } catch (final Exception e) {
            Log.i("Info", e.getMessage());
        }
        return null;
    }

    /**
     * Parameterized constructor
     * @param filePath The absolute path of the file to be uploaded
     * @param blobName The name of the blob where the file will be store in cloud
     * @param currentAndroidContext The context of the activity doing the upload
     */
    public BlobWriter(String filePath, String blobName, Context currentAndroidContext) {
        this.filePath = filePath;
        this.currentAndroidContext = currentAndroidContext;
        this.blobName = blobName;

    }

    /**
     * On post execution of the app
     * Set the file status to UPLOADED
     * Display a message to user that file has been uploaded
     */
    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        MetadataManager metadataManager = new MetadataManager(currentAndroidContext);
        metadataManager.setCloudStatus(CloudStates.UPLOADED, this.filePath);
        Toast.makeText(this.currentAndroidContext, "Finished Upload", Toast.LENGTH_SHORT).show();

    }
}

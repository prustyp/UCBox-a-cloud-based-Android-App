package com.pprusty.ucbox.downloadmanager;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;
import com.pprusty.ucbox.AzureStorageConstants;
import com.pprusty.ucbox.metadatamanager.CloudStates;
import com.pprusty.ucbox.metadatamanager.MetadataManager;

import java.io.File;
import java.io.FileOutputStream;

/**
 * This class extends AsyncTasks and downloads the blob
 * from Azure storage in background
 */
public class BlobDownloader extends AsyncTask<String, Void, Void> {

    private String filePath;
    private Context currentAndroidContext;
    private String blobName;

    /**
     * Parameterized constructor
     * @param filePath  The absolute path where the downloaded blob data will be stored
     * @param currentAndroidContext The context which calls this background task
     * @param blobName The name of the blob that has to be downloaded
     */
    public BlobDownloader(String filePath, Context currentAndroidContext, String blobName) {
        this.filePath = filePath;
        this.currentAndroidContext = currentAndroidContext;
        this.blobName = blobName;
    }


    /**
     * Do in background method
     * @param strings
     * @return
     */
    @Override
    protected Void doInBackground(String... strings) {

        try {
            //Create the file object for the given file path
            File fileObject = new File(this.filePath);

            //Created cloud storage account object from the connection string
            CloudStorageAccount account = CloudStorageAccount.parse(AzureStorageConstants.STORAGE_CONNECTION_STRING);

            // Create a blob service client
            CloudBlobClient blobClient = account.createCloudBlobClient();

            // Get a reference to a container
            // The container name must be lower case
            // Append a random UUID to the end of the container name so that
            // this sample can be run more than once in quick succession.
            CloudBlobContainer container = blobClient.getContainerReference(AzureStorageConstants.CONTAINER_NAME);


            // Get a reference to a blob in the container
            CloudBlockBlob blob1 = container
                    .getBlockBlobReference(this.blobName);


            FileOutputStream fos = new FileOutputStream(fileObject);
            //Download the blob data from cloud to output stream
            blob1.download(fos);
            //Flush the stream to file
            fos.flush();
            fos.close();


        } catch (Exception ex) {
            Log.i("Info", ex.getMessage());
        }
        return null;
    }

    /**
     * On Post Execution
     * Update the metadata status to downloaded
     * Show a message to the user.
     * @param aVoid
     */
    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        MetadataManager metadataManager = new MetadataManager(currentAndroidContext);
        //Set the metadata status of the file to Downloaded
        metadataManager.setCloudStatus(CloudStates.DOWNLOADED, this.filePath);
        //Show a message to the user.
        Toast.makeText(this.currentAndroidContext, "Finished Download", Toast.LENGTH_SHORT).show();
    }
}

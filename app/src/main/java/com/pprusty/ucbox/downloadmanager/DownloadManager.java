package com.pprusty.ucbox.downloadmanager;

import android.content.Context;
import android.widget.Toast;

import com.pprusty.ucbox.metadatamanager.CloudStates;
import com.pprusty.ucbox.metadatamanager.CloudStorageEntry;
import com.pprusty.ucbox.metadatamanager.MetadataManager;

/**
 * Manages the downloads and does the following activities
 * 1. Checks for the status of the files before downloading them to avoid exceptions
 * 2. Initiates downloads via BlobDownloader
 * 3. Sets the status of the file to downloading.
 */
public class DownloadManager {
    private String filePath;
    private Context parentContext;

    /**
     * Parameterized constructor
     * @param filePath The absolute path of the file that user wants to download
     * @param parentContext The context of the calling activity.
     */
    public DownloadManager(String filePath, Context parentContext) {
        this.filePath = filePath;
        this.parentContext = parentContext;
    }

    /**
     * Method which initiates the download
     */
    public void download() {
        MetadataManager metadataManager = new MetadataManager(this.parentContext);
        //Gets the sqlite row for file path
        CloudStorageEntry cloudStorageEntry = metadataManager.getCloudStorageEntryForFile(this.filePath);
        if(cloudStorageEntry != null) {
            //If the status of file is downloaded, then it does not downloads it again.
            if(cloudStorageEntry.cloudStatus == CloudStates.DOWNLOADED) {
                Toast.makeText(this.parentContext, "File has been downloaded" , Toast.LENGTH_SHORT).show();
                return;
            } else if(cloudStorageEntry.cloudStatus == CloudStates.DOWNLOADING) {
                //If the status is downloading then displays a message to user that it is being downloaded
                Toast.makeText(this.parentContext, "File is being downloaded" , Toast.LENGTH_SHORT).show();
            } else {
                //Initiates the download of the file and set the status to Downloading.
                metadataManager.setCloudStatus(CloudStates.DOWNLOADING, this.filePath);
                BlobDownloader blobDownloader = new BlobDownloader(this.filePath, this.parentContext, cloudStorageEntry.blobName);
                //Displays a message to user that file has been downloading
                Toast.makeText(this.parentContext, "Starting Download", Toast.LENGTH_SHORT).show();
                blobDownloader.execute();
            }

        } else {
            //If no such entry exist in the sqlite db then the file is not present in the cloud.
            Toast.makeText(this.parentContext, "File not present in Cloud" , Toast.LENGTH_SHORT).show();
            return;
        }

    }
}

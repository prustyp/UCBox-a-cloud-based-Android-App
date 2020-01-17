package com.pprusty.ucbox.metadatamanager;


/**
 * This is a Data Class for inserting and fetching rows from Sqlite DB
 * It has 4 fields and matches with columns in the sqlite table "File_Status"
 * in the DataManager class.
 */
public class CloudStorageEntry {

    //Full path of the file in device
    public String filePath;

    //Name of the blob in cloud
    public String blobName;

    //Size of the file
    public long size;

    //State of the file
    public CloudStates cloudStatus;

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getBlobName() {
        return blobName;
    }

    public void setBlobName(String blobName) {
        this.blobName = blobName;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public CloudStates getCloudStatus() {
        return cloudStatus;
    }

    public void setCloudStatus(CloudStates cloudStatus) {
        this.cloudStatus = cloudStatus;
    }


}

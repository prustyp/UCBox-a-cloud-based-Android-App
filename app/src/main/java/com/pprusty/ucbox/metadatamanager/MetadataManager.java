package com.pprusty.ucbox.metadatamanager;

import android.content.Context;
import android.util.Log;

import com.pprusty.ucbox.DataManager;

import java.io.File;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.List;

/**
 * This class manages the metadata for the file by using
 * the sqlite db via DataManager
 */
public class MetadataManager {
    private Context parentContext;

    /**
     * The Parameterized constructor for the clss
     * @param parentContext
     */
    public MetadataManager(Context parentContext) {
        //Used for toast
        this.parentContext = parentContext;
    }

    /**
     * This method returns the status of the file for
     * a given file path
     * @param filePath The full path of the file.
     * @return The State of the file in the Sqlite Database
     */
    public CloudStates getCloudStatus(String filePath) {
        DataManager dataManager = new DataManager(this.parentContext);
        return dataManager.getCloudState(filePath);
    }

    /**
     * This method sets the state of the file in the Sqlite database
     * @param cloudStates The state to which the file state will be set
     * @param filePath The path of the file for which the state will be set
     */
    public void setCloudStatus(CloudStates cloudStates, String filePath) {
        DataManager dataManager = new DataManager(this.parentContext);
        dataManager.setCloudStatus(cloudStates, filePath);
    }

    /**
     * Gets all the rows from the db
     * @return A list of the data objects from the Sqlite db
     */
    public List<CloudStorageEntry> getEntireListOfFilesInCloud() {
        DataManager dataManager = new DataManager(this.parentContext);
        return dataManager.getEntireList();

    }

    /**
     * This methods get the record from the Sqlite db for a particular file
     * @param filePath The path of the file for which the record needs to be fetched=
     * @return The data object for the fetched record for the file.
     */
    public CloudStorageEntry getCloudStorageEntryForFile(String filePath) {
        DataManager dataManager = new DataManager(this.parentContext);
        return dataManager.getCloudStorageEntry(filePath);
    }

    /**
     * This method generates the md5 hash of the given string
     * @param input
     * @return
     */
    private static String getMd5Hash(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            BigInteger number = new BigInteger(1, messageDigest);
            String md5 = number.toString(16);

            while (md5.length() < 32)
                md5 = "0" + md5;

            return md5;
        } catch (Exception e) {
            Log.e("MD5", e.getLocalizedMessage());
            return null;
        }
    }

    /**
     * This method puts a new entry in the sqlite db and return the blob name
     * @param filePath The full path of the file to be inserted
     * @param size The size of the file.
     * @return Returns the blobname = fileName + md5Hash(filePath)
     */
    public String putNewEntry(String filePath, long size) {
        DataManager dataManager = new DataManager(this.parentContext);
        String blobName = "";
        try {
            CloudStorageEntry cloudStorageEntry = new CloudStorageEntry();
            cloudStorageEntry.filePath = filePath;
            blobName =  (new File(cloudStorageEntry.filePath)).getName() +
                    "_" + getMd5Hash(filePath);
            cloudStorageEntry.blobName = blobName;
            cloudStorageEntry.cloudStatus = CloudStates.NOT_FOUND;
            cloudStorageEntry.size = size;
            dataManager.putCloudStorageEntry(cloudStorageEntry);
        }
        catch (Exception ex) {

        }
        return blobName;
    }

    /**
     * Clears the entries in the sqlite db
     */
    public void clearTable() {

        DataManager dataManager = new DataManager(this.parentContext);
        dataManager.cleanDatabase();
    }


}

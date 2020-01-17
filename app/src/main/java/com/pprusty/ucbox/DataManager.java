package com.pprusty.ucbox;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.pprusty.ucbox.metadatamanager.CloudStates;
import com.pprusty.ucbox.metadatamanager.CloudStorageEntry;

import java.util.ArrayList;
import java.util.List;

/**
 * Datamanager class for the project
 * Handles all sqlite operations
 */
public class DataManager extends SQLiteOpenHelper {
    //name of the db
    private static final String DATABASE_NAME = "ucbox-metadata.db";
    //Name of the table
    private static final String FILE_STATUS_DB = "File_Status";
    //Name of the columns
    private static final String FILE_PATH_DEVICE = "device_file_path";
    private static final String BLOB_PATH_CLOUD = "blob_path_cloud";
    private static final String CLOUD_STATUS = "cloud_status";
    private static final String SIZE = "size";


    /**
     * Comnstructor for the data manager
     * @param context
     */
    public DataManager(Context context) {
        super(context, DATABASE_NAME , null, 1);
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String createTableCommand = "CREATE TABLE " + FILE_STATUS_DB +
                " (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                FILE_PATH_DEVICE +
                " TEXT, " +
                BLOB_PATH_CLOUD +
                " TEXT, " +
                SIZE +
                " TEXT, " +
                CLOUD_STATUS +
                " TEXT)";
        sqLiteDatabase.execSQL(createTableCommand);

    }

    /**
     * Gets the state of the file in cloud
     * @param fileName The name of the file for which the state need to be queried
     * @return The state of the file in the db
     */
    public CloudStates getCloudState(String fileName) {
        SQLiteDatabase db = this.getReadableDatabase();
        String queryString = "select * from " + FILE_STATUS_DB + " where " + FILE_PATH_DEVICE + " =" +
                " '" + fileName + "'";
        Cursor res =  db.rawQuery( queryString, null );
        //If there is no such entry in the sqlite then return the state as not_found
        if(res==null || res.getCount() <= 0) {
            return CloudStates.NOT_FOUND;
        }
        res.moveToFirst();
        db.close();
        return Enum.valueOf(CloudStates.class, res.getString(res.getColumnIndex(CLOUD_STATUS)));
    }

    /**
     * This methods get the record from the Sqlite db for a particular file
     * @param filePath The path of the file for which the record needs to be fetched=
     * @return The data object for the fetched record for the file.
     */
    public CloudStorageEntry getCloudStorageEntry(String filePath) {
        SQLiteDatabase db = this.getReadableDatabase();
        String queryString = "select * from " + FILE_STATUS_DB + " where " + FILE_PATH_DEVICE + " =" +
                " '" + filePath + "'";
        Cursor res =  db.rawQuery( queryString, null );
        if(res==null || res.getCount() <= 0) {
            return null;
        }
        res.moveToFirst();
        CloudStorageEntry cloudStorageEntry = new CloudStorageEntry();
        cloudStorageEntry.filePath =  res.getString(res.getColumnIndex(FILE_PATH_DEVICE));
        cloudStorageEntry.blobName =  res.getString(res.getColumnIndex(BLOB_PATH_CLOUD));
        cloudStorageEntry.cloudStatus =  Enum.valueOf(CloudStates.class,
                res.getString(res.getColumnIndex(CLOUD_STATUS)));
        cloudStorageEntry.size =  Long.parseLong(res.getString(res.getColumnIndex(SIZE)));
        if(!res.isClosed()) {
            res.close();
        }
        db.close();
        return cloudStorageEntry;
    }

    /**
     * This method sets the state of the file in the Sqlite database
     * @param status The state to which the file state will be set
     * @param filePath The path of the file for which the state will be set
     */
    public void setCloudStatus(CloudStates status, String filePath) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues row  = new ContentValues();
        row.put(CLOUD_STATUS, status.toString());
        db.update(FILE_STATUS_DB, row, FILE_PATH_DEVICE + "='" + filePath+"'", null);
        db.close();

    }

    /**
     * This method puts a new entry in the sqlite db
     * @param cloudStorageEntry
     */
    public void putCloudStorageEntry(CloudStorageEntry cloudStorageEntry) {

        String query="insert into "+FILE_STATUS_DB+ "( "+FILE_PATH_DEVICE+","+BLOB_PATH_CLOUD+","
                +CLOUD_STATUS+","+ SIZE+") values "+"( '"+cloudStorageEntry.filePath+"','"+cloudStorageEntry.blobName+
                "','"+cloudStorageEntry.cloudStatus.toString()+"','"+Long.toString(cloudStorageEntry.size)+"')";
        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL(query);
        db.close();
    }


    /**
     * Gets all the rows from the db
     * @return A list of the data objects from the Sqlite db
     */
    public List<CloudStorageEntry> getEntireList() {
        SQLiteDatabase db = this.getReadableDatabase();
        String queryString = "select * from " + FILE_STATUS_DB;

        Cursor res =  db.rawQuery( queryString, null );
        if(res==null || res.getCount() <= 0) {
            return new ArrayList<CloudStorageEntry>();
        }
        res.moveToFirst();
        List<CloudStorageEntry> cloudStorageEntryList = new ArrayList<>();
        try {
            while (!res.isAfterLast()) {
                CloudStorageEntry cloudStorageEntry = new CloudStorageEntry();
                cloudStorageEntry.filePath =  res.getString(res.getColumnIndex(FILE_PATH_DEVICE));
                cloudStorageEntry.blobName =  res.getString(res.getColumnIndex(BLOB_PATH_CLOUD));
                cloudStorageEntry.cloudStatus =  Enum.valueOf(CloudStates.class,
                        res.getString(res.getColumnIndex(CLOUD_STATUS)));
                cloudStorageEntry.size =  Long.parseLong(res.getString(res.getColumnIndex(SIZE)));
                cloudStorageEntryList.add(cloudStorageEntry);
                res.moveToNext();
            }
        } finally {
            res.close();
        }
        db.close();
        return cloudStorageEntryList;
    }


    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }


    /**
     * Clears all the records from the database
     * To be used by developer
     */
    public void cleanDatabase() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(FILE_STATUS_DB, null, null);
        db.close();
    }
}

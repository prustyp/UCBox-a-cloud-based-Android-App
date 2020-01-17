package com.pprusty.ucbox;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.pprusty.ucbox.metadatamanager.CloudStates;
import com.pprusty.ucbox.metadatamanager.CloudStorageEntry;
import com.pprusty.ucbox.metadatamanager.MetadataManager;
import java.text.DecimalFormat;
import java.util.List;

/**
 * This is the activity for Account Info
 */
public class AccountInfoActivity extends AppCompatActivity {
    private TextView filesUploaded;
    private TextView filesDownloaded;
    private TextView storageUsed;
    private Button buttonResetDb;
    private TextView approxCost;
    private List<CloudStorageEntry> cloudStorageEntryList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_info);

        //back button on action bar
        assert getSupportActionBar() != null;   //null check
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);   //show back button

        this.filesUploaded=findViewById(R.id.textViewNumberOfUpload);
        this.filesDownloaded=findViewById(R.id.textViewNumberOfDownload);
        this.storageUsed=findViewById(R.id.textViewFilePathLabel);
        this.approxCost=findViewById(R.id.textViewApproxCost);

        //get list of metadata entries(From SQlite)
        MetadataManager metadataManager = new MetadataManager(this);
        cloudStorageEntryList =  metadataManager.getEntireListOfFilesInCloud();

        //calculate numberOfUploadedFiles, numberOfDownloadedFiles, total_storage_used and approximate_cost
        int numberOfUploadedFiles=0;
        int numberOfDownloadedFiles=0;
        long totalStorage=0;
        String storageSize="";
        String approximateCost="";
        for(int i=0 ;i<cloudStorageEntryList.size(); i++)
        {

            if(cloudStorageEntryList.get(i).cloudStatus != CloudStates.UPLOADING && cloudStorageEntryList.get(i).cloudStatus != CloudStates.NOT_FOUND )
            {
                numberOfUploadedFiles++;

            }
            if(cloudStorageEntryList.get(i).cloudStatus== CloudStates.DOWNLOADED)
            {
                numberOfDownloadedFiles++;

            }
            totalStorage=totalStorage+cloudStorageEntryList.get(i).getSize();
            storageSize=formatFileSize(totalStorage);
            approximateCost=approximateCost(totalStorage);


        }
        filesUploaded.setText("" + numberOfUploadedFiles);
        filesDownloaded.setText("" + numberOfDownloadedFiles);
        storageUsed.setText(storageSize);
        approxCost.setText(approximateCost);

        //Clean all data from SQlite Db
        this.buttonResetDb = findViewById(R.id.buttonResestDb);
        this.buttonResetDb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteDataFromDB(view);
            }
        });

    }

    /**
     * back to home screen
     * @return
     */
    @Override
    public boolean onSupportNavigateUp(){
        Intent homeIntent = new Intent(this, HomeActivity.class);
        finish();
        startActivity(homeIntent);
        finish();
        return true;
    }

    /**
     * File size string formatting
     * @param size
     * @return
     */
    public String formatFileSize(long size) {
        String hrSize = null;

        double b = size;
        double k = size/1024.0;
        double m = ((size/1024.0)/1024.0);
        double g = (((size/1024.0)/1024.0)/1024.0);
        double t = ((((size/1024.0)/1024.0)/1024.0)/1024.0);

        DecimalFormat dec = new DecimalFormat("0.00");

        if ( t>1 ) {
            hrSize = dec.format(t).concat(" TB");
        } else if ( g>1 ) {
            hrSize = dec.format(g).concat(" GB");
        } else if ( m>1 ) {
            hrSize = dec.format(m).concat(" MB");
        } else if ( k>1 ) {
            hrSize = dec.format(k).concat(" KB");
        } else {
            hrSize = dec.format(b).concat(" Bytes");
        }

        return hrSize;
    }


    /**
     * delete data from db
     * @param view
     */
    public void deleteDataFromDB (View view){
        MetadataManager metadataManager = new MetadataManager(this);
        metadataManager.clearTable();
    }

    //cost calculation of azure blob storage

    /**
     * cost calculation of azure blob storage
     * @param size total size stored in cloud
     * @return The cost per month
     */
    public String approximateCost(long size){
        String cost=null;
        double g = (((size/1024.0)/1024.0)/1024.0);
        double price=g*0.01;
        DecimalFormat decimalFormat = new DecimalFormat("###.#######");
        cost=decimalFormat.format(price);
        //cost=Double.toString(price);
        return cost;

    }
}

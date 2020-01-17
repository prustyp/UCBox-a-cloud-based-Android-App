package com.pprusty.ucbox;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pprusty.ucbox.metadatamanager.CloudStorageEntry;
import com.pprusty.ucbox.metadatamanager.MetadataManager;
import java.util.List;

/**
 * CloudListActivity class, responsible for listing the files
 * present in cloud and also initiating downloads
 */
public class CloudListActivity extends AppCompatActivity {

    private String selectedFilePath;
    private List<CloudStorageEntry> cloudStorageEntryList;
    private RecyclerView recyclerView;
    private CloudAdapter cloudAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cloud_list);

        //back button
        assert getSupportActionBar() != null;   //null check
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);   //show back button

        //get entries from SQlite
        MetadataManager metadataManager = new MetadataManager(this);
        cloudStorageEntryList =  metadataManager.getEntireListOfFilesInCloud();

        //link recycler view
        recyclerView = findViewById(R.id.cloudListRecyclerView);
        cloudAdapter = new CloudAdapter(this, cloudStorageEntryList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(cloudAdapter);

    }


    /**
     * back button
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
     * download dialog
     * @param pointer
     */
    public void showDialogForDownload (int pointer) {
        DialogDownload dialog = new DialogDownload(this.cloudStorageEntryList.get(pointer).filePath,this);
        dialog.show(getSupportFragmentManager(), "123");
    }
}

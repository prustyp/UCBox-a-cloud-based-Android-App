package com.pprusty.ucbox;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pprusty.ucbox.metadatamanager.CloudStorageEntry;

import java.io.File;
import java.util.List;

/**
 * This class provide a binding from list_item to views that are displayed within a RecyclerView
 */
public class CloudAdapter extends RecyclerView.Adapter<CloudAdapter.ListItemHolder> {

    private CloudListActivity cloudListActivity;
    private List<CloudStorageEntry> cloudStorageEntries;

    /**
     * Constructor for the adapter class
     * @param cloudListActivity
     * @param cloudStorageEntries
     */
    public CloudAdapter(CloudListActivity cloudListActivity, List<CloudStorageEntry> cloudStorageEntries) {
        this.cloudListActivity = cloudListActivity;
        this.cloudStorageEntries = cloudStorageEntries;
    }

    /**
     * Overriden method for onCreateViewHolder
     * @param parent
     * @param viewType
     * @return
     */
    @NonNull
    @Override
    public CloudAdapter.ListItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View listItem = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);

        return new ListItemHolder(listItem);
    }

    /**
     * Overriden method for onBindViewHolder
     * Sets the holder's view data from the data got from sqlite db based on postiion
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull CloudAdapter.ListItemHolder holder, int position) {
        CloudStorageEntry cloudStorageEntry = cloudStorageEntries.get(position);
        holder.textViewFileName.setText((new File(cloudStorageEntry.getFilePath())).getName());
        holder.textViewFilePath.setText(cloudStorageEntry.getFilePath());
        AccountInfoActivity accountInfoActivity=new AccountInfoActivity();
        String fileSize=accountInfoActivity.formatFileSize(cloudStorageEntry.getSize());
        holder.textViewFileSize.setText(fileSize);
        holder.textViewFileState.setText(cloudStorageEntry.cloudStatus.toString());
    }

    /**
     * Gets the number of count for the adapter
     * @return
     */
    @Override
    public int getItemCount() {
        return cloudStorageEntries.size();
    }

    /**
     * Inner class for ItemHolder
     */
    public class ListItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView textViewFileName;
        private TextView textViewFilePath;
        private TextView textViewFileSize;
        private TextView textViewFileState;

        public ListItemHolder(View view) {
            super(view);

            textViewFileName = view.findViewById(R.id.textViewFileName);
            textViewFilePath = view.findViewById(R.id.textViewFilePath);
            textViewFileSize = view.findViewById(R.id.textViewFileSize);
            textViewFileState = view.findViewById(R.id.textViewFileState);
            view.setClickable(true);
            view.setOnClickListener(this);
        }


        /**
         * On click on a item pops up a dialog box
         * @param view
         */
        @Override
        public void onClick(View view) {
            cloudListActivity.showDialogForDownload(getAdapterPosition());

        }

    }
}


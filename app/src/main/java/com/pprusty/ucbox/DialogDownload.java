package com.pprusty.ucbox;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import com.pprusty.ucbox.downloadmanager.DownloadManager;

/**
 * This class shows the dialog
 */
public class DialogDownload extends DialogFragment {
    private String filePath;
    private Context parentContext;

    public DialogDownload(String filePath, Context parentContext) {
        this.filePath = filePath;
        this.parentContext = parentContext;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class because this dialog
        // has a simple UI
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());


        // Dialog will have "Make a selection" as the title
        builder.setMessage("Do you want to Download this file")
                // An OK button that does nothing
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        DownloadManager downloadManager = new DownloadManager(filePath,parentContext);
                        downloadManager.download();
                        // Nothing happening here
                    } })
                // A "Cancel" button that does nothing
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Nothing happening here either
                    } });


        // Create the object and return it
        return builder.create();
    }// End of onCreateDialog


}




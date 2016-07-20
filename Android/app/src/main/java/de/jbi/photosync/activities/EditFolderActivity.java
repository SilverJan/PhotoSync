package de.jbi.photosync.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import java.util.UUID;

import de.jbi.photosync.R;
import de.jbi.photosync.content.DataContentHandler;
import de.jbi.photosync.content.SharedPreferencesUtil;
import de.jbi.photosync.domain.Folder;
import de.jbi.photosync.utils.Constants;
import de.jbi.photosync.utils.Logger;

public class EditFolderActivity extends AppCompatActivity {
    private TextView headerTV;
    private Switch activeFolderSwitch;
    private EditText changeFolderNameET;
    private EditText changeFolderDirET;
    private Button submitButton;

    private Folder currentFolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_folder);

        Intent receivedIntent = getIntent();
        currentFolder = DataContentHandler.getInstance().findFolderById(UUID.fromString(receivedIntent.getStringExtra(Constants.PASS_FOLDER_INTENT)));

        // ####################
        // ### SET UI STUFF ###
        // ####################

        headerTV = (TextView) findViewById(R.id.activity_edit_folder_header_text_view);
        activeFolderSwitch = (Switch) findViewById(R.id.activity_edit_folder_active_folder_switch);
        changeFolderNameET = (EditText) findViewById(R.id.activity_edit_folder_change_folder_name_edit_text);
        changeFolderDirET = (EditText) findViewById(R.id.activity_edit_folder_change_folder_dir_edit_text);
        submitButton = (Button) findViewById(R.id.activity_edit_folder_submit_button);

        changeFolderDirET.setText(currentFolder.getAbsolutePath().getPath());
        changeFolderNameET.setText(currentFolder.getName());
        headerTV.setText(currentFolder.getName());
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validateForm()) {
                    return;
                }
                passChangesToCurrentFolder();
            }
        });
    }

    private Boolean validateForm() {
        boolean valid = true;
        String folderNameText = changeFolderNameET.getText().toString();
        if (folderNameText.isEmpty()) {
            changeFolderNameET.setError("New name must have more than one character");
            valid = false;
        }
        return valid;
    }

    private void passChangesToCurrentFolder() {
        currentFolder.setName(changeFolderNameET.getText().toString());
        currentFolder.setSelected(activeFolderSwitch.isSelected());
        SharedPreferencesUtil.updateFolder(currentFolder);
        Logger.showLogToast("Folder edited successfully");
        finish();
    }
}

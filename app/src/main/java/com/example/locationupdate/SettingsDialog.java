package com.example.locationupdate;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatDialogFragment;

public class SettingsDialog extends AppCompatDialogFragment {
    private EditText editTextDistance;
    private SettingsDialogListener listener;
    Context context;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.settingdialog, null);

        builder.setView(view)
                .setTitle("Settings")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(editTextDistance.getText().toString().trim().equals(""))
                        {
                            editTextDistance.setError( "First name is required!" );
                        }
                        else {
                            String username = editTextDistance.getText().toString();
                            //String password = editTextPassword.getText().toString();
                            listener.applyTexts(username);
                        }
                    }
                });

        editTextDistance = view.findViewById(R.id.edit_distance);
        /*try
        {

            ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            String value = appInfo.metaData.getString("SilentDistance");
            editTextDistance.setText(value);

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }*/
        //editTextPassword = view.findViewById(R.id.edit_password);

        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (SettingsDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    "must implement ExampleDialogListener");
        }
    }

    public interface SettingsDialogListener {
        void applyTexts(String Distance);
    }
}

package com.eii.fip.foodanddrink;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class ConfigActivity extends Activity {
    // region Methode Android
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);
        LinkInterface();
        CreateInterface();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.config, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //endregion
    //region Declaration
    TextView editMessageFaim;
    TextView editMessageSoif;
    TextView editTypeSearchFaim;
    TextView editTypeSearchSoif;
    TextView editradius;

    Button btnSave;
    Button btnCancel;


    PreferenceManageur  pPrefrenceManager;

    //endregion
    //region Methode de base
    ///------------------------------------------------------------------------------------------\\\
    /// Rôle :  Link les interfaceXML                                                            \\\
    ///------------------------------------------------------------------------------------------\\\
    private void LinkInterface()
    {
        editMessageFaim = (TextView)findViewById(R.id.editMessageFaim);
        editMessageSoif= (TextView)findViewById(R.id.editMessageSoif);
        editTypeSearchFaim= (TextView)findViewById(R.id.editTypeSearchFaim);
        editTypeSearchSoif= (TextView)findViewById(R.id.editTypeSearchSoif);
        editradius = (TextView)findViewById(R.id.editRadius);
        editradius.setInputType(InputType.TYPE_CLASS_NUMBER);

        btnSave = (Button)findViewById(R.id.btnSave);
        btnCancel = (Button)findViewById(R.id.btnCancel);
    }
    ///------------------------------------------------------------------------------------------\\\
    /// Rôle :  Cree l'interface                                                                 \\\
    ///------------------------------------------------------------------------------------------\\\
    public void CreateInterface() {

        pPrefrenceManager = PreferenceManageur.getInstance();
        loadFormPref();

        btnSave.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View view) {
                pPrefrenceManager.setMessageFaim(editMessageFaim.getText().toString());
                pPrefrenceManager.setMessageSoif(editMessageSoif.getText().toString());
                pPrefrenceManager.TypeSearchFaim = (editTypeSearchFaim.getText().toString());
                pPrefrenceManager.TypeSearchSoif= (editTypeSearchSoif.getText().toString());
                pPrefrenceManager.setRadius(Integer.valueOf(editradius.getText().toString()));
                pPrefrenceManager.saveSettings();
                finish();
            }
        });
        btnCancel.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View view) {
                finish();
            }
        });


    }

    private void loadFormPref()
    {
        pPrefrenceManager.LoadSettings();
        editMessageFaim.setText(pPrefrenceManager.getMessageFaim());
        editMessageSoif.setText(pPrefrenceManager.getMessageSoif());
       editradius.setText(String.valueOf(pPrefrenceManager.getRadius()));
        editTypeSearchFaim.setText(pPrefrenceManager.TypeSearchFaim);
        editTypeSearchSoif.setText(pPrefrenceManager.TypeSearchSoif);
    }

    //endregion
}

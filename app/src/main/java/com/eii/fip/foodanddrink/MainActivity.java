
package com.eii.fip.foodanddrink;

import java.io.File;
import java.io.FileOutputStream;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Message;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.app.AlertDialog;
import android.widget.TextView;
import android.provider.Contacts;

import java.io.FileOutputStream;
import java.io.IOException;
import android.net.Uri;
import java.security.Provider;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.prefs.Preferences;

public class MainActivity extends Activity {


    //region Declarations
    //Declaration des objets graphiques
    private Button btn_Faim;
    private Button btn_Soif;
    private Button btn_test1;


    ///Declaration du Preference Manager
    private PreferenceManageur pPrefrenceManager;
    //endregion

    //region Routine Android
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Initialisation du DataContainer
        pPrefrenceManager = PreferenceManageur.getInstance();
        pPrefrenceManager.setSetting(getSharedPreferences(pPrefrenceManager.PREFS_NAME, 0));
        pPrefrenceManager.setDeviceIsPhone(IsDeviceSmartphone());
        pPrefrenceManager.LoadDataContainer();
        //Liaison de l'interface
        LinkInterface();
        //Creation de l'interface
        CreateInterface();


   }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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

    //region Fonction de base


    ///------------------------------------------------------------------------------------------\\\
    /// Rôle :  Link les interfaceXML                                                            \\\
    ///------------------------------------------------------------------------------------------\\\
    private void LinkInterface()
    {
        btn_Faim = (Button)findViewById(R.id.Btn_Faim);
        btn_Soif= (Button)findViewById(R.id.Btn_Soif);
        btn_test1 = (Button)findViewById(R.id.TestButton1);

    }
    ///------------------------------------------------------------------------------------------\\\
    /// Rôle :  Cree l'interface                                                                 \\\
    ///------------------------------------------------------------------------------------------\\\
    public void CreateInterface()
    {
       // DataContainer.getInstance().CustomListChecked = ;
        btn_Faim.setText("J'ai Faim");
        btn_Soif.setText("J'ai Soif");
        btn_Faim.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View view) {BtnFaim_OnClick(view);}
        });
        btn_Soif.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View view) {
                BtnSoif_OnClick(view);
            }
        });
        btn_test1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, FoundResultActivity.class);
                startActivity(intent);
            }
        });
        btn_test1.setVisibility(View.VISIBLE);


    }
//endregion

    //region ActionUI
    ///------------------------------------------------------------------------------------------\\\
    /// Rôle :  Action de BoutonFaim Click                                                       \\\
    ///------------------------------------------------------------------------------------------\\\
    public void BtnFaim_OnClick(View v)
    {

        pPrefrenceManager.MessageToSend = pPrefrenceManager.getMessageFaim();
        Intent intent = new Intent(MainActivity.this, SendSmsActivity.class);
        startActivity(intent);
    }
    ///------------------------------------------------------------------------------------------\\\
    /// Rôle :  Action de BoutonSoifClick                                                       \\\
    ///------------------------------------------------------------------------------------------\\\
    public void BtnSoif_OnClick(View v)
    {

            pPrefrenceManager.MessageToSend = pPrefrenceManager.getMessageSoif();
            Intent intent = new Intent(MainActivity.this, SendSmsActivity.class);
            startActivity(intent);

    }
    //endregion

    //region Methode Diverse
    private boolean IsDeviceSmartphone()
    {
        TelephonyManager manager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        if(manager.getPhoneType() == TelephonyManager.PHONE_TYPE_NONE){
            return false;
        }else{
            return true;
        }

    }

    //endregion

}
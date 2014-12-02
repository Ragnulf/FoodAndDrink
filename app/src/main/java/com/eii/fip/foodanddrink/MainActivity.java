
package com.eii.fip.foodanddrink;

import java.io.File;
import java.io.FileOutputStream;
import android.app.Activity;
import android.content.ClipData;
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
    private Button btn_Config;
    private TextView txtView_Status;



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
        pPrefrenceManager.LoadSettings();
        //Liaison de l'interface
        LinkInterface();
        //Creation de l'interface
        CreateInterface();
        //lance la recup de la position
        GetGpsPosition();


   }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

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
        btn_Config = (Button)findViewById(R.id.Btn_Config);
        txtView_Status = (TextView)findViewById(R.id.textView_Status);
        pPrefrenceManager.MainContext = (Context)this;
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

        btn_Config.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ConfigActivity.class);
                startActivity(intent);
            }

            });

    }
//endregion

    //region ActionUI
    ///------------------------------------------------------------------------------------------\\\
    /// Rôle :  Action de BoutonFaim Click                                                       \\\
    ///------------------------------------------------------------------------------------------\\\
    public void BtnFaim_OnClick(View v)
    {pPrefrenceManager.setType(pPrefrenceManager.TypeSearchFaim);

        pPrefrenceManager.MessageToSend = pPrefrenceManager.getMessageFaim();
        Intent intent = new Intent(MainActivity.this, FoundResultActivity.class);
        startActivity(intent);
    }
    ///------------------------------------------------------------------------------------------\\\
    /// Rôle :  Action de BoutonSoifClick                                                       \\\
    ///------------------------------------------------------------------------------------------\\\
    public void BtnSoif_OnClick(View v)
    {
            pPrefrenceManager.setType(pPrefrenceManager.TypeSearchSoif);
            pPrefrenceManager.MessageToSend = pPrefrenceManager.getMessageSoif();
            Intent intent = new Intent(MainActivity.this, FoundResultActivity.class);
            startActivity(intent);

    }
    //endregion

    //region Recupération position GPS
    ///------------------------------------------------------------------------------------------\\\
    /// Rôle :  Recupere les coordonnées gps                                                     \\\
    ///------------------------------------------------------------------------------------------\\\
    public void GetGpsPosition() {
        LocationManager locationManager;
        String svcName = Context.LOCATION_SERVICE;
        locationManager = (LocationManager) getSystemService(svcName);

        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        ;
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setSpeedRequired(false);
        criteria.setCostAllowed(true);


        String provider1 = locationManager.getBestProvider(criteria, true);
        Location l = locationManager.getLastKnownLocation(provider1);

        updateWithNewLocation(l);

        locationManager.requestLocationUpdates(provider1, 1000, 10, locationListener);


    }
    ///------------------------------------------------------------------------------------------\\\
    /// Rôle :  Update de la position                                                            \\\
    ///------------------------------------------------------------------------------------------\\\
    private void updateWithNewLocation(Location l) {
        String latLongString = "Aucun Emplacement trouvé";

        if (l != null) {
            double lat = l.getLatitude();
            double lng = l.getLongitude();
            double alt = l.getAltitude();
            pPrefrenceManager.setLatitude(String.valueOf(lat));
            pPrefrenceManager.setLongitude(String.valueOf(lng));
            AsyncTextViewChange.setTextInView(txtView_Status,"Position GPS trouvé");
            btn_Faim.setEnabled(true);
            btn_Soif.setEnabled(true);
        }
        else
        {
            btn_Faim.setEnabled(false);
            btn_Soif.setEnabled(false);
            AsyncTextViewChange.setTextInView(txtView_Status,"En Attente de récuperation de votre position");

        }
    }
    ///------------------------------------------------------------------------------------------\\\
    /// Rôle :  Evenement d'ecoute de cangement de position                                      \\\
    ///------------------------------------------------------------------------------------------\\\
    private final LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            updateWithNewLocation(location);
        }

        public void onProviderDisabled(String provider) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int Status, Bundle extras) {
        }
    };

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
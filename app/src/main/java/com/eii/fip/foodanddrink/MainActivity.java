
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

    //Declaration des objets graphiques
    private Button btn_Faim;
    private Button btn_Soif;
    private Button btn_test1;
    private TextView txt_GpsLocation;
    public static final String PREFS_NAME = "DataContainerPref";
    private boolean bIsPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DataContainer.getInstance().setSetting( getSharedPreferences(PREFS_NAME, 0));
        bIsPhone=IsDeviceSmartphone();
        LinkInterface();
        CreateInterface();
        GetGpsPosition();
        DataContainer.getInstance().LoadDataContainer();
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

    private void LinkInterface()
    {
        btn_Faim = (Button)findViewById(R.id.Btn_Faim);
        btn_Soif= (Button)findViewById(R.id.Btn_Soif);
        txt_GpsLocation = (TextView)findViewById(R.id.Txt_Local_GPS);
        btn_test1 = (Button)findViewById(R.id.TestButton1);

    }

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
                DataContainer.getInstance().LoadDataContainer();
            }
        });


    }
    //Récuperation de la position GPS
    public void GetGpsPosition()
    {
        LocationManager locationManager;
        String svcName = Context.LOCATION_SERVICE;
        locationManager = (LocationManager)getSystemService(svcName);

        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setPowerRequirement(Criteria.POWER_LOW);;
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setSpeedRequired(false);
        criteria.setCostAllowed(true);


        String provider1 = locationManager.getBestProvider(criteria, true);
        Location l = locationManager.getLastKnownLocation(provider1);

        updateWithNewLocation(l);

        locationManager.requestLocationUpdates(provider1, 1000, 10, locationListener);



    }
    private final LocationListener locationListener = new LocationListener()
    {
        public void onLocationChanged(Location location) {
            updateWithNewLocation(location);
        }
        public void onProviderDisabled(String provider) {}
        public void onProviderEnabled(String provider) {}
        public void onStatusChanged(String provider, int Status, Bundle extras) {}
    };
    private void updateWithNewLocation(Location l)
    {
        String latLongString = "Aucun Emplacement trouvé";

        if(l != null)
        {
            double lat = l.getLatitude();
            double lng = l.getLongitude();
            double alt = l.getAltitude();

            latLongString = "Latitude:" + lat + "\nLongitude:" +lng + "\nAltitude:" + alt;



        }
        txt_GpsLocation.setText( latLongString);

    }
    public void BtnFaim_OnClick(View v)
    {
        if(bIsPhone)
        {
        DataContainer.getInstance().MessageToSend = "J'ai Faim!!  Message envoyé via FoodandDrink";
        Intent intent = new Intent(MainActivity.this, SendSmsActivity.class);
        startActivity(intent);
        }
        else
        {
            ShowMessageBox("Fonction désactivé sur Tablette");
        }
    }
    public void BtnSoif_OnClick(View v)
    {
        if(bIsPhone)
        {
            DataContainer.getInstance().MessageToSend = "J'ai Soif!!  Message envoyé via FoodandDrink";
            Intent intent = new Intent(MainActivity.this, SendSmsActivity.class);
            startActivity(intent);
        }
        else
        {
            ShowMessageBox("Fonction désactivé sur Tablette");
        }

    }
    public void ShowMessageBox(String Msg)
    {
        AlertDialog alertDialog;
        alertDialog = new AlertDialog.Builder((Context)this).create();
        alertDialog.setTitle("Info");
        alertDialog.setMessage(Msg);
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.show();

    }

    private boolean IsDeviceSmartphone()
    {
        TelephonyManager manager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        if(manager.getPhoneType() == TelephonyManager.PHONE_TYPE_NONE){
            return false;
        }else{
            return true;
        }

    }

}
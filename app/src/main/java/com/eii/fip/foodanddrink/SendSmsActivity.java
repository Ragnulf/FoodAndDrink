package com.eii.fip.foodanddrink;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.List;


public class SendSmsActivity extends Activity {

    private Button BtnChange;
    private Button BtnSendSMS;
    private Intent intentContactList=null;
    private ListView ContactList;
    public List<String> CustomList;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_sms);
        LinkInterface();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.send_sms, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
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
        intentContactList = new Intent(SendSmsActivity.this, ContactListActivity.class);

        ContactList = (ListView)findViewById(R.id.listView);
        ContactList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ContactList.setItemChecked(i,ContactList.isItemChecked(i));
                 if(!DataContainer.getInstance().CustomListChecked.contains(ContactList.getItemAtPosition(i)))
                    {
                        if(ContactList.isItemChecked(i))
                        {
                            DataContainer.getInstance().CustomListChecked.add(ContactList.getItemAtPosition(i).toString());
                        }
                    }
                else
                    {
                        if(!ContactList.isItemChecked(i))
                        {
                        DataContainer.getInstance().CustomListChecked.remove(ContactList.getItemAtPosition(i).toString());
                        }
                }
                DataContainer.getInstance().SaveDataContainer();
            }
        });



        BtnChange = (Button)findViewById(R.id.Btn_change_list);
        BtnChange.setOnClickListener(new View.OnClickListener() {
                                         @Override
                                         public void onClick(View view) {
                                             startActivity(intentContactList);
                                         }
                                     });
        BtnSendSMS = (Button)findViewById(R.id.btn_send_sms);
        BtnSendSMS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(DataContainer.getInstance().bDeviceIsPhone) {
                    int temp = DataContainer.getInstance().CustomListChecked.size();
                    for (int i = 0; i < temp; i++) {
                        sendSms(DataContainer.getInstance().MessageToSend, DataContainer.getInstance().CustomListChecked.get(i));
                    }
                    if (temp > 1)
                        ShowMessageBox("Messages envoyés");
                    else if (temp == 1)
                        ShowMessageBox("Message envoyé");
                    else ;
                }
                else
                {
                    ShowMessageBox("Fonction désactivé sur Tablette");
                }
            }
        });





    }
    @Override
    protected void onResume()
    {
        super.onResume();
        if(DataContainer.getInstance().CustomList!=null) {
            CustomList = DataContainer.getInstance().CustomList;
            final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_checked, android.R.id.text1, CustomList);
            ContactList.setAdapter(adapter);
        }
        if(DataContainer.getInstance().CustomListChecked!=null)
        {
            for(int i=0;i<CustomList.size();i++)
            {
                if(DataContainer.getInstance().CustomListChecked.contains(CustomList.get(i)))
                {
                    ContactList.setItemChecked(i,true);
                }

            }

        }

    }
    private void sendSms(String msg,String Nom )
    {
        String num="";

        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String[] projection    = new String[] {ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER};

        Cursor people = getContentResolver().query(uri, projection, null, null, null);

        int indexName = people.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
        int indexNumber = people.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

        people.moveToFirst();
        do {
            String name   = people.getString(indexName);
            String number = people.getString(indexNumber);
            if(name.equals(Nom))
                num=number;

        } while (people.moveToNext());
        //Partie qui envoi le SMS
        if(num.length()>= 4 && msg.length() > 0){

            //Grâce à l'objet de gestion de SMS (SmsManager) que l'on récupère grâce à la méthode static getDefault()
            //On envoit le SMS à l'aide de la méthode sendTextMessage
            SmsManager.getDefault().sendTextMessage(num, null, msg, null, null);

        }
        else{

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


}

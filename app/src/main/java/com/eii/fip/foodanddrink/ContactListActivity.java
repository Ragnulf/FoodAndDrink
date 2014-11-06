package com.eii.fip.foodanddrink;

import android.app.Activity;
import android.app.LauncherActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;


public class ContactListActivity extends Activity {

    //region Declarations

    private ListView listContact;
    private Button btnAnnul;
    private Button btnSave;

    private PreferenceManageur pPreferenceManager = PreferenceManageur.getInstance();

    //private List<String> CustomList;


    //endregion

    //region Routine Android


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);
        LinkInterface();
        getContacts();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.contact_list, menu);
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
    @Override
    protected void onStart()
    {
        super.onStart();
        LoadCheckedList();
    }

    //endregion

    //region Fonction de base
    private void LinkInterface()
    {
        listContact = (ListView) findViewById(R.id.listViewAllContact);
        listContact.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                listContact.setItemChecked(i, listContact.isItemChecked(i));
            }
        });
        btnAnnul = (Button) findViewById(R.id.Btn_Back);
        btnAnnul.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnSave = (Button) findViewById(R.id.Btn_SaveList);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SaveCustomList();
                pPreferenceManager.SaveDataContainer();
                finish();
            }
        });
    }

    //endregion

    private void getContacts()
    {

        List<String> strlist = new ArrayList<String>();
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_checked, android.R.id.text1, strlist);
        listContact.setAdapter(adapter);


        //Recuperation de la liste des contacts
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String[] projection = new String[]{ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER};

        Cursor people = getContentResolver().query(uri, projection, null, null, null);

        int indexName = people.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
        int indexNumber = people.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

        people.moveToFirst();
        do {
            String name = people.getString(indexName);
            String number = people.getString(indexNumber);
            strlist.add(name);


        } while (people.moveToNext());

        Collections.sort(strlist);


    }
    private void LoadCheckedList()
    {
        int nbr = listContact.getAdapter().getCount();

        for (int i = 0; i < nbr; i++) {
            if (pPreferenceManager.CustomContactList != null) {
                String temp2 = listContact.getItemAtPosition(i).toString();
                if (pPreferenceManager.CustomContactList.contains(temp2)) {
                    listContact.setItemChecked(i, true);
                }
            }
        }

    }
    private void SaveCustomList()
    {
        pPreferenceManager.CustomContactList.clear();
        for (int i = 0; i < listContact.getCount(); i++) {
            if (listContact.isItemChecked(i)) {
                pPreferenceManager.CustomContactList.add(listContact.getItemAtPosition(i).toString());
            }
        }
    }



}

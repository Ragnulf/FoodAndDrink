package com.eii.fip.foodanddrink;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.telephony.TelephonyManager;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created by jonathan on 16/10/14.
 */
public class DataContainer {

    private static String GoogleAPIKEY = "AIzaSyA5FW8wnYDr_D4ddJyVDcWnLAGhfx3ykP8";



    private DataContainer(){}
    private static DataContainer INSTANCE = new DataContainer();
    private SharedPreferences settings;
    public static DataContainer getInstance()
    {
        return INSTANCE;
    }
    public List<String> CustomList =new ArrayList<String>();;
    public List<String> CustomListChecked = new ArrayList<String>();
    public String MessageToSend = "";
    public boolean bDeviceIsPhone = false;


    public void setSetting(SharedPreferences set)
{
    settings = set;
}
    public String  getCustomListString()
    {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < CustomList.size(); i++) {
            sb.append(CustomList.get(i)).append(";");
        }
        return sb.toString();
    }
    public void  setCustomList(String str)
    {
        String[] list = str.split(";");
        CustomList.clear();
        for (int i=0;i<list.length;i++)
        {
            CustomList.add(list[i]);
        }
        Collections.sort(CustomList);
    }
    public String  getCustomListCheckedString()
    {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < CustomListChecked.size(); i++) {
            sb.append(CustomListChecked.get(i)).append(";");
        }
        return sb.toString();
    }
    public void  setCustomListChecked(String str)
    {
        String[] list = str.split(";");
        CustomListChecked.clear();
        for (int i=0;i<list.length;i++)
        {
            CustomListChecked.add(list[i]);
        }
        Collections.sort(CustomListChecked);
    }
    public static final String PREFS_NAME = "DataContainerPref";
    public void   LoadDataContainer()
    {
        String str = settings.getString("CustomList","");
        DataContainer.getInstance().setCustomList(str);
        str = settings.getString("CustomListChecked","");
        DataContainer.getInstance().setCustomListChecked(str);
    }
    public void SaveDataContainer()
    {
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("CustomList", DataContainer.getInstance().getCustomListString());
        editor.putString("CustomListChecked",DataContainer.getInstance().getCustomListCheckedString());
        editor.commit();
    }






}

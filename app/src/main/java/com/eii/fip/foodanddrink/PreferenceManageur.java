package com.eii.fip.foodanddrink;

import android.content.SharedPreferences;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Created by jonathan on 04/11/14.
 * S'occuper de gerer l'acces au preference
 */
public class PreferenceManageur {

    //region Declarations

    //Chaine statique
    public static final String PREFS_NAME = "FoodAndDrinkPrefence";
    public static String GoogleAPIKEY = "AIzaSyA5FW8wnYDr_D4ddJyVDcWnLAGhfx3ykP8";
    //Declaration de l'instance unique
    private static PreferenceManageur INSTANCE = new PreferenceManageur();
    //Liste des contacts
    public List<String> CustomContactList =new ArrayList<String>();
    public List<String> CustomContactListChecked = new ArrayList<String>();
    //Propriétés
    public String MessageToSend = "";
    private String MessageFaim="J'ai Faim!!  Message envoyé via FoodandDrink";
    private String MessageSoif="J'ai Soif!!  Message envoyé via FoodandDrink";
    private boolean bDeviceIsPhone = false;


    //Declaration des preferences
    private SharedPreferences settings;


    //endregion

    //region Load/Save Preference
    public void   LoadDataContainer()
    {
        String str = settings.getString("CustomList","");
        setCustomContactList(str);
        str = settings.getString("CustomListChecked","");
        setCustomContactListChecked(str);
    }
    public void SaveDataContainer()
    {
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("CustomList", getCustomContactListString());
        editor.putString("CustomListChecked",getCustomContactListCheckedString());
        editor.apply();
    }
    //endregion

    //region Getter/Setter de la custom contact liste
    public String  getCustomContactListString()
    {


        StringBuilder sb = new StringBuilder();
        for(String item:CustomContactList )
        {
            sb.append(item).append(";");
        }
        return sb.toString();
    }
    public void  setCustomContactList(String str)
    {
        String[] list = str.split(";");
        CustomContactList.clear();
        for(String item:list)
        {
            CustomContactList.add(item);
        }
        Collections.sort(CustomContactList);
    }
    public String  getCustomContactListCheckedString()
    {
        StringBuilder sb = new StringBuilder();
        for(String item:CustomContactListChecked)
        {
            sb.append(item).append(";");

        }
        return sb.toString();
    }
    public void  setCustomContactListChecked(String str)
    {
        String[] list = str.split(";");
        CustomContactListChecked.clear();
        for (int i=0;i<list.length;i++)
        {
            CustomContactListChecked.add(list[i]);
        }
        Collections.sort(CustomContactListChecked);
    }
    //endregion

    //region Getter/Setter
    public String getMessageFaim(){return MessageFaim;}
    public String getMessageSoif(){return MessageSoif;}
    public boolean getDeviceIsPhone(){return bDeviceIsPhone;}



    public void setMessageFaim(String _Msg){MessageFaim=_Msg;}
    public void setMessageSoif(String _Msg){MessageSoif=_Msg;}
    public void setDeviceIsPhone(boolean _bool){bDeviceIsPhone=_bool;}
    //endregion



    //region Methodes_de_construction
    public static PreferenceManageur getInstance()
    {
        return INSTANCE;
    }
    private PreferenceManageur(){}
    public void setSetting(SharedPreferences set)
    {
        settings = set;
    }
    //endregion
}

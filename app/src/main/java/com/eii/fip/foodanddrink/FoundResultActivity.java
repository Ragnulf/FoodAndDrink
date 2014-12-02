package com.eii.fip.foodanddrink;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;

import java.util.Arrays;
import java.util.Comparator;

import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;


public class FoundResultActivity extends Activity {

    //region Declarations

    ///------------------------------------------------------------------------------------------\\\
    /// Données membres                                                                          \\\
    ///------------------------------------------------------------------------------------------\\\
    private ListView listViewResult;
    private String provider;
    private ArrayList<String> placeName ;

    ///Declaration du Preference Manager
    private PreferenceManageur pPrefrenceManager;
    ArrayAdapter<String> adapterListResult;
    ArrayList<Place> ListOfPlaces;

    //endregion

    //region Routine Android
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_found_result);
        pPrefrenceManager = PreferenceManageur.getInstance();
        LinkInterface();
        ExecuteRequest Request=new ExecuteRequest();
        Request.execute(buildRequest());
        CreateInterface();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.found_result, menu);
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
    /// Rôle: Link l'interface                                                                   \\\
    ///------------------------------------------------------------------------------------------\\\
    private void LinkInterface() {

        listViewResult = (ListView) findViewById(R.id.listViewResult);
        placeName = new ArrayList<String>();
        listViewResult = (ListView) findViewById(R.id.listViewResult);

        adapterListResult = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, placeName);
        listViewResult.setAdapter(adapterListResult);

    }
    ///------------------------------------------------------------------------------------------\\\
    /// Rôle :  Cree l'interface                                                                 \\\
    ///------------------------------------------------------------------------------------------\\\
    public void CreateInterface(){
        listViewResult.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                if(l>=0)
                {
                    pPrefrenceManager.AdresseToSend = ListOfPlaces.get(i).getVicinity();
                    pPrefrenceManager.NameTosend = ListOfPlaces.get(i).getName();


                    pPrefrenceManager.MessageToSend += " "+pPrefrenceManager.NameTosend+" \n "+pPrefrenceManager.AdresseToSend;

                    pPrefrenceManager.ActivityParent= FoundResultActivity.this;

                    Intent intent = new Intent(FoundResultActivity.this, SendSmsActivity.class);

                        startActivity(intent);

                }
            }
        });

    }

//endregion

    //region Recuperation des points d'interet

    ///------------------------------------------------------------------------------------------\\\
    /// Rôle :  Construit la requête                                                             \\\
    ///------------------------------------------------------------------------------------------\\\
    private String buildRequest(){
        final StringBuilder query = new StringBuilder();
        query.append("https://maps.googleapis.com/maps/api/place/nearbysearch/xml?");
        query.append("location=" + pPrefrenceManager.getLatitude() + "," + pPrefrenceManager.getLongitude() + "&");
        query.append("radius=" + pPrefrenceManager.getRadius() + "&");
        query.append("types=" + pPrefrenceManager.getType() + "&");
        query.append("sensor=true&"); //Must be true if queried from a device with GPS
        query.append("key=" + pPrefrenceManager.GoogleAPIKEY);
        return  query.toString();
    }
    ///------------------------------------------------------------------------------------------\\\
    /// Rôle :  Envoi la requete                                                                 \\\
    ///------------------------------------------------------------------------------------------\\
    private ArrayList<Place> SendRequest(String Request){
        HttpClient httpclient = new DefaultHttpClient();
        ArrayList<Place> PlacesRes = null;
        HttpResponse response;
        String responseString = null;
        try {
            response = httpclient.execute(new HttpGet(Request.toString()));
            StatusLine statusLine = response.getStatusLine();
            if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                response.getEntity().writeTo(out);
                out.close();
                responseString = out.toString();
               PlacesRes= ParseResult(responseString);
            } else {
                //Closes the connection.
                response.getEntity().getContent().close();
                throw new IOException(statusLine.getReasonPhrase());
            }
        } catch (ClientProtocolException e) {
            Log.e("ERROR", e.getMessage());
        } catch (IOException e) {
            Log.e("ERROR", e.getMessage());
        }
            return PlacesRes;
    }
    ///------------------------------------------------------------------------------------------\\\
    /// Rôle :  Parse la reponse                                                                 \\\
    ///------------------------------------------------------------------------------------------\\\
    private ArrayList<Place> ParseResult(String result) {
        ArrayList<Place> places = new ArrayList<Place>();

        try {
            Document xmlResult = loadXMLFromString(result);
            NodeList nodeList = xmlResult.getElementsByTagName("result");
            for (int i = 0, length = nodeList.getLength(); i < length; i++) {
                Node node = nodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element nodeElement = (Element) node;
                    Place place = new Place();
                    Node name = nodeElement.getElementsByTagName("name").item(0);
                    Node vicinity = nodeElement.getElementsByTagName("vicinity").item(0);
                    Node rating = nodeElement.getElementsByTagName("rating").item(0);
                    Node reference = nodeElement.getElementsByTagName("reference").item(0);
                    Node id = nodeElement.getElementsByTagName("id").item(0);
                    Node geometryElement = nodeElement.getElementsByTagName("geometry").item(0);
                    NodeList locationElement = geometryElement.getChildNodes();
                    Element latLngElem = (Element) locationElement.item(1);
                    Node lat = latLngElem.getElementsByTagName("lat").item(0);
                    Node lng = latLngElem.getElementsByTagName("lng").item(0);
                    float[] geometry = {Float.valueOf(lat.getTextContent()),
                            Float.valueOf(lng.getTextContent())};
                    int typeCount = nodeElement.getElementsByTagName("type").getLength();
                    String[] types = new String[typeCount];
                    for (int j = 0; j < typeCount; j++) {
                        types[j] = nodeElement.getElementsByTagName("type").item(j).getTextContent();
                    }
                    place.setVicinity(vicinity.getTextContent());
                    place.setId(id.getTextContent());
                    place.setName(name.getTextContent());
                    if (null == rating) {
                        place.setRating(0.0f);
                    } else {
                        place.setRating(Float.valueOf(rating.getTextContent()));
                    }
                    place.setReference(reference.getTextContent());
                    place.setGeometry(geometry);
                    place.setTypes(types);
                    places.add(place);
                }
            }

        }
            catch(Exception e){
                Log.e("ERROR", e.getMessage());
            }

        return places;
        }
    public static Document loadXMLFromString(String xml) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();

        InputSource is = new InputSource(new StringReader(xml));

        return builder.parse(is);
    }
    ///------------------------------------------------------------------------------------------\\\
    /// Rôle : Tache Asynchrone                                                             \\\
    ///------------------------------------------------------------------------------------------\\\
    private class ExecuteRequest extends AsyncTask<String, Void, ArrayList<Place> >
    {

       @Override
        protected ArrayList<Place> doInBackground(String... strings) {
           return SendRequest(strings[0]);
       }

        @Override
        protected void onPostExecute(ArrayList<Place> places) {
            Collections.sort(places, new Comparator<Place>() {
                public int compare(Place a, Place b) {
                    return (int)(a.GetDistance() - b.GetDistance());

                }
            });
            ListOfPlaces = places;
            placeName.clear();
            for(Place item:places)
            {
                double Distance =  item.GetDistance() ;
                placeName.add(item.getName()+"\n    "+item.getVicinity()+"\n    Distance: "+String.valueOf((int)item.GetDistance())+" m");
            }
            //listViewResult.refreshDrawableState();
            adapterListResult.notifyDataSetChanged();

        }


    }




//endregion

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
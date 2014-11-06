package com.eii.fip.foodanddrink;

import android.app.Activity;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;


public class FoundResultActivity extends Activity {

    public static Document loadXMLFromString(String xml) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();

        InputSource is = new InputSource(new StringReader(xml));

        return builder.parse(is);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_found_result);
        LinkInterface();
        //Reception de la position du GPS
       GetGpsPosition();


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

    ///------------------------------------------------------------------------------------------\\\
    /// Données membres                                                                          \\\
    ///------------------------------------------------------------------------------------------\\\
    private TextView txt_GpsLocation;
    private ListView listView;

    private String latitude;
    private String longitude;
    private String provider;
    private final int radius = 2000;
    private String type = "restaurant";
    private final String APIKEY = "AIzaSyA5FW8wnYDr_D4ddJyVDcWnLAGhfx3ykP8";



    private ArrayList<String> placeName ;



    ///------------------------------------------------------------------------------------------\\\
    /// Rôle: Link l'interface                                                                   \\\
    ///------------------------------------------------------------------------------------------\\\
    private void LinkInterface() {
        txt_GpsLocation = (TextView) findViewById(R.id.Txt_Local_GPS);
        listView = (ListView) findViewById(R.id.listViewResult);
        placeName = new ArrayList<String>();
        listView = (ListView) findViewById(R.id.listViewResult);

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, placeName);
        listView.setAdapter(adapter);

    }

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

    ///------------------------------------------------------------------------------------------\\\
    /// Rôle :  Update de la position                                                            \\\
    ///------------------------------------------------------------------------------------------\\\
    private void updateWithNewLocation(Location l) {
        String latLongString = "Aucun Emplacement trouvé";

        if (l != null) {
            double lat = l.getLatitude();
            double lng = l.getLongitude();
            double alt = l.getAltitude();
            latitude = String.valueOf(lat);
            longitude = String.valueOf(lng);

            latLongString = "Latitude:" + lat + "\nLongitude:" + lng + "\nAltitude:" + alt;


        }
        AsyncTextViewChange.setTextInView(txt_GpsLocation, latLongString);
        getinterest();

    }

    private void getinterest() {
        final StringBuilder query = new StringBuilder();

        query.append("https://maps.googleapis.com/maps/api/place/nearbysearch/xml?");
        query.append("location=" + latitude + "," + longitude + "&");
        query.append("radius=" + radius + "&");
        query.append("types=" + type + "&");
        query.append("sensor=true&"); //Must be true if queried from a device with GPS
        query.append("key=" + APIKEY);







        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpClient httpclient = new DefaultHttpClient();
                HttpResponse response;
                String responseString = null;
                try {
                    response = httpclient.execute(new HttpGet(query.toString()));
                    StatusLine statusLine = response.getStatusLine();
                    if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                        ByteArrayOutputStream out = new ByteArrayOutputStream();
                        response.getEntity().writeTo(out);
                        out.close();
                        responseString = out.toString();
                        ParseResult(responseString);
                    } else {
                        //Closes the connection.
                        response.getEntity().getContent().close();
                        throw new IOException(statusLine.getReasonPhrase());
                    }
                } catch (ClientProtocolException e) {
                    Log.e("ERROR", e.getMessage());
                } catch (IOException e) {
                    Log.e("ERROR", e.getMessage());
                } /*catch (JSONException e) {
                    Log.e("ERROR", e.getMessage());
                }*/
            }
        }).start();

    }

    private void ParseResult(String result) {
        try {

            ArrayList<Place> places = new ArrayList<Place>();

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




            for(Place item:places)
            {
                placeName.add(item.getName());
            }
        }
            catch(Exception e){
                Log.e("ERROR", e.getMessage());
            }


        }



}
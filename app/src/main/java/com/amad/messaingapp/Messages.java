package com.amad.messaingapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.amad.messaingapp.Entities.Message;
import com.amad.messaingapp.Entities.TypeSelect;
import com.amad.messaingapp.Utils.Helper;
import com.amad.messaingapp.Utils.JsonParser;
import com.estimote.coresdk.common.requirements.SystemRequirementsChecker;
import com.estimote.coresdk.observation.region.RegionUtils;
import com.estimote.coresdk.observation.region.beacon.BeaconRegion;
import com.estimote.coresdk.recognition.packets.Beacon;
import com.estimote.coresdk.service.BeaconManager;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Messages extends AppCompatActivity implements MsgAdapter.ItemClickCityCallBack{

    private BeaconManager beaconManager;
    private BeaconRegion region;
    private final OkHttpClient client = new OkHttpClient();
    private RecyclerView recyclerView;
    private MsgAdapter msgAdapter;
    private ArrayList<Message> msg_list;
    final static String MSG_READ = "readMsg";
    final static int MSG_READ_CODE = 00101;
    private String token;
    final static String MSG_NEW = "newMsg";
    final static int MSG_NEW_CODE = 11101;
    private String LAST_CALL  = "";
    private boolean madeCallToCompleteList =false;

    public static long regionEnterTime ;

    private double lastMin = 2.0;
    long lasttime= System.currentTimeMillis() ;

    private static Map<String, List<Double>> VALUES = new HashMap<>();

    private static final Map<String, String> PLACES_BY_BEACONS;

    static {
        Map<String, String> placesByBeacons = new HashMap<>();
        placesByBeacons.put("15212:31506", "Woodward332");
        placesByBeacons.put("1564:34409", "Woodward333F");
        placesByBeacons.put("26535:44799", "BookStand");
        PLACES_BY_BEACONS = Collections.unmodifiableMap(placesByBeacons);


        VALUES.put("31506", new ArrayList<Double>());
        VALUES.put("34409", new ArrayList<Double>());
        VALUES.put("44799", new ArrayList<Double>());
    }

    private String placesNearBeacon(Beacon beacon) {
        String beaconKey = String.format("%d:%d", beacon.getMajor(), beacon.getMinor());
        if (PLACES_BY_BEACONS.containsKey(beaconKey)) {
            return PLACES_BY_BEACONS.get(beaconKey);
        }
        return "";
    }

    private static Map<String, List<Double>> sortByValue(Map<String, List<Double>> unsortMap) {

        // 1. Convert Map to List of Map
        List<Map.Entry<String, List<Double>>> list =
                new LinkedList<Map.Entry<String, List<Double>>>(unsortMap.entrySet());

        // 2. Sort list with Collections.sort(), provide a custom Comparator
        //    Try switch the o1 o2 position for a different order
        Collections.sort(list, new Comparator<Map.Entry<String,List<Double>>>() {
            public int compare(Map.Entry<String, List<Double> > o1,
                               Map.Entry<String, List<Double> > o2) {
                return ((Integer)o2.getValue().size()).compareTo((Integer)o1.getValue().size());
            }
        });

        // 3. Loop the sorted list and put it into a new insertion order Map LinkedHashMap
        Map<String, List<Double>> sortedMap = new LinkedHashMap<String, List<Double>>();
        for (Map.Entry<String, List<Double>> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        /*
        //classic iterator example
        for (Iterator<Map.Entry<String, Integer>> it = list.iterator(); it.hasNext(); ) {
            Map.Entry<String, Integer> entry = it.next();
            sortedMap.put(entry.getKey(), entry.getValue());
        }*/


        return sortedMap;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);
        SetTheAppIcon();
        beaconManager = new BeaconManager(this);
        beaconManager.setRangingListener(new BeaconManager.BeaconRangingListener() {

            @Override
            public void onBeaconsDiscovered(BeaconRegion beaconRegion, List<Beacon> beacons) {
                if (!beacons.isEmpty()  ) {
                    Beacon nearestBeacon = beacons.get(0);
                    String pw ="";
                    for(int i =0; i<beacons.size();i++){

                        double t ;
                        if(beacons.get(i).getMinor() == 31506 && beacons.get(i).getMajor() == 15212)
                        {
                            t =  RegionUtils.computeAccuracy(beacons.get(i));
                            if(VALUES.containsKey("31506"))
                            {
                                List<Double> val = VALUES.get("31506");
                                val.add(t);
                                VALUES.put("31506",val);
                            }
                        }
                        else if((beacons.get(i).getMinor() == 34409) && beacons.get(i).getMajor() == 1564)
                        {

                            t =  RegionUtils.computeAccuracy(beacons.get(i));
                            if(VALUES.containsKey("34409"))
                            {
                                List<Double> val = VALUES.get("34409");
                                val.add(t);
                                VALUES.put("34409",val);
                            }
                        }
                        else if((beacons.get(i).getMinor() == 44799) && beacons.get(i).getMajor() == 26535)
                        {
                            t =  RegionUtils.computeAccuracy(beacons.get(i));
                            if(VALUES.containsKey("44799"))
                            {
                                List<Double> val = VALUES.get("44799");
                                val.add(t);
                                VALUES.put("44799",val);
                            }
                        }
                        else if(beacons.size() == 0)
                        {
                            getAllMessages();
                        }

                        VALUES = sortByValue(VALUES);

//                        if(VALUES.size() == 0)
//                            getAllProducts();
                        printMap(VALUES);


                        //


                        if((beacons.get(i).getMinor() == 31506 )
                                ||(beacons.get(i).getMinor() == 34409)
                                ||(beacons.get(i).getMinor() == 44799)
                                )
                        {
                            t =  RegionUtils.computeAccuracy(beacons.get(i));


                            String place = placesNearBeacon( beacons.get(i));
                            pw = pw + place.toString() + " Distance: " +  t + " / ";

                            nearestBeacon = beacons.get(i);



                            int s = beacons.size();

                            // TODO: update the UI here
                            // if(nearestBeacon.getProximityUUID())

                        }
                        else{
                        }       //
                    }


                    //Log.d("Airport", "Nearest places: " + places);




                }
                else
                {
                    if(!madeCallToCompleteList) {
                        madeCallToCompleteList = true;
                        getAllMessages();
                    }
                }

            }
        });

        region = new BeaconRegion("ranged region", UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D"), null, null);




        getAllMessages();


    }

    private void SetTheAppIcon()
    {
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.ic_launcher);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
    }

    public  String printMap(Map<String, List<Double>> map) {
        String toReturn = "";
        boolean changeList = false;
        String changeFor = "";


        ArrayList<TypeSelect> mList =  new ArrayList<TypeSelect>();

        for (Map.Entry<String, List<Double>> entry : map.entrySet()) {


            toReturn = toReturn + ("Key : " + entry.getKey()
                    + " Value : " + average(entry.getValue()));

            TypeSelect temp = new TypeSelect(average(entry.getValue()),entry.getKey());
            mList.add(temp);

            long time= System.currentTimeMillis();

            if(  time - lasttime > 2000)
            {
                changeList = true;
                lasttime = time;

            }

            if(entry.getValue().size() > 5)
            {
                List<Double> val = VALUES.get(entry.getKey());
                // VALUES.put(entry.getKey(), new ArrayList<Double>());
                VALUES.put("31506", new ArrayList<Double>());
                VALUES.put("34409", new ArrayList<Double>());
                VALUES.put("44799", new ArrayList<Double>());
            }
        }

        Collections.sort(mList, new Comparator<TypeSelect>() {
            @Override
            public int compare(TypeSelect o1, TypeSelect o2) {
                return o1.getAvg().compareTo(o2.getAvg());
            }
        });



        changeFor = mList.get(0).getMinor();
        double dis = mList.get(0).getAvg();



        if(changeList && LAST_CALL != changeFor ) {


             regionEnterTime = System.currentTimeMillis();
            madeCallToCompleteList = false;

            String type = GetType(changeFor);
            // make a call


            if(dis  == 1000)
            {
                getAllMessages();
                LAST_CALL = changeFor;

            } else if(dis < lastMin) {

                lastMin  = dis;
                LAST_CALL = changeFor;

                Log.d("call", "making call");
                Request request = new Request.Builder()
                        .url(Helper.Api_url.MESSAGES_REGION_TYPE.toString() + type)
                        .addHeader("Authorization", "BEARER " + token)
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {

                        final String responseData = response.body().string();
                        Log.d("Response", responseData);

                        //Run view-related code back on the main thread
                        Messages.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                try {

                                    ArrayList<Message> msgs = new ArrayList<Message>();
                                    msgs = JsonParser.JsonParse.Parse(responseData);
                                    Log.d("test", msgs.toString());
                                    msg_list = msgs;




                                    Log.d("test", msg_list.toString());
                                    msgAdapter.clear();
                                    msgAdapter.SetProducts(msg_list);
                                    msgAdapter.notifyDataSetChanged();


                                } catch (Exception e) {
                                    Toast.makeText(Messages.this, "No Messages!"
                                            ,Toast.LENGTH_SHORT).show();
                                    e.printStackTrace();
                                }

                            }
                        });
                    }
                });


            }
            else {
                lastMin = lasttime  + 3;
            }
        }
//        else if( changeList & LAST_CALL != changeFor){
//            LAST_CALL = changeFor;
//            getAllProducts();
//        }



        return toReturn;
    }



    private double average(List<Double> toAvg){

        if(toAvg.size() == 0) return 1000.0;
        double total = 0.0;
        for(int i=0;i<toAvg.size();i++)
        {
            total =  total + toAvg.get(i);
        }

        return  total/toAvg.size();
    }


    public String GetType(String min)
    {
        switch (min)
        {
            case "31506":
                return "Woodward332";
            case "34409":
                return "Woodward333F";
            case "44799":
                return "BooksStand";
            default:
                return "";

        }

    }



    @Override
    protected void onResume() {
        super.onResume();

        SystemRequirementsChecker.checkWithDefaultDialogs(this);



       // beaconManager = new BeaconManager(this);
        beaconManager.setRangingListener(new BeaconManager.BeaconRangingListener() {

            @Override
            public void onBeaconsDiscovered(BeaconRegion beaconRegion, List<Beacon> beacons) {
                if (!beacons.isEmpty()  ) {
                    Beacon nearestBeacon = beacons.get(0);
                    String pw ="";
                    for(int i =0; i<beacons.size();i++){

                        double t ;
                        if(beacons.get(i).getMinor() == 31506 && beacons.get(i).getMajor() == 15212)
                        {
                            t =  RegionUtils.computeAccuracy(beacons.get(i));
                            if(VALUES.containsKey("31506"))
                            {
                                List<Double> val = VALUES.get("31506");
                                val.add(t);
                                VALUES.put("31506",val);
                            }
                        }
                        else if((beacons.get(i).getMinor() == 34409) && beacons.get(i).getMajor() == 1564)
                        {

                            t =  RegionUtils.computeAccuracy(beacons.get(i));
                            if(VALUES.containsKey("34409"))
                            {
                                List<Double> val = VALUES.get("34409");
                                val.add(t);
                                VALUES.put("34409",val);
                            }
                        }
                        else if((beacons.get(i).getMinor() == 44799) && beacons.get(i).getMajor() == 26535)
                        {
                            t =  RegionUtils.computeAccuracy(beacons.get(i));
                            if(VALUES.containsKey("44799"))
                            {
                                List<Double> val = VALUES.get("44799");
                                val.add(t);
                                VALUES.put("44799",val);
                            }
                        }
                        else if(beacons.size() == 0)
                        {
                            getAllMessages();
                        }

                        VALUES = sortByValue(VALUES);

//                        if(VALUES.size() == 0)
//                            getAllProducts();
                        printMap(VALUES);


                        //


                        if((beacons.get(i).getMinor() == 31506 )
                                ||(beacons.get(i).getMinor() == 34409)
                                ||(beacons.get(i).getMinor() == 44799)
                                )
                        {
                            t =  RegionUtils.computeAccuracy(beacons.get(i));


                            String place = placesNearBeacon( beacons.get(i));
                            pw = pw + place.toString() + " Distance: " +  t + " / ";

                            nearestBeacon = beacons.get(i);



                            int s = beacons.size();

                            // TODO: update the UI here
                            // if(nearestBeacon.getProximityUUID())

                        }
                        else{
                        }       //
                    }


                    //Log.d("Airport", "Nearest places: " + places);




                }
                else
                {
                    if(!madeCallToCompleteList) {
                        madeCallToCompleteList = true;
                        getAllMessages();
                    }
                }

            }
        });

        region = new BeaconRegion("ranged region", UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D"), null, null);




        getAllMessages();
        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                beaconManager.startRanging(region);

            }
        });

    }



    private void getAllMessages()
    {
        SharedPreferences mPrefs = getSharedPreferences(MainActivity.STORED,MODE_PRIVATE);
        if(mPrefs.contains("MyAppKey")) {
             token = (String) mPrefs.getString("MyAppKey", null);

            Request request = new Request.Builder()
                    .url(Helper.Api_url.ALL_MESSAGES.toString())
                    .addHeader("Authorization", "BEARER " + token)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {

                    final String responseData = response.body().string();
                    Log.d("Response", responseData);
                    //Run view-related code back on the main thread
                    Messages.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            try {
                                ArrayList<Message> msgs = new ArrayList<Message>();
                                msgs = JsonParser.JsonParse.Parse(responseData);
                                Log.d("test", msgs.toString());
                                msg_list = msgs;



                            recyclerView = (RecyclerView) findViewById(R.id.view_msgs);
                            recyclerView.setLayoutManager(new LinearLayoutManager(Messages.this,LinearLayoutManager.VERTICAL,false));
                            recyclerView.getItemAnimator().setRemoveDuration(200);
                                msgAdapter = new MsgAdapter(Messages.this,R.layout.item_each_msg);
                                msgAdapter.notifyDataSetChanged();
                            recyclerView.setAdapter(msgAdapter);
                                msgAdapter.SetProducts(msgs);


                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    });
                }
            });
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {

            case R.id.logout:


                Log.d("logout","Logging off");
                SharedPreferences mPrefs = getSharedPreferences(MainActivity.STORED,MODE_PRIVATE);
                SharedPreferences.Editor prefsEditor = mPrefs.edit();

                if(mPrefs.contains("MyAppKey"))
                {
                    prefsEditor.remove("MyAppKey");
                    Intent i = new Intent(Messages.this,MainActivity.class);
                    startActivity(i);
                }
                else
                {

                }
                prefsEditor.commit();
                return true;

            case R.id.NewMsg:
                Intent newMsg = new Intent(Messages.this, Compose_Message.class);
                startActivity(newMsg);
                break;
            case R.id.refresh:
                getAllMessages();
                beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
                    @Override
                    public void onServiceReady() {
                        beaconManager.startRanging(region);

                    }
                });
                break;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater myMenu = getMenuInflater();
        myMenu.inflate(R.menu.menu_main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void OnItemClick(int p) {
        Log.d("item Clicked", p+"");
        // make call for read
        Intent editUser = new Intent(Messages.this, ReadMessage.class);
        editUser.putExtra(MSG_READ, msg_list.get(p) );
        startActivityForResult(editUser, MSG_READ_CODE);
    }

    @Override
    public boolean itemUnlock(int p) {

        Request request = new Request.Builder()
                .url(Helper.Api_url.UNLOCK_MESSAGE.toString() + msg_list.get(p).getId())
                .addHeader("Authorization", "BEARER " + token)
                .build();

        final boolean[] toReturn = {false};
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                final String responseData = response.body().string();

                Log.d("Response", responseData);
                if(responseData.equals("{\"status\":\"ok\"}"))
                {
                    toReturn[0] = true ;
                }
                //Run view-related code back on the main thread
                Messages.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        try {
                            if(responseData.equals("{\"status\":\"ok\"}"))
                            {
                                toReturn[0] = true ;
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                });
            }
        });
return toReturn[0];

    }
}

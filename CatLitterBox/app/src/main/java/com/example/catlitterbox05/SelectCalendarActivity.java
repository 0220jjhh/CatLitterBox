package com.example.catlitterbox05;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

public class SelectCalendarActivity extends AppCompatActivity {

    private String TAG = SelectCalendarActivity.class.getSimpleName();

    String day = null;
    String jsonStr;

    private ProgressDialog pDialog;
    private ListView lv02;

    // URL to get contacts JSON
    private static String url = "http://52.78.207.144/application/getLitterData.php";


    protected HttpURLConnection conn;
    ArrayList<HashMap<String, String>> contactList02;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_calendar);

        contactList02 = new ArrayList<>();
        lv02 = (ListView) findViewById(R.id.list02);

        Intent intent = getIntent();
        day = intent.getExtras().getString("date");
        getDbData(url);

        //start();

        Button Bdate = (Button) findViewById(R.id.date);
        Bdate.setOnClickListener(new View.OnClickListener () {
            public void onClick(View v) {
                Intent intent = new Intent(SelectCalendarActivity.this, CalendarActivity.class);
                startActivity(intent);
            }
        });
        //new SelectCalendarActivity.GetContacts().execute();
    }

/*
    Uri.Builder builder = new Uri.Builder()
            .appendQueryParameter(day, toString());
    String postParams = builder.build().getEncodedQuery();*/

    private void getDbData(String url) {
        class GetDataJSON extends AsyncTask<String, Void, String> {
            /*
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                // Showing progress dialog
                pDialog = new ProgressDialog(SelectCalendarActivity.this);
                pDialog.setMessage("Please wait...");
                pDialog.setCancelable(false);
                pDialog.show();
            }*/

            @Override
            protected String doInBackground(String... params) {
                String uri = params[0];

                BufferedReader bufferedReader = null;
                try {
                    URL url = new URL(uri);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                    String post_data = "date=" + Uri.encode(day);
                    Log.d(TAG, "date = " + post_data);

                    StringBuilder sb = new StringBuilder();

                    if (conn != null) {
                        conn.setConnectTimeout(10000);

                        conn.setUseCaches(false);
                        conn.setDefaultUseCaches(false);

                        conn.setDoOutput(true);
                        conn.setRequestMethod("POST");
                        conn.setRequestProperty("USET-AGENT", "Mozilla/5.0");
                        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                        conn.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

                        //서버로 값 전송
                        DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
                        wr.writeBytes(post_data);
                        wr.flush();
                        wr.close();

                        int responseCode = conn.getResponseCode();
                        System.out.println("GET Response Code : " + responseCode);
                        //String res = Integer.toString(responseCode);
                        //Log.e(TAG,"response="+res);
                        if (responseCode == HttpURLConnection.HTTP_OK) { //연결 코드가 리턴되면
                            bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                            String json;
                            while ((json = bufferedReader.readLine()) != null) {
                                sb.append(json + "\n");
                                jsonStr = json;
                                Log.d(TAG, "jsonStr11" + jsonStr);
                            }
                        }
                        bufferedReader.close();
                    }
                    return sb.toString().trim();
                } catch (Exception e) {
                    return new String("Exception: " + e.getMessage());
                }
            }
        }
        GetDataJSON g = new GetDataJSON();
        g.execute(url);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (jsonStr != null) {
            try {
                Log.d(TAG, "jsonStr22" + jsonStr);
                JSONObject jsonObj = new JSONObject(jsonStr);

                // Getting JSON Array node
                JSONArray contacts = jsonObj.getJSONArray("result");

                // looping through All Contacts
                for (int i = 0; i < contacts.length(); i++) {
                    JSONObject c = contacts.getJSONObject(i);

                    String litter_id = c.getString("litter_id");
                    String enter_time = c.getString("enter_time");
                    String exit_time = c.getString("exit_time");
                    String total_time = c.getString("total_time");
                    String poop_weight = c.getString("poop_weight");
                    String cat_weight = c.getString("cat_weight");

                    // tmp hash map for single contact
                    HashMap<String, String> contact = new HashMap<>();

                    // adding each child node to HashMap key => value
                    contact.put("litter_id", litter_id);
                    contact.put("enter_time", enter_time);
                    contact.put("exit_time", exit_time);
                    contact.put("total_time", total_time);
                    contact.put("poop_weight", poop_weight);
                    contact.put("cat_weight", cat_weight);

                    // adding contact to contact list
                    contactList02.add(contact);
                    Log.d(TAG,"contact = " + contactList02.toString());
                }
            } catch (final JSONException e) {
                Log.e(TAG, "Json parsing error: " + e.getMessage());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Json parsing error: " + e.getMessage(),
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });
            }
        } else {
            Log.e(TAG, "Couldn't get json from server.");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(),
                            "Couldn't get json from server. Check LogCat for possible errors!",
                            Toast.LENGTH_LONG)
                            .show();
                }
            });
            }

        ListAdapter adapter = new SimpleAdapter(
                SelectCalendarActivity.this, contactList02,
                R.layout.list_item02,
                new String[]{"enter_time", "exit_time", "total_time","poop_weight","cat_weight"},
                new int[]{R.id.enter_time, R.id.exit_time, R.id.total_time, R.id.poop_weight, R.id.cat_weight});

        lv02.setAdapter(adapter);

    }
/*
    public void start(){
        if (jsonStr != null) {
            try {
                Log.d(TAG, "jsonStr22" + jsonStr);
                JSONObject jsonObj = new JSONObject(jsonStr);

                // Getting JSON Array node
                JSONArray contacts = jsonObj.getJSONArray("result");

                // looping through All Contacts
                for (int i = 0; i < contacts.length(); i++) {
                    JSONObject c = contacts.getJSONObject(i);

                    String litter_id = c.getString("litter_id");
                    String enter_time = c.getString("enter_time");
                    String exit_time = c.getString("exit_time");
                    String total_time = c.getString("total_time");
                    String poop_weight = c.getString("poop_weight");
                    String cat_weight = c.getString("cat_weight");


                    // tmp hash map for single contact
                    HashMap<String, String> contact = new HashMap<>();

                    // adding each child node to HashMap key => value
                    contact.put("litter_id", litter_id);
                    contact.put("enter_time", enter_time);
                    contact.put("exit_time", exit_time);
                    contact.put("total_time", total_time);
                    contact.put("poop_weight", poop_weight);
                    contact.put("cat_weight", cat_weight);

                    // adding contact to contact list
                    contactList.add(contact);
                }
            } catch (final JSONException e) {
                Log.e(TAG, "Json parsing error: " + e.getMessage());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Json parsing error: " + e.getMessage(),
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });
            }
        } else {
            Log.e(TAG, "Couldn't get json from server.");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(),
                            "Couldn't get json from server. Check LogCat for possible errors!",
                            Toast.LENGTH_LONG)
                            .show();
                }
            });
        }*/


        /*

        ListAdapter adapter = new SimpleAdapter(
                SelectCalendarActivity.this, contactList,
                R.layout.list_item02,
                new String[]{"enter_time", "exit_time", "total_time","poop_weight","cat_weight"},
                new int[]{R.id.enter_time, R.id.exit_time, R.id.total_time, R.id.poop_weight, R.id.cat_weight});

        lv.setAdapter(adapter);*/
    }

    /*
    private class GetContacts extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(SelectCalendarActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();

            String jsonStr = sh.makeServiceCall(url);

            Log.e(TAG, "Response from url: " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    JSONArray contacts = jsonObj.getJSONArray("result");

                    // looping through All Contacts
                    for (int i = 0; i < contacts.length(); i++) {
                        JSONObject c = contacts.getJSONObject(i);

                        String litter_id = c.getString("litter_id");
                        String enter_time = c.getString("enter_time");
                        String exit_time = c.getString("exit_time");
                        String total_time = c.getString("total_time");
                        String poop_weight = c.getString("poop_weight");
                        String cat_weight = c.getString("cat_weight");


                        // tmp hash map for single contact
                        HashMap<String, String> contact = new HashMap<>();

                        // adding each child node to HashMap key => value
                        contact.put("litter_id", litter_id);
                        contact.put("enter_time", enter_time);
                        contact.put("exit_time", exit_time);
                        contact.put("total_time", total_time);
                        contact.put("poop_weight", poop_weight);
                        contact.put("cat_weight", cat_weight);

                        // adding contact to contact list
                        contactList.add(contact);
                    }
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });
                }
            } else {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();

            ListAdapter adapter = new SimpleAdapter(
                    SelectCalendarActivity.this, contactList,
                    R.layout.list_item,
                    new String[]{"enter_time", "exit_time", "total_time","poop_weight","cat_weight"},
                    new int[]{R.id.enter_time, R.id.exit_time, R.id.total_time, R.id.poop_weight, R.id.cat_weight});

            lv.setAdapter(adapter);
        }
    }*/
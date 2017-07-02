package com.example.catlitterbox05;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {
    private String TAG = MainActivity.class.getSimpleName();
    TextView catName;

    private ProgressDialog pDialog;
    private ListView lv;
    // URL to get contacts JSON
    private static String url = "http://52.78.207.144/application/getLitterData.php";

    ArrayList<HashMap<String, String>> contactList;
    String catNameMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        catName = (TextView)findViewById(R.id.catName);

        //Intent intent = getIntent();
        //catNameMain = intent.getExtras().getString("catNameSet");
        //catName.setText(catNameMain);

        Button calenderBtn = (Button) findViewById(R.id.calenderBtn);
        calenderBtn.setOnClickListener(new View.OnClickListener () {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CalendarActivity.class);
                startActivity(intent);
            }
        });

        Button settingBtn = (Button) findViewById(R.id.settingBtn);
        settingBtn.setOnClickListener(new View.OnClickListener () {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                startActivity(intent);
            }
        });

        //데이터 가져오기
        contactList = new ArrayList<>();
        lv = (ListView) findViewById(R.id.list);

        new GetContacts().execute();
    }

    private class GetContacts extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response
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
            /* *
             * Updating parsed JSON data into ListView
             * */
            ListAdapter adapter = new SimpleAdapter(
                    MainActivity.this, contactList,
                    R.layout.list_item,
                    new String[]{"enter_time", "exit_time", "total_time","poop_weight","cat_weight"},
                    new int[]{R.id.enter_time, R.id.exit_time, R.id.total_time, R.id.poop_weight, R.id.cat_weight});

            lv.setAdapter(adapter);
        }
    }
}

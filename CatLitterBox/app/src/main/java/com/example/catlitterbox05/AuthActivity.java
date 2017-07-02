package com.example.catlitterbox05;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

public class AuthActivity extends AppCompatActivity {
    private String TAG = AuthActivity.class.getSimpleName();

    private Button okBtn;
    EditText serialNum;
    String ajson;
    String token;
    String squery = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        FirebaseMessaging.getInstance().subscribeToTopic("news");
        token = FirebaseInstanceId.getInstance().getToken();

        serialNum = (EditText) findViewById(R.id.serialNum);
        okBtn = (Button) findViewById(R.id.okBtn);

        //sAdapter = new ListViewAdapter(this);
        okBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                searchData();
                String url = "http://52.78.207.144/application/matchSerialNum.php";
                getDbData(url);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                start();
            }
        });
    }

    private void searchData() {
        squery = serialNum.getText().toString().trim();
        if(serialNum.equals("")){
            Toast.makeText(AuthActivity.this,"시리얼 번호를 입력하세요!",Toast.LENGTH_SHORT).show();
        } else {
            finish();
        }
    }

    private void getDbData(String url) {
        class GetDataJSON extends AsyncTask<String,Void,String>{
            @Override
            protected String doInBackground(String...params){
                String uri = params[0];

                BufferedReader bufferedReader = null;
                try{
                    URL url = new URL(uri);
                    HttpURLConnection conn = (HttpURLConnection)url.openConnection();

                    String post_data = "serial_num=" + Uri.encode(squery) + "&token=" + Uri.encode(token);
                    Log.d(TAG, "post_data = " + post_data);

                    StringBuilder sb = new StringBuilder();

                    if(conn != null){
                        conn.setConnectTimeout(10000);

                        conn.setUseCaches(false);
                        conn.setDefaultUseCaches(false);

                        conn.setDoOutput(true);
                        conn.setRequestMethod("POST");
                        conn.setRequestProperty("USET-AGENT","Mozilla/5.0");
                        conn.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
                        conn.setRequestProperty("Accept-Language","en-US,en;q=0.5");

                        //서버로 값 전송
                        DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
                        wr.writeBytes(post_data);
                        wr.flush();
                        wr.close();

                        int responseCode = conn.getResponseCode();
                        System.out.println("GET Response Code : " + responseCode);
                        //String res = Integer.toString(responseCode);
                        //Log.e(TAG,"response="+res);
                        if(responseCode == HttpURLConnection.HTTP_OK){ //연결 코드가 리턴되면
                            bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                            String json;
                            while((json = bufferedReader.readLine())!=null){
                                sb.append(json + "\n");
                                ajson = json;
                                Log.e(TAG,"ajson1 = "+ajson);
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
    }

    //0 이면 등록되지않은 시리얼번호, 1이 제대로 된것, 2는 이미 등록된 시리얼 번호,
    public void start() {
        Log.e(TAG,"ajson2 = "+ajson);
        if(ajson.equals("0")){
            Toast.makeText(AuthActivity.this,"등록되지 않은 시리얼 번호입니다.",Toast.LENGTH_LONG).show();
        }
        else if (ajson.equals("1")) {
            Intent intent = new Intent(AuthActivity.this, MainActivity.class);
            startActivity(intent);
        }
        else
            Toast.makeText(AuthActivity.this,"이미 등록된 시리얼 번호입니다.",Toast.LENGTH_LONG).show();
    }
}

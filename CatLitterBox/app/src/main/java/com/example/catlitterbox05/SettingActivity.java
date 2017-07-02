package com.example.catlitterbox05;

import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ToggleButton;

import com.google.firebase.iid.FirebaseInstanceId;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static com.example.catlitterbox05.R.drawable.app_settings_device_switch_selected;

public class SettingActivity extends AppCompatActivity {
    private static String TAG = "SettingActivity";
    private static final int SELECT_PICTURE = 1;
    String token;

    ImageView catImage;
    Uri uri;
    String catNameSet;

    Button catBtn;
    EditText catName;
    Button deviceSwitch;
    ToggleButton toggleButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        token = FirebaseInstanceId.getInstance().getToken();
        toggleButton = (ToggleButton) findViewById(R.id.toggleButton);
        setChecked(true);

        catName = (EditText) findViewById(R.id.catName);
        catNameSet = catName.getText().toString();
        deviceSwitch = (Button) findViewById(R.id.deviceSwitch);
        catImage = (ImageView) findViewById(R.id.catImage);

        Button calendarBtn = (Button) findViewById(R.id.calendarBtn);
        calendarBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this, CalendarActivity.class);
                startActivity(intent);
            }
        });

        Button mainBtn = (Button) findViewById(R.id.mainBtn);
        mainBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        catBtn = (Button) findViewById(R.id.catBtn);
        catBtn.setOnClickListener(new View.OnClickListener() {   //사진첩 열기
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        //deviceSwitch
        deviceSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deviceSwitch.setBackgroundResource(app_settings_device_switch_selected);
                String url = "http://52.78.207.144/application/deviceSwitch.php";
                getDbData(url);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(SettingActivity.this, AuthActivity.class);
                startActivity(intent);
            }
        });
    }

    public void setChecked(boolean checked) {
        toggleButton.setChecked(true);
    }

    private void getDbData(String url) {
        class GetDataJSON extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... params) {
                String uri = params[0];

                BufferedReader bufferedReader = null;
                try {
                    URL url = new URL(uri);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                    String post_data = "token=" + Uri.encode(token);
                    Log.d(TAG, "token = " + post_data);

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
                        if (responseCode == HttpURLConnection.HTTP_OK) { //연결 코드가 리턴되면
                            bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                            String json;
                            while ((json = bufferedReader.readLine()) != null) {
                                sb.append(json + "\n");
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

    //사진첩 열기
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
        intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        //intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, SELECT_PICTURE);
    }

    //이미지 선택 후
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
/*
        try {
            //이미지를 하나 골랐을때
            if (requestCode == SELECT_PICTURE && resultCode == RESULT_OK && null != data) {
                //data에서 절대경로로 이미지를 가져옴
                uri = data.getData();
                getPath(uri);
                Log.d(TAG, "path = " + getPath(uri));
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                //이미지 사이즈를 줄이기
                int nh = (int) (bitmap.getHeight() * (300.0 / bitmap.getWidth()));
                Bitmap scaled = Bitmap.createScaledBitmap(bitmap, 300, nh, true);

                RoundedAvatarDrawable tmeRoundedAvatarDrawable = new RoundedAvatarDrawable(scaled);

                catImage.setBackground(tmeRoundedAvatarDrawable);
                catImage.setImageBitmap(scaled);
            } else {
                Toast.makeText(this, "취소 되었습니다.", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "로딩 오류", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }*/
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                try {
                    Bitmap bm = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                    catImage.setImageBitmap(bm);

                    uri = data.getData();
                    //경로 구하기
                    getPath(uri);
                    Log.d(TAG,"getPath = " + getPath(uri).toString());

                    //this.data.setImg(catImage);
                    //this.data.setImgName(data.getData().getLastPathSegment());
                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //사진 경로
    private String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        CursorLoader cursorLoader = new CursorLoader(this, uri, projection, null, null, null);
        Cursor cursor = cursorLoader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

}


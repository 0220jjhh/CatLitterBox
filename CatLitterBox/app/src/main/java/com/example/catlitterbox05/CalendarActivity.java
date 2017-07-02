package com.example.catlitterbox05;
import java.util.ArrayList;
import java.util.Calendar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.Toast;

/**
 * 그리드뷰를 이용한 달력
 */
public class CalendarActivity extends AppCompatActivity
{

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        Button mainBtn = (Button) findViewById(R.id.mainBtn);
        mainBtn.setOnClickListener(new View.OnClickListener () {
            public void onClick(View v) {
                Intent intent = new Intent(CalendarActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        Button settingBtn = (Button) findViewById(R.id.settingsBtn);
        settingBtn.setOnClickListener(new View.OnClickListener () {
            public void onClick(View v) {
                Intent intent = new Intent(CalendarActivity.this, SettingActivity.class);
                startActivity(intent);
            }
        });
        CalendarView calendar = (CalendarView) findViewById(R.id.calendar);
        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                Intent intent = new Intent(CalendarActivity.this,SelectCalendarActivity.class);
                String date = year+"-"+ "0"+(month + 1)+"-"+dayOfMonth;
                //Toast.makeText(CalendarActivity.this, date, Toast.LENGTH_LONG).show();
                intent.putExtra("date",date);
                startActivity(intent);
            }
        });

    }

}


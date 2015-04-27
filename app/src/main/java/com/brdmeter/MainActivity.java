package com.brdmeter;

import android.app.Activity;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.view.View.OnClickListener;
import android.widget.TextView;


public class MainActivity extends Activity {

    int totalTime = 0;         //всего времени
    int bufTime = 0;           //счётчик секунд
    float totalMoney = 0;        //всего денег
    float bufMoney = 0;          //счётчик копеек
    float salary = 23000;            //зарплата
    float countDayInMonth = 30;      //рабочих дней в месяце
    float countHoursIntDay = 8;      //рабочих часов в день
    float tickInMoney = 0;         //денег в секунду
    int tickCorrector = 0;          //коррекция счётчика хронометра


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button BtnStart = (Button)findViewById(R.id.buttonStart);
        final Button BtnStop =  (Button)findViewById(R.id.buttonStop);
        final Chronometer Chrome1 = (Chronometer)findViewById(R.id.chronometer);
        final TextView TextTotalTime = (TextView)findViewById(R.id.textView);
        final TextView TextTotalMoney = (TextView)findViewById(R.id.textView2);
        final TextView TextMoneyCounter = (TextView)findViewById(R.id.textView3);


        BtnStop.setEnabled(false);
        Chrome1.setBase(SystemClock.elapsedRealtime());         //сброс времени(на всякий)
        Chrome1.stop();

        tickInMoney = salary / countDayInMonth / countHoursIntDay / 60 / 60;    //подсчёт денег в секунду

        BtnStart.setOnClickListener(new OnClickListener() {             //кнопка Старт
            @Override
            public void onClick(View v) {
                Chrome1.setBase(SystemClock.elapsedRealtime());
                Chrome1.start();
                BtnStart.setEnabled(false);
                BtnStop.setEnabled(true);
            }
        });

        BtnStop.setOnClickListener(new OnClickListener() {              //Кнопка стоп
            @Override
            public void onClick(View v) {
                totalTime = totalTime + bufTime;    //всего времени подсчёт
                totalMoney = totalMoney + bufMoney;     //всего денег подсчёт

                Chrome1.setBase(SystemClock.elapsedRealtime());
                Chrome1.stop();
                BtnStart.setEnabled(true);
                BtnStop.setEnabled(false);
                TextMoneyCounter.clearComposingText();

                TextTotalTime.setText("Всего времени: "+String.valueOf(totalTime));
                TextTotalMoney.setText("Всего денег: " + String.format("%.2f", totalMoney));
                TextMoneyCounter.setText("0.0");

                bufTime = 0;
                bufMoney = 0;
                tickCorrector = 0;
            }
        });

        Chrome1.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                if (tickCorrector > 1) {
                    bufTime = bufTime + 1;      //увеличиваются секунды
                    bufMoney = bufMoney + tickInMoney;    //увеличиваются копейки

                    TextMoneyCounter.setText(String.format("%.2f", bufMoney));     //отображается увеличение копеек
                }
                tickCorrector = tickCorrector + 1;
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

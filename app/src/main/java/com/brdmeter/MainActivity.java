package com.brdmeter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
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
    boolean btnStopPressed = false;   //нажата ли кнопка Стоп

    public static final String PREFERENCE = "preference6";       //имя файла настроек
    public final String PREFERENCE_TOTAL_TIME = "0";    //параметр настроек Всего времени
    public final String PREFERENCE_TOTAL_MONEY = "";   //параметр настроек Всего денег

    private SharedPreferences setting;

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

        setting = getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE);                           //подключаемся к настройкам
        if (setting.contains(PREFERENCE_TOTAL_MONEY) || setting.contains(PREFERENCE_TOTAL_TIME))    //проверяем есть ли они там
        {
            totalTime = setting.getInt(PREFERENCE_TOTAL_TIME, 0);       //получаем
            totalMoney = setting.getFloat(PREFERENCE_TOTAL_MONEY, 0);   //данные
            TextTotalTime.setText("Всего времени: "+String.valueOf(totalTime));
            TextTotalMoney.setText("Всего денег: " + String.format("%.2f", totalMoney));
        }

        BtnStop.setEnabled(false);
        Chrome1.setBase(SystemClock.elapsedRealtime());         //сброс времени(на всякий)
        Chrome1.stop();

        tickInMoney = salary / countDayInMonth / countHoursIntDay / 60 / 60;    //подсчёт денег в секунду

        //КНОПКА СТАРТ

        BtnStart.setOnClickListener(new OnClickListener() {             //кнопка Старт
            @Override
            public void onClick(View v) {
                Chrome1.setBase(SystemClock.elapsedRealtime());
                Chrome1.start();
                btnStopPressed = false;     //кнопка стоп не нажата, подсчёт идёт, данные сохранять не надо
                BtnStart.setEnabled(false);
                BtnStop.setEnabled(true);
            }
        });

        //КНОПКА СТОП

        BtnStop.setOnClickListener(new OnClickListener() {              //Кнопка стоп
            @Override
            public void onClick(View v) {
                btnStopPressed = true;      //кнопка стоп нажата, подсчёт не идёт, данные сохранятся
                totalTime = totalTime + bufTime;    //всего времени подсчёт
                totalMoney = totalMoney + bufMoney;     //всего денег подсчёт

                Chrome1.setBase(SystemClock.elapsedRealtime());
                Chrome1.stop();
                BtnStart.setEnabled(true);
                BtnStop.setEnabled(false);

                TextTotalTime.setText("Всего времени: "+String.valueOf(totalTime));
                TextTotalMoney.setText("Всего денег: " + String.format("%.2f", totalMoney));
                TextMoneyCounter.setText("0.0");

                bufTime = 0;
                bufMoney = 0;
                tickCorrector = 0;
                if (btnStopPressed) {       //запись данных
                    SharedPreferences.Editor edit = setting.edit();
                    edit.putInt(PREFERENCE_TOTAL_TIME, totalTime);      //кладём
                    edit.putFloat(PREFERENCE_TOTAL_MONEY, totalMoney);  //данные
                    edit.apply();       //запись
                }
            }
        });

        //ТИКАНЬЕ ХРОНОМЕТРА

        Chrome1.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                if (tickCorrector > 1) {
                    bufTime = bufTime + 1;      //увеличиваются секунды
                    bufMoney = bufMoney + tickInMoney;    //увеличиваются копейки

                    TextMoneyCounter.setText(String.format("%.2f", bufMoney));     //отображается увеличение копеек
                }
                else {
                    tickCorrector = tickCorrector + 1;
                }
            }
        });
    }

    //СВОРАЧИВАНИЕ ПРИЛОЖЕНИЯ

    @Override
    protected void onPause() {
        super.onPause();
        if (btnStopPressed) {
            SharedPreferences.Editor edit = setting.edit();
            edit.putInt(PREFERENCE_TOTAL_TIME, totalTime);
            edit.putFloat(PREFERENCE_TOTAL_MONEY, totalMoney);
            edit.apply();
        }
    }

    //РАЗВОРАЧИВАНИЕ ПРИЛОЖЕНИЯ

    @Override
    protected  void onResume() {
        super.onResume();
        if (btnStopPressed) {
            totalTime = setting.getInt(PREFERENCE_TOTAL_TIME, 0);
            totalMoney = setting.getFloat(PREFERENCE_TOTAL_MONEY, 0);
        }
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

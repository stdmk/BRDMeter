package com.brdmeter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
    public final String PREFERENCE_TOTAL_TIME = "totaltime";             //параметр настроек Всего времени
    public final String PREFERENCE_TOTAL_MONEY = "totalmoney";            //параметр настроек Всего денег
    public final String PREFERENCE_WORKDAY_BEGIN = "workdaybegin";          //время начала рабочего дня
    public final String PREFERENCE_WORKDAY_END = "workdayend";            //время конца рабочего дня
    public final String PREFERENCE_LUNCH_BEGIN = "lunchbegin";            //время начала обеда
    public final String PREFERENCE_LUNCH_END = "lunchbegin";              //время конца обеда
    public final String PREFERENCE_SALARY = "salary";

    private SharedPreferences setting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button btnStart = (Button) findViewById(R.id.buttonStart);
        final Button btnStop = (Button) findViewById(R.id.buttonStop);
        final Chronometer chrome1 = (Chronometer) findViewById(R.id.chronometer);
        final TextView textTotalTime = (TextView) findViewById(R.id.textView);
        final TextView textTotalMoney = (TextView) findViewById(R.id.textView2);
        final TextView textMoneyCounter = (TextView) findViewById(R.id.textView3);

        setting = getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE);                           //подключаемся к настройкам
        if (setting.contains(PREFERENCE_TOTAL_MONEY) || setting.contains(PREFERENCE_TOTAL_TIME) ||  //проверяем есть ли они там
                setting.contains(PREFERENCE_WORKDAY_BEGIN) || setting.contains(PREFERENCE_WORKDAY_END) ||
                setting.contains(PREFERENCE_LUNCH_BEGIN) || setting.contains(PREFERENCE_LUNCH_END) ||
                setting.contains(PREFERENCE_SALARY)) {
            totalTime = setting.getInt(PREFERENCE_TOTAL_TIME, 0);       //получаем
            totalMoney = setting.getFloat(PREFERENCE_TOTAL_MONEY, 0);   //данные
            textTotalTime.setText("Всего времени: " + String.valueOf(totalTime));
            textTotalMoney.setText("Всего денег: " + String.format("%.2f", totalMoney));
        }
            Intent intent = new Intent(MainActivity.this, SettingActivity.class);
            startActivity(intent);



        btnStop.setEnabled(false);
        chrome1.setBase(SystemClock.elapsedRealtime());         //сброс времени(на всякий)
        chrome1.stop();

        tickInMoney = salary / countDayInMonth / countHoursIntDay / 60 / 60;    //подсчёт денег в секунду

        //КНОПКА СТАРТ

        btnStart.setOnClickListener(new OnClickListener() {             //кнопка Старт
            @Override
            public void onClick(View v) {
                chrome1.setBase(SystemClock.elapsedRealtime());
                chrome1.start();
                btnStopPressed = false;     //кнопка стоп не нажата, подсчёт идёт, данные сохранять не надо
                btnStart.setEnabled(false);
                btnStop.setEnabled(true);
            }
        });

        //КНОПКА СТОП

        btnStop.setOnClickListener(new OnClickListener() {              //Кнопка стоп
            @Override
            public void onClick(View v) {
                btnStopPressed = true;      //кнопка стоп нажата, подсчёт не идёт, данные сохранятся
                totalTime = totalTime + bufTime;    //всего времени подсчёт
                totalMoney = totalMoney + bufMoney;     //всего денег подсчёт

                chrome1.setBase(SystemClock.elapsedRealtime());
                chrome1.stop();
                btnStart.setEnabled(true);
                btnStop.setEnabled(false);

                textTotalTime.setText("Всего времени: " + String.valueOf(totalTime));
                textTotalMoney.setText("Всего денег: " + String.format("%.2f", totalMoney));
                textMoneyCounter.setText("0.0");

                bufTime = 0;
                bufMoney = 0;
                tickCorrector = 0;
                saveTotalData();
            }
        });

        //ТИКАНЬЕ ХРОНОМЕТРА

        chrome1.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                if (tickCorrector > 1) {
                    bufTime = bufTime + 1;      //увеличиваются секунды
                    bufMoney = bufMoney + tickInMoney;    //увеличиваются копейки

                    textMoneyCounter.setText(String.format("%.2f", bufMoney));     //отображается увеличение копеек
                } else {
                    tickCorrector = tickCorrector + 1;
                }
            }
        });
    }

    //СВОРАЧИВАНИЕ ПРИЛОЖЕНИЯ

    @Override
    protected void onPause() {
        super.onPause();
        saveTotalData();
    }

    //РАЗВОРАЧИВАНИЕ ПРИЛОЖЕНИЯ

    @Override
    protected void onResume() {
        super.onResume();
    }

    //ПРОЦЕДУРА СОХРАНЕНИЯ ДАННЫХ

    public void saveTotalData(){
        SharedPreferences.Editor edit = setting.edit();
        edit.putInt(PREFERENCE_TOTAL_TIME, totalTime);
        edit.putFloat(PREFERENCE_TOTAL_MONEY, totalMoney);
        edit.apply();
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

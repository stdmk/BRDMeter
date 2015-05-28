package com.brdmeter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.view.View.OnClickListener;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends Activity {


    long totalTime = 0;             //всего времени
    int bufTime = 0;                //счётчик секунд
    float totalMoney = 0;           //всего денег
    float bufMoney = 0;             //счётчик копеек
    float salary = 23000;           //зарплата
    float countDayInMonth = 30;     //рабочих дней в месяце
    float countHoursIntDay = 8;     //рабочих часов в день
    float tickInMoney = 0;          //денег в секунду
    int tickCorrector = 0;          //коррекция счётчика хронометра
    int workDayBegin;               //время начала рабочего дня (в секундах)
    int workDayEnd;                 //время конца рабочего дня (в секундах)
    int lunchBegin;                 //время начала обеда(в секундах)
    int lunchEnd;                   //время конца обеда(в секундах)
    float tempMoney;                //временные деньги (необходимо, если приложение было свёрнуто)
    int tempTime;                   //временное время о_О (необходимо по той же причине)
    long tempChrome1;               //положение хронометра перед сворачиванием
    Timer mainTimer;                //главный таймер
    MainTimerTask mainTimerTask;   //действия по таймеру
    long secInTimer;                //секунд по таймеру
    long minInTimer;                //минут по таймеру
    long hourInTimer;               //часов по таймеру

    public static final String PREFERENCE = "preference";       //имя файла настроек
    public final String PREFERENCE_TOTAL_TIME = "totaltime";             //параметр настроек Всего времени
    public final String PREFERENCE_TOTAL_MONEY = "totalmoney";            //параметр настроек Всего денег
    public final String PREFERENCE_WORKDAY_BEGIN = "workdaybegin";          //время начала рабочего дня
    public final String PREFERENCE_WORKDAY_END = "workdayend";            //время конца рабочего дня
    public final String PREFERENCE_LUNCH_BEGIN = "lunchbegin";            //время начала обеда
    public final String PREFERENCE_LUNCH_END = "lunchbegin";              //время конца обеда
    public final String PREFERENCE_SALARY = "salary";                     //зарплата
    public final String PREFERENCE_TEMP_TIME = "temptime";                //временное время
    public final String PREFERENCE_TEMP_MONEY = "tempmoney";              //временные деньги(когда приложение сворачивается)
    public final String PREFERENCE_TEMP_HOUR = "temphour";                //временно часов
    public final String PREFERENCE_TEMP_MIN = "tempmin";                  //временно минут
    public final String PREFERENCE_TEMP_SEC = "tempsec";                  //временно секунд

    private SharedPreferences setting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button btnStart = (Button) findViewById(R.id.buttonStart);            //кнопка старт
        final Button btnStop = (Button) findViewById(R.id.buttonStop);              //кнопка стоп
        final TextView textTotalTime = (TextView) findViewById(R.id.textView);      //вывод всего времени
        final TextView textTotalMoney = (TextView) findViewById(R.id.textView2);    //вывод всего денег
        final TextView textMoneyCounter = (TextView) findViewById(R.id.textView3);  //вывод счётчика времени
        final TextView textTimeCounter = (TextView) findViewById(R.id.textView6);   //вывод счётчика копеек

        setting = getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE);                           //подключаемся к настройкам
        totalTime = setting.getLong(PREFERENCE_TOTAL_TIME, 0);
        totalMoney = setting.getFloat(PREFERENCE_TOTAL_MONEY, 0);
        workDayBegin = setting.getInt(PREFERENCE_WORKDAY_BEGIN, 0);         //берём настройки из файла
        workDayEnd = setting.getInt(PREFERENCE_WORKDAY_END, 0);
        lunchBegin = setting.getInt(PREFERENCE_LUNCH_BEGIN, 0);
        lunchEnd = setting.getInt(PREFERENCE_LUNCH_END, 0);
        salary = setting.getFloat(PREFERENCE_SALARY, 0);
        tempTime = setting.getInt(PREFERENCE_TEMP_TIME, 0);
        tempMoney = setting.getFloat(PREFERENCE_TEMP_MONEY, 0);

        if (salary != 0 ) {                                                             //если зарплата не пустая - всё нормально, работаем
            textTotalTime.setText("Всего времени: " + String.valueOf(totalTime));
            textTotalMoney.setText("Всего денег: " + String.format("%.2f", totalMoney));
        } else {
            Intent intent = new Intent(MainActivity.this, SettingActivity.class);       //нет - заупускаем настройки
            startActivity(intent);
        }

        if (tempMoney != 0) {                                                           //если приложение было свёрнуто
            hourInTimer = setting.getLong(PREFERENCE_TEMP_HOUR, 0);
            minInTimer = setting.getLong(PREFERENCE_TEMP_MIN, 0);
            secInTimer = setting.getLong(PREFERENCE_TEMP_SEC, 0);
            bufMoney = setting.getFloat(PREFERENCE_TEMP_MONEY, 0);

            textTimeCounter.setText(String.format("%02d", hourInTimer) + ":"
                    + String.format("%02d", minInTimer) + ":"                   //выводим временные данные
                    + String.format("%02d", secInTimer));
            textMoneyCounter.setText(String.valueOf(bufMoney));

            btnStart.setEnabled(false);
            btnStop.setEnabled(true);

            mainTimer = new Timer();
            mainTimerTask = new MainTimerTask();
            mainTimer.schedule(mainTimerTask, 1000, 1000);      //запуск таймера
        } else {
            secInTimer = 0;
            minInTimer = 0;
            hourInTimer = 0;
            btnStop.setEnabled(false);
        }

        tickInMoney = salary / countDayInMonth / countHoursIntDay / 60 / 60;    //подсчёт денег в секунду

        //КНОПКА СТАРТ

        btnStart.setOnClickListener(new OnClickListener() {             //кнопка Старт
            @Override
            public void onClick(View v) {
                mainTimer = new Timer();
                mainTimerTask = new MainTimerTask();
                mainTimer.schedule(mainTimerTask, 1000, 1000);      //запуск таймера
                btnStart.setEnabled(false);
                btnStop.setEnabled(true);
            }
        });

        //КНОПКА СТОП

        btnStop.setOnClickListener(new OnClickListener() {              //Кнопка стоп
            @Override
            public void onClick(View v) {
                mainTimer.cancel();         //стоп таймера
                mainTimer = null;

                totalTime = totalTime + (hourInTimer * 60 * 60 + minInTimer * 60 + secInTimer);    //всего времени подсчёт
                totalMoney = totalMoney + bufMoney;     //всего денег подсчёт

                textTimeCounter.setText("00:00:00");    //обнуляем вывод таймера

                btnStart.setEnabled(true);
                btnStop.setEnabled(false);

                textTotalTime.setText("Всего времени: " + String.valueOf(totalTime));
                textTotalMoney.setText("Всего денег: " + String.format("%.2f", totalMoney));
                textMoneyCounter.setText("0.0");

                bufTime = 0;
                bufMoney = 0;
                secInTimer = 0;
                minInTimer = 0;
                hourInTimer = 0;

                SaveTotalData();
            }
        });
    }

    class MainTimerTask extends TimerTask {
        @Override
        public void run() {
            final TextView textMoneyCounter = (TextView) findViewById(R.id.textView3);  //вывод счётчика времени
            final TextView textTimeCounter = (TextView) findViewById(R.id.textView6);   //вывод счётчика копеек

            secInTimer = secInTimer +1;
            if (secInTimer == 60) {
                secInTimer = 0;
                minInTimer = minInTimer + 1;            //увеличение секунд, минут, часов
                if (minInTimer == 60) {
                    minInTimer = 0;
                    hourInTimer = hourInTimer + 1;
                }
            }

            bufMoney = bufMoney + tickInMoney;    //увеличиваются копейки

            runOnUiThread(new Runnable() {      //доступ к изменению компонентов
                @Override
                public void run() {
                    textMoneyCounter.setText(String.format("%.2f", bufMoney));      //отображается увеличение копеек
                    textTimeCounter.setText(String.format("%02d", hourInTimer)+":"
                            +String.format("%02d", minInTimer)+":"                  //отображается изменение таймера
                            +String.format("%02d", secInTimer));
                }
            });
        }
    }

    //СВОРАЧИВАНИЕ ПРИЛОЖЕНИЯ

    @Override
    protected void onPause() {
        super.onPause();

        mainTimer.cancel();
        SaveTempData();

        finish();
    }

    //РАЗВОРАЧИВАНИЕ ПРИЛОЖЕНИЯ

    @Override
    protected void onResume() {
        super.onResume();
    }
    //НАЖАТИЕ КНОПКИ НАЗАД

    public void onBackPressed() {
        super.onBackPressed();

        mainTimer.cancel();
        SaveTempData();

        finish();
    }

    //ПРОЦЕДУРА СОХРАНЕНИЯ ДАННЫХ

    public void SaveTotalData(){
        SharedPreferences.Editor edit = setting.edit();
        edit.putLong(PREFERENCE_TOTAL_TIME, totalTime);
        edit.putFloat(PREFERENCE_TOTAL_MONEY, totalMoney);
        edit.putInt(PREFERENCE_TEMP_TIME, 0);
        edit.putFloat(PREFERENCE_TEMP_MONEY, 0);
        edit.putLong(PREFERENCE_TEMP_HOUR, 0);
        edit.putLong(PREFERENCE_TEMP_MIN, 0);
        edit.putLong(PREFERENCE_TEMP_SEC, 0);
        edit.apply();
    }

    //ПРОЦЕДУРА СОХРАНЕНИЯ ВРЕМЕННЫХ ФАЙЛОВ

    public void SaveTempData () {

        SharedPreferences.Editor edit = setting.edit();
        edit.putInt(PREFERENCE_TEMP_TIME, bufTime);
        edit.putFloat(PREFERENCE_TEMP_MONEY, bufMoney);
        edit.putLong(PREFERENCE_TEMP_HOUR, hourInTimer);
        edit.putLong(PREFERENCE_TEMP_MIN, minInTimer);
        edit.putLong(PREFERENCE_TEMP_SEC, secInTimer);
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

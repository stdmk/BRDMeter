package com.brdmeter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.view.View.OnClickListener;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity {


    long totalTime = 0;             //всего времени
    long bufTime = 0;                //счётчик секунд
    float totalMoney = 0;           //всего денег
    float bufMoney = 0;             //счётчик копеек
    float salary = 23000;           //зарплата
    float countDayInMonth = 30;     //рабочих дней в месяце
    float countHoursIntDay = 8;     //рабочих часов в день
    float tickInMoney = 0;          //денег в секунду
    int workDayBegin;               //время начала рабочего дня (в секундах)
    int workDayEnd;                 //время конца рабочего дня (в секундах)
    int lunchBegin;                 //время начала обеда(в секундах)
    int lunchEnd;                   //время конца обеда(в секундах)
    float tempMoney;                //временные деньги (необходимо, если приложение было свёрнуто)
    long hideTime;                  //время когда приложение было свёрнуто
    Timer mainTimer;                //главный таймер
    MainTimerTask mainTimerTask;    //действия по таймеру
    long secInTimer;                //секунд по таймеру
    long minInTimer;                //минут по таймеру
    long hourInTimer;               //часов по таймеру
    long timeNow;                   //время сейчас
    boolean keyStart = false;       //ключ, что была нажата кнопка старт
    boolean keyDef = false;         //настройки по умолчанию?

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
    public final String PREFERENCE_HIDE_TIME = "hidetime";                //время перед сворачиванием
    public final String PREFERENCE_KEYDEF = "keydef";                     //были ли изменены настройки

    private SharedPreferences setting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final Button btnStart = (Button) findViewById(R.id.buttonStart);            //кнопка старт
        final Button btnStop = (Button) findViewById(R.id.buttonStop);              //кнопка стоп
        final TextView textTotalTime = (TextView) findViewById(R.id.textView);      //вывод всего времени
        final TextView textTotalMoney = (TextView) findViewById(R.id.textView2);    //вывод всего денег
        final TextView textMoneyCounter = (TextView) findViewById(R.id.textView3);  //вывод счётчика времени
        final TextView textTimeCounter = (TextView) findViewById(R.id.textView6);   //вывод счётчика копеек
        final TextView textTest = (TextView) findViewById(R.id.textView7);          //тестовая вьюшка, временная
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);  //прогресс-бар

        setting = getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE);                           //подключаемся к настройкам
        totalTime = setting.getLong(PREFERENCE_TOTAL_TIME, 0);
        totalMoney = setting.getFloat(PREFERENCE_TOTAL_MONEY, 0);
        workDayBegin = setting.getInt(PREFERENCE_WORKDAY_BEGIN, 0);         //берём настройки из файла
        workDayEnd = setting.getInt(PREFERENCE_WORKDAY_END, 0);
        lunchBegin = setting.getInt(PREFERENCE_LUNCH_BEGIN, 0);
        lunchEnd = setting.getInt(PREFERENCE_LUNCH_END, 0);
        salary = setting.getFloat(PREFERENCE_SALARY, 0);
        tempMoney = setting.getFloat(PREFERENCE_TEMP_MONEY, 0);
        keyDef = setting.getBoolean(PREFERENCE_KEYDEF, false);

        timeNow = System.currentTimeMillis() / 1000;   //текущее время

        if (keyDef) {                                                             //настройки были изменены, всё нормально, работаем
            textTotalTime.setText(CalcTotalTime());
            textTotalMoney.setText("Всего денег(руб): " + String.format("%.2f", totalMoney));
        } else {
            Intent intent = new Intent(MainActivity.this, SettingActivity.class);       //нет - заупускаем настройки
            startActivity(intent);
            finish();
        }

        tickInMoney = salary / countDayInMonth / countHoursIntDay / 60 / 60;    //подсчёт денег в секунду
        mainTimer = new Timer();

        if (tempMoney != 0) {                                                           //если приложение было свёрнуто
            hourInTimer = setting.getLong(PREFERENCE_TEMP_HOUR, 0);         //значение часов на таймере перед сворачиванием
            minInTimer = setting.getLong(PREFERENCE_TEMP_MIN, 0);           //значение минут на таймере перед сворачиванием
            secInTimer = setting.getLong(PREFERENCE_TEMP_SEC, 0);           //значение секунд на таймере перед сворачиванием
            bufMoney = setting.getFloat(PREFERENCE_TEMP_MONEY, 0);          //значение копеек перед сворачиванием
            bufTime = setting.getLong(PREFERENCE_TEMP_TIME, 0);             //значение насчитанных секунд перед сворачиванем
            hideTime = setting.getLong(PREFERENCE_HIDE_TIME, 0);            //время в секундах, когда приложение было свёрнуто

            long buf = bufTime+(timeNow-hideTime);              //значение сколько секунд приложение было свёрнуто+сколько насчитало перед сворачиванием
            secInTimer = secInTimer + buf;

            bufMoney = bufMoney + (tickInMoney * secInTimer);   //подсчёт копеек, которые прибавились, пока приложение было свёрнуто

            if (secInTimer>=60) {
                minInTimer = minInTimer +  secInTimer/60;
                secInTimer = secInTimer % 60;
            }                                                       //подсчёт сколько прошло минут или даже часов
            if (minInTimer>=60) {
                hourInTimer = hourInTimer + minInTimer/60;
                minInTimer = minInTimer % 60;
            }

            textTimeCounter.setText(String.format("%02d", hourInTimer) + ":"
                    + String.format("%02d", minInTimer) + ":"                   //выводим временные данные
                    + String.format("%02d", secInTimer));
            textMoneyCounter.setText(String.valueOf(bufMoney)+ " (руб.)");

            btnStart.setEnabled(false);
            btnStop.setEnabled(true);

            mainTimerTask = new MainTimerTask();
            mainTimer.schedule(mainTimerTask, 1000, 1000);      //запуск таймера
        } else {
            secInTimer = 0;
            minInTimer = 0;
            hourInTimer = 0;
            btnStop.setEnabled(false);
        }

        Calendar calendar = Calendar.getInstance();
        int buf = (calendar.get(Calendar.HOUR_OF_DAY) * 60 * 60) + (calendar.get(Calendar.MINUTE) * 60) + calendar.get(Calendar.SECOND);
        buf = buf - workDayBegin;
        progressBar.setMax(workDayEnd - workDayBegin);
        progressBar.setProgress(buf);           //установки прогресс-бара

        //КНОПКА СТАРТ

        btnStart.setOnClickListener(new OnClickListener() {             //кнопка Старт
            @Override
            public void onClick(View v) {
                mainTimerTask = new MainTimerTask();
                mainTimer.schedule(mainTimerTask, 1000, 1000);      //запуск таймера
                btnStart.setEnabled(false);
                btnStop.setEnabled(true);
                keyStart = true;
            }
        });

        //КНОПКА СТОП

        btnStop.setOnClickListener(new OnClickListener() {              //Кнопка стоп
            @Override
            public void onClick(View v) {
                mainTimer.cancel();         //стоп таймера
                mainTimer = null;
                mainTimer = new Timer();

                totalTime = totalTime + (hourInTimer * 60 * 60 + minInTimer * 60 + secInTimer);    //всего времени подсчёт
                totalMoney = totalMoney + bufMoney;     //всего денег подсчёт

                textTimeCounter.setText("00:00:00");    //обнуляем вывод таймера

                btnStart.setEnabled(true);
                btnStop.setEnabled(false);

                textTotalTime.setText(CalcTotalTime());
                textTotalTime.setText(CalcTotalTime());

                textTotalMoney.setText("Всего денег(руб): " + String.format("%.2f", totalMoney));
                textMoneyCounter.setText("0.0 (руб.)");

                bufTime = 0;
                bufMoney = 0;
                secInTimer = 0;
                minInTimer = 0;
                hourInTimer = 0;
                keyStart = false;

                SaveTotalData();
            }
        });
    }

    //ДЕЙСТВИЯ ПО ТАЙМЕРУ

    class MainTimerTask extends TimerTask {
        @Override
        public void run() {
            final TextView textMoneyCounter = (TextView) findViewById(R.id.textView3);  //вывод счётчика времени
            final TextView textTimeCounter = (TextView) findViewById(R.id.textView6);   //вывод счётчика копеек
            final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);  //прогресс-бар

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
                    textMoneyCounter.setText(String.format("%.2f", bufMoney)+ " (руб.)");      //отображается увеличение копеек
                    textTimeCounter.setText(String.format("%02d", hourInTimer)+":"
                            +String.format("%02d", minInTimer)+":"                  //отображается изменение таймера
                            +String.format("%02d", secInTimer));
                    progressBar.setProgress(progressBar.getProgress()+1);
                }
            });
        }
    }

    //СВОРАЧИВАНИЕ ПРИЛОЖЕНИЯ

    @Override
    protected void onPause() {
        super.onPause();

        if (keyStart) {
            mainTimer.cancel();
            hideTime = System.currentTimeMillis() / 1000;
            SaveTempData();
            finish();
        }
    }

    //РАЗВОРАЧИВАНИЕ ПРИЛОЖЕНИЯ

    @Override
    protected void onResume() {
        super.onResume();
    }
    //НАЖАТИЕ КНОПКИ НАЗАД

    public void onBackPressed() {
        super.onBackPressed();

        if (keyStart) {
            mainTimer.cancel();
            hideTime = System.currentTimeMillis() / 1000;
            SaveTempData();
            finish();
        }
    }

    //ПРОЦЕДУРА ПОДСЧЁТА "ВСЕГО ВРЕМЕНИ"

    public String CalcTotalTime() {

        long bufSec = totalTime;
        long bufMin = 0;
        long bufHour = 0;

        if (bufSec>=60) {
            bufMin = bufSec/60;
            bufSec = bufSec % 60;
        }                                                       //подсчёт сколько прошло минут или даже часов
        if (bufMin>=60) {
            bufHour = bufMin/60;
            bufMin = bufMin % 60;
        }
        return "Всего времени: "+String.valueOf(bufHour)+" ч. "+String.valueOf(bufMin)+ " м. "+String.valueOf(bufSec)+" с.";
    }

    //ПРОЦЕДУРА СОХРАНЕНИЯ ДАННЫХ

    public void SaveTotalData(){
        SharedPreferences.Editor edit = setting.edit();
        edit.putLong(PREFERENCE_TOTAL_TIME, totalTime);
        edit.putFloat(PREFERENCE_TOTAL_MONEY, totalMoney);
        edit.putLong(PREFERENCE_TEMP_TIME, 0);
        edit.putFloat(PREFERENCE_TEMP_MONEY, 0);
        edit.putLong(PREFERENCE_TEMP_HOUR, 0);
        edit.putLong(PREFERENCE_TEMP_MIN, 0);
        edit.putLong(PREFERENCE_TEMP_SEC, 0);
        edit.apply();
    }

    //ПРОЦЕДУРА СОХРАНЕНИЯ ВРЕМЕННЫХ ДАННЫХ

    public void SaveTempData () {

        SharedPreferences.Editor edit = setting.edit();
        edit.putLong(PREFERENCE_TEMP_TIME, bufTime);
        edit.putFloat(PREFERENCE_TEMP_MONEY, bufMoney);
        edit.putLong(PREFERENCE_TEMP_HOUR, hourInTimer);
        edit.putLong(PREFERENCE_TEMP_MIN, minInTimer);
        edit.putLong(PREFERENCE_TEMP_SEC, secInTimer);
        edit.putLong(PREFERENCE_HIDE_TIME, hideTime);
        edit.apply();
    }

    //TOOLBAR

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    //ДЕЙСТВИЯ ТУЛБАРА

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {           //кнопка настроек
            if (keyStart) {
                Toast toast = Toast.makeText(getApplicationContext(), "Сначала остановите отсчёт!", Toast.LENGTH_SHORT);
                toast.show();
            }
            else {
                Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                startActivity(intent);
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }
}

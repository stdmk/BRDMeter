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


    long totalTime = 0;             //всего времени сегодня
    long totalTimeInTimer = 0;          //всего времени сегодня для вывода на таймер
    long allTime = 0;               //всего времени вообще
    long secCounter = 0;                //счётчик секунд
    float totalMoney = 0;           //всего денег сегодня
    float totalMoneyInTimer = 0;
    float allMoney = 0;             //всего денег вообще
    float bufMoney = 0;             //счётчик копеек
    int dayToday = 0;               //сегодняшний день
    int dayPrepay;                  //день аванса
    int daySalary;                  //день зарплаты
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
    float brdm;                     //скучность времени
    int timeKey;
    boolean keyMinimize;            //было ли приложение свёрнуто

    public static final String PREFERENCE = "preference";       //имя файла настроек
    public final String PREFERENCE_TOTAL_TIME = "totaltime";             //Всего времени сегодня
    public final String PREFERENCE_ALL_TIME = "alltime";                 //всего времени вообще
    public final String PREFERENCE_TOTAL_MONEY = "totalmoney";            //Всего денег
    public final String PREFERENCE_ALL_MONEY = "allmoney";                //Всего денег вообще
    public final String PREFERENCE_DAY_TODAY = "daytoday";                //сегодняшний день. Необходимо для учитывания статистики всего или сегодня
    public final String PREFERENCE_WORKDAY_BEGIN = "workdaybegin";          //время начала рабочего дня
    public final String PREFERENCE_WORKDAY_END = "workdayend";            //время конца рабочего дня
    public final String PREFERENCE_LUNCH_BEGIN = "lunchbegin";            //время начала обеда
    public final String PREFERENCE_LUNCH_END = "lunchend";              //время конца обеда
    public final String PREFERENCE_DAY_PREPAY = "dayprepay";              //день аванса
    public final String PREFERENCE_DAY_SALARY = "daysalary";              //день зарплаты
    public final String PREFERENCE_SALARY = "salary";                     //зарплата
    public final String PREFERENCE_TEMP_TIME = "temptime";                //временное время
    public final String PREFERENCE_TEMP_MONEY = "tempmoney";              //временные деньги(когда приложение сворачивается)
    public final String PREFERENCE_TEMP_HOUR = "temphour";                //временно часов
    public final String PREFERENCE_TEMP_MIN = "tempmin";                  //временно минут
    public final String PREFERENCE_TEMP_SEC = "tempsec";                  //временно секунд
    public final String PREFERENCE_HIDE_TIME = "hidetime";                //время перед сворачиванием
    public final String PREFERENCE_KEYDEF = "keydef";                     //были ли изменены настройки
    public final String PREFERENCE_KEYMINIMIZE = "keyminimize";

    private SharedPreferences setting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final CalcTime calcTime = new CalcTime();     //класс, переводящий секунды в минуты и часы
        Preference preference = new Preference();     //класс работы с настройками

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
        allTime = setting.getLong(PREFERENCE_ALL_TIME, 0);
        totalMoney = setting.getFloat(PREFERENCE_TOTAL_MONEY, 0);
        allMoney = setting.getFloat(PREFERENCE_ALL_MONEY, 0);
        dayToday = setting.getInt(PREFERENCE_DAY_TODAY, 0);
        workDayBegin = setting.getInt(PREFERENCE_WORKDAY_BEGIN, 0);         //берём настройки из файла
        workDayEnd = setting.getInt(PREFERENCE_WORKDAY_END, 0);
        lunchBegin = setting.getInt(PREFERENCE_LUNCH_BEGIN, 0);
        lunchEnd = setting.getInt(PREFERENCE_LUNCH_END, 0);
        dayPrepay = setting.getInt(PREFERENCE_DAY_PREPAY, 0);
        daySalary = setting.getInt(PREFERENCE_DAY_SALARY, 0);
        salary = setting.getFloat(PREFERENCE_SALARY, 0);
        tempMoney = setting.getFloat(PREFERENCE_TEMP_MONEY, 0);
        keyDef = setting.getBoolean(PREFERENCE_KEYDEF, false);
        keyMinimize = setting.getBoolean(PREFERENCE_KEYMINIMIZE, false);

        timeNow = calcTime.TimeNowSec();   //текущее время

        Calendar calendar = Calendar.getInstance();

        totalTimeInTimer = totalTime;

        if (!keyDef){
            Intent intent = new Intent(MainActivity.this, SettingActivity.class);       //нет - заупускаем настройки
            startActivity(intent);
            finish();
        }

        if (dayToday == 0) {
            dayToday = calendar.get(Calendar.DAY_OF_MONTH);
        }

        if (dayToday<calendar.get(Calendar.DAY_OF_MONTH)){              //обнуляет статистику "На сегодня"
            dayToday = calendar.get(Calendar.DAY_OF_MONTH);

            preference.SaveAllData();

            totalTimeInTimer = 0;
            totalTime = 0;
            totalMoney = 0;

            textTotalTime.setText("Всего времени: 0 ч. 0 м. 0 с.");
            textTotalMoney.setText("Всего денег(руб): ");
        }

        tickInMoney = salary / countDayInMonth / countHoursIntDay / 60 / 60;    //подсчёт денег в секунду
        mainTimer = new Timer();

        if (keyMinimize) {                                                           //если приложение было свёрнуто

            keyMinimize = false;
            hourInTimer = setting.getLong(PREFERENCE_TEMP_HOUR, 0);         //значение часов на таймере перед сворачиванием
            minInTimer = setting.getLong(PREFERENCE_TEMP_MIN, 0);           //значение минут на таймере перед сворачиванием
            secInTimer = setting.getLong(PREFERENCE_TEMP_SEC, 0);           //значение секунд на таймере перед сворачиванием
            bufMoney = setting.getFloat(PREFERENCE_TEMP_MONEY, 0);          //значение копеек перед сворачиванием
            secCounter = setting.getLong(PREFERENCE_TEMP_TIME, 0);          //значение насчитанных секунд перед сворачиванем
            hideTime = setting.getLong(PREFERENCE_HIDE_TIME, 0);            //время в секундах, когда приложение было свёрнуто

            if (timeNow < workDayEnd) {   //если рабочий день ещё идёт
                if (hideTime < lunchBegin && timeNow > lunchBegin) {
                    if (timeNow < lunchEnd) {
                        secInTimer = secCounter + (lunchBegin - hideTime);
                    } else {
                        secInTimer = secCounter + ((lunchBegin - hideTime)+(timeNow - lunchEnd));
                    }
                    calcTime.InTimer();
                    textTimeCounter.setText(String.format("%02d", hourInTimer) + ":"
                            + String.format("%02d", minInTimer) + ":"                   //выводим временные данные
                            + String.format("%02d", secInTimer));
                    textMoneyCounter.setText(String.valueOf(bufMoney) + " (руб.)");

                    btnStart.setEnabled(false);
                    btnStop.setEnabled(true);

                    mainTimerTask = new MainTimerTask();
                    mainTimer.schedule(mainTimerTask, 1000, 1000);      //запуск таймера

                } else {
                    secInTimer = secCounter + (timeNow - hideTime);              //значение сколько секунд приложение было свёрнуто+сколько насчитало перед сворачиванием

                    bufMoney = bufMoney + (tickInMoney * secInTimer);   //подсчёт копеек, которые прибавились, пока приложение было свёрнуто

                    calcTime.InTimer();     //перевод секунд в минуты и часы на таймере

                    textTimeCounter.setText(String.format("%02d", hourInTimer) + ":"
                            + String.format("%02d", minInTimer) + ":"                   //выводим временные данные
                            + String.format("%02d", secInTimer));
                    textMoneyCounter.setText(String.valueOf(bufMoney) + " (руб.)");

                    btnStart.setEnabled(false);
                    btnStop.setEnabled(true);

                    mainTimerTask = new MainTimerTask();
                    mainTimer.schedule(mainTimerTask, 1000, 1000);      //запуск таймера
                }
            }
            else {  //если рабочий день закончился
                secInTimer = secCounter + (workDayEnd - hideTime);              //значение сколько секунд приложение было свёрнуто+сколько насчитало перед сворачиванием
                totalTime = totalTime + secInTimer;
                bufMoney = bufMoney + (tickInMoney * secInTimer);            //подсчёт копеек, которые прибавились, пока приложение было свёрнуто

                totalMoneyInTimer = secInTimer + totalTime;

                calcTime.InTimer();     //перевод секунд в минуты и часы на таймере

                totalMoney = totalMoney + bufMoney;

                dayToday = calendar.get(Calendar.DAY_OF_MONTH);

                preference.SaveAllData();
                preference.SaveTotalData();

                secInTimer = 0;
                minInTimer = 0;
                hourInTimer = 0;

                preference.ResetTempData();

                btnStart.setEnabled(true);
                btnStop.setEnabled(false);

                mainTimer.cancel();         //стоп таймера
                mainTimer = null;
                mainTimer = new Timer();

                keyStart = false;
                Toast toast = Toast.makeText(getApplicationContext(), "Похоже вы забыли остановить отсчёт" + "\n" + "Скукометр остановлен, время подсчитано", Toast.LENGTH_SHORT);
                toast.show();
            }
        } else {
            secInTimer = 0;
            minInTimer = 0;
            hourInTimer = 0;
            btnStop.setEnabled(false);
        }

        int buf = calcTime.TimeNowSec();
        buf = buf - workDayBegin;
        progressBar.setMax(workDayEnd - workDayBegin);
        progressBar.setProgress(buf);           //установки прогресс-бара

        textTotalTime.setText("Всего времени: " + calcTime.InString(totalTimeInTimer));
        textTotalMoney.setText("Всего денег(руб): " + String.format("%.2f", totalMoneyInTimer * tickInMoney));

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
                StopTimer();
            }
        });

        //НАЖАТИЕ НА ПРОГРЕСС-БАР

        progressBar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                int bufTime = calcTime.TimeNowSec();
                if (bufTime>workDayBegin && bufTime <workDayEnd) {
                    int buf = progressBar.getMax() - progressBar.getProgress();
                    Toast toast = Toast.makeText(getApplicationContext(), "До конца рабочего дня " + calcTime.InString(buf), Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });
    }

    //ДЕЙСТВИЯ ПО ТАЙМЕРУ

    class MainTimerTask extends TimerTask {
        @Override
        public void run() {
            final Button btnStart = (Button) findViewById(R.id.buttonStart);            //кнопка старт
            final Button btnStop = (Button) findViewById(R.id.buttonStop);              //кнопка стоп
            final TextView textMoneyCounter = (TextView) findViewById(R.id.textView3);  //вывод счётчика времени
            final TextView textTimeCounter = (TextView) findViewById(R.id.textView6);   //вывод счётчика копеек
            final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);  //прогресс-бар
            final TextView testText = (TextView) findViewById(R.id.textView7);

            CalcTime calcTime = new CalcTime();
            final int bufTime = calcTime.TimeNowSec();

            if (bufTime>lunchBegin && bufTime<lunchEnd) {
                timeKey = 1;
            }
            else if (bufTime < workDayBegin) {
                timeKey = 2;
            }
            else if (bufTime > workDayEnd) {
                timeKey = 3;
            }
            else {

                secInTimer = secInTimer + 1;
                if (secInTimer == 60) {
                    secInTimer = 0;
                    minInTimer = minInTimer + 1;            //увеличение секунд, минут, часов
                    if (minInTimer == 60) {
                        minInTimer = 0;
                        hourInTimer = hourInTimer + 1;
                    }
                }

                bufMoney = bufMoney + tickInMoney;    //увеличиваются копейки
                timeKey = 0;
            }

            runOnUiThread(new Runnable() {      //доступ к изменению компонентов
            @Override
            public void run() {
                switch (timeKey) {
                    case 0:
                        textMoneyCounter.setText(String.format("%.2f", bufMoney) + " (руб.)");      //отображается увеличение копеек
                        textTimeCounter.setText(String.format("%02d", hourInTimer) + ":"
                                + String.format("%02d", minInTimer) + ":"                  //отображается изменение таймера
                                + String.format("%02d", secInTimer));
                        progressBar.setProgress(progressBar.getProgress() + 1);
                        break;

                    case 1:
                        textMoneyCounter.setText("Время обеда!");
                        break;

                    case 2:
                        CalcTime calcTime = new CalcTime();
                        Toast toast = Toast.makeText(getApplicationContext(), "До начала рабочего дня" + calcTime.InString(workDayBegin - bufTime), Toast.LENGTH_SHORT);
                        toast.show();
                        mainTimer.cancel();         //стоп таймера
                        mainTimer = null;
                        mainTimer = new Timer();
                        btnStart.setEnabled(true);
                        btnStop.setEnabled(false);
                        keyStart = false;
                        break;

                    case 3:
                        toast = Toast.makeText(getApplicationContext(), "Рабочий день окончен", Toast.LENGTH_SHORT);
                        toast.show();
                        if (keyMinimize) {
                            mainTimer.cancel();         //стоп таймера
                            mainTimer = null;
                            mainTimer = new Timer();
                            btnStart.setEnabled(true);
                            btnStop.setEnabled(false);
                            keyStart = false;
                        } else {
                            StopTimer();
                        }
                        break;
                }
            }
            });
        }
    }

    //РАЗВОРАЧИВАНИЕ ПРИЛОЖЕНИЯ

    @Override
    protected void onResume() {
        super.onResume();
    }

    //СВОРАЧИВАНИЕ ПРИЛОЖЕНИЯ

    @Override
    protected void onPause() {
        super.onPause();

        if (keyStart) {
            Preference preference = new Preference();
            CalcTime calcTime = new CalcTime();
            keyMinimize = true;
            mainTimer.cancel();
            hideTime = calcTime.TimeNowSec();
            secCounter = calcTime.TimerSecValue();
            preference.SaveTempData();
        }
        finish();
    }

    //НАЖАТИЕ КНОПКИ НАЗАД

    public void onBackPressed() {
        super.onBackPressed();

        onPause();
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

        if (id == R.id.action_statistic) {          //кнопка статистики
            final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
            final TextView testText = (TextView) findViewById(R.id.textView7);

            String buf = ""; brdm = 0;

            //РАССЧЁТ СКУЧНОСТИ ДНЯ
            CalcTime calcTime = new CalcTime();
            int bufTime = calcTime.TimeNowSec();
            if (bufTime>workDayBegin && bufTime <workDayEnd) {
                brdm = ((float) totalTime / (float) progressBar.getMax()) * 100;                 //подсчёт скучности дня

                buf = "Скучность дня: " + String.valueOf((int) brdm) + "%" + "\n";
            }

            //РАССЧЁТ ДНЕЙ ДО АВАНСА И ЗАРПЛАТЫ

            Calendar calendar = Calendar.getInstance();
            int dayInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);  //дней в месяце
            int currDayMonth = calendar.get(Calendar.DAY_OF_MONTH);             //сегодняшний день
            int bufDays2;   //дней до ...

            if (currDayMonth < dayPrepay) {                         //если сегодняшний день < дня аванса
                bufDays2 = dayPrepay - currDayMonth;      //просто вычитаем из дня аванса сегодняшний
            }
            else {
                bufDays2 = dayInMonth - currDayMonth + dayPrepay; //иначе узнаём разницу до конца месяца + число дней до аванса
            }

            buf = buf + "Дней до аванса: " + String.valueOf(bufDays2) + "\n";

            if (currDayMonth < daySalary) {
                bufDays2 = daySalary - currDayMonth;
            }
            else {
                bufDays2 = dayInMonth - currDayMonth + daySalary;
            }

            buf = buf + "Дней до зарплаты: " + String.valueOf(bufDays2) + "\n";

            //СТАТИСТИКА ВСЕГО ВРЕМЕНИ И ДЕНЕГ ВООБЩЕ

            buf = buf + "Всего времени: " + calcTime.InString(allTime + calcTime.TimerSecValue()) + "\n";
            buf = buf + "Всего денег (руб): " + Math.round(allMoney + bufMoney) + "\n";

            //ФОРМИРОВАНИЕ РАДОСТНОГО СООБЩЕНИЯ

            if (dayToday == dayPrepay) {
                buf = buf + "СЕГОДНЯ АВАНС! :) \n";
            }

            if (dayToday == daySalary) {
                buf = buf + "СЕГОДНЯ ЗАРПЛАТА! :) \n";
            }

            //ФОРМИРОВАНИЕ СТРОКИ И ВЫВОД

            Toast toast = Toast.makeText(getApplicationContext(), buf, Toast.LENGTH_LONG);
            toast.show();
        }

        return super.onOptionsItemSelected(item);
    }

    //КЛАСС ПОДСЧЁТА РАЗЛИЧНОГО ВРЕМЕНИ

    class CalcTime {

        //ПЕРЕВОД СЕКУНД В МИНУТЫ И ЧАСЫ ДЛЯ ВЫВОДА В СТРОКУ

        String InString(long bufSec) {

            long bufMin = 0;
            long bufHour = 0;

            if (bufSec>=60) {
                bufMin = bufSec / 60;
                bufSec = bufSec % 60;
            }                                                       //подсчёт сколько прошло минут или даже часов
            if (bufMin>=60) {
                bufHour = bufMin/60;
                bufMin = bufMin % 60;
            }
            return String.valueOf(bufHour) + " ч. " + String.valueOf(bufMin)+ " м. "+String.valueOf(bufSec)+" с.";
        }

        //ПЕРЕВОДИТ СЕКУНДЫ В МИНУТЫ И ЧАСЫ + ЗАБЫИВАЕТ ЭТО В ЗНАЧЕНИЕ ТАЙМЕРА

        void InTimer () {
            if (secInTimer >= 60) {
                minInTimer = minInTimer + secInTimer / 60;
                secInTimer = secInTimer % 60;
            }                                                       //подсчёт сколько прошло минут или даже часов
            if (minInTimer >= 60) {
                hourInTimer = hourInTimer + minInTimer / 60;
                minInTimer = minInTimer % 60;
            }
        }

        //ФУНКЦИЯ ВОЗВРАЩАЕТ ТЕКУЩЕЕ ВРЕМЯ В СЕКУНДАХ

        int TimeNowSec() {
            Calendar calendar = Calendar.getInstance();
            return (calendar.get(Calendar.HOUR_OF_DAY) * 60 * 60) + (calendar.get(Calendar.MINUTE) * 60) + calendar.get(Calendar.SECOND);
        }

        //ВЫВОДИТ ТЕКУЩЕЕ ЗНАЧЕНИЕ ТАЙМЕРА В СЕКУНДАХ

        long TimerSecValue() {
            return (hourInTimer * 60 * 60) + (minInTimer * 60) + secInTimer;
        }
    }

    //ПРОЦЕДУРА СОХРАНЕНИЯ ДАННЫХ

    class Preference {
        void SaveTotalData(){
            SharedPreferences.Editor edit = setting.edit();
            edit.putLong(PREFERENCE_TOTAL_TIME, totalTime);
            edit.putFloat(PREFERENCE_TOTAL_MONEY, totalMoney);
            edit.putLong(PREFERENCE_TEMP_TIME, 0);
            edit.putFloat(PREFERENCE_TEMP_MONEY, 0);
            edit.putLong(PREFERENCE_TEMP_HOUR, 0);
            edit.putLong(PREFERENCE_TEMP_MIN, 0);
            edit.putLong(PREFERENCE_TEMP_SEC, 0);
            edit.putInt(PREFERENCE_DAY_TODAY, dayToday);
            edit.apply();
        }

        //ПРОЦЕДУРА СОХРАНЕНИЯ ВРЕМЕННЫХ ДАННЫХ

        void SaveTempData () {

            SharedPreferences.Editor edit = setting.edit();
            edit.putBoolean(PREFERENCE_KEYMINIMIZE, keyMinimize);
            edit.putLong(PREFERENCE_TEMP_TIME, secCounter);
            edit.putFloat(PREFERENCE_TEMP_MONEY, bufMoney);
            edit.putLong(PREFERENCE_TEMP_HOUR, hourInTimer);
            edit.putLong(PREFERENCE_TEMP_MIN, minInTimer);
            edit.putLong(PREFERENCE_TEMP_SEC, secInTimer);
            edit.putLong(PREFERENCE_HIDE_TIME, hideTime);
            edit.apply();
        }

        //ПРОЦЕДУРА СБРОСА ВРЕМЕННЫХ ДАННЫХ

        void ResetTempData() {
            SharedPreferences.Editor edit = setting.edit();
            edit.putBoolean(PREFERENCE_KEYMINIMIZE, keyMinimize);
            edit.putLong(PREFERENCE_TEMP_TIME, 0);
            edit.putFloat(PREFERENCE_TEMP_MONEY, 0);
            edit.putLong(PREFERENCE_TEMP_HOUR, 0);
            edit.putLong(PREFERENCE_TEMP_MIN, 0);
            edit.putLong(PREFERENCE_TEMP_SEC, 0);
            edit.putLong(PREFERENCE_HIDE_TIME, 0);
            edit.apply();
        }

        //ПРОЦЕДУРА СОХРАНЕНИЯ "Всего времени" И "Всего денег"

        void SaveAllData() {
            SharedPreferences.Editor edit = setting.edit();

            allTime = allTime + totalTime;
            allMoney = allMoney + totalMoney;

            edit.putLong(PREFERENCE_ALL_TIME, allTime);
            edit.putFloat(PREFERENCE_ALL_MONEY, allMoney);
            edit.putInt(PREFERENCE_DAY_TODAY, dayToday);
            edit.apply();
        }
    }

    //ПРОЦЕДУРА СТОПА ТАЙМЕРА

    public void StopTimer () {
        final Button btnStart = (Button) findViewById(R.id.buttonStart);            //кнопка старт
        final Button btnStop = (Button) findViewById(R.id.buttonStop);              //кнопка стоп
        final TextView textTotalTime = (TextView) findViewById(R.id.textView);      //вывод всего времени
        final TextView textTotalMoney = (TextView) findViewById(R.id.textView2);    //вывод всего денег
        final TextView textMoneyCounter = (TextView) findViewById(R.id.textView3);  //вывод счётчика времени
        final TextView textTimeCounter = (TextView) findViewById(R.id.textView6);   //вывод счётчика копеек

        CalcTime calcTime = new CalcTime();
        Preference preference = new Preference();

        keyMinimize = false;

        mainTimer.cancel();         //стоп таймера
        mainTimer = null;
        mainTimer = new Timer();

        totalTime = totalTime + calcTime.TimerSecValue();    //всего времени подсчёт
        totalMoney = totalMoney + bufMoney;     //всего денег подсчёт

        textTimeCounter.setText("00:00:00");    //обнуляем вывод таймера

        btnStart.setEnabled(true);
        btnStop.setEnabled(false);

        totalTimeInTimer = totalTimeInTimer + totalTime;

        textTotalTime.setText("Всего времени: " + calcTime.InString(totalTimeInTimer));

        textTotalMoney.setText("Всего денег(руб): " + String.format("%.2f", totalTimeInTimer * tickInMoney));
        textMoneyCounter.setText("0.0 (руб.)");

        preference.ResetTempData();
        preference.SaveTotalData();
        preference.SaveAllData();

        bufMoney = 0;
        secInTimer = 0;
        minInTimer = 0;
        hourInTimer = 0;
        keyStart = false;

        totalTime = 0;
        totalMoney = 0;

    }

}

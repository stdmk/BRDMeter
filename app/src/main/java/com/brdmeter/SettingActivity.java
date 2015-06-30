package com.brdmeter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.content.SharedPreferences;
import android.widget.Toast;


public class SettingActivity extends Activity {

    private SharedPreferences setting;

    public static final String PREFERENCE = "preference";                  //имя файла настроек
    public final String PREFERENCE_WORKDAY_BEGIN = "workdaybegin";          //время начала рабочего дня
    public final String PREFERENCE_WORKDAY_END = "workdayend";            //время конца рабочего дня
    public final String PREFERENCE_LUNCH_BEGIN = "lunchbegin";            //время начала обеда
    public final String PREFERENCE_LUNCH_END = "lunchend";               //время конца обеда
    public final String PREFERENCE_DAY_PREPAY = "dayprepay";              //день аванса
    public final String PREFERENCE_DAY_SALARY = "daysalary";              //день зарплаты
    public final String PREFERENCE_SALARY = "salary";                     //зарплата
    public final String PREFERENCE_KEYDEF = "keydef";                     //были ли заполнены настройки

    int workDayBegin;
    int workDayEnd;
    int lunchBegin;
    int lunchEnd;
    int dayPrepay;
    int daySalary;
    float salary;
    boolean bufKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        final TextView textView = (TextView)findViewById(R.id.textView5);
        final Button btnSave = (Button)findViewById(R.id.btnSave);
        final Button btnBack = (Button)findViewById(R.id.btnBack);
        final TimePicker timePicker = (TimePicker)findViewById(R.id.timePicker);
        final EditText editText = (EditText)findViewById(R.id.editText);
        final TextView testText = (TextView)findViewById(R.id.textView8);

        ReadSetting();
        if (!bufKey) {
            DefaultSetting();
            ReadSetting();
        }

        timePicker.setIs24HourView(true);                 //24-часовой формат

        Sec2HM(workDayBegin);   //переводит секунды в часы и минуты + выводит данные в таймпикер

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int i, j;

                //НАЧАЛО РАБОЧЕГО ДНЯ

                if (textView.getText() == getString(R.string.WorkDayBegin)) {
                    i = timePicker.getCurrentHour();        //берём введённые данные
                    j = timePicker.getCurrentMinute();
                    workDayBegin = i * 60 * 60 + j * 60;    //переводим в секунды (у нас ведь всё в секундах)
                    textView.setText(getString(R.string.WorkDayEnd));   //меняем текст

                    Sec2HM(workDayEnd);

                    btnBack.setEnabled(true);
                    return;
                }

                //КОНЕЦ РАБОЧЕГО ДНЯ

                if (textView.getText() == getString(R.string.WorkDayEnd)) {
                    i = timePicker.getCurrentHour();
                    j = timePicker.getCurrentMinute();
                    workDayEnd = i * 60 * 60 + j * 60;
                    textView.setText(getString(R.string.LunchBegin));

                    Sec2HM(lunchBegin);

                    return;
                }

                //НАЧАЛО ОБЕДА

                if (textView.getText() == getString(R.string.LunchBegin)) {
                    i = timePicker.getCurrentHour();
                    j = timePicker.getCurrentMinute();
                    lunchBegin = i * 60 * 60 + j * 60;
                    textView.setText(getString(R.string.LunchEnd));

                    Sec2HM(lunchEnd);
                    return;
                }

                //КОНЕЦ ОБЕДА

                if (textView.getText() == getString(R.string.LunchEnd)) {
                    i = timePicker.getCurrentHour();
                    j = timePicker.getCurrentMinute();
                    lunchEnd = i * 60 * 60 + j * 60;
                    textView.setText(getString(R.string.DayPrepay));
                    timePicker.setVisibility(View.INVISIBLE);       //убираем выбор времени и ставим эдит текст
                    editText.setVisibility(View.VISIBLE);           //дальше вводится день аванса
                    editText.requestFocus();
                    editText.setText(String.valueOf(dayPrepay));
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);   //программный вызов
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);                                         //клавиатуры
                    return;
                }

                //ВВОД ДНЯ АВАНСА

                if (textView.getText() == getString(R.string.DayPrepay)) {
                    dayPrepay = Integer.parseInt(editText.getText().toString());

                    if (dayPrepay <= 0) {
                        Toast toast = Toast.makeText(getApplicationContext(), "Проверьте правильность ввода данных", Toast.LENGTH_SHORT);
                        toast.show();
                        return;
                    }
                    if (dayPrepay > 31) {
                        Toast toast = Toast.makeText(getApplicationContext(), "Проверьте правильность ввода данных", Toast.LENGTH_SHORT);
                        toast.show();
                        return;
                    }

                    textView.setText(R.string.DaySalary);
                    editText.setText(String.valueOf(daySalary));
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                    return;
                }

                //ВВОД ДНЯ ЗАРПЛАТЫ

                if (textView.getText() == getString(R.string.DaySalary)) {
                    daySalary = Integer.parseInt(editText.getText().toString());

                    if (daySalary <= 0) {
                        Toast toast = Toast.makeText(getApplicationContext(), "Проверьте правильность ввода данных", Toast.LENGTH_SHORT);
                        toast.show();
                        return;
                    }
                    if (daySalary > 31) {
                        Toast toast = Toast.makeText(getApplicationContext(), "Проверьте правильность ввода данных", Toast.LENGTH_SHORT);
                        toast.show();
                        return;
                    }

                    textView.setText(R.string.Salary);
                    editText.setText(String.valueOf((int) salary));
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                    btnSave.setText("Сохранить");
                    return;
                }

                //ВВОД ЗАРПЛАТЫ

                if (textView.getText() == getString(R.string.Salary)) {
                    salary = Integer.parseInt(editText.getText().toString());
                    bufKey = true;
                    SaveTotalData();    //введены последние необходимые данные, сохраняем
                    Intent intent = new Intent(SettingActivity.this, MainActivity.class);
                    startActivity(intent);      //переходим на главное окно
                    finish();
                }
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final TextView textView = (TextView)findViewById(R.id.textView5);
                final TimePicker timePicker = (TimePicker)findViewById(R.id.timePicker);
                final EditText editText = (EditText)findViewById(R.id.editText);

                //ПЕРВОЕ ОКОШКО(ВОЗВРАЩАЕТ НА ГЛАВНЫЙ ЭКРАН)

                if (textView.getText() == getString(R.string.WorkDayBegin)) {
                    Intent intent = new Intent(SettingActivity.this, MainActivity.class);
                    startActivity(intent);
                }

                //ВВОД КОНЦА РАБОЧЕГО ДНЯ(ПЕРЕХОД К НАЧАЛУ РАБОЧЕГО ДНЯ)

                if (textView.getText() == getString(R.string.WorkDayEnd)) {
                    textView.setText(getString(R.string.WorkDayBegin));
                    Sec2HM(workDayBegin);
                    btnBack.setEnabled(false);
                }

                //ВВОД НАЧАЛА ОБЕДА(ПЕРЕХОД К КОНЦУ РАБОЧЕГО ДНЯ)

                if (textView.getText() == getString(R.string.LunchBegin)) {
                    textView.setText(getString(R.string.WorkDayEnd));
                    Sec2HM(workDayEnd);
                }

                //ВВОД КОНЦА ОБЕДА(ПЕРЕХОД К НАЧАЛУ ОБЕДА)

                if (textView.getText() == getString(R.string.LunchEnd)){
                    textView.setText(getString(R.string.LunchBegin));
                    Sec2HM(lunchBegin);
                }

                //ВВОД ДНЯ АВАНСА(ПЕРЕХОД К КОНЦУ ОБЕДА)

                if (textView.getText() == getString(R.string.DayPrepay)) {
                    textView.setText(getString(R.string.LunchEnd));
                    editText.setVisibility(View.INVISIBLE);
                    timePicker.setVisibility(View.VISIBLE);
                    Sec2HM(lunchEnd);
                }

                //ВВОД ДНЯ ЗАРПЛАТЫ(ПЕРЕХОД КО ДНЮ АВАНСА)

                if (textView.getText() == getString(R.string.DaySalary)) {
                    textView.setText(getString(R.string.DayPrepay));
                    editText.setText(String.valueOf(dayPrepay));
                }

                //ВВОД ЗАРПЛАТЫ(ПЕРЕХОД К ДНЮ ЗАРПЛАТЫ)

                if (textView.getText() == getString(R.string.Salary)) {
                    textView.setText(getString(R.string.DaySalary));
                    editText.setText(String.valueOf(daySalary));
                    btnSave.setText("Вперёд");
                }
            }
        });

    }

    //ОБРАБОТКА КЛАВИШИ НАЗАД

    public void onBackPressed() {
        super.onBackPressed();

    }

    //ПРОЦЕДУРА СОХРАНЕНИЯ ДАННЫХ

    public void SaveTotalData(){
        SharedPreferences.Editor edit = setting.edit();
        edit.putInt(PREFERENCE_WORKDAY_BEGIN, workDayBegin);
        edit.putInt(PREFERENCE_WORKDAY_END, workDayEnd);
        edit.putInt(PREFERENCE_LUNCH_BEGIN, lunchBegin);
        edit.putInt(PREFERENCE_LUNCH_END, lunchEnd);
        edit.putInt(PREFERENCE_DAY_PREPAY, dayPrepay);
        edit.putInt(PREFERENCE_DAY_SALARY, daySalary);
        edit.putFloat(PREFERENCE_SALARY, salary);
        edit.putBoolean(PREFERENCE_KEYDEF, bufKey);
        edit.apply();
    }

    //ПРОЦЕДУРА ЧТЕНИЯ НАСТРОЕК

    public void ReadSetting() {
        setting = getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE);
        workDayBegin = setting.getInt(PREFERENCE_WORKDAY_BEGIN, 0);
        workDayEnd = setting.getInt(PREFERENCE_WORKDAY_END, 0);
        lunchBegin = setting.getInt(PREFERENCE_LUNCH_BEGIN, 0);
        lunchEnd = setting.getInt(PREFERENCE_LUNCH_END, 0);
        dayPrepay = setting.getInt(PREFERENCE_DAY_PREPAY, 0);
        daySalary = setting.getInt(PREFERENCE_DAY_SALARY, 0);
        salary = setting.getFloat(PREFERENCE_SALARY, 0);
        bufKey = setting.getBoolean(PREFERENCE_KEYDEF, false);
    }

    //ПРОЦЕДУРА СБРОСА НАСТРОЕК

    public void DefaultSetting() {
        SharedPreferences.Editor edit = setting.edit();
        edit.putInt(PREFERENCE_WORKDAY_BEGIN, 28800);
        edit.putInt(PREFERENCE_WORKDAY_END, 61200);
        edit.putInt(PREFERENCE_LUNCH_BEGIN, 43200);
        edit.putInt(PREFERENCE_LUNCH_END, 46800);
        edit.putInt(PREFERENCE_DAY_PREPAY, 15);
        edit.putInt(PREFERENCE_DAY_SALARY, 1);
        edit.putFloat(PREFERENCE_SALARY, 10000);
        edit.putBoolean(PREFERENCE_KEYDEF, false);
        edit.apply();
    }

    //ПРОЦЕДУРА ПЕРЕВОДА СЕКУНД В ЧАСЫ И ВЫВОД ПОЛУЧЕННЫХ ДАННЫХ НА ЭКРАН

    public void Sec2HM (int sec) {
        final TimePicker timePicker = (TimePicker)findViewById(R.id.timePicker);

        int hour = sec / 60 / 60;                       //из всех секунд вычитаем получившиеся целые часы, получая минуты
        int min = (sec - (hour * 60 * 60)) / 60;        //перевод из секунд в целые часы
        timePicker.setCurrentHour(hour);
        timePicker.setCurrentMinute(min);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_setting, menu);
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

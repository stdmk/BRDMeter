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


public class SettingActivity extends Activity {

    private SharedPreferences setting;

    public static final String PREFERENCE = "preference";                  //имя файла настроек
    public final String PREFERENCE_WORKDAY_BEGIN = "workdaybegin";          //время начала рабочего дня
    public final String PREFERENCE_WORKDAY_END = "workdayend";            //время конца рабочего дня
    public final String PREFERENCE_LUNCH_BEGIN = "lunchbegin";            //время начала обеда
    public final String PREFERENCE_LUNCH_END = "lunchbegin";              //время конца обеда
    public final String PREFERENCE_SALARY = "salary";                     //зарплата

    int workDayBegin;
    int workDayEnd;
    int lunchBegin;
    int lunchEnd;
    float salary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        final TextView textView = (TextView)findViewById(R.id.textView5);
        final Button btnSave = (Button)findViewById(R.id.btnSave);
        final TimePicker timePicker = (TimePicker)findViewById(R.id.timePicker);
        final EditText editText = (EditText)findViewById(R.id.editText);

        timePicker.setIs24HourView(true);       //24-часовой формат
        timePicker.setCurrentHour(8);           //начальное
        timePicker.setCurrentMinute(0);         //значение

        setting = getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int i,j;

                //НАЧАЛО РАБОЧЕГО ДНЯ

                if (textView.getText() == getString(R.string.WorkDayBegin)) {
                    i = timePicker.getCurrentHour();        //берём введённые данные
                    j = timePicker.getCurrentMinute();
                    workDayBegin = i * 60 * 60 + j * 60;    //переводим в секунды (у нас ведь всё в секундах)
                    textView.setText(getString(R.string.WorkDayEnd));   //меняем текст
                    timePicker.setCurrentHour(17);      //ставим таймпикер на следующее положение
                    timePicker.setCurrentMinute(0);    //дальше всё аналогично
                    return;
                }

                //КОНЕЦ РАБОЧЕГО ДНЯ

                if (textView.getText() == getString(R.string.WorkDayEnd)) {
                    i = timePicker.getCurrentHour();
                    j = timePicker.getCurrentMinute();
                    workDayEnd = i * 60 * 60 + j * 60;
                    textView.setText(getString(R.string.LunchBegin));
                    timePicker.setCurrentHour(13);
                    timePicker.setCurrentMinute(0);
                    return;
                }

                //НАЧАЛО ОБЕДА

                if (textView.getText() == getString(R.string.LunchBegin)) {
                    i = timePicker.getCurrentHour();
                    j = timePicker.getCurrentMinute();
                    lunchBegin = i * 60 * 60 + j * 60;
                    textView.setText(getString(R.string.LunchEnd));
                    timePicker.setCurrentHour(14);
                    timePicker.setCurrentMinute(0);
                    return;
                }

                //КОНЕЦ ОБЕДА

                if (textView.getText() == getString(R.string.LunchEnd)) {
                    i = timePicker.getCurrentHour();
                    j = timePicker.getCurrentMinute();
                    lunchEnd = i * 60 * 60 + j * 60;
                    textView.setText(getString(R.string.Salary));
                    timePicker.setVisibility(View.INVISIBLE);       //убираем выбор времени и ставим эдит текст
                    editText.setVisibility(View.VISIBLE);           //дальше вводится зарплата
                    editText.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);   //программный вызов
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);                                         //клавиатуры
                    return;
                }

                //ВВОД ЗАРПЛАТЫ

                if (textView.getText() == getString(R.string.Salary)) {
                    salary = Integer.parseInt(editText.getText().toString());
                    SaveTotalData();    //введены последние необходимые данные, сохраняем
                    Intent intent = new Intent(SettingActivity.this, MainActivity.class);
                    startActivity(intent);      //переходим на главное окно
                }
            }
        });

    }

    //ОБРАБОТКА КЛАВИШИ НАЗАД

    public void onBackPressed() {
        super.onBackPressed();

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
            timePicker.setCurrentHour(8);
            timePicker.setCurrentMinute(0);
        }

        //ВВОД НАЧАЛА ОБЕДА(ПЕРЕХОД К КОНЦУ РАБОЧЕГО ДНЯ)

        if (textView.getText() == getString(R.string.LunchBegin)) {
            textView.setText(getString(R.string.WorkDayEnd));
            timePicker.setCurrentHour(17);
            timePicker.setCurrentMinute(0);
        }

        //ВВОД КОНЦА ОБЕДА(ПЕРЕХОД К НАЧАЛУ ОБЕДА)

        if (textView.getText() == getString(R.string.LunchEnd)){
            textView.setText(getString(R.string.LunchBegin));
            timePicker.setCurrentHour(13);
            timePicker.setCurrentMinute(0);
        }

        //ВВОД ЗАРПЛАТЫ(ПЕРЕХОД К КОНЦУ ОБЕДА)

        if (textView.getText() == getString(R.string.Salary)) {
            textView.setText(getString(R.string.LunchEnd));
            timePicker.setCurrentHour(14);
            timePicker.setCurrentMinute(0);
            editText.setVisibility(View.INVISIBLE);
            timePicker.setVisibility(View.VISIBLE);
        }
    }

    //ПРОЦЕДУРА СОХРАНЕНИЯ ДАННЫХ

    public void SaveTotalData(){
        SharedPreferences.Editor edit = setting.edit();
        edit.putInt(PREFERENCE_WORKDAY_BEGIN, workDayBegin);
        edit.putInt(PREFERENCE_WORKDAY_END, workDayEnd);
        edit.putInt(PREFERENCE_LUNCH_BEGIN, lunchBegin);
        edit.putInt(PREFERENCE_LUNCH_END, lunchBegin);
        edit.putFloat(PREFERENCE_SALARY, salary);
        edit.apply();
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

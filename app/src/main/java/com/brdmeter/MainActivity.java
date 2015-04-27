package com.brdmeter;

import android.app.Activity;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
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
    int salary = 23000;            //зарплата
    int countDayInMonth = 30;      //рабочих дней в месяце
    int countHoursIntDay = 8;      //рабочих часов в день
    float kostylInMoney = 0;         //костыль, необходимый для верного подсчёта денег


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

        kostylInMoney = salary / countDayInMonth / countHoursIntDay / 60 / 60;

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
                bufTime = bufTime - 2;
                totalTime = totalTime + bufTime;
                bufMoney = bufMoney - kostylInMoney * 2;
                totalMoney = totalMoney + bufMoney;

                Chrome1.setBase(SystemClock.elapsedRealtime());
                Chrome1.stop();
                BtnStart.setEnabled(true);
                BtnStop.setEnabled(false);
                TextMoneyCounter.clearComposingText();

                TextTotalTime.setText("Всего времени: "+String.valueOf(totalTime));
                TextTotalMoney.setText("Всего денег: " + String.valueOf(totalMoney));

                bufTime = 0;
            }
        });

        Chrome1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                bufTime = bufTime + 1;      //увеличиваются секунды
                bufMoney = bufMoney + kostylInMoney;    //увеличиваются копейки

                TextMoneyCounter.setText(String.format("%.2f", bufMoney));     //отображается увеличение копеек
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

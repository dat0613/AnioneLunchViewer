package com.example.asus.scenechange;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.w3c.dom.Document;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class SceneActivity extends AppCompatActivity {

    Button button;
    TextView textView;
    String html;

    Spinner spinnerYear;
    Spinner spinnerMonth;
    Spinner spinnerDate;

    ProgressBar progressBar;

    ArrayList<Integer> listYear;
    ArrayList<Integer> listMonth;
    ArrayList<Integer> listDate;

    int []maxDate;

    int year;
    int month;
    int date;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scene);
        setTitle("민급식");

//        android.support.v7.app.ActionBar ac = getSupportActionBar();
//        ac.setTitle("민급식");

        Calendar cal = Calendar.getInstance();

        year = cal.get(cal.YEAR);
        month = cal.get(cal.MONTH);
        date = cal.get(cal.DATE);

        button = (Button) findViewById(R.id.button);
        textView = (TextView) findViewById(R.id.textView);

        spinnerYear = (Spinner) findViewById(R.id.spinnerYear);
        spinnerMonth = (Spinner) findViewById(R.id.spinnerMonth);
        spinnerDate = (Spinner) findViewById(R.id.spinnerDate);

        progressBar = (ProgressBar)findViewById(R.id.progressBar);

        maxDate = new int[12];
        maxDate[0]=31;
        maxDate[1]=28;
        maxDate[2]=31;
        maxDate[3]=30;
        maxDate[4]=31;
        maxDate[5]=30;
        maxDate[6]=30;
        maxDate[7]=31;
        maxDate[8]=30;
        maxDate[9]=31;
        maxDate[10]=30;
        maxDate[11]=31;

        listYear = new ArrayList<Integer>();
        listYear.add(year);
        spinnerYear.setAdapter(new ArrayAdapter<>(getApplicationContext(),R.layout.support_simple_spinner_dropdown_item,listYear));


        listMonth = new ArrayList<Integer>();
        for(int i = 1; i < 12;i++)
            listMonth.add(i);
        spinnerMonth.setAdapter(new ArrayAdapter<>(getApplicationContext(),R.layout.support_simple_spinner_dropdown_item,listMonth));
        spinnerMonth.setSelection(month);

        listDate = new ArrayList<Integer>();
        for(int i = 1;i<maxDate[Integer.parseInt(spinnerMonth.getSelectedItem().toString()) - 1] + 1;i++)
            listDate.add(i);
        spinnerDate.setAdapter(new ArrayAdapter<>(getApplicationContext(),R.layout.support_simple_spinner_dropdown_item,listDate));
        spinnerDate.setSelection(date - 1);



        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        spinnerYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                new JsoupAsyncTask().execute();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        spinnerMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                int lastSelectedDate = Integer.parseInt (spinnerDate.getSelectedItem().toString()) - 1;
                listDate.clear();
                for(int i = 1;i<maxDate[Integer.parseInt(spinnerMonth.getSelectedItem().toString()) - 1] + 1;i++)
                    listDate.add(i);
                spinnerDate.setAdapter(new ArrayAdapter<>(getApplicationContext(),R.layout.support_simple_spinner_dropdown_item,listDate));
                if(lastSelectedDate > listDate.size() - 1)
                    lastSelectedDate = listDate.size() - 1;
                //System.out.println("값 : " + lastSelectedDate);
                spinnerDate.setSelection(lastSelectedDate);

                new JsoupAsyncTask().execute();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        spinnerDate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                new JsoupAsyncTask().execute();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        new JsoupAsyncTask().execute();
    }

    private class JsoupAsyncTask extends AsyncTask<Void, Void, Void>{
        @Override
        protected void onPreExecute(){

            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }

        @Override
        protected Void doInBackground(Void... params){
            try{
                html = "";

                year = Integer.parseInt(spinnerYear.getSelectedItem().toString());
                month = Integer.parseInt(spinnerMonth.getSelectedItem().toString())-1;
                date = Integer.parseInt(spinnerDate.getSelectedItem().toString());

                String url = "http://anione.hs.kr/index.jsp?mnu=M001006008001&SCODE=S0000000109&frame=&cmd=list&year="+year+"&month="+month;
                org.jsoup.nodes.Document doc = Jsoup.connect(url).get();
                Elements table = doc.select("table#m_lunchTable tbody td#K"+date);
                String str = table.text();
                if((str.length() > 3)) {
                    str = str.replaceAll("[!-9]", "");
                    str = str.replace("에너지단백질칼슘철분", "");
//                str = str.replace("[","\n[");
//                str = str.replace("]","]\n");
                    str = str.replace("  ", " ");
                    str = str.replace(" ", "\n");
                    str = str.replace("\n\n\n", "\n\n");
                    //str = str.replace("","\n");
                    //System.out.println(str);
                }
                else {
                    str = "급식 정보가 없습니다";
                }

                String day = "";
                String yearStr = Integer.toString(year);
                String monthStr = Integer.toString(month+1);
                if(month+1 < 10){
                    monthStr = "0" + Integer.toString(month+1);
                }
                String dateStr = Integer.toString(date);
                if(date < 10){
                    dateStr = "0" + Integer.toString(date);
                }

                day = yearStr +"-"+ monthStr +"-"+ dateStr;

                System.out.println(day);
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                Date date_;
                try {
                    date_ = dateFormat.parse(day);
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(date_);
                    String week = "";
                    switch(cal.get(Calendar.DAY_OF_WEEK)){
                        case 1:
                            week = "일요일";
                            break;
                        case 2:
                            week = "월요일";
                            break;
                        case 3:
                            week = "화요일";
                            break;
                        case 4:
                            week = "수요일";
                            break;
                        case 5:
                            week = "목요일";
                            break;
                        case 6:
                            week = "금요일";
                            break;
                        case 7:
                            week = "토요일";
                            break;
                    }
                    html += (week+"\n");
                    System.out.println(week);
                }
                catch (java.text.ParseException e){
                }
                html += str;
            }
            catch (IOException e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected  void onPostExecute(Void result){
            textView.setText(html);
            progressBar.setVisibility(View.GONE);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
    }
}

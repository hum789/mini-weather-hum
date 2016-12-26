package com.pku.hum.miniweather;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;

import cn.edu.pku.hum.app.MyApplication;
import cn.edu.pku.hum.bean.City;


/**
 * Created by hum on 2016/10/18.
 */
public class SelectCity extends Activity implements View.OnClickListener{
    private ImageView mBackBtn;
    private ListView mListView;
    private TextView mCityInfo;
    private EditText eSearch;
    private String state;

    MyApplication app;
    List<City> data = new ArrayList<City>();
    ArrayList<String> city = new ArrayList<>();
    ArrayList<String> cityId = new ArrayList<>();
    private int position;

    public void search(String string) {
        city.clear();
        cityId.clear();
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).getCity().contains(string)) {
                city.add(data.get(i).getCity());
                cityId.add(data.get(i).getNumber());
            }

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_city);

        mCityInfo = (TextView)findViewById(R.id.title_city_name);
        mBackBtn = (ImageView) findViewById(R.id.title_back);
        mBackBtn.setOnClickListener(this);

        app = (MyApplication)getApplication();
        data = app.getCityList();

        for(int i=0; i<data.size(); i++) {
            if(cityId.isEmpty()) {
                city.add(data.get(i).getCity());
                cityId.add(data.get(i).getNumber());
            }
            String id1 = data.get(i).getNumber().substring(0,5);
            String id2 = cityId.get(cityId.size()-1).substring(0,5);
            if(!id1.equals(id2)) {
                city.add(data.get(i).getCity());
                cityId.add(data.get(i).getNumber());
            }
        }
        mListView = (ListView) findViewById(R.id.list_view);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                SelectCity.this, android.R.layout.simple_list_item_1, city);
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Toast.makeText(SelectCity.this,city.get(position),
                        Toast.LENGTH_SHORT).show();
                SelectCity.this.position = position;
                mCityInfo.setText("当前城市："+city.get(position));
            }
        });

        eSearch = (EditText) findViewById(R.id.search_edit);

        eSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                // TODO Auto-generated method stub
                //这个应该是在改变的时候会做的动作吧，具体还没用到过。
                if (eSearch.getText().toString() != null) {
                    state = eSearch.getText().toString();
                    search(state);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
                // TODO Auto-generated method stub
                //这是文本框改变之前会执行的动作
            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub

            }
        });
    }

    @Override
    public void onClick(View v) {
        SharedPreferences sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("curCityId",cityId.get(position));
        editor.commit();
        switch (v.getId()) {
            case R.id.title_back:
                Intent i = new Intent();
                i.putExtra("cityCode", cityId.get(position));
                setResult(RESULT_OK, i);
                finish();
                break;
            default:
                break;
        }
    }
}

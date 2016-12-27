package com.pku.hum.miniweather;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import android.view.animation.*;

import cn.edu.pku.hum.app.MyApplication;
import cn.edu.pku.hum.bean.City;
import cn.edu.pku.hum.bean.TodayWeather;
import cn.edu.pku.hum.util.NetUtil;

import com.baidu.location.service.*;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;


/**
 * Created by hum on 2016/9/22.
 */
public class MainActivity extends Activity implements View.OnClickListener,ViewPager.OnPageChangeListener {
    private static final int UPDATE_TODAY_WEATHER = 1;
    private ImageView mUpdateBtn;
    private ImageView mCitySelect;
    private ImageView mLocationBtn;
    public LocationService locationService;
    public String locationCity;
    public String locationCityId;
    private BDLocationListener mListener;

    private TextView cityTv, timeTv, humidityTv, temperature_nowTv, weekTv, pmDataTv, pmQualityTv,temperatureTv, climateTv,
            windTv, city_name_Tv;
    private TextView temperatureTv0,temperatureTv1,temperatureTv2,temperatureTv3,temperatureTv4,temperatureTv5,
            windTv0,windTv1,windTv2,windTv3,windTv4,windTv5,
            climateTv0,climateTv1,climateTv2,climateTv3,climateTv4,climateTv5,
            weekTv0,weekTv1,weekTv2,weekTv3,weekTv4,weekTv5;
    private ImageView weatherImg,weatherImg0,weatherImg1,weatherImg2,weatherImg3,weatherImg4,weatherImg5, pmImg;

    private ViewPagerAdapter viewPagerAdapter;
    private ViewPager viewPager;
    private List<View> views;

    private ImageView[] dots;
    private int[] ids = {R.id.iv_week1,R.id.iv_week2};
    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg){
            switch (msg.what) {
                case UPDATE_TODAY_WEATHER:
                    updateTodayWeather((TodayWeather) msg.obj);
                    break;
                default:
                    break;
            }
        }
    };
    List<City> data = new ArrayList<City>();
    MyApplication app;

    @Override
    protected void onStart() {
        locationService = new LocationService(getApplicationContext());
        locationService.registerListener(mListener);
        super.onStart();
        Log.i("tag", "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("tag", "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i("tag", "onPause");
    }

    @Override
    protected void onStop() {
        locationService.unregisterListener(mListener); //注销掉监听
        locationService.stop(); //停止定位服务
        super.onStop();
        Log.i("tag", "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("tag", "onDestroy");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.i("tag", "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_info);

        mUpdateBtn = (ImageView) findViewById(R.id.title_update_btn);
        mUpdateBtn.setOnClickListener(this);

        if (NetUtil.getNetworkState(this) != NetUtil.NETWORN_NONE) {
            Log.d("myWeather", "network is OK!");
            Toast.makeText(MainActivity.this, "network is OK!", Toast.LENGTH_LONG).show();
        } else {
            Log.d("myWeather", "network is not connected!");
            Toast.makeText(MainActivity.this, "network is not connected!", Toast.LENGTH_LONG).show();
        }

        mCitySelect = (ImageView) findViewById(R.id.title_city_manager);
        mCitySelect.setOnClickListener(this);
        mLocationBtn = (ImageView) findViewById(R.id.title_location);
        mLocationBtn.setOnClickListener(this);
        initView();
        initDots();

    }
    void  initDots() {
        dots = new ImageView[views.size()];
        for (int i=0; i<views.size(); i++) {
            dots[i] = (ImageView)findViewById(ids[i]);
        }
    }


    void initView(){
        Log.d("tag", "initView");
        LayoutInflater inflater = LayoutInflater.from(this);
        views = new ArrayList<View>();
        views.add(inflater.inflate(R.layout.week1, null));
        views.add(inflater.inflate(R.layout.week2, null));
        viewPagerAdapter = new ViewPagerAdapter(views, this);
        viewPager = (ViewPager)findViewById(R.id.week_viewpager);
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setOnPageChangeListener(this);

        city_name_Tv = (TextView) findViewById(R.id.title_city_name);
        cityTv = (TextView) findViewById(R.id.city);
        timeTv = (TextView) findViewById(R.id.time);
        humidityTv = (TextView) findViewById(R.id.humidity);
        temperature_nowTv = (TextView) findViewById(R.id.temperature_now);
        pmDataTv = (TextView) findViewById(R.id.pm_data);
        pmQualityTv = (TextView) findViewById(R.id.pm2_5_quality);
        pmImg = (ImageView) findViewById(R.id.pm2_5_img_0_50);

        weekTv = (TextView) findViewById(R.id.week_today);
        temperatureTv = (TextView) findViewById(R.id.temperature);
        climateTv = (TextView) findViewById(R.id.climate);
        windTv = (TextView) findViewById(R.id.wind);
        weatherImg = (ImageView) findViewById(R.id.weather_img_qing);

        weekTv0 = (TextView)views.get(0).findViewById(R.id.week1_today);
        weekTv1 = (TextView)views.get(0).findViewById(R.id.week2_today);
        weekTv2 = ((TextView)views.get(0).findViewById(R.id.week3_today));
        weekTv3 = ((TextView)views.get(1).findViewById(R.id.week4_today));
        weekTv4 = ((TextView)views.get(1).findViewById(R.id.week5_today));
        weekTv5 = ((TextView)views.get(1).findViewById(R.id.week6_today));
        temperatureTv0 = ((TextView)views.get(0).findViewById(R.id.week1_temperature));
        temperatureTv1 = ((TextView)views.get(0).findViewById(R.id.week2_temperature));
        temperatureTv2 = ((TextView)views.get(0).findViewById(R.id.week3_temperature));
        temperatureTv3 = ((TextView)views.get(1).findViewById(R.id.week4_temperature));
        temperatureTv4 = ((TextView)views.get(1).findViewById(R.id.week5_temperature));
        temperatureTv5 = ((TextView)views.get(1).findViewById(R.id.week6_temperature));
        climateTv0 = ((TextView)views.get(0).findViewById(R.id.week1_climate));
        climateTv1 = ((TextView)views.get(0).findViewById(R.id.week2_climate));
        climateTv2 = ((TextView)views.get(0).findViewById(R.id.week3_climate));
        climateTv3 = ((TextView)views.get(1).findViewById(R.id.week4_climate));
        climateTv4 = ((TextView)views.get(1).findViewById(R.id.week5_climate));
        climateTv5 = ((TextView)views.get(1).findViewById(R.id.week6_climate));
        windTv0 = ((TextView)views.get(0).findViewById(R.id.week1_wind));
        windTv1 = ((TextView)views.get(0).findViewById(R.id.week2_wind));
        windTv2 = ((TextView)views.get(0).findViewById(R.id.week3_wind));
        windTv3 = ((TextView)views.get(1).findViewById(R.id.week4_wind));
        windTv4 = ((TextView)views.get(1).findViewById(R.id.week5_wind));
        windTv5 = ((TextView)views.get(1).findViewById(R.id.week6_wind));
        weatherImg0 = ((ImageView)views.get(0).findViewById(R.id.week1_weather_pic));
        weatherImg1 = ((ImageView)views.get(0).findViewById(R.id.week2_weather_pic));
        weatherImg2 = ((ImageView)views.get(0).findViewById(R.id.week3_weather_pic));
        weatherImg3 = ((ImageView)views.get(1).findViewById(R.id.week4_weather_pic));
        weatherImg4 = ((ImageView)views.get(1).findViewById(R.id.week5_weather_pic));
        weatherImg5 = ((ImageView)views.get(1).findViewById(R.id.week6_weather_pic));
        weekTv0.setText("N/A");
        weekTv1.setText("N/A");
        weekTv2.setText("N/A");
        weekTv3.setText("N/A");
        weekTv4.setText("N/A");
        weekTv5.setText("N/A");
        temperatureTv0.setText("N/A");
        temperatureTv1.setText("N/A");
        temperatureTv2.setText("N/A");
        temperatureTv3.setText("N/A");
        temperatureTv4.setText("N/A");
        temperatureTv5.setText("N/A");
        climateTv0.setText("N/A");
        climateTv1.setText("N/A");
        climateTv2.setText("N/A");
        climateTv3.setText("N/A");
        climateTv4.setText("N/A");
        climateTv5.setText("N/A");
        windTv0.setText("N/A");
        windTv1.setText("N/A");
        windTv2.setText("N/A");
        windTv3.setText("N/A");
        windTv4.setText("N/A");
        windTv5.setText("N/A");


        city_name_Tv.setText("N/A");
        cityTv.setText("N/A");
        timeTv.setText("N/A");
        humidityTv.setText("N/A");
        temperature_nowTv.setText("N/A");
        pmDataTv.setText("N/A");
        pmQualityTv.setText("N/A");
        weekTv.setText("N/A");
        temperatureTv.setText("N/A");
        climateTv.setText("N/A");
        windTv.setText("N/A");


    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }
    @Override
    public void onPageScrollStateChanged(int state) {

    }
    @Override
    public void onPageSelected(int position) {
        for (int a=0; a<ids.length; a++) {
            if (a == position) {
                dots[a].setImageResource(R.drawable.page_indicator_focused);
            }else {
                dots[a].setImageResource(R.drawable.page_indicator_unfocused);
            }
        }
    }
    @Override
    public void onClick(View view) {

        Log.d("tag", "onClick");

        if (view.getId() == R.id.title_city_manager) {
            Intent i = new Intent(this, SelectCity.class);
            startActivityForResult(i, 1);
        }

        if (view.getId() == R.id.title_location) {
            locationService.start();
            mListener = new BDLocationListener() {

                @Override
                public void onReceiveLocation(BDLocation location) {
                    // TODO Auto-generated method stub
                    if (null != location && location.getLocType() != BDLocation.TypeServerError) {
                        app = (MyApplication)getApplication();
                        data = app.getCityList();
                        locationCity = location.getCity();
                        for (int i=0; i<data.size(); i++) {
                            if (locationCity.contains(data.get(i).getCity())) {
                                locationCityId = data.get(i).getNumber();
                            }
                        }
                        queryWeatherCode(locationCityId);
                    }
                }
            };
        }

        if (view.getId() == R.id.title_update_btn) {
            Animation animation = AnimationUtils.loadAnimation(this, R.anim.update_action);
            animation.reset();
            mUpdateBtn.clearAnimation();
            mUpdateBtn.startAnimation(animation);


            SharedPreferences sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
            String cityCode = sharedPreferences.getString("curCityId", "101010100");
            Log.d("myWeather", cityCode);

            if (NetUtil.getNetworkState(this) != NetUtil.NETWORN_NONE) {
                Log.d("myWeather", "network is OK!");
                queryWeatherCode(cityCode);
            } else {
                Log.d("myWeather", "network is not connected!");
                Toast.makeText(MainActivity.this, "network is not connected!", Toast.LENGTH_LONG).show();
            }
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("tag", "onActivityResult");
        if (requestCode == 1 && resultCode == RESULT_OK) {
            String newCityCode = data.getStringExtra("cityCode");
            Log.d("myWeather", "选择的城市代码为"+newCityCode);

            if (NetUtil.getNetworkState(this) != NetUtil.NETWORN_NONE) {
                Log.d("myWeather", "network is OK!");
                queryWeatherCode(newCityCode);
            } else {
                Log.d("myWeather", "network is not connected!");
                Toast.makeText(MainActivity.this, "network is not connected!", Toast.LENGTH_LONG).show();
            }
        }
    }


    private void queryWeatherCode(String cityCode) {
        Log.d("tag", "queryWeatherCode");
        final String address = "http://wthrcdn.etouch.cn/WeatherApi?citykey=" + cityCode;
        Log.d("myWeather", address);
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection con = null;
                TodayWeather todayWeather = null;
                try {
                    URL url = new URL(address);
                    con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("GET");
                    con.setConnectTimeout(8000);
                    con.setReadTimeout(8000);
                    InputStream in = con.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String str;
                    while ((str = reader.readLine()) != null) {
                        response.append(str);
                        Log.d("myWeather", str);
                    }
                    String responseStr = response.toString();
                    Log.d("myWeather", responseStr);
                    todayWeather = parseXML(responseStr);
                    if (todayWeather != null) {
                        Log.d("myWeather", todayWeather.toString());

                        Message msg =new Message();
                        msg.what = UPDATE_TODAY_WEATHER;
                        msg.obj= todayWeather;
                        mHandler.sendMessage(msg);

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (con != null) {
                        con.disconnect();
                    }
                }
            }
        }).start();
    }



    private TodayWeather parseXML(String xmldata) {
        Log.d("tag", "parseXML");
        TodayWeather todayWeather = null;
        int fengxiangCount = 0;
        int fengliCount = 0;
        int dateCount = 0;
        int highCount = 0;
        int lowCount = 0;
        int typeCount = 0;
        try {
            XmlPullParserFactory fac = XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser = fac.newPullParser();
            xmlPullParser.setInput(new StringReader(xmldata));
            int eventType = xmlPullParser.getEventType();
            Log.d("myWeather", "parseXML");
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        if (xmlPullParser.getName().equals("resp")) {
                            todayWeather = new TodayWeather();
                        }

                        if(todayWeather != null) {
                            if (xmlPullParser.getName().equals("city")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setCity(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("updatetime")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setUpdatetime(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("shidu")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setShidu(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("wendu")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setWendu(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("pm25")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setPm25(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("quality")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setQuality(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("fengxiang") && fengxiangCount == 0) {
                                eventType = xmlPullParser.next();
                                todayWeather.setFengxiang(xmlPullParser.getText());
                                fengxiangCount++;
                            } else if (xmlPullParser.getName().equals("fengli") && fengliCount == 0) {
                                eventType = xmlPullParser.next();
                                todayWeather.setFengli(xmlPullParser.getText());
                                fengliCount++;
                            } else if (xmlPullParser.getName().equals("date_1")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setDate0(xmlPullParser.getText());
                                Log.d("todayWeather", todayWeather.getDate0());
                            } else if (xmlPullParser.getName().equals("high_1")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setHigh0(xmlPullParser.getText().substring(2).trim());
                            } else if (xmlPullParser.getName().equals("low_1")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setLow0(xmlPullParser.getText().substring(2).trim());
                            } else if (xmlPullParser.getName().equals("fl_1")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setFengli0(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("type_1")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setType0(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("date") && dateCount == 0) {
                                eventType = xmlPullParser.next();
                                todayWeather.setDate(xmlPullParser.getText());
                                dateCount++;
                            } else if (xmlPullParser.getName().equals("high") && highCount == 0) {
                                eventType = xmlPullParser.next();
                                todayWeather.setHigh(xmlPullParser.getText().substring(2).trim());
                                highCount++;
                            } else if (xmlPullParser.getName().equals("low") && lowCount == 0) {
                                eventType = xmlPullParser.next();
                                todayWeather.setLow(xmlPullParser.getText().substring(2).trim());
                                lowCount++;
                            } else if (xmlPullParser.getName().equals("type") && typeCount == 0) {
                                eventType = xmlPullParser.next();
                                todayWeather.setType(xmlPullParser.getText());
                                typeCount++;
                            } else if (xmlPullParser.getName().equals("date") && dateCount == 1) {
                                eventType = xmlPullParser.next();
                                todayWeather.setDate2(xmlPullParser.getText());
                                dateCount++;
                            } else if (xmlPullParser.getName().equals("high") && highCount == 1) {
                                eventType = xmlPullParser.next();
                                todayWeather.setHigh2(xmlPullParser.getText().substring(2).trim());
                                highCount++;
                            } else if (xmlPullParser.getName().equals("low") && lowCount == 1) {
                                eventType = xmlPullParser.next();
                                todayWeather.setLow2(xmlPullParser.getText().substring(2).trim());
                                lowCount++;
                            } else if (xmlPullParser.getName().equals("type") && typeCount == 1) {
                                eventType = xmlPullParser.next();
                                todayWeather.setType2(xmlPullParser.getText());
                                typeCount++;
                            } else if (xmlPullParser.getName().equals("fengli") && fengliCount == 1) {
                                eventType = xmlPullParser.next();
                                todayWeather.setFengli2(xmlPullParser.getText());
                                fengliCount++;
                            } else if (xmlPullParser.getName().equals("date") && dateCount == 2) {
                                eventType = xmlPullParser.next();
                                todayWeather.setDate3(xmlPullParser.getText());
                                dateCount++;
                            } else if (xmlPullParser.getName().equals("high") && highCount == 2) {
                                eventType = xmlPullParser.next();
                                todayWeather.setHigh3(xmlPullParser.getText().substring(2).trim());
                                highCount++;
                            } else if (xmlPullParser.getName().equals("low") && lowCount == 2) {
                                eventType = xmlPullParser.next();
                                todayWeather.setLow3(xmlPullParser.getText().substring(2).trim());
                                lowCount++;
                            } else if (xmlPullParser.getName().equals("type") && typeCount == 2) {
                                eventType = xmlPullParser.next();
                                todayWeather.setType3(xmlPullParser.getText());
                                typeCount++;
                            } else if (xmlPullParser.getName().equals("fengli") && fengliCount == 2) {
                                eventType = xmlPullParser.next();
                                todayWeather.setFengli3(xmlPullParser.getText());
                                fengliCount++;
                            } else if (xmlPullParser.getName().equals("date") && dateCount == 3) {
                                eventType = xmlPullParser.next();
                                todayWeather.setDate4(xmlPullParser.getText());
                                dateCount++;
                            } else if (xmlPullParser.getName().equals("high") && highCount == 3) {
                                eventType = xmlPullParser.next();
                                todayWeather.setHigh4(xmlPullParser.getText().substring(2).trim());
                                highCount++;
                            } else if (xmlPullParser.getName().equals("low") && lowCount == 3) {
                                eventType = xmlPullParser.next();
                                todayWeather.setLow4(xmlPullParser.getText().substring(2).trim());
                                lowCount++;
                            } else if (xmlPullParser.getName().equals("type") && typeCount == 3) {
                                eventType = xmlPullParser.next();
                                todayWeather.setType4(xmlPullParser.getText());
                                typeCount++;
                            } else if (xmlPullParser.getName().equals("fengli") && fengliCount == 3) {
                                eventType = xmlPullParser.next();
                                todayWeather.setFengli4(xmlPullParser.getText());
                                fengliCount++;
                            } else if (xmlPullParser.getName().equals("date") && dateCount == 4) {
                                eventType = xmlPullParser.next();
                                todayWeather.setDate5(xmlPullParser.getText());
                                dateCount++;
                            } else if (xmlPullParser.getName().equals("high") && highCount == 4) {
                                eventType = xmlPullParser.next();
                                todayWeather.setHigh5(xmlPullParser.getText().substring(2).trim());
                                highCount++;
                            } else if (xmlPullParser.getName().equals("low") && lowCount == 4) {
                                eventType = xmlPullParser.next();
                                todayWeather.setLow5(xmlPullParser.getText().substring(2).trim());
                                lowCount++;
                            } else if (xmlPullParser.getName().equals("type") && typeCount == 4) {
                                eventType = xmlPullParser.next();
                                todayWeather.setType5(xmlPullParser.getText());
                                typeCount++;
                            } else if (xmlPullParser.getName().equals("fengli") && fengliCount == 4) {
                                eventType = xmlPullParser.next();
                                todayWeather.setFengli5(xmlPullParser.getText());
                                fengliCount++;
                            }
                        }

                        break;
                    case XmlPullParser.END_TAG:
                        break;
                }
                eventType = xmlPullParser.next();
            }
        }catch (XmlPullParserException e) {
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        }
        return todayWeather;
    }

    void updateTodayWeather(TodayWeather todayWeather){
        Log.d("tag", "updateTodayWeather");
        city_name_Tv.setText(todayWeather.getCity()+"天气");
        cityTv.setText(todayWeather.getCity());
        timeTv.setText(todayWeather.getUpdatetime()+ "发布");
        humidityTv.setText("湿度："+ todayWeather.getShidu());
        temperature_nowTv.setText(todayWeather.getWendu() + "℃");
        pmDataTv.setText(todayWeather.getPm25());
        pmQualityTv.setText(todayWeather.getQuality());
        weekTv.setText(todayWeather.getDate());
        weekTv0.setText(todayWeather.getDate0());
        weekTv1.setText(todayWeather.getDate());
        weekTv2.setText(todayWeather.getDate2());
        weekTv3.setText(todayWeather.getDate3());
        weekTv4.setText(todayWeather.getDate4());
        weekTv5.setText(todayWeather.getDate5());

        temperatureTv.setText(todayWeather.getHigh()+"~"+ todayWeather.getLow());
        temperatureTv0.setText(todayWeather.getHigh0()+"~"+ todayWeather.getLow0());
        temperatureTv1.setText(todayWeather.getHigh()+"~"+ todayWeather.getLow());
        temperatureTv2.setText(todayWeather.getHigh2()+"~"+ todayWeather.getLow2());
        temperatureTv3.setText(todayWeather.getHigh3()+"~"+ todayWeather.getLow3());
        temperatureTv4.setText(todayWeather.getHigh4()+"~"+ todayWeather.getLow4());
        temperatureTv5.setText(todayWeather.getHigh5()+"~"+ todayWeather.getLow5());

        climateTv.setText(todayWeather.getType());
        climateTv0.setText(todayWeather.getType0());
        climateTv1.setText(todayWeather.getType());
        climateTv2.setText(todayWeather.getType2());
        climateTv3.setText(todayWeather.getType3());
        climateTv4.setText(todayWeather.getType4());
        climateTv5.setText(todayWeather.getType5());

        windTv.setText("风力:"+ todayWeather.getFengli());
        windTv0.setText("风力:"+ todayWeather.getFengli0());
        windTv1.setText("风力:"+ todayWeather.getFengli());
        windTv2.setText("风力:"+ todayWeather.getFengli2());
        windTv3.setText("风力:"+ todayWeather.getFengli3());
        windTv4.setText("风力:"+ todayWeather.getFengli4());
        windTv5.setText("风力:"+ todayWeather.getFengli5());

        if(todayWeather.getPm25() == null) {
            pmQualityTv.setText("N/A");
        }
        else if(Integer.parseInt(todayWeather.getPm25())<=50) {
            pmImg.setImageResource(R.drawable.biz_plugin_weather_0_50);
        }else if(Integer.parseInt(todayWeather.getPm25()) > 50 && Integer.parseInt(todayWeather.getPm25()) <= 100) {
            pmImg.setImageResource(R.drawable.biz_plugin_weather_51_100);
        }else if(Integer.parseInt(todayWeather.getPm25()) > 100 && Integer.parseInt(todayWeather.getPm25()) <= 150) {
            pmImg.setImageResource(R.drawable.biz_plugin_weather_101_150);
        }else if(Integer.parseInt(todayWeather.getPm25()) > 150 && Integer.parseInt(todayWeather.getPm25()) <= 200) {
            pmImg.setImageResource(R.drawable.biz_plugin_weather_151_200);
        }else if(Integer.parseInt(todayWeather.getPm25()) > 200 && Integer.parseInt(todayWeather.getPm25()) <= 300) {
            pmImg.setImageResource(R.drawable.biz_plugin_weather_201_300);
        }else if(Integer.parseInt(todayWeather.getPm25()) > 300) {
            pmImg.setImageResource(R.drawable.biz_plugin_weather_greater_300);
        }

        if(todayWeather.getType().equals("晴")) {
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_qing);
            weatherImg1.setImageResource(R.drawable.biz_plugin_weather_qing);
        }else if(todayWeather.getType().equals("暴雪")) {
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_baoxue);
            weatherImg1.setImageResource(R.drawable.biz_plugin_weather_baoxue);
        }else if(todayWeather.getType().equals("大雨")) {
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_dayu);
            weatherImg1.setImageResource(R.drawable.biz_plugin_weather_dayu);
        }else if(todayWeather.getType().equals("大暴雨")) {
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_dabaoyu);
            weatherImg1.setImageResource(R.drawable.biz_plugin_weather_dabaoyu);
        }else if(todayWeather.getType().equals("大雪")) {
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_daxue);
            weatherImg1.setImageResource(R.drawable.biz_plugin_weather_daxue);
        }else if(todayWeather.getType().equals("大雨")) {
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_dayu);
            weatherImg1.setImageResource(R.drawable.biz_plugin_weather_dayu);
        }else if(todayWeather.getType().equals("多云")) {
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_duoyun);
            weatherImg1.setImageResource(R.drawable.biz_plugin_weather_duoyun);
        }else if(todayWeather.getType().equals("雷阵雨")) {
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_leizhenyu);
            weatherImg1.setImageResource(R.drawable.biz_plugin_weather_leizhenyu);
        }else if(todayWeather.getType().equals("雷阵雨冰雹")) {
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_leizhenyubingbao);
            weatherImg1.setImageResource(R.drawable.biz_plugin_weather_leizhenyubingbao);
        }else if(todayWeather.getType().equals("沙尘暴")) {
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_shachenbao);
            weatherImg1.setImageResource(R.drawable.biz_plugin_weather_shachenbao);
        }else if(todayWeather.getType().equals("特大暴雨")) {
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_tedabaoyu);
            weatherImg1.setImageResource(R.drawable.biz_plugin_weather_tedabaoyu);
        }else if(todayWeather.getType().equals("雾")) {
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_wu);
            weatherImg1.setImageResource(R.drawable.biz_plugin_weather_wu);
        }else if(todayWeather.getType().equals("小雪")) {
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_xiaoxue);
            weatherImg1.setImageResource(R.drawable.biz_plugin_weather_xiaoxue);
        }else if(todayWeather.getType().equals("小雨")) {
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_xiaoyu);
            weatherImg1.setImageResource(R.drawable.biz_plugin_weather_xiaoyu);
        }else if(todayWeather.getType().equals("阴")) {
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_yin);
            weatherImg1.setImageResource(R.drawable.biz_plugin_weather_yin);
        }else if(todayWeather.getType().equals("雨夹雪")) {
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_yujiaxue);
            weatherImg1.setImageResource(R.drawable.biz_plugin_weather_yujiaxue);
        }else if(todayWeather.getType().equals("阵雪")) {
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_zhenxue);
            weatherImg1.setImageResource(R.drawable.biz_plugin_weather_zhenxue);
        }else if(todayWeather.getType().equals("阵雨")) {
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_zhenyu);
            weatherImg1.setImageResource(R.drawable.biz_plugin_weather_zhenyu);
        }else if(todayWeather.getType().equals("中雪")) {
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_zhongxue);
            weatherImg1.setImageResource(R.drawable.biz_plugin_weather_zhongxue);
        }else if(todayWeather.getType().equals("中雨")) {
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_zhongyu);
            weatherImg1.setImageResource(R.drawable.biz_plugin_weather_zhongyu);
        }

        if(todayWeather.getType0().equals("晴")) {
            weatherImg0.setImageResource(R.drawable.biz_plugin_weather_qing);
        }else if(todayWeather.getType0().equals("暴雪")) {
            weatherImg0.setImageResource(R.drawable.biz_plugin_weather_baoxue);
        }else if(todayWeather.getType0().equals("大雨")) {
            weatherImg0.setImageResource(R.drawable.biz_plugin_weather_dayu);
        }else if(todayWeather.getType0().equals("大暴雨")) {
            weatherImg0.setImageResource(R.drawable.biz_plugin_weather_dabaoyu);
        }else if(todayWeather.getType0().equals("大雪")) {
            weatherImg0.setImageResource(R.drawable.biz_plugin_weather_daxue);
        }else if(todayWeather.getType0().equals("大雨")) {
            weatherImg0.setImageResource(R.drawable.biz_plugin_weather_dayu);
        }else if(todayWeather.getType0().equals("多云")) {
            weatherImg0.setImageResource(R.drawable.biz_plugin_weather_duoyun);
        }else if(todayWeather.getType0().equals("雷阵雨")) {
            weatherImg0.setImageResource(R.drawable.biz_plugin_weather_leizhenyu);
        }else if(todayWeather.getType0().equals("雷阵雨冰雹")) {
            weatherImg0.setImageResource(R.drawable.biz_plugin_weather_leizhenyubingbao);
        }else if(todayWeather.getType0().equals("沙尘暴")) {
            weatherImg0.setImageResource(R.drawable.biz_plugin_weather_shachenbao);
        }else if(todayWeather.getType0().equals("特大暴雨")) {
            weatherImg0.setImageResource(R.drawable.biz_plugin_weather_tedabaoyu);
        }else if(todayWeather.getType0().equals("雾")) {
            weatherImg0.setImageResource(R.drawable.biz_plugin_weather_wu);
        }else if(todayWeather.getType0().equals("小雪")) {
            weatherImg0.setImageResource(R.drawable.biz_plugin_weather_xiaoxue);
        }else if(todayWeather.getType0().equals("小雨")) {
            weatherImg0.setImageResource(R.drawable.biz_plugin_weather_xiaoyu);
        }else if(todayWeather.getType0().equals("阴")) {
            weatherImg0.setImageResource(R.drawable.biz_plugin_weather_yin);
        }else if(todayWeather.getType0().equals("雨夹雪")) {
            weatherImg0.setImageResource(R.drawable.biz_plugin_weather_yujiaxue);
        }else if(todayWeather.getType0().equals("阵雪")) {
            weatherImg0.setImageResource(R.drawable.biz_plugin_weather_zhenxue);
        }else if(todayWeather.getType0().equals("阵雨")) {
            weatherImg0.setImageResource(R.drawable.biz_plugin_weather_zhenyu);
        }else if(todayWeather.getType0().equals("中雪")) {
            weatherImg0.setImageResource(R.drawable.biz_plugin_weather_zhongxue);
        }else if(todayWeather.getType0().equals("中雨")) {
            weatherImg0.setImageResource(R.drawable.biz_plugin_weather_zhongyu);
        }

        if(todayWeather.getType2().equals("晴")) {
            weatherImg2.setImageResource(R.drawable.biz_plugin_weather_qing);
        }else if(todayWeather.getType2().equals("暴雪")) {
            weatherImg2.setImageResource(R.drawable.biz_plugin_weather_baoxue);
        }else if(todayWeather.getType2().equals("大雨")) {
            weatherImg2.setImageResource(R.drawable.biz_plugin_weather_dayu);
        }else if(todayWeather.getType2().equals("大暴雨")) {
            weatherImg2.setImageResource(R.drawable.biz_plugin_weather_dabaoyu);
        }else if(todayWeather.getType2().equals("大雪")) {
            weatherImg2.setImageResource(R.drawable.biz_plugin_weather_daxue);
        }else if(todayWeather.getType2().equals("大雨")) {
            weatherImg2.setImageResource(R.drawable.biz_plugin_weather_dayu);
        }else if(todayWeather.getType2().equals("多云")) {
            weatherImg2.setImageResource(R.drawable.biz_plugin_weather_duoyun);
        }else if(todayWeather.getType2().equals("雷阵雨")) {
            weatherImg2.setImageResource(R.drawable.biz_plugin_weather_leizhenyu);
        }else if(todayWeather.getType2().equals("雷阵雨冰雹")) {
            weatherImg2.setImageResource(R.drawable.biz_plugin_weather_leizhenyubingbao);
        }else if(todayWeather.getType2().equals("沙尘暴")) {
            weatherImg2.setImageResource(R.drawable.biz_plugin_weather_shachenbao);
        }else if(todayWeather.getType2().equals("特大暴雨")) {
            weatherImg2.setImageResource(R.drawable.biz_plugin_weather_tedabaoyu);
        }else if(todayWeather.getType2().equals("雾")) {
            weatherImg2.setImageResource(R.drawable.biz_plugin_weather_wu);
        }else if(todayWeather.getType2().equals("小雪")) {
            weatherImg2.setImageResource(R.drawable.biz_plugin_weather_xiaoxue);
        }else if(todayWeather.getType2().equals("小雨")) {
            weatherImg2.setImageResource(R.drawable.biz_plugin_weather_xiaoyu);
        }else if(todayWeather.getType2().equals("阴")) {
            weatherImg2.setImageResource(R.drawable.biz_plugin_weather_yin);
        }else if(todayWeather.getType2().equals("雨夹雪")) {
            weatherImg2.setImageResource(R.drawable.biz_plugin_weather_yujiaxue);
        }else if(todayWeather.getType2().equals("阵雪")) {
            weatherImg2.setImageResource(R.drawable.biz_plugin_weather_zhenxue);
        }else if(todayWeather.getType2().equals("阵雨")) {
            weatherImg2.setImageResource(R.drawable.biz_plugin_weather_zhenyu);
        }else if(todayWeather.getType2().equals("中雪")) {
            weatherImg2.setImageResource(R.drawable.biz_plugin_weather_zhongxue);
        }else if(todayWeather.getType2().equals("中雨")) {
            weatherImg2.setImageResource(R.drawable.biz_plugin_weather_zhongyu);
        }

        if(todayWeather.getType3().equals("晴")) {
            weatherImg3.setImageResource(R.drawable.biz_plugin_weather_qing);
        }else if(todayWeather.getType3().equals("暴雪")) {
            weatherImg3.setImageResource(R.drawable.biz_plugin_weather_baoxue);
        }else if(todayWeather.getType3().equals("大雨")) {
            weatherImg3.setImageResource(R.drawable.biz_plugin_weather_dayu);
        }else if(todayWeather.getType3().equals("大暴雨")) {
            weatherImg3.setImageResource(R.drawable.biz_plugin_weather_dabaoyu);
        }else if(todayWeather.getType3().equals("大雪")) {
            weatherImg3.setImageResource(R.drawable.biz_plugin_weather_daxue);
        }else if(todayWeather.getType3().equals("大雨")) {
            weatherImg3.setImageResource(R.drawable.biz_plugin_weather_dayu);
        }else if(todayWeather.getType3().equals("多云")) {
            weatherImg3.setImageResource(R.drawable.biz_plugin_weather_duoyun);
        }else if(todayWeather.getType3().equals("雷阵雨")) {
            weatherImg3.setImageResource(R.drawable.biz_plugin_weather_leizhenyu);
        }else if(todayWeather.getType3().equals("雷阵雨冰雹")) {
            weatherImg3.setImageResource(R.drawable.biz_plugin_weather_leizhenyubingbao);
        }else if(todayWeather.getType3().equals("沙尘暴")) {
            weatherImg3.setImageResource(R.drawable.biz_plugin_weather_shachenbao);
        }else if(todayWeather.getType3().equals("特大暴雨")) {
            weatherImg3.setImageResource(R.drawable.biz_plugin_weather_tedabaoyu);
        }else if(todayWeather.getType3().equals("雾")) {
            weatherImg3.setImageResource(R.drawable.biz_plugin_weather_wu);
        }else if(todayWeather.getType3().equals("小雪")) {
            weatherImg3.setImageResource(R.drawable.biz_plugin_weather_xiaoxue);
        }else if(todayWeather.getType3().equals("小雨")) {
            weatherImg3.setImageResource(R.drawable.biz_plugin_weather_xiaoyu);
        }else if(todayWeather.getType3().equals("阴")) {
            weatherImg3.setImageResource(R.drawable.biz_plugin_weather_yin);
        }else if(todayWeather.getType3().equals("雨夹雪")) {
            weatherImg3.setImageResource(R.drawable.biz_plugin_weather_yujiaxue);
        }else if(todayWeather.getType3().equals("阵雪")) {
            weatherImg3.setImageResource(R.drawable.biz_plugin_weather_zhenxue);
        }else if(todayWeather.getType3().equals("阵雨")) {
            weatherImg3.setImageResource(R.drawable.biz_plugin_weather_zhenyu);
        }else if(todayWeather.getType3().equals("中雪")) {
            weatherImg3.setImageResource(R.drawable.biz_plugin_weather_zhongxue);
        }else if(todayWeather.getType3().equals("中雨")) {
            weatherImg3.setImageResource(R.drawable.biz_plugin_weather_zhongyu);
        }

        if(todayWeather.getType4().equals("晴")) {
            weatherImg4.setImageResource(R.drawable.biz_plugin_weather_qing);
        }else if(todayWeather.getType4().equals("暴雪")) {
            weatherImg4.setImageResource(R.drawable.biz_plugin_weather_baoxue);
        }else if(todayWeather.getType4().equals("大雨")) {
            weatherImg4.setImageResource(R.drawable.biz_plugin_weather_dayu);
        }else if(todayWeather.getType4().equals("大暴雨")) {
            weatherImg4.setImageResource(R.drawable.biz_plugin_weather_dabaoyu);
        }else if(todayWeather.getType4().equals("大雪")) {
            weatherImg4.setImageResource(R.drawable.biz_plugin_weather_daxue);
        }else if(todayWeather.getType4().equals("大雨")) {
            weatherImg4.setImageResource(R.drawable.biz_plugin_weather_dayu);
        }else if(todayWeather.getType4().equals("多云")) {
            weatherImg4.setImageResource(R.drawable.biz_plugin_weather_duoyun);
        }else if(todayWeather.getType4().equals("雷阵雨")) {
            weatherImg4.setImageResource(R.drawable.biz_plugin_weather_leizhenyu);
        }else if(todayWeather.getType4().equals("雷阵雨冰雹")) {
            weatherImg4.setImageResource(R.drawable.biz_plugin_weather_leizhenyubingbao);
        }else if(todayWeather.getType4().equals("沙尘暴")) {
            weatherImg4.setImageResource(R.drawable.biz_plugin_weather_shachenbao);
        }else if(todayWeather.getType4().equals("特大暴雨")) {
            weatherImg4.setImageResource(R.drawable.biz_plugin_weather_tedabaoyu);
        }else if(todayWeather.getType4().equals("雾")) {
            weatherImg4.setImageResource(R.drawable.biz_plugin_weather_wu);
        }else if(todayWeather.getType4().equals("小雪")) {
            weatherImg4.setImageResource(R.drawable.biz_plugin_weather_xiaoxue);
        }else if(todayWeather.getType4().equals("小雨")) {
            weatherImg4.setImageResource(R.drawable.biz_plugin_weather_xiaoyu);
        }else if(todayWeather.getType4().equals("阴")) {
            weatherImg4.setImageResource(R.drawable.biz_plugin_weather_yin);
        }else if(todayWeather.getType4().equals("雨夹雪")) {
            weatherImg4.setImageResource(R.drawable.biz_plugin_weather_yujiaxue);
        }else if(todayWeather.getType4().equals("阵雪")) {
            weatherImg4.setImageResource(R.drawable.biz_plugin_weather_zhenxue);
        }else if(todayWeather.getType4().equals("阵雨")) {
            weatherImg4.setImageResource(R.drawable.biz_plugin_weather_zhenyu);
        }else if(todayWeather.getType4().equals("中雪")) {
            weatherImg4.setImageResource(R.drawable.biz_plugin_weather_zhongxue);
        }else if(todayWeather.getType4().equals("中雨")) {
            weatherImg4.setImageResource(R.drawable.biz_plugin_weather_zhongyu);
        }

        if(todayWeather.getType5().equals("晴")) {
            weatherImg5.setImageResource(R.drawable.biz_plugin_weather_qing);
        }else if(todayWeather.getType5().equals("暴雪")) {
            weatherImg5.setImageResource(R.drawable.biz_plugin_weather_baoxue);
        }else if(todayWeather.getType5().equals("大雨")) {
            weatherImg5.setImageResource(R.drawable.biz_plugin_weather_dayu);
        }else if(todayWeather.getType5().equals("大暴雨")) {
            weatherImg5.setImageResource(R.drawable.biz_plugin_weather_dabaoyu);
        }else if(todayWeather.getType5().equals("大雪")) {
            weatherImg5.setImageResource(R.drawable.biz_plugin_weather_daxue);
        }else if(todayWeather.getType5().equals("大雨")) {
            weatherImg5.setImageResource(R.drawable.biz_plugin_weather_dayu);
        }else if(todayWeather.getType5().equals("多云")) {
            weatherImg5.setImageResource(R.drawable.biz_plugin_weather_duoyun);
        }else if(todayWeather.getType5().equals("雷阵雨")) {
            weatherImg5.setImageResource(R.drawable.biz_plugin_weather_leizhenyu);
        }else if(todayWeather.getType5().equals("雷阵雨冰雹")) {
            weatherImg5.setImageResource(R.drawable.biz_plugin_weather_leizhenyubingbao);
        }else if(todayWeather.getType5().equals("沙尘暴")) {
            weatherImg5.setImageResource(R.drawable.biz_plugin_weather_shachenbao);
        }else if(todayWeather.getType5().equals("特大暴雨")) {
            weatherImg5.setImageResource(R.drawable.biz_plugin_weather_tedabaoyu);
        }else if(todayWeather.getType5().equals("雾")) {
            weatherImg5.setImageResource(R.drawable.biz_plugin_weather_wu);
        }else if(todayWeather.getType5().equals("小雪")) {
            weatherImg5.setImageResource(R.drawable.biz_plugin_weather_xiaoxue);
        }else if(todayWeather.getType5().equals("小雨")) {
            weatherImg5.setImageResource(R.drawable.biz_plugin_weather_xiaoyu);
        }else if(todayWeather.getType5().equals("阴")) {
            weatherImg5.setImageResource(R.drawable.biz_plugin_weather_yin);
        }else if(todayWeather.getType5().equals("雨夹雪")) {
            weatherImg5.setImageResource(R.drawable.biz_plugin_weather_yujiaxue);
        }else if(todayWeather.getType5().equals("阵雪")) {
            weatherImg5.setImageResource(R.drawable.biz_plugin_weather_zhenxue);
        }else if(todayWeather.getType5().equals("阵雨")) {
            weatherImg5.setImageResource(R.drawable.biz_plugin_weather_zhenyu);
        }else if(todayWeather.getType5().equals("中雪")) {
            weatherImg5.setImageResource(R.drawable.biz_plugin_weather_zhongxue);
        }else if(todayWeather.getType5().equals("中雨")) {
            weatherImg5.setImageResource(R.drawable.biz_plugin_weather_zhongyu);
        }
        Toast.makeText(MainActivity.this,"更新成功！",Toast.LENGTH_SHORT).show();

    }
}



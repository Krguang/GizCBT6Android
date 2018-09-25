package com.gizwits.opensource.appkit.ControlModule;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gizwits.gizwifisdk.api.GizWifiDevice;
import com.gizwits.gizwifisdk.enumration.GizWifiDeviceNetStatus;
import com.gizwits.gizwifisdk.enumration.GizWifiErrorCode;
import com.gizwits.opensource.appkit.CommonModule.GosDeploy;
import com.gizwits.opensource.appkit.R;

import java.util.concurrent.ConcurrentHashMap;

public class AhuDeviceDataActivity extends GosControlModuleBaseActivity {


    private static final String TAG = "test";
    private GizWifiDevice mDevice;

    private TextView tv_data_tempReal;
    private TextView tv_data_humiReal;
    private TextView tv_data_tempSet;
    private TextView tv_data_humiSet;
    private TextView tv_data_lengShuiFaKaiDu;
    private TextView tv_data_reShuiFaKaiDu;
    private TextView tv_data_jiaShiQIKaiDu;

    private TextView tv_dongXiaJi;
    private TextView tv_shouZiDong;

    private Button bt_fengJiYiQiDong;
    private Button bt_zhiBanStatus;
    private Button bt_dianJiaRe1;
    private Button bt_dianJiaRe2;
    private Button bt_dianJiaRe3;
    private Button bt_fengJiStatus;
    private Button bt_zhongXiaoBaoJing;
    private Button bt_dianJiaReGaoWen;
    private Button bt_fengJiQueFeng;
    private Button bt_paiFengJiYiQiDong;
    private Button bt_diWenPanGuan;
    private Button bt_mieJunYunXing;

    String stringFromPrevious;

    private enum handler_key {

        /** 更新界面 */
        UPDATE_UI,

        DISCONNECT,
    }

    private Runnable mRunnable = new Runnable() {
        public void run() {
            if (isDeviceCanBeControlled()) {
                progressDialog.cancel();
            } else {
                toastDeviceNoReadyAndExit();
            }
        }

    };

    /** The handler. */
    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            AhuDeviceDataActivity.handler_key key = AhuDeviceDataActivity.handler_key.values()[msg.what];
            switch (key) {
                case UPDATE_UI:
                    updateUI();
                    break;
                case DISCONNECT:
                    toastDeviceDisconnectAndExit();
                    break;
            }
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_k_ahu);

        initDevice();
       // setToolBar(true, getDeviceName());
        setToolBar(true, stringFromPrevious);
        final Drawable add = getResources().getDrawable(R.drawable.common_setting_more);
        int color = GosDeploy.appConfig_Contrast();
        add.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        mToolbar.setOverflowIcon(add);
        initView();
        initEvent();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: 第二界面被摧毁了！");
    }

    private void initView() {

        tv_data_tempReal = (TextView) findViewById(R.id.tv_data_tempReal);
        tv_data_humiReal = (TextView) findViewById(R.id.tv_data_humiReal);
        tv_data_tempSet = (TextView) findViewById(R.id.tv_data_tempSet);
        tv_data_humiSet = (TextView) findViewById(R.id.tv_data_humiSet);;
        tv_data_lengShuiFaKaiDu = (TextView) findViewById(R.id.tv_data_lengShuiFaKaiDu);
        tv_data_reShuiFaKaiDu = (TextView) findViewById(R.id.tv_data_reShuiFaKaiDu);
        tv_data_jiaShiQIKaiDu = (TextView) findViewById(R.id.tv_data_jiaShiQIKaiDu);

        tv_dongXiaJi = (TextView) findViewById(R.id.tv_dongXiaJi);
        tv_shouZiDong = (TextView) findViewById(R.id.tv_shouZiDong);

        bt_fengJiYiQiDong = (Button) findViewById(R.id.bt_fengJiYiQiDong);
        bt_zhiBanStatus = (Button) findViewById(R.id.bt_zhiBanStatus);
        bt_dianJiaRe1 = (Button) findViewById(R.id.bt_dianJiaRe1);
        bt_dianJiaRe2 = (Button) findViewById(R.id.bt_dianJiaRe2);
        bt_dianJiaRe3 = (Button) findViewById(R.id.bt_dianJiaRe3);
        bt_fengJiStatus = (Button) findViewById(R.id.bt_fengJiStatus);
        bt_zhongXiaoBaoJing = (Button) findViewById(R.id.bt_zhongXiaoBaoJing);
        bt_dianJiaReGaoWen = (Button) findViewById(R.id.bt_dianJiaReGaoWen);
        bt_fengJiQueFeng = (Button) findViewById(R.id.bt_fengJiQueFeng);
        bt_paiFengJiYiQiDong = (Button) findViewById(R.id.bt_paiFengJiYiQiDong);
        bt_diWenPanGuan = (Button) findViewById(R.id.bt_diWenPanGuan);
        bt_mieJunYunXing = (Button) findViewById(R.id.bt_mieJunYunXing);

    }

    private void initEvent() {

        tv_data_tempReal.setText("00.0℃");
        tv_data_humiReal.setText("00.0RH");
        tv_data_tempSet.setText("00.0℃");
        tv_data_humiSet.setText("00.0RH");
        tv_data_lengShuiFaKaiDu.setText("00.0%");
        tv_data_reShuiFaKaiDu.setText("00.0%");
        tv_data_jiaShiQIKaiDu.setText("00.0%");
        tv_dongXiaJi.setText("");
        tv_shouZiDong.setText("");
        bt_fengJiYiQiDong.setBackgroundResource(R.drawable.led_gray);
        bt_zhiBanStatus.setBackgroundResource(R.drawable.led_gray);
        bt_dianJiaRe1.setBackgroundResource(R.drawable.led_gray);
        bt_dianJiaRe2.setBackgroundResource(R.drawable.led_gray);
        bt_dianJiaRe3.setBackgroundResource(R.drawable.led_gray);
        bt_fengJiStatus.setBackgroundResource(R.drawable.led_gray);
        bt_zhongXiaoBaoJing.setBackgroundResource(R.drawable.led_gray);
        bt_dianJiaReGaoWen.setBackgroundResource(R.drawable.led_gray);
        bt_fengJiQueFeng.setBackgroundResource(R.drawable.led_gray);
        bt_paiFengJiYiQiDong.setBackgroundResource(R.drawable.led_gray);
        bt_diWenPanGuan.setBackgroundResource(R.drawable.led_gray);
        bt_mieJunYunXing.setBackgroundResource(R.drawable.led_gray);
    }


    private void initDevice() {
        Intent intent = getIntent();
        stringFromPrevious = intent.getStringExtra("extra_data");
        mDevice = (GizWifiDevice) intent.getParcelableExtra("GizWifiDevice");
        mDevice.setListener(gizWifiDeviceListener);
    }


    private String getDeviceName() {
        if (TextUtils.isEmpty(mDevice.getAlias())) {
            return mDevice.getProductName();
        }
        return mDevice.getAlias();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getStatusOfDevice();
    }

    /*
     * ========================================================================
     * 菜单栏
     * ========================================================================
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.device_more, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_setDeviceInfo:
                setDeviceInfo();
                break;

            case R.id.action_getHardwareInfo:
                if (mDevice.isLAN()) {
                    mDevice.getHardwareInfo();
                } else {
                    myToast("只允许在局域网下获取设备硬件信息！");
                }
                break;

            case R.id.action_getStatu:
                mDevice.getDeviceStatus();
                break;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Description:根据保存的的数据点的值来更新UI
     */
    protected void updateUI() {

        switch (stringFromPrevious){

            case "AHU301":
                updateAhu301();
                break;

            case "AHU302":
                updateAhu302();
                break;

            case "AHU303":
                updateAhu303();
                break;

            case "AHU304":
                updateAhu304();
                break;

            case "AHU305":
                updateAhu305();
                break;

            case "AHU306":
                updateAhu306();
                break;

            case "AHU307":
                updateAhu307();
                break;

            case "AHU308":
                updateAhu308();
                break;

            default:break;
        }
    }

    private void updateAhu301() {

        tv_data_tempReal.setText(formatValue(data_AHU301_tempReal/10.0,0.1)+"℃");
        tv_data_humiReal.setText(formatValue(data_AHU301_humiReal/10.0,0.1)+"RH");
        tv_data_tempSet.setText(formatValue(data_AHU301_tempSet/10.0,0.1)+"℃");
        tv_data_humiSet.setText(formatValue(data_AHU301_humiSet/10.0,0.1)+"RH");
        tv_data_lengShuiFaKaiDu.setText(formatValue(data_AHU301_lengShuiFaKaiDu/10.0,0.1)+"%");
        tv_data_reShuiFaKaiDu.setText(formatValue(data_AHU301_reShuiFaKaiDu/10.0,0.1)+"%");
        tv_data_jiaShiQIKaiDu.setText(formatValue(data_AHU301_jiaShiQIKaiDu/10.0,0.1)+"%");


        if (data_AHU301_dongXiaJi){
            tv_dongXiaJi.setText("夏季");
        }else{
            tv_dongXiaJi.setText("冬季");
        }

        if (data_AHU301_shouZiDong){
            tv_shouZiDong.setText("手动");
        }else {
            tv_shouZiDong.setText("自动");
        }

        if (data_AHU301_fengJiYiQiDong){
            bt_fengJiYiQiDong.setBackgroundResource(R.drawable.led_green);
        }else {
            bt_fengJiYiQiDong.setBackgroundResource(R.drawable.led_gray);
        }

        if (data_AHU301_zhiBanStatus){
            bt_zhiBanStatus.setBackgroundResource(R.drawable.led_green);
        }else {
            bt_zhiBanStatus.setBackgroundResource(R.drawable.led_gray);
        }

        if (data_AHU301_dianJiaRe1){
            bt_dianJiaRe1.setBackgroundResource(R.drawable.led_green);
        }else {
            bt_dianJiaRe1.setBackgroundResource(R.drawable.led_gray);
        }

        if (data_AHU301_dianJiaRe2){
            bt_dianJiaRe2.setBackgroundResource(R.drawable.led_green);
        }else {
            bt_dianJiaRe2.setBackgroundResource(R.drawable.led_gray);
        }

        if (data_AHU301_dianJiaRe3){
            bt_dianJiaRe3.setBackgroundResource(R.drawable.led_green);
        }else {
            bt_dianJiaRe3.setBackgroundResource(R.drawable.led_gray);
        }

        if (data_AHU301_fengJiStatus){
            bt_fengJiStatus.setBackgroundResource(R.drawable.led_green);
        }else {
            bt_fengJiStatus.setBackgroundResource(R.drawable.led_gray);
        }

        if (data_AHU301_zhongXiaoBaoJing){
            bt_zhongXiaoBaoJing.setBackgroundResource(R.drawable.led_red);
        }else {
            bt_zhongXiaoBaoJing.setBackgroundResource(R.drawable.led_gray);
        }

        if (data_AHU301_dianJiaReGaoWen){
            bt_dianJiaReGaoWen.setBackgroundResource(R.drawable.led_red);
        }else {
            bt_dianJiaReGaoWen.setBackgroundResource(R.drawable.led_gray);
        }

        if (data_AHU301_fengJiQueFeng){
            bt_fengJiQueFeng.setBackgroundResource(R.drawable.led_red);
        }else {
            bt_fengJiQueFeng.setBackgroundResource(R.drawable.led_gray);
        }

        if (data_AHU301_paiFengJiYiQiDong){
            bt_paiFengJiYiQiDong.setBackgroundResource(R.drawable.led_green);
        }else {
            bt_paiFengJiYiQiDong.setBackgroundResource(R.drawable.led_gray);
        }

        if (data_AHU301_diWenPanGuan){
            bt_diWenPanGuan.setBackgroundResource(R.drawable.led_red);
        }else {
            bt_diWenPanGuan.setBackgroundResource(R.drawable.led_gray);
        }

        if (data_AHU301_mieJunYunXing){
            bt_mieJunYunXing.setBackgroundResource(R.drawable.led_green);
        }else {
            bt_mieJunYunXing.setBackgroundResource(R.drawable.led_gray);
        }
    }

    private void updateAhu302() {

        tv_data_tempReal.setText(formatValue(data_AHU302_tempReal/10.0,0.1)+"℃");
        tv_data_humiReal.setText(formatValue(data_AHU302_humiReal/10.0,0.1)+"RH");
        tv_data_tempSet.setText(formatValue(data_AHU302_tempSet/10.0,0.1)+"℃");
        tv_data_humiSet.setText(formatValue(data_AHU302_humiSet/10.0,0.1)+"RH");
        tv_data_lengShuiFaKaiDu.setText(formatValue(data_AHU302_lengShuiFaKaiDu/10.0,0.1)+"%");
        tv_data_reShuiFaKaiDu.setText(formatValue(data_AHU302_reShuiFaKaiDu/10.0,0.1)+"%");
        tv_data_jiaShiQIKaiDu.setText(formatValue(data_AHU302_jiaShiQIKaiDu/10.0,0.1)+"%");

        if (data_AHU302_dongXiaJi){
            tv_dongXiaJi.setText("夏季");
        }else{
            tv_dongXiaJi.setText("冬季");
        }

        if (data_AHU302_shouZiDong){
            tv_shouZiDong.setText("手动");
        }else {
            tv_shouZiDong.setText("自动");
        }

        if (data_AHU302_fengJiYiQiDong){
            bt_fengJiYiQiDong.setBackgroundResource(R.drawable.led_green);
        }else {
            bt_fengJiYiQiDong.setBackgroundResource(R.drawable.led_gray);
        }

        if (data_AHU302_zhiBanStatus){
            bt_zhiBanStatus.setBackgroundResource(R.drawable.led_green);
        }else {
            bt_zhiBanStatus.setBackgroundResource(R.drawable.led_gray);
        }

        if (data_AHU302_dianJiaRe1){
            bt_dianJiaRe1.setBackgroundResource(R.drawable.led_green);
        }else {
            bt_dianJiaRe1.setBackgroundResource(R.drawable.led_gray);
        }

        if (data_AHU302_dianJiaRe2){
            bt_dianJiaRe2.setBackgroundResource(R.drawable.led_green);
        }else {
            bt_dianJiaRe2.setBackgroundResource(R.drawable.led_gray);
        }

        if (data_AHU302_dianJiaRe3){
            bt_dianJiaRe3.setBackgroundResource(R.drawable.led_green);
        }else {
            bt_dianJiaRe3.setBackgroundResource(R.drawable.led_gray);
        }

        if (data_AHU302_fengJiStatus){
            bt_fengJiStatus.setBackgroundResource(R.drawable.led_green);
        }else {
            bt_fengJiStatus.setBackgroundResource(R.drawable.led_gray);
        }

        if (data_AHU302_zhongXiaoBaoJing){
            bt_zhongXiaoBaoJing.setBackgroundResource(R.drawable.led_red);
        }else {
            bt_zhongXiaoBaoJing.setBackgroundResource(R.drawable.led_gray);
        }

        if (data_AHU302_dianJiaReGaoWen){
            bt_dianJiaReGaoWen.setBackgroundResource(R.drawable.led_red);
        }else {
            bt_dianJiaReGaoWen.setBackgroundResource(R.drawable.led_gray);
        }

        if (data_AHU302_fengJiQueFeng){
            bt_fengJiQueFeng.setBackgroundResource(R.drawable.led_red);
        }else {
            bt_fengJiQueFeng.setBackgroundResource(R.drawable.led_gray);
        }

        if (data_AHU302_paiFengJiYiQiDong){
            bt_paiFengJiYiQiDong.setBackgroundResource(R.drawable.led_green);
        }else {
            bt_paiFengJiYiQiDong.setBackgroundResource(R.drawable.led_gray);
        }

        if (data_AHU302_diWenPanGuan){
            bt_diWenPanGuan.setBackgroundResource(R.drawable.led_red);
        }else {
            bt_diWenPanGuan.setBackgroundResource(R.drawable.led_gray);
        }

        if (data_AHU302_mieJunYunXing){
            bt_mieJunYunXing.setBackgroundResource(R.drawable.led_green);
        }else {
            bt_mieJunYunXing.setBackgroundResource(R.drawable.led_gray);
        }

    }

    private void updateAhu303() {

        tv_data_tempReal.setText(formatValue(data_AHU303_tempReal/10.0,0.1)+"℃");
        tv_data_humiReal.setText(formatValue(data_AHU303_humiReal/10.0,0.1)+"RH");
        tv_data_tempSet.setText(formatValue(data_AHU303_tempSet/10.0,0.1)+"℃");
        tv_data_humiSet.setText(formatValue(data_AHU303_humiSet/10.0,0.1)+"RH");
        tv_data_lengShuiFaKaiDu.setText(formatValue(data_AHU303_lengShuiFaKaiDu/10.0,0.1)+"%");
        tv_data_reShuiFaKaiDu.setText(formatValue(data_AHU303_reShuiFaKaiDu/10.0,0.1)+"%");
        tv_data_jiaShiQIKaiDu.setText(formatValue(data_AHU303_jiaShiQIKaiDu/10.0,0.1)+"%");

        if (data_AHU303_dongXiaJi){
            tv_dongXiaJi.setText("夏季");
        }else{
            tv_dongXiaJi.setText("冬季");
        }

        if (data_AHU303_shouZiDong){
            tv_shouZiDong.setText("手动");
        }else {
            tv_shouZiDong.setText("自动");
        }

        if (data_AHU303_fengJiYiQiDong){
            bt_fengJiYiQiDong.setBackgroundResource(R.drawable.led_green);
        }else {
            bt_fengJiYiQiDong.setBackgroundResource(R.drawable.led_gray);
        }

        if (data_AHU303_zhiBanStatus){
            bt_zhiBanStatus.setBackgroundResource(R.drawable.led_green);
        }else {
            bt_zhiBanStatus.setBackgroundResource(R.drawable.led_gray);
        }

        if (data_AHU303_dianJiaRe1){
            bt_dianJiaRe1.setBackgroundResource(R.drawable.led_green);
        }else {
            bt_dianJiaRe1.setBackgroundResource(R.drawable.led_gray);
        }

        if (data_AHU303_dianJiaRe2){
            bt_dianJiaRe2.setBackgroundResource(R.drawable.led_green);
        }else {
            bt_dianJiaRe2.setBackgroundResource(R.drawable.led_gray);
        }

        if (data_AHU303_dianJiaRe3){
            bt_dianJiaRe3.setBackgroundResource(R.drawable.led_green);
        }else {
            bt_dianJiaRe3.setBackgroundResource(R.drawable.led_gray);
        }

        if (data_AHU303_fengJiStatus){
            bt_fengJiStatus.setBackgroundResource(R.drawable.led_green);
        }else {
            bt_fengJiStatus.setBackgroundResource(R.drawable.led_gray);
        }

        if (data_AHU303_zhongXiaoBaoJing){
            bt_zhongXiaoBaoJing.setBackgroundResource(R.drawable.led_red);
        }else {
            bt_zhongXiaoBaoJing.setBackgroundResource(R.drawable.led_gray);
        }

        if (data_AHU303_dianJiaReGaoWen){
            bt_dianJiaReGaoWen.setBackgroundResource(R.drawable.led_red);
        }else {
            bt_dianJiaReGaoWen.setBackgroundResource(R.drawable.led_gray);
        }

        if (data_AHU303_fengJiQueFeng){
            bt_fengJiQueFeng.setBackgroundResource(R.drawable.led_red);
        }else {
            bt_fengJiQueFeng.setBackgroundResource(R.drawable.led_gray);
        }

        if (data_AHU303_paiFengJiYiQiDong){
            bt_paiFengJiYiQiDong.setBackgroundResource(R.drawable.led_green);
        }else {
            bt_paiFengJiYiQiDong.setBackgroundResource(R.drawable.led_gray);
        }

        if (data_AHU303_diWenPanGuan){
            bt_diWenPanGuan.setBackgroundResource(R.drawable.led_red);
        }else {
            bt_diWenPanGuan.setBackgroundResource(R.drawable.led_gray);
        }

        if (data_AHU303_mieJunYunXing){
            bt_mieJunYunXing.setBackgroundResource(R.drawable.led_green);
        }else {
            bt_mieJunYunXing.setBackgroundResource(R.drawable.led_gray);
        }

    }

    private void updateAhu304() {

        tv_data_tempReal.setText(formatValue(data_AHU304_tempReal/10.0,0.1)+"℃");
        tv_data_humiReal.setText(formatValue(data_AHU304_humiReal/10.0,0.1)+"RH");
        tv_data_tempSet.setText(formatValue(data_AHU304_tempSet/10.0,0.1)+"℃");
        tv_data_humiSet.setText(formatValue(data_AHU304_humiSet/10.0,0.1)+"RH");
        tv_data_lengShuiFaKaiDu.setText(formatValue(data_AHU304_lengShuiFaKaiDu/10.0,0.1)+"%");
        tv_data_reShuiFaKaiDu.setText(formatValue(data_AHU304_reShuiFaKaiDu/10.0,0.1)+"%");
        tv_data_jiaShiQIKaiDu.setText(formatValue(data_AHU304_jiaShiQIKaiDu/10.0,0.1)+"%");

        if (data_AHU304_dongXiaJi){
            tv_dongXiaJi.setText("夏季");
        }else{
            tv_dongXiaJi.setText("冬季");
        }

        if (data_AHU304_shouZiDong){
            tv_shouZiDong.setText("手动");
        }else {
            tv_shouZiDong.setText("自动");
        }

        if (data_AHU304_fengJiYiQiDong){
            bt_fengJiYiQiDong.setBackgroundResource(R.drawable.led_green);
        }else {
            bt_fengJiYiQiDong.setBackgroundResource(R.drawable.led_gray);
        }

        if (data_AHU304_zhiBanStatus){
            bt_zhiBanStatus.setBackgroundResource(R.drawable.led_green);
        }else {
            bt_zhiBanStatus.setBackgroundResource(R.drawable.led_gray);
        }

        if (data_AHU304_dianJiaRe1){
            bt_dianJiaRe1.setBackgroundResource(R.drawable.led_green);
        }else {
            bt_dianJiaRe1.setBackgroundResource(R.drawable.led_gray);
        }

        if (data_AHU304_dianJiaRe2){
            bt_dianJiaRe2.setBackgroundResource(R.drawable.led_green);
        }else {
            bt_dianJiaRe2.setBackgroundResource(R.drawable.led_gray);
        }

        if (data_AHU304_dianJiaRe3){
            bt_dianJiaRe3.setBackgroundResource(R.drawable.led_green);
        }else {
            bt_dianJiaRe3.setBackgroundResource(R.drawable.led_gray);
        }

        if (data_AHU304_fengJiStatus){
            bt_fengJiStatus.setBackgroundResource(R.drawable.led_green);
        }else {
            bt_fengJiStatus.setBackgroundResource(R.drawable.led_gray);
        }

        if (data_AHU304_zhongXiaoBaoJing){
            bt_zhongXiaoBaoJing.setBackgroundResource(R.drawable.led_red);
        }else {
            bt_zhongXiaoBaoJing.setBackgroundResource(R.drawable.led_gray);
        }

        if (data_AHU304_dianJiaReGaoWen){
            bt_dianJiaReGaoWen.setBackgroundResource(R.drawable.led_red);
        }else {
            bt_dianJiaReGaoWen.setBackgroundResource(R.drawable.led_gray);
        }

        if (data_AHU304_fengJiQueFeng){
            bt_fengJiQueFeng.setBackgroundResource(R.drawable.led_red);
        }else {
            bt_fengJiQueFeng.setBackgroundResource(R.drawable.led_gray);
        }

        if (data_AHU304_paiFengJiYiQiDong){
            bt_paiFengJiYiQiDong.setBackgroundResource(R.drawable.led_green);
        }else {
            bt_paiFengJiYiQiDong.setBackgroundResource(R.drawable.led_gray);
        }

        if (data_AHU304_diWenPanGuan){
            bt_diWenPanGuan.setBackgroundResource(R.drawable.led_red);
        }else {
            bt_diWenPanGuan.setBackgroundResource(R.drawable.led_gray);
        }

        if (data_AHU304_mieJunYunXing){
            bt_mieJunYunXing.setBackgroundResource(R.drawable.led_green);
        }else {
            bt_mieJunYunXing.setBackgroundResource(R.drawable.led_gray);
        }

    }

    private void updateAhu305() {

        tv_data_tempReal.setText(formatValue(data_AHU305_tempReal/10.0,0.1)+"℃");
        tv_data_humiReal.setText(formatValue(data_AHU305_humiReal/10.0,0.1)+"RH");
        tv_data_tempSet.setText(formatValue(data_AHU305_tempSet/10.0,0.1)+"℃");
        tv_data_humiSet.setText(formatValue(data_AHU305_humiSet/10.0,0.1)+"RH");
        tv_data_lengShuiFaKaiDu.setText(formatValue(data_AHU305_lengShuiFaKaiDu/10.0,0.1)+"%");
        tv_data_reShuiFaKaiDu.setText(formatValue(data_AHU305_reShuiFaKaiDu/10.0,0.1)+"%");
        tv_data_jiaShiQIKaiDu.setText(formatValue(data_AHU305_jiaShiQIKaiDu/10.0,0.1)+"%");

        if (data_AHU305_dongXiaJi){
            tv_dongXiaJi.setText("夏季");
        }else{
            tv_dongXiaJi.setText("冬季");
        }

        if (data_AHU305_shouZiDong){
            tv_shouZiDong.setText("手动");
        }else {
            tv_shouZiDong.setText("自动");
        }

        if (data_AHU305_fengJiYiQiDong){
            bt_fengJiYiQiDong.setBackgroundResource(R.drawable.led_green);
        }else {
            bt_fengJiYiQiDong.setBackgroundResource(R.drawable.led_gray);
        }

        if (data_AHU305_zhiBanStatus){
            bt_zhiBanStatus.setBackgroundResource(R.drawable.led_green);
        }else {
            bt_zhiBanStatus.setBackgroundResource(R.drawable.led_gray);
        }

        if (data_AHU305_dianJiaRe1){
            bt_dianJiaRe1.setBackgroundResource(R.drawable.led_green);
        }else {
            bt_dianJiaRe1.setBackgroundResource(R.drawable.led_gray);
        }

        if (data_AHU305_dianJiaRe2){
            bt_dianJiaRe2.setBackgroundResource(R.drawable.led_green);
        }else {
            bt_dianJiaRe2.setBackgroundResource(R.drawable.led_gray);
        }

        if (data_AHU305_dianJiaRe3){
            bt_dianJiaRe3.setBackgroundResource(R.drawable.led_green);
        }else {
            bt_dianJiaRe3.setBackgroundResource(R.drawable.led_gray);
        }

        if (data_AHU305_fengJiStatus){
            bt_fengJiStatus.setBackgroundResource(R.drawable.led_green);
        }else {
            bt_fengJiStatus.setBackgroundResource(R.drawable.led_gray);
        }

        if (data_AHU305_zhongXiaoBaoJing){
            bt_zhongXiaoBaoJing.setBackgroundResource(R.drawable.led_red);
        }else {
            bt_zhongXiaoBaoJing.setBackgroundResource(R.drawable.led_gray);
        }

        if (data_AHU305_dianJiaReGaoWen){
            bt_dianJiaReGaoWen.setBackgroundResource(R.drawable.led_red);
        }else {
            bt_dianJiaReGaoWen.setBackgroundResource(R.drawable.led_gray);
        }

        if (data_AHU305_fengJiQueFeng){
            bt_fengJiQueFeng.setBackgroundResource(R.drawable.led_red);
        }else {
            bt_fengJiQueFeng.setBackgroundResource(R.drawable.led_gray);
        }

        if (data_AHU305_paiFengJiYiQiDong){
            bt_paiFengJiYiQiDong.setBackgroundResource(R.drawable.led_green);
        }else {
            bt_paiFengJiYiQiDong.setBackgroundResource(R.drawable.led_gray);
        }

        if (data_AHU305_diWenPanGuan){
            bt_diWenPanGuan.setBackgroundResource(R.drawable.led_red);
        }else {
            bt_diWenPanGuan.setBackgroundResource(R.drawable.led_gray);
        }

        if (data_AHU305_mieJunYunXing){
            bt_mieJunYunXing.setBackgroundResource(R.drawable.led_green);
        }else {
            bt_mieJunYunXing.setBackgroundResource(R.drawable.led_gray);
        }

    }

    private void updateAhu306() {

        tv_data_tempReal.setText(data_AHU306_tempReal+"℃");
        tv_data_humiReal.setText(data_AHU306_humiReal+"RH");
        tv_data_tempSet.setText(data_AHU306_tempSet+"℃");
        tv_data_humiSet.setText(data_AHU306_humiSet+"RH");
        tv_data_lengShuiFaKaiDu.setText(data_AHU306_lengShuiFaKaiDu+"%");
        tv_data_reShuiFaKaiDu.setText(data_AHU306_reShuiFaKaiDu+"%");
        tv_data_jiaShiQIKaiDu.setText(data_AHU306_jiaShiQIKaiDu+"%");

        if (data_AHU306_dongXiaJi){
            tv_dongXiaJi.setText("夏季");
        }else{
            tv_dongXiaJi.setText("冬季");
        }

        if (data_AHU306_shouZiDong){
            tv_shouZiDong.setText("手动");
        }else {
            tv_shouZiDong.setText("自动");
        }

        if (data_AHU306_fengJiYiQiDong){
            bt_fengJiYiQiDong.setBackgroundResource(R.drawable.led_green);
        }else {
            bt_fengJiYiQiDong.setBackgroundResource(R.drawable.led_gray);
        }

        if (data_AHU306_zhiBanStatus){
            bt_zhiBanStatus.setBackgroundResource(R.drawable.led_green);
        }else {
            bt_zhiBanStatus.setBackgroundResource(R.drawable.led_gray);
        }

        if (data_AHU306_dianJiaRe1){
            bt_dianJiaRe1.setBackgroundResource(R.drawable.led_green);
        }else {
            bt_dianJiaRe1.setBackgroundResource(R.drawable.led_gray);
        }

        if (data_AHU306_dianJiaRe2){
            bt_dianJiaRe2.setBackgroundResource(R.drawable.led_green);
        }else {
            bt_dianJiaRe2.setBackgroundResource(R.drawable.led_gray);
        }

        if (data_AHU306_dianJiaRe3){
            bt_dianJiaRe3.setBackgroundResource(R.drawable.led_green);
        }else {
            bt_dianJiaRe3.setBackgroundResource(R.drawable.led_gray);
        }

        if (data_AHU306_fengJiStatus){
            bt_fengJiStatus.setBackgroundResource(R.drawable.led_green);
        }else {
            bt_fengJiStatus.setBackgroundResource(R.drawable.led_gray);
        }

        if (data_AHU306_zhongXiaoBaoJing){
            bt_zhongXiaoBaoJing.setBackgroundResource(R.drawable.led_red);
        }else {
            bt_zhongXiaoBaoJing.setBackgroundResource(R.drawable.led_gray);
        }

        if (data_AHU306_dianJiaReGaoWen){
            bt_dianJiaReGaoWen.setBackgroundResource(R.drawable.led_red);
        }else {
            bt_dianJiaReGaoWen.setBackgroundResource(R.drawable.led_gray);
        }

        if (data_AHU306_fengJiQueFeng){
            bt_fengJiQueFeng.setBackgroundResource(R.drawable.led_red);
        }else {
            bt_fengJiQueFeng.setBackgroundResource(R.drawable.led_gray);
        }

        if (data_AHU306_paiFengJiYiQiDong){
            bt_paiFengJiYiQiDong.setBackgroundResource(R.drawable.led_green);
        }else {
            bt_paiFengJiYiQiDong.setBackgroundResource(R.drawable.led_gray);
        }

        if (data_AHU306_diWenPanGuan){
            bt_diWenPanGuan.setBackgroundResource(R.drawable.led_red);
        }else {
            bt_diWenPanGuan.setBackgroundResource(R.drawable.led_gray);
        }

        if (data_AHU306_mieJunYunXing){
            bt_mieJunYunXing.setBackgroundResource(R.drawable.led_green);
        }else {
            bt_mieJunYunXing.setBackgroundResource(R.drawable.led_gray);
        }

    }

    private void updateAhu307() {

        tv_data_tempReal.setText(data_AHU307_tempReal+"℃");
        tv_data_humiReal.setText(data_AHU307_humiReal+"RH");
        tv_data_tempSet.setText(data_AHU307_tempSet+"℃");
        tv_data_humiSet.setText(data_AHU307_humiSet+"RH");
        tv_data_lengShuiFaKaiDu.setText(data_AHU307_lengShuiFaKaiDu+"%");
        tv_data_reShuiFaKaiDu.setText(data_AHU307_reShuiFaKaiDu+"%");
        tv_data_jiaShiQIKaiDu.setText(data_AHU307_jiaShiQIKaiDu+"%");

        if (data_AHU307_dongXiaJi){
            tv_dongXiaJi.setText("夏季");
        }else{
            tv_dongXiaJi.setText("冬季");
        }

        if (data_AHU307_shouZiDong){
            tv_shouZiDong.setText("手动");
        }else {
            tv_shouZiDong.setText("自动");
        }

        if (data_AHU307_fengJiYiQiDong){
            bt_fengJiYiQiDong.setBackgroundResource(R.drawable.led_green);
        }else {
            bt_fengJiYiQiDong.setBackgroundResource(R.drawable.led_gray);
        }

        if (data_AHU307_zhiBanStatus){
            bt_zhiBanStatus.setBackgroundResource(R.drawable.led_green);
        }else {
            bt_zhiBanStatus.setBackgroundResource(R.drawable.led_gray);
        }

        if (data_AHU307_dianJiaRe1){
            bt_dianJiaRe1.setBackgroundResource(R.drawable.led_green);
        }else {
            bt_dianJiaRe1.setBackgroundResource(R.drawable.led_gray);
        }

        if (data_AHU307_dianJiaRe2){
            bt_dianJiaRe2.setBackgroundResource(R.drawable.led_green);
        }else {
            bt_dianJiaRe2.setBackgroundResource(R.drawable.led_gray);
        }

        if (data_AHU307_dianJiaRe3){
            bt_dianJiaRe3.setBackgroundResource(R.drawable.led_green);
        }else {
            bt_dianJiaRe3.setBackgroundResource(R.drawable.led_gray);
        }

        if (data_AHU307_fengJiStatus){
            bt_fengJiStatus.setBackgroundResource(R.drawable.led_green);
        }else {
            bt_fengJiStatus.setBackgroundResource(R.drawable.led_gray);
        }

        if (data_AHU307_zhongXiaoBaoJing){
            bt_zhongXiaoBaoJing.setBackgroundResource(R.drawable.led_red);
        }else {
            bt_zhongXiaoBaoJing.setBackgroundResource(R.drawable.led_gray);
        }

        if (data_AHU307_dianJiaReGaoWen){
            bt_dianJiaReGaoWen.setBackgroundResource(R.drawable.led_red);
        }else {
            bt_dianJiaReGaoWen.setBackgroundResource(R.drawable.led_gray);
        }

        if (data_AHU307_fengJiQueFeng){
            bt_fengJiQueFeng.setBackgroundResource(R.drawable.led_red);
        }else {
            bt_fengJiQueFeng.setBackgroundResource(R.drawable.led_gray);
        }

        if (data_AHU307_paiFengJiYiQiDong){
            bt_paiFengJiYiQiDong.setBackgroundResource(R.drawable.led_green);
        }else {
            bt_paiFengJiYiQiDong.setBackgroundResource(R.drawable.led_gray);
        }

        if (data_AHU307_diWenPanGuan){
            bt_diWenPanGuan.setBackgroundResource(R.drawable.led_red);
        }else {
            bt_diWenPanGuan.setBackgroundResource(R.drawable.led_gray);
        }

        if (data_AHU307_mieJunYunXing){
            bt_mieJunYunXing.setBackgroundResource(R.drawable.led_green);
        }else {
            bt_mieJunYunXing.setBackgroundResource(R.drawable.led_gray);
        }

    }

    private void updateAhu308() {

        tv_data_tempReal.setText(data_AHU308_tempReal+"℃");
        tv_data_humiReal.setText(data_AHU308_humiReal+"RH");
        tv_data_tempSet.setText(data_AHU308_tempSet+"℃");
        tv_data_humiSet.setText(data_AHU308_humiSet+"RH");
        tv_data_lengShuiFaKaiDu.setText(data_AHU308_lengShuiFaKaiDu+"%");
        tv_data_reShuiFaKaiDu.setText(data_AHU308_reShuiFaKaiDu+"%");
        tv_data_jiaShiQIKaiDu.setText(data_AHU308_jiaShiQIKaiDu+"%");

        if (data_AHU308_dongXiaJi){
            tv_dongXiaJi.setText("夏季");
        }else{
            tv_dongXiaJi.setText("冬季");
        }

        if (data_AHU308_shouZiDong){
            tv_shouZiDong.setText("手动");
        }else {
            tv_shouZiDong.setText("自动");
        }

        if (data_AHU308_fengJiYiQiDong){
            bt_fengJiYiQiDong.setBackgroundResource(R.drawable.led_green);
        }else {
            bt_fengJiYiQiDong.setBackgroundResource(R.drawable.led_gray);
        }

        if (data_AHU308_zhiBanStatus){
            bt_zhiBanStatus.setBackgroundResource(R.drawable.led_green);
        }else {
            bt_zhiBanStatus.setBackgroundResource(R.drawable.led_gray);
        }

        if (data_AHU308_dianJiaRe1){
            bt_dianJiaRe1.setBackgroundResource(R.drawable.led_green);
        }else {
            bt_dianJiaRe1.setBackgroundResource(R.drawable.led_gray);
        }

        if (data_AHU308_dianJiaRe2){
            bt_dianJiaRe2.setBackgroundResource(R.drawable.led_green);
        }else {
            bt_dianJiaRe2.setBackgroundResource(R.drawable.led_gray);
        }

        if (data_AHU308_dianJiaRe3){
            bt_dianJiaRe3.setBackgroundResource(R.drawable.led_green);
        }else {
            bt_dianJiaRe3.setBackgroundResource(R.drawable.led_gray);
        }

        if (data_AHU308_fengJiStatus){
            bt_fengJiStatus.setBackgroundResource(R.drawable.led_green);
        }else {
            bt_fengJiStatus.setBackgroundResource(R.drawable.led_gray);
        }

        if (data_AHU308_zhongXiaoBaoJing){
            bt_zhongXiaoBaoJing.setBackgroundResource(R.drawable.led_red);
        }else {
            bt_zhongXiaoBaoJing.setBackgroundResource(R.drawable.led_gray);
        }

        if (data_AHU308_dianJiaReGaoWen){
            bt_dianJiaReGaoWen.setBackgroundResource(R.drawable.led_red);
        }else {
            bt_dianJiaReGaoWen.setBackgroundResource(R.drawable.led_gray);
        }

        if (data_AHU308_fengJiQueFeng){
            bt_fengJiQueFeng.setBackgroundResource(R.drawable.led_red);
        }else {
            bt_fengJiQueFeng.setBackgroundResource(R.drawable.led_gray);
        }

        if (data_AHU308_paiFengJiYiQiDong){
            bt_paiFengJiYiQiDong.setBackgroundResource(R.drawable.led_green);
        }else {
            bt_paiFengJiYiQiDong.setBackgroundResource(R.drawable.led_gray);
        }

        if (data_AHU308_diWenPanGuan){
            bt_diWenPanGuan.setBackgroundResource(R.drawable.led_red);
        }else {
            bt_diWenPanGuan.setBackgroundResource(R.drawable.led_gray);
        }

        if (data_AHU308_mieJunYunXing){
            bt_mieJunYunXing.setBackgroundResource(R.drawable.led_green);
        }else {
            bt_mieJunYunXing.setBackgroundResource(R.drawable.led_gray);
        }

    }


    private void setEditText(EditText et, Object value) {
        et.setText(value.toString());
        et.setSelection(value.toString().length());
        et.clearFocus();
    }

    /**
     * Description:页面加载后弹出等待框，等待设备可被控制状态回调，如果一直不可被控，等待一段时间后自动退出界面
     */
    private void getStatusOfDevice() {
        // 设备是否可控
        if (isDeviceCanBeControlled()) {
            // 可控则查询当前设备状态
            mDevice.getDeviceStatus();
        } else {
            // 显示等待栏
            progressDialog.show();
            if (mDevice.isLAN()) {
                // 小循环10s未连接上设备自动退出
                mHandler.postDelayed(mRunnable, 10000);
            } else {
                // 大循环20s未连接上设备自动退出
                mHandler.postDelayed(mRunnable, 20000);
            }
        }
    }

    /**
     * 发送指令,下发单个数据点的命令可以用这个方法
     *
     * <h3>注意</h3>
     * <p>
     * 下发多个数据点命令不能用这个方法多次调用，一次性多次调用这个方法会导致模组无法正确接收消息，参考方法内注释。
     * </p>
     *
     * @param key
     *            数据点对应的标识名
     * @param value
     *            需要改变的值
     */
    private void sendCommand(String key, Object value) {
        if (value == null) {
            return;
        }
        int sn = 5;
        ConcurrentHashMap<String, Object> hashMap = new ConcurrentHashMap<String, Object>();
        hashMap.put(key, value);
        // 同时下发多个数据点需要一次性在map中放置全部需要控制的key，value值
        // hashMap.put(key2, value2);
        // hashMap.put(key3, value3);
        mDevice.write(hashMap, sn);
        Log.i("liang", "下发命令：" + hashMap.toString());
    }

    private boolean isDeviceCanBeControlled() {
        return mDevice.getNetStatus() == GizWifiDeviceNetStatus.GizDeviceControlled;
    }

    private void toastDeviceNoReadyAndExit() {
        Toast.makeText(this, "设备无响应，请检查设备是否正常工作", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void toastDeviceDisconnectAndExit() {
        Toast.makeText(AhuDeviceDataActivity.this, "连接已断开", Toast.LENGTH_SHORT).show();
        finish();
    }

    /**
     * 展示设备硬件信息
     *
     * @param hardwareInfo
     */
    private void showHardwareInfo(String hardwareInfo) {
        String hardwareInfoTitle = "设备硬件信息";
        new AlertDialog.Builder(this).setTitle(hardwareInfoTitle).setMessage(hardwareInfo)
                .setPositiveButton(R.string.besure, null).show();
    }

    /**
     * Description:设置设备别名与备注
     */
    private void setDeviceInfo() {

        final Dialog mDialog = new AlertDialog.Builder(this,R.style.edit_dialog_style).setView(new EditText(this)).create();
        mDialog.show();

        Window window = mDialog.getWindow();
        window.setContentView(R.layout.alert_gos_set_device_info);
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(layoutParams);
        final EditText etAlias;
        final EditText etRemark;
        etAlias = (EditText) window.findViewById(R.id.etAlias);
        etRemark = (EditText) window.findViewById(R.id.etRemark);

        LinearLayout llNo, llSure;
        llNo = (LinearLayout) window.findViewById(R.id.llNo);
        llSure = (LinearLayout) window.findViewById(R.id.llSure);

        if (!TextUtils.isEmpty(mDevice.getAlias())) {
            setEditText(etAlias, mDevice.getAlias());
        }
        if (!TextUtils.isEmpty(mDevice.getRemark())) {
            setEditText(etRemark, mDevice.getRemark());
        }

        llNo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });

        llSure.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(etRemark.getText().toString())
                        && TextUtils.isEmpty(etAlias.getText().toString())) {
                    myToast("请输入设备别名或备注！");
                    return;
                }
                mDevice.setCustomInfo(etRemark.getText().toString(), etAlias.getText().toString());
                mDialog.dismiss();
                String loadingText = (String) getText(R.string.loadingtext);
                progressDialog.setMessage(loadingText);
                progressDialog.show();
            }
        });

        mDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                hideKeyBoard();
            }
        });
    }

    /*
     * 获取设备硬件信息回调
     */
    @Override
    protected void didGetHardwareInfo(GizWifiErrorCode result, GizWifiDevice device,
                                      ConcurrentHashMap<String, String> hardwareInfo) {
        super.didGetHardwareInfo(result, device, hardwareInfo);
        StringBuffer sb = new StringBuffer();
        if (GizWifiErrorCode.GIZ_SDK_SUCCESS != result) {
            myToast("获取设备硬件信息失败：" + result.name());
        } else {
            sb.append("Wifi Hardware Version:" + hardwareInfo.get(WIFI_HARDVER_KEY) + "\r\n");
            sb.append("Wifi Software Version:" + hardwareInfo.get(WIFI_SOFTVER_KEY) + "\r\n");
            sb.append("MCU Hardware Version:" + hardwareInfo.get(MCU_HARDVER_KEY) + "\r\n");
            sb.append("MCU Software Version:" + hardwareInfo.get(MCU_SOFTVER_KEY) + "\r\n");
            sb.append("Wifi Firmware Id:" + hardwareInfo.get(WIFI_FIRMWAREID_KEY) + "\r\n");
            sb.append("Wifi Firmware Version:" + hardwareInfo.get(WIFI_FIRMWAREVER_KEY) + "\r\n");
            sb.append("Product Key:" + "\r\n" + hardwareInfo.get(PRODUCT_KEY) + "\r\n");

            // 设备属性
            sb.append("Device ID:" + "\r\n" + mDevice.getDid() + "\r\n");
            sb.append("Device IP:" + mDevice.getIPAddress() + "\r\n");
            sb.append("Device MAC:" + mDevice.getMacAddress() + "\r\n");
        }
        showHardwareInfo(sb.toString());
    }

    /*
     * 设置设备别名和备注回调
     */
    @Override
    protected void didSetCustomInfo(GizWifiErrorCode result, GizWifiDevice device) {
        super.didSetCustomInfo(result, device);
        if (GizWifiErrorCode.GIZ_SDK_SUCCESS == result) {
            myToast("设置成功");
            progressDialog.cancel();
            finish();
        } else {
            myToast("设置失败：" + result.name());
        }
    }

    /*
     * 设备状态改变回调，只有设备状态为可控才可以下发控制命令
     */
    @Override
    protected void didUpdateNetStatus(GizWifiDevice device, GizWifiDeviceNetStatus netStatus) {
        super.didUpdateNetStatus(device, netStatus);
        if (netStatus == GizWifiDeviceNetStatus.GizDeviceControlled) {
            mHandler.removeCallbacks(mRunnable);
            progressDialog.cancel();
        } else {
            mHandler.sendEmptyMessage(AhuDeviceDataActivity.handler_key.DISCONNECT.ordinal());
        }
    }

    /*
     * 设备上报数据回调，此回调包括设备主动上报数据、下发控制命令成功后设备返回ACK
     */
    @Override
    protected void didReceiveData(GizWifiErrorCode result, GizWifiDevice device,
                                  ConcurrentHashMap<String, Object> dataMap, int sn) {
        super.didReceiveData(result, device, dataMap, sn);
        Log.i("liang", "接收到数据");
        if (result == GizWifiErrorCode.GIZ_SDK_SUCCESS && dataMap.get("data") != null) {
            getDataFromReceiveDataMap(dataMap);
            mHandler.sendEmptyMessage(AhuDeviceDataActivity.handler_key.UPDATE_UI.ordinal());
        }
    }
}

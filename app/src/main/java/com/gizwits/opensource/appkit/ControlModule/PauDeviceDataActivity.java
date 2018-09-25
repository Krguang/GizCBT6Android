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

import java.text.DecimalFormat;
import java.util.concurrent.ConcurrentHashMap;

public class PauDeviceDataActivity extends GosControlModuleBaseActivity {


    private static final String TAG = "test";
    private GizWifiDevice mDevice;

    private TextView tv_data_tempReal;
    private TextView tv_data_xinFengWenDu;
    private TextView tv_data_tempSet;
    private TextView tv_data_lengShuiFaKaiDu;
    private TextView tv_data_reShuiFaKaiDu;

    private TextView tv_dongXiaJi;
    private TextView tv_shouZiDong;

    private Button bt_fengJiYiQiDong;
    private Button bt_dianYuRe1;
    private Button bt_dianYuRe2;
    private Button bt_dianYuRe3;
    private Button bt_fengJiStatus;
    private Button bt_zhongXiaoBaoJing;
    private Button bt_dianYuReGaoWen;
    private Button bt_fengJiQueFeng;
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
            PauDeviceDataActivity.handler_key key = PauDeviceDataActivity.handler_key.values()[msg.what];
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
        setContentView(R.layout.activity_k_pau);

        initDevice();
        Log.d(TAG, "onCreate: "+"initDevice()");
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
        tv_data_xinFengWenDu = (TextView) findViewById(R.id.tv_data_xinFengWenDU);
        tv_data_tempSet = (TextView) findViewById(R.id.tv_data_tempSet);
        tv_data_lengShuiFaKaiDu = (TextView) findViewById(R.id.tv_data_lengShuiFaKaiDu);
        tv_data_reShuiFaKaiDu = (TextView) findViewById(R.id.tv_data_reShuiFaKaiDu);


        tv_dongXiaJi = (TextView) findViewById(R.id.tv_dongXiaJi);
        tv_shouZiDong = (TextView) findViewById(R.id.tv_shouZiDong);

        bt_fengJiYiQiDong = (Button) findViewById(R.id.bt_fengJiYiQiDong);
        bt_dianYuRe1 = (Button) findViewById(R.id.bt_dianYuRe1);
        bt_dianYuRe2 = (Button) findViewById(R.id.bt_dianYuRe2);
        bt_dianYuRe3 = (Button) findViewById(R.id.bt_dianYuRe3);
        bt_fengJiStatus = (Button) findViewById(R.id.bt_fengJiStatus);
        bt_zhongXiaoBaoJing = (Button) findViewById(R.id.bt_zhongXiaoBaoJing);
        bt_dianYuReGaoWen = (Button) findViewById(R.id.bt_dianYuReGaoWen);
        bt_fengJiQueFeng = (Button) findViewById(R.id.bt_fengJiQueFeng);
        bt_diWenPanGuan = (Button) findViewById(R.id.bt_diWenPanGuan);
        bt_mieJunYunXing = (Button) findViewById(R.id.bt_mieJunYunXing);

    }

    private void initEvent() {

        tv_data_tempReal.setText("00.0℃");
        tv_data_xinFengWenDu.setText("00.0℃");
        tv_data_tempSet.setText("00.0℃");
        tv_data_lengShuiFaKaiDu.setText("00.0%");
        tv_data_reShuiFaKaiDu.setText("00.0%");
        tv_dongXiaJi.setText("");
        tv_shouZiDong.setText("");
        bt_fengJiYiQiDong.setBackgroundResource(R.drawable.led_gray);
        bt_dianYuRe1.setBackgroundResource(R.drawable.led_gray);
        bt_dianYuRe2.setBackgroundResource(R.drawable.led_gray);
        bt_dianYuRe3.setBackgroundResource(R.drawable.led_gray);
        bt_fengJiStatus.setBackgroundResource(R.drawable.led_gray);
        bt_zhongXiaoBaoJing.setBackgroundResource(R.drawable.led_gray);
        bt_dianYuReGaoWen.setBackgroundResource(R.drawable.led_gray);
        bt_fengJiQueFeng.setBackgroundResource(R.drawable.led_gray);
        bt_diWenPanGuan.setBackgroundResource(R.drawable.led_gray);
        bt_mieJunYunXing.setBackgroundResource(R.drawable.led_gray);
    }


    private void initDevice() {
        Intent intent = getIntent();
        stringFromPrevious = intent.getStringExtra("extra_data");
        mDevice = (GizWifiDevice) intent.getParcelableExtra("GizWifiDevice");
        Log.d(TAG, "initDevice: mDevice = " + mDevice);
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

            case "PAU301":
                updatePau301();
                break;

            case "PAU302":
                updatePau302();
                break;

            case "PAU303":
                updatePau303();
                break;

            case "PAU304":
                updatePau304();
                break;

            case "PAU305":
                updatePau305();
                break;

            default:break;
        }
    }

    private void updatePau301() {

        tv_data_tempReal.setText(data_PAU301_tempReal+"℃");
        tv_data_xinFengWenDu.setText(data_PAU301_xinFengWenDU+"℃");
        tv_data_tempSet.setText(data_PAU301_tempSet+"℃");
        tv_data_lengShuiFaKaiDu.setText(data_PAU301_lengShuiFaKaiDu+"%");
        tv_data_reShuiFaKaiDu.setText(data_PAU301_reShuiFaKaiDu+"%");

        if (data_PAU301_dongXiaJi){
            tv_dongXiaJi.setText("夏季");
        }else{
            tv_dongXiaJi.setText("冬季");
        }

        if (data_PAU301_shouZiDong){
            tv_shouZiDong.setText("手动");
        }else {
            tv_shouZiDong.setText("自动");
        }

        if (data_PAU301_fengJiYiQiDong){
            bt_fengJiYiQiDong.setBackgroundResource(R.drawable.led_green);
        }else {
            bt_fengJiYiQiDong.setBackgroundResource(R.drawable.led_gray);
        }

        if (data_PAU301_dianYuRe1){
            bt_dianYuRe1.setBackgroundResource(R.drawable.led_green);
        }else {
            bt_dianYuRe1.setBackgroundResource(R.drawable.led_gray);
        }

        if (data_PAU301_dianYuRe2){
            bt_dianYuRe2.setBackgroundResource(R.drawable.led_green);
        }else {
            bt_dianYuRe2.setBackgroundResource(R.drawable.led_gray);
        }

        if (data_PAU301_dianYuRe3){
            bt_dianYuRe3.setBackgroundResource(R.drawable.led_green);
        }else {
            bt_dianYuRe3.setBackgroundResource(R.drawable.led_gray);
        }

        if (data_PAU301_fengJiStatus){
            bt_fengJiStatus.setBackgroundResource(R.drawable.led_green);
        }else {
            bt_fengJiStatus.setBackgroundResource(R.drawable.led_gray);
        }

        if (data_PAU301_zhongXiaoBaoJing){
            bt_zhongXiaoBaoJing.setBackgroundResource(R.drawable.led_red);
        }else {
            bt_zhongXiaoBaoJing.setBackgroundResource(R.drawable.led_gray);
        }

        if (data_PAU301_dianYuReGaoWen){
            bt_dianYuReGaoWen.setBackgroundResource(R.drawable.led_red);
        }else {
            bt_dianYuReGaoWen.setBackgroundResource(R.drawable.led_gray);
        }

        if (data_PAU301_fengJiQueFeng){
            bt_fengJiQueFeng.setBackgroundResource(R.drawable.led_red);
        }else {
            bt_fengJiQueFeng.setBackgroundResource(R.drawable.led_gray);
        }

        if (data_PAU301_diWenPanGuan){
            bt_diWenPanGuan.setBackgroundResource(R.drawable.led_red);
        }else {
            bt_diWenPanGuan.setBackgroundResource(R.drawable.led_gray);
        }

        if (data_PAU301_mieJunYunXing){
            bt_mieJunYunXing.setBackgroundResource(R.drawable.led_green);
        }else {
            bt_mieJunYunXing.setBackgroundResource(R.drawable.led_gray);
        }
    }

    private void updatePau302() {

        tv_data_tempReal.setText(data_PAU302_tempReal+"℃");
        tv_data_xinFengWenDu.setText(data_PAU302_xinFengWenDU+"℃");
        tv_data_tempSet.setText(data_PAU302_tempSet+"℃");
        tv_data_lengShuiFaKaiDu.setText(data_PAU302_lengShuiFaKaiDu+"%");
        tv_data_reShuiFaKaiDu.setText(data_PAU302_reShuiFaKaiDu+"%");

        if (data_PAU302_dongXiaJi){
            tv_dongXiaJi.setText("夏季");
        }else{
            tv_dongXiaJi.setText("冬季");
        }

        if (data_PAU302_shouZiDong){
            tv_shouZiDong.setText("手动");
        }else {
            tv_shouZiDong.setText("自动");
        }

        if (data_PAU302_fengJiYiQiDong){
            bt_fengJiYiQiDong.setBackgroundResource(R.drawable.led_green);
        }else {
            bt_fengJiYiQiDong.setBackgroundResource(R.drawable.led_gray);
        }


        if (data_PAU302_dianYuRe1){
            bt_dianYuRe1.setBackgroundResource(R.drawable.led_green);
        }else {
            bt_dianYuRe1.setBackgroundResource(R.drawable.led_gray);
        }

        if (data_PAU302_dianYuRe2){
            bt_dianYuRe2.setBackgroundResource(R.drawable.led_green);
        }else {
            bt_dianYuRe2.setBackgroundResource(R.drawable.led_gray);
        }

        if (data_PAU302_dianYuRe3){
            bt_dianYuRe3.setBackgroundResource(R.drawable.led_green);
        }else {
            bt_dianYuRe3.setBackgroundResource(R.drawable.led_gray);
        }

        if (data_PAU302_fengJiStatus){
            bt_fengJiStatus.setBackgroundResource(R.drawable.led_green);
        }else {
            bt_fengJiStatus.setBackgroundResource(R.drawable.led_gray);
        }

        if (data_PAU302_zhongXiaoBaoJing){
            bt_zhongXiaoBaoJing.setBackgroundResource(R.drawable.led_red);
        }else {
            bt_zhongXiaoBaoJing.setBackgroundResource(R.drawable.led_gray);
        }

        if (data_PAU302_dianYuReGaoWen){
            bt_dianYuReGaoWen.setBackgroundResource(R.drawable.led_red);
        }else {
            bt_dianYuReGaoWen.setBackgroundResource(R.drawable.led_gray);
        }

        if (data_PAU302_fengJiQueFeng){
            bt_fengJiQueFeng.setBackgroundResource(R.drawable.led_red);
        }else {
            bt_fengJiQueFeng.setBackgroundResource(R.drawable.led_gray);
        }

        if (data_PAU302_diWenPanGuan){
            bt_diWenPanGuan.setBackgroundResource(R.drawable.led_red);
        }else {
            bt_diWenPanGuan.setBackgroundResource(R.drawable.led_gray);
        }

        if (data_PAU302_mieJunYunXing){
            bt_mieJunYunXing.setBackgroundResource(R.drawable.led_green);
        }else {
            bt_mieJunYunXing.setBackgroundResource(R.drawable.led_gray);
        }

    }

    private void updatePau303() {

        tv_data_tempReal.setText(data_PAU303_tempReal+"℃");
        tv_data_xinFengWenDu.setText(data_PAU303_xinFengWenDU+"℃");
        tv_data_tempSet.setText(data_PAU303_tempSet+"℃");
        tv_data_lengShuiFaKaiDu.setText(data_PAU303_lengShuiFaKaiDu+"%");
        tv_data_reShuiFaKaiDu.setText(data_PAU303_reShuiFaKaiDu+"%");

        if (data_PAU303_dongXiaJi){
            tv_dongXiaJi.setText("夏季");
        }else{
            tv_dongXiaJi.setText("冬季");
        }

        if (data_PAU303_shouZiDong){
            tv_shouZiDong.setText("手动");
        }else {
            tv_shouZiDong.setText("自动");
        }

        if (data_PAU303_fengJiYiQiDong){
            bt_fengJiYiQiDong.setBackgroundResource(R.drawable.led_green);
        }else {
            bt_fengJiYiQiDong.setBackgroundResource(R.drawable.led_gray);
        }

        if (data_PAU303_dianYuRe1){
            bt_dianYuRe1.setBackgroundResource(R.drawable.led_green);
        }else {
            bt_dianYuRe1.setBackgroundResource(R.drawable.led_gray);
        }

        if (data_PAU303_dianYuRe2){
            bt_dianYuRe2.setBackgroundResource(R.drawable.led_green);
        }else {
            bt_dianYuRe2.setBackgroundResource(R.drawable.led_gray);
        }

        if (data_PAU303_dianYuRe3){
            bt_dianYuRe3.setBackgroundResource(R.drawable.led_green);
        }else {
            bt_dianYuRe3.setBackgroundResource(R.drawable.led_gray);
        }

        if (data_PAU303_fengJiStatus){
            bt_fengJiStatus.setBackgroundResource(R.drawable.led_green);
        }else {
            bt_fengJiStatus.setBackgroundResource(R.drawable.led_gray);
        }

        if (data_PAU303_zhongXiaoBaoJing){
            bt_zhongXiaoBaoJing.setBackgroundResource(R.drawable.led_red);
        }else {
            bt_zhongXiaoBaoJing.setBackgroundResource(R.drawable.led_gray);
        }

        if (data_PAU303_dianYuReGaoWen){
            bt_dianYuReGaoWen.setBackgroundResource(R.drawable.led_red);
        }else {
            bt_dianYuReGaoWen.setBackgroundResource(R.drawable.led_gray);
        }

        if (data_PAU303_fengJiQueFeng){
            bt_fengJiQueFeng.setBackgroundResource(R.drawable.led_red);
        }else {
            bt_fengJiQueFeng.setBackgroundResource(R.drawable.led_gray);
        }

        if (data_PAU303_diWenPanGuan){
            bt_diWenPanGuan.setBackgroundResource(R.drawable.led_red);
        }else {
            bt_diWenPanGuan.setBackgroundResource(R.drawable.led_gray);
        }

        if (data_PAU303_mieJunYunXing){
            bt_mieJunYunXing.setBackgroundResource(R.drawable.led_green);
        }else {
            bt_mieJunYunXing.setBackgroundResource(R.drawable.led_gray);
        }

    }

    private void updatePau304() {

        tv_data_tempReal.setText(data_PAU304_tempReal+"℃");
        tv_data_xinFengWenDu.setText(data_PAU304_xinFengWenDU+"℃");
        tv_data_tempSet.setText(data_PAU304_tempSet+"℃");
        tv_data_lengShuiFaKaiDu.setText(data_PAU304_lengShuiFaKaiDu+"%");
        tv_data_reShuiFaKaiDu.setText(data_PAU304_reShuiFaKaiDu+"%");

        if (data_PAU304_dongXiaJi){
            tv_dongXiaJi.setText("夏季");
        }else{
            tv_dongXiaJi.setText("冬季");
        }

        if (data_PAU304_shouZiDong){
            tv_shouZiDong.setText("手动");
        }else {
            tv_shouZiDong.setText("自动");
        }

        if (data_PAU304_fengJiYiQiDong){
            bt_fengJiYiQiDong.setBackgroundResource(R.drawable.led_green);
        }else {
            bt_fengJiYiQiDong.setBackgroundResource(R.drawable.led_gray);
        }


        if (data_PAU304_dianYuRe1){
            bt_dianYuRe1.setBackgroundResource(R.drawable.led_green);
        }else {
            bt_dianYuRe1.setBackgroundResource(R.drawable.led_gray);
        }

        if (data_PAU304_dianYuRe2){
            bt_dianYuRe2.setBackgroundResource(R.drawable.led_green);
        }else {
            bt_dianYuRe2.setBackgroundResource(R.drawable.led_gray);
        }

        if (data_PAU304_dianYuRe3){
            bt_dianYuRe3.setBackgroundResource(R.drawable.led_green);
        }else {
            bt_dianYuRe3.setBackgroundResource(R.drawable.led_gray);
        }

        if (data_PAU304_fengJiStatus){
            bt_fengJiStatus.setBackgroundResource(R.drawable.led_green);
        }else {
            bt_fengJiStatus.setBackgroundResource(R.drawable.led_gray);
        }

        if (data_PAU304_zhongXiaoBaoJing){
            bt_zhongXiaoBaoJing.setBackgroundResource(R.drawable.led_red);
        }else {
            bt_zhongXiaoBaoJing.setBackgroundResource(R.drawable.led_gray);
        }

        if (data_PAU304_dianYuReGaoWen){
            bt_dianYuReGaoWen.setBackgroundResource(R.drawable.led_red);
        }else {
            bt_dianYuReGaoWen.setBackgroundResource(R.drawable.led_gray);
        }

        if (data_PAU304_fengJiQueFeng){
            bt_fengJiQueFeng.setBackgroundResource(R.drawable.led_red);
        }else {
            bt_fengJiQueFeng.setBackgroundResource(R.drawable.led_gray);
        }

        if (data_PAU304_diWenPanGuan){
            bt_diWenPanGuan.setBackgroundResource(R.drawable.led_red);
        }else {
            bt_diWenPanGuan.setBackgroundResource(R.drawable.led_gray);
        }

        if (data_PAU304_mieJunYunXing){
            bt_mieJunYunXing.setBackgroundResource(R.drawable.led_green);
        }else {
            bt_mieJunYunXing.setBackgroundResource(R.drawable.led_gray);
        }

    }

    private void updatePau305() {

        tv_data_tempReal.setText(data_PAU305_tempReal+"℃");
        tv_data_xinFengWenDu.setText(data_PAU305_xinFengWenDU+"℃");
        tv_data_tempSet.setText(data_PAU305_tempSet+"℃");
        tv_data_lengShuiFaKaiDu.setText(data_PAU305_lengShuiFaKaiDu+"%");
        tv_data_reShuiFaKaiDu.setText(data_PAU305_reShuiFaKaiDu+"%");

        if (data_PAU305_dongXiaJi){
            tv_dongXiaJi.setText("夏季");
        }else{
            tv_dongXiaJi.setText("冬季");
        }

        if (data_PAU305_shouZiDong){
            tv_shouZiDong.setText("手动");
        }else {
            tv_shouZiDong.setText("自动");
        }

        if (data_PAU305_fengJiYiQiDong){
            bt_fengJiYiQiDong.setBackgroundResource(R.drawable.led_green);
        }else {
            bt_fengJiYiQiDong.setBackgroundResource(R.drawable.led_gray);
        }


        if (data_PAU305_dianYuRe1){
            bt_dianYuRe1.setBackgroundResource(R.drawable.led_green);
        }else {
            bt_dianYuRe1.setBackgroundResource(R.drawable.led_gray);
        }

        if (data_PAU305_dianYuRe2){
            bt_dianYuRe2.setBackgroundResource(R.drawable.led_green);
        }else {
            bt_dianYuRe2.setBackgroundResource(R.drawable.led_gray);
        }

        if (data_PAU305_dianYuRe3){
            bt_dianYuRe3.setBackgroundResource(R.drawable.led_green);
        }else {
            bt_dianYuRe3.setBackgroundResource(R.drawable.led_gray);
        }

        if (data_PAU305_fengJiStatus){
            bt_fengJiStatus.setBackgroundResource(R.drawable.led_green);
        }else {
            bt_fengJiStatus.setBackgroundResource(R.drawable.led_gray);
        }

        if (data_PAU305_zhongXiaoBaoJing){
            bt_zhongXiaoBaoJing.setBackgroundResource(R.drawable.led_red);
        }else {
            bt_zhongXiaoBaoJing.setBackgroundResource(R.drawable.led_gray);
        }

        if (data_PAU305_dianYuReGaoWen){
            bt_dianYuReGaoWen.setBackgroundResource(R.drawable.led_red);
        }else {
            bt_dianYuReGaoWen.setBackgroundResource(R.drawable.led_gray);
        }

        if (data_PAU305_fengJiQueFeng){
            bt_fengJiQueFeng.setBackgroundResource(R.drawable.led_red);
        }else {
            bt_fengJiQueFeng.setBackgroundResource(R.drawable.led_gray);
        }

        if (data_PAU305_diWenPanGuan){
            bt_diWenPanGuan.setBackgroundResource(R.drawable.led_red);
        }else {
            bt_diWenPanGuan.setBackgroundResource(R.drawable.led_gray);
        }

        if (data_PAU305_mieJunYunXing){
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
        Toast.makeText(PauDeviceDataActivity.this, "连接已断开", Toast.LENGTH_SHORT).show();
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
            mHandler.sendEmptyMessage(PauDeviceDataActivity.handler_key.DISCONNECT.ordinal());
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
            mHandler.sendEmptyMessage(PauDeviceDataActivity.handler_key.UPDATE_UI.ordinal());
        }
    }

    /**
     *Description:显示格式化数值，保留对应分辨率的小数个数，比如传入参数（20.3656，0.01），将返回20.37
     *@param date 传入的数值
     *@param scale 保留多少位小数
     *@return
     */
    protected String formatValue(double date, Object scale) {
        if (scale instanceof Double) {
            DecimalFormat df = new DecimalFormat(scale.toString());
            return df.format(date);
        }
        return Math.round(date) + "";
    }
}

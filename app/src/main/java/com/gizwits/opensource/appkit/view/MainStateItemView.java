package com.gizwits.opensource.appkit.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gizwits.opensource.appkit.R;

public class MainStateItemView extends LinearLayout {

    private static final String NAMESPACE = "http://schemas.android.com/apk/res/com.gizwits.opensource.appkit";
    private String mDestitle;
    private String mDesTempNow;
    private String mDesHumiNow;
    private String mDesTempSet;
    private String mDesHumiSet;

    public MainStateItemView(Context context) {
        this(context,null);
    }

    public MainStateItemView(Context context, @Nullable AttributeSet attrs) {
        this(context,attrs,0);
    }

    public MainStateItemView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View.inflate(context, R.layout.main_state_item_view,this);

        TextView tv_title = (TextView) findViewById(R.id.tv_main_state_title);
        TextView tv_temp_now = (TextView) findViewById(R.id.tv_main_state_tempnow);
        TextView tv_humi_now = (TextView) findViewById(R.id.tv_main_state_huminow);
        TextView tv_temp_set = (TextView) findViewById(R.id.tv_main_state_tempset);
        TextView tv_humi_set = (TextView) findViewById(R.id.tv_main_state_humiset);

        initAttrs(attrs);

        tv_title.setText(mDestitle);
        tv_temp_now.setText(mDestitle);
        tv_humi_now.setText(mDestitle);
        tv_temp_set.setText(mDestitle);
        tv_humi_set.setText(mDestitle);

    }

    private void initAttrs(AttributeSet attrs) {



    }


}

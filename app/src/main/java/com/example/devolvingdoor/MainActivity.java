package com.example.devolvingdoor;

import android.animation.Animator;
import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pools;

public class MainActivity extends AppCompatActivity {

    private LinearLayout llContainer;
    LayoutTransition transition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initDev0lving();
        TextView titleView = findViewById(R.id.titleView);
        titleView.setText("当前实现方式：通过LayoutTransition");
        findViewById(R.id.check).setOnClickListener(view -> {
            startActivity(new Intent(this,MainActivity2.class));
            finish();
        });
    }

    private void initDev0lving() {
        llContainer = (LinearLayout) findViewById(R.id.ll_container);
        transition = new LayoutTransition();
        ObjectAnimator valueAnimator = ObjectAnimator.ofFloat(null, "alpha", 0, 1);
        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                if (llContainer.getChildCount() == 4) handler.sendEmptyMessage(1); //当前展示超过四条，执行删除动画
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (llContainer.getChildCount() == 5) handler.sendEmptyMessage(2);  //动画执行完毕，删除view
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        transition.setAnimator(LayoutTransition.APPEARING, valueAnimator);
        //删除动画
        PropertyValuesHolder alpha = PropertyValuesHolder.ofFloat("alpha", 0, 0);
        ObjectAnimator objectAnimator = ObjectAnimator.ofPropertyValuesHolder(null, new PropertyValuesHolder[]{alpha}).setDuration(transition.getDuration(LayoutTransition.DISAPPEARING));

        transition.setAnimator(LayoutTransition.DISAPPEARING, objectAnimator);
        llContainer.setLayoutTransition(transition);
    }

    private String[] texts = new String[]{
            "蔡徐坤哈哈哈",
            "大家好，我是联系市场连年吧的个人联系生，大奖赛哦大家按时ask达拉斯",
            "喜欢唱跳rap篮球",
            "music！"};


    Pools.SimplePool<TextView> textViewPool = new Pools.SimplePool<>(texts.length);

    private TextView obtainTextView() {
        TextView textView = textViewPool.acquire();
        if (textView == null) {
            textView = new TextView(MainActivity.this);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.bottomMargin = dp2px(12);
            textView.setLayoutParams(layoutParams);
            textView.setPadding(dp2px(12), dp2px(6), dp2px(12), dp2px(6));
            textView.setTextColor(0xffffffff);
            textView.setMaxLines(1);
            textView.setEllipsize(TextUtils.TruncateAt.END);
            textView.setGravity(Gravity.CENTER);
            textView.setTextSize(15);
            textView.setTextColor(0xffffffff);
            Drawable drawable = getResources().getDrawable(R.mipmap.ic_launcher_round);
            drawable.setBounds(0, 0, 80, 80);
            textView.setCompoundDrawablesRelative(drawable, null, null, null);
            textView.setCompoundDrawablePadding(10);
            textView.setBackgroundResource(R.drawable.circle_black_bg);
        }
        textView.setText(texts[index]);
        return textView;
    }

    private int dp2px(float dp) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, displayMetrics);
    }

    int index = 0;
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @SuppressLint("ResourceAsColor")
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    TextView textView = obtainTextView();
                    llContainer.addView(textView);
                    sendEmptyMessageDelayed(0, 2000);
                    index++;
                    if (index == 4) {
                        index = 0;
                    }
                    break;
                case 1:
                    //给展示的第一个view增加渐变透明动画
                    llContainer.getChildAt(0).animate().alpha(0).setDuration(transition.getDuration(LayoutTransition.APPEARING)).start();
                    break;
                case 2:
                    //删除顶部view
                    llContainer.removeViewAt(0);
                    break;
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        handler.sendEmptyMessage(0);
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeMessages(0);
    }
}
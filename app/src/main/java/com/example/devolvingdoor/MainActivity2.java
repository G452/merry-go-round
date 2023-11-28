package com.example.devolvingdoor;

import android.animation.ObjectAnimator;
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
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pools;

public class MainActivity2 extends AppCompatActivity {

    private LinearLayout llContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initDevolving();
        TextView titleView = findViewById(R.id.titleView);
        titleView.setText("当前实现方式：通过ObjectAnimator、ViewPropertyAnimator");
        findViewById(R.id.check).setOnClickListener(view -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });
    }

    private void initDevolving() {
        llContainer = (LinearLayout) findViewById(R.id.ll_container);
        llContainer.setOnHierarchyChangeListener(new ViewGroup.OnHierarchyChangeListener() {
            @Override
            public void onChildViewAdded(View parent, View child) {
                animateAppearance(child, llContainer.getChildCount() == 5);
                animateOtherViewsUp();
                if (llContainer.getChildCount() == 5) {
                    animateDisappearance(llContainer.getChildAt(0));
                }
            }

            @Override
            public void onChildViewRemoved(View parent, View child) {
                if (llContainer.getChildCount() == 5) {
                    handleDelAnimMessage();
                }
            }
        });
    }


    float mTranslationNum = 100F;
    int mDuration = 800;

    private void animateAppearance(View view, boolean needDel) {
        view.setAlpha(0f);
        view.setScaleX(0.5f);
        view.setScaleY(0.5f);
        view.setTranslationY(mTranslationNum);
        view.setPivotX(0f);
        ViewPropertyAnimator appearanceAnimator = view.animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .translationY(0f)
                .setDuration(mDuration);

        if (needDel) {
            View firstChild = llContainer.getChildAt(0);
            if (firstChild != null) {
                animateDisappearance(firstChild);
            }
        }
        appearanceAnimator.start();
    }

    private void animateOtherViewsUp() {
        for (int i = 0; i < llContainer.getChildCount() - 1; i++) {
            View child = llContainer.getChildAt(i);
            ObjectAnimator translationYAnimator = ObjectAnimator.ofFloat(child, "translationY", mTranslationNum, 0);
            translationYAnimator.setDuration(mDuration);
            translationYAnimator.start();
        }
    }

    private void animateDisappearance(View view) {
        view.animate()
                .alpha(0f)
                .setDuration(mDuration)
                .withEndAction(() -> llContainer.removeView(view))
                .setListener(null)
                .start();
    }

    private void handleDelAnimMessage() {
        llContainer.getChildAt(0).animate().alpha(0).setDuration(mDuration).start();
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
            textView = new TextView(MainActivity2.this);
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
            if (msg.what == 0) {
                TextView textView = obtainTextView();
                textView.post(() -> mTranslationNum = textView.getHeight() + dp2px(12));
                llContainer.addView(textView);
                sendEmptyMessageDelayed(0, mDuration * 150 / 100);
                index++;
                if (index == 4) {
                    index = 0;
                }
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
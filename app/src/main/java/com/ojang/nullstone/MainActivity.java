package com.ojang.nullstone;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class MainActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
							 WindowManager.LayoutParams.FLAG_FULLSCREEN);

        RelativeLayout root = new RelativeLayout(this);

        // ===== 背景图 =====
        ImageView bg = new ImageView(this);
        bg.setImageResource(R.drawable.bg);
        bg.setScaleType(ImageView.ScaleType.CENTER_CROP);
        RelativeLayout.LayoutParams bgParams = new RelativeLayout.LayoutParams(
			RelativeLayout.LayoutParams.MATCH_PARENT,
			RelativeLayout.LayoutParams.MATCH_PARENT);
        bg.setLayoutParams(bgParams);
        root.addView(bg);

        // ===== 标题 =====
        ImageView logo = new ImageView(this);
        logo.setImageResource(R.drawable.logo);
        logo.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        RelativeLayout.LayoutParams logoParams = new RelativeLayout.LayoutParams(
			RelativeLayout.LayoutParams.WRAP_CONTENT,
			RelativeLayout.LayoutParams.WRAP_CONTENT);
        logoParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        logoParams.topMargin = dp(40);
        logo.setLayoutParams(logoParams);
        root.addView(logo);

        // ===== 按钮容器（水平居中，底部） =====
        RelativeLayout btnContainer = new RelativeLayout(this);
        RelativeLayout.LayoutParams containerParams = new RelativeLayout.LayoutParams(
			RelativeLayout.LayoutParams.MATCH_PARENT,
			RelativeLayout.LayoutParams.WRAP_CONTENT);
        containerParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        containerParams.bottomMargin = dp(80);
        btnContainer.setLayoutParams(containerParams);
        root.addView(btnContainer);

        // 按钮统一大小
        int btnSize = dp(120);

        // ===== 开始游戏按钮（左） =====
        ImageView btnStart = new ImageView(this);
        btnStart.setImageResource(R.drawable.btn_start);
        btnStart.setScaleType(ImageView.ScaleType.FIT_CENTER);
        RelativeLayout.LayoutParams startParams = new RelativeLayout.LayoutParams(
			btnSize, btnSize);
        startParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        startParams.leftMargin = 0;
        startParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        startParams.leftMargin = dp(80);
        btnStart.setLayoutParams(startParams);
        btnStart.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					startActivity(new Intent(MainActivity.this, GameActivity.class));
				}
			});
        btnContainer.addView(btnStart);

        // ===== 设置按钮（右） =====
        ImageView btnSettings = new ImageView(this);
        btnSettings.setImageResource(R.drawable.btn_settings);
        btnSettings.setScaleType(ImageView.ScaleType.FIT_CENTER);
        RelativeLayout.LayoutParams settingsParams = new RelativeLayout.LayoutParams(
			btnSize, btnSize);
        settingsParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        settingsParams.rightMargin = dp(80);
        btnSettings.setLayoutParams(settingsParams);
        btnSettings.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					startActivity(new Intent(MainActivity.this, SettingsActivity.class));
				}
			});
        btnContainer.addView(btnSettings);

        setContentView(root);
    }

    // dp 转 px
    private int dp(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return (int)(dp * density + 0.5f);
    }
}

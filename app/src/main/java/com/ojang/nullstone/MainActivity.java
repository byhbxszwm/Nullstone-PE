package com.ojang.nullstone;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class MainActivity extends Activity {

    // 主菜单音乐列表（想加歌就在这加）
    private static final String[] MENU_MUSIC = {
        "music/menu1.ogg",
        "music/menu2.ogg",
        "music/menu3.ogg"
    };

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

        // ===== 按钮容器 =====
        RelativeLayout btnContainer = new RelativeLayout(this);
        RelativeLayout.LayoutParams containerParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        containerParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        containerParams.bottomMargin = dp(80);
        btnContainer.setLayoutParams(containerParams);
        root.addView(btnContainer);

        int btnSize = dp(120);

        // ===== 开始游戏 =====
        ImageView btnStart = new ImageView(this);
        btnStart.setImageResource(R.drawable.btn_start);
        btnStart.setScaleType(ImageView.ScaleType.FIT_CENTER);
        RelativeLayout.LayoutParams startParams = new RelativeLayout.LayoutParams(
                btnSize, btnSize);
        startParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        startParams.leftMargin = dp(80);
        btnStart.setLayoutParams(startParams);
        btnStart.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, GameActivity.class));
            }
        });
        btnContainer.addView(btnStart);

        // ===== 设置 =====
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

        // 播放主菜单音乐
        MusicPlayer.getInstance(this).playScene("menu", MENU_MUSIC);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 如果在设置界面，不暂停音乐；如果 App 切到后台，暂停
        // 简单起见：不主动 pause，切后台系统会暂停 MediaPlayer
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 回到主菜单，确保音乐在播
        MusicPlayer.getInstance(this).playScene("menu", MENU_MUSIC);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 注意：不要 stop() 音乐——否则从主菜单进游戏时音乐会断
        // 只有 App 真正退出时才 stop，用 isFinishing() 判断
        if (isFinishing()) {
            MusicPlayer.getInstance(this).stop();
        }
    }

    private int dp(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return (int)(dp * density + 0.5f);
    }
}
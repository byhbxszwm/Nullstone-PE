package com.ojang.nullstone;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

public class SettingsActivity extends Activity {

    private PrefManager pref;
    private LinearLayout root;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        pref = PrefManager.getInstance(this);

        ScrollView scroll = new ScrollView(this);
        scroll.setBackgroundColor(Color.rgb(20, 20, 30));

        root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(20), dp(20), dp(20), dp(20));

        // 标题
        TextView title = new TextView(this);
        title.setText("设置");
        title.setTextColor(Color.WHITE);
        title.setTextSize(28);
        title.setGravity(Gravity.CENTER);
        title.setPadding(0, 0, 0, dp(20));
        root.addView(title);

        // ===== 游戏设置 =====
        addCategory("游戏设置");
        addOption("游戏模式", new String[]{"生存", "创造"},
                pref.getGameMode(), new OnOption() {
                    public void onSelected(int i) { pref.setGameMode(i); }
                });
        addOption("难度", new String[]{"和平", "简单", "普通", "困难"},
                pref.getDifficulty(), new OnOption() {
                    public void onSelected(int i) { pref.setDifficulty(i); }
                });

        // ===== 图形设置 =====
        addCategory("图形设置");
        addSlider("渲染距离", 4, 16, pref.getRenderDistance(), " 区块",
                new OnSlider() {
                    public void onChanged(int v) { pref.setRenderDistance(v); }
                });
        addSlider("视野 (FOV)", 50, 110, pref.getFov(), "°",
                new OnSlider() {
                    public void onChanged(int v) { pref.setFov(v); }
                });

        // ===== 控制设置 =====
        addCategory("控制设置");
        addSlider("灵敏度", 10, 200, pref.getSensitivity(), "%",
                new OnSlider() {
                    public void onChanged(int v) { pref.setSensitivity(v); }
                });
        addSwitch("左手模式", pref.isLeftHanded(), new OnSwitch() {
            public void onChanged(boolean v) { pref.setLeftHanded(v); }
        });
        addSwitch("震动反馈", pref.isVibrationOn(), new OnSwitch() {
            public void onChanged(boolean v) { pref.setVibrationOn(v); }
        });

        // ===== 声音设置 =====
        addCategory("声音设置");
        addSlider("主音量", 0, 100, pref.getMasterVolume(), "%",
                new OnSlider() {
                    public void onChanged(int v) { pref.setMasterVolume(v); }
                });
        addSlider("音乐音量", 0, 100, pref.getMusicVolume(), "%",
                new OnSlider() {
                    public void onChanged(int v) { pref.setMusicVolume(v); }
                });

        // 完成按钮
        Button btnDone = new Button(this);
        btnDone.setText("完成");
        btnDone.setTextSize(18);
        btnDone.setTextColor(Color.WHITE);
        btnDone.setBackgroundColor(Color.rgb(51, 181, 229));
        btnDone.setPadding(0, dp(16), 0, dp(16));
        btnDone.setAllCaps(false);
        LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        btnParams.setMargins(0, dp(30), 0, 0);
        btnDone.setLayoutParams(btnParams);
        btnDone.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });
        root.addView(btnDone);

        scroll.addView(root);
        setContentView(scroll);
    }

    // ===== 分类标题 =====
    private void addCategory(String name) {
        TextView tv = new TextView(this);
        tv.setText(name);
        tv.setTextColor(Color.rgb(51, 181, 229));
        tv.setTextSize(18);
        tv.setPadding(0, dp(20), 0, dp(10));
        root.addView(tv);

        View line = new View(this);
        line.setBackgroundColor(Color.rgb(60, 60, 80));
        line.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp(1)));
        root.addView(line);
    }

    // ===== 选项 =====
    interface OnOption { void onSelected(int index); }

    private void addOption(String label, final String[] options,
                           int current, final OnOption callback) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(0, dp(12), 0, dp(12));

        TextView tvLabel = new TextView(this);
        tvLabel.setText(label);
        tvLabel.setTextColor(Color.WHITE);
        tvLabel.setTextSize(16);
        tvLabel.setLayoutParams(new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        row.addView(tvLabel);

        final TextView tvValue = new TextView(this);
        tvValue.setText(options[current]);
        tvValue.setTextColor(Color.rgb(51, 181, 229));
        tvValue.setTextSize(16);
        row.addView(tvValue);

        final int[] cur = {current};
        row.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                cur[0] = (cur[0] + 1) % options.length;
                tvValue.setText(options[cur[0]]);
                callback.onSelected(cur[0]);
            }
        });

        root.addView(row);
    }

    // ===== 滑块 =====
    interface OnSlider { void onChanged(int value); }

    private void addSlider(String label, final int min, int max,
                           int current, final String unit, final OnSlider callback) {
        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setPadding(0, dp(12), 0, dp(12));

        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);

        TextView tvLabel = new TextView(this);
        tvLabel.setText(label);
        tvLabel.setTextColor(Color.WHITE);
        tvLabel.setTextSize(16);
        tvLabel.setLayoutParams(new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        row.addView(tvLabel);

        final TextView tvValue = new TextView(this);
        tvValue.setText(current + unit);
        tvValue.setTextColor(Color.rgb(51, 181, 229));
        tvValue.setTextSize(16);
        row.addView(tvValue);

        container.addView(row);

        SeekBar seekBar = new SeekBar(this);
        seekBar.setMax(max - min);
        seekBar.setProgress(current - min);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar sb, int progress, boolean fromUser) {
                int value = progress + min;
                tvValue.setText(value + unit);
                callback.onChanged(value);
            }
            public void onStartTrackingTouch(SeekBar sb) {}
            public void onStopTrackingTouch(SeekBar sb) {}
        });
        container.addView(seekBar);

        root.addView(container);
    }

    // ===== 开关 =====
    interface OnSwitch { void onChanged(boolean value); }

    private void addSwitch(String label, boolean current, final OnSwitch callback) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(0, dp(12), 0, dp(12));

        TextView tvLabel = new TextView(this);
        tvLabel.setText(label);
        tvLabel.setTextColor(Color.WHITE);
        tvLabel.setTextSize(16);
        tvLabel.setLayoutParams(new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        row.addView(tvLabel);

        Switch sw = new Switch(this);
        sw.setChecked(current);
        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton cb, boolean checked) {
                callback.onChanged(checked);
            }
        });
        row.addView(sw);

        root.addView(row);
    }

    private int dp(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return (int)(dp * density + 0.5f);
    }
}
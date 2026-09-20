package com.ojang.nullstone;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefManager {

    private static final String PREF_NAME = "nullstone_settings";

    private static final String KEY_RENDER_DISTANCE = "render_distance";
    private static final String KEY_FOV = "fov";
    private static final String KEY_SENSITIVITY = "sensitivity";
    private static final String KEY_MASTER_VOLUME = "master_volume";
    private static final String KEY_MUSIC_VOLUME = "music_volume";
    private static final String KEY_LEFT_HANDED = "left_handed";
    private static final String KEY_VIBRATION = "vibration";
    private static final String KEY_GAME_MODE = "game_mode";
    private static final String KEY_DIFFICULTY = "difficulty";

    private SharedPreferences pref;
    private static PrefManager instance;

    private PrefManager(Context context) {
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized PrefManager getInstance(Context context) {
        if (instance == null) instance = new PrefManager(context);
        return instance;
    }

    // 渲染距离 4~16，默认 8
    public int getRenderDistance() {
        return pref.getInt(KEY_RENDER_DISTANCE, 8);
    }
    public void setRenderDistance(int value) {
        pref.edit().putInt(KEY_RENDER_DISTANCE, value).apply();
    }

    // FOV 50~110，默认 70
    public int getFov() {
        return pref.getInt(KEY_FOV, 70);
    }
    public void setFov(int value) {
        pref.edit().putInt(KEY_FOV, value).apply();
    }

    // 灵敏度 10~200，默认 100
    public int getSensitivity() {
        return pref.getInt(KEY_SENSITIVITY, 100);
    }
    public void setSensitivity(int value) {
        pref.edit().putInt(KEY_SENSITIVITY, value).apply();
    }

    // 主音量 0~100，默认 80
    public int getMasterVolume() {
        return pref.getInt(KEY_MASTER_VOLUME, 80);
    }
    public void setMasterVolume(int value) {
        pref.edit().putInt(KEY_MASTER_VOLUME, value).apply();
    }

    // 音乐音量 0~100，默认 60
    public int getMusicVolume() {
        return pref.getInt(KEY_MUSIC_VOLUME, 60);
    }
    public void setMusicVolume(int value) {
        pref.edit().putInt(KEY_MUSIC_VOLUME, value).apply();
    }

    // 左手模式
    public boolean isLeftHanded() {
        return pref.getBoolean(KEY_LEFT_HANDED, false);
    }
    public void setLeftHanded(boolean value) {
        pref.edit().putBoolean(KEY_LEFT_HANDED, value).apply();
    }

    // 震动
    public boolean isVibrationOn() {
        return pref.getBoolean(KEY_VIBRATION, true);
    }
    public void setVibrationOn(boolean value) {
        pref.edit().putBoolean(KEY_VIBRATION, value).apply();
    }

    // 游戏模式 0=生存 1=创造
    public int getGameMode() {
        return pref.getInt(KEY_GAME_MODE, 1);
    }
    public void setGameMode(int value) {
        pref.edit().putInt(KEY_GAME_MODE, value).apply();
    }

    // 难度 0=和平 1=简单 2=普通 3=困难
    public int getDifficulty() {
        return pref.getInt(KEY_DIFFICULTY, 0);
    }
    public void setDifficulty(int value) {
        pref.edit().putInt(KEY_DIFFICULTY, value).apply();
    }
}
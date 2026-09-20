package com.ojang.nullstone;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;

import java.util.Random;

public class MusicPlayer {

    private static MusicPlayer instance;

    private MediaPlayer player;
    private Context context;
    private Random random = new Random();

    private String[] playlist;      // 当前播放列表
    private int currentIndex = -1;  // 当前播的下标

    // 当前是哪个"场景"的音乐，切换场景才换歌
    private String currentScene = null;

    private MusicPlayer(Context context) {
        this.context = context.getApplicationContext();
    }

    public static synchronized MusicPlayer getInstance(Context context) {
        if (instance == null) {
            instance = new MusicPlayer(context);
        }
        return instance;
    }

    /**
     * 切换到某个场景的音乐。
     * 如果已经在播这个场景，什么都不做。
     *
     * @param scene     场景标识，比如 "menu" / "game"
     * @param musicList 这个场景的音乐列表（assets 路径）
     */
    public void playScene(String scene, String[] musicList) {
        if (musicList == null || musicList.length == 0) return;

        // 已经在播同一个场景 → 不打断
        if (scene.equals(currentScene) && player != null) {
            if (!player.isPlaying()) {
                player.start();   // 暂停了就继续
            }
            return;
        }

        currentScene = scene;
        playlist = musicList;

        int index = random.nextInt(playlist.length);
        playIndex(index);
    }

    private void playIndex(int index) {
        stopInternal();

        if (playlist == null || index < 0 || index >= playlist.length) return;

        currentIndex = index;

        try {
            AssetFileDescriptor afd = context.getAssets().openFd(playlist[index]);
            player = new MediaPlayer();
            player.setDataSource(
                afd.getFileDescriptor(),
                afd.getStartOffset(),
                afd.getLength()
            );
            afd.close();

            player.setLooping(false);

            PrefManager pref = PrefManager.getInstance(context);
            float vol = pref.getMasterVolume() / 100.0f
                      * pref.getMusicVolume() / 100.0f;
            player.setVolume(vol, vol);

            // 放完随机切下一首
            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                public void onCompletion(MediaPlayer mp) {
                    if (playlist != null && playlist.length > 0) {
                        int next = random.nextInt(playlist.length);
                        playIndex(next);
                    }
                }
            });

            player.prepare();
            player.start();

        } catch (Exception e) {
            e.printStackTrace();
            player = null;
        }
    }

    /**
     * 重新应用音量（用户在设置里改完后调用）
     */
    public void applyVolume() {
        if (player == null) return;
        PrefManager pref = PrefManager.getInstance(context);
        float vol = pref.getMasterVolume() / 100.0f
                  * pref.getMusicVolume() / 100.0f;
        player.setVolume(vol, vol);
    }

    public void pause() {
        if (player != null && player.isPlaying()) {
            player.pause();
        }
    }

    public void resume() {
        if (player != null && !player.isPlaying()) {
            player.start();
        }
    }

    /**
     * 彻底停止（比如退出 App 时）
     */
    public void stop() {
        stopInternal();
        currentScene = null;
        playlist = null;
    }

    private void stopInternal() {
        if (player != null) {
            try {
                if (player.isPlaying()) player.stop();
            } catch (Exception e) {}
            player.release();
            player = null;
        }
    }
}
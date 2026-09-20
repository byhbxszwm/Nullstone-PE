package com.ojang.nullstone;

import android.graphics.Bitmap;

public class NativeLib {

    static {
        System.loadLibrary("nullstone");
    }

    public static native void initWorld(int seed);
    public static native void loadTexture(Bitmap bitmap);
    public static native void setScreenSize(int width, int height);
    public static native void renderFrame();
    public static native void updateGame(float deltaTime);
    public static native void onTouchDown(float x, float y);
    public static native void onTouchMove(float x, float y);
    public static native void onTouchUp(float x, float y);
    public static native void rotateCamera(float dYaw, float dPitch);
    public static native void setKeyState(int key, boolean pressed);

    public static native void setRenderDistance(int distance);
    public static native void setFov(int fov);
    public static native void setSensitivity(int sensitivity);

    public static native void breakBlock();
    public static native void placeBlock(int blockType);

    public static native void destroyWorld();
}
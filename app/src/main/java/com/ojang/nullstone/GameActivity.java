package com.ojang.nullstone;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.io.InputStream;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class GameActivity extends Activity {

    private GLSurfaceView glView;
    private FrameLayout rootLayout;

    private int lookPointerId = -1;
    private float lastTouchX = 0;
    private float lastTouchY = 0;

    private Handler handler = new Handler();
    private long touchStartTime = 0;
    private float touchStartX = 0;
    private float touchStartY = 0;
    private boolean isLongPress = false;
    private static final long LONG_PRESS_TIME = 400;
    private static final float MOVE_THRESHOLD = 40;
    private int currentBlock = 1;

    private Runnable longPressRunnable = new Runnable() {
        public void run() {
            isLongPress = true;
            NativeLib.breakBlock();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        rootLayout = new FrameLayout(this);

        glView = new GLSurfaceView(this);
        glView.setEGLContextClientVersion(2);
        glView.setRenderer(new GameRenderer());
        glView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);

        rootLayout.addView(glView, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT));

        // ===== 准星（十字） =====
        View crosshair = new View(this) {
            private final Paint p = new Paint();
            {
                p.setColor(0xFFFFFFFF);
                p.setStrokeWidth(dp(2));
                p.setAntiAlias(true);
            }

            @Override
            protected void onDraw(Canvas canvas) {
                int cx = getWidth() / 2;
                int cy = getHeight() / 2;
                int half = dp(10);
                canvas.drawLine(cx - half, cy, cx + half, cy, p);
                canvas.drawLine(cx, cy - half, cx, cy + half, p);
            }
        };
        FrameLayout.LayoutParams crossParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT);
        crosshair.setLayoutParams(crossParams);
        rootLayout.addView(crosshair);

        // ===== 按钮层 =====
        FrameLayout controlLayer = new FrameLayout(this);
        controlLayer.setLayoutParams(new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT));

        int btnSize = dp(60);
        int margin = dp(16);

        ImageView btnUp = createButton("up", btnSize);
        FrameLayout.LayoutParams upParams = new FrameLayout.LayoutParams(btnSize, btnSize);
        upParams.gravity = Gravity.BOTTOM | Gravity.LEFT;
        upParams.leftMargin = margin + btnSize + dp(4);
        upParams.bottomMargin = margin + btnSize * 2 + dp(8);
        btnUp.setLayoutParams(upParams);
        bindKey(btnUp, 0);
        controlLayer.addView(btnUp);

        ImageView btnDown = createButton("down", btnSize);
        FrameLayout.LayoutParams downParams = new FrameLayout.LayoutParams(btnSize, btnSize);
        downParams.gravity = Gravity.BOTTOM | Gravity.LEFT;
        downParams.leftMargin = margin + btnSize + dp(4);
        downParams.bottomMargin = margin;
        btnDown.setLayoutParams(downParams);
        bindKey(btnDown, 1);
        controlLayer.addView(btnDown);

        ImageView btnLeft = createButton("left", btnSize);
        FrameLayout.LayoutParams leftParams = new FrameLayout.LayoutParams(btnSize, btnSize);
        leftParams.gravity = Gravity.BOTTOM | Gravity.LEFT;
        leftParams.leftMargin = margin;
        leftParams.bottomMargin = margin + btnSize + dp(4);
        btnLeft.setLayoutParams(leftParams);
        bindKey(btnLeft, 3);
        controlLayer.addView(btnLeft);

        ImageView btnRight = createButton("right", btnSize);
        FrameLayout.LayoutParams rightParams = new FrameLayout.LayoutParams(btnSize, btnSize);
        rightParams.gravity = Gravity.BOTTOM | Gravity.LEFT;
        rightParams.leftMargin = margin + (btnSize + dp(4)) * 2;
        rightParams.bottomMargin = margin + btnSize + dp(4);
        btnRight.setLayoutParams(rightParams);
        bindKey(btnRight, 2);
        controlLayer.addView(btnRight);

        ImageView btnJump = createButton("jump", dp(80));
        FrameLayout.LayoutParams jumpParams = new FrameLayout.LayoutParams(dp(80), dp(80));
        jumpParams.gravity = Gravity.BOTTOM | Gravity.RIGHT;
        jumpParams.rightMargin = margin;
        jumpParams.bottomMargin = margin;
        btnJump.setLayoutParams(jumpParams);
        bindKey(btnJump, 4);
        controlLayer.addView(btnJump);

        rootLayout.addView(controlLayer);

        setContentView(rootLayout);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();
        int pointerIndex = event.getActionIndex();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN: {
                float x = event.getX(pointerIndex);
                float y = event.getY(pointerIndex);

                if (!isOnButton(x, y)) {
                    if (event.getPointerCount() == 1) {
                        lookPointerId = event.getPointerId(pointerIndex);
                        lastTouchX = x;
                        lastTouchY = y;
                        touchStartTime = System.currentTimeMillis();
                        touchStartX = x;
                        touchStartY = y;
                        isLongPress = false;
                        handler.postDelayed(longPressRunnable, LONG_PRESS_TIME);
                    } else {
                        lookPointerId = event.getPointerId(pointerIndex);
                        lastTouchX = x;
                        lastTouchY = y;
                    }
                }
                break;
            }

            case MotionEvent.ACTION_MOVE: {
                int idx = event.findPointerIndex(lookPointerId);
                if (idx >= 0) {
                    float x = event.getX(idx);
                    float y = event.getY(idx);
                    float dx = x - lastTouchX;
                    float dy = y - lastTouchY;

                    float totalMoved = (float)Math.sqrt(
                        (x - touchStartX) * (x - touchStartX) +
                        (y - touchStartY) * (y - touchStartY));
                    if (totalMoved > MOVE_THRESHOLD) {
                        handler.removeCallbacks(longPressRunnable);
                    }

                    NativeLib.rotateCamera(dx * 0.3f, dy * 0.3f);

                    lastTouchX = x;
                    lastTouchY = y;
                }
                break;
            }

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP: {
                int pointerId = event.getPointerId(pointerIndex);
                if (pointerId == lookPointerId) {
                    handler.removeCallbacks(longPressRunnable);

                    long pressTime = System.currentTimeMillis() - touchStartTime;
                    float totalMoved = (float)Math.sqrt(
                        (event.getX(pointerIndex) - touchStartX) * (event.getX(pointerIndex) - touchStartX) +
                        (event.getY(pointerIndex) - touchStartY) * (event.getY(pointerIndex) - touchStartY));

                    if (!isLongPress && pressTime < LONG_PRESS_TIME && totalMoved < MOVE_THRESHOLD) {
                        NativeLib.placeBlock(currentBlock);
                    }

                    lookPointerId = -1;
                }
                break;
            }

            case MotionEvent.ACTION_CANCEL: {
                handler.removeCallbacks(longPressRunnable);
                lookPointerId = -1;
                break;
            }
        }

        return super.dispatchTouchEvent(event);
    }

    private boolean isOnButton(float x, float y) {
        int btnSize = dp(60);
        int margin = dp(16);
        int screenH = getResources().getDisplayMetrics().heightPixels;
        int screenW = getResources().getDisplayMetrics().widthPixels;

        int dirLeft = margin;
        int dirRight = margin + (btnSize + dp(4)) * 3;
        int dirTop = screenH - margin - btnSize * 2 - dp(8);
        int dirBottom = screenH - margin;

        if (x >= dirLeft && x <= dirRight && y >= dirTop && y <= dirBottom) {
            return true;
        }

        int jumpLeft = screenW - margin - dp(80);
        int jumpRight = screenW - margin;
        int jumpTop = screenH - margin - dp(80);
        int jumpBottom = screenH - margin;

        if (x >= jumpLeft && x <= jumpRight && y >= jumpTop && y <= jumpBottom) {
            return true;
        }

        return false;
    }

    private ImageView createButton(String name, int size) {
        ImageView iv = new ImageView(this);
        try {
            InputStream is = getAssets().open("controls/" + name + ".png");
            Bitmap bmp = BitmapFactory.decodeStream(is);
            is.close();
            iv.setImageBitmap(bmp);
        } catch (Exception e) {
            iv.setBackgroundColor(Color.argb(120, 255, 255, 255));
        }
        iv.setAlpha(0.7f);
        return iv;
    }

    private void bindKey(ImageView btn, final int keyCode) {
        btn.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        NativeLib.setKeyState(keyCode, true);
                        v.setAlpha(1.0f);
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        NativeLib.setKeyState(keyCode, false);
                        v.setAlpha(0.7f);
                        break;
                }
                return true;
            }
        });
    }

    private int dp(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return (int)(dp * density + 0.5f);
    }

    // ============================================================
    //                    从 assets/blocks/ 拼 atlas
    // ============================================================
    private Bitmap buildAtlas() {
        String[] names = {
            "grass_top",
            "grass_side",
            "dirt",
            "stone",
            "cobblestone",
            "log_side",
            "log_top",
            "leaves",
            "sand",
            "planks"
        };

        final int TILE = 16;
        final int COLS = 16;
        int rows = (names.length + COLS - 1) / COLS;
        if (rows < 1) rows = 1;

        Bitmap atlas = Bitmap.createBitmap(COLS * TILE, rows * TILE, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(atlas);

        canvas.drawColor(0xFFFF00FF);

        Paint paint = new Paint();
        paint.setFilterBitmap(false);
        paint.setAntiAlias(false);
        paint.setDither(false);

        for (int i = 0; i < names.length; i++) {
            int col = i % COLS;
            int row = i / COLS;

            try {
                InputStream is = getAssets().open("blocks/" + names[i] + ".png");
                Bitmap tile = BitmapFactory.decodeStream(is);
                is.close();

                if (tile == null) continue;

                Bitmap scaled = tile;
                if (tile.getWidth() != TILE || tile.getHeight() != TILE) {
                    scaled = Bitmap.createScaledBitmap(tile, TILE, TILE, false);
                }
                canvas.drawBitmap(scaled, col * TILE, row * TILE, paint);

                if (scaled != tile) scaled.recycle();
                tile.recycle();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return atlas;
    }

    class GameRenderer implements GLSurfaceView.Renderer {
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            PrefManager pref = PrefManager.getInstance(GameActivity.this);

            NativeLib.setRenderDistance(pref.getRenderDistance());
            NativeLib.setFov(pref.getFov());
            NativeLib.setSensitivity(pref.getSensitivity());

            int seed = (int)(System.currentTimeMillis() % 100000);
            NativeLib.initWorld(seed);

            Bitmap atlas = buildAtlas();
            NativeLib.loadTexture(atlas);
            atlas.recycle();
        }

        public void onSurfaceChanged(GL10 gl, int width, int height) {
            NativeLib.setScreenSize(width, height);
        }

        public void onDrawFrame(GL10 gl) {
            NativeLib.updateGame(0.016f);
            NativeLib.renderFrame();
        }
    }

    protected void onPause() {
        super.onPause();
        if (glView != null) glView.onPause();
    }

    protected void onResume() {
        super.onResume();
        if (glView != null) glView.onResume();
    }

    protected void onDestroy() {
        super.onDestroy();
        NativeLib.destroyWorld();
    }
}
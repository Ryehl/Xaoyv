package com.billy.android.swipe.listener;

public interface SwipeBackListener {
    void onSwipeStateChanged(int state);
    void onSwipeStart(int direction);
    void onSwipeProcess(int direction, boolean settling, float progress);
    void onSwipeRelease(int direction, float progress, float xVelocity, float yVelocity);
}

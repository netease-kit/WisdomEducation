/*
 *  Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 *  Use of this source code is governed by a MIT license that can be found in the LICENSE file
 */

package com.netease.yunxin.app.wisdom.record.video.common.thread;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.text.TextUtils;

import java.util.HashMap;

public final class Handlers {
    private static final String DEFAULT_TAG = "Default";

    private static Handlers instance;

    public static synchronized Handlers sharedInstance() {
        if (instance == null) {
            instance = new Handlers();
        }

        return instance;
    }

    private static Handler sharedHandler;

    public static Handler sharedHandler(Context context) {
        if (sharedHandler == null) {
            sharedHandler = new Handler(context.getMainLooper());
        }

        return sharedHandler;
    }

    public static Handler newHandler(Context context) {
        return new Handler(context.getMainLooper());
    }

    private Handlers() {

    }

    public final Handler newHandler() {
        return newHandler(DEFAULT_TAG);
    }

    public final Handler newHandler(String tag) {
        return new Handler(getHandlerThread(tag).getLooper());
    }

    private final HashMap<String, HandlerThread> threads = new HashMap<>();

    private HandlerThread getHandlerThread(String tag) {
        HandlerThread handlerThread;
        synchronized (threads) {
            handlerThread = threads.get(tag);
            if (handlerThread == null) {
                handlerThread = new HandlerThread(nameOfTag(tag));
                handlerThread.start();
                threads.put(tag, handlerThread);
            }
        }

        return handlerThread;
    }

    public void removeHandler(String tag) {
        synchronized (threads) {
            HandlerThread handlerThread = threads.remove(tag);
            if (handlerThread != null) {
                handlerThread.quit();
            }
        }
    }

    private static String nameOfTag(String tag) {
        return "HT-" + (TextUtils.isEmpty(tag) ? DEFAULT_TAG : tag);
    }
}

/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.player;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Surface;
import android.view.TextureView;

import com.netease.neliveplayer.proxy.gslb.NEGslbResultListener;
import com.netease.neliveplayer.proxy.gslb.NEGslbServerModel;
import com.netease.neliveplayer.sdk.NELivePlayer;
import com.netease.neliveplayer.sdk.NEMediaDataSource;
import com.netease.neliveplayer.sdk.constant.NEErrorType;
import com.netease.neliveplayer.sdk.constant.NEPlayStatusType;
import com.netease.neliveplayer.sdk.model.NEAudioPcmConfig;
import com.netease.neliveplayer.sdk.model.NEAudioTrackInfo;
import com.netease.neliveplayer.sdk.model.NECacheConfig;
import com.netease.neliveplayer.sdk.model.NEDataSourceConfig;
import com.netease.neliveplayer.sdk.model.NEDecryptionConfig;
import com.netease.neliveplayer.sdk.model.NEMediaRealTimeInfo;
import com.netease.yunxin.app.wisdom.player.sdk.LivePlayer;
import com.netease.yunxin.app.wisdom.player.sdk.LivePlayerObserver;
import com.netease.yunxin.app.wisdom.player.sdk.VodPlayer;
import com.netease.yunxin.app.wisdom.player.sdk.VodPlayerObserver;
import com.netease.yunxin.app.wisdom.player.sdk.constant.CauseCode;
import com.netease.yunxin.app.wisdom.player.sdk.constant.DecryptionConfigCode;
import com.netease.yunxin.app.wisdom.player.sdk.model.AutoRetryConfig;
import com.netease.yunxin.app.wisdom.player.sdk.model.DataSourceConfig;
import com.netease.yunxin.app.wisdom.player.sdk.model.MediaInfo;
import com.netease.yunxin.app.wisdom.player.sdk.model.StateInfo;
import com.netease.yunxin.app.wisdom.player.sdk.model.VideoOptions;
import com.netease.yunxin.app.wisdom.player.sdk.model.VideoScaleMode;
import com.netease.yunxin.app.wisdom.player.util.Handlers;
import com.netease.yunxin.app.wisdom.player.view.IRenderView;
import com.netease.yunxin.kit.alog.ALog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author netease
 * <p>
 * 播放器基础扩展类，封装了播放器SDK接口操作的核心流程：
 * <p>
 * case 1: 先安装render view，再启动player
 * setup render view ->
 * init player ->
 * async prepare ->
 * on video size changed ->
 * set video size to render view ->
 * render view on measure done ->
 * on surface created ->
 * set player display ->
 * playing...
 * <p>
 * case 2: 先启动player，按需安装render view
 * init player ->
 * async prepare ->
 * on video size changed ->
 * setup render view ->
 * set video size to render view ->
 * render view on measure done ->
 * on surface created ->
 * set player display ->
 * playing...
 */
abstract class BaseLivePlayer extends VodPlayer {

    /// constant
    private static final String PLAYER_HANDLER_THREAD_TAG = "LIVE_PLAYER";

    /// context
    Context context;

    private Handler uiHandler;

    /// input
    private String videoPath;

    private NEMediaDataSource mediaDataSource;

    VideoOptions options;

    private AutoRetryConfig autoRetryConfig;

    /// player
    private List<LivePlayerObserver> observers = new ArrayList<>(1);

    private long positionCallbackInternal = 0;

    private NELivePlayer.OnCurrentPositionListener positionListener;

    private long realTimeCallbackInternal = 0;

    private NELivePlayer.OnCurrentRealTimeListener realTimeListener;

    private long syncTimeCallbackInternal = 0;

    private NELivePlayer.OnCurrentSyncTimestampListener syncTimeListener;

    private NELivePlayer.OnCurrentSyncContentListener contentTimeListener;

    private IRenderView renderView;

    NELivePlayer player;

    private Handler playerHandler; // player专用的异步带looper线程

    private final Object lock = new Object(); // 保护player的锁

    private NELivePlayer.OnSubtitleListener subtitleListener;

    private NEAudioPcmConfig audioPcmConfig;

    private NELivePlayer.OnAudioFrameFilterListener audioFrameFilterListener;

    private int videoFormat;

    private NELivePlayer.OnVideoFrameFilterListener videoFrameFilterListener;


    /// status
    AtomicBoolean hasReset = new AtomicBoolean(false); // 是否重置了播放器

    private STATE currentState = STATE.IDLE; // 当前状态

    private int cause; // 切换到当前状态的原因，例如停止的原因code，错误的错误码，其他状态为0

    private long lastCallBackPosition; // 最后一次回调的实时播放位置

    private long lastCallbackRealTime; // 最后一次回调的实时时间戳

    private long lastCallbackSyncTime; // 最后一次回调的实时时间戳

    private long lastPlayPosition; // 上一次播放的位置（点播用）

    private int lastAudioTrack = -1;    // 上一次选择的音轨

    /// video size
    private int videoWidth;

    private int videoHeight;

    private int videoSarNum;

    private int videoSarDen;

    //video mode
    private VideoScaleMode scaleMode = VideoScaleMode.FIT;

    /// timer/ticker
    private Timer vodTimer;

    private TimerTask vodTimerTask;

    private int timerIndex = 0;

    private LivePlayer mMasterPlayer;

    /// abstract methods for children
    abstract void onChildInit();

    abstract void onChildDestroy();

    BaseLivePlayer(final Context context, final String videoPath, final VideoOptions options) {
        // input
        this.videoPath = videoPath;
        //init other
        initBaseLivePlayer(context, options);
    }

    BaseLivePlayer(Context context, NEMediaDataSource mediaDataSource, VideoOptions options) {
        // input
        this.mediaDataSource = mediaDataSource;
        //init other
        initBaseLivePlayer(context, options);
    }

    private void initBaseLivePlayer(Context context, VideoOptions options) {
        // input
        this.options = options == null ? VideoOptions.getDefault() : options;
        // context
        this.context = context.getApplicationContext();
        this.uiHandler = new Handler(context.getMainLooper());
        // state
        setCurrentState(STATE.IDLE, 0);
    }

    /**
     * ***************************** player interface *************************
     */

    @Override
    public void registerPlayerObserver(LivePlayerObserver observer, boolean register) {
        if (observer == null) {
            return;
        }
        if (register) {
            if (!observers.contains(observer)) {
                observers.add(observer);
            }
        } else {
            observers.remove(observer);
        }
    }


    @Override
    public int registerAudioFrameFilterListener(NEAudioPcmConfig config,
                                                NELivePlayer.OnAudioFrameFilterListener listener, boolean register) {
        if (register && listener == null) {
            return -1;
        }
        if (register) {
            audioPcmConfig = config;
            audioFrameFilterListener = listener;
        } else {
            audioPcmConfig = null;
            audioFrameFilterListener = null;
        }
        return setOnAudioFrameFilterListener();
    }

    @Override
    public int registerVideoFrameFilterListener(int format, NELivePlayer.OnVideoFrameFilterListener listener,
                                                boolean register) {
        if (register && listener == null) {
            return -1;
        }
        if (register) {
            videoFormat = format;
            videoFrameFilterListener = listener;
        } else {
            videoFormat = 0;
            videoFrameFilterListener = null;
        }
        return setOnVideoFrameFilterListener();
    }

    @Override
    public void registerPlayerCurrentPositionListener(long interval, NELivePlayer.OnCurrentPositionListener listener,
                                                      boolean register) {
        if (register && (listener == null || interval <= 0)) {
            return; // 注册时参数无效
        }
        if (register) {
            positionCallbackInternal = interval;
            positionListener = listener;
        } else {
            positionCallbackInternal = 0;
            positionListener = null;
        }
        // try bind position listener
        setOnCurrentPositionListener();
    }


    @Override
    public void registerPlayerCurrentRealTimestampListener(long interval,
                                                           NELivePlayer.OnCurrentRealTimeListener listener,
                                                           boolean register) {
        if (register && (listener == null || interval <= 0)) {
            return; // 注册时参数无效
        }
        if (register) {
            realTimeCallbackInternal = interval;
            realTimeListener = listener;
        } else {
            realTimeCallbackInternal = 0;
            realTimeListener = null;
        }
        // try bind real time listener
        setOnCurrentRealTimeListener();
    }

    @Override
    public void registerPlayerCurrentSyncTimestampListener(long interval,
                                                           NELivePlayer.OnCurrentSyncTimestampListener listener,
                                                           boolean register) {
        if (register && (listener == null || interval <= 0)) {
            return; // 注册时参数无效
        }
        if (register) {
            syncTimeCallbackInternal = interval;
            syncTimeListener = listener;
        } else {
            syncTimeCallbackInternal = 0;
            syncTimeListener = null;
        }
        // try bind real time listener
        setOnCurrentSyncTimeListener();
    }

    @Override
    public void registerPlayerCurrentSyncContentListener(NELivePlayer.OnCurrentSyncContentListener listener,
                                                         boolean register) {
        if (register && (listener == null)) {
            return; // 注册时参数无效
        }
        if (register) {
            contentTimeListener = listener;
        } else {
            contentTimeListener = null;
        }
        // try bind content time listener
        setOnCurrentSyncContentListener();
    }


    @Override
    public void registerPlayerSubtitleListener(NELivePlayer.OnSubtitleListener listener, boolean register) {
        if (register && listener == null) {
            return; // 注册时参数无效
        }
        if (register) {
            subtitleListener = listener;
        } else {
            subtitleListener = null;
        }
        // try bind subtitle listener
        setOnSubtitleListener();
    }

    @Override
    public void setSubtitleFile(String path) {
        synchronized (lock) {
            if (player != null) {
                player.setSubtitleFile(path);
                ALog.i("set subtitle file " + path);
            }
        }
    }

    @Override
    public void setLoopCount(int loopCount) {
        synchronized (lock) {
            if (player != null) {
                player.setLoopCount(loopCount);
                ALog.i("set loop " + loopCount);
            }
        }
    }

    @Override
    public void setBufferSize(int size) {
        synchronized (lock) {
            if (player != null) {
                player.setBufferSize(size);
                ALog.i("set buffer size " + size);
            }
        }
    }

    @Override
    public int getLoopCount() {
        synchronized (lock) {
            if (player != null) {
                ALog.i("find  is looping or not ");
                return player.getLoopCount();
            }
        }
        return 0;
    }

    @Override
    public void syncClockTo(LivePlayer player) {
        synchronized (lock) {
            mMasterPlayer = player;
        }
    }

    /*
     * 异步初始化 or 点播恢复播放：
     * case 1: 上层构造完 NIMLivePlayer 后，初始化并开始播放。
     * case 2: 切换视频地址，先reset后，立即异步初始化
     * case 3: 点播切换分辨率时，切换视频地址，先reset后，立即异步初始化
     * case 4: 断网时reset，网络恢复时立即异步初始化
     * case 5: 点播暂停后重新开始播放
     */
    @Override
    public void start() {
        synchronized (lock) {
            ALog.i("currentState..." + currentState + "...." + videoPath);
            // case 5：点播暂停后重新开始
            if (player != null && currentState == STATE.PAUSED) {
                ALog.i("player restart...");
                restart();
                return;
            }
            if (player != null && currentState == STATE.PREPARING) {
                ALog.i("player is preparing ...");
                return;
            }
            if (player != null && currentState == STATE.PREPARED) {
                ALog.i("player is prepared ...");
                player.start();
                resetPlayerState();
                return;
            }
            if (player != null && currentState == STATE.STOPPED) {
                ALog.i("player is stopped ...");
                player.seekTo(0L);
                player.start();
                resetPlayerState();
                return;
            }
            if (player != null && currentState == STATE.PLAYING) {
                ALog.i("player is playing ...");
                return;
            }
            ALog.i("player async init...");
            // 正在播放中，如果要重新初始化，那么要重置
            if (player != null && currentState != STATE.IDLE && !hasReset.get()) {
                ALog.i("reset current player before async init...");
                resetPlayer();
            }
            // 工作线程
            if (playerHandler == null) {
                playerHandler = Handlers.sharedInstance().newHandler(PLAYER_HANDLER_THREAD_TAG + hashCode());
            }
            // state
            setCurrentState(STATE.PREPARING, 0);
            // 子类初始化逻辑
            onChildInit();
            // 异步初始化播放器
            playerHandler.post(new Runnable() {

                @Override
                public void run() {
                    initPlayer();
                }
            });
        }
    }

    private void resetPlayerState() {
        // 点播，重新seekTo上次的位置
        if (lastPlayPosition > 0) {
            player.seekTo(lastPlayPosition);
            lastPlayPosition = 0; // 复位
        }
        if (lastAudioTrack != -1) {
            player.setSelectedAudioTrack(lastAudioTrack);
            lastAudioTrack = -1;
        }
        ALog.i("player start...");

        // 点播，开启ticker timer
        if (player.getDuration() > 0) {
            startVodTimer();
        }
    }

    /*
     * 点播暂停后重新开始
     */
    private void restart() {
        player.start();
        reSetupRenderView();
        // state
        setCurrentState(STATE.PLAYING, 0);
        final MediaInfo mediaInfo = new MediaInfo(player.getMediaInfo(), player.getDuration());
        // 点播，开启ticker timer
        if (player.getDuration() > 0) {
            startVodTimer();
        }
        // notify
        uiHandler.post(new Runnable() {

            @Override
            public void run() {
                try {
                    for (LivePlayerObserver observer : getObservers()) {
                        observer.onPrepared(mediaInfo);
                    }
                } catch (Throwable th) {
                    th.printStackTrace();
                }
            }
        });
    }


    private void reSetupRenderView() {
        // try bind surface holder, case: player reset后、切后台切回前台，原来绑定的surfaceView的被置null了，重新初始化时，要重新绑上去
        if (renderView != null && renderView.getSurface() != null) {
            setupRenderView(renderView, scaleMode);
        }
    }


    @Override
    public void setupRenderView(IRenderView renderView, VideoScaleMode videoScaleMode) {
        ALog.i("setup render view, view=" + renderView);
        if (renderView == null) {
            this.renderView = null;
            setDisplaySurface(null);
            return;
        }
        this.renderView = renderView;
        this.scaleMode = videoScaleMode;
        this.renderView.onSetupRenderView();
        this.renderView.setCallback(surfaceCallback); // 将render view的surface回调告知player
        setVideoSizeToRenderView(); // 触发render view的显示
        setDisplaySurface(renderView.getSurface());
    }

    @Override
    public void setVideoScaleMode(VideoScaleMode videoScaleMode) {
        ALog.i("setVideoScaleMode " + videoScaleMode);
        if (renderView == null || videoScaleMode == null) {
            ALog.i("renderView is null or videoScaleMode is null ");
            return;
        }
        this.scaleMode = videoScaleMode;
        setVideoSizeToRenderView(); // 触发render view的显示
    }

    @Override
    public void showView() {
        if (renderView != null) {
            renderView.showView(true);
        }
    }

    @Override
    public void hideView() {
        if (renderView != null) {
            renderView.showView(false);
        }
    }

    @Override
    public void pause() {
        // 点播暂停
        synchronized (lock) {
            if (player != null && player.isPlaying() && player.getDuration() > 0) {
                player.pause();
                stopVodTimer(false); // 停止ticker timer
                ALog.i("player paused");
                setCurrentState(STATE.PAUSED, CauseCode.CODE_VIDEO_PAUSED_BY_MANUAL);
            }
        }
    }

    @Override
    public void stop() {
        final long timeStart = System.currentTimeMillis();
        // 工作线程退出
        if (playerHandler != null) {
            playerHandler.removeCallbacksAndMessages(null);
            Handlers.sharedInstance().removeHandler(PLAYER_HANDLER_THREAD_TAG + hashCode());
            playerHandler = null;
        }
        // timer销毁
        stopVodTimer(false);
        // 销毁播放器
        synchronized (lock) {
            mMasterPlayer = null;
            if (renderView != null) {
                renderView.setCallback(null);
                setDisplaySurface(null);
            }
            if (player != null) {
                //                player.reset();
                player.release();
                player = null;
            }
        }
        // 子类销毁
        onChildDestroy();
        // state
        ALog.i("stop && destroy player! cost=" + (System.currentTimeMillis() - timeStart));
        setCurrentState(STATE.STOPPED, CauseCode.CODE_VIDEO_STOPPED_BY_MANUAL);
    }

    @Override
    public void switchContentPath(String newVideoPath) {
        if (TextUtils.isEmpty(newVideoPath)) {
            return;
        }
        if (videoPath != null && videoPath.equals(newVideoPath)) {
            ALog.i("no need to switch video, as path is the same! ");
            return;
        }
        ALog.i("switching video path to " + newVideoPath);
        videoPath = newVideoPath;
        // 重置
        resetPlayer();
        start();
    }

    @Override
    public void queryPreloadUrlResult(final String url, final NEGslbResultListener listener) {
        if (player == null) {
            ALog.i("queryPreloadUrlResult player is null ");
            return;
        }
        player.queryPreloadUrlResult(url, listener);
    }

    @Override
    public void switchWithGslbResult(final Object session, final NEGslbServerModel model) {
        synchronized (lock) {
            player.switchWithGslbResult(session, model);
        }
    }

    @Override
    public NEGslbServerModel getCurrentServerModel() {
        return player.getCurrentServerModel();
    }

    @Override
    public void setMute(boolean mute) {
        synchronized (lock) {
            if (player != null) {
                player.setMute(mute);
                ALog.i("set mute " + mute);
            }
        }
    }

    @Override
    public void setMirror(boolean isMirror) {
        synchronized (lock) {
            //软解使用SDK镜像接口
            if (player != null && options != null && !options.hardwareDecode) {
                player.setMirror(isMirror);
                ALog.i("set mirror,player.setMirror " + isMirror);
            }
            //硬解只支持TextureView，使用TextureView的setMirror接口
            if (renderView != null && renderView instanceof TextureView && options != null && options.hardwareDecode) {
                ((TextureView) renderView).setScaleX(isMirror ? -1.0F : 1.0F);
                ALog.i("set mirror, renderView.setScaleX" + isMirror);
            }
        }
    }

    @Override
    public void setVolume(float volume) {
        synchronized (lock) {
            if (player != null) {
                player.setVolume(volume);
                ALog.i("set volume " + volume);
            }
        }
    }


    @Override
    public boolean isPlaying() {
        synchronized (lock) {
            if (player != null) {
                ALog.i("find  is playing or not ");
                return player.isPlaying();
            }
        }
        return false;
    }

    @Override
    public StateInfo getCurrentState() {
        return new StateInfo(currentState, cause);
    }


    @Override
    public long getCurrentSyncTimestamp() {
        synchronized (lock) {
            if (player != null) {
                return player.getCurrentSyncTimestamp();
            }
        }
        return 0;
    }

    @Override
    public long getDuration() {
        synchronized (lock) {
            if (player != null) {
                return player.getDuration();
            }
        }
        return 0;
    }

    @Override
    public long getCurrentPosition() {
        synchronized (lock) {
            if (player != null) {
                return player.getCurrentPosition();
            }
        }
        return 0;
    }

    @Override
    public long getCachedPosition() {
        synchronized (lock) {
            if (player != null) {
                return player.getPlayableDuration();
            }
        }
        return 0;
    }

    @Override
    public float getCurrentPositionPercent() {
        synchronized (lock) {
            if (player != null) {
                long duration = player.getDuration();
                long current = player.getCurrentPosition();
                if (duration > 0) {
                    return 100.0f * current / duration;
                }
            }
        }
        return 0;
    }

    @Override
    public void seekTo(long position) {
        synchronized (lock) {
            if (player != null) {
                player.seekTo(position);
                ALog.i("seek to " + position);
            }
        }
    }


    @Override
    public void switchContentUrl(String newVideoPath) {
        synchronized (lock) {
            if (player != null) {
                if (TextUtils.isEmpty(newVideoPath)) {
                    return;
                }
                if (videoPath != null && videoPath.equals(newVideoPath)) {
                    ALog.i("no need to switch video, as path is the same! ");
                    return;
                }
                ALog.i("switching video path to " + newVideoPath);
                videoPath = newVideoPath;
                player.switchContentUrl(newVideoPath);
                // try bind surface holder, case: player reset后，原来绑定的surfaceView的被置null了，重新初始化时，要重新绑上去
                if (renderView != null && renderView.getSurface() != null) {
                    setDisplaySurface(renderView.getSurface());
                }
                ALog.i("switch content url " + newVideoPath);
            }
        }
    }


    @Override
    public void switchContentUrl(String newVideoPath, DataSourceConfig config) {
        ALog.i("switchContentUrl switching video path to " + newVideoPath);
        newSwitchContentUrl(newVideoPath, config);
    }

    private void newSwitchContentUrl(String newVideoPath, DataSourceConfig config) {
        synchronized (lock) {
            if (player != null) {
                if (TextUtils.isEmpty(newVideoPath)) {
                    return;
                }
                if (videoPath != null && videoPath.equals(newVideoPath)) {
                    ALog.i("no need to switch video, as path is the same! ");
                    return;
                }
                if (config == null) {
                    return;
                }
                ALog.i("switching video path to " + newVideoPath);
                videoPath = newVideoPath;
                NEDataSourceConfig dataSourceConfig = new NEDataSourceConfig();
                if (config.cacheConfig != null) {
                    dataSourceConfig.cacheConfig = new NECacheConfig(config.cacheConfig.isCache,
                            config.cacheConfig.cachePath);
                }
                if (config.decryptionConfig != null) {
                    if (config.decryptionConfig.decryptionCode == DecryptionConfigCode.CODE_DECRYPTION_INFO) {
                        dataSourceConfig.decryptionConfig = new NEDecryptionConfig(
                                config.decryptionConfig.transferToken, config.decryptionConfig.accid,
                                config.decryptionConfig.appKey, config.decryptionConfig.token);
                    } else if (config.decryptionConfig.decryptionCode == DecryptionConfigCode.CODE_DECRYPTION_KEY) {
                        dataSourceConfig.decryptionConfig = new NEDecryptionConfig(config.decryptionConfig.flvKey,
                                config.decryptionConfig.flvKeyLen);
                    } else {
                        ALog.e(" player need init dataSourceConfig");
                    }

                }
                player.switchContentUrl(newVideoPath, dataSourceConfig);
                // try bind surface holder, case: player reset后，原来绑定的surfaceView的被置null了，重新初始化时，要重新绑上去
                if (renderView != null && renderView.getSurface() != null) {
                    setDisplaySurface(renderView.getSurface());
                }
                ALog.i("switch content url with data source config " + newVideoPath);
            }
        }
    }


    @Override
    public void setPlaybackSpeed(float speed) {
        synchronized (lock) {
            if (player != null) {
                if (speed > 2.0) {
                    speed = 2.0f;
                } else if (speed < 0.5f) {
                    speed = 0.5f;
                }
                player.setPlaybackSpeed(speed);
                ALog.i("set playback speed to " + speed);
            }
        }
    }

    @Override
    public Bitmap getSnapshot() {
        Bitmap bm = null;
        synchronized (lock) {
            if (player != null && videoWidth != 0 && videoHeight != 0) {
                bm = Bitmap.createBitmap(videoWidth, videoHeight, Bitmap.Config.ARGB_8888);
                if (player.getSnapshot(bm)) {
                    ALog.i("player get snapshot success, w=" + bm.getWidth() + ", h=" + bm.getHeight());
                } else {
                    bm = null;
                    ALog.i("player get snapshot failed!");
                }
            }
        }
        return bm;
    }

    @Override
    public void onActivityStop(boolean isLive) {
    }

    @Override
    public void onActivityResume(boolean isLive) {
        reSetupRenderView();
    }

    @Override
    public NEAudioTrackInfo[] getAudioTracksInfo() {
        synchronized (lock) {
            if (player != null) {
                return player.getAudioTracksInfo();
            }
        }
        return null;
    }

    @Override
    public int getSelectedAudioTrack() {
        synchronized (lock) {
            if (player != null) {
                return player.getSelectedAudioTrack();
            }
        }
        return -1;
    }

    @Override
    public int setSelectedAudioTrack(int index) {
        synchronized (lock) {
            if (player != null) {
                return player.setSelectedAudioTrack(index);
            }
        }
        return -1;
    }

    @Override
    public void setAutoRetryConfig(AutoRetryConfig config) {
        synchronized (lock) {
            autoRetryConfig = config;
            if (player != null) {
                player.setAutoRetryConfig(config);
            }
            ALog.i("set auto retry config: " + config);
        }
    }

    @Override
    public NEMediaRealTimeInfo getMediaRealTimeInfo() {
        synchronized (lock) {
            if (player != null) {
                return player.getMediaRealTimeInfo();
            }
        }
        return null;
    }

    /**
     * ********************************** core *******************************
     */

    /*
     * 初始化播放器并异步prepare
     */
    private void initPlayer() {
        synchronized (lock) {
            // native player
            if (player == null) {
                final long timeStart = System.currentTimeMillis();
                player = NELivePlayer.create(); // 耗时40ms-300ms，如果上次析构底层还未完成，那么可能耗时比较高
                ALog.i("create player=" + player + ", cost=" + (System.currentTimeMillis() - timeStart));
            }
            // config player
            configPlayer();
            player.prepareAsync();
            // recovery player
            hasReset.set(false);
            // notify
            uiHandler.post(new Runnable() {

                @Override
                public void run() {
                    try {
                        for (LivePlayerObserver observer : getObservers()) {
                            observer.onPreparing();
                        }
                    } catch (Throwable th) {
                        th.printStackTrace();
                    }
                }
            });
        }
        ALog.i("player async prepare...");
    }

    /*
     * 配置player
     * 在lock下操作player
     */
    private void configPlayer() {
        if (player == null) {
            return;
        }
        // config
        player.setBufferStrategy(options.bufferStrategy.getValue()); // 设置播放缓冲策略
        player.setBufferSize(options.bufferSize); // 点播设置播放缓冲大小
        player.setHardwareDecoder(options.hardwareDecode); // 设置解码模式
        player.setShouldAutoplay(options.isAutoStart); // 是否需要自动播放
        int timeout = options.playbackTimeout;
        if (timeout <= 0) {
            timeout = 10;
            options.playbackTimeout = 10;
        }
        player.setPlaybackTimeout(timeout); // 超时重连时间10s
        player.setLoopCount(options.loopCount);
        player.setAccurateSeek(options.isAccurateSeek);
        player.setAutoRetryConfig(autoRetryConfig);
        // player listeners
        player.setOnPreparedListener(onPreparedListener);
        player.setOnVideoSizeChangedListener(onVideoSizeChangedListener);
        player.setOnCompletionListener(onCompletionListener);
        player.setOnErrorListener(onErrorListener);
        player.setOnBufferingUpdateListener(onBufferingUpdateListener);
        player.setOnInfoListener(onInfoListener);
        player.setOnSeekCompleteListener(onSeekCompleteListener);
        player.setOnHttpResponseInfoListener(onHttpResponseInfoListener);
        // try bind audio frame listener
        setOnAudioFrameFilterListener();
        // try bind video frame listener
        setOnVideoFrameFilterListener();
        // try bind real time listener
        setOnCurrentRealTimeListener();
        if (options.isSyncOpen) {
            player.setSyncOpen(true);
        }
        // try bind sync time listener
        setOnCurrentSyncTimeListener();
        // try bind content time listener
        setOnCurrentSyncContentListener();
        // try bind subtitle listener
        setOnSubtitleListener();
        // video source
        try {
            ALog.i("set player data source=" + videoPath);
            if (mediaDataSource != null) {
                player.setDataSource(mediaDataSource);
            } else if (options.dataSourceConfig != null) {
                NEDataSourceConfig dataSourceConfig = new NEDataSourceConfig();
                if (options.dataSourceConfig.cacheConfig != null) {
                    dataSourceConfig.cacheConfig = new NECacheConfig(options.dataSourceConfig.cacheConfig.isCache,
                            options.dataSourceConfig.cacheConfig.cachePath);
                }
                if (options.dataSourceConfig.decryptionConfig != null) {
                    if (options.dataSourceConfig.decryptionConfig.decryptionCode ==
                            DecryptionConfigCode.CODE_DECRYPTION_INFO) {
                        dataSourceConfig.decryptionConfig = new NEDecryptionConfig(
                                options.dataSourceConfig.decryptionConfig.transferToken,
                                options.dataSourceConfig.decryptionConfig.accid,
                                options.dataSourceConfig.decryptionConfig.appKey,
                                options.dataSourceConfig.decryptionConfig.token);
                    } else if (options.dataSourceConfig.decryptionConfig.decryptionCode ==
                               DecryptionConfigCode.CODE_DECRYPTION_KEY) {
                        dataSourceConfig.decryptionConfig = new NEDecryptionConfig(
                                options.dataSourceConfig.decryptionConfig.flvKey,
                                options.dataSourceConfig.decryptionConfig.flvKeyLen);
                    } else {
                        ALog.e("configPlayer player need init dataSourceConfig");
                    }

                }
                player.setDataSource(videoPath, dataSourceConfig);
            } else {
                player.setDataSource(videoPath); // 如果数据源连接不上，那么需要1分钟才会回调onError, 错误码-1002
            }
        } catch (IOException e) {
            e.printStackTrace();
            ALog.e("set player data source error, e=" + e.getMessage());
        }
        if (mMasterPlayer instanceof BaseLivePlayer) {
            player.syncClockTo(((BaseLivePlayer) mMasterPlayer).player);
        }
        reSetupRenderView();


    }

    /*
     * 向render view设置视频帧大小
     * 前置条件：
     * 1) Player#onVideoSizeChanged必须回调了，存储了视频帧大小
     * 2) 已经安装了render view
     * <p>
     * 调用时机：
     * 1) onVideoSizeChanged回调中
     * 2) setupRenderView时
     */
    private void setVideoSizeToRenderView() {
        if (videoWidth != 0 && videoHeight != 0 && renderView != null) {
            renderView.setVideoSize(videoWidth, videoHeight, videoSarNum, videoSarDen, scaleMode);
        }
    }

    /*
     * 安装实时音频数据回调监听器
     * <p>
     * 调用时机：
     * 1) register时，可能player还未初始化，那么缓存起来，等configPlayer时再注册
     * 2) configPlayer时，可能player重新初始化，重新注册监听器
     */
    private int setOnAudioFrameFilterListener() {
        synchronized (lock) {
            if (player == null) {
                return -1;
            }
            if (audioFrameFilterListener != null) {
                player.setOnAudioFrameFilterListener(audioPcmConfig, audioFrameFilterListener);
                ALog.i("set on audioFrame filter listener=" + audioFrameFilterListener + ", audioPcmConfig=" +
                        audioPcmConfig);
            } else {
                player.setOnAudioFrameFilterListener(null, null);
                ALog.i("set on audioFrame filter listener=null");
            }
            return 0;
        }
    }


    /*
     * 安装实时视频数据回调监听器
     * <p>
     * 调用时机：
     * 1) register时，可能player还未初始化，那么缓存起来，等configPlayer时再注册
     * 2) configPlayer时，可能player重新初始化，重新注册监听器
     */
    private int setOnVideoFrameFilterListener() {
        synchronized (lock) {
            if (player == null) {
                return -1;
            }
            if (videoFrameFilterListener != null) {
                player.setOnVideoFrameFilterListener(videoFormat, videoFrameFilterListener);
                ALog.i("set on videoFrame filter listener=" + videoFrameFilterListener + ", videoFormat=" +
                        videoFormat);
            } else {
                player.setOnVideoFrameFilterListener(0, null);
                ALog.i("set on videoFrame filter listener=null");
            }
            return 0;
        }
    }

    /*
     * 安装实时播放位置回调监听器
     * <p>
     * 调用时机：
     * 1) register时，可能player还未初始化，那么缓存起来，等configPlayer时再注册
     * 2) configPlayer时，可能player重新初始化，重新注册监听器
     */
    private void setOnCurrentPositionListener() {
        synchronized (lock) {
            if (player == null) {
                return;
            }
            if (positionListener != null && positionCallbackInternal > 0) {
                player.setOnCurrentPositionListener(positionCallbackInternal, onCurrentPositionListener);
                ALog.i("set on current position listener=" + positionListener + ", interval=" +
                        positionCallbackInternal);
            } else {
                player.setOnCurrentPositionListener(0, null);
                ALog.i("set on current position listener=null");
            }
        }
    }


    /*
     * 安装实时真实时间戳回调监听器
     * <p>
     * 调用时机：
     * 1) register时，可能player还未初始化，那么缓存起来，等configPlayer时再注册
     * 2) configPlayer时，可能player重新初始化，重新注册监听器
     */
    private void setOnCurrentRealTimeListener() {
        synchronized (lock) {
            if (player == null) {
                return;
            }
            if (realTimeListener != null && realTimeCallbackInternal > 0) {
                player.setOnCurrentRealTimeListener(realTimeCallbackInternal, onCurrentRealTimeListener);
                ALog.i("set on current sync time listener=" + syncTimeListener + ", interval=" +
                        syncTimeCallbackInternal);
            } else {
                player.setOnCurrentRealTimeListener(0, null);
                ALog.i("set on current sync time listener=null");
            }
        }
    }

    /*
     * 安装实时同步时间戳回调监听器
     * <p>
     * 调用时机：
     * 1) register时，可能player还未初始化，那么缓存起来，等configPlayer时再注册
     * 2) configPlayer时，可能player重新初始化，重新注册监听器
     */
    private void setOnCurrentSyncTimeListener() {
        synchronized (lock) {
            if (player == null) {
                return;
            }
            if (syncTimeListener != null && syncTimeCallbackInternal > 0) {
                player.setOnCurrentSyncTimestampListener(syncTimeCallbackInternal, onCurrentSyncTimeListener);
                ALog.i("set on current sync time listener=" + syncTimeListener + ", interval=" +
                        syncTimeCallbackInternal);
            } else {
                player.setOnCurrentSyncTimestampListener(0, null);
                ALog.i("set on current sync time listener=null");
            }
        }
    }

    /*
     * 安装内容信息时间戳回调监听器
     * <p>
     * 调用时机：
     * 1) register时，可能player还未初始化，那么缓存起来，等configPlayer时再注册
     * 2) configPlayer时，可能player重新初始化，重新注册监听器
     */
    private void setOnCurrentSyncContentListener() {
        synchronized (lock) {
            if (player == null) {
                return;
            }
            if (contentTimeListener != null) {
                player.setOnCurrentSyncContentListener(contentTimeListener);
                ALog.i("set on current content time listener=" + contentTimeListener);
            } else {
                player.setOnCurrentSyncContentListener(null);
                ALog.i("set on current content time listener=null");
            }
        }
    }


    /*
     * 安装字幕回调监听器
     * <p>
     * 调用时机：
     * 1) register时，可能player还未初始化，那么缓存起来，等configPlayer时再注册
     * 2) configPlayer时，可能player重新初始化，重新注册监听器
     */
    private void setOnSubtitleListener() {
        synchronized (lock) {
            if (player == null) {
                return;
            }
            if (subtitleListener != null) {
                player.setOnSubtitleListener(subtitleListener);
                ALog.i("set on subtitle listener=" + subtitleListener);
            } else {
                player.setOnSubtitleListener(null);
                ALog.i("set on subtitle listener=null");
            }
        }
    }

    /*
     * 播放器和显示surface的绑定
     * case 1: surfaceCreated时绑定到播放器
     * case 2: surfaceDestroyed时解除绑定
     * case 3: 播放器被reset后重新初始化时，如果surface没有被销毁，那么重新绑定到播放器
     */
    private synchronized void setDisplaySurface(Surface surface) {
        if (player != null) {
            player.setSurface(surface);
            ALog.i("set player display surface=" + surface);
        }
    }

    /*
     * 重置播放器
     * 使用场景：
     * case 1: 断网的时候,主动重置,等网络恢复后再重新初始化来恢复播放
     * case 2: 切换播放地址时，先重置，存储新地址后再重新初始化
     * case 3: 播放过程中发生错误/解析视频流错误，重置
     * case 4: 长期处于后台,回到前台时，主动重置，给予恢复的机会
     * case 5: 播放结束，重置
     */
    void resetPlayer() {
        stopVodTimer(false); // 停止定时器
        synchronized (lock) {
            if (player != null) {
                player.reset();
                hasReset.set(true);
                ALog.i("reset player!");
            }
        }
    }

    synchronized void setCurrentState(final STATE state, final int causeCode) {
        currentState = state;
        if (causeCode < NEErrorType.NELP_EN_UNKNOWN_ERROR && cause != 0 && cause >= NEErrorType.NELP_EN_UNKNOWN_ERROR) {
            // 上层定义的错误码，不要覆盖掉已有的播放器底层的错误码
            ALog.i(
                    "player error code=" + cause + ", new cause code=" + causeCode + ", never replace error code!");
        } else {
            cause = causeCode;
        }
        ALog.i("player state changed to " + state + (cause != 0 ? ", cause code=" + cause : ""));
        // task
        //        if (playerHandler != null) {
        //            playerHandler.removeCallbacks(preparingTimeoutTask);
        //            if (currentState == STATE.PREPARING) {
        //                playerHandler.postDelayed(preparingTimeoutTask, options.playbackTimeout * 1000);
        //            }
        //        }
        // notify
        uiHandler.post(new Runnable() {

            @Override
            public void run() {
                try {
                    for (LivePlayerObserver observer : getObservers()) {
                        observer.onStateChanged(new StateInfo(state, cause));
                    }
                } catch (Throwable th) {
                    th.printStackTrace();
                }
            }
        });
    }

    void savePlayerState() {
        if (player == null || player.getDuration() <= 0) {
            return;
        }
        lastPlayPosition = player.getCurrentPosition();
        lastAudioTrack = player.getSelectedAudioTrack();
    }

    /*
     * 准备阶段超时任务
     */
    //    private Runnable preparingTimeoutTask = new Runnable() {
    //        @Override
    //        public void run() {
    //            ALog.e("preparing timeout!!! Timeout=" + options.playbackTimeout + "s");
    //
    //            onErrorListener.onError(player, CauseCode.CODE_VIDEO_PREPARING_TIMEOUT, 0);
    //        }
    //    };

    /**
     * ***************************** surface callback *************************
     */

    private IRenderView.SurfaceCallback surfaceCallback = new IRenderView.SurfaceCallback() {

        @Override
        public void onSurfaceCreated(Surface surface) {
            ALog.i("on surface created");
            setDisplaySurface(surface); // 播放器和显示surface的绑定
        }

        @Override
        public void onSurfaceDestroyed(Surface surface) {
            ALog.i("on surface destroyed");
            setDisplaySurface(null); // 解除播放器和显示Surface的绑定
        }

        @Override
        public void onSurfaceSizeChanged(Surface surface, int format, int width, int height) {
            ALog.i("on surface changed, width=" + width + ", height=" + height + ", format=" + format);
        }
    };

    /**
     * ******************************* player callback ******************************
     */

    /*
     * 获取到视频尺寸or视频尺寸发生变化
     * 最早回调
     */
    private NELivePlayer.OnVideoSizeChangedListener onVideoSizeChangedListener = new NELivePlayer.OnVideoSizeChangedListener() {

        @Override
        public void onVideoSizeChanged(NELivePlayer p, int width, int height, int sarNum, int sarDen) {
            if (videoWidth == p.getVideoWidth() && videoHeight == p.getVideoHeight() &&
                    ((videoSarNum == sarNum && videoSarDen == sarDen) || sarNum <= 0 || sarDen <= 0)) {
                return; // the same or invalid sarNum/sarDen
            }
            videoWidth = width;
            videoHeight = height;
            videoSarNum = sarNum;
            videoSarDen = sarDen;
            ALog.i("on video size changed, width=" + videoWidth + ", height=" + videoHeight + ", sarNum=" +
                    videoSarNum + ", sarDen=" + videoSarDen);
            setVideoSizeToRenderView();
        }
    };

    /*
     * 视频播放器准备好了，可以开始播放了
     */
    private NELivePlayer.OnPreparedListener onPreparedListener = new NELivePlayer.OnPreparedListener() {

        @Override
        public void onPrepared(NELivePlayer neLivePlayer) {
            ALog.i("on player prepared!");
            synchronized (lock) {
                if (player != null) {
                    setCurrentState(STATE.PREPARED, 0);
                    if (!options.isAutoStart) {
                        player.start();
                    }
                    resetPlayerState();

                    final MediaInfo mediaInfo = new MediaInfo(player.getMediaInfo(), player.getDuration());


                    // notify
                    try {
                        for (LivePlayerObserver observer : getObservers()) {
                            observer.onPrepared(mediaInfo);
                        }
                    } catch (Throwable th) {
                        th.printStackTrace();
                    }
                }
            }
        }
    };

    /*
     * 视频播放结束：点播资源播放完成，直播推流停止时
     */
    private NELivePlayer.OnCompletionListener onCompletionListener = new NELivePlayer.OnCompletionListener() {

        @Override
        public void onCompletion(NELivePlayer neLivePlayer) {
            ALog.i("on player completion!");
            // notify
            try {
                VodPlayerObserver o;
                for (LivePlayerObserver observer : getObservers()) {
                    if (observer instanceof VodPlayerObserver) {
                        o = (VodPlayerObserver) observer;
                        o.onCompletion();
                    }
                }
            } catch (Throwable th) {
                th.printStackTrace();
            }
//            // reset 这里在播放结束重置了播放器，用户也可以调用release释放播放器
//            resetPlayer();

            stopVodTimer(false); // 停止定时器
            // state
            setCurrentState(STATE.STOPPED, CauseCode.CODE_VIDEO_STOPPED_AS_ON_COMPLETION);
        }
    };

    /*
     * 播放过程中发生错误
     */
    private NELivePlayer.OnErrorListener onErrorListener = new NELivePlayer.OnErrorListener() {

        @Override
        public boolean onError(NELivePlayer neLivePlayer, final int what, final int extra) {
            ALog.e("on player error!!! what=" + what + ", extra=" + extra);
            // reset
            resetPlayer();
            // notify
            uiHandler.post(new Runnable() {

                @Override
                public void run() {
                    try {
                        for (LivePlayerObserver observer : getObservers()) {
                            observer.onError(what, extra);
                        }
                    } catch (Throwable th) {
                        th.printStackTrace();
                    }
                }
            });
            // state
            setCurrentState(STATE.ERROR, what);
            return true;
        }
    };

    /*
     * 视频状态变化、事件发生
     */
    private NELivePlayer.OnInfoListener onInfoListener = new NELivePlayer.OnInfoListener() {

        @Override
        public boolean onInfo(NELivePlayer neLivePlayer, int what, int extra) {
            try {
                if (what == NEPlayStatusType.NELP_BUFFERING_START) {
                    ALog.i("on player info: buffering start");
                    for (LivePlayerObserver observer : getObservers()) {
                        observer.onBufferingStart();
                    }

                } else if (what == NEPlayStatusType.NELP_BUFFERING_END) {
                    ALog.i("on player info: buffering end");
                    for (LivePlayerObserver observer : getObservers()) {
                        observer.onBufferingEnd();
                    }

                } else if (what == NEPlayStatusType.NELP_FIRST_VIDEO_RENDERED) {
                    ALog.i("on player info: first video rendered");
                    // state
                    setCurrentState(STATE.PLAYING, 0);
                    for (LivePlayerObserver observer : getObservers()) {
                        observer.onFirstVideoRendered();
                    }

                } else if (what == NEPlayStatusType.NELP_FIRST_AUDIO_RENDERED) {
                    ALog.i("on player info: first audio rendered");
//                    setCurrentState(STATE.PLAYING, 0);
                    for (LivePlayerObserver observer : getObservers()) {
                        observer.onFirstAudioRendered();
                    }


                } else if (what == NEPlayStatusType.NELP_AUDIO_VIDEO_UN_SYNC) {
                    // [点播专用]
                    ALog.i("on player info: audio video un sync");
                    VodPlayerObserver o;
                    for (LivePlayerObserver observer : getObservers()) {
                        if (observer instanceof VodPlayerObserver) {
                            o = (VodPlayerObserver) observer;
                            o.onAudioVideoUnsync();
                        }
                    }

                } else if (what == NEPlayStatusType.NELP_NET_STATE_BAD) {
                    // [点播专用]
                    ALog.i("on player info: network state bad tip");
                    VodPlayerObserver o;
                    for (LivePlayerObserver observer : getObservers()) {
                        if (observer instanceof VodPlayerObserver) {
                            o = (VodPlayerObserver) observer;
                            o.onNetStateBad();
                        }
                    }
                } else if (what == NEPlayStatusType.NELP_VIDEO_DECODER_OPEN) {
                    ALog.i("on player info: hardware decoder opened");
                    for (LivePlayerObserver observer : getObservers()) {
                        observer.onVideoDecoderOpen(extra);
                    }

                } else {
                    ALog.i("on player info: what=" + what + ", extra=" + extra);
                }
            } catch (Throwable th) {
                th.printStackTrace();
            }
            return false;
        }
    };

    /*
     * 视频缓冲百分比更新
     */
    private NELivePlayer.OnBufferingUpdateListener onBufferingUpdateListener = new NELivePlayer.OnBufferingUpdateListener() {

        @Override
        public void onBufferingUpdate(NELivePlayer neLivePlayer, final int percent) {
            ALog.d("on buffering update, percent=" + percent);
            try {
                for (LivePlayerObserver observer : getObservers()) {
                    observer.onBuffering(percent);
                }
            } catch (Throwable th) {
                th.printStackTrace();
            }
        }
    };

    /*
     * 拉流http状态信息回调
     */
    private NELivePlayer.OnHttpResponseInfoListener onHttpResponseInfoListener = new NELivePlayer.OnHttpResponseInfoListener() {

        @Override
        public void onHttpResponseInfo(final int code, final String header) {
            ALog.i("onHttpResponseInfo，code：" + code + "，header：" + header);
            try {
                for (LivePlayerObserver observer : getObservers()) {
                    observer.onHttpResponseInfo(code, header);
                }
            } catch (Throwable th) {
                th.printStackTrace();
            }
        }
    };

    /*
     * 实时播放位置回调
     */
    private NELivePlayer.OnCurrentPositionListener onCurrentPositionListener = new NELivePlayer.OnCurrentPositionListener() {

        @Override
        public void onCurrentPosition(final long l) {
            if (positionListener == null) {
                return;
            }
            if (lastCallBackPosition == l) {
                return;
            }
            // notify
            uiHandler.post(new Runnable() {

                @Override
                public void run() {
                    try {
                        if (positionListener != null) {
                            positionListener.onCurrentPosition(l);
                        }
                    } catch (Throwable th) {
                        th.printStackTrace();
                    }
                }
            });
            // save time
            lastCallBackPosition = l;
        }
    };

    /*
     * 实时真实时间戳回调
     */
    private NELivePlayer.OnCurrentRealTimeListener onCurrentRealTimeListener = new NELivePlayer.OnCurrentRealTimeListener() {

        @Override
        public void onCurrentRealTime(final long l) {
            if (realTimeListener == null) {
                return;
            }
            if (lastCallbackRealTime == l) {
                return;
            }
            // notify
            uiHandler.post(new Runnable() {

                @Override
                public void run() {
                    try {
                        if (realTimeListener != null) {
                            realTimeListener.onCurrentRealTime(l);
                        }
                    } catch (Throwable th) {
                        th.printStackTrace();
                    }
                }
            });
            // save time
            lastCallbackRealTime = l;
        }
    };

    /*
     * 实时同步时间戳回调
     */
    private NELivePlayer.OnCurrentSyncTimestampListener onCurrentSyncTimeListener = new NELivePlayer.OnCurrentSyncTimestampListener() {

        @Override
        public void onCurrentSyncTimestamp(final long l) {
            if (syncTimeListener == null) {
                return;
            }
            if (lastCallbackSyncTime == l) {
                return;
            }
            // notify
            uiHandler.post(new Runnable() {

                @Override
                public void run() {
                    try {
                        if (syncTimeListener != null) {
                            syncTimeListener.onCurrentSyncTimestamp(l);
                        }
                    } catch (Throwable th) {
                        th.printStackTrace();
                    }
                }
            });
            // save time
            lastCallbackSyncTime = l;
        }
    };

    /*
     * [点播专用]点播跳转到指定事件播放回调
     */
    private NELivePlayer.OnSeekCompleteListener onSeekCompleteListener = new NELivePlayer.OnSeekCompleteListener() {

        @Override
        public void onSeekComplete(NELivePlayer neLivePlayer) {
            ALog.i("on seek completed");
            try {
                VodPlayerObserver o;
                for (LivePlayerObserver observer : getObservers()) {
                    if (observer instanceof VodPlayerObserver) {
                        o = (VodPlayerObserver) observer;
                        o.onSeekCompleted();
                    }
                }
            } catch (Throwable th) {
                th.printStackTrace();
            }
        }
    };

    private NELivePlayer.OnDecryptionListener onDecryptionListener = new NELivePlayer.OnDecryptionListener() {

        @Override
        public void onDecryption(final int ret) {
            ALog.i("on decryption: " + ret);
            // notify
            uiHandler.post(new Runnable() {

                @Override
                public void run() {
                    try {
                        VodPlayerObserver o;
                        for (LivePlayerObserver observer : getObservers()) {
                            if (observer instanceof VodPlayerObserver) {
                                o = (VodPlayerObserver) observer;
                                o.onDecryption(ret);
                            }
                        }
                    } catch (Throwable th) {
                        th.printStackTrace();
                    }
                }
            });
        }
    };

    /*
     * [点播专用] 点播过程中定时回调当前播放进度
     */
    private void onVodTickerTimer() {
        long current = -1;
        long duration = -1;
        long cached = -1;
        synchronized (lock) {
            if (player != null) {
                current = player.getCurrentPosition();
                duration = player.getDuration();
                cached = player.getPlayableDuration();
            }
        }
        // log
        final long c = current;
        final long d = duration;
        final long cc = cached;
        if (timerIndex++ % 10 == 0) {
            // 每10s输出一次log
            ALog.i("on vod ticker timer, progress=" + c + "/" + d + ", cached=" + cc);
        }
        // notify
        if (c >= 0 && d > 0) {
            uiHandler.post(new Runnable() {

                @Override
                public void run() {
                    try {
                        VodPlayerObserver o;
                        for (LivePlayerObserver observer : getObservers()) {
                            if (observer instanceof VodPlayerObserver) {
                                o = (VodPlayerObserver) observer;
                                o.onCurrentPlayProgress(c, d, 100.0f * c / d, cc);
                            }
                        }
                    } catch (Throwable th) {
                        th.printStackTrace();
                    }
                }
            });
        }
    }

    /**
     * ******************************* vod timer ******************************
     */

    private void startVodTimer() {
        stopVodTimer(true);
        timerIndex = 0;
        vodTimer = new Timer("NIM_VOD_TICKER_TIMER");
        vodTimerTask = new TimerTask() {

            @Override
            public void run() {
                onVodTickerTimer();
            }
        };
        vodTimer.scheduleAtFixedRate(vodTimerTask, 1000, 1000);
        ALog.i("start vod timer...");
    }

    private void stopVodTimer(boolean onStart) {
        if (vodTimerTask != null) {
            vodTimerTask.cancel();
            vodTimerTask = null;
        }
        if (vodTimer != null) {
            vodTimer.cancel();
            vodTimer.purge();
            vodTimer = null;
        }
        timerIndex = 0;
        if (!onStart) {
            ALog.i("stop vod timer!");
        }
    }

    /**
     * ******************************* common ******************************
     */

    private List<LivePlayerObserver> getObservers() {
        // 创建副本，为了使得回调到app后，app如果立即注销观察者，会造成List异常。
        List<LivePlayerObserver> copyObservers = new ArrayList<>(observers.size());
        copyObservers.addAll(observers);
        return copyObservers;
    }
}

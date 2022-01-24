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
 * @author CommsEase
 * <p>
 * Player basic extension class that wraps the core process of the player SDK APIs
 * <p>
 * Case 1: Install render view and start the player
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
 * Case 2: Start the player and install render view as required
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

    // constant
    private static final String PLAYER_HANDLER_THREAD_TAG = "LIVE_PLAYER";

    // context
    Context context;

    private Handler uiHandler;

    // input
    private String videoPath;

    private NEMediaDataSource mediaDataSource;

    VideoOptions options;

    private AutoRetryConfig autoRetryConfig;

    // player
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

    private Handler playerHandler; // async player with looper thread

    private final Object lock = new Object(); // The lock to protect the player

    private NELivePlayer.OnSubtitleListener subtitleListener;

    private NEAudioPcmConfig audioPcmConfig;

    private NELivePlayer.OnAudioFrameFilterListener audioFrameFilterListener;

    private int videoFormat;

    private NELivePlayer.OnVideoFrameFilterListener videoFrameFilterListener;


    // status
    AtomicBoolean hasReset = new AtomicBoolean(false); // Check whether the player is reset

    private STATE currentState = STATE.IDLE; // The current state of the player

    private int cause; // The cause to switch the current state. For example, error code for stops and error messages. O is returned for other states

    private long lastCallBackPosition; // Callback for last playback position

    private long lastCallbackRealTime; // Real-time timestamp of the last callback

    private long lastCallbackSyncTime; // The timestamp of the last callback

    private long lastPlayPosition; // last playback position（VOD）

    private int lastAudioTrack = -1;    // Last audio tract

    // video size
    private int videoWidth;

    private int videoHeight;

    private int videoSarNum;

    private int videoSarDen;

    //video mode
    private VideoScaleMode scaleMode = VideoScaleMode.FIT;

    // timer/ticker
    private Timer vodTimer;

    private TimerTask vodTimerTask;

    private int timerIndex = 0;

    private LivePlayer mMasterPlayer;

    // abstract methods for children
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
            return; // Invalid parameters for registration
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
            return; // Invalid parameters for registration
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
            return; // Invalid parameters for registration
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
            return; // Invalid parameters for registration
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
            return; // Invalid parameters for registration
        }
        if (register) {
            subtitleListener = listener;
        } else {
            subtitleListener = null;
        }
        // try bind the subtitle listener
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
     * Async instantiate the player or resume playback:
     * Case 1: Construct NIMLivePlayer and instantiate the player to start playback
     * Case 2: If the source URL is changed, reset the configuration and instantiate the player async
     * Case 3: If the resolution or source URL is changed, reset the configuration and instantiate the player async
     * Case 4: Reset if server is disconnected. initialize the player after network connection resumes
     * Case 5: Resume playback after paused
     */
    @Override
    public void start() {
        synchronized (lock) {
            ALog.i("currentState..." + currentState + "...." + videoPath);
            // case 5: Resume playback after paused
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
            // The player is running. If reinitialization is required, the player needs to be reset
            if (player != null && currentState != STATE.IDLE && !hasReset.get()) {
                ALog.i("reset current player before async init...");
                resetPlayer();
            }
            // Working thread
            if (playerHandler == null) {
                playerHandler = Handlers.sharedInstance().newHandler(PLAYER_HANDLER_THREAD_TAG + hashCode());
            }
            // state
            setCurrentState(STATE.PREPARING, 0);
            // Child initialize logic
            onChildInit();
            // The player is async initialized
            playerHandler.post(new Runnable() {

                @Override
                public void run() {
                    initPlayer();
                }
            });
        }
    }

    private void resetPlayerState() {
        // VOD, resumed to the last playback position
        if (lastPlayPosition > 0) {
            player.seekTo(lastPlayPosition);
            lastPlayPosition = 0; // Resume to the last playback position
        }
        if (lastAudioTrack != -1) {
            player.setSelectedAudioTrack(lastAudioTrack);
            lastAudioTrack = -1;
        }
        ALog.i("player start...");

        // VOD, enable ticker timer
        if (player.getDuration() > 0) {
            startVodTimer();
        }
    }

    /*
     * Resume playback after paused
     */
    private void restart() {
        player.start();
        reSetupRenderView();
        // state
        setCurrentState(STATE.PLAYING, 0);
        final MediaInfo mediaInfo = new MediaInfo(player.getMediaInfo(), player.getDuration());
        // VOD, enable ticker timer
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
        // try bind surface holder. If the player is reset and the player is switched from background to the foreground, the original surfaceView is nullified and must be initialized and bound again
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
        this.renderView.setCallback(surfaceCallback); // Send notification of the surface callback of render view to player
        setVideoSizeToRenderView(); // trigger render view display
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
        setVideoSizeToRenderView(); // Trigger render view display
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
        // Pause playback
        synchronized (lock) {
            if (player != null && player.isPlaying() && player.getDuration() > 0) {
                player.pause();
                stopVodTimer(false); // stop ticker timer
                ALog.i("player paused");
                setCurrentState(STATE.PAUSED, CauseCode.CODE_VIDEO_PAUSED_BY_MANUAL);
            }
        }
    }

    @Override
    public void stop() {
        final long timeStart = System.currentTimeMillis();
        // Work thread exits
        if (playerHandler != null) {
            playerHandler.removeCallbacksAndMessages(null);
            Handlers.sharedInstance().removeHandler(PLAYER_HANDLER_THREAD_TAG + hashCode());
            playerHandler = null;
        }
        // Replease the timer
        stopVodTimer(false);
        // Release the player
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
        // release the child
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
        // Reset the player
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
            //SDK mirror interfaces as software decoder
            if (player != null && options != null && !options.hardwareDecode) {
                player.setMirror(isMirror);
                ALog.i("set mirror,player.setMirror " + isMirror);
            }
            //TextureView is supported by hardware decoder. Use setMirror of TextureView
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
                // try bind surface holder. If the player is reset, the original surfaceView is nullified and must be initialized and bound again.
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
                // try bind surface holder. If the player is reset, the original surfaceView is nullified and must be initialized and bound again.
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
     * Instantiate the player and async prepare
     */
    private void initPlayer() {
        synchronized (lock) {
            // native player
            if (player == null) {
                final long timeStart = System.currentTimeMillis();
                player = NELivePlayer.create(); // consume 40ms-300ms.
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
     * Configure the player
     * Operate the player in lock
     */
    private void configPlayer() {
        if (player == null) {
            return;
        }
        // config
        player.setBufferStrategy(options.bufferStrategy.getValue()); // Set the buffer strategy
        player.setBufferSize(options.bufferSize); // Set buffer size
        player.setHardwareDecoder(options.hardwareDecode); // Set decoding mode
        player.setShouldAutoplay(options.isAutoStart); // Specify whether to enable auto playback
        int timeout = options.playbackTimeout;
        if (timeout <= 0) {
            timeout = 10;
            options.playbackTimeout = 10;
        }
        player.setPlaybackTimeout(timeout); // 10s for reconnection after timeout
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
                player.setDataSource(videoPath); //If data source is disconnected, the onError is triggered after 1 minute. Error code -1002
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
     * Set video frame size for render view
     * Prerequisites:
     * 1. Player#onVideoSizeChanged is triggered and stores the video frame size
     * 2) render view is installed
     * <p>
     * Method call occasion:
     * 1) onVideoSizeChanged is triggered
     * 2) setupRenderView is called
     */
    private void setVideoSizeToRenderView() {
        if (videoWidth != 0 && videoHeight != 0 && renderView != null) {
            renderView.setVideoSize(videoWidth, videoHeight, videoSarNum, videoSarDen, scaleMode);
        }
    }

    /*
     * set up the listener for audio statistics callback
     * <p>
     * Method call occasion:
     * 1. If the player is not instantiated when the listener is registered, store the listener in the cache and register when configPlayer is called
     * 2. If configPlayer is called, the player may be instantiated again and the listener must be registered again.
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
     * Set up the listener for video statistics callback
     * <p>
     * Method call occasion:
     * 1. If the player is not instantiated when the listener is registered, store the listener in the cache and register when configPlayer is called
     * 2. If configPlayer is called, the player may be instantiated again and the listener must be registered again.
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
     * Set up the listener for playback position callback
     * <p>
     * Method call occasion:
     * 1. If the player is not instantiated when the listener is registered, store the listener in the cache and register when configPlayer is called
     * 2. If configPlayer is called, the player may be instantiated again and the listener must be registered again.
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
     * Set up the listener for real-time timestamp callback
     * <p>
     * method call occasion:
     * 1. If the player is not instantiated when the listener is registered, store the listener in the cache and register when configPlayer is called
     * 2. If configPlayer is called, the player may be instantiated again and the listener must be registered again.
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
     * Set up the listener for current sync timestamp callback
     * <p>
     * method call occasion:
     * 1. If the player is not instantiated when the listener is registered, store the listener in the cache and register when configPlayer is called
     * 2. If configPlayer is called, the player may be instantiated again and the listener must be registered again.
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
     * Set up the listener for current sync content callback
     * <p>
     * method call occasion:
     * 1. If the player is not instantiated when the listener is registered, store the listener in the cache and register when configPlayer is called
     * 2. If configPlayer is called, the player may be instantiated again and the listener must be registered again.
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
     * Set up the listener for subtitle callback
     * <p>
     * method call occasion:
     * 1. If the player is not instantiated when the listener is registered, store the listener in the cache and register when configPlayer is called
     * 2. If configPlayer is called, the player may be instantiated again and the listener must be registered again.
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
     * Bind the player and surface view
     * case 1: When surfaceCreated is called, bind the surface view to the player
     * case 2: When surfaceDestroyed is called, unbind the surface view from the player
     * case 3: If the player is reset and initialized, and the surface is not released, rebind the surface to the player
     */
    private synchronized void setDisplaySurface(Surface surface) {
        if (player != null) {
            player.setSurface(surface);
            ALog.i("set player display surface=" + surface);
        }
    }

    /*
     * Reset the player
     * Use cases:
     * case 1: The network is disconnected. Reset the player and resume after the network is reconnected
     * case 2: The source URL is changed. Initialize the player after the new URL is loaded
     * case 3: An error occurred during playback
     * case 4: The player is running in the background for a long time and switched to the foreground
     * case 5: The playback is completed
     */
    void resetPlayer() {
        stopVodTimer(false); // Stop the timer
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
            // The upper layer error codes cannot be duplicate to the player error codes
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
     * preparing timeout task
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
            setDisplaySurface(surface); // Bind the player and surface view
        }

        @Override
        public void onSurfaceDestroyed(Surface surface) {
            ALog.i("on surface destroyed");
            setDisplaySurface(null); // Unbind the player and the surface view
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
     * Get the video dimension or the dimension changes
     * Early callback
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
     * The player is ready for playback
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
     * The playback is completed: The playback of the video source in the VOD system is complete or streaming data in live streaming stops
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
//            // Reset the player after the playback is complete or release the player by calling release
//            resetPlayer();

            stopVodTimer(false); // Stop the timer
            // state
            setCurrentState(STATE.STOPPED, CauseCode.CODE_VIDEO_STOPPED_AS_ON_COMPLETION);
        }
    };

    /*
     * an error occurred while the player is playing
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
     * The video status changes or events occurs
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
                    // [VOD only]
                    ALog.i("on player info: audio video un sync");
                    VodPlayerObserver o;
                    for (LivePlayerObserver observer : getObservers()) {
                        if (observer instanceof VodPlayerObserver) {
                            o = (VodPlayerObserver) observer;
                            o.onAudioVideoUnsync();
                        }
                    }

                } else if (what == NEPlayStatusType.NELP_NET_STATE_BAD) {
                    // [VOD only]
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
     * Update video buffering in percentage
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
     * HTTP response status callback
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
     * Current playback position callback
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
     * Current real time callback
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
     * Real time sync callback
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
     * [VOD only] specified events callback
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
     * [VOD only] Current playback position callback at a specified rate
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
            // output log every 10 seconds
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
        // Create a duplicate of observer because the list may cause exceptions if the app immediately unregisters the observer.
        List<LivePlayerObserver> copyObservers = new ArrayList<>(observers.size());
        copyObservers.addAll(observers);
        return copyObservers;
    }
}

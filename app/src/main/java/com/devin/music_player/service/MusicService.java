package com.devin.music_player.service;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * <p></p>
 *
 * @author <a href="https://github.com/wzh-devin">devin</a>
 * @version 1.0
 * @date 2024/9/22 23:58
 * @since 1.0
 */
public class MusicService extends Service {
    private static final String TAG = "MusicService";
    private ExoPlayer player;
    private final List<MediaItem> mediaItems = new ArrayList<>();
    private int currentTrackIndex = 0;

    /**
     * 初始化数据
     */
    public void init() {
        if (player == null) {
            // 创建音乐播放器
            player = new ExoPlayer.Builder(this).build();
            // 加载音乐
            try {
                String[] fileNames = getAssets().list("music");
                if (fileNames != null) {
                    for (String fileName : fileNames) {
                        // 构建音乐文件的URI
                        Uri uri = Uri.parse("asset:///music/" + fileName);
                        mediaItems.add(MediaItem.fromUri(uri));
                    }
                }
            } catch (IOException e) {
                Log.e(TAG, "Error loading music files from assets.", e);
            }

            // 添加音乐
            player.addMediaItems(mediaItems);
            // 顺序播放
            player.setRepeatMode(ExoPlayer.REPEAT_MODE_ALL);
            // 播放
            player.prepare();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new LocalBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        init();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Optional.ofNullable(player).ifPresent(p -> {
            p.stop();
            p.release();
            player = null;
        });
    }

    /**
     * 获取当前音乐的名字
     *
     * @return 当前音乐的名字
     */
    public String getCurrentSongName() {
        if (currentTrackIndex >= 0 && currentTrackIndex < mediaItems.size()) {
            MediaItem mediaItem = mediaItems.get(currentTrackIndex);
            return getFileNameFromMediaItem(mediaItem);
        }
        return "";
    }

    /**
     * 获取文件名
     *
     * @param mediaItem 媒体项
     * @return 文件名
     */
    private String getFileNameFromMediaItem(MediaItem mediaItem) {
        return Optional.ofNullable(mediaItem.playbackProperties)
                .map(properties -> properties.uri)
                .map(Uri::getLastPathSegment)
                .orElse("");
    }

    /**
     * 获取本地服务
     */
    public class LocalBinder extends Binder {
        public MusicService getService() {
            Log.i("LocalBinder", "Returning MusicService instance.");
            return MusicService.this;
        }
    }

    /**
     * 判断是否在播放中
     *
     * @return 是否在播放中
     */
    public boolean isPlaying() {
        return player != null && player.isPlaying();
    }

    /**
     * 暂停播放
     */
    public void pause() {
        Optional.ofNullable(player).ifPresent(Player::pause);
    }

    /**
     * 开始播放
     */
    public void play() {
//        Optional.ofNullable(player).ifPresent(Player::play);
        if (player != null) {
            player.play();
        }
    }

    /**
     * 获取当前的播放进度
     *
     * @return 当前播放进度
     */
    public long getContentPosition() {
        return Optional.ofNullable(player).map(Player::getContentPosition).orElse(0L);
    }

    /**
     * 获取歌曲总时长
     *
     * @return 歌曲总时长
     */
    public long getDuration() {
        return Optional.ofNullable(player).map(Player::getDuration).orElse(0L);
    }

    /**
     * 播放歌曲
     *
     * @param name 歌曲名称
     */
    public void playMusic(String name) {
        Optional.ofNullable(player).ifPresent(p -> {
            if (!mediaItems.isEmpty()) {
                for (int i = 0; i < mediaItems.size(); i++) {
                    MediaItem mediaItem = mediaItems.get(i);
                    String fileName = getFileNameFromMediaItem(mediaItem);
                    if (fileName != null && fileName.equals(name)) {
                        try {
                            p.seekTo(i, 0);
                            p.prepare();
                            p.play();
                            Log.i("MediaPlayer", "Playing music: " + name);
                            break;
                        } catch (Exception e) {
                            Log.e("MediaPlayerError", "Error seeking to position or playing: " + e.getMessage(), e);
                        }
                    }
                }
            } else {
                Log.e("MediaPlayerError", "mediaItems is null or empty.");
            }
        });
    }
}

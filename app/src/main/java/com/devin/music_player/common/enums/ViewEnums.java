package com.devin.music_player.common.enums;

import com.devin.music_player.R;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * <p></p>
 *
 * @author <a href="https://github.com/wzh-devin">devin</a>
 * @version 1.0
 * @date 2024/9/22 21:44
 * @since 1.0
 */
//@AllArgsConstructor
//@Getter
public enum ViewEnums {

    TV_SONG_NAME("tv_songName", "歌曲名", 0, R.id.tv_songName),
    TV_SINGER("tv_singer", "作者名", 0, R.id.tv_singer),
    TV_SEEK_BAR_HINT("tv_seekBarHint", "进度时间", 0, R.id.tv_seekBarHint),
    TV_DURATION("tv_duration", "终止时间", 0, R.id.tv_duration),
    BTN_PLAY("btn_play", "播放", 1, R.id.btn_play),
    BTN_MUSIC_TYPE("btn_musicType", "音乐类型", 1, R.id.btn_musicType),
    BTN_DOWNLOAD("btn_download", "下载", 1, R.id.btn_download),
    BTN_PLAYLIST("btn_playList", "播放列表", 1, R.id.btn_playList),
    BTN_PLAY_WAY("btn_playWay", "播放方式", 1, R.id.btn_playWay),
    BTN_NEXT("btn_next", "下一首", 1, R.id.btn_next),
    BTN_PRE("btn_pre", "上一首", 1, R.id.btn_pre),
    BTN_RECORDING("btn_recording", "竞唱", 1, R.id.btn_recording),
    BTN_TALKING("btn_talking", "评论", 1, R.id.btn_talking);

    private final String viewName;
    private final String desc;
    private final Integer viewType;
    private final Integer viewId;

    ViewEnums(String viewName, String desc, Integer viewType, Integer viewId) {
        this.viewName = viewName;
        this.desc = desc;
        this.viewType = viewType;
        this.viewId = viewId;
    }

    public String getViewName() {
        return viewName;
    }

    public String getDesc() {
        return desc;
    }

    public Integer getViewType() {
        return viewType;
    }

    public Integer getViewId() {
        return viewId;
    }
}
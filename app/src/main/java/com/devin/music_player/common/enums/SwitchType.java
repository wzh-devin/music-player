package com.devin.music_player.common.enums;

import android.app.Service;

import com.devin.music_player.service.MusicService;

/**
 * <p>
 *     切换音乐的类型
 * </p>
 *
 * @author <a href="https://github.com/wzh-devin">devin</a>
 * @version 1.0
 * @date 2024/9/24 15:33
 * @since 1.0
 */
public enum SwitchType {
    /**
     * 上一首
     */
    PRE {
        @Override
        public void switchMusic(Service service) {
            if (service instanceof MusicService) {
                ((MusicService) service).playPreMusic();
            }
        }
    },

    /**
     * 下一首
     */
    NEXT {
        @Override
        public void switchMusic(Service service) {
            if (service instanceof MusicService) {
                ((MusicService) service).playNextMusic();
            }
        }
    },
    ;

    public abstract void switchMusic(Service service);
}

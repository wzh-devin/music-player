package com.devin.music_player.common.enums;

import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * <p>
 *     视图类型枚举
 * </p>
 *
 * @author <a href="https://github.com/wzh-devin">devin</a>
 * @version 1.0
 * @date 2024/9/22 23:26
 * @since 1.0
 */
public enum ViewTypeEnums {

    TEXT_VIEW(0, TextView.class),
    IMAGE_BUTTON(1, ImageButton.class),
    SEEK_BAR(2, SeekBar.class),
    ;

    private final Integer code;
    private final Class<? extends View> viewTypeClass;

    public Integer getCode() {
        return code;
    }

    public Class<? extends View> getViewTypeClass() {
        return viewTypeClass;
    }

    ViewTypeEnums(Integer code, Class<? extends View> viewTypeClass) {
        this.code = code;
        this.viewTypeClass = viewTypeClass;
    }
}

package com.devin.music_player.common.utils;

import static com.devin.music_player.common.enums.ViewTypeEnums.IMAGE_BUTTON;
import static com.devin.music_player.common.enums.ViewTypeEnums.TEXT_VIEW;

import android.annotation.SuppressLint;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.devin.music_player.common.enums.ViewEnums;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * <p></p>
 *
 * @author <a href="https://github.com/wzh-devin">devin</a>
 * @version 1.0
 * @date 2024/9/24 17:17
 * @since 1.0
 */
public class ViewSelectUtil {

    public static final Map<String, TextView> textViewMap = new HashMap<>();
    public static final Map<String, ImageButton> imageButtonMap = new HashMap<>();

    public static <T> void setOptionalValue(ViewEnums view, T type) {
        View vi = null;
        if (Objects.equals(view.getViewType(), TEXT_VIEW.getCode())) {
            vi = textViewMap.get(view.getViewName());
        } else if (Objects.equals(view.getViewType(), IMAGE_BUTTON.getCode())) {
            vi = imageButtonMap.get(view.getViewName());
        }
        Optional.ofNullable(vi).ifPresent(v -> {
            if (v instanceof TextView) {
                if (type instanceof Long) {
                    ((TextView) v).setText(format((Long) type));
                } else if (type instanceof String) {
                    ((TextView) v).setText((String) type);
                }
            } else {
                ((ImageButton) v).setImageResource((Integer) type);
            }
        });
    }

    private static String format(long param) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("mm:ss"); // "分:秒"格式
        return sdf.format(param);
    }

    /**
     * 设置字体样式
     */
    public static void setFontStyle(AssetManager assets) {
        if (Objects.isNull(assets) || textViewMap.isEmpty()) {
            return;
        }
        Typeface tf = Typeface.createFromAsset(assets, "fonts/YeZi.ttf");
        // 加载字体
        textViewMap.forEach((viewName, view) -> {
            view.setTypeface(tf);
        });
    }
}

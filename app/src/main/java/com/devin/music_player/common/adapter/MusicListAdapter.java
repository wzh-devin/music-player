package com.devin.music_player.common.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import java.util.List;

/**
 * <p>
 *     设置音乐列表的适配器
 * </p>
 *
 * @author <a href="https://github.com/wzh-devin">devin</a>
 * @version 1.0
 * @date 2024/9/24 22:23
 * @since 1.0
 */
@Deprecated // 暂时废弃
public class MusicListAdapter extends ArrayAdapter<String> {
    private Context context;
    private int resource;

    public MusicListAdapter(@NonNull Context context, int resource, List<String> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
    }


    @SuppressLint("ResourceType")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        @SuppressLint("ViewHolder")
        View view = inflater.inflate(resource, parent, false);

        // 加载字体
        Typeface tf = Typeface.createFromAsset(context.getAssets(), "fonts/YeZi.ttf");
        TextView textView = view.findViewById(resource);
        textView.setTypeface(tf);

        return super.getView(position, convertView, parent);
    }
}

package com.devin.music_player;

import static com.devin.music_player.common.enums.ViewEnums.TV_SONG_NAME;
import static com.devin.music_player.common.enums.ViewTypeEnums.IMAGE_BUTTON;
import static com.devin.music_player.common.enums.ViewTypeEnums.TEXT_VIEW;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.devin.music_player.common.enums.ViewEnums;
import com.devin.music_player.service.MusicService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class MainActivity extends AppCompatActivity {

    private ActionBar actionBar;
    private SeekBar seekBar;
    private static final Map<String, TextView> textViewMap = new HashMap<>();
    private static final Map<String, ImageButton> imageButtonMap = new HashMap<>();
    private ListView listView;
    private ArrayAdapter<String> adapter;

    // 数据初始化
    @SuppressLint("ClickableViewAccessibility")
    public void initView() {
        for (ViewEnums view : ViewEnums.values()) {
            if (view.getViewType().equals(TEXT_VIEW.getCode())) {
                textViewMap.put(view.getViewName(), (TextView) findViewById(getResources()
                        .getIdentifier(view.getViewName(), "id", getPackageName())));
            } else if (view.getViewType().equals(IMAGE_BUTTON.getCode())) {
                imageButtonMap.put(view.getViewName(), (ImageButton) findViewById(getResources()
                        .getIdentifier(view.getViewName(), "id", getPackageName())));
            }
        }
        seekBar = (SeekBar) findViewById(R.id.seekBar);

        // 设置监听器
        imageButtonMap.forEach((viewName, imageButton) -> {
            Optional.ofNullable(imageButtonMap.get(viewName))
                    .ifPresent(v -> v.setOnClickListener(listener));
        });
    }

    // 设置监听效果
    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(MainActivity.this, MusicService.class);
            Log.i("viewId>>>>>>>>>>>>>>>>>", "" + view.getId() );
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 设置全屏
//        getWindow().setFlags(
//                WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN
//        );
        initView();
    }


}
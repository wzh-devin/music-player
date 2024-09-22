package com.devin.music_player;

import android.annotation.SuppressLint;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Map;

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
        textViewMap.put("tvSongName", (TextView) findViewById(R.id.tv_songName));
        textViewMap.put("tvSinger", (TextView) findViewById(R.id.tv_singer));

    }
}
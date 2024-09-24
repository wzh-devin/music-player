package com.devin.music_player;

import static com.devin.music_player.common.enums.ViewEnums.BTN_PLAY;
import static com.devin.music_player.common.enums.ViewEnums.TV_DURATION;
import static com.devin.music_player.common.enums.ViewEnums.TV_SEEK_BAR_HINT;
import static com.devin.music_player.common.enums.ViewEnums.TV_SONG_NAME;
import static com.devin.music_player.common.enums.ViewTypeEnums.IMAGE_BUTTON;
import static com.devin.music_player.common.enums.ViewTypeEnums.TEXT_VIEW;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.devin.music_player.common.enums.ViewEnums;
import com.devin.music_player.service.MusicService;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private ActionBar actionBar;
    private SeekBar seekBar;
    private static final Map<String, TextView> textViewMap = new HashMap<>();
    private static final Map<String, ImageButton> imageButtonMap = new HashMap<>();
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private List<String> musicList = new ArrayList<>();
    private MusicService musicService;
    private boolean isServiceBound = false;
    private Timer timer;
    private Handler mainHandler = new Handler(Looper.getMainLooper());

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

//         设置 ActionBar
        actionBar = getSupportActionBar();
        actionBar.setTitle("MusicPlayer");
        actionBar.setDisplayHomeAsUpEnabled(true);

        // 绑定MusicService
        Intent intent = new Intent(MainActivity.this, MusicService.class);
        boolean isBind = bindService(intent, serviceConnection, BIND_AUTO_CREATE);
        Log.i("isBind::::::::::>>", Boolean.toString(isBind));
    }

    // 数据初始化
    @SuppressLint("ClickableViewAccessibility")
    public void initView() {
        for (ViewEnums view : ViewEnums.values()) {
            if (view.getViewType().equals(TEXT_VIEW.getCode())) {
                textViewMap.put(view.getViewName(), (TextView) findViewById(getResources().getIdentifier(view.getViewName(), "id", getPackageName())));
            } else if (view.getViewType().equals(IMAGE_BUTTON.getCode())) {
                imageButtonMap.put(view.getViewName(), (ImageButton) findViewById(getResources().getIdentifier(view.getViewName(), "id", getPackageName())));
            }
        }
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        listView = (ListView) findViewById(R.id.lv_music);

        // 设置监听器
        imageButtonMap.forEach((viewName, imageButton) -> {
            Optional.ofNullable(imageButtonMap.get(viewName)).ifPresent(v -> v.setOnClickListener(listener));
        });
    }

    // 设置监听效果
    private final View.OnClickListener listener = new View.OnClickListener() {
        @SuppressLint({"ShowToast", "NonConstantResourceId"})
        @Override
        public void onClick(View view) {
//            Intent intent = new Intent(MainActivity.this, MusicService.class);
            Log.i("viewId>>>>>>>>>>>>>>>>>", "" + view.getId());
            if (view.getId() == ViewEnums.BTN_PLAYLIST.getViewId()) {
                showMusicList();
            } else if (view.getId() == BTN_PLAY.getViewId()) {
                playOrPauseMusic();
            } else {
                Toast.makeText(MainActivity.this, "未实现", Toast.LENGTH_SHORT);
            }
        }
    };

    private final AdapterView.OnItemClickListener musicListListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
            Log.i("onItemClick============>>>>", adapterView.toString() + "---->view: " + view.toString() + "---->i: " + position + "---->l: " + l);

            ListView lv = (ListView) adapterView;
            lv.setSelector(R.color.blue);
            String musicName = adapterView.getItemAtPosition(position).toString();

            if ("返回".equals(musicName)) {
                // 获取ListView布局参数
                listView.setVisibility(View.GONE);
            } else {
                // TODO 播放音乐
                playMusic(musicName);
            }
        }
    };

    // 绑定服务
    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
//            musicService = ((MusicService.LocalBinder) iBinder).getService();
            MusicService.LocalBinder binder = (MusicService.LocalBinder) service;
            musicService = binder.getService();
            isServiceBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            musicService = null;
            isServiceBound = false;
        }
    };

    /**
     * 展示音乐列表
     */
    private void showMusicList() {
        // 获取播放列表
        musicList = getMusic();
        // 创建适配器
        adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_single_choice, musicList);
        // 设置适配器
        listView.setAdapter(adapter);
        // 设置选择模式
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        // 设置监听器
        listView.setOnItemClickListener(musicListListener);
        // 设置可见
        listView.setVisibility(View.VISIBLE);
    }

    /**
     * 获取音乐列表
     *
     * @return
     */
    private List<String> getMusic() {
        List<String> mList = new ArrayList<>();
        try {
            String[] fName = getAssets().list("music");
            if (fName != null) {
                mList.addAll(Arrays.asList(fName));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        mList.add("返回");
        return mList;
    }

    private void playMusic(String musicName) {
        updateSongName(musicName);
        if (isServiceBound && musicService != null) {
            timer = new Timer();
            timer.schedule(new ProgressUpdate(), 0, 1000);
            musicService.playMusic(musicName);
            setOptionalValue(BTN_PLAY, R.drawable.stop);
        }
    }

    /**
     * 播放或暂停音乐
     */
    private void playOrPauseMusic() {
        if (musicService.isPlaying()) {
            // 暂停音乐
            Optional.ofNullable(imageButtonMap.get(BTN_PLAY.getViewName())).ifPresent(v -> {
                v.setImageResource(R.drawable.play);
                musicService.pause();
            });
        } else {
            // 创建Timer对象，监听音乐播放进度
            Timer timer = new Timer();
            timer.schedule(new ProgressUpdate(), 0, 1000);

            // 播放音乐
//            Optional.ofNullable(imageButtonMap.get(BTN_PLAY.getViewName()))
//                    .ifPresent(v -> {
//                        Log.i("imageButton:::::::::::", v + "");
//                        v.setImageResource(R.drawable.stop);
//                        musicService.play();
//                        // 更新歌曲名
//                        String songName = musicService.getCurrentSongName();
//                        updateSongName(songName);
//                    });
            ImageButton play = imageButtonMap.get(BTN_PLAY.getViewName());
            if (play != null) {
                play.setImageResource(R.drawable.stop);
                musicService.play();
                String songName = musicService.getCurrentSongName();
                updateSongName(songName);
            }
        }
    }

    /**
     * 更新歌曲名
     *
     * @param songName
     */
    private void updateSongName(String songName) {
        // 去掉文件名后缀
        songName = songName.substring(0, songName.lastIndexOf("."));
        setOptionalValue(TV_SONG_NAME, songName);
    }

    // 更新音乐播放进度
    private class ProgressUpdate extends TimerTask {

        @Override
        public void run() {
            mainHandler.post(() -> {
                long position = musicService.getContentPosition();
                long duration = musicService.getDuration();

                // 更新文本进度
                setOptionalValue(TV_SEEK_BAR_HINT, position);
                // 更新文本总时长进度
                setOptionalValue(TV_DURATION, duration);

                // 更新进度条
                seekBar.setMax((int) duration);
                seekBar.setProgress((int) position);
            });
        }
    }

    /**
     * 设置文本内容
     *
     * @param view
     * @param type
     * @param <T>
     */
    private <T> void setOptionalValue(ViewEnums view, T type) {
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

    /**
     * 格式化
     *
     * @param param
     * @return
     */
    private String format(long param) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("mm:ss"); // "分:秒"格式
        return sdf.format(param);
    }

}
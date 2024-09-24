package com.devin.music_player;

import static com.devin.music_player.common.enums.ViewEnums.BTN_PLAY;
import static com.devin.music_player.common.enums.ViewEnums.BTN_PLAY_WAY;
import static com.devin.music_player.common.enums.ViewEnums.TV_DURATION;
import static com.devin.music_player.common.enums.ViewEnums.TV_SEEK_BAR_HINT;
import static com.devin.music_player.common.enums.ViewEnums.TV_SONG_NAME;
import static com.devin.music_player.common.enums.ViewTypeEnums.IMAGE_BUTTON;
import static com.devin.music_player.common.enums.ViewTypeEnums.TEXT_VIEW;
import static com.devin.music_player.common.utils.ViewSelectUtil.imageButtonMap;
import static com.devin.music_player.common.utils.ViewSelectUtil.textViewMap;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.devin.music_player.common.enums.SwitchType;
import com.devin.music_player.common.enums.ViewEnums;
import com.devin.music_player.common.utils.ViewSelectUtil;
import com.devin.music_player.service.MusicService;
import com.google.android.exoplayer2.Player;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private ActionBar actionBar;
    private SeekBar seekBar;
    private ConstraintLayout layout;
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
        layout = (ConstraintLayout) findViewById(R.id.main);

        // 设置监听器
        imageButtonMap.forEach((viewName, imageButton) -> {
            Optional.ofNullable(imageButtonMap.get(viewName)).ifPresent(v -> v.setOnClickListener(listener));
        });

        // 为进度条添加改变监听
        seekBar.setOnSeekBarChangeListener(seekChangedBarListener);

        // 设置触摸监听
        layout.setOnTouchListener(layoutOnTouchListener);
    }

    // 设置按钮点击的监听效果
    private final View.OnClickListener listener = new View.OnClickListener() {
        @SuppressLint({"ShowToast", "NonConstantResourceId"})
        @Override
        public void onClick(View view) {
//            Intent intent = new Intent(MainActivity.this, MusicService.class);
            Log.i("viewId>>>>>>>>>>>>>>>>>", "" + view.getId());
            switch (Objects.requireNonNull(ViewEnums.fromViewId(view.getId()))) {
                case BTN_PLAYLIST -> showMusicList();
                case BTN_PLAY -> playOrPauseMusic();
                case BTN_NEXT -> playNextMusic();
                case BTN_PRE -> playPreMusic();
                case BTN_PLAY_WAY -> changePlayMode();
                default -> {
                    Toast.makeText(MainActivity.this, "暂未实现", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

    // 列表音乐点击监听
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
                playMusic(musicName);
            }
        }
    };

    //  进度条改变事件监听
    private final SeekBar.OnSeekBarChangeListener seekChangedBarListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            Log.i("onProgressChanged=================>", "seekBar: " + seekBar +
                    "\n====>progress: " + progress +
                    "\n====>fromUser: " + fromUser);
            if (fromUser) {
                timer = new Timer();
                timer.schedule(new ProgressUpdate(), 0, 1000);
                ViewSelectUtil.setOptionalValue(TV_SEEK_BAR_HINT, progress);
                musicService.seekTo(progress);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            Log.i("onStartTrackingTouch=================>", "seekBar: " + seekBar);
            ViewSelectUtil.setOptionalValue(BTN_PLAY, R.drawable.play);
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            Log.i("onStopTrackingTouch=================>", "seekBar: " + seekBar);
            ViewSelectUtil.setOptionalValue(BTN_PLAY, R.drawable.stop);
            updateSongName(musicService.getCurrentSongName());
            musicService.play();
        }
    };

    // 触摸监听
    private final View.OnTouchListener layoutOnTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent event) {
            // 获取 ListView 的位置信息
            int[] listViewLocation = new int[2];
            listView.getLocationOnScreen(listViewLocation);

            // 判断触摸事件的坐标是否在 ListView 区域内
            if (event.getRawX() < listViewLocation[0] ||
                    event.getRawX() > listViewLocation[0] + listView.getWidth() ||
                    event.getRawY() < listViewLocation[1] ||
                    event.getRawY() > listViewLocation[1] + listView.getHeight()) {
                // 如果触摸事件不在 ListView 区域内，则隐藏 ListView
                listView.setVisibility(View.GONE);
                return true; // 表示触摸事件已经被处理
            }
            return false; // 表示触摸事件未被处理
        }
    };

    // 绑定服务
    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            musicService = ((MusicService.LocalBinder) service).getService();
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
            ViewSelectUtil.setOptionalValue(BTN_PLAY, R.drawable.stop);
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
            Optional.ofNullable(imageButtonMap.get(BTN_PLAY.getViewName()))
                    .ifPresent(v -> {
                        Log.i("imageButton:::::::::::", v + "");
                        v.setImageResource(R.drawable.stop);
                        musicService.play();
                        // 更新歌曲名
                        String songName = musicService.getCurrentSongName();
                        updateSongName(songName);
                    });
        }
    }

    /**
     * 播放下一首
     */
    public void playNextMusic() {
        switchMusic(SwitchType.NEXT);
    }

    /**
     * 播放上一首
     */
    public void playPreMusic() {
        switchMusic(SwitchType.PRE);
    }

    /**
     * 切换音乐
     */
    private void switchMusic(SwitchType type) {
        if (isServiceBound && musicService != null) {
            timer = new Timer();
            timer.schedule(new ProgressUpdate(), 0, 1000);
            ViewSelectUtil.setOptionalValue(BTN_PLAY, R.drawable.play);
            type.switchMusic(musicService);
            ViewSelectUtil.setOptionalValue(BTN_PLAY, R.drawable.stop);
            updateSongName(musicService.getCurrentSongName());
        }
    }

    /**
     * 切换播放方式
     */
    private void changePlayMode() {
        if (!Objects.isNull(musicService)) {
            Integer currentMode = musicService.getPlayMode();
            switch(currentMode) {
                case Player.REPEAT_MODE_ALL -> {
                    // 切换到单曲循环
                    musicService.setPlayMode(Player.REPEAT_MODE_ONE);
                    ViewSelectUtil.setOptionalValue(BTN_PLAY_WAY, R.drawable.order);
                }
                case Player.REPEAT_MODE_ONE -> {
                    // 切换到顺序播放
                    musicService.setPlayMode(Player.REPEAT_MODE_ALL);
                    ViewSelectUtil.setOptionalValue(BTN_PLAY_WAY, R.drawable.random);
                }
                default -> {
                }
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
        ViewSelectUtil.setOptionalValue(TV_SONG_NAME, songName);
    }

    // 更新音乐播放进度
    private class ProgressUpdate extends TimerTask {

        @Override
        public void run() {
            mainHandler.post(() -> {
                long position = musicService.getContentPosition();
                long duration = musicService.getDuration();

                // 更新文本进度
                ViewSelectUtil.setOptionalValue(TV_SEEK_BAR_HINT, position);
                // 更新文本总时长进度
                ViewSelectUtil.setOptionalValue(TV_DURATION, duration);

                // 更新进度条
                seekBar.setMax((int) duration);
                seekBar.setProgress((int) position);
            });
        }
    }

    /**
     * 返回home桌面
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 销毁对象
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(serviceConnection);
        musicService.onDestroy();
    }
}
package com.example.musicdata;

import android.Manifest;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class MainActivity extends AppCompatActivity {


    private static final int REQUEST_CODE = 0; // 请求码

    // 所需的全部权限
    static final String[] PERMISSIONS = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE //访问存储权限
    };
    private PermissionsChecker mPermissionsChecker; // 权限检测器




    private ListView MyListView;
    private List<Song> list;
    private MyAdapter adapter;
    private MediaPlayer mediaPlayer; //声名
    private boolean ispause = false;
    private int PLAY_MODE=1;  //1为顺序播放，2为随机播放，3为单曲循环，默认顺序

    private Vector<Integer> V = new Vector<Integer>();
    private int current_position_in_vector=-1; //当前播放的曲目在vector中的位置,播放上一首时需要，下标从0开始,小于或等于vector.size()

    Button btn_last;//上一首
    ImageButton btn_pause; //暂停
    Button btn_next; //下一首
    SeekBar seekBar;
    TextView textView;
    TextView current;
    TextView total;
    ImageView settings;

    Handler handler;
    final int UPDATE=0x101; //消息代码


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);


        mPermissionsChecker = new PermissionsChecker(this);



        btn_last = (Button)findViewById(R.id.btn_last);
        btn_pause = (ImageButton)findViewById(R.id.btn_stop);
        btn_next = (Button)findViewById(R.id.btn_next);
        seekBar = (SeekBar)findViewById(R.id.sb) ;
        textView = (TextView)findViewById(R.id.SrollTextView) ;
        current = (TextView)findViewById(R.id.current_progress);
        total = (TextView)findViewById(R.id.total_progress);
        settings = (ImageView) findViewById(R.id.settings);

        mediaPlayer=new MediaPlayer(); //无参构造方法

        //初始化列表项
        init_ListView();

        //监听方法初始化
        init_ClickListeners();



        //---------------------------------------开辟多线程用于处理进度条----------------------------------------------------------------
          //实例化handler对象
        handler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                //更新UI
                if(msg.what==UPDATE)
                {
                    seekBar.setProgress(msg.arg1);//设置进度条进度
                    //System.out.println(msg.arg1);
                    String current_time = MusicUtils.formatTime(msg.arg2);
                    current.setText(current_time);
                }
            }
        };

        Runnable x = new MyRunnable();
        new Thread(x).start();
        //-------------------------------------------------------------------------------------------------------------------------------
    }

    //---------------------------------------开辟多线程用于处理进度条----------------------------------------------------------------


    class MyRunnable implements Runnable{

        //如果音乐正在播放，每隔1秒发送一次消息,如果没有播放或没有音乐对象mediaPlayer则不发送消息
        @Override
        public void run()
        {
            int position,musicMax,seekBarMax;

            while(!Thread.currentThread().isInterrupted()) //循环
            {
                if(mediaPlayer.isPlaying() && mediaPlayer!=null)
                {
                    position=mediaPlayer.getCurrentPosition(); //当前位置（秒）
                    musicMax=mediaPlayer.getDuration(); //歌曲总时长
                    seekBarMax=seekBar.getMax(); //拖动条总长度

                    Message msg = handler.obtainMessage();//获取一个message

                    msg.arg1 = position * seekBarMax / musicMax;
                    msg.arg2 = position;
                    msg.what = UPDATE;
                    //seekBar.setProgress(msg.arg1);
                    handler.sendMessage(msg);//发送消息

                    //休眠1秒
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

        }
    }


    //-------------------------------------------------------------------------------------------------------------------------------

    /**
     * 初始化ListView
     */
    private void init_ListView() {
        MyListView = (ListView) findViewById(R.id.main_listview);
        list = new ArrayList<>();
        //把扫描到的音乐赋值给list
        list = MusicUtils.getMusicData(this);
        adapter = new MyAdapter(this,list);
        MyListView.setAdapter(adapter);
    }

    private void init_ClickListeners(){

        //列表元素监听方法
        MyListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {//匿名内部类
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l)
            {
                if(ispause)//暂停状态则要改变图标
                {
                    ispause=false;//改成播放状态
//                    ((ImageButton)view).setImageDrawable(getResources().getDrawable(R.drawable.play));//更换图标
                    btn_pause.setBackgroundResource(R.drawable.pause);
                    //((ImageButton)view).setBackgroundResource(R.drawable.pause);
                }
                play(position);
            }
        });

        //快退键监听方法
        btn_last.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    play(-1);//-1表示点击按钮 上一首 所执行的功能
            }
        });

        //暂停键的监听方法
        btn_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if(mediaPlayer.isPlaying() && ispause==false)//正在播放，则暂停
                {
                    mediaPlayer.pause();
                    ispause=true;//暂停状态
                    ((ImageButton)view).setBackgroundResource(R.drawable.play);
                }
                else
                {
                    mediaPlayer.start();
                    ispause=false;
                    ((ImageButton)view).setBackgroundResource(R.drawable.pause);
                }
            }
        });

        //快进键监听方法
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ispause)//暂停状态则要改变图标
                {
                    ispause=false;//改成播放状态
                    btn_pause.setBackgroundResource(R.drawable.pause);
                }

                play(-2);//position为-2表示是 系统决定下一首播放的曲目
            }
        });

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent =new Intent(MainActivity.this,SecondActivity.class);
                intent.putExtra("MODE",PLAY_MODE);
                startActivityForResult(intent,1);   //传递数据
            }
        });

        //重写进度条方法
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                if (b) {
                    int curt_time = (int)(progress/(double)seekBar.getMax()*mediaPlayer.getDuration());
                    String current_time = MusicUtils.formatTime(curt_time);
                    current.setText(current_time);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo((int)((double)(seekBar.getProgress()/(double)seekBar.getMax())* mediaPlayer.getDuration()));//以毫秒为单位

            }
        });

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                play(-2);
            }
        });




    }


    /**
     * 播放音频的方法
     */
    private void play(int position) {
        if(position==-1)//点击上一首
        {
            if(current_position_in_vector<=0)
            {
                Toast.makeText(MainActivity.this,"没有上一首啦",Toast.LENGTH_SHORT).show();
            }
            else
            {
                current_position_in_vector--;
                position = V.get(current_position_in_vector);
            }
        }
        else if(position==-2)//点击下一首
        {
            //分两种情况
            if(current_position_in_vector==V.size()-1) //current_position_in_vector在vector末尾
            {
                switch (PLAY_MODE) {
                    case 1:
                        int x = V.get(current_position_in_vector); //获得上一首播放的曲目
                        position = (x + 1) < list.size() ? x + 1 : 0;//顺序播放

                        V.add(position); //把曲目加入V
                        current_position_in_vector++;
                        break;
                    case 2:
                        position = position+(int) (Math.random() * 10000);//随机播放

                        position = position > (list.size()-1)? position%(list.size()) : position;

                        //position = list.size()-1;
                        V.add(position); //把曲目加入V
                        current_position_in_vector++;
                        break;
                    case 3:
                        x = V.get(current_position_in_vector); //获得上一首播放的曲目
                        position = x; //单曲循环,不加入vector
                        break;
                }
            }
            else //current_position_in_vector在vector中间
            {
                current_position_in_vector++;
                position = V.get(current_position_in_vector);

            }
        }
        else//点击列表
        {
            //分两种情况
            if(current_position_in_vector < V.size()-1) //current_position_in_vector位于vector中间，截断处理
            {
                V.setSize(current_position_in_vector+1); //截断

                V.add(position); //加入vector
                current_position_in_vector++;
            }
            else {
                V.add(position);
                current_position_in_vector++;
            }
        }
        if(position>=0)
        {
            mediaPlayer.reset();//重置播放器
            //播放之前要先把音频文件重置
            try
            {
                mediaPlayer.reset();
                //调用方法传进去要播放的音频路径
                String path = list.get(position).path;
                mediaPlayer.setDataSource(path);
                //异步准备音频资源
                mediaPlayer.prepareAsync();
                //调用mediaPlayer的监听方法，音频准备完毕会响应此方法
                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mediaPlayer) {
                        mediaPlayer.start();//开始音频
                    }
                });

            }
            catch (java.io.IOException e)
            {
                e.printStackTrace();
            }

            Song s=list.get(position);
            Toast.makeText(MainActivity.this,s.song,Toast.LENGTH_SHORT).show();
            textView.setText("           Music:  " +list.get(position).song+"       "+"                Artist:  "+list.get(position).singer);//设置当前歌曲信息
            String total_time = MusicUtils.formatTime(list.get(position).duration);
            total.setText(total_time);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // 拒绝时, 关闭页面, 缺少主要权限, 无法运行
        if (requestCode == REQUEST_CODE && resultCode == PermissionsActivity.PERMISSIONS_DENIED) {
            finish();
        }
        else {
            switch (requestCode){
                case 1:
                    if(resultCode==2)
                    {
                        int mode = data.getIntExtra("MODE",PLAY_MODE);
                        switch (mode){
                            case 1:
                                PLAY_MODE=1;
                                break;
                            case 2:
                                PLAY_MODE=2;
                                break;
                            case 3:
                                PLAY_MODE=3;
                                break;
                        }
                    }

            }
        }

    }

    //对应与onCreate方法的onDestroy方法
    @Override
    protected void onDestroy() {
        if(mediaPlayer.isPlaying())
        {
            mediaPlayer.stop(); //停止播放
        }
        mediaPlayer.release();//释放资源
        super.onDestroy();
    }


    @Override protected void onResume() {
        super.onResume();

        // 缺少权限时, 进入权限配置页面
        if (mPermissionsChecker.lacksPermissions(PERMISSIONS)) {
            startPermissionsActivity();
        }
    }

    private void startPermissionsActivity() {
        PermissionsActivity.startActivityForResult(this, REQUEST_CODE, PERMISSIONS);
    }

}

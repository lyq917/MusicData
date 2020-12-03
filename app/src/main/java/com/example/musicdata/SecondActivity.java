package com.example.musicdata;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SecondActivity extends AppCompatActivity {

    ImageButton settings_back;
    ImageButton play_mode_icon;
    TextView play_mode;

    private int PLAY_MODE=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        settings_back = (ImageButton)findViewById(R.id.settings_back);
        play_mode_icon = (ImageButton)findViewById(R.id.play_mode_icon);
        play_mode = (TextView)findViewById(R.id.play_mode) ;


        Intent intent = getIntent();
        PLAY_MODE=intent.getIntExtra("MODE",PLAY_MODE);
        switch (PLAY_MODE){
            case 1:
                ((ImageButton)play_mode_icon).setBackgroundResource(R.drawable.playmode1);
                play_mode.setText("顺序播放");
                break;
            case 2:
                ((ImageButton)play_mode_icon).setBackgroundResource(R.drawable.playmode2);
                play_mode.setText("随机播放");
                break;
            case 3:
                ((ImageButton)play_mode_icon).setBackgroundResource(R.drawable.playmode3);
                play_mode.setText("单曲循环");
                break;
        }

        init_ClickListeners();
    }

    private void init_ClickListeners(){
        settings_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent();
                intent.putExtra("MODE",PLAY_MODE);
                setResult(2,intent);

                finish();
            }
        });
        play_mode_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mode_Choose();
            }
        });

    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("MODE",PLAY_MODE);
        setResult(2,intent);

        finish();
    }

    private void mode_Choose() {
        final String[] mode = new String[]{"顺序播放", "随机播放","单曲循环"};
        new AlertDialog.Builder(SecondActivity.this)
                .setTitle("模式选择")
                .setItems(mode, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        switch (which) {
                            case 0:
                                PLAY_MODE=1;
                                ((ImageButton)play_mode_icon).setBackgroundResource(R.drawable.playmode1);
                                play_mode.setText("顺序播放");
                                break;
                            case 1:
                                PLAY_MODE=2;
                                ((ImageButton)play_mode_icon).setBackgroundResource(R.drawable.playmode2);
                                play_mode.setText("随机播放");
                                break;
                            case 2:
                                PLAY_MODE=3;
                                ((ImageButton)play_mode_icon).setBackgroundResource(R.drawable.playmode3);
                                play_mode.setText("单曲循环");
                                break;
                        }
                    }
                })
                .create()
                .show();
    }
}

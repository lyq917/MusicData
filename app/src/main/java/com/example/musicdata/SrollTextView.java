package com.example.musicdata;

import android.content.Context;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

/**
 * 实现滚动显示歌曲信息
 */
//自定义控件 显示的文字必须要超出给定的宽度，到这里如果不出意外就可以看到文字滚动了。
//当你的界面view太多的时候，往往这个TextView就不一定能够获取到焦点，获取不到焦点也就看不懂跑马灯效果了下面给出解决办法
public class SrollTextView extends AppCompatTextView
{

    public SrollTextView(Context context)
    {
        super(context);
    }

    public SrollTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SrollTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean isFocused()
    {
        return true;
    }
}

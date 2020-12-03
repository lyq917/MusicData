package com.example.musicdata;


import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class MyAdapter extends BaseAdapter {
    private Context context;
    private List<Song> list;
    public MyAdapter(MainActivity mainActivity, List<Song> list) {
        this.context = mainActivity;
        this.list = list;

    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertview, ViewGroup viewGroup) {
        ViewHolder holder = null;
        if (convertview == null)
        {
            holder = new ViewHolder();
            //引入布局
            convertview = View.inflate(context, R.layout.item_music_listview, null);
            //实例化对象
            holder.song = (TextView) convertview.findViewById(R.id.item_mymusic_song);
            holder.singer = (TextView) convertview.findViewById(R.id.item_mymusic_singer);
            holder.duration = (TextView) convertview.findViewById(R.id.item_mymusic_duration);
            //holder.position = (TextView) convertview.findViewById(R.id.item_mymusic_postion);
            convertview.setTag(holder);
        }
        else //conertview 复用
            {
                holder = (ViewHolder) convertview.getTag();
            }
        //给控件赋值
        holder.song.setText(list.get(position).song.toString());
        holder.singer.setText(list.get(position).singer.toString());
        //时间需要转换一下
        int duration = list.get(position).duration;
        String time = MusicUtils.formatTime(duration);
        holder.duration.setText(time);
        //holder.position.setText(position+1+"");

        return convertview;
    }
    class ViewHolder{
        TextView song;//歌曲名
        TextView singer;//歌手
        TextView duration;//时长
        //TextView position;//序号

    }

}

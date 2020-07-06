package com.example.m_feelm;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;

public class ActorInfoItemAdapter extends BaseAdapter {
    Context context;
    int layout;
    ArrayList<ActorInfoItem> item=new ArrayList<ActorInfoItem>();
    LayoutInflater inf;

    public ActorInfoItemAdapter(Context context, int layout, ArrayList<ActorInfoItem> item) {
        this.context = context;
        this.layout = layout;
        this.item = item;
        inf = (LayoutInflater) context.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return item.size();
    }

    @Override
    public Object getItem(int position) {
        return item.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null){
            convertView = inf.inflate(R.layout.actor_info_item,parent,false);
            }
        ImageView iv = (ImageView) convertView.findViewById(R.id.actor_info_poster);
        TextView title=convertView.findViewById(R.id.actor_info_title);

        System.out.println("adapter Test: "+item.get(position).poster);
        Glide.with(context)
                .load(item.get(position).poster)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(iv);

        title.setText(item.get(position).getTitle());

        System.out.println("ok");
        return convertView;
    }
}



package ru.ifmo.droid2016.vkdemo;
/**
 * Created by Andrey on 28.12.2016.
 */


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ru.ifmo.droid2016.vkdemo.NewsDataBase.NewsDB;
import ru.ifmo.droid2016.vkdemo.model.Post;

public class RecyclerNews extends AppCompatActivity {
    RecyclerView lvSimple;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lenta);
        List<Post> data = new ArrayList<Post>();
        data.add(new Post(1,"","No group loaded","","","",0,0));
        NewsDB gg = new NewsDB(this.getApplicationContext(),1);
        List<Post> list = null;
        try {
            list = gg.get();
            for(int i=0;i<list.size();i++){
             //   Log.d("My",Integer.toString(list.get(i).id)+" "+list.get(i).header+" "+list.get(i).text);
            }
        } catch (Exception e){

        }
        RecyclerNewsAdapter sAdapter = new RecyclerNewsAdapter(this,data);
        if(list!=null){
            sAdapter = new RecyclerNewsAdapter(this,list);
        }
        lvSimple = (RecyclerView) findViewById(R.id.recyclerNews_post);
        lvSimple.setAdapter(sAdapter);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        lvSimple.setLayoutManager(llm);
    }
}

class RecyclerNewsAdapter
        extends RecyclerView.Adapter<RecyclerNewsAdapter.TimetableViewHolder> {

    private List<Post> data;

    private LayoutInflater layoutInflater;

    private Context context;
    ArrayList<Boolean> positionArray;


    public RecyclerNewsAdapter(@NonNull Context context,
                               @NonNull List<Post> data) {
        this.data = data;
        layoutInflater = LayoutInflater.from(context);
        this.context = context;
        positionArray = new ArrayList<Boolean>(data.size());
        for(int i =0;i<data.size();i++){
            //if(data.get(i).ignore==1)
             //   positionArray.add(false); else
                positionArray.add(true);
        }
        setHasStableIds(false);
    }
    public RecyclerNewsAdapter(){

    }

    public void setData(@NonNull List<Post> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @Override
    public TimetableViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return TimetableViewHolder.newInstance(layoutInflater, parent);
    }

    @Override
    public void onBindViewHolder(TimetableViewHolder holder, final int position) {
        final Post entry = data.get(position);
        Log.d("My",entry.text+"---");
        holder.news_header.setText(entry.header);
        holder.news_date.setText(entry.date);
        holder.news_text.setText(entry.text);
        /*
        holder.cbSelect.setOnCheckedChangeListener(null);
        holder.cbSelect.setChecked(positionArray.get(position));
        holder.cbSelect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //set your object's last statusif(isChecked ){
                GroupListDB gg = new GroupListDB(context,1);

                if(isChecked ){
                    gg.setIgnore(entry.id,0);
                    Log.d("My","SHo ce : "+Boolean.toString(isChecked)+" "+Integer.toString(entry.id));
                    positionArray.set(position, true);
                }else {
                    gg.setIgnore(entry.id,1);
                    Log.d("My", "SHo ce : " + Boolean.toString(isChecked) + " " + Integer.toString(entry.id));
                    positionArray.set(position, false);
                }
            }
        });*/

        //       final String avatar_url = entry.icon_url;
        //       final Bitmap blob = Bitmap.createBitmap(15, 15, Bitmap.Config.ALPHA_8);
//        holder.image.set;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class TimetableViewHolder extends RecyclerView.ViewHolder {

        final TextView news_header;
        final TextView news_date;
        final TextView news_text;
        final ImageView image;
        public CheckBox cbSelect;

        TimetableViewHolder(View view) {
            super(view);
            news_header = (TextView) view.findViewById(R.id.news_header);
            news_text = (TextView) view.findViewById(R.id.news_text_pls);
            news_date = (TextView) view.findViewById(R.id.news_date);
            cbSelect = (CheckBox) view.findViewById(R.id.group_ignore);
            image = (ImageView) view.findViewById(R.id.group_avatar);
        }

        static TimetableViewHolder newInstance(LayoutInflater layoutInflater, ViewGroup parent) {
            View view = layoutInflater.inflate(R.layout.item_news, parent, false);
            return new TimetableViewHolder(view);
        }
    }
}

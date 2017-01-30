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
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ru.ifmo.droid2016.vkdemo.GroupListDataBase.GroupListDB;
import ru.ifmo.droid2016.vkdemo.NewsDataBase.NewsDB;
import ru.ifmo.droid2016.vkdemo.model.GroupEntry;

public class RecyclerGroupSettings extends AppCompatActivity{
    RecyclerView lvSimple;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        List<GroupEntry> data = new ArrayList<GroupEntry>();
        data.add(new GroupEntry(1,"kek","No group loaded",0));
        GroupListDB gg = new GroupListDB(this.getApplicationContext(),1);
        List<GroupEntry> list = null;
        try {
            list = gg.get();
        } catch (Exception e){

        }
        RecyclerGroupsAdapter sAdapter = new RecyclerGroupsAdapter(this,data);
        if(list!=null){
             sAdapter = new RecyclerGroupsAdapter(this,list);
        }
        lvSimple = (RecyclerView) findViewById(R.id.recyclerGroups_post);
        lvSimple.setAdapter(sAdapter);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        lvSimple.setLayoutManager(llm);
    }
}

class RecyclerGroupsAdapter
        extends RecyclerView.Adapter<RecyclerGroupsAdapter.TimetableViewHolder> {

    private List<GroupEntry> data;

    private  LayoutInflater layoutInflater;

    private  Context context;
    ArrayList<Boolean> positionArray;


    public RecyclerGroupsAdapter(@NonNull Context context,
                                @NonNull List<GroupEntry> data) {
        this.data = data;
        layoutInflater = LayoutInflater.from(context);
        this.context = context;
        positionArray = new ArrayList<Boolean>(data.size());
        for(int i =0;i<data.size();i++){
            if(data.get(i).ignore==1)
            positionArray.add(false); else
                positionArray.add(true);
        }
        setHasStableIds(false);
    }
    public RecyclerGroupsAdapter(){

    }

    public void setData(@NonNull List<GroupEntry> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @Override
    public TimetableViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return TimetableViewHolder.newInstance(layoutInflater, parent);
    }

    @Override
    public void onBindViewHolder(TimetableViewHolder holder, final int position) {
        final GroupEntry entry = data.get(position);
        if(entry.header.length()>20){
            final String name = entry.header.replace("\n","").substring(0,20)+"...";
            holder.group_name.setText(name);
        } else {
            final String name = entry.header.replace("\n", "");
            holder.group_name.setText(name);
        }
        holder.cbSelect.setOnCheckedChangeListener(null);
        holder.cbSelect.setChecked(positionArray.get(position));
        holder.cbSelect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //set your object's last statusif(isChecked ){
                GroupListDB gg = new GroupListDB(context,1);
                NewsDB gg2 = new NewsDB(context,1);

                if(isChecked ){
                    gg.setIgnore(entry.id,0);
                    Log.d("My","SHo ce : "+Boolean.toString(isChecked)+" "+Integer.toString(entry.id));
                    positionArray.set(position, true);
             }else {
                    gg2.deleteGroup(entry.id);
                    gg.setIgnore(entry.id,1);
                    Log.d("My", "SHo ce : " + Boolean.toString(isChecked) + " " + Integer.toString(entry.id));
                    positionArray.set(position, false);
                }
            }
        });
        //       final String avatar_url = entry.icon_url;
        //       final Bitmap blob = Bitmap.createBitmap(15, 15, Bitmap.Config.ALPHA_8);
//        holder.image.set;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class TimetableViewHolder extends RecyclerView.ViewHolder {

        final TextView group_name;
        final ImageView image;
        public CheckBox cbSelect;

        TimetableViewHolder(View view) {
            super(view);
            group_name = (TextView) view.findViewById(R.id.group_header);
            cbSelect = (CheckBox) view.findViewById(R.id.group_ignore);
            image = (ImageView) view.findViewById(R.id.group_avatar);
        }

        static TimetableViewHolder newInstance(LayoutInflater layoutInflater, ViewGroup parent) {
            View view = layoutInflater.inflate(R.layout.item_group, parent, false);
            return new TimetableViewHolder(view);
        }
    }
}
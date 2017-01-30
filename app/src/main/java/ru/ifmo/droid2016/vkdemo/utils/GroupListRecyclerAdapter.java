package ru.ifmo.droid2016.vkdemo.utils;

/**
 * Created by Andrey on 28.12.2016.
 */


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import ru.ifmo.droid2016.vkdemo.R;
import ru.ifmo.droid2016.vkdemo.model.GroupEntry;


class GroupRecyclerAdapter
        extends RecyclerView.Adapter<GroupRecyclerAdapter.TimetableViewHolder> {

    @NonNull
    private List<GroupEntry> data;

    @NonNull
    private final LayoutInflater layoutInflater;

    @NonNull
    private final Context context;


    public GroupRecyclerAdapter(@NonNull Context context,
                                    @NonNull List<GroupEntry> data) {
        this.data = data;
        layoutInflater = LayoutInflater.from(context);
        this.context = context;
        setHasStableIds(false);
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
    public void onBindViewHolder(TimetableViewHolder holder, int position) {
        final GroupEntry entry = data.get(position);
        final String name = entry.header;
 //       final String avatar_url = entry.icon_url;
 //       final Bitmap blob = Bitmap.createBitmap(15, 15, Bitmap.Config.ALPHA_8);
        holder.group_name.setText(name);
//        holder.image.set;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class TimetableViewHolder extends RecyclerView.ViewHolder {

        final TextView group_name;
        final ImageView image;

        TimetableViewHolder(View view) {
            super(view);
            group_name = (TextView) view.findViewById(R.id.group_header);
            image = (ImageView) view.findViewById(R.id.group_avatar);
        }

        static TimetableViewHolder newInstance(LayoutInflater layoutInflater, ViewGroup parent) {
            View view = layoutInflater.inflate(R.layout.item_group, parent, false);
            return new TimetableViewHolder(view);
        }
    }
}


package ru.ifmo.droid2016.vkdemo.model;

import java.util.Comparator;

/**
 * Created by Nemzs on 20.12.2016.
 */
public class Post {

    /**
     * Ссылка на иконку группы(или что там надо)
     */
    public final String icon_url;

    /**
     * Заголовок(имя группы)
     */
    public final String header;

    /**
     * ID группы
     */
    public final int id;

    /**
     * Дата и время YY:MM:DD HH:MM
     */
    public final String date;

    /**
     * Текст новости
     */
    public final String text;

    public final String group_id;

    public final int likes;

    public final int reposts;


    public Post(int id, String icon_url, String header, String date, String text,String group_id,int likes,int reposts) {
        this.id = id;
        this.icon_url = icon_url;
        this.header = header;
        this.text = text;
        this.date = date;
        this.group_id = group_id;
        this.likes = likes;
        this.reposts = reposts;
    }
    public static final Comparator<Post> COMPARE_BY_LIKES = new Comparator<Post>() {
        @Override
        public int compare(Post lhs, Post rhs) {
            return rhs.likes - lhs.likes;
        }
    };
}

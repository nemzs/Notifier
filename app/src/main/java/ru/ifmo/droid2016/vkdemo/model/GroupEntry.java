package ru.ifmo.droid2016.vkdemo.model;

/**
 * Created by Andrey on 21.12.2016.
 */

public class GroupEntry {

    /**
     * ID группы
     */
    public final int id;

    /**
     * Ссылка на иконку группы(или что там надо)
     */
    public final String icon_url;

    /**
     *Заголовок(имя группы)
     */
    public final String header;
    public int ignore;

    public GroupEntry(int id, String icon_url, String header, int ignore ){
        this.id = id;
        this.icon_url = icon_url;
        this.header = header;
        this.ignore = ignore;
    }
}

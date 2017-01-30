package ru.ifmo.droid2016.vkdemo.GroupListDataBase;

import android.support.annotation.IntDef;

/**
 * Created by Andrey on 27.12.2016.
 */


@IntDef(value = {
        DataSchemeVersion.V1
})

public @interface DataSchemeVersion {

    int V1 = 1;
}

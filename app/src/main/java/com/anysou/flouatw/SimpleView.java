package com.anysou.flouatw;

import android.content.Context;
import android.view.LayoutInflater;

import com.dalimao.library.DragView;

/**
 * Created by liuguangli on 16/10/25.
 */

public class SimpleView extends DragView {
    public SimpleView(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.float_simple, this);
    }

}

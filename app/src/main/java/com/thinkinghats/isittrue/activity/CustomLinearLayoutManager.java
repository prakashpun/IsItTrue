package com.thinkinghats.isittrue.activity;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;

/**
 * Created by prakash_pun on 8/7/2017.
 */

public class CustomLinearLayoutManager extends LinearLayoutManager{

    private boolean enableScroll = false;

    public CustomLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);

    }

    @Override
    public boolean canScrollVertically() {
        return enableScroll;
    }

    @Override
    public boolean canScrollHorizontally() {
        return enableScroll;
    }

}

package com.example.recyclerlistvideo;

import com.chad.library.adapter.base.entity.MultiItemEntity;

/**
 * @author sunwei
 * email：tianmu19@gmail.com
 * date：2020/3/19 10:35
 * package：com.example.recyclerlistvideo
 * version：1.0
 * <p>description：              </p>
 */
public class MultiItem implements MultiItemEntity {
    private int itemType;
    public static final int VIDEO = 1;
    public static final int GRID = 0;
    public static final int TWOPIC = 2;

    private int seekPosition = 0;

    public int getSeekPosition() {
        return seekPosition;
    }

    public void setSeekPosition(int seekPosition) {
        this.seekPosition = seekPosition;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    @Override
    public int getItemType() {
        return itemType;
    }
}

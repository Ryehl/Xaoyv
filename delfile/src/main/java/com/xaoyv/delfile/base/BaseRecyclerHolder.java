package com.xaoyv.delfile.base;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import butterknife.ButterKnife;

/**
 * Tag:BaseHolder
 *
 * @author Xaoyv
 * date 2021/7/16 14:58
 */
public class BaseRecyclerHolder extends RecyclerView.ViewHolder {
    public BaseRecyclerHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}

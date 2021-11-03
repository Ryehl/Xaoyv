package com.xaoyv.delfile.base;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * Tag:BaseAdapter
 *
 * @author Xaoyv
 * date 2021/7/16 14:41
 */
public abstract class BaseRecyclerAdapter<T> extends RecyclerView.Adapter<BaseRecyclerHolder> {
    protected List<T> mDatas;
    private OnXRecyclerItemClickListner onXRecyclerItemClickListner = null;//三方XRecyclerView点击事件
    private OnRecyclerItemClickListner onRecyclerItemClickListner = null;//RecyclerView点击事件

    @LayoutRes
    protected abstract int getLayoutId(int viewType);

    protected abstract BaseRecyclerHolder getCreateViewHolder(View view, int viewType);

    @NonNull
    @Override
    public BaseRecyclerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(getLayoutId(viewType), parent, false);
        BaseRecyclerHolder mHolder = getCreateViewHolder(view, viewType);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = mHolder.getLayoutPosition();
                if (onRecyclerItemClickListner != null) {
                    onRecyclerItemClickListner.onItemClickListner(v, position);
                }
                if (onXRecyclerItemClickListner != null) {
                    onXRecyclerItemClickListner.onItemClickListner(v, position - 1);
                }
            }
        });
        return mHolder;
    }

    @Override
    public int getItemCount() {
        return mDatas == null ? 0 : mDatas.size();
    }


    public List<T> getDatas() {
        return mDatas;
    }

    /**
     * 添加数据
     *
     * @param mDatas
     */
    public void setData(List<T> mDatas) {
        this.mDatas = mDatas;
        notifyDataSetChanged();
    }

    /**
     * 添加数据
     *
     * @param datas
     */
    public void addData(List<T> datas) {
        if (this.mDatas == null) {
            this.mDatas = new ArrayList<>();
        }
        this.mDatas.addAll(datas);
        notifyDataSetChanged();
    }

    public void addData(T data) {
        if (this.mDatas == null) {
            this.mDatas = new ArrayList<>();
        }
        this.mDatas.add(data);
        notifyItemChanged(getItemCount());
    }


    public interface OnRecyclerItemClickListner {
        void onItemClickListner(View v, int position);
    }

    public interface OnXRecyclerItemClickListner {
        void onItemClickListner(View v, int position);
    }

    public void setOnXRecyclerItemClickListner(OnXRecyclerItemClickListner onXRecyclerItemClickListner) {
        this.onXRecyclerItemClickListner = onXRecyclerItemClickListner;
    }

    public void setOnRecyclerItemClickListner(OnRecyclerItemClickListner onRecyclerItemClickListner) {
        this.onRecyclerItemClickListner = onRecyclerItemClickListner;
    }
}

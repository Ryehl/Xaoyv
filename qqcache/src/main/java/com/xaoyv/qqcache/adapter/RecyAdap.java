package com.xaoyv.qqcache.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.xaoyv.qqcache.R;

import java.util.List;

/**
 * <p>Project's name:Xaoyv</p>
 * <p>说明:RecyclerView</p>
 *
 * @author Xaoyv
 * date 12/16/2020 4:30 PM
 */
public class RecyAdap extends RecyclerView.Adapter<RecyAdap.Holder> {
    private List<String> list;
    private OnItemClickListener listener;

    public RecyAdap(List<String> list) {
        this.list = list;
    }

    public void setListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        Glide.with(holder.itemView.getContext()).load(list.get(position)).into(holder.img);
        holder.img.setOnClickListener(v -> {
            listener.onItemClickListener(holder.img, holder.getLayoutPosition());
        });
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = View.inflate(parent.getContext(), R.layout.item_recy, null);
        return new Holder(inflate);
    }

    @Override
    public int getItemCount() {
        if (list == null)
            return 0;
        return list.size();
    }

    static class Holder extends RecyclerView.ViewHolder {
        ImageView img;

        Holder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.item_show);
        }
    }

    public interface OnItemClickListener {
        void onItemClickListener(View view, int position);
    }
}

package com.xaoyv.app.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.xaoyv.app.R;

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

    public void setListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public RecyAdap(List<String> list) {
        this.list = list;
    }

    class Holder extends RecyclerView.ViewHolder {
        ImageView img;

        Holder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.item_show);
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onClick(itemView, getLayoutPosition());
                }
            });
        }
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        Glide.with(holder.itemView.getContext()).load(list.get(position)).into(holder.img);
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(View.inflate(parent.getContext(), R.layout.item_recy, null));
    }

    @Override
    public int getItemCount() {
        if (list == null)
            return 0;
        return list.size();
    }

    public interface OnItemClickListener {
        void onClick(View view, int position);
    }
}

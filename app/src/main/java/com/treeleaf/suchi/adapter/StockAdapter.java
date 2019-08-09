package com.treeleaf.suchi.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.treeleaf.suchi.R;
import com.treeleaf.suchi.realm.models.Items;


public class StockAdapter extends ListAdapter<Items, StockAdapter.StockHolder> {
    private OnItemClickListener listener;

    public StockAdapter() {
        super(DIFF_CALLBACK);
    }


    private static final DiffUtil.ItemCallback<Items> DIFF_CALLBACK = new DiffUtil.ItemCallback<Items>() {
        @Override
        public boolean areItemsTheSame(@NonNull Items oldItem, @NonNull Items newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Items oldItem, @NonNull Items newItem) {
            return oldItem.getName().equals(newItem.getName()) && oldItem.getQuantity().equals(newItem.getQuantity())
                    && oldItem.getPhoto_url().equals(newItem.getPhoto_url())
                    && oldItem.getCode().equals(newItem.getCode())
                    && oldItem.getDesc().equals(newItem.getDesc())
                    && oldItem.getUnitPrice().equals(newItem.getUnitPrice())
                    && oldItem.getMarkedPrice().equals(newItem.getMarkedPrice())
                    && oldItem.getSellingPrice().equals(newItem.getSellingPrice())
                    && oldItem.getExpiryDate().equals(newItem.getExpiryDate());
        }
    };


    @NonNull
    @Override
    public StockAdapter.StockHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.stock_item, parent, false);
        return new StockHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull StockHolder holder, int position) {
        Items current = getItem(position);
        holder.unit.setText(current.getUnits().getName());
        holder.sku.setText(current.getName());
        holder.qty.setText(current.getQuantity());
        holder.price.setText(current.getUnitPrice());

    }


    public Items getStock(int position) {
        return getItem(position);
    }

    class StockHolder extends RecyclerView.ViewHolder {
        private TextView unit;
        private TextView sku;
        private TextView qty;
        private TextView price;


        public StockHolder(@NonNull View itemView) {
            super(itemView);

            unit = itemView.findViewById(R.id.tv_unit);
            sku = itemView.findViewById(R.id.tv_sku);
            qty = itemView.findViewById(R.id.tv_qty);
            price = itemView.findViewById(R.id.tv_price);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (listener != null && position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(getItem(position));
                    }
                }
            });


        }
    }

    public interface OnItemClickListener {
        void onItemClick(Items stock);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}

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
import com.treeleaf.suchi.realm.models.Stock;

public class StockAdapter extends ListAdapter<Stock, StockAdapter.StockHolder> {
    private OnItemClickListener listener;

    public StockAdapter() {
        super(DIFF_CALLBACK);
    }


    private static final DiffUtil.ItemCallback<Stock> DIFF_CALLBACK = new DiffUtil.ItemCallback<Stock>() {
        @Override
        public boolean areItemsTheSame(@NonNull Stock oldItem, @NonNull Stock newItem) {
            return oldItem.getSn().equals(newItem.getSn());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Stock oldItem, @NonNull Stock newItem) {
            return oldItem.getPrice().equals(newItem.getPrice()) && oldItem.getQuantity().equals(newItem.getQuantity())
                    && oldItem.getSku().equals(newItem.getSku());
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
        Stock currentStock = getItem(position);
        holder.serialNo.setText(currentStock.getSn());
        holder.sku.setText(currentStock.getSku());
        holder.qty.setText(currentStock.getQuantity());
        holder.price.setText(currentStock.getPrice());

    }


    public Stock getStock(int position) {
        return getItem(position);
    }

    class StockHolder extends RecyclerView.ViewHolder {
        private TextView serialNo;
        private TextView sku;
        private TextView qty;
        private TextView price;


        public StockHolder(@NonNull View itemView) {
            super(itemView);

            serialNo = itemView.findViewById(R.id.tv_sn);
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
        void onItemClick(Stock stock);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}

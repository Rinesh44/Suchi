package com.treeleaf.suchi.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.treeleaf.suchi.R;
import com.treeleaf.suchi.realm.models.Stock;

import java.util.ArrayList;
import java.util.List;

public class StockAdapter extends RecyclerView.Adapter<StockAdapter.StockHolder> {
    private List<Stock> stocks = new ArrayList<>();

    @NonNull
    @Override
    public StockAdapter.StockHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.stock_item, parent, false);
        return new StockHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull StockHolder holder, int position) {
        Stock currentStock = stocks.get(position);
        holder.serialNo.setText(currentStock.getSn());
        holder.sku.setText(currentStock.getSku());
        holder.qty.setText(currentStock.getQuantity());
        holder.price.setText(currentStock.getPrice());

    }

    @Override
    public int getItemCount() {
        return stocks.size();
    }

    public void setStocks(List<Stock> stocks) {
        this.stocks = stocks;
        notifyDataSetChanged();
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


        }
    }
}

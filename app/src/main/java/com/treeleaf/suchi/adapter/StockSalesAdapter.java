package com.treeleaf.suchi.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.treeleaf.suchi.R;
import com.treeleaf.suchi.dto.InventoryDto;
import com.treeleaf.suchi.dto.InventoryStocksDto;
import com.treeleaf.suchi.realm.models.Units;
import com.treeleaf.suchi.realm.repo.UnitRepo;
import com.treeleaf.suchi.utils.AppUtils;

import java.util.ArrayList;
import java.util.List;

public class StockSalesAdapter extends RecyclerView.Adapter<StockSalesAdapter.StockSalesHolder> {
    private static final String TAG = "StockSalesAdapter";
    private List<InventoryStocksDto> salesStockList = new ArrayList<>();
    private InventoryDto inventoryDto;
    private Context mContext;
    //var to get current adapter position
    private int selectedPosition = -1;
    private RecyclerView mRecyclerView;

    public StockSalesAdapter(Context mContext, InventoryDto inventoryDto, RecyclerView mRecyclerView) {
        this.mContext = mContext;
        this.inventoryDto = inventoryDto;
        this.salesStockList = inventoryDto.getInventoryStocks();
        this.mRecyclerView = mRecyclerView;
    }

    @NonNull
    @Override
    public StockSalesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.sales_stock, parent, false);
        return new StockSalesHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull StockSalesHolder holder, int position) {
        InventoryStocksDto salesStock = salesStockList.get(position);


        StringBuilder unitSellingPriceBuilder = new StringBuilder();
        unitSellingPriceBuilder.append("Rs. ");
        unitSellingPriceBuilder.append(salesStock.getSalesPrice());
        holder.mSellingPrice.setText(unitSellingPriceBuilder);

        holder.mQuantity.setText(salesStock.getQuantity());

        Units unit = UnitRepo.getInstance().getUnitById(salesStock.getUnitId());
        holder.mUnit.setText(unit.getName());

        StringBuilder totalAmountBuilder = new StringBuilder();
        totalAmountBuilder.append("Rs. ");

        double calculatedAmount = Double.valueOf(salesStock.getSalesPrice()) * Double.valueOf(salesStock.getQuantity());
        totalAmountBuilder.append(String.valueOf(calculatedAmount));
        holder.mTotalPrice.setText(totalAmountBuilder);


      /*  //to make only one checkbox selectable
        if (selectedPosition == position) {
            holder.mSelect.setChecked(true);
        } else {
            holder.mSelect.setChecked(false);
        }*/


        holder.mSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedPosition = holder.getAdapterPosition();

                mRecyclerView.post(new Runnable() {
                    @Override
                    public void run() {
                        notifyDataSetChanged();
                    }
                });

                Intent intent = new Intent("selected_stock_qty");
                intent.putExtra("quantity", holder.mQuantity.getText().toString().trim());
                intent.putExtra("selling_price", holder.mSellingPrice.getText().toString().trim());
                intent.putExtra("inventory_id", inventoryDto.getInventory_id());
                intent.putExtra("inventory_stock_id", salesStock.getId());
                intent.putExtra("unit", holder.mUnit.getText().toString());
                intent.putExtra("photo_url", inventoryDto.getSku().getPhoto_url());
                intent.putExtra("name", inventoryDto.getSku().getName());
                intent.putExtra("checked", true);

                LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
            }
        });

  /*      holder.mSelect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                selectedPosition = holder.getAdapterPosition();

                //refresh recyclerview after view is laid upon
                mRecyclerView.post(new Runnable() {
                    @Override
                    public void run() {
                        notifyDataSetChanged();
                    }
                });

                if (checked) {
                    Intent intent = new Intent("selected_stock_qty");
                    intent.putExtra("quantity", holder.mQuantity.getText().toString().trim());
                    intent.putExtra("selling_price", holder.mSellingPrice.getText().toString().trim());
                    intent.putExtra("inventory_id", inventoryDto.getInventory_id());
                    intent.putExtra("inventory_stock_id", salesStock.getId());
                    intent.putExtra("unit", holder.mUnit.getText().toString());
                    intent.putExtra("photo_url", inventoryDto.getSku().getPhoto_url());
                    intent.putExtra("name", inventoryDto.getSku().getName());
                    intent.putExtra("checked", true);

                    LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
                }
            }
        });*/

    }


    @Override
    public int getItemCount() {
        return salesStockList.size();
    }

    class StockSalesHolder extends RecyclerView.ViewHolder {
        private TextView mSellingPrice;
        private MaterialButton mSelect;
        private TextView mTotalPrice;
        private TextView mQuantity;
        private TextView mUnit;


        public StockSalesHolder(@NonNull View itemView) {
            super(itemView);
            mSellingPrice = itemView.findViewById(R.id.tv_selling_price);
            mQuantity = itemView.findViewById(R.id.tv_quantity);
            mUnit = itemView.findViewById(R.id.tv_unit);
            mSelect = itemView.findViewById(R.id.cb_select);
            mTotalPrice = itemView.findViewById(R.id.tv_total_price);
        }
    }

}

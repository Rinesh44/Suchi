package com.treeleaf.suchi.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.treeleaf.suchi.R;
import com.treeleaf.suchi.realm.models.Inventory;
import com.treeleaf.suchi.realm.models.StockKeepingUnit;
import com.treeleaf.suchi.realm.models.Units;
import com.treeleaf.suchi.realm.repo.StockKeepingUnitRepo;
import com.treeleaf.suchi.realm.repo.UnitRepo;
import com.treeleaf.suchi.utils.AppUtils;

import de.hdodenhof.circleimageview.CircleImageView;


public class StockAdapter extends ListAdapter<Inventory, StockAdapter.StockHolder> {
    private static final String TAG = "StockAdapter";
    private OnItemClickListener listener;
    private Context context;
    private String imageUrl;

    public StockAdapter(Context context) {
        super(DIFF_CALLBACK);
        this.context = context;
    }


    private static final DiffUtil.ItemCallback<Inventory> DIFF_CALLBACK = new DiffUtil.ItemCallback<Inventory>() {
        @Override
        public boolean areItemsTheSame(@NonNull Inventory oldItem, @NonNull Inventory newItem) {
            return oldItem.getInventory_id().equals(newItem.getInventory_id());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Inventory oldItem, @NonNull Inventory newItem) {
            return oldItem.getUser_id().equals(newItem.getUser_id()) && oldItem.getQuantity().equals(newItem.getQuantity())
                    && oldItem.getMarkedPrice().equals(newItem.getMarkedPrice())
                    && oldItem.getSalesPrice().equals(newItem.getSalesPrice())
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
        Inventory current = getItem(position);
        if (current.isSynced()) {
            holder.sku.setText(current.getSku().getName());
            holder.unit.setText(current.getSku().getUnits().getName());
            holder.price.setText(current.getSku().getUnitPrice());
            holder.itemImage.setBorderColor(context.getResources().getColor(android.R.color.holo_green_light));
            imageUrl = current.getSku().getPhoto_url();
        } else {
            Units unit = UnitRepo.getInstance().getUnitById(current.getUnitId());
            StockKeepingUnit stockKeepingUnit = StockKeepingUnitRepo.getInstance().getSkuById(current.getSkuId());

            holder.sku.setText(stockKeepingUnit.getName());
            holder.unit.setText(unit.getName());
            holder.itemImage.setBorderColor(context.getResources().getColor(android.R.color.holo_red_light));
            holder.price.setText(stockKeepingUnit.getUnitPrice());

            imageUrl = stockKeepingUnit.getPhoto_url();
        }

        holder.qty.setText(current.getQuantity());

        AppUtils.showLog(TAG, "ImageUrl: " + imageUrl);
        if (!imageUrl.isEmpty()) {
            RequestOptions options = new RequestOptions()
                    .fitCenter()
                    .placeholder(R.drawable.ic_stock)
                    .error(R.drawable.ic_stock);

            Glide.with(context).load(imageUrl).apply(options).into(holder.itemImage);
        }


    }


    public Inventory getStock(int position) {
        return getItem(position);
    }

    class StockHolder extends RecyclerView.ViewHolder {
        private TextView unit;
        private TextView sku;
        private TextView qty;
        private TextView price;
        private CircleImageView itemImage;


        public StockHolder(@NonNull View itemView) {
            super(itemView);

            unit = itemView.findViewById(R.id.tv_unit);
            sku = itemView.findViewById(R.id.tv_sku);
            qty = itemView.findViewById(R.id.tv_qty);
            price = itemView.findViewById(R.id.tv_price);
            itemImage = itemView.findViewById(R.id.iv_item);

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
        void onItemClick(Inventory stock);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}

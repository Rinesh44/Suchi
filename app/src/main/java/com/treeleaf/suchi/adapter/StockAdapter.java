package com.treeleaf.suchi.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.treeleaf.suchi.R;
import com.treeleaf.suchi.dto.InventoryDto;
import com.treeleaf.suchi.realm.models.StockKeepingUnit;
import com.treeleaf.suchi.realm.repo.StockKeepingUnitRepo;
import com.treeleaf.suchi.utils.AppUtils;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class StockAdapter extends ListAdapter<InventoryDto, StockAdapter.StockHolder> implements Filterable {
    private static final String TAG = "StockAdapter";
    private OnItemClickListener listener;
    private Context context;
    private String imageUrl;
    private List<InventoryDto> inventoryListFiltered = new ArrayList<>();
    private List<InventoryDto> inventoryList;

    public StockAdapter(Context context, List<InventoryDto> inventoryList) {
        super(DIFF_CALLBACK);
        this.context = context;
        this.inventoryList = inventoryList;
        this.inventoryListFiltered = inventoryList;
    }


    private static final DiffUtil.ItemCallback<InventoryDto> DIFF_CALLBACK = new DiffUtil.ItemCallback<InventoryDto>() {
        @Override
        public boolean areItemsTheSame(@NonNull InventoryDto oldItem, @NonNull InventoryDto newItem) {
            return oldItem.getInventory_id().equals(newItem.getInventory_id());
        }

        @Override
        public boolean areContentsTheSame(@NonNull InventoryDto oldItem, @NonNull InventoryDto newItem) {
            return oldItem.isSynced() == newItem.isSynced()
                    && oldItem.getUser_id().equals(newItem.getUser_id())
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
        InventoryDto current = inventoryListFiltered.get(position);
        if (current.isSynced()) {
            holder.unsynced.setVisibility(View.GONE);
            holder.sku.setText(current.getSku().getName());
            holder.category.setText(current.getSku().getCategories().getName());
            holder.price.setText(current.getSku().getUnitPrice());
            holder.itemImage.setBorderColor(context.getResources().getColor(android.R.color.holo_green_light));
            imageUrl = current.getSku().getPhoto_url();
//            holder.itemView.setBackgroundColor(context.getResources().getColor(R.color.white));
        } else {

            StockKeepingUnit stockKeepingUnit = StockKeepingUnitRepo.getInstance().getSkuById(current.getSkuId());

            holder.unsynced.setVisibility(View.VISIBLE);
            holder.sku.setText(stockKeepingUnit.getName());
            holder.category.setText(stockKeepingUnit.getCategories().getName());
//            holder.itemView.setBackgroundColor(context.getResources().getColor(R.color.disabled));
            holder.price.setText(stockKeepingUnit.getUnitPrice());
            holder.itemImage.setBorderColor(context.getResources().getColor(android.R.color.transparent));

            imageUrl = stockKeepingUnit.getPhoto_url();
        }


        AppUtils.showLog(TAG, "ImageUrl: " + imageUrl);
        if (!imageUrl.isEmpty()) {
            RequestOptions options = new RequestOptions()
                    .fitCenter()
                    .placeholder(R.drawable.ic_stock)
                    .error(R.drawable.ic_stock);

            Glide.with(context).load(imageUrl).apply(options).into(holder.itemImage);
        }

    }

    @Override
    public int getItemCount() {
        return inventoryListFiltered.size();
    }

    public InventoryDto getStockAt(int position) {
        return getItem(position);
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    AppUtils.showLog(TAG, "char empty");
                    inventoryListFiltered = inventoryList;
                } else {
                    List<InventoryDto> filteredList = new ArrayList<>();
                    for (InventoryDto row : inventoryList) {
//                        AppUtils.showLog(TAG, "rows: " + row.getWalletAddress());
                        if (row.getSku().getName().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }

                    }

                    inventoryListFiltered = filteredList;

                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = inventoryListFiltered;
                return filterResults;

            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                inventoryListFiltered = (ArrayList<InventoryDto>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    class StockHolder extends RecyclerView.ViewHolder {
        private TextView category;
        private TextView sku;
        private TextView price;
        private ImageView unsynced;
        private CircleImageView itemImage;


        public StockHolder(@NonNull View itemView) {
            super(itemView);

            category = itemView.findViewById(R.id.tv_category);
            sku = itemView.findViewById(R.id.tv_sku);
            price = itemView.findViewById(R.id.tv_price);
            unsynced = itemView.findViewById(R.id.iv_unsynced);
            itemImage = itemView.findViewById(R.id.iv_item);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    AppUtils.showLog(TAG, "onclickCalled");

                    if (listener != null && position != RecyclerView.NO_POSITION) {
                        AppUtils.showLog(TAG, "allvalid");
                        listener.onItemClick(inventoryListFiltered.get(position));
                    }
                }
            });


        }
    }

    public interface OnItemClickListener {
        void onItemClick(InventoryDto stock);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}

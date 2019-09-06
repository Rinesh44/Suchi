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
import com.treeleaf.suchi.dto.SalesStockDto;
import com.treeleaf.suchi.utils.AppUtils;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SalesAdapter extends ListAdapter<SalesStockDto, SalesAdapter.SalesHolder> implements Filterable {
    private static final String TAG = "SalesAdapter";
    private OnItemClickListener listener;
    private Context context;
    private String imageUrl;
    private List<SalesStockDto> salesStockListFiltered = new ArrayList<>();
    private List<SalesStockDto> salesStockList;

    public SalesAdapter(Context context, List<SalesStockDto> salesStockList) {
        super(DIFF_CALLBACK);
        this.context = context;
        this.salesStockList = salesStockList;
        this.salesStockListFiltered = salesStockList;
    }

    private static final DiffUtil.ItemCallback<SalesStockDto> DIFF_CALLBACK = new DiffUtil.ItemCallback<SalesStockDto>() {
        @Override
        public boolean areItemsTheSame(@NonNull SalesStockDto oldItem, @NonNull SalesStockDto newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull SalesStockDto oldItem, @NonNull SalesStockDto newItem) {
            return oldItem.isSynced() == newItem.isSynced()
                    && oldItem.getInventory_id().equals(newItem.getInventory_id())
                    && oldItem.getName().equals(newItem.getName())
                    && oldItem.getAmount().equals(newItem.getAmount())
                    && oldItem.getPhotoUrl().equals(newItem.getPhotoUrl())
                    && oldItem.getQuantity().equals(newItem.getQuantity())
                    && oldItem.getUnit().equals(newItem.getUnit())
                    && oldItem.getUnitPrice().equals(newItem.getUnitPrice());
        }
    };

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    AppUtils.showLog(TAG, "char empty");
                    salesStockListFiltered = salesStockList;
                } else {
                    List<SalesStockDto> filteredList = new ArrayList<>();
                    for (SalesStockDto row : salesStockList) {
//                        AppUtils.showLog(TAG, "rows: " + row.getWalletAddress());
                        if (row.getName().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }

                    }

                    salesStockListFiltered = filteredList;

                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = salesStockListFiltered;
                return filterResults;

            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                salesStockListFiltered = (ArrayList<SalesStockDto>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    @NonNull
    @Override
    public SalesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.sales_item, parent, false);
        return new SalesHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull SalesHolder holder, int position) {
        SalesStockDto current = salesStockListFiltered.get(position);

        holder.name.setText(current.getName());
        holder.amount.setText(current.getAmount());
        holder.unitPrice.setText(current.getUnitPrice());
        holder.unit.setText(current.getUnit());
        holder.qty.setText(current.getQuantity());
        holder.itemImage.setBorderColor(context.getResources().getColor(android.R.color.holo_green_light));
        imageUrl = current.getPhotoUrl();

        if (current.isSynced()) {
            holder.unsynced.setVisibility(View.GONE);
        } else {
            holder.unsynced.setVisibility(View.VISIBLE);
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
        return salesStockListFiltered.size();
    }

    class SalesHolder extends RecyclerView.ViewHolder {
        private TextView name;
        private TextView qty;
        private TextView unit;
        private TextView unitPrice;
        private TextView amount;
        private CircleImageView itemImage;
        private ImageView unsynced;

        public SalesHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.tv_name);
            qty = itemView.findViewById(R.id.tv_qty);
            unit = itemView.findViewById(R.id.tv_unit);
            unitPrice = itemView.findViewById(R.id.tv_unit_price);
            amount = itemView.findViewById(R.id.tv_amount);
            unsynced = itemView.findViewById(R.id.iv_unsynced);
            itemImage = itemView.findViewById(R.id.iv_item_image);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    AppUtils.showLog(TAG, "onclickcalled");

                    if (listener != null && position != RecyclerView.NO_POSITION) {
                        AppUtils.showLog(TAG, "all valid");
                        listener.onItemClick(salesStockListFiltered.get(position));
                    }
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(SalesStockDto sales);
    }

    public void setOnItemClickListener(SalesAdapter.OnItemClickListener listener) {
        this.listener = listener;
    }


}



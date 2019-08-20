package com.treeleaf.suchi.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.treeleaf.suchi.R;
import com.treeleaf.suchi.dto.InventoryDto;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class InventorySalesAdapter extends ArrayAdapter {
    private static final String TAG = "InventorySalesAdapter";
    private List<InventoryDto> dataList;
    private Context mContext;
    private int itemLayout;

    private ListFilter listFilter = new ListFilter();
    private List<InventoryDto> dataListAllItems;

    public InventorySalesAdapter(Context context, int resource, List<InventoryDto> storeDataLst) {
        super(context, resource, storeDataLst);
        dataList = storeDataLst;
        mContext = context;
        itemLayout = resource;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public InventoryDto getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public View getView(int position, View view, @NonNull ViewGroup parent) {

        if (view == null) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(itemLayout, parent, false);
        }

        InventoryDto inventoryDto = getItem(position);

        TextView itemName = (TextView) view.findViewById(R.id.tv_item_name);
        itemName.setText(inventoryDto.getSku().getName());

        CircleImageView itemImage = (CircleImageView) view.findViewById(R.id.iv_item_icon);
        String imageUrl = inventoryDto.getSku().getPhoto_url();
        if (!imageUrl.isEmpty()) {
            RequestOptions options = new RequestOptions()
                    .fitCenter()
                    .placeholder(R.drawable.ic_stock)
                    .error(R.drawable.ic_stock);

            Glide.with(mContext).load(imageUrl).apply(options).into(itemImage);
        }

        return view;
    }


    @NonNull
    @Override
    public Filter getFilter() {
        return listFilter;
    }

    public class ListFilter extends Filter {
        private final Object lock = new Object();

        @Override
        protected FilterResults performFiltering(CharSequence prefix) {
            FilterResults results = new FilterResults();
            if (dataListAllItems == null) {
                synchronized (lock) {
                    dataListAllItems = new ArrayList<InventoryDto>(dataList);
                }
            }

            if (prefix == null || prefix.length() == 0) {
                synchronized (lock) {
                    results.values = dataListAllItems;
                    results.count = dataListAllItems.size();
                }
            } else {
                final String searchStrLowerCase = prefix.toString().toLowerCase();

                ArrayList<InventoryDto> matchValues = new ArrayList<InventoryDto>();

                for (InventoryDto dataItem : dataListAllItems) {
                    if (dataItem.getSku().getName().toLowerCase().startsWith(searchStrLowerCase)) {
                        matchValues.add(dataItem);
                    }
                }

                results.values = matchValues;
                results.count = matchValues.size();
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (results.values != null) {
                dataList = (ArrayList<InventoryDto>) results.values;
            } else {
                dataList = null;
            }
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }

    }
}



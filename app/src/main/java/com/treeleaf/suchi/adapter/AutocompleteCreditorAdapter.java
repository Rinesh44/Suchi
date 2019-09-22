package com.treeleaf.suchi.adapter;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
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
import com.treeleaf.suchi.dto.CreditorsDto;
import com.treeleaf.suchi.utils.AppUtils;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AutocompleteCreditorAdapter extends ArrayAdapter {
    private static final String TAG = "AutoCompleteSearchAdapt";

    private List<CreditorsDto> dataList;
    private Context mContext;
    private int itemLayout;

    private ListFilter listFilter = new ListFilter();
    private List<CreditorsDto> dataListAllItems;

    public AutocompleteCreditorAdapter(Context context, int resource, List<CreditorsDto> storeDataLst) {
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
    public CreditorsDto getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public View getView(int position, View view, @NonNull ViewGroup parent) {

        if (view == null) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(itemLayout, parent, false);
        }

        CreditorsDto creditorsDto = getItem(position);

        TextView itemName = (TextView) view.findViewById(R.id.tv_item_name);
        assert creditorsDto != null;
        itemName.setText(creditorsDto.getName());

        CircleImageView itemImage = (CircleImageView) view.findViewById(R.id.iv_item_icon);
        String image = creditorsDto.getPic();
        if (image != null) {
            Bitmap imageBitmap = decodeBase64(image);

            RequestOptions options = new RequestOptions()
                    .fitCenter()
                    .placeholder(R.drawable.ic_user_proto)
                    .error(R.drawable.ic_user_proto);

            Glide.with(mContext).load(imageBitmap).apply(options).into(itemImage);
        } else {
            itemImage.setImageDrawable(mContext.getDrawable(R.drawable.ic_user_proto));
        }

        return view;
    }


    public static Bitmap decodeBase64(String input) {
        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory
                .decodeByteArray(decodedByte, 0, decodedByte.length);
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
                    dataListAllItems = new ArrayList<CreditorsDto>(dataList);
                }
            }

            if (prefix == null || prefix.length() == 0) {
                synchronized (lock) {
                    results.values = dataListAllItems;
                    results.count = dataListAllItems.size();
                }
            } else {
                final String searchStrLowerCase = prefix.toString().toLowerCase();

                ArrayList<CreditorsDto> matchValues = new ArrayList<CreditorsDto>();

                for (CreditorsDto dataItem : dataListAllItems) {
                    if (dataItem.getName().toLowerCase().contains(searchStrLowerCase)) {
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
                dataList = (ArrayList<CreditorsDto>) results.values;
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

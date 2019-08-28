package com.treeleaf.suchi.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.treeleaf.suchi.R;
import com.treeleaf.suchi.realm.models.SalesStock;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.MyViewHolder> {

    private List<SalesStock> cartItemList;
    private Context mContext;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, qty, unit, unitPrice, amount;
        public CircleImageView itemImage;

        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.tv_name);
            itemImage = (CircleImageView) view.findViewById(R.id.iv_item_image);
            qty = (TextView) view.findViewById(R.id.tv_qty);
            unit = (TextView) view.findViewById(R.id.tv_unit);
            unitPrice = (TextView) view.findViewById(R.id.tv_unit_price);
            amount = (TextView) view.findViewById(R.id.tv_amount);
        }
    }


    public CartAdapter(Context mContext, List<SalesStock> cartItemList) {
        this.mContext = mContext;
        this.cartItemList = cartItemList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cart_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        SalesStock stock = cartItemList.get(position);
        holder.name.setText(stock.getName());
        holder.qty.setText(stock.getQuantity());
        holder.unit.setText(stock.getUnit());
        holder.unitPrice.setText(stock.getUnitPrice());
        holder.amount.setText(stock.getAmount());

        String imageUrl = stock.getPhotoUrl();
        if (!imageUrl.isEmpty()) {
            RequestOptions options = new RequestOptions()
                    .fitCenter()
                    .placeholder(R.drawable.ic_stock)
                    .error(R.drawable.ic_stock);

            Glide.with(mContext).load(imageUrl).apply(options).into(holder.itemImage);
        }
    }

    @Override
    public int getItemCount() {
        return cartItemList.size();
    }

    public void deleteItem(int pos) {
        cartItemList.remove(pos);
        notifyDataSetChanged();
    }
}
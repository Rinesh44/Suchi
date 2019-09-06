package com.treeleaf.suchi.adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.treeleaf.suchi.R;
import com.treeleaf.suchi.realm.models.SalesStock;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.MyViewHolder> {
    private final ViewBinderHelper viewBinderHelper = new ViewBinderHelper();
    private List<SalesStock> cartItemList;
    private Context mContext;
    private onEditClickListener editListener;
    private onDeleteClickListener deleteListener;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView name, qty, unit, unitPrice, amount;
        public CircleImageView itemImage;
        public SwipeRevealLayout mSwipeRevealLayout;
        public ImageButton mEdit, mDelete;

        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.tv_name);
            itemImage = (CircleImageView) view.findViewById(R.id.iv_item_image);
            qty = (TextView) view.findViewById(R.id.tv_qty);
            unit = (TextView) view.findViewById(R.id.tv_unit);
            unitPrice = (TextView) view.findViewById(R.id.tv_unit_price);
            amount = (TextView) view.findViewById(R.id.tv_amount);
            mSwipeRevealLayout = (SwipeRevealLayout) view.findViewById(R.id.srl_cart);
            mEdit = (ImageButton) view.findViewById(R.id.ib_edit);
            mDelete = (ImageButton) view.findViewById(R.id.ib_delete);
        }
    }


    public CartAdapter(Context mContext, List<SalesStock> cartItemList) {
        this.mContext = mContext;
        this.cartItemList = cartItemList;
        viewBinderHelper.setOpenOnlyOne(true);

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

        viewBinderHelper.bind(holder.mSwipeRevealLayout, stock.getId());

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

        holder.mEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editListener != null) {
                    editListener.onEditClicked(position);
                }
            }
        });

        holder.mDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (deleteListener != null) {
                    deleteListener.onDeleteClicked(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartItemList.size();
    }

    public void deleteItem(int pos) {
        cartItemList.remove(pos);
        notifyDataSetChanged();
    }

    public void closeSwipeLayout(String layoutId) {
        viewBinderHelper.closeLayout(layoutId);
    }

    public void saveStates(Bundle outState) {
        viewBinderHelper.saveStates(outState);
    }

    public void restoreStates(Bundle inState) {
        viewBinderHelper.restoreStates(inState);
    }

    public interface onEditClickListener {
        void onEditClicked(int position);
    }


    public interface onDeleteClickListener {
        void onDeleteClicked(int position);
    }

    public void setOnEditClickListener(CartAdapter.onEditClickListener editListener) {
        this.editListener = editListener;
    }

    public void setOnDeleteClickListener(onDeleteClickListener deleteListener) {
        this.deleteListener = deleteListener;
    }
}
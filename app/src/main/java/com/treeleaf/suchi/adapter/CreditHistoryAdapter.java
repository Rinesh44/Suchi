package com.treeleaf.suchi.adapter;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.treeleaf.suchi.R;
import com.treeleaf.suchi.dto.CreditDto;
import com.treeleaf.suchi.realm.models.Creditors;
import com.treeleaf.suchi.realm.repo.CreditorRepo;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CreditHistoryAdapter extends RecyclerView.Adapter<CreditHistoryAdapter.CreditHistoryHolder> {
    private static final String TAG = "CreditHistoryAdapter";
    private List<CreditDto> creditHistoryList = new ArrayList<>();
    private Context mContext;
    private OnItemClickListener listener;

    public CreditHistoryAdapter(Context mContext, List<CreditDto> creditHistoryList) {
        this.mContext = mContext;
        this.creditHistoryList = creditHistoryList;
    }

    @NonNull
    @Override
    public CreditHistoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.credit_history_item, parent, false);
        return new CreditHistoryHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CreditHistoryHolder holder, int position) {
        CreditDto creditDto = creditHistoryList.get(position);

        Creditors creditors = CreditorRepo.getInstance().getCreditorById(creditDto.getCreditorId());

        holder.mName.setText(creditors.getName());
        holder.mAddress.setText(creditors.getAddress());
        holder.mPhone.setText(creditors.getPhone());

        StringBuilder dueAmountBuilder = new StringBuilder();
        dueAmountBuilder.append("Rs. ");
        dueAmountBuilder.append(new DecimalFormat("##.##").format(Double.valueOf(creditDto.getDueAmount())));
        holder.mDueAmount.setText(dueAmountBuilder);


        if (creditors.getPic() != null) {
            Bitmap creditorImage = decodeBase64(creditors.getPic());
            if (creditorImage != null) {

                RequestOptions options = new RequestOptions()
                        .fitCenter()
                        .placeholder(R.drawable.ic_user_proto)
                        .error(R.drawable.ic_user_proto);

                Glide.with(mContext).load(creditorImage).apply(options).into(holder.mCreditorImage);
            }
        }


    }


    @Override
    public int getItemCount() {
        return creditHistoryList.size();
    }

    class CreditHistoryHolder extends RecyclerView.ViewHolder {
        private CircleImageView mCreditorImage;
        private TextView mName;
        private TextView mAddress;
        private TextView mPhone;
        private TextView mDueAmount;


        public CreditHistoryHolder(@NonNull View itemView) {
            super(itemView);
            mCreditorImage = itemView.findViewById(R.id.iv_creditor);
            mName = itemView.findViewById(R.id.tv_name);
            mAddress = itemView.findViewById(R.id.tv_address);
            mPhone = itemView.findViewById(R.id.tv_phone);
            mDueAmount = itemView.findViewById(R.id.tv_due_amount);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();

                    if (listener != null && position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(creditHistoryList.get(position));
                    }
                }
            });
        }
    }

    public static Bitmap decodeBase64(String input) {
        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory
                .decodeByteArray(decodedByte, 0, decodedByte.length);
    }

    public interface OnItemClickListener {
        void onItemClick(CreditDto creditDto);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

}

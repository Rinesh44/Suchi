package com.treeleaf.suchi.adapter;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.treeleaf.suchi.R;
import com.treeleaf.suchi.dto.CreditDto;
import com.treeleaf.suchi.dto.CreditorsDto;
import com.treeleaf.suchi.dto.InventoryDto;
import com.treeleaf.suchi.realm.models.Creditors;
import com.treeleaf.suchi.realm.repo.CreditorRepo;
import com.treeleaf.suchi.utils.AppUtils;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CreditHistoryAdapter extends RecyclerView.Adapter<CreditHistoryAdapter.CreditHistoryHolder> implements Filterable {
    private static final String TAG = "CreditHistoryAdapter";
    private List<CreditDto> creditHistoryList;
    private Context mContext;
    private OnItemClickListener listener;
    private List<CreditDto> creditHistoryListFiltered;

    public CreditHistoryAdapter(Context mContext, List<CreditDto> creditHistoryList) {
        this.mContext = mContext;
        this.creditHistoryList = creditHistoryList;
        this.creditHistoryListFiltered = creditHistoryList;
        AppUtils.showLog(TAG, "creditLIstSize:  " + creditHistoryList.size());
    }

    @NonNull
    @Override
    public CreditHistoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.credit_history_item, parent, false);
        return new CreditHistoryHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CreditHistoryHolder holder, int position) {
        CreditDto creditDto = creditHistoryListFiltered.get(position);

        Creditors creditors = CreditorRepo.getInstance().getCreditorById(creditDto.getCreditorId());

        holder.mName.setText(creditors.getName());
        holder.mAddress.setText(creditors.getAddress());
        holder.mPhone.setText(creditors.getPhone());

        if (!creditDto.getBalance().equals("0")) {
            StringBuilder dueAmountBuilder = new StringBuilder();
            dueAmountBuilder.append("Rs. ");
            dueAmountBuilder.append(new DecimalFormat("##.##").format(Double.valueOf(creditDto.getBalance())));
            holder.mDueAmount.setText(dueAmountBuilder);

            if (!creditDto.getPaidAmount().equals("N/A")) {
                StringBuilder paidAmountBuilder = new StringBuilder();
                paidAmountBuilder.append("Rs. ");
                paidAmountBuilder.append(new DecimalFormat("##.##").format(Double.valueOf(creditDto.getPaidAmount())));
                holder.mPaidAmount.setText(paidAmountBuilder);
            } else holder.mPaidAmount.setText(creditDto.getPaidAmount());

        } else {
            //remove amount and set paid text
            holder.mDueAmount.setText("Paid");

            String formattedTotalAmount = creditDto.getTotalAmount().replace("Rs. ", "");
            StringBuilder paidAmountBuilder = new StringBuilder();
            paidAmountBuilder.append("Rs. ");
            paidAmountBuilder.append(new DecimalFormat("##.##").format(Double.valueOf(formattedTotalAmount)));
            holder.mPaidAmount.setText(paidAmountBuilder);
        }


        holder.mAddedDate.setText(getDateSimple(creditors.getCreatedAt()));

        if (creditors.getPic() != null) {
            Bitmap creditorImage = decodeBase64(creditors.getPic());
            if (creditorImage != null) {

                RequestOptions options = new RequestOptions()
                        .fitCenter()
                        .placeholder(R.drawable.ic_user_proto)
                        .error(R.drawable.ic_user_proto);

                Glide.with(mContext).load(creditorImage).apply(options).into(holder.mCreditorImage);
            }
        } else {
            holder.mCreditorImage.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_user_proto));
        }


    }


    @Override
    public int getItemCount() {
        return creditHistoryListFiltered.size();
    }

    public List<CreditDto> getFilteredList() {
        return creditHistoryListFiltered;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    AppUtils.showLog(TAG, "char empty");
                    creditHistoryListFiltered = creditHistoryList;
                } else {
                    List<CreditDto> filteredList = new ArrayList<>();
                    for (CreditDto row : creditHistoryList) {
                        Creditors creditor = CreditorRepo.getInstance().getCreditorById(row.getCreditorId());
//                        AppUtils.showLog(TAG, "rows: " + row.getWalletAddress());
                        if (creditor.getName().toLowerCase().contains(charString.toLowerCase()) ||
                                creditor.getAddress().toLowerCase().contains(charString.toLowerCase()) ||
                                creditor.getPhone().contains(charString)) {
                            filteredList.add(row);
                        }

                    }

                    creditHistoryListFiltered = filteredList;

                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = creditHistoryListFiltered;
                return filterResults;

            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                creditHistoryListFiltered = (ArrayList<CreditDto>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public String getDateSimple(long time) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            return sdf.format(new Date(time));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    class CreditHistoryHolder extends RecyclerView.ViewHolder {
        private CircleImageView mCreditorImage;
        private TextView mName;
        private TextView mAddress;
        private TextView mPhone;
        private TextView mDueAmount;
        private TextView mPaidAmount;
        private TextView mAddedDate;

        public CreditHistoryHolder(@NonNull View itemView) {
            super(itemView);
            mCreditorImage = itemView.findViewById(R.id.iv_creditor);
            mName = itemView.findViewById(R.id.tv_creditor_name);
            mAddress = itemView.findViewById(R.id.tv_creditor_address);
            mPhone = itemView.findViewById(R.id.tv_creditor_phone);
            mDueAmount = itemView.findViewById(R.id.tv_due_amount);
            mPaidAmount = itemView.findViewById(R.id.tv_paid_amount);
            mAddedDate = itemView.findViewById(R.id.tv_creditor_joined);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();

                    if (listener != null && position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(creditHistoryListFiltered.get(position));
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

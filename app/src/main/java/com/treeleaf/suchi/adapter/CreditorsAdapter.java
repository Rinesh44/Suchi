package com.treeleaf.suchi.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.treeleaf.suchi.R;
import com.treeleaf.suchi.dto.CreditorsDto;
import com.treeleaf.suchi.realm.models.Creditors;
import com.treeleaf.suchi.realm.repo.CreditorRepo;
import com.treeleaf.suchi.utils.AppUtils;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CreditorsAdapter extends RecyclerView.Adapter<CreditorsAdapter.CreditorsHolder> implements Filterable {
    public static final String TAG = "CreditorsAdapter";
    private final ViewBinderHelper viewBinderHelper = new ViewBinderHelper();
    private List<CreditorsDto> creditorsList;
    private Context mContext;
    private List<CreditorsDto> creditorsListFiltered;
    private OnItemClickListener listener;
    private onEditClickListener editListener;
    private onDeleteClickListener deleteListener;


    public CreditorsAdapter(List<CreditorsDto> creditorsList, Context mContext) {
        this.creditorsList = creditorsList;
        this.mContext = mContext;
        this.creditorsListFiltered = creditorsList;
        viewBinderHelper.setOpenOnlyOne(true);
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    AppUtils.showLog(TAG, "char empty");
                    creditorsListFiltered = creditorsList;
                } else {
                    List<CreditorsDto> filteredList = new ArrayList<>();
                    for (CreditorsDto row : creditorsList) {
                        Creditors creditor = CreditorRepo.getInstance().getCreditorById(row.getId());
//                        AppUtils.showLog(TAG, "rows: " + row.getWalletAddress());
                        if(creditor != null) {
                            if (creditor.getName().toLowerCase().contains(charString.toLowerCase()) ||
                                    creditor.getAddress().toLowerCase().contains(charString.toLowerCase()) ||
                                    creditor.getPhone().contains(charString)) {
                                filteredList.add(row);
                            }
                        }

                    }

                    creditorsListFiltered = filteredList;

                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = creditorsListFiltered;
                return filterResults;

            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                creditorsListFiltered = (ArrayList<CreditorsDto>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    @NonNull
    @Override
    public CreditorsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.creditor_single_view, parent, false);
        return new CreditorsHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CreditorsHolder holder, int position) {
        CreditorsDto creditorsDto = creditorsListFiltered.get(position);

        Creditors creditors = CreditorRepo.getInstance().getCreditorById(creditorsDto.getId());

        viewBinderHelper.bind(holder.mSwipeRevealLayout, creditors.getId());

        holder.mName.setText(creditors.getName());
        holder.mAddress.setText(creditors.getAddress());
        holder.mPhone.setText(creditors.getPhone());

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


        holder.mEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editListener != null) {
                    editListener.onEditClicked(creditors.getId());
                }
            }
        });

        holder.mDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (deleteListener != null) {
                    deleteListener.onDeleteClicked(creditors.getId());
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return creditorsListFiltered.size();
    }

    class CreditorsHolder extends RecyclerView.ViewHolder {
        private CircleImageView mCreditorImage;
        private TextView mName;
        private TextView mAddress;
        private TextView mPhone;
        public SwipeRevealLayout mSwipeRevealLayout;
        public ImageButton mEdit, mDelete;


        public CreditorsHolder(@NonNull View itemView) {
            super(itemView);
            mCreditorImage = itemView.findViewById(R.id.iv_creditor_image);
            mName = itemView.findViewById(R.id.tv_creditor_name);
            mAddress = itemView.findViewById(R.id.tv_creditor_address);
            mPhone = itemView.findViewById(R.id.tv_creditor_number);
            mSwipeRevealLayout = (SwipeRevealLayout) itemView.findViewById(R.id.srl_creditors);
            mEdit = (ImageButton) itemView.findViewById(R.id.ib_edit);
            mDelete = (ImageButton) itemView.findViewById(R.id.ib_delete);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();

                    if (listener != null && position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(creditorsListFiltered.get(position));
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
        void onItemClick(CreditorsDto creditorDto);
    }

    public void setOnItemClickListener(CreditorsAdapter.OnItemClickListener listener) {
        this.listener = listener;
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
        void onEditClicked(String id);
    }

    public void deleteItem(String id) {
        removeFromDb(id);

        for (CreditorsDto creditorDto: creditorsListFiltered
             ) {
            if(creditorDto.getId().equals(id)) creditorsListFiltered.remove(creditorDto);
        }
        notifyDataSetChanged();
    }

    private void removeFromDb(String id) {
        CreditorRepo.getInstance().deleteCreditorById(id);
    }

    public interface onDeleteClickListener {
        void onDeleteClicked(String id);
    }

    public void setOnEditClickListener(CreditorsAdapter.onEditClickListener editListener) {
        this.editListener = editListener;
    }

    public void setOnDeleteClickListener(CreditorsAdapter.onDeleteClickListener deleteListener) {
        this.deleteListener = deleteListener;
    }

}

package com.treeleaf.suchi.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.treeleaf.suchi.R;
import com.treeleaf.suchi.dto.InventoryDto;
import com.treeleaf.suchi.dto.InventoryStocksDto;
import com.treeleaf.suchi.realm.models.Units;
import com.treeleaf.suchi.realm.repo.UnitRepo;
import com.treeleaf.suchi.utils.AppUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class StockSalesAdapter extends RecyclerView.Adapter<StockSalesAdapter.StockSalesHolder> {
    private static final String TAG = "StockSalesAdapter";
    private List<InventoryStocksDto> salesStockList = new ArrayList<>();
    private InventoryDto inventoryDto;
    private Context mContext;
    private List<Units> unitList = new ArrayList<>();
    private List<String> unitItems = new ArrayList<>();
    private ArrayAdapter<String> unitItemsAdapter;

    public StockSalesAdapter(Context mContext, InventoryDto inventoryDto) {
        this.mContext = mContext;
        this.inventoryDto = inventoryDto;
        this.salesStockList = inventoryDto.getInventoryStocks();
    }

    @NonNull
    @Override
    public StockSalesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.sales_stock, parent, false);
        return new StockSalesHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull StockSalesHolder holder, int position) {
        InventoryStocksDto salesStock = salesStockList.get(position);

        holder.mMarkedPrice.setText(salesStock.getMarkedPrice());
        holder.mSellingPrice.setText(salesStock.getSalesPrice());
        holder.mQuantity.setText(salesStock.getQuantity());

        Units unit = UnitRepo.getInstance().getUnitById(salesStock.getUnitId());
        holder.mUnit.setText(unit.getName());
        holder.mAmount.setText(salesStock.getSalesPrice());

        setUpUnitSpinner(inventoryDto.getSku().getUnits());
        holder.mUnitSpinner.setAdapter(unitItemsAdapter);

        holder.mIncrement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int value = Integer.valueOf(holder.mSelectQuantity.getText().toString());
                if (value < Integer.valueOf(holder.mQuantity.getText().toString())) {
                    value++;
                    holder.mSelectQuantity.setText(String.valueOf(value));
                    double amount = value * Double.valueOf(holder.mSellingPrice.getText().toString());
                    holder.mAmount.setText(String.valueOf(amount));
                } else {
                    Toast.makeText(mContext, "Quantity exceeded", Toast.LENGTH_SHORT).show();
                }
            }
        });

        holder.mSelectQuantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() != 0) {
                    double quantity = Double.valueOf(charSequence.toString());
                    if (quantity <= Double.valueOf(holder.mQuantity.getText().toString())) {
                        double sum = quantity * Double.valueOf(holder.mSellingPrice.getText().toString());
                        holder.mAmount.setText(String.valueOf(sum));
                    } else {
                        Toast.makeText(mContext, "Quantity exceeded", Toast.LENGTH_SHORT).show();
                        holder.mSelectQuantity.setText("1");
                        holder.mAmount.setText(holder.mSellingPrice.getText().toString());
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                AppUtils.showLog(TAG, "ontextchanged");
                String selectedUnit = (String) holder.mUnitSpinner.getSelectedItem();
                Intent intent = new Intent("item_details");
                intent.putExtra("amount", holder.mAmount.getText().toString().trim());
                intent.putExtra("quantity", holder.mSelectQuantity.getText().toString().trim());
                intent.putExtra("inventory_stock_id", salesStock.getId());
                intent.putExtra("unit", selectedUnit);

                LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
            }
        });

        holder.mDecrement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int value1 = Integer.valueOf(holder.mSelectQuantity.getText().toString());
                if (value1 != 1) {
                    value1--;
                    holder.mSelectQuantity.setText(String.valueOf(value1));
                    double amount = value1 * Double.valueOf(holder.mSellingPrice.getText().toString());
                    holder.mAmount.setText(String.valueOf(amount));
                }
            }
        });


        holder.mSelect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked) {
                    holder.mAmountHolder.setVisibility(View.VISIBLE);
                } else {
                    holder.mAmountHolder.setVisibility(View.GONE);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return salesStockList.size();
    }

    class StockSalesHolder extends RecyclerView.ViewHolder {
        private TextView mMarkedPrice;
        private TextView mSellingPrice;
        private TextView mQuantity;
        private TextView mUnit;
        private CheckBox mSelect;
        private LinearLayout mAmountHolder;
        private AppCompatSpinner mUnitSpinner;
        private EditText mSelectQuantity;
        private ImageButton mIncrement;
        private ImageButton mDecrement;
        private TextView mAmount;


        public StockSalesHolder(@NonNull View itemView) {
            super(itemView);

            mMarkedPrice = itemView.findViewById(R.id.tv_marked_price);
            mSellingPrice = itemView.findViewById(R.id.tv_selling_price);
            mQuantity = itemView.findViewById(R.id.tv_quantity);
            mUnit = itemView.findViewById(R.id.tv_unit);
            mSelect = itemView.findViewById(R.id.cb_select);
            mAmountHolder = itemView.findViewById(R.id.ll_price_holder);
            mUnitSpinner = itemView.findViewById(R.id.sp_unit);
            mSelectQuantity = itemView.findViewById(R.id.et_quantity);
            mIncrement = itemView.findViewById(R.id.btn_increment);
            mDecrement = itemView.findViewById(R.id.btn_decrement);
            mAmount = itemView.findViewById(R.id.tv_amount);
        }
    }

    private void setUpUnitSpinner(List<Units> unitList) {
        for (Units unit : unitList
        ) {
            unitItems.add(unit.getName());
        }

        Set<String> set = new HashSet<>(unitItems);
        unitItems.clear();
        unitItems.addAll(set);


        unitItemsAdapter = new ArrayAdapter<String>(mContext, R.layout.support_simple_spinner_dropdown_item, unitItems) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
//                TextView name = (TextView) view.findViewById(android.R.id.text1);
                return view;
            }
        };


    }
}

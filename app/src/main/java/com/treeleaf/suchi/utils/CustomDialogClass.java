package com.treeleaf.suchi.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.treeleaf.suchi.R;
import com.treeleaf.suchi.activities.inventory.stock.SearchStock;
import com.treeleaf.suchi.activities.inventory.stock.StockEntryActivity;
import com.treeleaf.suchi.realm.models.Items;
import com.treeleaf.suchi.realm.repo.ItemsRepo;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CustomDialogClass extends Dialog implements
        android.view.View.OnClickListener {
    private static final String TAG = "CustomDialogClass";

    public Activity activity;
    public Dialog dialog;
    @BindView(R.id.actv_sku)
    AutoCompleteTextView mSearchSku;
    @BindView(R.id.iv_go)
    public ImageView mGo;
    @BindView(R.id.btn_add_sku)
    public MaterialButton mCreateSku;

    public List<Items> mSkuItems = new ArrayList<>();


    public CustomDialogClass(Activity activity) {
        super(activity);
        // TODO Auto-generated constructor stub
        this.activity = activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_dialog);

        ButterKnife.bind(this);

        mGo.setOnClickListener(this);
        mCreateSku.setOnClickListener(this);

        mSkuItems = ItemsRepo.getInstance().getAllItems();
        List<String> skuList = new ArrayList<>();
        for (Items item : mSkuItems
        ) {
            skuList.add(item.getName());
        }


        ArrayAdapter<String> skuListAdapter = new ArrayAdapter<String>(activity, R.layout.support_simple_spinner_dropdown_item, skuList) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
//                TextView name = (TextView) view.findViewById(android.R.id.text1);
                return view;
            }
        };

        mSearchSku.setAdapter(skuListAdapter);

        mSearchSku.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                dismiss();

                String selectedItem = (String) adapterView.getItemAtPosition(i);

                int itemPosition = skuList.indexOf(selectedItem);
                AppUtils.showLog(TAG, "itemId: " + itemPosition);

                Items item = mSkuItems.get(itemPosition);

                Intent intent = new Intent(activity, SearchStock.class);
                intent.putExtra("selected_sku", item);
                activity.startActivity(intent);
            }
        });


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.iv_go:

                break;

            case R.id.btn_add_sku:
                activity.startActivity(new Intent(activity, StockEntryActivity.class));
                break;

            default:
                break;
        }
        dismiss();
    }
}
package com.treeleaf.suchi.activities.inventory.stock;

import com.treeleaf.suchi.realm.models.Items;

import java.util.List;

public interface StockView {
    void getStockItemsSuccess(List<Items> inventoryList);

    void getStockItemsFail(String msg);
}

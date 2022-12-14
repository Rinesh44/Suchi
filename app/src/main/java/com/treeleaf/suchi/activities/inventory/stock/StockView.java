package com.treeleaf.suchi.activities.inventory.stock;

import com.treeleaf.suchi.realm.models.Inventory;

import java.util.List;

public interface StockView {

    void addUnsyncedInventoriesSuccess();

    void addUnsyncedInventoriesFail(String msg);

    void getStockItemsSuccess(List<Inventory> inventoryList);

    void getStockItemsFail(String msg);
}

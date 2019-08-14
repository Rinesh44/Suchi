package com.treeleaf.suchi.activities.inventory.stock;

import com.treeleaf.suchi.realm.models.Inventory;

import java.util.List;

public interface StockView {

    void addUnsyncedInventoriesSuccess(List<Inventory> inventoryList);

    void addUnsyncedInventoriesFail(String msg);
}

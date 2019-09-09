package com.treeleaf.suchi.activities.inventory.stock;

import com.treeleaf.suchi.realm.models.Inventory;

import java.util.List;

public interface StockPresenter {
    void addUnsyncedInventories(String token, List<Inventory> inventoryList);

    void getStockItems(String token);
}

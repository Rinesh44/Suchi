package com.treeleaf.suchi.activities.inventory.stock;

import com.treeleaf.suchi.realm.models.Inventory;

import java.util.List;

public interface SearchStockView {
    void getStockItemsSuccess(List<Inventory> inventoryList);

    void getStockItemsFail(String msg);

    void addStockSuccess();

    void addStockFail(String msg);

    void updateStockSuccess();

    void updateStockFail(String msg);
}

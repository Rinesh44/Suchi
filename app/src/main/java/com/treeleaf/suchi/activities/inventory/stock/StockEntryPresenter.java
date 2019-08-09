package com.treeleaf.suchi.activities.inventory.stock;

import com.treeleaf.suchi.entities.InventoryProto;

public interface StockEntryPresenter {

    void makeStockEntry(String token, InventoryProto.StockKeepingUnit stockKeepingUnit);
}

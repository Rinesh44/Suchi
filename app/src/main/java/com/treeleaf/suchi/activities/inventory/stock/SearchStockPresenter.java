package com.treeleaf.suchi.activities.inventory.stock;

import com.treeleaf.suchi.entities.InventoryProto;
import com.treeleaf.suchi.entities.ReqResProto;

public interface SearchStockPresenter {
    void getStockItems(String token);

    void addStock(String token, InventoryProto.Inventory inventory);
}

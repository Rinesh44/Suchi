package com.treeleaf.suchi.activities.inventory.stock;

import com.treeleaf.suchi.entities.SuchiProto;

public interface SearchStockPresenter {
    void getStockItems(String token);

    void addStock(String token, SuchiProto.Inventory inventory);

    void updateStock(String token, SuchiProto.Inventory inventory);
}

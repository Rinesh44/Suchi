package com.treeleaf.suchi.activities.inventory.stock;

import com.treeleaf.suchi.entities.SuchiProto;

public interface StockEntryPresenter {

    void makeStockEntry(String token, SuchiProto.StockKeepingUnit stockKeepingUnit);
}

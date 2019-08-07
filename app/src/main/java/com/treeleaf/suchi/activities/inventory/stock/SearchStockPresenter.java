package com.treeleaf.suchi.activities.inventory.stock;

import com.treeleaf.suchi.entities.ReqResProto;

public interface SearchStockPresenter {

    void addStock(String token, ReqResProto.Request addInventoryRequest);
}

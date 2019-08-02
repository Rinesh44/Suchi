package com.treeleaf.suchi.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.treeleaf.suchi.realm.models.Stock;
import com.treeleaf.suchi.repository.StockRepository;

import java.util.List;

public class StockViewModel extends AndroidViewModel {
    private StockRepository stockRepository;
    private LiveData<List<Stock>> allStocks;


    public StockViewModel(@NonNull Application application) {
        super(application);
        stockRepository = new StockRepository(application);
        allStocks = stockRepository.getAllStocks();
    }

    public void insert(Stock stock) {
        stockRepository.insert(stock);
    }

    public void update(Stock stock) {
        stockRepository.insert(stock);
    }

    public void delete(Stock stock) {
        stockRepository.delete(stock);
    }

    public void deleteAllStocks() {
        stockRepository.deleteAllStocks();
    }

    public LiveData<List<Stock>> getAllStocks() {
        return allStocks;
    }


}

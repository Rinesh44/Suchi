package com.treeleaf.suchi.repository;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.treeleaf.suchi.dao.StockDao;
import com.treeleaf.suchi.realm.models.Stock;
import com.treeleaf.suchi.room.StockDatabase;

import java.util.List;

public class StockRepository {
    private StockDao stockDao;
    private LiveData<List<Stock>> allStocks;

    public StockRepository(Application application) {
        StockDatabase stockDatabase = StockDatabase.getInstance(application);
        stockDao = stockDatabase.stockDao();
        allStocks = stockDao.getAllStocks();
    }

    public void insert(Stock stock) {
        new InsertStockAsyncTask(stockDao).execute(stock);
    }

    public void update(Stock stock) {
        new UpdateStockAsyncTask(stockDao).execute(stock);
    }

    public void delete(Stock stock) {
        new DeleteStockAsyncTask(stockDao).execute(stock);
    }

    public void deleteAllStocks() {
        new DeleteAllStockAsyncTask(stockDao).execute();
    }

    public LiveData<List<Stock>> getAllStocks() {
        return allStocks;
    }


    private static class InsertStockAsyncTask extends AsyncTask<Stock, Void, Void> {
        private StockDao stockDao;

        public InsertStockAsyncTask(StockDao stockDao) {
            this.stockDao = stockDao;
        }

        @Override
        protected Void doInBackground(Stock... stocks) {
            stockDao.insert(stocks[0]);
            return null;
        }
    }

    private static class UpdateStockAsyncTask extends AsyncTask<Stock, Void, Void> {
        private StockDao stockDao;

        public UpdateStockAsyncTask(StockDao stockDao) {
            this.stockDao = stockDao;
        }

        @Override
        protected Void doInBackground(Stock... stocks) {
            stockDao.update(stocks[0]);
            return null;
        }
    }

    private static class DeleteStockAsyncTask extends AsyncTask<Stock, Void, Void> {
        private StockDao stockDao;

        public DeleteStockAsyncTask(StockDao stockDao) {
            this.stockDao = stockDao;
        }

        @Override
        protected Void doInBackground(Stock... stocks) {
            stockDao.delete(stocks[0]);
            return null;
        }
    }

    private static class DeleteAllStockAsyncTask extends AsyncTask<Void, Void, Void> {
        private StockDao stockDao;

        public DeleteAllStockAsyncTask(StockDao stockDao) {
            this.stockDao = stockDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            stockDao.deleteAllStocks();
            return null;
        }
    }

}

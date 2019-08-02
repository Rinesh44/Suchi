package com.treeleaf.suchi.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.treeleaf.suchi.realm.models.Stock;

import java.util.List;

@Dao
public interface StockDao {

    @Insert
    void insert(Stock stock);

    @Update
    void update(Stock stock);

    @Delete
    void delete(Stock stock);

    @Query("DELETE FROM stock_table")
    void deleteAllStocks();

    @Query("SELECT * FROM stock_table ORDER BY sn DESC")
    LiveData<List<Stock>> getAllStocks();
}

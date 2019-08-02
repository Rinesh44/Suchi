package com.treeleaf.suchi.room;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.treeleaf.suchi.dao.StockDao;
import com.treeleaf.suchi.realm.models.Stock;

@Database(entities = {Stock.class}, version = 1, exportSchema = false)
public abstract class StockDatabase extends RoomDatabase {

    private static StockDatabase instance;

    public abstract StockDao stockDao();

    public static synchronized StockDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(), StockDatabase.class, "stock_database")
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallback)
                    .build();
        }
        return instance;
    }

    private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback() {

        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new PopulateStocksAsyncTask(instance).execute();
        }
    };

    private static class PopulateStocksAsyncTask extends AsyncTask<Void, Void, Void> {
        private StockDao stockDao;

        public PopulateStocksAsyncTask(StockDatabase stockDb) {
            stockDao = stockDb.stockDao();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            stockDao.insert(new Stock("1", "Kotins", "2", "100"));
            return null;
        }
    }

}

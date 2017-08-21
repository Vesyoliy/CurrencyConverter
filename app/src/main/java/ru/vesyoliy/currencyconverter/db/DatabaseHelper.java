package ru.vesyoliy.currencyconverter.db;

import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

import java.util.List;

import ru.vesyoliy.currencyconverter.db.dao.CurrencyDao;
import ru.vesyoliy.currencyconverter.model.Currency;

/**
 * Working with a database in this project is very simple,
 * it used only in one place and in one thread.
 * In more difficult project it is needed to synchronize its methods
 * and to control it is single instance in the all project.
 */

public class DatabaseHelper {

    @NonNull
    private final DatabaseOpenHelper mDbOpenHelper;

    @Nullable
    private SQLiteDatabase mDatabase;

    @NonNull
    private final CurrencyDao mCurrencyDao;

    public DatabaseHelper(@NonNull DatabaseOpenHelper dbOpenHelper, @NonNull CurrencyDao currencyDao) {
        mDbOpenHelper = dbOpenHelper;
        mCurrencyDao = currencyDao;
    }

    @WorkerThread
    public void openDatabase() {
        if (mDatabase != null) {
            throw new IllegalStateException("Database is already open");
        }
        mDatabase = mDbOpenHelper.getWritableDatabase();
    }

    /**
     * Don't forgot to close the database when it is not needed any more.
     */
    @WorkerThread
    public void closeDatabase() {
        if (mDatabase == null) {
            throw new IllegalStateException("Database is not open yet");
        }
        mDatabase.close();
        mDatabase = null;
    }

    @WorkerThread
    public List<Currency> getCurrencyList() {
        if (mDatabase == null) {
            throw new IllegalStateException("Database is not open yet");
        }
        return mCurrencyDao.getCurrencyList(mDatabase);
    }

    @WorkerThread
    public void saveCurrencyList(@NonNull List<Currency> currencyList) {
        if (mDatabase == null) {
            throw new IllegalStateException("Database is not open yet");
        }
        mCurrencyDao.saveCurrencyList(mDatabase, currencyList);
    }

}

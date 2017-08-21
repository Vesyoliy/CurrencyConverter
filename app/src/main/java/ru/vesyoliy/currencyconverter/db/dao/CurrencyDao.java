package ru.vesyoliy.currencyconverter.db.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

import java.util.ArrayList;
import java.util.List;

import ru.vesyoliy.currencyconverter.model.Currency;

public final class CurrencyDao implements BaseDao {

    private static final String TABLE_NAME = "Currency";
    private static final String NAME_COLUMN = "name";
    private static final String VALUE_COLUMN = "value";
    private static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" +
            NAME_COLUMN + " TEXT UNIQUE NOT NULL, " +
            VALUE_COLUMN + " TEXT NOT NULL);";

    public CurrencyDao() {
        //ignored
    }

    @Override
    @WorkerThread
    public void createTable(@NonNull SQLiteDatabase database) {
        database.execSQL(CurrencyDao.CREATE_TABLE);
    }

    @Override
    @WorkerThread
    public void updateTable(@NonNull SQLiteDatabase database, int fromVersion, int toVersion) {
        //ignored
    }

    @Nullable
    @WorkerThread
    public List<Currency> getCurrencyList(@NonNull SQLiteDatabase database) {
        List<Currency> currencyList;

        Cursor cursor = database.rawQuery("SELECT " + NAME_COLUMN + ", " + VALUE_COLUMN +
                " FROM " + TABLE_NAME + ";", null);

        if (cursor.moveToFirst()) {
            currencyList = new ArrayList<>(cursor.getCount());

            do {
                currencyList.add(
                        new Currency(
                                cursor.getString(0),
                                cursor.getString(1)
                        )
                );
            } while (cursor.moveToNext());
        } else {
            currencyList = null;
        }

        cursor.close();

        return currencyList;
    }

    @WorkerThread
    public void saveCurrencyList(@NonNull SQLiteDatabase database, @NonNull List<Currency> currencyList) {
        ContentValues cv = new ContentValues(2);

        for (Currency currency : currencyList) {
            cv.clear();
            cv.put(VALUE_COLUMN, currency.getValue());
            cv.put(NAME_COLUMN, currency.getName());
            database.insertWithOnConflict(TABLE_NAME, null, cv, SQLiteDatabase.CONFLICT_REPLACE);
        }
    }

    @SuppressWarnings("unused")
    @WorkerThread
    private int getCurrencyCount(@NonNull SQLiteDatabase database) {
        Cursor cursor = database.rawQuery("select count(*) from " + TABLE_NAME + ";", null);
        int count;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        } else {
            count = -1;
        }
        cursor.close();
        return count;
    }
}

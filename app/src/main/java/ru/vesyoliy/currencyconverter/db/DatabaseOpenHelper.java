package ru.vesyoliy.currencyconverter.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;

import java.util.List;

import ru.vesyoliy.currencyconverter.db.dao.BaseDao;

public final class DatabaseOpenHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "CurrencyConverterDb";

    @NonNull
    private final List<BaseDao> mDaoList;

    public DatabaseOpenHelper(@NonNull Context context, @NonNull List<BaseDao> daoList) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mDaoList = daoList;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        for (BaseDao dao : mDaoList) {
            dao.createTable(sqLiteDatabase);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int fromVersion, int toVersion) {
        for (BaseDao dao : mDaoList) {
            dao.updateTable(sqLiteDatabase, fromVersion, toVersion);
        }
    }

}

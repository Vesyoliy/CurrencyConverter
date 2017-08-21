package ru.vesyoliy.currencyconverter.db.dao;

import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;

@WorkerThread
public interface BaseDao {

    void createTable(@NonNull SQLiteDatabase database);

    void updateTable(@NonNull SQLiteDatabase database, int fromVersion, int toVersion);

}

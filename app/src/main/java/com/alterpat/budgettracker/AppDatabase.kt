package com.alterpat.budgettracker

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = arrayOf(Transaction::class), version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transactionDao() : TransactionDao

    companion object {
        val MIGRATION_1_2: Migration = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE transactions ADD COLUMN date INTEGER DEFAULT NULL")
            }
        }
    }




}
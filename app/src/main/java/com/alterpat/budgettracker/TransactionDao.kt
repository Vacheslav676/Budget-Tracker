package com.alterpat.budgettracker

import androidx.room.*

@Dao
interface TransactionDao {
    @Query("SELECT * from transactions")
    fun getAll(): List<Transaction>

    @Insert
    fun insertAll(vararg transaction: Transaction)

    @Delete
    fun delete(transaction: Transaction)

    @Update
    fun update(vararg transaction: Transaction)

    @Query("SELECT * FROM transactions ORDER BY amount DESC")
    fun getAllSortedByAmountDescending(): List<Transaction>

    @Query("""
        SELECT * 
        FROM transactions 
        ORDER BY 
            CASE 
                WHEN amount > 0 THEN 0 
                ELSE 1 
            END, 
            CASE 
                WHEN amount > 0 THEN -amount 
                ELSE amount 
            END
    """)
    fun getPositiveDescendingNegativeAscending(): List<Transaction>


}
package com.techhive.statussaver.roomdata;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.techhive.statussaver.model.History;

import java.util.List;

@Dao
public interface HistoryDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(History history);

    @Query("DELETE FROM history_table")
    void deleteAll();

    @Query("SELECT * FROM history_table ORDER BY id ASC")
    List<History> getAllHistory();

    @Delete
    void delete(History history);

}

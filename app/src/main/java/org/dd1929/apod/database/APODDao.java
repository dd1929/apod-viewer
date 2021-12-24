package org.dd1929.apod.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import org.dd1929.apod.models.APOD;

import java.util.List;

@Dao
public interface APODDao {

    //Methods to insert data
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(APOD apod);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(List<APOD> apods);

    //Methods to delete data
    @Query("DELETE FROM apod_table")
    void deleteAll();

    @Delete
    void delete(APOD apod);

    @Query("UPDATE apod_table SET mIsRandom = 0 where mIsRandom = 1")
    void removeRandomAPODs();

    //Methods to retrieve data
    @Query("SELECT * FROM apod_table ORDER BY mDate DESC")
    LiveData<List<APOD>> getAllAPODs();

    @Query("SELECT * FROM apod_table WHERE mIsFavourite = 1")
    LiveData<List<APOD>> getFavAPODs();

    @Query("SELECT * FROM apod_table WHERE mIsRandom = 1")
    LiveData<List<APOD>> getRandomAPODs();

    @Query("SELECT * FROM apod_table WHERE mDate BETWEEN :startDate AND :endDate")
    LiveData<List<APOD>> getRangeAPODs(Long startDate, Long endDate);

    @Query("SELECT * FROM apod_table WHERE mDate LIKE :date")
    LiveData<APOD> getAPOD(Long date);

    @Query("SELECT * FROM apod_table WHERE mDate = (SELECT MAX(mDate) FROM apod_table)")
    LiveData<APOD> getLatestAPOD();

    //Methods to update fields
    @Query("UPDATE apod_table SET mIsFavourite = :isFav WHERE mDate = :date")
    void makeFavourite(Long date, Boolean isFav);

    @Query("UPDATE apod_table SET mIsRandom = :isRandom WHERE mDate = :date")
    void makeRandom(Long date, Boolean isRandom);
}

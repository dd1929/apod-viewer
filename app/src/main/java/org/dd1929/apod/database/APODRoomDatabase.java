package org.dd1929.apod.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import org.dd1929.apod.models.APOD;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {APOD.class}, version = 4, exportSchema = false)
public abstract class APODRoomDatabase extends RoomDatabase {

    public abstract APODDao apodDao();

    private static volatile APODRoomDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
    static APODRoomDatabase getDatabase(final Context context){
        if (INSTANCE == null){
            synchronized (APODRoomDatabase.class){
                if (INSTANCE == null){
                    INSTANCE = Room
                            .databaseBuilder(context.getApplicationContext(), APODRoomDatabase.class, "apod_database")
                            .addMigrations(MIGRATION_2_3, MIGRATION_2_4, MIGRATION_3_4)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    static final Migration MIGRATION_2_3= new Migration(2, 3) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE apod_table RENAME TO temp_table");
            database.execSQL("CREATE TABLE apod_table (mUrl TEXT, mHDUrl TEXT, mDetails TEXT, mThumbUrl TEXT, mImageFormat TEXT, mCopyright TEXT, mTitle TEXT, mDate INTEGER PRIMARY KEY, mAPODFileName TEXT, mMediaType TEXT, mIsFavourite INTEGER, mIsRandom INTEGER)");
            database.execSQL("INSERT INTO apod_table (mUrl, mHDUrl, mDetails, mThumbUrl, mImageFormat, mCopyright, mTitle, mDate, mAPODFileName, mMediaType) SELECT mUrl, mHDUrl, mDetails, mThumbUrl, mImageFormat, mCopyright, mTitle, mDate, mAPODFileName, mMediaType FROM temp_table");
            database.execSQL("DROP TABLE temp_table");
        }
    };

    static final Migration MIGRATION_2_4= new Migration(2, 4) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE apod_table RENAME TO temp_table");
            database.execSQL("CREATE TABLE apod_table (mUrl TEXT, mHDUrl TEXT, mDetails TEXT, mThumbUrl TEXT, mCopyright TEXT, mTitle TEXT, mDate INTEGER PRIMARY KEY, mMediaType TEXT, mIsFavourite INTEGER, mIsRandom INTEGER)");
            database.execSQL("INSERT INTO apod_table (mUrl, mHDUrl, mDetails, mThumbUrl, mCopyright, mTitle, mDate, mMediaType) SELECT mUrl, mHDUrl, mDetails, mThumbUrl, mCopyright, mTitle, mDate, mMediaType FROM temp_table");
            database.execSQL("DROP TABLE temp_table");
        }
    };

    static final Migration MIGRATION_3_4= new Migration(3, 4) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE apod_table RENAME TO temp_table");
            database.execSQL("CREATE TABLE apod_table (mUrl TEXT, mHDUrl TEXT, mDetails TEXT, mThumbUrl TEXT, mCopyright TEXT, mTitle TEXT, mDate INTEGER PRIMARY KEY, mMediaType TEXT, mIsFavourite INTEGER, mIsRandom INTEGER)");
            database.execSQL("INSERT INTO apod_table (mUrl, mHDUrl, mDetails, mThumbUrl, mCopyright, mTitle, mDate, mMediaType, mIsFavourite, mIsRandom) SELECT mUrl, mHDUrl, mDetails, mThumbUrl, mCopyright, mTitle, mDate, mMediaType, mIsFavourite, mIsRandom FROM temp_table");
            database.execSQL("DROP TABLE temp_table");
        }
    };
}

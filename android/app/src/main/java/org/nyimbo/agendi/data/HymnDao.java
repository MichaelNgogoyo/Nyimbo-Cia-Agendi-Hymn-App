package org.nyimbo.agendi.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import org.nyimbo.agendi.model.HymnEntity;
import java.util.List;

@Dao public interface HymnDao {
    @Query("SELECT * FROM hymns WHERE version = :version ORDER BY number")
    LiveData<List<HymnEntity>> hymns(int version);
    @Query("SELECT * FROM hymns WHERE version = :version AND (:query = '' OR title LIKE '%' || :query || '%' OR lyrics LIKE '%' || :query || '%' OR CAST(number AS TEXT) LIKE '%' || :query || '%') ORDER BY number")
    LiveData<List<HymnEntity>> search(int version, String query);
    @Query("SELECT * FROM hymns WHERE favorite = 1 ORDER BY title") LiveData<List<HymnEntity>> favorites();
    @Query("SELECT * FROM hymns WHERE lastViewedAt > 0 ORDER BY lastViewedAt DESC LIMIT :limit") LiveData<List<HymnEntity>> recent(int limit);
    @Query("SELECT * FROM hymns WHERE id = :id LIMIT 1") HymnEntity get(long id);
    @Query("SELECT * FROM hymns WHERE version = :version AND number = :number LIMIT 1") HymnEntity getByNumber(int version, int number);
    @Query("SELECT * FROM hymns WHERE version = :version AND number > :number ORDER BY number ASC LIMIT 1") HymnEntity next(int version, int number);
    @Query("SELECT * FROM hymns WHERE version = :version AND number < :number ORDER BY number DESC LIMIT 1") HymnEntity previous(int version, int number);
    @Query("SELECT COUNT(*) FROM hymns WHERE version = :version") int countByVersion(int version);
    @Insert(onConflict = OnConflictStrategy.REPLACE) void insertAll(List<HymnEntity> hymns);
    @Insert(onConflict = OnConflictStrategy.REPLACE) void upsert(HymnEntity hymn);
    @Transaction default void syncAll(List<HymnEntity> rows) {
        for (HymnEntity row : rows) {
            HymnEntity local = getByNumber(row.version, row.number);
            if (local != null) { row.id = local.id; row.favorite = local.favorite; row.userNotes = local.userNotes; row.lastViewedAt = local.lastViewedAt; if (row.sourceUrl == null) row.sourceUrl = local.sourceUrl; }
            upsert(row);
        }
    }
    @Query("UPDATE hymns SET favorite = :favorite WHERE id = :id") void setFavorite(long id, boolean favorite);
    @Query("UPDATE hymns SET userNotes = :notes WHERE id = :id") void saveNotes(long id, String notes);
    @Query("UPDATE hymns SET lastViewedAt = :time WHERE id = :id") void markViewed(long id, long time);
}

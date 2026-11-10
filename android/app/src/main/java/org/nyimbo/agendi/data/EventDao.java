package org.nyimbo.agendi.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Delete;
import org.nyimbo.agendi.model.EventEntity;
import java.util.List;

@Dao public interface EventDao {
    @Query("SELECT * FROM events ORDER BY eventDate, startTime") LiveData<List<EventEntity>> all();
    @Query("SELECT * FROM events WHERE bookmarked = 1 ORDER BY eventDate") LiveData<List<EventEntity>> bookmarks();
    @Query("SELECT * FROM events WHERE bookmarked = 1 ORDER BY eventDate") List<EventEntity> bookmarksBlocking();
    @Insert(onConflict = OnConflictStrategy.REPLACE) void upsertAll(List<EventEntity> events);
    @Insert(onConflict = OnConflictStrategy.REPLACE) void upsert(EventEntity event);
    @Query("SELECT * FROM events WHERE title = :title AND eventDate = :date AND venue = :venue LIMIT 1") EventEntity findMatching(String title, String date, String venue);
    @Query("SELECT * FROM events WHERE id = :id LIMIT 1") EventEntity findById(String id);
    @Delete void delete(EventEntity event);
    @Transaction default void syncAll(List<EventEntity> rows) {
        for (EventEntity row : rows) {
            EventEntity local = findMatching(row.title, row.eventDate, row.venue);
            if (local == null) local = findById(row.id);
            if (local != null) {
                row.bookmarked = local.bookmarked;
                if (!local.id.equals(row.id)) delete(local);
            }
            upsert(row);
        }
    }
    @Query("UPDATE events SET bookmarked = :saved WHERE id = :id") void setBookmarked(String id, boolean saved);
}

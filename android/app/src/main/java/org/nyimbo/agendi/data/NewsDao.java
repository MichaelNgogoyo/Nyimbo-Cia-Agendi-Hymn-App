package org.nyimbo.agendi.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import org.nyimbo.agendi.model.NewsEntity;
import java.util.List;

@Dao public interface NewsDao {
    @Query("SELECT * FROM news ORDER BY publishedAt DESC") LiveData<List<NewsEntity>> all();
    @Insert(onConflict = OnConflictStrategy.REPLACE) void upsertAll(List<NewsEntity> news);
}

package org.nyimbo.agendi.data;

import android.content.Context;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nyimbo.agendi.model.HymnEntity;
import java.util.Collections;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class HymnDaoTest {
    private NyimboDatabase db;
    @Before public void setUp() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, NyimboDatabase.class).allowMainThreadQueries().build();
        db.hymns().insertAll(Collections.singletonList(new HymnEntity(7, 1, "Authorized sample", "Verse one\n\nVerse two", null, null, null, false, false, "", 0)));
    }
    @After public void close() { if (db != null) db.close(); }
    @Test public void personalNotesAndFavoriteStayInTheLocalRow() {
        HymnEntity hymn = db.hymns().getByNumber(1, 7);
        db.hymns().saveNotes(hymn.id, "My rehearsal note"); db.hymns().setFavorite(hymn.id, true);
        HymnEntity stored = db.hymns().get(hymn.id);
        assertEquals("My rehearsal note", stored.userNotes); assertTrue(stored.favorite);
    }
    @Test public void oldAndNewVersionsHaveIndependentNumbering() {
        db.hymns().insertAll(Collections.singletonList(new HymnEntity(7, 0, "Old version record", "Old text", null, null, null, true, false, "", 0)));
        assertEquals("Authorized sample", db.hymns().getByNumber(1, 7).title);
        assertEquals("Old version record", db.hymns().getByNumber(0, 7).title);
    }
}

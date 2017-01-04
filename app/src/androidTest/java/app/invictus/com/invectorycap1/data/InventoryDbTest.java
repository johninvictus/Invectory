package app.invictus.com.invectorycap1.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static app.invictus.com.invectorycap1.data.InventoryContract.*;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * Created by invictus on 12/27/16.
 */

@RunWith(AndroidJUnit4.class)
public class InventoryDbTest {
    Context context;

    @Before
    public void getContext() {
        this.context = InstrumentationRegistry.getTargetContext();
    }

    @Test
    public void deleteAndCreateDb() {
        context.deleteDatabase(InventorySQLiteHelper.DATABASE);
        SQLiteDatabase db = new InventorySQLiteHelper(context).getReadableDatabase();
        assertTrue("Database cant be created ::ERROR", db.isOpen());
        db.close();
    }

    @Test
    public void insertRetrieveDb() {
        ContentValues values = new ContentValues();
        values.put(InventoryTable.COLUMN_INVENTORY_NAME, "Bread");
        values.put(InventoryTable.COLUMN_INVENTORY_DESCRIPTION, "The sweetest bread in EA");
        values.put(InventoryTable.COLUMN_INVENTORY_IMAGE, "no image");
        values.put(InventoryTable.COLUMN_INVENTORY_PRICE, "50");
        values.put(InventoryTable.COLUMN_INVENTORY_QUANTITY, "100");

        SQLiteDatabase db = new InventorySQLiteHelper(context).getWritableDatabase();
        long rowId = db.insert(InventoryTable.TABLE_NAME, null, values);

        assertTrue("Data was not added in the db ::ERROR ", rowId != -1);


        /**
         *Now retrieve the inserted data
         * */
        Cursor cursor = db.query(InventoryTable.TABLE_NAME, null, null, null, null, null, null);

        assertEquals("Data cant be retrieved from the db ::ERROR ", true, cursor.getCount() != -1);
        db.close();
    }

    @After
    public void clean() {
        context.deleteDatabase(InventorySQLiteHelper.DATABASE);
    }
}

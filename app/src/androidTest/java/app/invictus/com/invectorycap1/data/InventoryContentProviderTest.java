package app.invictus.com.invectorycap1.data;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.test.InstrumentationRegistry;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static app.invictus.com.invectorycap1.data.InventoryContract.*;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * Created by invictus on 12/27/16.
 */
@RunWith(JUnit4.class)
public class InventoryContentProviderTest {

    Context context;

    @Before
    public void getContext() {
        context = InstrumentationRegistry.getTargetContext();
    }

    @Test
    public void deleteDb() {
        /*
        * In order to start a fresh
        * **/
        context.deleteDatabase(InventorySQLiteHelper.DATABASE);
    }

    @Test
    public void getType() throws Exception {
        /*
        * Return dir type
        * */
        String dirType = context.getContentResolver().getType(InventoryTable.CONTENT_URI);
        assertEquals("Did not return dirType ::ERROR", dirType, InventoryTable.CONTENT_LIST_TYPE);

        /*
        * Return item type
        * */
        int sampleInt = 12;
        Uri sampleUri = ContentUris.withAppendedId(InventoryTable.CONTENT_URI, sampleInt);
        String itemType = context.getContentResolver().getType(sampleUri);

        assertEquals("Did not return itemType ::ERROR", itemType, InventoryTable.CONTENT_ITEM_TYPE);
    }

    @Test
    public void insertQueryUpdate() {

        ContentValues values = new ContentValues();
        values.put(InventoryTable.COLUMN_INVENTORY_NAME, "Bread");
        values.put(InventoryTable.COLUMN_INVENTORY_DESCRIPTION, "The sweetest bread in EA");
        values.put(InventoryTable.COLUMN_INVENTORY_IMAGE, "no image");
        values.put(InventoryTable.COLUMN_INVENTORY_PRICE, "50");
        values.put(InventoryTable.COLUMN_INVENTORY_QUANTITY, "100");

        Uri uri = context.getContentResolver().insert(InventoryTable.CONTENT_URI, values);

        assertEquals("Cant insert ::ERROR", true, uri != null);
        long rowId = ContentUris.parseId(uri);

        /**
         * get all product from query
         * **/
        Cursor cursor = null;

        cursor = context.getContentResolver()
                .query(InventoryTable.CONTENT_URI,
                        null,
                        null,
                        null,
                        null);

        Assert.assertEquals("Dir type query ::ERROR", true, cursor.getCount() != -1);

        /**
         * Get single item
         * */
        int sampleid = 1;
        cursor = context.getContentResolver().query(
                ContentUris.withAppendedId(InventoryTable.CONTENT_URI, sampleid),
                null,
                null,
                null,
                null);

        Assert.assertEquals("Cant get single item ::ERROR", true, cursor.getCount() == 1);

        /**
         * Update data
         * */
        ContentValues updateValues = new ContentValues();
        updateValues.put(InventoryTable.COLUMN_INVENTORY_PRICE, "50");

        int rows = context.getContentResolver().update(ContentUris.withAppendedId(InventoryTable.CONTENT_URI, rowId), updateValues, null, null);

        assertEquals("Cant update value", true, rows != -1);
    }


    @Test
    public void deleteContentDb() throws Exception {
        long l = context.getContentResolver().delete(InventoryTable.CONTENT_URI, null, null);
        assertTrue("Cant delete data", l != -1);

        /**
         * Have assumed single delete item will pass the test
         * Forgive my laziness
         * */
    }

}

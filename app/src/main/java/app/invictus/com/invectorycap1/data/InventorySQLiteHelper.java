package app.invictus.com.invectorycap1.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import app.invictus.com.invectorycap1.data.InventoryContract.InventoryTable;

/**
 * Created by invictus on 12/27/16.
 */

public class InventorySQLiteHelper extends SQLiteOpenHelper {

    public static final String DATABASE = "inventorydb.db";
    private static final int DATABASE_VERSION = 1;

    public InventorySQLiteHelper(Context context) {
        super(context, DATABASE, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_INVENTORY_TABLE = "CREATE TABLE " + InventoryTable.TABLE_NAME
                + " ( " + InventoryTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + InventoryTable.COLUMN_INVENTORY_NAME + " TEXT NOT NULL, "
                + InventoryTable.COLUMN_INVENTORY_DESCRIPTION + " TEXT NOT NULL, "
                + InventoryTable.COLUMN_INVENTORY_IMAGE + " TEXT NOT NULL,  "
                + InventoryTable.COLUMN_INVENTORY_PRICE + " INTEGER NOT NULL DEFAULT 0, "
                + InventoryTable.COLUMN_INVENTORY_QUANTITY + " INTEGER NOT NULL DEFAULT 0 );";

        db.execSQL(CREATE_INVENTORY_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + InventoryTable.TABLE_NAME);
    }
}

package app.invictus.com.invectorycap1.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

import static app.invictus.com.invectorycap1.data.InventoryContract.*;

/**
 * Created by invictus on 12/27/16.
 */

public class InventoryContentProvider extends ContentProvider {

    private static final int INVENTORIES = 100;
    private static final int INVENTORIES_ID = 200;

    private InventorySQLiteHelper dbHelper;

    public static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(CONTENT_AUTHORITY, InventoryTable.PATH_INVENTORIES, INVENTORIES);
        uriMatcher.addURI(CONTENT_AUTHORITY, InventoryTable.PATH_INVENTORIES + "/#", INVENTORIES_ID);
    }


    @Override
    public boolean onCreate() {
        dbHelper = new InventorySQLiteHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projections, String selection, String[] selectionArgs, String sortOrder) {
        final int matcher = uriMatcher.match(uri);
        Cursor cursor;

        switch (matcher) {
            case INVENTORIES:
                cursor = dbHelper.getWritableDatabase()
                        .query(InventoryTable.TABLE_NAME,
                                projections,
                                selection,
                                selectionArgs,
                                null,
                                null,
                                sortOrder);
                break;

            case INVENTORIES_ID:
                selection = InventoryTable._ID + " =? ";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                cursor = dbHelper.getWritableDatabase()
                        .query(InventoryTable.TABLE_NAME,
                                projections,
                                selection, selectionArgs,
                                null,
                                null,
                                sortOrder);
                break;

            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        final int matcher = uriMatcher.match(uri);

        switch (matcher) {
            case INVENTORIES:
                return InventoryTable.CONTENT_LIST_TYPE;
            case INVENTORIES_ID:
                return InventoryTable.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("::Error, unrecognised uri " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long rowId = db.insert(InventoryTable.TABLE_NAME, null, contentValues);

        if (rowId != -1) {
            getContext().getContentResolver().notifyChange(uri, null);
            db.close();
            return ContentUris.withAppendedId(InventoryTable.CONTENT_URI, rowId);
        }
        throw new SQLException("Failed to add a record ::ERROR " + uri);
    }

    @Override
    public int delete(Uri uri, String where, String[] whereArgs) {
        final int matcher = uriMatcher.match(uri);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rowId;

        switch (matcher) {
            case INVENTORIES:
                rowId = db.delete(InventoryTable.TABLE_NAME, null, null);
                break;
            case INVENTORIES_ID:
                where = InventoryTable._ID + "=?";
                whereArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowId = db.delete(InventoryTable.TABLE_NAME, where, whereArgs);
                break;
            default:
                throw new IllegalArgumentException("unsupported " + uri);
        }

        if (rowId != -1) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        db.close();
        return rowId;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        final int matcher = uriMatcher.match(uri);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rowId;
        switch (matcher) {
            case INVENTORIES_ID:
                selection = InventoryTable._ID + " =?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowId = db.update(InventoryTable.TABLE_NAME,
                        contentValues,
                        selection,
                        selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unsupported " + uri);
        }
        if (rowId != -1) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowId;
    }

}

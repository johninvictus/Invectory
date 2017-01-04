package app.invictus.com.invectorycap1.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by invictus on 12/27/16.
 */

public class InventoryContract {

    public static final String CONTENT_AUTHORITY = "app.invictus.com.invectorycap1";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);


    /*
    * Invectory Table
    * **/
    public static final class InventoryTable implements BaseColumns {

        public static final String PATH_INVENTORIES = "inventories";
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_INVENTORIES);

        public static final String CONTENT_LIST_TYPE
                = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + PATH_INVENTORIES;

        public static final String CONTENT_ITEM_TYPE
                = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + PATH_INVENTORIES;


        public static final String NO_IMAGE = "no image";
        /*
        * Table data
        * **/
        public static final String TABLE_NAME = "inventory";

        public static final String COLUMN_INVENTORY_NAME = "name";
        public static final String COLUMN_INVENTORY_DESCRIPTION = "description";
        public static final String COLUMN_INVENTORY_IMAGE = "image";
        public static final String COLUMN_INVENTORY_QUANTITY = "quantity";
        public static final String COLUMN_INVENTORY_PRICE = "price";
    }
}

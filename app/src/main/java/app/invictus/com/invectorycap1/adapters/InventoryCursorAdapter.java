package app.invictus.com.invectorycap1.adapters;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import app.invictus.com.invectorycap1.R;
import app.invictus.com.invectorycap1.data.InventoryContract;

import static app.invictus.com.invectorycap1.data.InventoryContract.*;

/**
 * Created by invictus on 12/28/16.
 */

public class InventoryCursorAdapter extends CursorAdapter {

    private static final String LOG_TAG = InventoryCursorAdapter.class.getSimpleName();

    public InventoryCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.single_inventory_row, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        ImageView itemImage = (ImageView) view.findViewById(R.id.item_image);
        TextView itemTitle = (TextView) view.findViewById(R.id.item_title);
        final TextView itemQuantity = (TextView) view.findViewById(R.id.item_quantity);
        Button btnSell = (Button) view.findViewById(R.id.item_sell);

        String name = cursor.getString(cursor.getColumnIndex(InventoryTable.COLUMN_INVENTORY_NAME));
        itemTitle.setText(name);

        int quantity = cursor.getInt(cursor.getColumnIndex(InventoryTable.COLUMN_INVENTORY_QUANTITY));
        itemQuantity.setText(String.valueOf(quantity));

        final long _ID = cursor.getLong(cursor.getColumnIndex(InventoryTable._ID));

        final int position = cursor.getPosition();

        btnSell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cursor.moveToPosition(position);
                int tempQuantity = Integer.valueOf(itemQuantity.getText().toString());
                tempQuantity--;

                if (tempQuantity > 0) {
                    ContentValues values = new ContentValues();
                    values.put(InventoryTable.COLUMN_INVENTORY_QUANTITY, tempQuantity);

                    int rowID = context.getContentResolver()
                            .update(ContentUris.withAppendedId(InventoryTable.CONTENT_URI, _ID),
                                    values,
                                    null,
                                    null);
                }
            }
        });


        String imageString = cursor.getString(cursor.getColumnIndex(InventoryTable.COLUMN_INVENTORY_IMAGE));
        if (!imageString.equalsIgnoreCase(InventoryTable.NO_IMAGE)) {
            itemImage.setImageURI(Uri.parse(imageString));
        }

    }


}

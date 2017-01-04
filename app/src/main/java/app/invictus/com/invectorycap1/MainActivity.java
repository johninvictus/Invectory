package app.invictus.com.invectorycap1;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import app.invictus.com.invectorycap1.adapters.InventoryCursorAdapter;

import static app.invictus.com.invectorycap1.data.InventoryContract.*;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private ListView inventoryListView;
    private InventoryCursorAdapter adapter;
    private FloatingActionButton fab;

    private static final int ITEM_LOADER = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inventoryListView = (ListView) findViewById(R.id.inventory_list_view);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        adapter = new InventoryCursorAdapter(getApplicationContext(), null);

        /**
         * set empty view and adapter
         * */
        inventoryListView.setAdapter(adapter);

        getLoaderManager().initLoader(ITEM_LOADER, null, this);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        inventoryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                long _ID = adapter.getCursor().getLong(adapter.getCursor().getColumnIndex(InventoryTable._ID));
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                Uri currentUri = ContentUris.withAppendedId(InventoryTable.CONTENT_URI, _ID);

                intent.setData(currentUri);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_dummy:
                addDummyData();
                break;
            case R.id.action_setting:
                Toast.makeText(getApplicationContext(), "settings  menu", Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_delete_all:
                deleteAll();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteAll() {
        getContentResolver().delete(InventoryTable.CONTENT_URI, null, null);
    }

    private void addDummyData() {
        ContentValues values = new ContentValues();
        values.put(InventoryTable.COLUMN_INVENTORY_NAME, "Bread");
        values.put(InventoryTable.COLUMN_INVENTORY_DESCRIPTION, "The sweetest bread in EA");
        values.put(InventoryTable.COLUMN_INVENTORY_IMAGE, InventoryTable.NO_IMAGE);
        values.put(InventoryTable.COLUMN_INVENTORY_PRICE, "50");
        values.put(InventoryTable.COLUMN_INVENTORY_QUANTITY, "100");

        Uri uri = getContentResolver().insert(InventoryTable.CONTENT_URI, values);
        if (uri != null) {
            Toast.makeText(getApplicationContext(), "dummy data added", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = new String[]{
                InventoryTable._ID,
                InventoryTable.COLUMN_INVENTORY_NAME,
                InventoryTable.COLUMN_INVENTORY_DESCRIPTION,
                InventoryTable.COLUMN_INVENTORY_PRICE,
                InventoryTable.COLUMN_INVENTORY_QUANTITY,
                InventoryTable.COLUMN_INVENTORY_IMAGE
        };

        return new CursorLoader(this,
                InventoryTable.CONTENT_URI,
                projection,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        adapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }
}

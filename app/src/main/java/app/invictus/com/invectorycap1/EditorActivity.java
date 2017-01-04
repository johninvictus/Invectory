package app.invictus.com.invectorycap1;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import static app.invictus.com.invectorycap1.data.InventoryContract.*;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = EditorActivity.class.getSimpleName();
    Uri currentUri;

    /**
     * Identifier for the item data loader
     */
    private static final int ITEM_LOADER = 100;
    private static final int PICK_IMAGE_REQUEST = 0;
    private static final int SEND_MAIL_REQUEST = 1;

    EditText editTextPrice;
    EditText editTextQuantity;
    EditText editTextName;
    EditText editTextDescription;

    ImageButton imageButtonSelectImage;
    ImageView imageViewInventory;
    Button buttonorder;

    private Uri mUriImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        currentUri = getIntent().getData();
        if (currentUri != null) {
            setTitle(R.string.action_edit);
            invalidateOptionsMenu();
            getLoaderManager().initLoader(ITEM_LOADER, null, this);
        }

        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        editTextPrice = (EditText) findViewById(R.id.edit_text_price);
        editTextQuantity = (EditText) findViewById(R.id.edit_text_quanty);
        editTextName = (EditText) findViewById(R.id.edit_text_name);
        editTextDescription = (EditText) findViewById(R.id.edit_text_description);

        imageButtonSelectImage = (ImageButton) findViewById(R.id.btn_select_image);
        imageViewInventory = (ImageView) findViewById(R.id.image_view_inventory);
        buttonorder = (Button) findViewById(R.id.btn_reorder);

        imageButtonSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });

        buttonorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                
            }
        });
    }

    private void selectImage() {
        Intent intent;

        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
        } else {
            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
        }

        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        super.onActivityResult(requestCode, resultCode, resultData);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            // The document selected by the user won't be returned in the intent.
            // Instead, a URI to that document will be contained in the return intent
            // provided to this method as a parameter.  Pull that uri using "resultData.getData()"

            if (resultData != null) {
                mUriImage = resultData.getData();
                Log.i(LOG_TAG, "Uri: " + mUriImage.toString());
                imageViewInventory.setImageBitmap(getBitmapFromUri(mUriImage));
            }
        }
    }

    public Bitmap getBitmapFromUri(Uri uri) {

        if (uri == null || uri.toString().isEmpty())
            return null;

        // Get the dimensions of the View
        int targetW = imageViewInventory.getWidth();
        int targetH = imageViewInventory.getHeight();

        InputStream input = null;
        try {
            input = this.getContentResolver().openInputStream(uri);

            // Get the dimensions of the bitmap
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(input, null, bmOptions);
            input.close();

            int photoW = bmOptions.outWidth;
            int photoH = bmOptions.outHeight;

            // Determine how much to scale down the image
            int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

            // Decode the image file into a Bitmap sized to fill the View
            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = scaleFactor;
            bmOptions.inPurgeable = true;

            input = this.getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(input, null, bmOptions);
            input.close();
            return bitmap;

        } catch (FileNotFoundException fne) {
            Log.e(LOG_TAG, "Failed to load image.", fne);
            return null;
        } catch (Exception e) {
            Log.e(LOG_TAG, "Failed to load image.", e);
            return null;
        } finally {
            try {
                input.close();
            } catch (IOException ioe) {

            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem saveMenuItem = menu.findItem(R.id.action_save);
        MenuItem editMenuItem = menu.findItem(R.id.action_edit);
        MenuItem deleteMenuItem = menu.findItem(R.id.action_delete);

        if (currentUri != null) {
            saveMenuItem.setVisible(false);
        } else {
            editMenuItem.setVisible(false);
            deleteMenuItem.setVisible(false);

        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                save();
                break;
            case R.id.action_edit:
                save();
                break;
            case R.id.action_delete:
                deleteInventory();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteInventory() {
        AlertDialog.Builder builder = new AlertDialog.Builder(EditorActivity.this);
        builder.setTitle(R.string.delete_message)
                .setMessage(R.string.delete_message_m)
                .setPositiveButton(R.string.action_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setNegativeButton(R.string.action_delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        getContentResolver().delete(currentUri, null, null);
                        finish();
                    }
                });
        builder.create();
        builder.show();
    }

    private void save() {
        String price = editTextPrice.getText().toString();
        String quantity = editTextQuantity.getText().toString();
        String name = editTextName.getText().toString();
        String description = editTextDescription.getText().toString();

        if (TextUtils.isEmpty(price) || TextUtils.isEmpty(quantity) ||
                TextUtils.isEmpty(name) || TextUtils.isEmpty(description) || mUriImage == null) {
            Toast.makeText(getApplicationContext(), "fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        ContentValues values = new ContentValues();
        values.put(InventoryTable.COLUMN_INVENTORY_NAME, name);
        values.put(InventoryTable.COLUMN_INVENTORY_DESCRIPTION, description);
        values.put(InventoryTable.COLUMN_INVENTORY_IMAGE, mUriImage.toString());
        values.put(InventoryTable.COLUMN_INVENTORY_PRICE, Integer.valueOf(price));
        values.put(InventoryTable.COLUMN_INVENTORY_QUANTITY, Integer.valueOf(quantity));

        if (currentUri == null) {
            // save into a fresh db
            Uri insertUri = getContentResolver().insert(InventoryTable.CONTENT_URI, values);
            if (insertUri != null) {
                finish();
            } else {
                Toast.makeText(getApplicationContext(), "Error saving inventory", Toast.LENGTH_SHORT).show();
            }
        } else {
            int rows = getContentResolver().update(currentUri, values, null, null);

            if (rows != -1) {
                finish();
            } else {
                Toast.makeText(getApplicationContext(), "Error updating inventory", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        String[] projections = new String[]{
                InventoryTable._ID,
                InventoryTable.COLUMN_INVENTORY_NAME,
                InventoryTable.COLUMN_INVENTORY_DESCRIPTION,
                InventoryTable.COLUMN_INVENTORY_IMAGE,
                InventoryTable.COLUMN_INVENTORY_PRICE,
                InventoryTable.COLUMN_INVENTORY_QUANTITY
        };

        return new CursorLoader(this,
                currentUri,
                projections,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() == -1) {
            return;
        }

        if (cursor.moveToFirst()) {
            int priceColumnIndex = cursor.getColumnIndex(InventoryTable.COLUMN_INVENTORY_PRICE);
            int quantityColunIndex = cursor.getColumnIndex(InventoryTable.COLUMN_INVENTORY_QUANTITY);
            int nameColumnIndex = cursor.getColumnIndex(InventoryTable.COLUMN_INVENTORY_NAME);
            int descriptionColumnIndex = cursor.getColumnIndex(InventoryTable.COLUMN_INVENTORY_DESCRIPTION);
            int imageColumnIndex = cursor.getColumnIndex(InventoryTable.COLUMN_INVENTORY_IMAGE);

            int price = cursor.getInt(priceColumnIndex);
            int quantity = cursor.getInt(quantityColunIndex);
            String name = cursor.getString(nameColumnIndex);
            String description = cursor.getString(descriptionColumnIndex);
            String image = cursor.getString(imageColumnIndex);


            //set values
            editTextPrice.setText(String.valueOf(price));
            editTextQuantity.setText(String.valueOf(quantity));
            editTextName.setText(name);
            editTextDescription.setText(description);

            if (!image.equalsIgnoreCase(InventoryTable.NO_IMAGE)) {
                /*
                * set image from uri
                * */
                imageViewInventory.setImageURI(Uri.parse(image));
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        //set values
        editTextPrice.setText("");
        editTextQuantity.setText("");
        editTextName.setText("");
        editTextDescription.setText("");
        imageViewInventory.setImageURI(null);
    }
}

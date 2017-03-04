package fit.ultimate.task2;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseIntArray;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnTextChanged;

/**
 * Created by Pham on 3/3/2017.
 */

public class PickContactActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int CONTACT_LIST_LOADER = 1000;
    public static ImageLoader mImageLoader;
    public static ContactSearchAdapter contactSearchAdapter;
    public static ArrayList<Data> arrayListData;
    public static SparseIntArray sparseIntArrayPosition;
    @BindView(R.id.recycler_view_contact)
    public RecyclerView recyclerViewContactSearch;
    @BindView(R.id.recycler_view_contact_list)
    RecyclerView recyclerViewContactList;
    @BindView(R.id.edit_text_search)
    EditText editTextSearch;
    private ContactAdapter contactAdapter;
    private String mSearchTerm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_contact);
        ButterKnife.bind(this);

        mImageLoader = new ImageLoader(this, getListPreferredItemHeight()) {
            @Override
            protected Bitmap processBitmap(Object data) {
                // This gets called in a background thread and passed the data from
                // ImageLoader.loadImage().
                return loadContactPhotoThumbnail((String) data, getImageSize());
            }
        };

        // Set a placeholder loading image for the image loader
        mImageLoader.setLoadingImage(R.drawable.ic_contact_picture_holo_light);

        // Add a cache to the image loader
        mImageLoader.addImageCache(this.getSupportFragmentManager(), 0.1f);
        contactAdapter = new ContactAdapter(this, mImageLoader, recyclerViewContactSearch);
        recyclerViewContactList.setAdapter(contactAdapter);
        recyclerViewContactList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerViewContactList.setLayoutManager(linearLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerViewContactList.getContext(),
                linearLayoutManager.getOrientation());
        recyclerViewContactList.addItemDecoration(dividerItemDecoration);

        arrayListData = new ArrayList<>();
        contactSearchAdapter = new ContactSearchAdapter(this, mImageLoader);
        recyclerViewContactSearch.setAdapter(contactSearchAdapter);
        recyclerViewContactSearch.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        sparseIntArrayPosition = new SparseIntArray();

        getSupportLoaderManager().initLoader(CONTACT_LIST_LOADER, null, this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.contact_list_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add_contact:
                Intent data = new Intent();
                String text = "";
                for (int i = 0; i < this.arrayListData.size(); i++) {
                    text += arrayListData.get(i).getContactName() + "\n";
                }
                data.putExtra("contacts", text);
                if (getParent() == null) {
                    setResult(RESULT_OK, data);
                } else {
                    getParent().setResult(RESULT_OK, data);
                }
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnTextChanged(R.id.edit_text_search)
    public void onTextChanged(CharSequence text) {
        String newText = text.toString();
        String newFilter = !TextUtils.isEmpty(newText) ? newText : null;

        // Don't do anything if the filter is empty
        if (mSearchTerm == null && newFilter == null) {
            return;
        }

        // Don't do anything if the new filter is the same as the current filter
        if (mSearchTerm != null && mSearchTerm.equals(newFilter)) {
            return;
        }

        // Updates current filter to new filter
        mSearchTerm = newFilter;

        // Restarts the loader. This triggers onCreateLoader(), which builds the
        // necessary content Uri from mSearchTerm.
        getSupportLoaderManager().restartLoader(
                ContactAdapter.ContactsQuery.QUERY_ID, null, PickContactActivity.this);
    }


    private int getListPreferredItemHeight() {
        final TypedValue typedValue = new TypedValue();

        // Resolve list item preferred height theme attribute into typedValue
        this.getTheme().resolveAttribute(
                android.R.attr.listPreferredItemHeight, typedValue, true);

        // Create a new DisplayMetrics object
        final DisplayMetrics metrics = new android.util.DisplayMetrics();

        // Populate the DisplayMetrics
        this.getWindowManager().getDefaultDisplay().getMetrics(metrics);

        // Return theme value based on DisplayMetrics
        return (int) typedValue.getDimension(metrics);
    }

    private Bitmap loadContactPhotoThumbnail(String photoData, int imageSize) {


        // Instantiates an AssetFileDescriptor. Given a content Uri pointing to an image file, the
        // ContentResolver can return an AssetFileDescriptor for the file.
        AssetFileDescriptor afd = null;

        // This "try" block catches an Exception if the file descriptor returned from the Contacts
        // Provider doesn't point to an existing file.
        try {
            Uri thumbUri;
            // If Android 3.0 or later, converts the Uri passed as a string to a Uri object.
            if (Utils.hasHoneycomb()) {
                thumbUri = Uri.parse(photoData);
            } else {
                // For versions prior to Android 3.0, appends the string argument to the content
                // Uri for the Contacts table.
                final Uri contactUri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, photoData);

                // Appends the content Uri for the Contacts.Photo table to the previously
                // constructed contact Uri to yield a content URI for the thumbnail image
                thumbUri = Uri.withAppendedPath(contactUri, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
            }
            // Retrieves a file descriptor from the Contacts Provider. To learn more about this
            // feature, read the reference documentation for
            // ContentResolver#openAssetFileDescriptor.
            afd = this.getContentResolver().openAssetFileDescriptor(thumbUri, "r");

            // Gets a FileDescriptor from the AssetFileDescriptor. A BitmapFactory object can
            // decode the contents of a file pointed to by a FileDescriptor into a Bitmap.
            FileDescriptor fileDescriptor = afd.getFileDescriptor();

            if (fileDescriptor != null) {
                // Decodes a Bitmap from the image pointed to by the FileDescriptor, and scales it
                // to the specified width and height
                return ImageLoader.decodeSampledBitmapFromDescriptor(
                        fileDescriptor, imageSize, imageSize);
            }
        } catch (FileNotFoundException e) {
            // If the file pointed to by the thumbnail URI doesn't exist, or the file can't be
            // opened in "read" mode, ContentResolver.openAssetFileDescriptor throws a
            // FileNotFoundException.
            if (BuildConfig.DEBUG) {
                Log.d("PickContactActivity", "Contact photo thumbnail not found for contact " + photoData
                        + ": " + e.toString());
            }
        } finally {
            // If an AssetFileDescriptor was returned, try to close it
            if (afd != null) {
                try {
                    afd.close();
                } catch (IOException e) {
                    // Closing a file descriptor might cause an IOException if the file is
                    // already closed. Nothing extra is needed to handle this.
                }
            }
        }

        // If the decoding failed, returns null
        return null;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Uri contentUri;

        // There are two types of searches, one which displays all contacts and
        // one which filters contacts by a search query. If mSearchTerm is set
        // then a search query has been entered and the latter should be used.

        if (mSearchTerm == null) {
            // Since there's no search string, use the content URI that searches the entire
            // Contacts table
            contentUri = ContactAdapter.ContactsQuery.CONTENT_URI;
        } else {
            // Since there's a search string, use the special content Uri that searches the
            // Contacts table. The URI consists of a base Uri and the search string.
            contentUri =
                    Uri.withAppendedPath(ContactAdapter.ContactsQuery.FILTER_URI, Uri.encode(mSearchTerm));
        }

        // Returns a new CursorLoader for querying the Contacts table. No arguments are used
        // for the selection clause. The search string is either encoded onto the content URI,
        // or no contacts search string is used. The other search criteria are constants. See
        // the ContactsQuery interface.
        return new CursorLoader(this,
                contentUri,
                ContactAdapter.ContactsQuery.PROJECTION,
                ContactAdapter.ContactsQuery.SELECTION,
                null,
                ContactAdapter.ContactsQuery.SORT_ORDER);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        contactAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        contactAdapter.swapCursor(null);
    }

}

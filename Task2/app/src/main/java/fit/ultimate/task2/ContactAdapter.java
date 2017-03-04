package fit.ultimate.task2;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Pham on 18/2/2017.
 */

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> {
    private static final String LOG_TAG = ContactAdapter.class.getSimpleName();
    private static char firstChar = ']';
    private final Context context;
    private Cursor cursor;
    private ViewHolderUtil.SetOnClickListener listener;
    private ImageLoader mImageLoader;
    private RecyclerView mRecyclerViewContactSearch;

    public ContactAdapter(Context context, ImageLoader imageLoader, RecyclerView recyclerViewContactSearch) {
        this.context = context;
        this.mImageLoader = imageLoader;
        this.mRecyclerViewContactSearch = recyclerViewContactSearch;
    }

    @Override
    public ContactAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (parent instanceof RecyclerView) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_contact_list, parent, false);
            view.setFocusable(true);
            return new ViewHolder(view);
        } else {
            throw new RuntimeException("NOT BOUND TO RECYCLER_VIEW");
        }
    }

    @Override
    public void onBindViewHolder(final ContactAdapter.ViewHolder holder, final int position) {
        final int pos = position;
        cursor.moveToPosition(position);
        final String photoUri = cursor.getString(ContactsQuery.PHOTO_THUMBNAIL_DATA);
        final int contactId = cursor.getInt(ContactsQuery.ID);
        //return negative if the specified key is not mapped
        if (PickContactActivity.sparseIntArrayPosition.indexOfKey(contactId) < 0)
            PickContactActivity.sparseIntArrayPosition.put(contactId, -1);
        final String displayName = cursor.getString(ContactsQuery.DISPLAY_NAME);
        holder.textViewContactName.setText(String.format(Locale.ENGLISH, "%s", displayName));
        if (displayName.charAt(0) != firstChar) {
            firstChar = displayName.charAt(0);
            holder.textViewHeader.setText(String.valueOf(firstChar));
            holder.separatorContainer.setVisibility(View.VISIBLE);
        } else holder.separatorContainer.setVisibility(View.INVISIBLE);
        holder.setItemClickListener(listener);
        holder.checkboxPickContact.setChecked(PickContactActivity.sparseIntArrayPosition.get(contactId) != -1);
        holder.checkboxPickContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (((CheckBox) view).isChecked()) {
                    PickContactActivity.arrayListData.add(new Data(photoUri, displayName));
                    PickContactActivity.sparseIntArrayPosition.put(contactId, PickContactActivity.arrayListData.size() - 1);
                    PickContactActivity.contactSearchAdapter.swapArrayList(PickContactActivity.arrayListData);
                    if (PickContactActivity.arrayListData.size() > 3) {
                        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
                        int px = Math.round(176 * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
                        mRecyclerViewContactSearch.getLayoutParams().width = px;
                        mRecyclerViewContactSearch.requestLayout();
                    }
                } else {
                    if (PickContactActivity.sparseIntArrayPosition.get(contactId) != -1) {
                        if (PickContactActivity.arrayListData.size() == 1)
                            PickContactActivity.arrayListData.clear();
                        else {
                            PickContactActivity.arrayListData.remove(PickContactActivity.sparseIntArrayPosition.get(contactId));
                        }
                        PickContactActivity.sparseIntArrayPosition.put(contactId, -1);
                        PickContactActivity.contactSearchAdapter.swapArrayList(PickContactActivity.arrayListData);
                        if (PickContactActivity.arrayListData.size() < 3) {
                            mRecyclerViewContactSearch.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                        }
                    }
                }
            }
        });
//        holder.checkboxPickContact.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
//                if (isChecked) {
//                    PickContactActivity.arrayListData.add(photoUri);
//                    PickContactActivity.sparseIntArrayPosition[pos] = PickContactActivity.arrayListData.size()-1;
//                    PickContactActivity.contactSearchAdapter.swapArrayList(PickContactActivity.arrayListData);
//                } else {
//                    if(PickContactActivity.sparseIntArrayPosition[pos]!=0) {
//                        PickContactActivity.arrayListData.remove(PickContactActivity.sparseIntArrayPosition[pos]);
//                        PickContactActivity.sparseIntArrayPosition[pos] = 0;
//                        PickContactActivity.contactSearchAdapter.swapArrayList(PickContactActivity.arrayListData);
//                    }
//                }
//            }
//        });
        mImageLoader.loadImage(photoUri, holder.imageViewContact);
    }

    @Override
    public int getItemCount() {
        if (cursor != null) {
            return cursor.getCount();
        }
        return 0;
    }

    public void swapCursor(Cursor newCursor) {
        cursor = newCursor;
        notifyDataSetChanged();
    }

    public interface ContactsQuery {

        // An identifier for the loader
        final static int QUERY_ID = 1;

        // A content URI for the Contacts table
        final static Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;

        // The search/filter query Uri
        final static Uri FILTER_URI = ContactsContract.Contacts.CONTENT_FILTER_URI;

        // The selection clause for the CursorLoader query. The search criteria defined here
        // restrict results to contacts that have a display name and are linked to visible groups.
        // Notice that the search on the string provided by the user is implemented by appending
        // the search string to CONTENT_FILTER_URI.
        @SuppressLint("InlinedApi")
        final static String SELECTION =
                (Utils.hasHoneycomb() ? ContactsContract.Contacts.DISPLAY_NAME_PRIMARY : ContactsContract.Contacts.DISPLAY_NAME) +
                        "<>''" + " AND " + ContactsContract.Contacts.IN_VISIBLE_GROUP + "=1";

        // The desired sort order for the returned Cursor. In Android 3.0 and later, the primary
        // sort key allows for localization. In earlier versions. use the display name as the sort
        // key.
        @SuppressLint("InlinedApi")
        final static String SORT_ORDER =
                Utils.hasHoneycomb() ? ContactsContract.Contacts.SORT_KEY_PRIMARY : ContactsContract.Contacts.DISPLAY_NAME;

        // The projection for the CursorLoader query. This is a list of columns that the Contacts
        // Provider should return in the Cursor.
        @SuppressLint("InlinedApi")
        final static String[] PROJECTION = {

                // The contact's row id
                ContactsContract.Contacts._ID,

                // A pointer to the contact that is guaranteed to be more permanent than _ID. Given
                // a contact's current _ID value and LOOKUP_KEY, the Contacts Provider can generate
                // a "permanent" contact URI.
                ContactsContract.Contacts.LOOKUP_KEY,

                // In platform version 3.0 and later, the Contacts table contains
                // DISPLAY_NAME_PRIMARY, which either contains the contact's displayable name or
                // some other useful identifier such as an email address. This column isn't
                // available in earlier versions of Android, so you must use Contacts.DISPLAY_NAME
                // instead.
                Utils.hasHoneycomb() ? ContactsContract.Contacts.DISPLAY_NAME_PRIMARY : ContactsContract.Contacts.DISPLAY_NAME,

                // In Android 3.0 and later, the thumbnail image is pointed to by
                // PHOTO_THUMBNAIL_URI. In earlier versions, there is no direct pointer; instead,
                // you generate the pointer from the contact's ID value and constants defined in
                // android.provider.ContactsContract.Contacts.
                Utils.hasHoneycomb() ? ContactsContract.Contacts.PHOTO_THUMBNAIL_URI : ContactsContract.Contacts._ID,

                // The sort order column for the returned Cursor, used by the AlphabetIndexer
                SORT_ORDER,
        };

        // The query column numbers which map to each value in the projection
        final static int ID = 0;
        final static int LOOKUP_KEY = 1;
        final static int DISPLAY_NAME = 2;
        final static int PHOTO_THUMBNAIL_DATA = 3;
        final static int SORT_KEY = 4;
    }

    public interface ContactAdapterOnClickHandler {
        void onClick(int planId);
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.checkboxPickContact)
        CheckBox checkboxPickContact;
        @BindView(R.id.imageview_contact)
        ImageView imageViewContact;
        @BindView(R.id.textview_contact_name)
        TextView textViewContactName;
        @BindView(R.id.separatorContainer)
        FrameLayout separatorContainer;
        @BindView(R.id.textview_header)
        TextView textViewHeader;
        private ViewHolderUtil.SetOnClickListener listener;

        ViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setClickable(true);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onItemClick(getAdapterPosition());
                    }
                }
            });
        }

        void setItemClickListener(ViewHolderUtil.SetOnClickListener itemClickListener) {
            this.listener = itemClickListener;
        }

        @Override
        public void onClick(View view) {
        }
    }
}

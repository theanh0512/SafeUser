package fit.ultimate.task2;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Pham on 18/2/2017.
 */

public class ContactSearchAdapter extends RecyclerView.Adapter<ContactSearchAdapter.ViewHolder> {
    private static final String LOG_TAG = ContactSearchAdapter.class.getSimpleName();
    private final Context context;
    private ViewHolderUtil.SetOnClickListener listener;
    private ImageLoader mImageLoader;
    private ArrayList<Data> arrayListUri;

    public ContactSearchAdapter(Context context, ImageLoader imageLoader) {
        this.context = context;
        this.mImageLoader = imageLoader;
    }

    @Override
    public ContactSearchAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (parent instanceof RecyclerView) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_contact_search, parent, false);
            view.setFocusable(true);
            return new ViewHolder(view);
        } else {
            throw new RuntimeException("NOT BOUND TO RECYCLER_VIEW");
        }
    }

    @Override
    public void onBindViewHolder(final ContactSearchAdapter.ViewHolder holder, int position) {
        final String photoUri = arrayListUri.get(position).getUri();
        holder.setItemClickListener(listener);

        mImageLoader.loadImage(photoUri, holder.imageViewContactSearch);
    }

    @Override
    public int getItemCount() {
        if (arrayListUri != null) {
            return arrayListUri.size();
        }
        return 0;
    }

    public void swapArrayList(ArrayList<Data> arrayList) {
        this.arrayListUri = arrayList;
        notifyDataSetChanged();
    }

    public interface ContactAdapterOnClickHandler {
        void onClick(int planId);
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.imageview_contact_search)
        ImageView imageViewContactSearch;
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

package fit.ultimate.task1;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Pham on 18/2/2017.
 */

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {
    private static final String LOG_TAG = TaskAdapter.class.getSimpleName();
    private final Context context;
    private ArrayList<Task> arrayListTask;
    private String urlStringThumbnail = "http://mootask.com/taskcontroller/showtaskimagethumbnail?id=";
    private String urlStringLogo = "http://mootask.com/profilecontroller/showlogobyid?id=";

    public TaskAdapter(Context context) {
        this.context = context;
    }

    @Override
    public TaskAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (parent instanceof RecyclerView) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_task, parent, false);
            view.setFocusable(true);
            return new ViewHolder(view);
        } else {
            throw new RuntimeException("NOT BOUND TO RECYCLER_VIEW");
        }
    }

    @Override
    public void onBindViewHolder(final TaskAdapter.ViewHolder holder, int position) {
        String caption = arrayListTask.get(position).getCaption();
        int entityId = arrayListTask.get(position).getEntityId();
        holder.textViewDescription.setText(Html.fromHtml(caption));
        final String logoPath = urlStringLogo + String.valueOf(entityId);
        final String thumbnailPath = urlStringThumbnail + String.valueOf(entityId);
        try {
            Picasso.with(context).load(thumbnailPath).placeholder(R.drawable.ic_place_holder)
                    .error(R.drawable.ic_error_fallback).into(holder.imageViewThumbnail, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {
                    Picasso.with(context)
                            .load(thumbnailPath)
                            .into(holder.imageViewThumbnail, new Callback() {
                                @Override
                                public void onSuccess() {

                                }

                                @Override
                                public void onError() {
//                                        Log.v("Picasso","Could not fetch image");
                                }
                            });
                }
            });
            Picasso.with(context).load(logoPath).placeholder(R.drawable.ic_place_holder)
                    .error(R.drawable.ic_error_fallback).into(holder.imageViewLogo, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {
                    Picasso.with(context)
                            .load(logoPath)
                            .into(holder.imageViewLogo, new Callback() {
                                @Override
                                public void onSuccess() {

                                }

                                @Override
                                public void onError() {
//                                        Log.v("Picasso","Could not fetch image");
                                }
                            });
                }
            });
        } catch (IndexOutOfBoundsException e) {
        }
    }

    @Override
    public int getItemCount() {
        if (arrayListTask != null) {
            return arrayListTask.size();
        }
        return 0;
    }

    public void swapArrayList(ArrayList<Task> arrayList) {
        this.arrayListTask = arrayList;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.imageViewThumbnail)
        ImageView imageViewThumbnail;
        @BindView(R.id.textViewDescription)
        TextView textViewDescription;
        @BindView(R.id.imageViewLogo)
        ImageView imageViewLogo;

        ViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setClickable(true);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
        }

        @Override
        public void onClick(View view) {
        }
    }
}

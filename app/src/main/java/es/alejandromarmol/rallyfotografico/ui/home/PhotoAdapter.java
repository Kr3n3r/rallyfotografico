package es.alejandromarmol.rallyfotografico.ui.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import client.api.UsersApi;
import client.model.Photo;
import es.alejandromarmol.rallyfotografico.R;
import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder> {

    private final List<Photo> photoList;
    private final Context context;

    public PhotoAdapter(Context context, List<Photo> photoList) {
        this.context = context;
        this.photoList = photoList;
    }

    @NonNull
    @Override
    public PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_photo, parent, false);
        return new PhotoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoViewHolder holder, int position) {
        Photo item = photoList.get(position);

        holder.title.setText(item.getName());
        holder.subtitle.setText(item.getName());
        holder.author.setText(item.getOwner().toString());
        holder.button.setText(context.getString(R.string.view_details_title));

        Glide.with(context)
                .load(item.getImage().toString())
                .into(holder.image);

        holder.itemView.setOnClickListener(v -> {
            v.animate().scaleX(0.97f).scaleY(0.97f).setDuration(100)
                    .withEndAction(() -> v.animate().scaleX(1f).scaleY(1f).setDuration(100).start())
                    .start();
        });
    }

    @Override
    public int getItemCount() {
        return photoList.size();
    }

    static class PhotoViewHolder extends RecyclerView.ViewHolder {
        TextView title, subtitle, author;
        ShapeableImageView image;
        Button button;

        PhotoViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tvTitle);
            subtitle = itemView.findViewById(R.id.tvSubtitle);
            author = itemView.findViewById(R.id.tvAuthor);
            image = itemView.findViewById(R.id.ivPhoto);
            button = itemView.findViewById(R.id.btnDetails);
        }
    }
}

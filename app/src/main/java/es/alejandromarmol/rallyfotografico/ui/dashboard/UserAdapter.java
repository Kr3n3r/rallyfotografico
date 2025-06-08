package es.alejandromarmol.rallyfotografico.ui.dashboard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.List;

import client.model.Photo;
import client.model.User;
import es.alejandromarmol.rallyfotografico.R;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    public interface SubtitleProvider {
        String getSubtitle(User user);
    }

    public interface OnPhotoButtonClickListener {
        void onButtonClick(User user);
    }

    private final List<User> userList;
    private final Context context;
    private final String buttonText;
    private final OnPhotoButtonClickListener buttonClickListener;
    private final SubtitleProvider subtitleProvider;

    public UserAdapter(Context context, List<User> userList, String buttonText,
                       SubtitleProvider subtitleProvider, OnPhotoButtonClickListener listener) {
        this.context = context;
        this.userList = userList;
        this.buttonText = buttonText;
        this.subtitleProvider = subtitleProvider;
        this.buttonClickListener = listener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User item = userList.get(position);

        holder.title.setText(item.getUsername());
        holder.subtitle.setText(subtitleProvider.getSubtitle(item));
        holder.author.setText(subtitleProvider.getSubtitle(item));
        holder.button.setText(buttonText);

//        Glide.with(context)
//                .load(item.getImage().toString())
//                .into(holder.image);

        holder.button.setOnClickListener(v -> buttonClickListener.onButtonClick(item));

        holder.itemView.setOnClickListener(v -> {
            v.animate().scaleX(0.97f).scaleY(0.97f).setDuration(100)
                    .withEndAction(() -> v.animate().scaleX(1f).scaleY(1f).setDuration(100).start())
                    .start();
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView title, subtitle, author;
//        ShapeableImageView image;
        Button button;

        UserViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tvTitle);
            subtitle = itemView.findViewById(R.id.tvSubtitle);
            author = itemView.findViewById(R.id.tvSecondSubtitle);
//            image = itemView.findViewById(R.id.ivPhoto);
            button = itemView.findViewById(R.id.btnDetails);
        }
    }
}

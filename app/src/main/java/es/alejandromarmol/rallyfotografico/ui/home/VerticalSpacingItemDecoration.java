package es.alejandromarmol.rallyfotografico.ui.home;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class VerticalSpacingItemDecoration extends RecyclerView.ItemDecoration {
    private final int spacing;

    public VerticalSpacingItemDecoration(int spacing) {
        this.spacing = spacing;
    }

    @Override
    public void getItemOffsets(@NonNull android.graphics.Rect outRect, @NonNull View view,
                               @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        outRect.top = spacing;
    }
}
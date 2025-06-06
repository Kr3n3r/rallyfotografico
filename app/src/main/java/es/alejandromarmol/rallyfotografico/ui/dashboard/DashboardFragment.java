package es.alejandromarmol.rallyfotografico.ui.dashboard;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import client.api.PhotosApi;
import client.model.Photo;
import es.alejandromarmol.rallyfotografico.R;
import es.alejandromarmol.rallyfotografico.Session;
import es.alejandromarmol.rallyfotografico.Utils;
import es.alejandromarmol.rallyfotografico.databinding.FragmentDashboardBinding;
import es.alejandromarmol.rallyfotografico.ui.home.PhotoAdapter;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;
    private PhotoAdapter adapter;
    private List<Photo> photoList = new ArrayList<>();


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        DashboardViewModel dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Configurar RecyclerView
        binding.rvSubmittedPhotos.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new PhotoAdapter(getContext(), photoList, getString(R.string.delete_photo_button), photo -> String.format("Number of votes: %s", photo.getVotes()) , photo -> {
            try {
                PhotosApi photosApi = new PhotosApi();
                photosApi.getInvoker().setApiKeyPrefix("Token");
                photosApi.getInvoker().setApiKey(Session.getToken(this.getContext()));
                photosApi.photosDestroy(photo.getId());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        binding.rvSubmittedPhotos.setAdapter(adapter);

        Utils.loadPhotos(this, adapter, photoList);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
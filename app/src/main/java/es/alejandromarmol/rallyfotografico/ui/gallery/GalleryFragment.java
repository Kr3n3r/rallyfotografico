package es.alejandromarmol.rallyfotografico.ui.gallery;

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
import client.api.UsersApi;
import client.api.VotesApi;
import client.model.AuthToken;
import client.model.Photo;
import client.model.Vote;
import es.alejandromarmol.rallyfotografico.R;
import es.alejandromarmol.rallyfotografico.Session;
import es.alejandromarmol.rallyfotografico.databinding.FragmentGalleryBinding;
import es.alejandromarmol.rallyfotografico.ui.home.PhotoAdapter;
import es.alejandromarmol.rallyfotografico.ui.home.VerticalSpacingItemDecoration;

public class GalleryFragment extends Fragment {

    private FragmentGalleryBinding binding;
    private PhotoAdapter adapter;
    private List<Photo> photoList = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        GalleryViewModel galleryViewModel =
                new ViewModelProvider(this).get(GalleryViewModel.class);

        binding = FragmentGalleryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Configurar RecyclerView
        binding.recyclerPhotos.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new PhotoAdapter(getContext(), photoList, getString(R.string.vote_title), photo -> String.format("Number of votes: %s", photo.getVotes()) ,photo -> {
            voteForPhoto(URI.create(photo.getUri()),photo.getOwner());
        });
        binding.recyclerPhotos.setAdapter(adapter);
        binding.recyclerPhotos.addItemDecoration(new VerticalSpacingItemDecoration(24));

        loadPhotos();

        return root;
    }

    private void voteForPhoto(URI photoURI, URI photoAuthor) {
        new Thread(() -> {
            try {
                VotesApi votesApi = new VotesApi();
                votesApi.getInvoker().setApiKeyPrefix("Token");
                votesApi.getInvoker().setApiKey(Session.getToken(this.getContext()));
                Vote vote = new Vote();
                vote.setPhoto(photoURI);
                vote.setUser(photoAuthor);
                votesApi.votesCreate(vote);

                requireActivity().runOnUiThread(() -> {
                    loadPhotos();
                    Log.d("INFO", "Reloading photos");
                });


            } catch (Exception e) {
                Log.e("API_ERROR", "Error al votar", e);
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(getContext(), "Error al votar", Toast.LENGTH_SHORT).show()
                );
            }
        }).start();
    }

    private void loadPhotos() {
        new Thread(() -> {
            try {
                PhotosApi photosApi = new PhotosApi();
                photosApi.getInvoker().setApiKey(Session.getToken(this.getContext()));
                List<Photo> responsePhotos = photosApi.photosList(UUID.fromString(Session.getContest(this.getContext())));
                responsePhotos.sort((p1, p2) -> Integer.compare(Integer.parseInt(p2.getVotes()), Integer.parseInt(p1.getVotes())));

                // Actualizamos la lista y la UI desde el hilo principal
                requireActivity().runOnUiThread(() -> {
                    photoList.clear();
                    photoList.addAll(responsePhotos);
                    adapter.notifyDataSetChanged();
                    Log.d("INFO", "All the photos extracted");
                });

            } catch (Exception e) {
                Log.e("API_ERROR", "Error al obtener las fotos", e);
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(getContext(), "Error al cargar las fotos", Toast.LENGTH_SHORT).show()
                );
            }
        }).start();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
package es.alejandromarmol.rallyfotografico.ui.gallery;

import static android.app.ProgressDialog.show;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import android.net.Uri;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import client.ApiException;
import client.api.ContestsApi;
import client.api.PhotosApi;
import client.api.UsersApi;
import client.api.VotesApi;
import client.model.AuthToken;
import client.model.Contest;
import client.model.Photo;
import client.model.Vote;
import es.alejandromarmol.rallyfotografico.R;
import es.alejandromarmol.rallyfotografico.Session;
import es.alejandromarmol.rallyfotografico.Utils;
import es.alejandromarmol.rallyfotografico.databinding.FragmentGalleryBinding;
import es.alejandromarmol.rallyfotografico.ui.home.PhotoAdapter;
import es.alejandromarmol.rallyfotografico.ui.home.VerticalSpacingItemDecoration;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class GalleryFragment extends Fragment {

    private FragmentGalleryBinding binding;
    private PhotoAdapter adapter;
    private List<Photo> photoList = new ArrayList<>();

    private static final int PICK_IMAGE_REQUEST = 1;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private Uri selectedImageUri;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        GalleryViewModel galleryViewModel =
                new ViewModelProvider(this).get(GalleryViewModel.class);

        binding = FragmentGalleryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
//
        // Configurar RecyclerView
        binding.recyclerPhotos.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new PhotoAdapter(getContext(), photoList, getString(R.string.vote_title), photo -> String.format("Number of votes: %s", photo.getVotes()) ,photo -> {
            voteForPhoto(URI.create(photo.getUri()),photo.getOwner());
        });
        binding.recyclerPhotos.setAdapter(adapter);
        binding.recyclerPhotos.addItemDecoration(new VerticalSpacingItemDecoration(24));
        binding.fabAddPhoto.setOnClickListener(v -> {
            showUploadPhotoDialog();
        });

        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        selectedImageUri = result.getData().getData();
                        // Aquí puedes actualizar un ImageView o almacenar la URI para subirla después
                    }
                }
        );


        Utils.loadPhotos(this, adapter, photoList);

        return root;
    }

    private void showUploadPhotoDialog() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView = inflater.inflate(R.layout.dialog_upload_photo, null);

        EditText inputTitle = dialogView.findViewById(R.id.inputTitle);
        Spinner spinnerContests = dialogView.findViewById(R.id.inputContest);
        Button btnSelectImage = dialogView.findViewById(R.id.buttonSelectImage);
        TextView txtImageSelected = dialogView.findViewById(R.id.buttonSelectImage);

        loadContestsIntoSpinner(spinnerContests); // Ver siguiente paso

        btnSelectImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            imagePickerLauncher.launch(intent);
        });

        new AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.upload_photo_title))
                .setView(dialogView)
                .setPositiveButton(R.string.upload_photo, (dialog, which) -> {
                    String title = inputTitle.getText().toString();
//                    UUID contestId = (UUID) spinnerContests.getSelectedItem();
                    Contest contest = (Contest) spinnerContests.getSelectedItem();
                    ContestsApi contestsApi = new ContestsApi();
                    URI contestURI;
                    try {
                        contestsApi.getInvoker().setApiKey(Session.getToken(this.getContext()));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    try {
                        contestURI = contestsApi.getContestURI(contest.getId());
                    } catch (ExecutionException e) {
                        throw new RuntimeException(e);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    } catch (TimeoutException e) {
                        throw new RuntimeException(e);
                    } catch (ApiException e) {
                        throw new RuntimeException(e);
                    }

                    if (selectedImageUri != null) {
                        try {
                            File imageFile = getFileFromUri(requireContext(), selectedImageUri);
                            Photo photo = new Photo();
                            photo.setName(title);
                            photo.setContest(contestURI);
//                            photo.setOwnerName("admin");
                            uploadPhoto(imageFile, photo);
                        } catch (IOException e) {
                            Utils.showMessage(getContext(), getString(R.string.notification_error_procesing_photos), Utils.MessageType.ERROR);;
                        }
                    } else {
                        Utils.showMessage(getContext(), getString(R.string.notification_warn_select_photo), Utils.MessageType.WARN);
                    }
                })
                .setNegativeButton(getString(R.string.dialog_cancel_button), null)
                .show();
    }

    private void uploadPhoto(File imageFile, Photo photo) {
        new Thread(() -> {
            try {
                    PhotosApi photosApi = new PhotosApi();
                    photosApi.getInvoker().setApiKey(Session.getToken(this.getContext()));
                    photosApi.getInvoker().setApiKeyPrefix("Token");

                    photosApi.photosCreateWithImage(imageFile, photo, response -> {
                        Log.d("PHOTO", "Photo subida correctamente: " + response.getId());
                    },
                    error -> {
                        Log.e("PHOTO", "Error al subir photo", error);
                    });

                    requireActivity().runOnUiThread(() -> {
                        Utils.showMessage(getContext(), getString(R.string.notification_ok_uploading_photo), Utils.MessageType.OK);
                        Utils.loadPhotos(this, adapter, photoList);
                    });
            } catch (Exception e) {
                Log.e("UPLOAD_ERROR", "Error al subir foto", e);
                requireActivity().runOnUiThread(() ->
                        Utils.showMessage(getContext(), getString(R.string.notification_error_procesing_photos), Utils.MessageType.ERROR)
                );
            }
        }).start();
    }

//
    public File getFileFromUri(Context context, Uri uri) throws IOException {
        InputStream inputStream = context.getContentResolver().openInputStream(uri);
        File file = new File(context.getCacheDir(), "temp_image.jpg");
        OutputStream outputStream = new FileOutputStream(file);
        byte[] buffer = new byte[1024];
        int len;
        while ((len = inputStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, len);
        }
        outputStream.close();
        inputStream.close();
        return file;
    }

    private void loadContestsIntoSpinner(Spinner spinner) {
        new Thread(() -> {
            try {
                ContestsApi contestsApi = new ContestsApi();
                contestsApi.getInvoker().setApiKey(Session.getToken(getContext()));
                List<Contest> contestList = contestsApi.contestsList();

                requireActivity().runOnUiThread(() -> {
                    ArrayAdapter<Contest> adapter = new ArrayAdapter<>(requireContext(),
                            android.R.layout.simple_spinner_item, contestList);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner.setAdapter(adapter);
                });
            } catch (Exception e) {
                Log.e("API_ERROR", "Error al cargar concursos", e);
            }
        }).start();
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
                    Utils.loadPhotos(this, adapter, photoList);
                    Log.d("INFO", "Reloading photos");
                });


            } catch (Exception e) {
                Log.e("API_ERROR", "Error al votar", e);
                requireActivity().runOnUiThread(() ->
                        Utils.showMessage(getContext(), getContext().getString(R.string.notification_error_voting), Utils.MessageType.ERROR)
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
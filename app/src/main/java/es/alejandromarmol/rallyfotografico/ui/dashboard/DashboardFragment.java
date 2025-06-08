package es.alejandromarmol.rallyfotografico.ui.dashboard;

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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import client.ApiException;
import client.api.ContestsApi;
import client.api.PhotosApi;
import client.api.RolesApi;
import client.model.Contest;
import client.model.Photo;
import client.model.Role;
import client.model.User;
import es.alejandromarmol.rallyfotografico.R;
import es.alejandromarmol.rallyfotografico.Session;
import es.alejandromarmol.rallyfotografico.SignUpActivity;
import es.alejandromarmol.rallyfotografico.Utils;
import es.alejandromarmol.rallyfotografico.databinding.FragmentDashboardBinding;
import es.alejandromarmol.rallyfotografico.ui.home.PhotoAdapter;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;
    private PhotoAdapter adapter;
    private List<Photo> photoList = new ArrayList<>();
    private UserAdapter userAdapter;
    private List<User> userList = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        DashboardViewModel dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Configurar RecyclerView
        binding.rvSubmittedPhotos.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new PhotoAdapter(getContext(), photoList, getString(R.string.delete_photo_button), photo -> String.format("Number of votes: %s", photo.getVotes()) , photo -> {
            Utils.destroyPhoto(getParentFragment(),photo, null);
            // TODO : como recargar fotos?
            Utils.loadPhotos(this, adapter, photoList);
        });
        binding.rvSubmittedPhotos.setAdapter(adapter);

        binding.rvUsers.setLayoutManager(new LinearLayoutManager(getContext()));
//        userAdapter = new UserAdapter(getContext(), userList, getString(R.string.edit_user_button), user -> user.getGroups().get(0) , user -> {
        userAdapter = new UserAdapter(getContext(), userList, getString(R.string.edit_user_button), user -> user.getUsername() , user -> {
          showManageUserDialog(user);
        });
        binding.rvUsers.setAdapter(userAdapter);

        binding.btnManageGroups.setOnClickListener(v -> {
            showManageGroupsDialog();
        });

        Utils.loadPhotos(this, adapter, photoList);
        Utils.loadUsers(this, userAdapter, userList);

        return root;
    }

    private void showManageGroupsDialog() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView = inflater.inflate(R.layout.dialog_manage_groups, null);

        EditText inputGroupName = dialogView.findViewById(R.id.inputGroupName);
        Spinner spinner = dialogView.findViewById(R.id.groupSpinner);
        Button buttonCreateNewGroup = dialogView.findViewById(R.id.buttonCreateNewGroup);
        Button buttonUpdateExistingGroup = dialogView.findViewById(R.id.buttonUpdateExistingGroup);
        Button buttonDeleteGroup = dialogView.findViewById(R.id.buttonDeleteGroup);

        Utils.loadGroupsIntoSpinner(this, spinner); // Ver siguiente paso

        buttonCreateNewGroup.setOnClickListener( view -> {
            Utils.createNewGroup(getContext(),inputGroupName.getText().toString());
        });

        buttonDeleteGroup.setOnClickListener(view -> {
            String selectedText = spinner.getSelectedItem().toString();

            Pattern pattern = Pattern.compile("\\(id: (\\d+)\\)");
            Matcher matcher = pattern.matcher(selectedText);
            if (matcher.find()) {
                int groupId = Integer.parseInt(matcher.group(1));
                Utils.deleteGroup(getContext(), groupId);
            } else {
                Toast.makeText(getContext(), "No se pudo extraer el ID del grupo.", Toast.LENGTH_SHORT).show();
            }
        });

        new AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.manage_group_title))
                .setView(dialogView)
                .setNegativeButton(getString(R.string.dialog_cancel_button), null)
                .show();
    }

    private void showManageUserDialog(User user) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView = inflater.inflate(R.layout.dialog_manage_user, null);

        EditText inputUsername = dialogView.findViewById(R.id.inputUsername);
        EditText inputEmail = dialogView.findViewById(R.id.inputEmail);
        Button buttonSaveUser = dialogView.findViewById(R.id.buttonSaveUser);
        Button buttonManageRoles = dialogView.findViewById(R.id.buttonManageRoles);
        Button buttonResetPassword = dialogView.findViewById(R.id.buttonResetPassword);

        inputUsername.setText(user.getUsername());
        inputEmail.setText(user.getEmail());

        buttonSaveUser.setOnClickListener( view -> {
            Utils.updateUser(getContext(), user.getId(), inputUsername.getText().toString(), inputEmail.getText().toString());
        });

//        buttonCreateNewGroup.setOnClickListener( view -> {
//            Utils.createNewGroup(getContext(),inputGroupName.getText().toString());
//        });

        new AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.edit_user_title))
                .setView(dialogView)
                .setNegativeButton(getString(R.string.dialog_exit_button), null)
                .show();
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
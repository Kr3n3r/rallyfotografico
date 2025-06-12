package es.alejandromarmol.rallyfotografico.ui.dashboard;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
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
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import client.ApiException;
import client.api.ContestsApi;
import client.api.PhotosApi;
import client.api.RolesApi;
import client.api.UsersApi;
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
    private List<Role> roleList = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        DashboardViewModel dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Configurar RecyclerView
        binding.rvSubmittedPhotos.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new PhotoAdapter(getContext(), photoList, getString(R.string.delete_photo_button), photo -> String.format(getString(R.string.number_of_votes_subtitle)+ photo.getVotes()) , photo -> {
            Utils.destroyPhoto(getParentFragment(),photo, null);
            requireActivity().runOnUiThread(() -> {
                Utils.loadPhotos(this, adapter, photoList);
            });
        });
        binding.rvSubmittedPhotos.setAdapter(adapter);

        Utils.loadRoles(this, roleList);
        Utils.loadContest(this, new Utils.ContestCallback() {
            @Override
            public void onContestLoaded(Contest contest){
                loadDescription(contest);
                loadContestName(contest);
            }

            @Override
            public void onError(Exception e) {
                Utils.showMessage(getContext(), getString(R.string.notification_error_getting_contest), Utils.MessageType.ERROR);
            }
        });

        binding.btnEditContest.setOnClickListener(v -> {
            Utils.loadContest(this, new Utils.ContestCallback() {
                @Override
                public void onContestLoaded(Contest contest){
                    showContestDialog(contest);
                }

                @Override
                public void onError(Exception e) {
                    Utils.showMessage(getContext(), getString(R.string.notification_error_getting_contest), Utils.MessageType.ERROR);
                }
            });
        });

        binding.rvUsers.setLayoutManager(new LinearLayoutManager(getContext()));
        userAdapter = new UserAdapter(getContext(), userList, getString(R.string.edit_user_button), user -> {
            if (user.getGroups().size() != 0) {
                int roleId = Integer.parseInt(user.getGroups().get(0));
                return roleList.stream()
                        .filter(role -> role.getId() == roleId)
                        .map(Role::getName)
                        .findFirst()
                        .orElse(user.getUsername());
            }
            return user.getUsername();
        }, user -> {
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

    private void loadDescription(Contest contest) {
        new Thread(() -> {
            try {
                requireActivity().runOnUiThread(() -> {
                    String description = contest.getDescription();
                    binding.tvContestDescription.setText(description);
                    adapter.notifyDataSetChanged();
                });

            } catch (Exception e) {
                Log.e("ERROR", "Error getting description", e);
                requireActivity().runOnUiThread(() ->
                        Utils.showMessage( getContext(), getContext().getString(R.string.notification_error_getting_description), Utils.MessageType.ERROR)
                );
            }
        }).start();
    }

    private void loadContestName(Contest contest) {
        new Thread(() -> {
            try {
                requireActivity().runOnUiThread(() -> {
                    String contestName="";
                    contestName = contest.getName().toString();
                    binding.tvContestTitle.setText(contestName);
                    binding.tvDashboardTitle.setText(contestName);
                    adapter.notifyDataSetChanged();
                });

            } catch (Exception e) {
                Log.e("ERROR", "Error getting Contest name", e);
                requireActivity().runOnUiThread(() ->
                        Utils.showMessage(getContext(), getContext().getString(R.string.notification_error_getting_contest_name), Utils.MessageType.ERROR)
                );
            }
        }).start();
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
                Utils.showMessage(getContext(), getString(R.string.notification_error_getting_role_id), Utils.MessageType.ERROR);
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
        Button buttonDeleteUser = dialogView.findViewById(R.id.buttonDeleteUser);
        Button buttonManageRoles = dialogView.findViewById(R.id.buttonManageRoles);
        Button buttonResetPassword = dialogView.findViewById(R.id.buttonResetPassword);

        inputUsername.setText(user.getUsername());
        inputEmail.setText(user.getEmail());

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.edit_user_title))
                .setView(dialogView)
                .setNegativeButton(getString(R.string.dialog_exit_button), null)
                .create();

        buttonSaveUser.setOnClickListener( view -> {
            Utils.updateUser(getContext(), user.getId(), inputUsername.getText().toString(), inputEmail.getText().toString());
        });

        buttonDeleteUser.setOnClickListener( view -> {
            Utils.deleteUser(getContext(), user.getId());
            dialog.dismiss();
            requireActivity().runOnUiThread(() -> {
                Utils.loadUsers(this, userAdapter, userList);
            });
        });

        buttonManageRoles.setOnClickListener( view -> {
            showManageUserRolesDialog(user);
        });
//        buttonCreateNewGroup.setOnClickListener( view -> {
//            Utils.createNewGroup(getContext(),inputGroupName.getText().toString());
//        });

        dialog.show();
    }

    private void showContestDialog(Contest contest) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView = inflater.inflate(R.layout.dialog_manage_contest, null);

        EditText inputName = dialogView.findViewById(R.id.inputName);
        EditText inputDescription = dialogView.findViewById(R.id.inputDescription);
        EditText input_start_date = dialogView.findViewById(R.id.input_start_date);
        EditText input_end_date = dialogView.findViewById(R.id.input_end_date);
        EditText input_voting_start_date = dialogView.findViewById(R.id.input_voting_start_date);
        EditText input_voting_end_date = dialogView.findViewById(R.id.input_voting_end_date);
        EditText maxPhotoPerUserInput = dialogView.findViewById(R.id.maxPhotoPerUserInput);
        Button saveButton = dialogView.findViewById(R.id.buttonSaveContest);

        SimpleDateFormat displayFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        inputName.setText(contest.getName());
        inputDescription.setText(contest.getDescription());

        // Mostrar fechas en formato yyyy-MM-dd
        input_start_date.setText(displayFormat.format(contest.getStartDate()));
        input_end_date.setText(displayFormat.format(contest.getEndDate()));
        input_voting_start_date.setText(displayFormat.format(contest.getVotingStartDate()));
        input_voting_end_date.setText(displayFormat.format(contest.getVotingEndDate()));

        maxPhotoPerUserInput.setText(contest.getMaxPhotosPerUser().toString());

        setupDatePicker(input_start_date);
        setupDatePicker(input_end_date);
        setupDatePicker(input_voting_start_date);
        setupDatePicker(input_voting_end_date);

        saveButton.setOnClickListener(view -> {
            try {
                contest.setName(inputName.getText().toString());
                contest.setDescription(inputDescription.getText().toString());

                // Parsear fechas usando el mismo formato
                java.util.Date startDate = parser.parse(input_start_date.getText().toString());
                java.util.Date endDate = parser.parse(input_end_date.getText().toString());
                java.util.Date votingStartDate = parser.parse(input_voting_start_date.getText().toString());
                java.util.Date votingEndDate = parser.parse(input_voting_end_date.getText().toString());

                contest.setStartDate(new java.sql.Date(startDate.getTime()));
                contest.setEndDate(new java.sql.Date(endDate.getTime()));
                contest.setVotingStartDate(new java.sql.Date(votingStartDate.getTime()));
                contest.setVotingEndDate(new java.sql.Date(votingEndDate.getTime()));

                contest.setMaxPhotosPerUser(Long.parseLong(maxPhotoPerUserInput.getText().toString()));

                Utils.updateContest(getContext(), contest);

            } catch (ParseException e) {
                e.printStackTrace();
            }
        });

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.edit_contest_title))
                .setView(dialogView)
                .setNegativeButton(getString(R.string.dialog_exit_button), null)
                .create();

        dialog.show();
    }

    // Método para configurar DatePicker en un EditText
    private void setupDatePicker(EditText editText) {
        // Bloquear edición manual
        editText.setInputType(InputType.TYPE_NULL);
        editText.setFocusable(false);
        editText.setClickable(true);

        editText.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();

            // Intentar cargar la fecha que ya tenga el EditText si está bien formateada
            String currentText = editText.getText().toString();
            if (!currentText.isEmpty()) {
                try {
                    LocalDate date = LocalDate.parse(currentText);
                    calendar.set(date.getYear(), date.getMonthValue() - 1, date.getDayOfMonth());
                } catch (Exception e) {
                    // Ignorar, usar fecha actual
                }
            }

            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog picker = new DatePickerDialog(requireContext(), (view, y, m, d) -> {
                // Formatear fecha a ISO yyyy-MM-dd
                String selectedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", y, m + 1, d);
                editText.setText(selectedDate);
            }, year, month, day);

            picker.show();
        });
    }

    private void showManageUserRolesDialog(User user) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView = inflater.inflate(R.layout.dialog_user_roles, null);
        TableLayout rolesTable = dialogView.findViewById(R.id.rolesTable);

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.edit_user_title))
                .setView(dialogView)
                .setNegativeButton(getString(R.string.dialog_exit_button), null)
                .create();

        dialog.show();

        new Thread(() -> {
            try {
                RolesApi rolesApi = new RolesApi();
                rolesApi.getInvoker().setApiKeyPrefix("Token");
                rolesApi.getInvoker().setApiKey(Session.getToken(getContext()));
                List<Role> roleList = rolesApi.rolesList();

                UsersApi usersApi = new UsersApi();
                usersApi.getInvoker().setApiKeyPrefix("Token");
                usersApi.getInvoker().setApiKey(Session.getToken(getContext()));

                new Handler(Looper.getMainLooper()).post(() -> {
                    for (Role role : roleList) {
                        TableRow row = new TableRow(getContext());

                        TextView roleNameView = new TextView(getContext());
                        roleNameView.setText(role.getName());
                        roleNameView.setPadding(16, 16, 16, 16);

                        Button actionButton = new Button(getContext());
                        boolean hasRole = user.getGroups().contains(role.getId());
                        actionButton.setText(hasRole ? getString(R.string.delete_photo_button) : getString(R.string.assign));

                        actionButton.setOnClickListener(v -> {
                            boolean currentlyHasRole = user.getGroups().contains(role.getId());

                            if (currentlyHasRole) {
                                List<String> groups = user.getGroups();
                                groups.remove(Integer.valueOf(role.getId()));
                                user.setGroups(groups);
                                actionButton.setText(getString(R.string.assign));
                            } else {
                                List<String> groups = user.getGroups();
                                groups.add(role.getId().toString());
                                user.setGroups(groups);
                                actionButton.setText(getString(R.string.delete_photo_button));
                            }

                            // Actualizar el usuario en un hilo nuevo
                            new Thread(() -> {
                                try {
                                    usersApi.usersUpdate(user.getId(), user);
                                    new Handler(Looper.getMainLooper()).post(() -> {
                                        Utils.showMessage(getContext(), "Roles updated", Utils.MessageType.OK);
                                    });
                                } catch (Exception e) {
                                    new Handler(Looper.getMainLooper()).post(() -> {
                                        Utils.showMessage(getContext(), "Error updating user", Utils.MessageType.ERROR);
                                    });
                                }
                            }).start();
                        });

                        row.addView(roleNameView);
                        row.addView(actionButton);
                        rolesTable.addView(row);
                    }
                });

            } catch (Exception e) {
                new Handler(Looper.getMainLooper()).post(() -> {
                    Utils.showMessage(
                            getContext(),
                            getString(R.string.notification_error_retrieving_roles_body),
                            Utils.MessageType.ERROR
                    );
                });
            }
        }).start();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
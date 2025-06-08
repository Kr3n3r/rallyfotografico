package es.alejandromarmol.rallyfotografico;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import client.api.ContestsApi;
import client.api.PhotosApi;
import client.api.UsersApi;
import client.model.Contest;
import client.model.Photo;
import client.model.User;
import es.alejandromarmol.rallyfotografico.ui.home.PhotoAdapter;
import es.dmoral.toasty.Toasty;

public class Utils {
    public static void loadPhotos(Fragment fragment, PhotoAdapter adapter, List<Photo> photoList) {
        new Thread(() -> {
            try {
                PhotosApi photosApi = new PhotosApi();
                photosApi.getInvoker().setApiKey(Session.getToken(fragment.getContext()));
                List<Photo> responsePhotos = photosApi.photosList(UUID.fromString(Session.getContest(fragment.getContext())));
                responsePhotos.sort((p1, p2) -> Integer.compare(Integer.parseInt(p2.getVotes()), Integer.parseInt(p1.getVotes())));

                fragment.requireActivity().runOnUiThread(() -> {
                    photoList.clear();
                    photoList.addAll(responsePhotos);
                    adapter.notifyDataSetChanged();
                    Log.d("INFO", "All the photos extracted");
                });

            } catch (Exception e) {
                Log.e("API_ERROR", "Error al obtener las fotos", e);
                fragment.requireActivity().runOnUiThread(() ->
                        Utils.showMessage(fragment.getContext(), fragment.getContext().getString(R.string.notification_error_getting_photos), MessageType.ERROR)
                );
            }
        }).start();
    }

    public interface ContestCallback {
        void onContestLoaded(Contest contest);
        void onError(Exception e);
    }

    public static void loadContest(Fragment fragment, ContestCallback callback) {
        new Thread(() -> {
            try {
                ContestsApi contestsApi = new ContestsApi();
                contestsApi.getInvoker().setApiKey(Session.getToken(fragment.getContext()));
                Contest loadedContest = contestsApi.contestsList().get(0);
                Session.setContest(loadedContest, fragment.getContext());
                Log.d("INFO", loadedContest.toString());

                Contest finalContest = loadedContest;
                fragment.requireActivity().runOnUiThread(() -> {
                    callback.onContestLoaded(finalContest);
                });

            } catch (Exception e) {
                Log.e("API_ERROR", "Error getting contest", e);
                fragment.requireActivity().runOnUiThread(() ->
                        callback.onError(e)
                );
            }
        }).start();
    }

    public interface UserExistsCallback {
        void onResult(boolean exists);
    }

    public static void userExists(String username, UserExistsCallback callback) {
        new Thread(() -> {
            boolean exists = false;
            try {
                UsersApi api = new UsersApi();
                ArrayList<User> usersList = new ArrayList<>(api.usersList());
                for (User user : usersList) {
                    if (user.getUsername().equalsIgnoreCase(username)) {
                        exists = true;
                        break;
                    }
                }
            } catch (Exception e) {
                Log.e("API_ERROR", "Error getting users", e);
            }

            boolean finalExists = exists;
            new Handler(Looper.getMainLooper()).post(() -> {
                callback.onResult(finalExists);
            });
        }).start();
    }

    public interface PasswordIsValidCallback {
        void onResult(boolean isValid);
    }

    public static void passwordIsValid(String password, PasswordIsValidCallback callback) {
        new Thread(() -> {
            boolean valid = false;
            try {
                if (password.length() >= 8 &&
                        password.matches(".*[0-9].*") &&
                        password.matches(".*[^a-zA-Z0-9 ].*") &&
                        password.matches(".*[.,].*")) {
                    valid = true;
                }
            } catch (Exception e) {
                Log.e("ERROR", "Error validating password", e);
            }

            boolean isValid = valid;
            new Handler(Looper.getMainLooper()).post(() -> {
                callback.onResult(isValid);
            });
        }).start();
    }

    public enum MessageType {
        ERROR, WARN, INFO, OK
    }
    public static void showMessage(Context context, String message, MessageType messageType) {
        switch (messageType) {
            case OK:
                Toasty.success(context, message, Toast.LENGTH_SHORT, true).show();
                break;
            case ERROR:
                Toasty.error(context, message, Toast.LENGTH_SHORT, true).show();
                break;
            case WARN:
                Toasty.warning(context, message, Toast.LENGTH_SHORT, true).show();
                break;
            case INFO:
                Toasty.info(context, message, Toast.LENGTH_SHORT, true).show();
                break;

        }
    }

    public static void setupPasswordCheck(Context context, EditText passwordEditText) {
        Handler debounceHandler = new Handler(Looper.getMainLooper());
        final long DEBOUNCE_DELAY = 500; // miliseconds

        // Variable que necesita ser final (pero mutable)
        final Runnable[] debounceRunnable = new Runnable[1];

        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (debounceRunnable[0] != null) {
                    debounceHandler.removeCallbacks(debounceRunnable[0]);
                }

                debounceRunnable[0] = () -> {
                    String password = s.toString().trim();
                    if (!password.isEmpty()) {
                        Utils.passwordIsValid(password, isValid -> {
                            if (!isValid) {
                                passwordEditText.setError(context.getString(R.string.password_edit_text_error));
                            } else {
                                passwordEditText.setError(null);
                            }
                        });
                    } else {
                        passwordEditText.setError(null); // Limpiar si está vacío
                    }
                };

                debounceHandler.postDelayed(debounceRunnable[0], DEBOUNCE_DELAY);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    public static void setupUserCheck(Context context, EditText usernameEditText) {
        Handler debounceHandler = new Handler(Looper.getMainLooper());
        final long DEBOUNCE_DELAY = 500; // miliseconds

        // Variable que necesita ser final (pero mutable)
        final Runnable[] debounceRunnable = new Runnable[1];

        usernameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (debounceRunnable[0] != null) {
                    debounceHandler.removeCallbacks(debounceRunnable[0]);
                }

                debounceRunnable[0] = () -> {
                    String username = s.toString().trim();
                    if (!username.isEmpty()) {
                        Utils.userExists(username, exists -> {
                            if (exists) {
                                usernameEditText.setError(context.getString(R.string.username_edit_text_error));
                            } else {
                                usernameEditText.setError(null);
                            }
                        });
                    } else {
                        usernameEditText.setError(null); // Limpiar si está vacío
                    }
                };

                debounceHandler.postDelayed(debounceRunnable[0], DEBOUNCE_DELAY);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }


}

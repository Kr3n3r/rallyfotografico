package es.alejandromarmol.rallyfotografico;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

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

public class Utils {
    private static final String CHANNEL_ID = "rally_notification_channel";
    private static final String CHANNEL_NAME = "Notificaciones de Rally";
    private static final String CHANNEL_DESCRIPTION = "Mensajes emergentes para el usuario";

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
                        Utils.showNotification(fragment.getContext(), fragment.getContext().getString(R.string.notification_error_title), fragment.getContext().getString(R.string.notification_error_getting_photos))
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

    public static void showNotification(Context context, String title, String content) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            Log.w("Utils", "Permiso POST_NOTIFICATIONS no concedido");
            return;
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_background) // Asegúrate de tener un icono
                .setContentTitle(title)
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        notificationManager.notify(new Random().nextInt(), builder.build());
    }

    public static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = CHANNEL_NAME;
            String description = CHANNEL_DESCRIPTION;
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    }

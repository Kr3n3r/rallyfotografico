package es.alejandromarmol.rallyfotografico;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import client.ApiException;
import client.api.AuthApi;
import client.api.UsersApi;
import client.model.User;

public class SignUpActivity extends AppCompatActivity {

    EditText usernameEditText;
    EditText emailEditText;
    EditText passwordEditText;
    CheckBox checkBox;
    private Handler debounceHandler = new Handler(Looper.getMainLooper());
    private Runnable debounceRunnable;
    private static final long DEBOUNCE_DELAY = 500; // milisegundos
    private static final int REQUEST_NOTIFICATION_PERMISSION = 1;

    private void requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{android.Manifest.permission.POST_NOTIFICATIONS},
                        REQUEST_NOTIFICATION_PERMISSION
                );
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);
        requestNotificationPermissionIfNeeded();
        Utils.createNotificationChannel(this);

        Button submitButton = findViewById(R.id.btn_submit);
        TextView loginLink = findViewById(R.id.tv_login_link);

        usernameEditText = findViewById(R.id.et_fullname);
        emailEditText = findViewById(R.id.et_email);
        passwordEditText = findViewById(R.id.et_password);
        checkBox = findViewById(R.id.cb_terms);

        try {
            Session.checkTokenAndIntent(this,ContestActivity.class,this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        loginLink.setOnClickListener(v -> {
            Intent intent = new Intent(this, LogInActivity.class);
            startActivity(intent);
        });

        submitButton.setOnClickListener(v -> {
            if (validateInputs(checkBox)) signUp();
        });

        setupUsernameCheck(usernameEditText);
        setupPasswordCheck(passwordEditText);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.btn_submit), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private boolean validateInputs(CheckBox checkBox) {
        String username = usernameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (username.isEmpty()) {
            usernameEditText.setError(getString(R.string.username_empty_error));
            return false;
        }

        if (usernameEditText.getError() != null) {
            return false;
        }

        if (email.isEmpty()) {
            emailEditText.setError(getString(R.string.mail_empty_error));
            return false;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError(getString(R.string.mail_not_valid_error));
            return false;
        }

        if (password.isEmpty()) {
            passwordEditText.setError(getString(R.string.password_empty_error));
            return false;
        }

        if (passwordEditText.getError() != null) {
            return false;
        }

        if (!checkBox.isChecked()) {
            Utils.showNotification(this, getString(R.string.notification_error_title), getString(R.string.terms_not_accepted_error));
            return false;
        }

        return true;

    }
    private void signUp() {
        new Thread(() -> {
            try {
                UsersApi usersApi = new UsersApi();

                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                String email = emailEditText.getText().toString();

                User user = new User();
                user.setUsername(username);
                user.setPassword(password);
                user.setEmail(email);
                List<URI> groups = new ArrayList<>();
                user.setGroups(groups);

                usersApi.usersCreate(user);

                AuthApi authApi = new AuthApi();
                Session.setToken(authApi.authCreate(username, password, ""), this);

                runOnUiThread(() -> {
                    Utils.showNotification(this, getString(R.string.notification_succeeded_title), getString(R.string.notification_user_created_body));
                    // Solo aquí lanzamos el Intent
                    Intent intent = new Intent(this, LogInActivity.class);
                    startActivity(intent);
                });

            } catch (ApiException apiException) {
                runOnUiThread(() -> {
                    Utils.showNotification(this, getString(R.string.notification_error_title), getString(R.string.notification_error_creating_user_body));
                });
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    Utils.showNotification(this, getString(R.string.notification_error_title), getString(R.string.notification_unexpected_error_body));
                });

            }
        }).start();
    }

    private void setupUsernameCheck(EditText usernameEditText) {
        usernameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (debounceRunnable != null) {
                    debounceHandler.removeCallbacks(debounceRunnable);
                }

                debounceRunnable = () -> {
                    String username = s.toString().trim();
                    if (!username.isEmpty()) {
                        Utils.userExists(username, exists -> {
                            if (exists) {
                                usernameEditText.setError(getString(R.string.username_edit_text_error));
//                                usernameEditText.setBackgroundResource(R.drawable.edit_text_error_background);
                            } else {
                                usernameEditText.setError(null);
//                                usernameEditText.setBackgroundResource(R.drawable.edit_text_normal_background);
                            }
                        });
                    }
                };

                debounceHandler.postDelayed(debounceRunnable, DEBOUNCE_DELAY);
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Este método es obligatorio, aunque no se use
            }
        });
    }

    private void setupPasswordCheck(EditText passwordEditText) {
        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (debounceRunnable != null) {
                    debounceHandler.removeCallbacks(debounceRunnable);
                }

                debounceRunnable = () -> {
                    String password = s.toString().trim();
                    if (!password.isEmpty()) {
                        Utils.passwordIsValid(password, isValid -> {
                            if (!isValid) {
                                passwordEditText.setError(getString(R.string.password_edit_text_error));
//                                usernameEditText.setBackgroundResource(R.drawable.edit_text_error_background);
                            } else {
                                passwordEditText.setError(null);
//                                usernameEditText.setBackgroundResource(R.drawable.edit_text_normal_background);
                            }
                        });
                    }
                };

                debounceHandler.postDelayed(debounceRunnable, DEBOUNCE_DELAY);
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Este método es obligatorio, aunque no se use
            }
        });
    }
}
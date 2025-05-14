package es.alejandromarmol.rallyfotografico;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import client.ApiException;
import client.api.AuthApi;
import client.api.ContestsApi;
import client.api.PhotosApi;
import client.api.RolesApi;
import client.api.UsersApi;
import client.api.VotesApi;
import client.model.AuthToken;
import client.model.Contest;
import client.model.Role;
import client.model.User;
import client.model.Vote;

public class SignUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);

        Button submitButton = findViewById(R.id.btn_submit);
        TextView loginLink = findViewById(R.id.tv_login_link);
        EditText usernameEditText = findViewById(R.id.et_fullname);
        EditText emailEditText = findViewById(R.id.et_email);
        EditText passwordEditText = findViewById(R.id.et_password);

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
                        Toast.makeText(this, "Usuario creado correctamente", Toast.LENGTH_SHORT).show();
                        // Solo aquí lanzamos el Intent
                        Intent intent = new Intent(this, LogInActivity.class);
                        startActivity(intent);
                    });

                } catch (ApiException apiException) {
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Error de autenticación: " + apiException, Toast.LENGTH_SHORT).show();
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Error inesperado: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                }
            }).start();
        });


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.btn_submit), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public static void main(String[] args) {

    }
}
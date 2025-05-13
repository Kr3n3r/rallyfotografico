package es.alejandromarmol.rallyfotografico;

import android.content.Intent;
import android.os.Bundle;
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

import client.api.AuthApi;
import client.api.UsersApi;
import client.model.AuthToken;
import client.model.User;

public class LogInActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_log_in);

        Button submitButton = findViewById(R.id.btn_submit);
        TextView registerLink = findViewById(R.id.tv_login_link);
        EditText usernameEditText = findViewById(R.id.et_fullname);
        EditText passwordEditText = findViewById(R.id.et_password);

        registerLink.setOnClickListener(v -> {
            Intent intent = new Intent(this, SignUpActivity.class);
            startActivity(intent);
        });

        submitButton.setOnClickListener(v -> {
            new Thread(() -> {
                try {
                    // LOG IN
                    String username = usernameEditText.getText().toString();
                    String password = passwordEditText.getText().toString();
                    AuthApi authApi = new AuthApi();
                    AuthToken token = authApi.authCreate(username,password,"");
                    UsersApi usersApi = new UsersApi();
                    usersApi.getInvoker().setApiKey(token.getToken());
                    usersApi.usersList();
//                    Intent intent = new Intent(this, ContestActivity.class);
//                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                } finally {
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Se han cargado correctamente los usuarios", Toast.LENGTH_SHORT).show();
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
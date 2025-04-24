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
import client.api.ContestsApi;
import client.api.PhotosApi;
import client.api.RolesApi;
import client.api.UsersApi;
import client.api.VotesApi;
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

        loginLink.setOnClickListener(v -> {
            Intent intent = new Intent(this, ContestActivity.class);
            startActivity(intent);
        });

//        submitButton.setOnClickListener(v -> {
//            new Thread(() -> {
//                try {
//                    // REGISTER
//                    String username = usernameEditText.getText().toString();
//                    String email = emailEditText.getText().toString();
//                    String password = passwordEditText.getText().toString();
//                    UsersApi usersApi = new UsersApi();
//                    usersApi.getInvoker().setUsername("admin");
//                    usersApi.getInvoker().setPassword("admin");
//                    User newUser = new User();
//                    newUser.setUsername(username);
//                    newUser.setPassword(password);
//                    newUser.setEmail(email);
//                    //newUser.setGroups();
//                    List<URI> groups = new ArrayList<>();
//                    newUser.setGroups(groups);
//                    usersApi.usersCreate(newUser);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    runOnUiThread(() -> {
//                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                    });
//                }
//            }).start();
//
//        });


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.btn_submit), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public static void main(String[] args) {

    }
}
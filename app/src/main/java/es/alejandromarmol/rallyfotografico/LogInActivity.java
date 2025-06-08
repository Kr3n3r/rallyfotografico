package es.alejandromarmol.rallyfotografico;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import client.ApiException;
import client.api.AuthApi;
import client.api.UsersApi;
import client.model.AuthToken;
import client.model.User;
import okhttp3.internal.Util;

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
        CheckBox termsCheckBox = findViewById(R.id.cb_terms);

        try {
            Session.checkTokenAndIntent(this, ContestActivity.class,this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        registerLink.setOnClickListener(v -> {
            Intent intent = new Intent(this, SignUpActivity.class);
            startActivity(intent);
        });

        submitButton.setOnClickListener(v -> {
            if (!termsCheckBox.isChecked()) {
                Utils.showMessage(this, getString(R.string.terms_not_accepted_error), Utils.MessageType.WARN);
                return;
            }
            new Thread(() -> {
                try {
                    String username = usernameEditText.getText().toString();
                    String password = passwordEditText.getText().toString();
                    AuthApi authApi = new AuthApi();
                    AuthToken token = authApi.authCreate(username, password, "");

                    // Si llegamos aquí, la autenticación fue exitosa
                    Session.setToken(token, this);

                    runOnUiThread(() -> {
                        Utils.showMessage(this, getString(R.string.notification_user_successfully_auth_body), Utils.MessageType.OK);
                        // Solo aquí lanzamos el Intent
                        Intent intent = new Intent(this, ContestActivity.class);
                        startActivity(intent);
                    });

                } catch (ApiException apiException) {
                    runOnUiThread(() -> {
                        Utils.showMessage(this, getString(R.string.notification_error_authenticating_user_body), Utils.MessageType.ERROR);
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(() -> {
                        Utils.showMessage(this, getString(R.string.notification_unexpected_error_body), Utils.MessageType.WARN);
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
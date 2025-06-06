package es.alejandromarmol.rallyfotografico;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;

import client.model.AuthToken;
import client.model.Contest;

public class Session {

    private static final String SHARED_PREFERENCES_NAME = "auth";
    private static final String TOKEN_KEY = "token";
    private static final String CONTEST_KEY = "contest";

    private static final Class<?> NO_TOKEN_ACTIVITY = LauncherActivity.class;

    private static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    public static String getToken(Context context) throws Exception {
        String token = getPrefs(context).getString(TOKEN_KEY, null);
        if (TextUtils.isEmpty(token)) {
            throw new Exception("No token found.");
        }
        return token;
    }

    public static void setToken(AuthToken token, Context context) throws Exception {
        if (token == null) {
            throw new Exception("Invalid token.");
        }
        getPrefs(context).edit().putString(TOKEN_KEY, token.getToken()).apply();
    }

    public static String getContest(Context context) throws Exception {
        String contest = getPrefs(context).getString(CONTEST_KEY, null);
        if (TextUtils.isEmpty(contest)) {
            throw new Exception("No contest found.");
        }
        return contest;
    }

    public static void setContest(Contest contest, Context context) throws Exception {
        if (contest == null || TextUtils.isEmpty(contest.getId().toString())) {
            throw new Exception("Invalid contest.");
        }
        getPrefs(context).edit().putString(CONTEST_KEY, contest.getId().toString()).apply();
    }

    public static void checkTokenAndIntent(Context context, Class<?> targetActivity, Activity currentActivity) {
        try {
            getToken(context); // Verifica si hay token
            if (!currentActivity.getClass().equals(targetActivity)) {
                Intent intent = new Intent(context, targetActivity);
                currentActivity.startActivity(intent);
                currentActivity.finish();
            }
        } catch (Exception e) {
            // Redirige solo si no estamos ya en la NO_TOKEN_ACTIVITY
            if (!currentActivity.getClass().equals(NO_TOKEN_ACTIVITY) &&
                    !currentActivity.getClass().equals(SignUpActivity.class) &&
                    !currentActivity.getClass().equals(LogInActivity.class)) {
                Intent intent = new Intent(context, NO_TOKEN_ACTIVITY);
                currentActivity.startActivity(intent);
                currentActivity.finish();
            }
        }
    }
}

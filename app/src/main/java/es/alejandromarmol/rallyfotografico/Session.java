package es.alejandromarmol.rallyfotografico;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import client.model.AuthToken;
import client.model.Contest;

public class Session {
    private static final String SHARED_PREFERENCES_NAME = "auth";
    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor sharedPreferencesEditor;

    private static final String TOKEN_NAME = "token";
    private static String TOKEN = "";
    private static final String CONTEST_NAME = "contest";
    private static String CONTEST = "";

    public static String getToken(Context context) throws Exception {
        try{
            sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
            TOKEN = sharedPreferences.getString(TOKEN_NAME,null);
            if (TOKEN == null || TOKEN.equals("")) throw new Exception("No token found.");
            return TOKEN;
        } catch (Exception e){
            throw new Exception(e);
        }
    }

    public static void setToken(AuthToken token, Context context) throws Exception {
        if (token.toString().isEmpty() || token.equals("")) throw new Exception("No token received.");
        try{
            sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
            sharedPreferencesEditor = sharedPreferences.edit();
            sharedPreferencesEditor.putString(TOKEN_NAME, token.getToken());
            sharedPreferencesEditor.apply();
            TOKEN = token.getToken();
        } catch (Exception e){
            throw new Exception(e);
        }
    }

    public static String getContest(Context context) throws Exception {
        try{
            sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
            CONTEST = sharedPreferences.getString(CONTEST_NAME,null);
            if (CONTEST == null || CONTEST.equals("")) throw new Exception("No contest found.");
            return CONTEST;
        } catch (Exception e){
            throw new Exception(e);
        }
    }

    public static void setContest(Contest contest, Context context) throws Exception {
        if (contest.toString().isEmpty() || contest.equals("")) throw new Exception("No contest received.");
        try{
            sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
            sharedPreferencesEditor = sharedPreferences.edit();
            sharedPreferencesEditor.putString(CONTEST_NAME, contest.getId().toString());
            sharedPreferencesEditor.apply();
            CONTEST = getContest(context);
        } catch (Exception e){
            throw new Exception(e);
        }
    }

    public static void checkTokenAndIntent(Context context, Class<?> cls, Activity activity) throws Exception {
        try{
            getToken(context);
            Intent intent = new Intent(context,cls);
            activity.startActivity(intent);
        } catch (Exception e){
            throw new Exception(e);
        }
    }

}

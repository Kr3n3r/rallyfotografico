package es.alejandromarmol.rallyfotografico.ui.notifications;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import client.model.AuthToken;
import es.alejandromarmol.rallyfotografico.LauncherActivity;
import es.alejandromarmol.rallyfotografico.Session;
import es.alejandromarmol.rallyfotografico.databinding.FragmentNotificationsBinding;

public class NotificationsFragment extends Fragment {

    private FragmentNotificationsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        NotificationsViewModel notificationsViewModel =
                new ViewModelProvider(this).get(NotificationsViewModel.class);

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textNotifications;
        notificationsViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        try {
            AuthToken authToken = new AuthToken();
            authToken.setToken("");
            Session.setToken(authToken,this.getContext());
            Session.checkTokenAndIntent(getContext(), LauncherActivity.class, getActivity());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
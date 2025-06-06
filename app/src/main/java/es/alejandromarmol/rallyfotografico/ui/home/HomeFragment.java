package es.alejandromarmol.rallyfotografico.ui.home;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import client.ApiException;
import client.ApiInvoker;
import client.api.ContestsApi;
import client.api.PhotosApi;
import client.api.UsersApi;
import client.api.VotesApi;
import client.model.Contest;
import client.model.Photo;

import client.model.User;
import client.model.Vote;
import es.alejandromarmol.rallyfotografico.R;
import es.alejandromarmol.rallyfotografico.Session;
import es.alejandromarmol.rallyfotografico.Utils;
import es.alejandromarmol.rallyfotografico.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private PhotoAdapter adapter;
    private List<Photo> photoList = new ArrayList<>();
    private Contest contest;
    private String contestName, submissionDeadline, votingDeadline = new String();
    private TextView countdownTextView;
    private CountDownTimer countDownTimer;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Configurar RecyclerView
        binding.recyclerPhotos.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new PhotoAdapter(getContext(), photoList, getString(R.string.view_details_title), photo -> photo.getName() ,null);
        binding.recyclerPhotos.setAdapter(adapter);
        binding.recyclerPhotos.addItemDecoration(new VerticalSpacingItemDecoration(24));

        // Load contest from API
        Utils.loadContest(this, new Utils.ContestCallback() {
            @Override
            public void onContestLoaded(Contest contest){
                loadContestName(contest);
                loadSubmissionDeadline(contest);
                loadVotingDeadline(contest);
                countdownTextView = binding.tvCountdown;
                String date = contest.getEndDate().toString();
                startCountdownTo(date);
            }

            @Override
            public void onError(Exception e) {
                Utils.showNotification(getContext(), getString(R.string.notification_error_title), getString(R.string.notification_error_getting_contest));
            }
        });

        Utils.loadPhotos(this, adapter, photoList);

        return root;
    }

    private void startCountdownTo(String targetDateTime) {
        long endTimeMillis = parseDateToMillis(targetDateTime);
        long currentTimeMillis = System.currentTimeMillis();
        long millisUntilFinished = endTimeMillis - currentTimeMillis;

        if (millisUntilFinished > 0) {
            countDownTimer = new CountDownTimer(millisUntilFinished, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    long seconds = millisUntilFinished / 1000;
                    long hours = seconds / 3600;
                    long minutes = (seconds % 3600) / 60;
                    long secs = seconds % 60;

                    String timeFormatted = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, secs);
                    countdownTextView.setText(timeFormatted);
                }

                @Override
                public void onFinish() {
                    countdownTextView.setText("00:00:00");
                }
            };

            countDownTimer.start();
        } else {
            countdownTextView.setText("00:00:00");
        }
    }

    private long parseDateToMillis(String dateTimeString) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.getDefault());
            Date targetDate = sdf.parse(dateTimeString);
            return targetDate.getTime();
        } catch (Exception e) {
            e.printStackTrace();
            return System.currentTimeMillis();
        }
    }

    private void loadSubmissionDeadline(Contest contest) {
        new Thread(() -> {
            try {
                // Actualizamos la lista y la UI desde el hilo principal
                requireActivity().runOnUiThread(() -> {
                    this.submissionDeadline="";
                    submissionDeadline = contest.getEndDate().toString();
                    binding.submissionDeadline.setText(this.submissionDeadline);
                    adapter.notifyDataSetChanged();
                    Log.d("INFO", "Submission deadline setted correctly");
                });

            } catch (Exception e) {
                Log.e("ERROR", "Error getting contest deadline", e);
                requireActivity().runOnUiThread(() ->
                        Utils.showNotification(getContext(), getContext().getString(R.string.notification_error_title), getContext().getString(R.string.notification_error_getting_contest_deadline))
                );
            }
        }).start();
    }

    private void loadVotingDeadline(Contest contest) {
        new Thread(() -> {
            try {
                requireActivity().runOnUiThread(() -> {
                    this.votingDeadline="";
                    votingDeadline = contest.getVotingEndDate().toString();
                    binding.votingDeadline.setText(this.votingDeadline);
                    adapter.notifyDataSetChanged();
                    Log.d("INFO", "Submission voting deadline setted correctly");
                });

            } catch (Exception e) {
                Log.e("ERROR", "Error getting voting deadline", e);
                requireActivity().runOnUiThread(() ->
                        Utils.showNotification(getContext(), getContext().getString(R.string.notification_error_title), getContext().getString(R.string.notification_error_getting_voting_deadline))
                );
            }
        }).start();
    }

    private void loadContestName(Contest contest) {
        new Thread(() -> {
            try {
                requireActivity().runOnUiThread(() -> {
                    this.contestName="";
                    contestName = contest.getName().toString();
                    binding.tvContestTitle.setText(this.contestName);
                    adapter.notifyDataSetChanged();
                    Log.d("INFO", "Contest name setted correctly");
                });

            } catch (Exception e) {
                Log.e("ERROR", "Error getting Contest name", e);
                requireActivity().runOnUiThread(() ->
                        Utils.showNotification(getContext(), getContext().getString(R.string.notification_error_title), getContext().getString(R.string.notification_error_getting_contest_name))
                );
            }
        }).start();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        binding = null;
    }
}

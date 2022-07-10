package com.example.eventnotes.activity;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.eventnotes.R;
import com.example.eventnotes.adapter.EventAdapter;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EventFragment extends Fragment {
    private static final JsonFactory jsonFactory = GsonFactory.getDefaultInstance();
    GoogleAccountCredential credential;
    private static final String TAG = "EventFragment";
    RecyclerView rvEventList;
    FloatingActionButton addEventBtn;
    NavController navController;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_event, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        NetHttpTransport httpTransport = new NetHttpTransport();
        DateTime now = new DateTime(System.currentTimeMillis());
        rvEventList = view.findViewById(R.id.rv_event_list);
        addEventBtn = view.findViewById(R.id.add_event);
        navController = Navigation.findNavController(view);

        addEventBtn.setOnClickListener(v -> {
            navController.navigate(R.id.action_eventFragment_to_addEventFragment);
        });
        EventAdapter adapter = new EventAdapter(requireContext());
        rvEventList.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        try {
            httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(requireActivity());
        ArrayList<String> scopes = new ArrayList<>();
        scopes.add("https://www.googleapis.com/auth/calendar");
        scopes.add("https://www.googleapis.com/auth/userinfo.email");
        scopes.add("https://www.googleapis.com/auth/userinfo.profile");
        scopes.add("openid");

        credential = GoogleAccountCredential.usingOAuth2(requireContext(), scopes);
//        SharedPreferences settings = requireActivity().getPreferences(Context.MODE_PRIVATE);
//        credential.setSelectedAccountName(settings.getString(PREF_ACCOUNT_NAME, null));
        credential.setSelectedAccount(account.getAccount());
        if (account.getAccount() == null) {
            Toast.makeText(requireContext(), "Invalid account", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(requireContext(), "Valid account", Toast.LENGTH_SHORT).show();
        }
        // Tasks client
        Calendar service = new Calendar.Builder(httpTransport, jsonFactory, credential)
                .setApplicationName("Google-TasksAndroidSample/1.0").build();

        final Events[] events = {null};
        ExecutorService service1 = Executors.newSingleThreadExecutor();
        service1.execute(() -> {
            Looper.prepare();
            try {
                requireActivity().runOnUiThread(() -> Toast.makeText(requireContext(), "Events fetching", Toast.LENGTH_SHORT).show());
                events[0] = service.events().list(account.getEmail())
                        .setMaxResults(10)
                        .setTimeMin(now)
                        .setOrderBy("startTime")
                        .setSingleEvents(true)
                        .execute();
                Event e = new Event();
            } catch(IOException e) {
                e.printStackTrace();
            }
            requireActivity().runOnUiThread(() -> {
                if (events[0] != null) {
                    Toast.makeText(requireContext(), "Events fetched", Toast.LENGTH_SHORT).show();
                    List<Event> items = events[0].getItems();
                    adapter.setEvents(items);
                    rvEventList.setAdapter(adapter);
                    StringBuilder eventString = new StringBuilder("Events");
                    for(Event e: items) {
                        eventString.append("\n").append(e.getSummary());
                        Log.w(TAG, "onViewCreated: start=" + e.getStart());
                    }
                } else {
                    Toast.makeText(requireContext(), "Events not fetched", Toast.LENGTH_SHORT).show();
                }
            });
        });

    }
}
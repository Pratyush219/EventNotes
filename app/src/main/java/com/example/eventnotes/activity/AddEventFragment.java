package com.example.eventnotes.activity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.eventnotes.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Events;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AddEventFragment extends Fragment {
    private static final String TAG = "AddEventFragment";
    private static final JsonFactory jsonFactory = GsonFactory.getDefaultInstance();
    GoogleAccountCredential credential;
    EditText etDate, etStartTime, etEndTime, etSummary, etDescription;
    private long startTime = 0, endTime = 0;
    private int startHour, endHour, startMinute, endMinute;
    private Button btnCreate;
    private java.util.Calendar calendar;
    private boolean dateSet = false, startSet = false, endSet = false;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_event, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        NetHttpTransport httpTransport = new NetHttpTransport();
        DateTime now = new DateTime(System.currentTimeMillis());
        etSummary = view.findViewById(R.id.et_summary);
        etDescription = view.findViewById(R.id.et_description);
        etDate = view.findViewById(R.id.et_date);
        etStartTime = view.findViewById(R.id.et_start_time);
        etEndTime = view.findViewById(R.id.et_end_time);
        btnCreate = view.findViewById(R.id.btn_create);
        etStartTime.setShowSoftInputOnFocus(false);
        etEndTime.setShowSoftInputOnFocus(false);
        etDate.setShowSoftInputOnFocus(false);
        try {
            httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }
        etDate.setOnClickListener(v -> {
            showDateDialog();
        });
        etStartTime.setOnClickListener(v -> {
            showTimeDialog(etStartTime, 0);
        });
        etEndTime.setOnClickListener(v -> {
            showTimeDialog(etEndTime, 1);
        });
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
        Calendar service = new Calendar.Builder(httpTransport, jsonFactory, credential)
                .setApplicationName("Google-TasksAndroidSample/1.0").build();

        if (service.events() == null) {
            Toast.makeText(requireContext(), "Invalid request", Toast.LENGTH_SHORT).show();
        }
        ExecutorService service1 = Executors.newSingleThreadExecutor();
        btnCreate.setOnClickListener(v -> {
            if(isFilled(etSummary) && isFilled(etDescription) && isFilled(etDate) && isFilled(etStartTime) && isFilled(etEndTime)) {
                final Event[] event = {new Event()
                        .setSummary(etSummary.getText().toString())
                        .setDescription(etDescription.getText().toString())};

                DateTime startDateTime = new DateTime(startTime);
                EventDateTime start = new EventDateTime()
                        .setDateTime(startDateTime)
                        .setTimeZone("Asia/Kolkata");
                event[0].setStart(start);

                DateTime endDateTime = new DateTime(endTime);
                EventDateTime end = new EventDateTime()
                        .setDateTime(endDateTime)
                        .setTimeZone("Asia/Kolkata");
                event[0].setEnd(end);
                String calendarId = account.getEmail();
                service1.execute(() -> {
                    try {
                        event[0] = service.events().insert(calendarId, event[0]).execute();
                        Log.w(TAG, "onViewCreated: Event insertion=success " + event[0].getHtmlLink());
                        requireActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                requireActivity().onBackPressed();
                            }
                        });
                    } catch (IOException e) {
                        Log.w(TAG, "onViewCreated: Event insertion=failed");
                        e.printStackTrace();
                    }
                });
            } else {
                Toast.makeText(requireContext(), "Please fill all the details", Toast.LENGTH_SHORT).show();
            }
            
        });
    }
    private void showTimeDialog(EditText etTime, int i) {
        if(!dateSet && !startSet && !endSet) {
            calendar = java.util.Calendar.getInstance();
            startHour = calendar.get(java.util.Calendar.HOUR_OF_DAY);
            startMinute = calendar.get(java.util.Calendar.MINUTE);
            endHour = calendar.get(java.util.Calendar.HOUR_OF_DAY);
            endMinute = calendar.get(java.util.Calendar.MINUTE);
        } else if(i == 0){
            calendar.set(java.util.Calendar.HOUR_OF_DAY, startHour);
            calendar.set(java.util.Calendar.MINUTE, startMinute);
        } else if(i == 1){
            calendar.set(java.util.Calendar.HOUR_OF_DAY, endHour);
            calendar.set(java.util.Calendar.MINUTE, endMinute);
        }
        Log.w(TAG, "showTimeDialog: calendar data=" + calendar.get(java.util.Calendar.HOUR_OF_DAY) + " " + calendar.get(java.util.Calendar.MINUTE));
        TimePickerDialog.OnTimeSetListener timeListener = (timePicker, hour, minute) -> {
            calendar.set(java.util.Calendar.HOUR_OF_DAY, hour);
            calendar.set(java.util.Calendar.MINUTE, minute);
            if(i == 0) {
                startSet = true;
                startHour = hour;
                startMinute = minute;
                Toast.makeText(requireContext(), "Date=" + calendar.get(java.util.Calendar.DAY_OF_MONTH), Toast.LENGTH_SHORT).show();
                startTime = calendar.getTimeInMillis();
//                    Toast.makeText(requireContext(), "Start Time = " + startTime, Toast.LENGTH_SHORT).show();
            } else {
                endSet = true;
                endHour = hour;
                endMinute = minute;
                endTime = calendar.getTimeInMillis();
//                    Toast.makeText(requireContext(), "End Time = " + endTime, Toast.LENGTH_SHORT).show();
                Toast.makeText(requireContext(), "Date=" + calendar.get(java.util.Calendar.DAY_OF_MONTH), Toast.LENGTH_SHORT).show();
            }

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh-mm aa", new Locale.Builder().setRegion("IN").build());
            etTime.setText(simpleDateFormat.format(calendar.getTime()));
        };
        Toast.makeText(requireContext(), "Time " + calendar.get(java.util.Calendar.HOUR_OF_DAY) + " " +
                calendar.get(java.util.Calendar.MINUTE), Toast.LENGTH_SHORT).show();
        new TimePickerDialog(requireActivity(),
                timeListener,
                calendar.get(java.util.Calendar.HOUR_OF_DAY),
                calendar.get(java.util.Calendar.MINUTE), false).show();
    }

    private void showDateDialog() {
        if(!dateSet && !startSet && !endSet){
            calendar = java.util.Calendar.getInstance();
            startHour = calendar.get(java.util.Calendar.HOUR_OF_DAY);
            startMinute = calendar.get(java.util.Calendar.MINUTE);
            endHour = calendar.get(java.util.Calendar.HOUR_OF_DAY);
            endMinute = calendar.get(java.util.Calendar.MINUTE);
        }
        DatePickerDialog.OnDateSetListener dateListener = (datePicker, year, month, dayOfMonth) -> {
            Toast.makeText(requireContext(), "Date set", Toast.LENGTH_SHORT).show();
            calendar.set(java.util.Calendar.YEAR, year);
            calendar.set(java.util.Calendar.MONTH, month);
            calendar.set(java.util.Calendar.DAY_OF_MONTH, dayOfMonth);
            dateSet = true;
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy", new Locale.Builder().setRegion("IN").build());
            etDate.setText(simpleDateFormat.format(calendar.getTime()));
        };
        Toast.makeText(requireContext(), "Date: " + calendar.get(java.util.Calendar.DATE), Toast.LENGTH_SHORT).show();
        new DatePickerDialog(requireContext(), dateListener,
                calendar.get(java.util.Calendar.YEAR),
                calendar.get(java.util.Calendar.MONTH),
                calendar.get(java.util.Calendar.DAY_OF_MONTH)).show();
    }
    boolean isFilled(EditText editText) {
        return editText.getText() != null && !editText.getText().toString().isEmpty();
    }
}
package com.example.a1212508_1211441_courseproject.ui.logout;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.example.a1212508_1211441_courseproject.HomeActivity;
import com.example.a1212508_1211441_courseproject.MainActivity;
import com.example.a1212508_1211441_courseproject.R;

public class LogoutFragment extends Fragment {

    private Button buttonLogout;
    private SharedPreferences sharedPreferences;

    public LogoutFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Initialize SharedPreferences
        sharedPreferences = getActivity().getSharedPreferences("UserPrefs", MODE_PRIVATE);
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_logout, container, false);

        buttonLogout = rootView.findViewById(R.id.buttonLogout);

        buttonLogout.setOnClickListener(v -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove("email");
            editor.remove("password");
            editor.apply();

            // Redirect to MainActivity (login screen)
            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);

            // Close the current activity hosting this fragment
            if (getActivity() != null) {
                getActivity().finish();
            }
        });

        return rootView;
    }
}

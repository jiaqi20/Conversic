package com.example.conversic.ui.logout;

import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.conversic.LogOutActivity;
import com.example.conversic.R;

public class LogOutFragment extends Fragment {

    private LogOutViewModel mViewModel;

    public static LogOutFragment newInstance() {
        return new LogOutFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.log_out_fragment, container, false);

        Button btnLogout = view.findViewById(R.id.btn_logout1);

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), LogOutActivity.class));
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(LogOutViewModel.class);
        // TODO: Use the ViewModel
    }

}

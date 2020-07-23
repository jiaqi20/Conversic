package com.example.conversic.ui.crashcourse;

import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.conversic.R;

public class CrashCourseFragment extends Fragment {

    private CrashCourseViewModel mViewModel;

    public static CrashCourseFragment newInstance() {
        return new CrashCourseFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.crash_course_fragment, container, false);



        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(CrashCourseViewModel.class);
        // TODO: Use the ViewModel
    }

}

package com.example.conversic.ui.conversic;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
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
import android.widget.Toast;

import com.example.conversic.Conversic1;
import com.example.conversic.Conversic2;
import com.example.conversic.R;

public class ConversicFragment extends Fragment {

    private ConversicViewModel mViewModel;

    public static ConversicFragment newInstance() {
        return new ConversicFragment();
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.conversic_fragment, container, false);

        Button btnConversic1 = view.findViewById(R.id.btn_conversic1);
        Button btnConversic2 = view.findViewById(R.id.btn_conversic2);

        btnConversic1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), Conversic1.class));
            }
        });

        btnConversic2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Function under development", Toast.LENGTH_SHORT).show();
            }
        });
        /*
        btnConversic2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment dialog = (DialogFragment) DialogFragment.instantiate(getActivity(), "Hello world");
                dialog.show(getFragmentManager(), "dialog");
                //startActivity(new Intent(getContext(), Conversic2.class));
            }
        });

         */

        return view;
    }



}

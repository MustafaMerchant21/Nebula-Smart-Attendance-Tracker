package com.nebula.NebulaApp;

import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
public class LeaveFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_leave, container, false);
        Toolbar toolbar = (Toolbar) requireActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("Leave");
        return v;
    }
}
package com.nebula.NebulaApp;

import static com.nebula.NebulaApp.HomeFragment.SHARED_PREFS;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class AppliedFragment extends Fragment {
    public AppliedFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView emptyTextView;
    LeaveAdapter leaveAdapter;
    DatabaseReference reference;
    SharedPreferences sharedPref;
    CardView leaveCardView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_applied, container, false);
        emptyTextView = v.findViewById(R.id.emptyTextView);
        swipeRefreshLayout = v.findViewById(R.id.swipe_refresh_layout);
        sharedPref = this.requireActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        reference = FirebaseDatabase.getInstance().getReference().child("Institute").child(sharedPref.getString("Institute_id", ""))
                .child("Departments").child(sharedPref.getString("selectedCourse", ""))
                .child("Leave Data").child("Applied");

        recyclerView = v.findViewById(R.id.leavesRV);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity().getApplicationContext()));
        FirebaseRecyclerOptions<Leave> options
                = new FirebaseRecyclerOptions.Builder<Leave>()
                .setQuery(reference, Leave.class)
                .build();
        leaveAdapter = new LeaveAdapter(options, getActivity());
        recyclerView.setAdapter(leaveAdapter);
        recyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                checkEmpty();
            }
        }, 500); // Adjust the delay time as needed

        // Register AdapterDataObserver to observe changes in the adapter's data set
        leaveAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                checkEmpty(); // Check if the RecyclerView is empty whenever the data changes
            }
        });
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Refresh the data
                leaveAdapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        return v;
    }
    @Override public void onStart()
    {
        super.onStart();
        leaveAdapter.startListening();
    }

    // Function to tell the app to stop getting
    // data from database on stopping of the activity
    @Override public void onStop()
    {
        super.onStop();
        leaveAdapter.stopListening();
    }
    private void checkEmpty() {
        if (leaveAdapter.getItemCount() == 0) {
            // RecyclerView is empty, show the empty TextView
            emptyTextView.setVisibility(View.VISIBLE);
        } else {
            // RecyclerView has items, hide the empty TextView
            emptyTextView.setVisibility(View.GONE);
        }
    }
}
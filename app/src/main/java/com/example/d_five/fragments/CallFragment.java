package com.example.d_five.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.d_five.CallHistory;
import com.example.d_five.R;
import com.example.d_five.activities.MainActivity;
import com.example.d_five.adapter.CallAdapter;
import com.example.d_five.dao.ConnectionDB;
import com.example.d_five.dao.DAOFactory;
import com.example.d_five.databinding.FragmentCallBinding;
import com.example.d_five.model.User;
import com.example.d_five.utilities.Constants;
import com.example.d_five.utilities.PreferenceManager;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CallFragment extends Fragment {
    private FragmentCallBinding binding;
    private ArrayList<CallHistory> callHistories;
    private RecyclerView recyclerView;
    MainActivity mainActivity;

    // database
    private DAOFactory daoFactory;
    private PreferenceManager preferenceManager;
    private CallAdapter callAdapter;
    private Boolean showMissedCallsOnly;
    private Context mContext;

    public CallFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCallBinding.inflate(inflater, container, false);
        View rootView = binding.getRoot();
        mainActivity = (MainActivity) mContext;

        binding.searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard();
                }
            }
        });

        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                try {
                    filterCallSearch(newText);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                return true;
            }
        });

        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard();
            }
        });
        binding.callsRecyclerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard();
            }
        });
        return rootView;
    }

    @SuppressLint("NotifyDataSetChanged")
    private void filterCallHistory() {
        ArrayList<CallHistory> filteredHistories = new ArrayList<>();

        for (CallHistory call : callHistories) {
            // Check if the call is a missed call and the switch button is on
            if (showMissedCallsOnly && call.getStatus().equals("Missed")) {
                filteredHistories.add(call);
            }
            // If the switch button is off, add all calls to the filtered list
            else if (!showMissedCallsOnly) {
                filteredHistories.add(call);
            }
        }

        callAdapter.setData(filteredHistories);
        callAdapter.notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void filterCallSearch(String newText) throws Exception {
        ArrayList<CallHistory> filteredSearchResult = new ArrayList<>();

        for (CallHistory call : callHistories) {
            if (call.getUsername().contains(newText)) {
                filteredSearchResult.add(call);
            }

//            if (filteredSearchResult.isEmpty()) {
//                Toast.makeText(getContext(), "No call found", Toast.LENGTH_SHORT).show();
//            }
            callAdapter.setData(filteredSearchResult);
            callAdapter.notifyDataSetChanged();
        }
    }

    private void hideKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        View focusedView = requireActivity().getCurrentFocus();

        if (focusedView != null) {
            inputMethodManager.hideSoftInputFromWindow(focusedView.getWindowToken(), 0);
            focusedView.clearFocus();
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        daoFactory = new DAOFactory(ConnectionDB.connection);
        preferenceManager = new PreferenceManager(mContext);

        ExecutorService service = Executors.newSingleThreadExecutor();
        service.execute(new Runnable() {
            @Override
            public void run() {
                mainActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        binding.progressBar.setVisibility(View.VISIBLE);
                    }
                });

                // load data
                dataInitialize();


                mainActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        binding.progressBar.setVisibility(View.GONE);
                        // set adapter
                        recyclerView = view.findViewById(R.id.callsRecyclerView);
                        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
                        recyclerView.setHasFixedSize(true);

                        callAdapter = new CallAdapter(mContext, callHistories);
                        recyclerView.setAdapter(callAdapter);

                        callAdapter.setItemClickedListener(new ItemClickedListener() {
                            @Override
                            public void onItemClick(View view, int position, CallHistory call) {
                                hideKeyboard();
                                call.setExpandable(!call.isExpandable());
                            }
                        });
                    }
                });
            }
        });


//        dataInitialize();

//        recyclerView = view.findViewById(R.id.callsRecyclerView);
//        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//        recyclerView.setHasFixedSize(true);
//
//        callAdapter = new CallAdapter(getContext(), callHistories);
//        recyclerView.setAdapter(callAdapter);
//
//        callAdapter.setItemClickedListener(new ItemClickedListener() {
//            @Override
//            public void onItemClick(View view, int position, CallHistory call) {
//                hideKeyboard();
//                call.setExpandable(!call.isExpandable());
//            }
//        });

        binding.switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                showMissedCallsOnly = b;
                filterCallHistory();
                if (b) {
                    binding.tvSwitchAll.setTextColor(ContextCompat.getColor(getContext(), R.color.n_gray));
                    binding.tvSwitchMissed.setTextColor(ContextCompat.getColor(getContext(), R.color.gray));
                } else {
                    binding.tvSwitchAll.setTextColor(ContextCompat.getColor(getContext(), R.color.gray));
                    binding.tvSwitchMissed.setTextColor(ContextCompat.getColor(getContext(), R.color.n_gray));
                }
            }
        });
    }

    private void dataInitialize() {
        callHistories = new ArrayList<>();

        try {
            User myself = daoFactory.getUserDAO().getInfoUser(preferenceManager.getString(Constants.USER_NAME));
            callHistories = (ArrayList<CallHistory>) daoFactory.getCallDAO().getListHistoryCall(myself.getId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void onActivityChange() {
        try {
            User myself = daoFactory.getUserDAO().getInfoUser(preferenceManager.getString(Constants.USER_NAME));
            callHistories = (ArrayList<CallHistory>) daoFactory.getCallDAO().getListHistoryCall(myself.getId());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (callHistories != null) {
            callAdapter.setData(callHistories);
            callAdapter.notifyDataSetChanged();
        }
    }

    public interface ItemClickedListener {
        void onItemClick(View view, int position, CallHistory call);
    }
}


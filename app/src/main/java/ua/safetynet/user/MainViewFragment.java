package ua.safetynet.user;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.DefaultItemAnimator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ua.safetynet.Database;
import ua.safetynet.group.GroupHomeFragment;
import ua.safetynet.R;
import ua.safetynet.auth.FirebaseAuthActivity;
import ua.safetynet.group.GroupRecyclerAdapter;
import ua.safetynet.group.Group;

/**
 * @author Jeremy McCormick
 * Main menu fragment with recycler view of groups user is in and display of outstanding balance
 */
public class MainViewFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private List<Group> groupList;

    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private TextView mainBalance;
    private ProgressBar progressBar;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public MainViewFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MainViewFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MainViewFragment newInstance(String param1, String param2) {
        MainViewFragment fragment = new MainViewFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main_view, container, false);

        mainBalance = rootView.findViewById(R.id.main_balance_amount);
        final NumberFormat format = NumberFormat.getCurrencyInstance(Locale.US);

        try {
            String test = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }
        catch (NullPointerException e) {
            //Somehow the current user is null, log out and start sign in
            Log.d("MAIN FRAG", "Current user is null, logging out and starting sign in");
            AuthUI.getInstance().signOut(getActivity()).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    startActivity(new Intent(getActivity(), FirebaseAuthActivity.class));
                }
            });
            return rootView;
        }
        Database db = new Database();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db.queryGroups(userId,new Database.DatabaseGroupsListener() {

            @Override
            public void onGroupsRetrieval(ArrayList<Group> groups) {
                BigDecimal bal = new BigDecimal("0");
                for (int count = 0; count < groups.size(); count++){
                    bal.add(groups.get(count).getFunds());
                }
                mainBalance.setText(format.format(bal));
                mainBalance.setTextSize(45);
            }
        });

        mRecyclerView = rootView.findViewById(R.id.main_recyclerview);
        mRecyclerView.setHasFixedSize(true);
        //Get loading spinner
        progressBar = rootView.findViewById(R.id.main_view_progressbar);
        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(container.getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        makeGroupList();
        //Hide view components so we can show spinner before data is gotten
        mRecyclerView.setVisibility(View.GONE);
        mainBalance.setVisibility(View.GONE);
        // Inflate the layout for this fragment
        return rootView;

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    /**
     * Setup the group recycler view by getting list of groups user is in from database and populating
     * it with that list.
     */
    private void makeGroupList() {
        if(FirebaseAuth.getInstance().getCurrentUser() != null) {
            groupList = new ArrayList<Group>();
            Database db = new Database();
            db.queryGroups(new Database.DatabaseGroupsListener() {
                @Override
                public void onGroupsRetrieval(ArrayList<Group> groups) {
                    Log.d("MAIN FRAGMENT", groups.toString());
                    groupList = groups;
                    // specify an adapter (see also next example)
                    mAdapter = new GroupRecyclerAdapter(groupList, (group -> {
                        GroupHomeFragment homeFragment = GroupHomeFragment.newInstance(group);
                        FragmentManager fm =getActivity().getSupportFragmentManager();
                        if(fm != null)
                            fm.beginTransaction().replace(R.id.fragment_container, homeFragment).commit();
                    }));
                    mRecyclerView.setAdapter(mAdapter);
                    makeVisible();
                }
            });
        }
    }
    private void makeVisible(){
        progressBar.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
        mainBalance.setVisibility(View.VISIBLE);
    }
}
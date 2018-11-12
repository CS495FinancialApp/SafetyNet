package ua.safetynet.user;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.DefaultItemAnimator;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import ua.safetynet.Database;
import ua.safetynet.R;
import ua.safetynet.group.GroupRecyclerAdapter;
import ua.safetynet.group.Group;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MainViewFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MainViewFragment#newInstance} factory method to
 * create an instance of this fragment.
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

        TextView mainBalance = rootView.findViewById(R.id.main_balance_amount);
        String text = "<font color=#ad3535>-</font> <font color=#000000>$55.27</font>";
        mainBalance.setText(Html.fromHtml(text,0));
        mainBalance.setTextSize(45);

        mRecyclerView = rootView.findViewById(R.id.main_recyclerview);
        mRecyclerView.setHasFixedSize(true);
        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(container.getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        makeGroupList();


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

    private void makeGroupList() {
        Bitmap bmp1 = BitmapFactory.decodeResource(getResources(), R.drawable.test_tux);
        Bitmap bmp2 = BitmapFactory.decodeResource(getResources(), R.drawable.test_brain);
        Bitmap bmp3 = BitmapFactory.decodeResource(getResources(), R.drawable.test_rent);
        Group group1 = new Group("Work Fundraiser", "1", bmp1 , new BigDecimal(45), new BigDecimal(20), null,null,null);
        Group group2 = new Group("Johnson Family", "2", bmp2 , new BigDecimal(305.52), new BigDecimal(100), null,null,null);
        Group group3 = new Group("Roomates!", "3", bmp3 , new BigDecimal(234.89), new BigDecimal(25), null,null,null);
        groupList = new ArrayList<Group>();
        Database db = new Database();
        db.queryGroups(new Database.DatabaseGroupsListener() {
            @Override
            public void onGroupsRetrieval(ArrayList<Group> groups) {
                Log.d("MAIN FRAGMENT", groups.toString());
                groupList = groups;
                // specify an adapter (see also next example)
                mAdapter = new GroupRecyclerAdapter(groupList);
                mRecyclerView.setAdapter(mAdapter);
            }
        });
    }
}
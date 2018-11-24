package ua.safetynet.user;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import ua.safetynet.Database;
import ua.safetynet.R;
import ua.safetynet.payment.Transaction;
import ua.safetynet.payment.TransactionRecyclerAdapter;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ViewUserFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ViewUserFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ViewUserFragment extends Fragment {

    private static final String TAG = "VIEW USER";
    private static final String USER = "user";
    private User user;
    private TextView nameText;
    private ImageView userImageView;
    private ImageView editImage;
    private List<Transaction> transactionList;
    private RecyclerView mTransactionRecycler;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter mAdapter;

    private OnFragmentInteractionListener mListener;

    public ViewUserFragment() {
        // Required empty public constructor
    }


    public static ViewUserFragment newInstance(User user) {
        ViewUserFragment fragment = new ViewUserFragment();
        Bundle args = new Bundle();
        args.putParcelable(USER, user);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            user = getArguments().getParcelable(USER);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_view_user, container, false);
        mLayoutManager = new LinearLayoutManager(container.getContext());
        return view;
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        nameText = view.findViewById(R.id.view_user_name);
        userImageView = view.findViewById(R.id.view_user_image);
        editImage = view.findViewById(R.id.view_user_edit_button);
        editImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment editUserFragment = EditUserFragment.newInstance(user);
                Log.d(TAG, "Edit Button clicked, going to that fragment");
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, editUserFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
        populateHeader();
        //Setup recycler view
        mTransactionRecycler = view.findViewById(R.id.view_user_recycler);
        mTransactionRecycler.setHasFixedSize(true);
        mTransactionRecycler.setLayoutManager(mLayoutManager);
        mTransactionRecycler.setItemAnimator(new DefaultItemAnimator());
        getTransactionList();
    }
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
    private void populateHeader() {
        nameText.setText(user.getName());
        Glide.with(getContext()).load(user.getImage()).into(userImageView);
    }
    private void getTransactionList() {
        if(FirebaseAuth.getInstance().getCurrentUser() != null) {
            Database db = new Database();
            db.queryUserTransactions(user.getUserId(), new Database.DatabaseTransactionsListener() {
                @Override
                public void onTransactionsRetrieval(ArrayList<Transaction> transactions) {
                    transactionList = transactions;
                    //Set adapter with group view
                    mAdapter = new TransactionRecyclerAdapter(transactionList,TransactionRecyclerAdapter.GROUP);
                    mTransactionRecycler.setAdapter(mAdapter);
                }
            });
        }
    }
}

package ua.safetynet.user;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.bumptech.glide.Glide;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import de.hdodenhof.circleimageview.CircleImageView;
import ua.safetynet.R;


public class EditUserFragment extends Fragment {

    private User user = null;
    private OnFragmentInteractionListener mListener;
    private TextInputEditText nameText;
    private TextInputEditText emailText;
    private TextInputEditText phoneText;
    private CircleImageView imageView;
    public EditUserFragment() {
        // Required empty public constructor
    }


    public static EditUserFragment newInstance(User user) {
        EditUserFragment fragment = new EditUserFragment();
        Bundle args = new Bundle();
        args.putSerializable("user", user);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            user = (User)savedInstanceState.getSerializable("user");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_user, container, false);
        return view;
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        nameText = view.findViewById(R.id.edit_user_name);
        emailText = view.findViewById(R.id.edit_user_email);
        phoneText = view.findViewById(R.id.edit_user_phone);
        imageView = view.findViewById(R.id.edit_user_image);
        populateText();
        setupPhoneInput();
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

    private void populateText() {
        if(user == null) {
            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            if (firebaseUser.getDisplayName() != null)
                nameText.setText(firebaseUser.getDisplayName());
            if (firebaseUser.getEmail() != null)
                emailText.setText(firebaseUser.getEmail());
            if (!firebaseUser.getPhoneNumber().isEmpty())
                phoneText.setText(firebaseUser.getPhoneNumber());
            if (firebaseUser.getPhotoUrl() != null) {
                Glide.with(getContext()).load(firebaseUser.getPhotoUrl()).into(imageView);
            }
        }

    }

    private void setupPhoneInput() {
        if(phoneText != null)
            phoneText.addTextChangedListener(new PhoneNumberFormattingTextWatcher("US"));
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}

package ua.safetynet.group;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.NumberFormat;

import ua.safetynet.Database;
import ua.safetynet.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CreateGroupFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CreateGroupFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreateGroupFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private Button bttnCreateGroup;
    private EditText groupName;
    private EditText withdrawalLimitText;
    private EditText repayTime;
    private BigDecimal withdrawLimit;
    public static final int PICK_IMAGE = 1;
    private static final String TAG = "CREATE GROUP";
    private ImageView groupImage;
    private Bitmap image;
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public CreateGroupFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CreateGroupFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CreateGroupFragment newInstance(String param1, String param2) {
        CreateGroupFragment fragment = new CreateGroupFragment();
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
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_create_group, container, false);
        //button for submitting a group
        bttnCreateGroup = view.findViewById(R.id.create_group_button);
        //text box for group name
        groupName = view.findViewById(R.id.new_group_name_text);
        //Text box for repay time and limit
        repayTime = view.findViewById(R.id.new_group_repay_time);
        withdrawalLimitText = view.findViewById(R.id.new_group_withdrawal_limit);
        //Image view
        groupImage = view.findViewById(R.id.new_group_image);
        Glide.with(getContext()).load(R.drawable.group_default_logo).into(groupImage);

        //Set up listener to format w/d amount
        setupAmountEditTextListener();
        //group submission button listener
        bttnCreateGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //create the database connection
                Database database = new Database();
                Group group = new Group();
                group.setName(groupName.getText().toString().trim());
                group.addUsers(FirebaseAuth.getInstance().getCurrentUser().getUid());
                group.addAdmins(FirebaseAuth.getInstance().getCurrentUser().getUid());
                group.setRepaymentTime(Integer.parseInt(repayTime.toString()));
                group.setWithdrawalLimit(withdrawLimit);

                //send the data after getting data from the blanks
                database.createGroup(group);
                Toast.makeText(getActivity(), "Group added!!", Toast.LENGTH_LONG).show();
            }
        });
        return view;
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
    private void makeDbGroup() {

    }
    /**
     * Adds text changed listener to amount text box. Formats it in a money style with digits shifting down
     * as you enter them
     */
    public void setupAmountEditTextListener() {
        withdrawalLimitText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            private String current = "";

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals(current)) {
                    //Throws an error if backspacing with all 0's, check for this and return if so
                    String emptyTest = s.toString().replaceAll("[^1-9]", "");
                    if (emptyTest.isEmpty())
                        return;
                    withdrawalLimitText.removeTextChangedListener(this);

                    String cleanString = s.toString().replaceAll("[^0-9]", "");
                    BigDecimal parsed = new BigDecimal(cleanString).setScale(2, BigDecimal.ROUND_FLOOR).divide(new BigDecimal(100), BigDecimal.ROUND_FLOOR);
                    withdrawLimit = parsed;
                    String formatted = NumberFormat.getCurrencyInstance().format(parsed);
                    current = formatted;
                    withdrawalLimitText.setText(formatted);
                    withdrawalLimitText.setSelection(formatted.length());
                    withdrawalLimitText.addTextChangedListener(this);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }
    private void selectImage() {
        Intent pickIntent = new Intent(Intent.ACTION_PICK);
        pickIntent.setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"image/*");

        startActivityForResult(pickIntent, PICK_IMAGE);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE) {
            if(data == null) {
                Log.d(TAG, "Couldn't get image from storage");
                Toast.makeText(getContext(), "Could not get photo from storage",Toast.LENGTH_SHORT ).show();
                return;
            }
            Uri imageUri = data.getData();
            if(imageUri != null) {
                Log.d(TAG, "Got new image from picker");
                Bitmap newImage = null;
                try {
                    newImage = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
                Glide.with(getContext()).asBitmap().load(newImage).into(groupImage);
            }
            else
                Log.d(TAG, "Could not image from return bundle");
        }
        else
            Log.d(TAG, "Request code not equal to PICK IMAGE");
    }
}

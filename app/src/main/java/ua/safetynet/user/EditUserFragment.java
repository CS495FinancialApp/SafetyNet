package ua.safetynet.user;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import ua.safetynet.Database;
import ua.safetynet.R;


public class EditUserFragment extends Fragment {

    private User user = null;
    private OnFragmentInteractionListener mListener;
    private TextInputEditText nameText;
    private TextInputEditText emailText;
    private TextInputEditText phoneText;
    private ImageView imageView;
    private Bitmap image;
    public static final String TAG = "Edit User Fragment";
    public static final int PICK_IMAGE = 1;

    public EditUserFragment() {
        // Required empty public constructor
    }


    public static EditUserFragment newInstance(User user) {
        EditUserFragment fragment = new EditUserFragment();
        Bundle args = new Bundle();
        args.putParcelable("user",user);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState!= null && getArguments() != null) {
            user = savedInstanceState.getParcelable("user");
            Log.d(TAG, "User obj gotten from bundle");
        }
        setHasOptionsMenu(true);
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
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
        view.findViewById(R.id.edit_user_done).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storeUser();
                leaveFragment();
            }
        });
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
            if(firebaseUser != null) {
                if (firebaseUser.getDisplayName() != null)
                    nameText.setText(firebaseUser.getDisplayName());
                if (firebaseUser.getEmail() != null)
                    emailText.setText(firebaseUser.getEmail());
                if (firebaseUser.getPhoneNumber() != null )
                    phoneText.setText(firebaseUser.getPhoneNumber());
                if (firebaseUser.getPhotoUrl() != null) {
                    Glide.with(getContext()).load(firebaseUser.getPhotoUrl()).into(imageView);
                }
            }
        }
        else {
            if (user.getName() != null)
                nameText.setText(user.getName());
            if (user.getEmail() != null)
                emailText.setText(user.getEmail());
            if (user.getPhoneNumber() != null)
                phoneText.setText(user.getPhoneNumber());
            if (user.getImage() != null) {
                Glide.with(getContext()).load(user.getImage()).into(imageView);
            }
        }

    }

    private void setupPhoneInput() {
        if(phoneText != null)
            phoneText.addTextChangedListener(new PhoneNumberFormattingTextWatcher("US"));
    }
    private void selectImage() {
        Intent pickIntent = new Intent(Intent.ACTION_PICK);
        pickIntent.setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"image/*");

        startActivityForResult(pickIntent, PICK_IMAGE);
    }
    private void storeUser() {
        if(user==null) {
            user = new User();
            Log.d(TAG, FirebaseAuth.getInstance().getCurrentUser().getUid() + " " + FirebaseAuth.getInstance().getUid());
            user.setUserId(FirebaseAuth.getInstance().getUid());
            user.setName(nameText.getText().toString());
            user.setEmail(emailText.getText().toString());
            user.setPhoneNumber(phoneText.getText().toString());
            user.setImage(((BitmapDrawable) imageView.getDrawable()).getBitmap());
        }
        else {
            user.setName(nameText.getText().toString());
            user.setEmail(emailText.getText().toString());
            user.setPhoneNumber(phoneText.getText().toString());
            user.setImage(((BitmapDrawable) imageView.getDrawable()).getBitmap());
        }

            Database db = new Database();
            db.setUser(user);
    }
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
    private void leaveFragment() {
        try {
            getFragmentManager().popBackStackImmediate();
        }
        catch(NullPointerException e) {
            Log.d(TAG, e.getLocalizedMessage());
            Intent mainPageIntent = new Intent(getContext(), MainPageActivity.class);
            mainPageIntent.putExtra("user",user);
            startActivity(mainPageIntent);
        }
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
                Glide.with(getContext()).asBitmap().load(newImage).into(imageView);
            }
            else
                Log.d(TAG, "Could not image from return bundle");
        }
        else
            Log.d(TAG, "Request code not equal to PICK IMAGE");
    }
}

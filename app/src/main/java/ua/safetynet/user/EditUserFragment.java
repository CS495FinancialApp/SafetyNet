package ua.safetynet.user;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.InputStream;

import de.hdodenhof.circleimageview.CircleImageView;
import ua.safetynet.Database;
import ua.safetynet.R;


public class EditUserFragment extends Fragment {

    private User user = null;
    private OnFragmentInteractionListener mListener;
    private TextInputEditText nameText;
    private TextInputEditText emailText;
    private TextInputEditText phoneText;
    private CircleImageView imageView;
    private Bitmap image;
    public static final String TAG = "Edit User Fragment";
    public static final int PICK_IMAGE = 1;

    public EditUserFragment() {
        // Required empty public constructor
    }


    public static EditUserFragment newInstance(User user) {
        EditUserFragment fragment = new EditUserFragment();
        Bundle args = new Bundle();
        //args.putStr("user", user);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            user = (User)savedInstanceState.getSerializable("user");
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
        populateText();
        setupPhoneInput();
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.done_check_toolbar, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.edit_user_done:
                storeUser();
                return true;
        }
        return super.onOptionsItemSelected(item); // important line
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
                if (!firebaseUser.getPhoneNumber().isEmpty())
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
        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType("image/*");

        Intent pickIntent = new Intent(Intent.ACTION_PICK);
        pickIntent.setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"image/*");

        Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});

        startActivityForResult(chooserIntent, PICK_IMAGE);
    }
    private void storeUser() {
        if(user==null) {
            user = new User();
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE) {
            if(data == null) {
                Log.d(TAG, "Couldn't get image from storage");
                Toast.makeText(getContext(), "Could not get photo from storage",Toast.LENGTH_SHORT ).show();
                return;
            }
            final Bundle extras = data.getExtras();
            if(extras != null) {
                Bitmap newImage = extras.getParcelable("data");
                imageView.setImageBitmap(newImage);
            }
        }
    }
}

package ua.safetynet.auth;

import android.app.Activity;
import android.arch.lifecycle.Lifecycle;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import ua.safetynet.R;
public class OnboardingFragment extends Fragment {
    public static final int MAKE_GROUP = 0;
    public static final int PAYMENT = 1;
    public static final int PAYOUT = 2;
    public static final int USERINFO = 3;

    private TextView headerText;
    private TextView bodyText;
    private ImageView helpImage;
    private int type;

    public OnboardingFragment() {

    }
    public static OnboardingFragment newInstance(int type) {
        OnboardingFragment onFragment = new OnboardingFragment();
        Bundle args = new Bundle();
        args.putInt("type", type);
        onFragment.setArguments(args);
        return onFragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null) {
            type = getArguments().getInt("type");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_onboarding, container,false);
        headerText = view.findViewById(R.id.onboarding_fragment_label);
        bodyText = view.findViewById(R.id.onboarding_fragement_description);
        helpImage = view.findViewById(R.id.edit_user_image);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    private void populateView() {
        
    }
}

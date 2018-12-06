package ua.safetynet.auth;

import android.app.Activity;
import android.arch.lifecycle.Lifecycle;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import ua.safetynet.R;

/**
 * @author Jeremy McCormick
 * Fragment for onbaording to show the actual information and images
 * Changes view shown based on type passed into new instance
 */
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

    /**
     * New instance, must pass type to set which card it shows
     * @param type which tutorial slide to show
     * @return
     */
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
        helpImage = view.findViewById(R.id.onboarding_fragment_image);
        populateView();
        //Set background color
        switch (type) {
            case MAKE_GROUP:
                view.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.colorPrimary, null));
                break;
            case PAYMENT:
                view.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.onboarding_bg1, null));
                break;
            case PAYOUT:
                view.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.onboarding_bg2, null));
                break;
            case USERINFO:
                view.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.onboarding_bg1, null));
                break;
            default:
                view.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.colorPrimary, null));
                break;
        }
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

    /**
     * Populate view based on which type passed in
     */
    private void populateView() {
        String header;
        String body;
        Drawable image;
        if(type == MAKE_GROUP) {
            header = "Welcome to SafetyNet";
            body = "Take some time to get familiar with the app. First you'll want to join a group.";
            image = getResources().getDrawable(R.drawable.oboarding_newgroup, null);
        }
        else if(type == PAYMENT) {
            header = "Making a deposit";
            body = "Under payments, you can deposit an amount to a group using a card, PayPal, or Venmo";
            image = getResources().getDrawable(R.drawable.onboarding_payment, null);
        }
        else if(type == PAYOUT) {
            header = "Making a withdrawal";
            body = "You can withdrawal funds from a group when needed. Just be sure to pay it back in time so your withdrawal stays anonymous";
            image = getResources().getDrawable(R.drawable.onboarding_payout, null);
        }
        else { //USERINFO
            header = "Before we start, let's login and then get some basic information";
            body = "";
            image = null;
        }
        headerText.setText(header);
        bodyText.setText(body);
        helpImage.setAdjustViewBounds(true);
        Glide.with(this).load(image).into(helpImage);
    }
}

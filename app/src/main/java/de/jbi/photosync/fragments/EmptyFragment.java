package de.jbi.photosync.fragments;

/**
 * Created by Jan on 14.05.2016.
 */

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.jbi.photosync.R;

/**
 * Fragment that appears in the "frame_container", shows an empty fragment
 */
public class EmptyFragment extends Fragment {
    public static final String ARG_FRAGMENT_NUMBER = "fragment_number";

    public EmptyFragment() {
        // Empty constructor required for fragment subclasses
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_empty, container, false);
        int i = getArguments().getInt(ARG_FRAGMENT_NUMBER);
        String fragmentTitle = getResources().getStringArray(R.array.fragments_array)[i];
        getActivity().setTitle(fragmentTitle);
        return rootView;
    }
}

package org.dd1929.apod.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import org.dd1929.apod.R;

public class ErrorFragment extends Fragment {

    private SwipeRefreshLayout mSwipeRefresh;
    private Button mRefreshButton;

    @Nullable
    @Override
    public View onCreateView(@Nullable LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_error, container, false);
        mSwipeRefresh = v.findViewById(R.id.swipe_refresh);
        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getActivity().recreate();
            }
        });
        mSwipeRefresh.setColorSchemeResources(R.color.color_primary);
        mSwipeRefresh.setProgressBackgroundColorSchemeResource(R.color.swipe_refresh_color_background);

        mRefreshButton = v.findViewById(R.id.refresh_button);
        mRefreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().recreate();
            }
        });
        return v;
    }
}

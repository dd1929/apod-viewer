package org.dd1929.apod.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.dd1929.apod.adapters.LibsAdapter;
import org.dd1929.apod.dialogs.LicenseFragment;
import org.dd1929.apod.enums.License;
import org.dd1929.apod.models.Library;
import org.dd1929.apod.R;
import org.dd1929.apod.viewholders.LibsHolder;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AboutLibsFragment extends Fragment{
    private static final String DIALOG_LICENSE = "license";

    private RecyclerView mLibsRecyclerView;

    public static AboutLibsFragment newInstance() {
        return new AboutLibsFragment();
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_about_libs, container, false);

        mLibsRecyclerView = v.findViewById(R.id.libs_recycler_view);
        mLibsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        List<Library> libs = new ArrayList<>();
        libs.add(new Library(getString(R.string.androidx_name), getString(R.string.androidx_author), getString(R.string.androidx_desc), getString(R.string.androidx_cr), License.APACHEv2, getString(R.string.apache_v2_url), getString(R.string.androidx_src), getString(R.string.androidx_site)));
        libs.add(new Library(getString(R.string.mdc_name), getString(R.string.mdc_author), getString(R.string.mdc_desc), getString(R.string.mdc_cr), License.APACHEv2, getString(R.string.apache_v2_url), getString(R.string.mdc_src), getString(R.string.mdc_site)));
        libs.add(new Library(getString(R.string.gson_name), getString(R.string.gson_author), getString(R.string.gson_desc), getString(R.string.gson_cr), License.APACHEv2, getString(R.string.apache_v2_url), getString(R.string.gson_src), null));
        libs.add(new Library(getString(R.string.glide_name), getString(R.string.glide_author), getString(R.string.glide_desc), getString(R.string.glide_cr), License.BSD, null, getString(R.string.androidx_src), getString(R.string.glide_site)));
        libs.add(new Library(getString(R.string.gview_name), getString(R.string.gview_author), getString(R.string.gview_desc), getString(R.string.gview_cr), License.APACHEv2, getString(R.string.apache_v2_url), getString(R.string.gview_src), null));
        libs.add(new Library(getString(R.string.ssiv_name), getString(R.string.ssiv_author), getString(R.string.ssiv_desc), getString(R.string.ssiv_cr), License.APACHEv2, getString(R.string.apache_v2_url), getString(R.string.ssiv_src), null));
        libs.add(new Library(getString(R.string.rf_name), getString(R.string.rf_author), getString(R.string.rf_desc), getString(R.string.rf_cr), License.APACHEv2, getString(R.string.apache_v2_url), getString(R.string.rf_src), getString(R.string.rf_site)));
        libs.add(new Library(getString(R.string.okhttp_name), getString(R.string.okhttp_author), getString(R.string.okhttp_desc), getString(R.string.okhttp_cr), License.APACHEv2, getString(R.string.apache_v2_url), getString(R.string.okhttp_src), getString(R.string.okhttp_site)));
        libs.add(new Library(getString(R.string.blmm_name), getString(R.string.blmm_author), getString(R.string.blmm_desc), getString(R.string.blmm_cr), License.APACHEv2, getString(R.string.apache_v2_url), getString(R.string.blmm_src), null));

        Collections.sort(libs, (l1, l2) -> l1.getName().compareToIgnoreCase(l2.getName())); //Comparator<Library>

        final LibsAdapter adapter = new LibsAdapter(new LibsAdapter.LibsDiff(), new LibsHolder.OnLicenseClickListener() {
            @Override
            public void onLicenseClicked(Library lib) {
                FragmentManager manager = getParentFragmentManager();
                LicenseFragment dialog = LicenseFragment.newInstance(lib);
                dialog.show(manager, DIALOG_LICENSE);
            }
        });
        mLibsRecyclerView.setAdapter(adapter);
        adapter.submitList(libs);

        return v;
    }
}

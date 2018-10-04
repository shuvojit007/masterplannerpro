package com.cruxbd.master_planner_pro.view.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.cruxbd.master_planner_pro.R;
import com.cruxbd.master_planner_pro.adapters.RecyclerAdapter;
import com.github.florent37.hollyviewpager.HollyViewPager;
import com.github.florent37.hollyviewpager.HollyViewPagerBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class MasterPlannerRecyclerViewFragment extends Fragment {
    private static final String TAG = "MP" +
            "RecyclerViewFragment";
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    public static RecyclerAdapter mAdapter;
    Unbinder unbinder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        Log.d(TAG, "onCreateView: ");
        View view = inflater.inflate(R.layout.fragment_master_planner_recycler_view, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        mAdapter = new RecyclerAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(new RecyclerAdapter());

//        Log.d(TAG, "onViewCreated: ");
        HollyViewPagerBus.registerRecyclerView(getActivity(), recyclerView);


    }

    public static void Refresh() {
//        Log.d(TAG, "Refresh: ");
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
//            Log.d(TAG, "Refresh: notify data set changed");
        }

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.recyclerView)
    public void onViewClicked() {
//        Log.d(TAG, "onViewClicked: recyclerview");
    }
}


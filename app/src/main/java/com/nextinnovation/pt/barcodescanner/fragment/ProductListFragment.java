package com.nextinnovation.pt.barcodescanner.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.nextinnovation.pt.barcodescanner.R;
import com.nextinnovation.pt.barcodescanner.adapter.ProductAdapter;
import com.nextinnovation.pt.barcodescanner.database.DatabaseHelper;
import com.nextinnovation.pt.barcodescanner.model.Product;

import java.util.ArrayList;

/**
 * Created by PT on 2/9/2017.
 */

public class ProductListFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private ProductAdapter mAdapter;
    private SwipeRefreshLayout swipeRefresh;
    ArrayList<Product> productArrayList;
    DatabaseHelper db ;
    public ProductListFragment(){

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_prduct_list,container,false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.product_list_recycler_view);
        swipeRefresh = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        swipeRefresh.setColorSchemeColors(getResources().getColor(R.color.green),getResources().getColor(R.color.blue),getResources().getColor(R.color.orange));
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadProductList();
            }
        });
       loadProductList();
        loadAdMob(view);
        return view;
    }

    private void loadProductList() {
        db= new DatabaseHelper(getContext());
        productArrayList = db.getAllEmployee();
        mAdapter = new ProductAdapter(getContext(), productArrayList);
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        swipeRefresh.setRefreshing(false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {



    }
    private void loadAdMob(View view) {
        MobileAds.initialize(getContext(), "ca-app-pub-1714609736931391~6770372869");
        AdView mAdView = (AdView)view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }
}

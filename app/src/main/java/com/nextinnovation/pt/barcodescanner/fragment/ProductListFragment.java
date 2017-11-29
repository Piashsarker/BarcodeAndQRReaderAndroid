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
import android.widget.RelativeLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.NativeExpressAdView;
import com.nextinnovation.pt.barcodescanner.R;
import com.nextinnovation.pt.barcodescanner.activity.MainActivity;
import com.nextinnovation.pt.barcodescanner.adapter.ProductAdapter;
import com.nextinnovation.pt.barcodescanner.database.DatabaseHelper;
import com.nextinnovation.pt.barcodescanner.utils.Utils;

import java.util.ArrayList;

/**
 * Created by PT on 2/9/2017.
 */

public class ProductListFragment extends Fragment implements MainActivity.ItemScanned {

    private RecyclerView mRecyclerView;
    private ProductAdapter mAdapter;
    private SwipeRefreshLayout swipeRefresh;
    ArrayList<Object> productArrayList;
    private RelativeLayout mainLayout , emptyLayout ;
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
        mRecyclerView = view.findViewById(R.id.product_list_recycler_view);
        swipeRefresh = view.findViewById(R.id.swipe_refresh_layout);
        mainLayout = view.findViewById(R.id.main_layout);
        emptyLayout = view.findViewById(R.id.empty_layout);
        swipeRefresh.setColorSchemeColors(getResources().getColor(R.color.green),getResources().getColor(R.color.blue),getResources().getColor(R.color.orange));
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadProductList();
            }
        });


        loadProductList();
        return view;
    }

    private void loadProductList() {
        db= new DatabaseHelper(getContext());
        productArrayList = db.getAllProduct();
        if(Utils.isNetworkAvailable(getContext())){
            addNativeExpressAd();
        }

        if(!productArrayList.isEmpty()){
            mAdapter = new ProductAdapter(getContext(), productArrayList);
            mRecyclerView.setHasFixedSize(true);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
            mRecyclerView.setLayoutManager(mLayoutManager);
            mRecyclerView.setAdapter(mAdapter);
            swipeRefresh.setRefreshing(false);
            emptyLayout.setVisibility(View.GONE);
        }
        else{
            emptyLayout.setVisibility(View.VISIBLE);
            swipeRefresh.setRefreshing(false);
        }

    }

    private void addNativeExpressAd() {
        /** Setting adViewItem dynamically into the **/
        for(int i=0 ; i<productArrayList.size(); i+=4){

                final  NativeExpressAdView adView = new NativeExpressAdView(getActivity());
                adView.setAdUnitId(getContext().getResources().getString(R.string.ad_unit_id));
                adView.setAdSize(new AdSize(320 , 150));
                adView.loadAd(new AdRequest.Builder().build());
                productArrayList.add(i , adView);
        }

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        loadProductList();
    }

    @Override
    public void itemUpdated() {
        loadProductList();
    }
}

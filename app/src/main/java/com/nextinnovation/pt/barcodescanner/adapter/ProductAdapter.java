package com.nextinnovation.pt.barcodescanner.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nextinnovation.pt.barcodescanner.ClipBoardManager;
import com.nextinnovation.pt.barcodescanner.R;
import com.nextinnovation.pt.barcodescanner.WebViewActivity;
import com.nextinnovation.pt.barcodescanner.database.DatabaseHelper;
import com.nextinnovation.pt.barcodescanner.model.Product;

import java.util.ArrayList;

/**
 * Created by PT on 2/9/2017.
 */

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Product> productArrayList;

    public ProductAdapter(Context context, ArrayList<Product> productArrayList) {
        this.context = context;
        this.productArrayList = productArrayList;
    }

    @Override
    public ProductAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_product,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ProductAdapter.ViewHolder holder, final int position) {
        holder.productId.setText(productArrayList.get(position).getProductBarcodeNo());
        holder.currentTime.setText(productArrayList.get(position).getScanTime());
        holder.currentDate.setText(productArrayList.get(position).getScanDate());

        holder.search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, WebViewActivity.class);
                intent.putExtra("product_id",productArrayList.get(position).getProductBarcodeNo());
                context.startActivity(intent);
            }
        });
        holder.copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipBoardManager clipBoardManager = new ClipBoardManager();
                clipBoardManager.copyToClipboard(context,productArrayList.get(position).getProductBarcodeNo());
                Snackbar.make(v,"Copied To Clipborad",Snackbar.LENGTH_SHORT).show();
            }
        });


    }
    public void addNewPrdouct( Product product){
        DatabaseHelper helper = new DatabaseHelper(context);
        helper.addEmployee(product);
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return productArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView search, copy, currentDate, currentTime, productId;

        public ViewHolder(View itemView) {
            super(itemView);
            search = (TextView) itemView.findViewById(R.id.search);
            copy = (TextView) itemView.findViewById(R.id.copy);
            currentDate = (TextView) itemView.findViewById(R.id.current_date);
            currentTime = (TextView) itemView.findViewById(R.id.current_time);
            productId = (TextView) itemView.findViewById(R.id.product_code);

        }
    }
}

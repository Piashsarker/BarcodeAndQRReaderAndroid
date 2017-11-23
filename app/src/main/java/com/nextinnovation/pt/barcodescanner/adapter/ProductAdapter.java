package com.nextinnovation.pt.barcodescanner.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.ads.NativeExpressAdView;
import com.nextinnovation.pt.barcodescanner.R;
import com.nextinnovation.pt.barcodescanner.activity.WebViewActivity;
import com.nextinnovation.pt.barcodescanner.model.Product;
import com.nextinnovation.pt.barcodescanner.utils.ClipBoardManager;

import java.util.ArrayList;

/**
 * Created by PT on 2/9/2017.
 */

public class ProductAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private ArrayList<Object> productArrayList;
    private static final int PRODUCT_ITEM_VIEW_TYPE = 0 ;
    private static final int AD_VIEW_TYPE = 1;
    public ProductAdapter(Context context, ArrayList<Object> productArrayList) {
        this.context = context;
        this.productArrayList = productArrayList;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType){
            case PRODUCT_ITEM_VIEW_TYPE :
            default:
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_product,parent,false);
                return new ProductViewHolder(view);
            case AD_VIEW_TYPE:
                View nativeExpressLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_native_express_add, parent , false);
                return  new NativeExpressAdViewHolder(nativeExpressLayoutView);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
       int viewType = getItemViewType(position);
       switch (viewType){
           case PRODUCT_ITEM_VIEW_TYPE:
           default :
               ProductViewHolder productViewHolder = (ProductViewHolder)holder;
               setProductView(productViewHolder , position);
               break ;
           case AD_VIEW_TYPE:
               /** Now set the add view in the cardview of adViewHolder **/
               NativeExpressAdViewHolder nativeExpressAdViewHolder = (NativeExpressAdViewHolder) holder;
               NativeExpressAdView adView = (NativeExpressAdView)productArrayList.get(position);
               ViewGroup adCardView = (ViewGroup)nativeExpressAdViewHolder.itemView;
               adCardView.removeAllViews();
               if(adView.getParent()!=null){
                   ((ViewGroup)adView.getParent()).removeView(adView);
               }
               adCardView.addView(adView);
               break ;

       }
    }

    private void setProductView(ProductViewHolder holder, final  int position) {
         final Product product = (Product)productArrayList.get(position);
        holder.productId.setText(product.getProductBarcodeNo());
        holder.currentTime.setText(product.getScanTime());
        holder.currentDate.setText(product.getScanDate());

        holder.search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, WebViewActivity.class);
                intent.putExtra("product_id",product.getProductBarcodeNo());
                context.startActivity(intent);
            }
        });
        holder.copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipBoardManager clipBoardManager = new ClipBoardManager();
                clipBoardManager.copyToClipboard(context,product.getProductBarcodeNo());
                Snackbar.make(v,"Copied To Clipboard",Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        return (position % 4==0 )? AD_VIEW_TYPE : PRODUCT_ITEM_VIEW_TYPE ;
    }



    @Override
    public int getItemCount() {
        return productArrayList.size();
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView search, copy, currentDate, currentTime, productId;

        public ProductViewHolder(View itemView) {
            super(itemView);
            search = (TextView) itemView.findViewById(R.id.search);
            copy = (TextView) itemView.findViewById(R.id.copy);
            currentDate = (TextView) itemView.findViewById(R.id.current_date);
            currentTime = (TextView) itemView.findViewById(R.id.current_time);
            productId = (TextView) itemView.findViewById(R.id.product_code);

        }
    }

    public class NativeExpressAdViewHolder extends  RecyclerView.ViewHolder{
        public NativeExpressAdViewHolder(View itemView) {
            super(itemView);
        }
    }
}

package com.nextinnovation.pt.barcodescanner.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_barcode,parent,false);
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
        holder.txtScanResult.setText(product.getProductBarcodeNo());
        holder.txtScanTime.setText(product.getScanDate()+" "+product.getScanTime());
        holder.txtScanNo.setText(String.valueOf(position+1));

        if(position%2==0){
            holder.layoutRightButtons.setBackgroundColor(context.getResources().getColor(R.color.card_right_blue));
        }
        if(position%3==0){
            holder.layoutRightButtons.setBackgroundColor(context.getResources().getColor(R.color.card_right_purple));
        }


        holder.layoutSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, WebViewActivity.class);
                intent.putExtra("product_id",product.getProductBarcodeNo());
                context.startActivity(intent);
            }
        });
        holder.layoutCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipBoardManager clipBoardManager = new ClipBoardManager();
                clipBoardManager.copyToClipboard(context,product.getProductBarcodeNo());
                Snackbar.make(v,"Copied To Clipboard",Snackbar.LENGTH_SHORT).show();
            }
        });
        holder.btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openShareDialog(product.getProductBarcodeNo());
            }
        });
    }

    private void openShareDialog(String result) {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        String appLink = "https://play.google.com/store/apps/details?id="+context.getPackageName();
        sharingIntent.setType("text/plain");
        String shareBodyText = "Scan Result: "+ result+"."+
                "\nCheck Out The Cool & Fastest Barcode Reader App \n " +
                "Link: "+appLink +" \n" +
                " #Barcode #Android";
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,"Barcode Reader Android App");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBodyText);
        context.startActivity(Intent.createChooser(sharingIntent, "Share"));
    }

    @Override
    public int getItemViewType(int position) {
        if((position%4 ==0) &&  productArrayList.get(position) instanceof NativeExpressAdView){
            return  AD_VIEW_TYPE ;
        }
        else{
            return  PRODUCT_ITEM_VIEW_TYPE;
        }

    }



    @Override
    public int getItemCount() {
        return productArrayList.size();
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout layoutRightButtons ;
        private RelativeLayout layoutCopy , layoutSearch ;
        private TextView txtScanResult , txtScanNo , txtScanTime ;
        private Button btnShare ;

        public ProductViewHolder(View itemView) {
            super(itemView);
        layoutRightButtons = itemView.findViewById(R.id.layout_right_buttons);
        layoutCopy = itemView.findViewById(R.id.layout_copy);
        layoutSearch = itemView.findViewById(R.id.layout_search);
        txtScanNo = itemView.findViewById(R.id.txt_scan_no);
        txtScanResult = itemView.findViewById(R.id.txt_scan_result);
        txtScanTime = itemView.findViewById(R.id.txt_date_time);
        btnShare = itemView.findViewById(R.id.btn_share);

        }
    }

    public class NativeExpressAdViewHolder extends  RecyclerView.ViewHolder{
        public NativeExpressAdViewHolder(View itemView) {
            super(itemView);
        }
    }
}

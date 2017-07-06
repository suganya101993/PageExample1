package com.wish.registry.adapterclasses;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.wish.registry.R;
import com.wish.registry.utilclasses.Constants;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Mindmade technologies on 21-03-2017.
 */
public class RetailerArchiveAdapter extends RecyclerView.Adapter {
    Context mContext;
    ArrayList<HashMap<String, String>> data;
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    private final int VIEW_TYPE_NODATA = 2;
    OnLoadMoreListener loadMoreListener;
    boolean isLoading = false,
            isMoreDataAvailable = true;
    int interval = 10 * 200;

    public RetailerArchiveAdapter(Context context, ArrayList<HashMap<String, String>> passdata) {
        mContext = context;
        data = passdata;

    }

    @Override
    public int getItemViewType(int position) {
        if (data.get(position).get(Constants.TYPE_KEY).equals(Constants.ADAPTER_VIEW_TYPE_LOADING_VALUE)) {
            return VIEW_TYPE_LOADING;
        } else if (data.get(position).get(Constants.TYPE_KEY).equals(Constants.ADAPTER_VIEW_TYPE_DATA_VALUE)) {
            return VIEW_TYPE_ITEM;
        } else {
            return VIEW_TYPE_NODATA;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.retailer_archive_adapter, parent, false);
            return new RetailerArchiveAdapter.RetialerarcheiveViewHolder(view);
        } else if (viewType == VIEW_TYPE_LOADING) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.bottom_loading, parent, false);
            return new ProgressViewHolder(view);
        } else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.nodata_adapter, parent, false);
            return new RetailerArchiveNoData(view);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (position >= getItemCount() - 1 && isMoreDataAvailable && !isLoading && loadMoreListener != null) {
            isLoading = true;
            loadMoreListener.onLoadMore();
        }
        if (holder instanceof RetailerArchiveAdapter.RetialerarcheiveViewHolder) {
            ((RetailerArchiveAdapter.RetialerarcheiveViewHolder) holder).productTitleTV.setText(data.get(position).get("title"));
            ((RetailerArchiveAdapter.RetialerarcheiveViewHolder) holder).productPricrTV.setText(data.get(position).get("price"));
            ((RetailerArchiveAdapter.RetialerarcheiveViewHolder) holder).retialerarchiveProgressBar.setVisibility(View.VISIBLE);
            Glide.with(mContext).load(data.get(position).get("image")).listener(new RequestListener<String, GlideDrawable>() {
                @Override
                public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                    ((RetailerArchiveAdapter.RetialerarcheiveViewHolder) holder).retialerarchiveProgressBar.setVisibility(View.GONE);
                    return false;
                }
                @Override
                public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                    ((RetailerArchiveAdapter.RetialerarcheiveViewHolder) holder).retialerarchiveProgressBar.setVisibility(View.GONE);
                    return false;
                }
            }).error(R.drawable.app_logo).placeholder(Color.TRANSPARENT).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(((RetailerArchiveAdapter.RetialerarcheiveViewHolder) holder).productIV);
        } else if (holder instanceof RetailerArchiveNoData) {
            ((RetailerArchiveNoData) holder).nodataTV.setText(data.get(position).get(Constants.TITLE_KEY));
            hideFooter();
        }

    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    private  class RetialerarcheiveViewHolder extends RecyclerView.ViewHolder {

        ImageView productIV;
        TextView productTitleTV, productPricrTV;
        ImageView retialerarchiveProgressBar;

        public RetialerarcheiveViewHolder(View itemView) {
            super(itemView);
            productIV = (ImageView) itemView.findViewById(R.id.retialer_archive_adapter_IV);
            productTitleTV = (TextView) itemView.findViewById(R.id.retialer_archive_adapter_details_TV);
            productPricrTV = (TextView) itemView.findViewById(R.id.retialer_archive_adapter_price_TV);
            retialerarchiveProgressBar = (ImageView) itemView.findViewById(R.id.retialer_archive_adapter_progressbar);

            Glide.with(mContext).load(R.raw.imageloading)
                    .asGif()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(retialerarchiveProgressBar);
        }
    }

    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        public ProgressViewHolder(View itemView) {
            super(itemView);
        }
    }

    public static class RetailerArchiveNoData extends RecyclerView.ViewHolder {
        TextView nodataTV;

        public RetailerArchiveNoData(View itemView) {
            super(itemView);
            nodataTV = (TextView) itemView.findViewById(R.id.nodataTV);
        }
    }


    public void notifyDataChanged() {
        notifyDataSetChanged();
        isLoading = false;
    }

    public void setMoreDataAvailable(boolean moreDataAvailable) {
        isMoreDataAvailable = moreDataAvailable;
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }

    public void setLoadMoreListener(OnLoadMoreListener loadMoreListener) {
        this.loadMoreListener = loadMoreListener;
        Log.d("Success", "Comes setLoadMoreListener");
    }

    private void hideFooter() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (data.size() > 0) {
                    data.remove(data.size() - 1);
                    notifyDataSetChanged();
                }
            }
        }, interval);

    }
}

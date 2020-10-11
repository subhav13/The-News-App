package com.mainsm.newssampleapp.adopters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.ads.nativetemplates.NativeTemplateStyle;
import com.google.android.ads.nativetemplates.TemplateView;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.mainsm.newssampleapp.R;
import com.mainsm.newssampleapp.models.ArticlesItem;
import com.mainsm.newssampleapp.models.NewsHeadlinesResponse;
import com.mainsm.newssampleapp.ui.news.NewsListActivity;
import com.mainsm.newssampleapp.ui.webview.WebViewActivity;
import com.mainsm.newssampleapp.utils.AppUtils;

import java.text.MessageFormat;
import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private NewsListActivity activity;
    private NewsHeadlinesResponse response;
    private static int AD_VIEW = 111;
    private static int NORMAL = 222;
    private boolean isAdsEnabled;

    public NewsAdapter(Context context, NewsHeadlinesResponse response, boolean isEnabled) {
        this.context = context;
        this.response = response;
        this.isAdsEnabled = isEnabled;
    }



    public NewsAdapter() {
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
        if(viewType == AD_VIEW){
             view = LayoutInflater.from(context).inflate(R.layout.item_ads_row, parent, false);
            return new AdViewHolder(view);
        }else
            view = LayoutInflater.from(context).inflate(R.layout.news_headlines_row, parent, false);
            return new MyHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(getItemViewType(position) == NORMAL){

            MyHolder viewHolder = ((MyHolder)holder);
            ArticlesItem item = response.getArticles().get(position);
            Glide.with(context).load(item.getUrlToImage()).diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.newspaper1)
                    .into(viewHolder.thumbnail);

            viewHolder.title.setText(item.getTitle());
            viewHolder.description.setText(item.getDescription());
            viewHolder.source.setText(item.getSource().getName());
            viewHolder.time.setText(MessageFormat.format(" • {0}", AppUtils.DateToTimeFormat(item.getPublishedAt(), context)));

            viewHolder.itemView.setOnClickListener(view -> {
                Intent intent = new Intent(context, WebViewActivity.class);
                intent.putExtra("url", item.getUrl());
                context.startActivity(intent);
            });

        }
        else if (getItemViewType(position) == AD_VIEW) {
            setUpNativeAds(holder);
        }
    }

    private void setUpNativeAds(RecyclerView.ViewHolder holder) {
        final AdLoader adLoader = new AdLoader.Builder(context, "ca-app-pub-3940256099942544/2247696110")
                .forUnifiedNativeAd(unifiedNativeAd -> {
                    // Show the ad.
                    NativeTemplateStyle styles = new
                            NativeTemplateStyle.Builder().build();

                    TemplateView template = ((AdViewHolder)holder).Adtemplate;
                    template.setStyles(styles);
                    template.setNativeAd(unifiedNativeAd);


                })
                .withAdListener(new AdListener() {
                    @Override
                    public void onAdFailedToLoad(LoadAdError loadAdError) {
                        super.onAdFailedToLoad(loadAdError);
                        ((AdViewHolder)holder).Adtemplate.destroyNativeAd();
                    }
                })
                .withNativeAdOptions(new NativeAdOptions.Builder()
                                             // Methods in the NativeAdOptions.Builder class can be
                                             // used here to specify individual options settings.
                                             .build())
                .build();
        adLoader.loadAd(new AdRequest.Builder().build());
    }



    @Override
    public int getItemViewType(int position) {
        if(position % 3 == 0 && position != 0)
            if(isAdsEnabled)
                return AD_VIEW;
        return NORMAL;

    }


    @Override
    public int getItemCount() {
        return Math.max(response.getArticles().size(), 0);
    }

    static class MyHolder extends RecyclerView.ViewHolder {
        ImageView thumbnail;
        TextView title, description, source, time;
        public MyHolder(@NonNull View itemView) {
            super(itemView);
            thumbnail = itemView.findViewById(R.id.thumb);
            title = itemView.findViewById(R.id.title);
            description = itemView.findViewById(R.id.description);
            source = itemView.findViewById(R.id.source);
            time = itemView.findViewById(R.id.time_ago);

        }
    }

    static class AdViewHolder extends RecyclerView.ViewHolder {
        TemplateView Adtemplate;

        public AdViewHolder(@NonNull View itemView) {
            super(itemView);
            Adtemplate = itemView.findViewById(R.id.my_template);
        }
    }

    }

package adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.feng.jian.nytimessearch.R;
import com.feng.jian.nytimessearch.activities.ArticleActivity;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import java.util.List;

import model.Article;

/**
 * Created by jian_feng on 3/31/17.
 */

public class ArticleArrayAdapter extends RecyclerView.Adapter<ArticleArrayAdapter.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public ImageView imageView;
        public TextView textView;
        public View itemView;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            this.itemView = itemView;
            textView = (TextView) itemView.findViewById(R.id.tvTitle);
            imageView = (ImageView) itemView.findViewById(R.id.ivImage);
        }
    }

    private List<Article> mArticles;
    private Context mContext;

    public ArticleArrayAdapter(Context context, List<Article> articles) {
        mArticles = articles;
        mContext = context;
    }

    private Context getContext() {
        return mContext;
    }

    @Override
    public ArticleArrayAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View articleView = inflater.inflate(R.layout.item_article_result, parent, false);
        ViewHolder viewHolder = new ViewHolder(articleView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ArticleArrayAdapter.ViewHolder holder, final int position) {
        Article article = mArticles.get(position);
        TextView textView = holder.textView;
        ImageView imageView = holder.imageView;

        textView.setText(article.getHeadline());
        imageView.setImageResource(0);
        String thumbnail = article.getThumbnail();
        if (!TextUtils.isEmpty(thumbnail)) {
            Picasso.with(mContext).load(thumbnail).into(imageView);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), ArticleActivity.class);
                Article article = mArticles.get(position);

                i.putExtra("article", Parcels.wrap(article));
                getContext().startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mArticles.size();
    }
}
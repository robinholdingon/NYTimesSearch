package com.feng.jian.nytimessearch.activities;

import android.app.FragmentManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.GridView;

import com.feng.jian.nytimessearch.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

import adapter.ArticleArrayAdapter;
import adapter.SpacesItemDecoration;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.util.TextUtils;
import fragments.FilterPopupFragment;
import fragments.OnFilterData;
import model.Article;

public class SearchActivity extends AppCompatActivity implements OnFilterData {

    GridView gvResults;

    ArrayList<Article> articles;
    ArticleArrayAdapter adapter;

    public static String API_URL = "https://api.nytimes.com/svc/search/v2/articlesearch.json";
    public static String API_KEY = "bd3dfed52104470b9321185cb5c9a593";

    private String date = "2017-03-01";
    private String order;
    private String query = "abc";
    private ArrayList<String> newsDeskValues = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setupViews();

        onArticleSearch();
    }

    private void setupViews() {
        RecyclerView rvArticles = (RecyclerView) findViewById(R.id.gvResults);
        SpacesItemDecoration decoration = new SpacesItemDecoration(1);
        rvArticles.addItemDecoration(decoration);
        articles = new ArrayList<>();
        adapter = new ArticleArrayAdapter(this, articles);
        rvArticles.setAdapter(adapter);

        StaggeredGridLayoutManager gridLayoutManager =
                new StaggeredGridLayoutManager(2,
                        StaggeredGridLayoutManager.VERTICAL);
        rvArticles.setLayoutManager(gridLayoutManager);
//

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        int searchEditId = android.support.v7.appcompat.R.id.search_src_text;
        final EditText et = (EditText) searchView.findViewById(searchEditId);
        et.setTextColor(Color.BLACK);
        et.setHintTextColor(Color.BLACK);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                SearchActivity.this.query = et.getText().toString();
                onArticleSearch();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            return true;
        } else if (id == R.id.action_filter) {
            FragmentManager fm = getFragmentManager();
            Calendar c = Calendar.getInstance();
            String[] dateInfo = date.split("-");
            c.set(Integer.parseInt(dateInfo[0]), Integer.parseInt(dateInfo[1]) - 1, Integer.parseInt(dateInfo[2]));

            FilterPopupFragment.newInstance(order, c.getTime(), newsDeskValues).show(fm, "Filter Popup");
        }

        return super.onOptionsItemSelected(item);
    }

    public void onArticleSearch() {
        AsyncHttpClient client = new AsyncHttpClient();

        RequestParams params = new RequestParams();
        params.put("api-key", API_KEY);
        params.put("page", 0);
        params.put("q", query);
        if (!TextUtils.isEmpty(date)) {
            params.put("begin_date", date.replace("-", ""));
        }
        if (!TextUtils.isEmpty(order)) {
            params.put("sort", order.toLowerCase());
        }
        if (!newsDeskValues.isEmpty()) {
            //fq=news_desk:("Education"%20"Health")
            String ndvQueyr = "news_desk:(";
            for (int i = 0; i < newsDeskValues.size(); i ++) {
                String value = newsDeskValues.get(i);
                String quotes = "%22" + value + "%22";
                if (i < newsDeskValues.size() - 1) {
                    quotes += "%20";
                }
                ndvQueyr += quotes;
            }
            ndvQueyr += ")";
            params.put("fq", ndvQueyr);
        }

        Log.d("DEBUG", params.toString());

        client.get(API_URL, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                JSONArray articleJsonResults;
                try {
                    articleJsonResults = response.getJSONObject("response").getJSONArray("docs");
                    articles.clear();
                    articles.addAll(Article.fromJSONArray(articleJsonResults));
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    Log.e("ERROR" , e.toString());
                }
            }
        });
    }

    @Override
    public void parseFilterData(String date, String order, ArrayList<String> newsDeskValues) {
        this.date = date;
        this.order = order;
        this.newsDeskValues.clear();
        this.newsDeskValues.addAll(newsDeskValues);
        onArticleSearch();
    }
}

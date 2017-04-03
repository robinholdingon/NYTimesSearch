package model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import static utils.Constants.NY_TIMES_DOMAIN;

/**
 * Created by jian_feng on 3/31/17.
 */

@Parcel
public class Article {

    public String webUrl;
    public String headline;
    public String thumbnail = "";

    public static Article fromJson(JSONObject jsonObject) {
        Article article = new Article();
        try {
            article.webUrl = jsonObject.getString("web_url");
            article.headline = jsonObject.getJSONObject("headline").getString("main");

            JSONArray multimedia = jsonObject.getJSONArray("multimedia");
            if (multimedia.length() > 0) {
                JSONObject multimediaJson = multimedia.getJSONObject(0);
                article.thumbnail = NY_TIMES_DOMAIN + multimediaJson.getString("url");
            }
        } catch (JSONException e) {

        }
        return article;
    }

    public static ArrayList<Article> fromJSONArray(JSONArray array) {
        ArrayList<Article> results = new ArrayList<>();

        for (int i = 0; i < array.length(); i ++) {
            try {
                results.add(Article.fromJson(array.getJSONObject(i)));
            } catch (JSONException e) {

            }
        }
        return results;
    }

    public String getWebUrl() {
        return webUrl;
    }

    public String getHeadline() {
        return headline;
    }

    public String getThumbnail() {
        return thumbnail;
    }
}

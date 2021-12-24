package org.dd1929.apod.utils;

import android.net.Uri;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import org.dd1929.apod.models.APOD;

import java.lang.reflect.Type;

public class APODDeserializer implements JsonDeserializer<APOD> {

    private static final String YT_HOST = "youtube.com";
    private static final String YT_SHORT_HOST = "youtu.be";
    private static final String YT_THUMB_URL_BASE = "https://img.youtube.com/vi/";

    @Override
    public APOD deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        String title = null;
        Long date = null;
        String details = null;
        String mediaType = null;
        String url = null;
        String hdUrl = null;
        String copyright = null;
        String thumbUrl = null;

        if (jsonObject.has("title")) {
            title = jsonObject.get("title").getAsString();
        }
        if (jsonObject.has("date")) {
            date = DateParseUtils.getDateFromString(jsonObject.get("date").getAsString()).getTime();
        }
        if (jsonObject.has("explanation")) {
            details = jsonObject.get("explanation").getAsString();
        }
        if (jsonObject.has("url")) {
            url = jsonObject.get("url").getAsString();
        }
        if (jsonObject.has("hdurl")) {
            hdUrl = jsonObject.get("hdurl").getAsString();
        }
        if (jsonObject.has("media_type")) {
            mediaType = jsonObject.get("media_type").getAsString();
        }
        if (jsonObject.has("copyright")) {
            copyright = jsonObject.get("copyright").getAsString();
        }

        String videoId = Uri.parse(url).getLastPathSegment();
        if (videoId.contains("watch")){
            String[] lastSegmentSplit = videoId.split("=");
            videoId = lastSegmentSplit[lastSegmentSplit.length - 1];
        }

        if (Uri.parse(url).getHost().contains(YT_HOST) || Uri.parse(url).getHost().contains(YT_SHORT_HOST)){
            thumbUrl = YT_THUMB_URL_BASE + videoId + "/0.jpg";
        }

        return new APOD(title, url, hdUrl, date, details, mediaType, copyright, thumbUrl,false, false);
    }
}

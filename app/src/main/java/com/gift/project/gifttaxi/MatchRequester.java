package com.gift.project.gifttaxi;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.gift.project.gifttaxi.Dto.MatchDto;
import com.gift.project.gifttaxi.Dto.MatchResultDto;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpContent;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.http.json.JsonHttpContent;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.jackson2.JacksonFactory;

;

public class MatchRequester implements Runnable {
    private Handler handler;
    private MatchDto match;
    private Handler failHandler;

    public MatchRequester(MatchDto match, Handler handler, Handler failHandler) {
        this.handler = handler;
        this.match = match;
        this.failHandler = failHandler;
    }

    @Override
    public void run() {
        try {
            HttpRequestFactory requestFactory = new NetHttpTransport().createRequestFactory(
                    new HttpRequestInitializer() {
                        @Override
                        public void initialize(HttpRequest request) {
                            request.setParser(new JsonObjectParser(new JacksonFactory()));
                        }
                    });
            String urlString = String.format("http://IP:8080/taxis/match");
            GenericUrl url = new GenericUrl(urlString);
            HttpContent content = new JsonHttpContent(new JacksonFactory(), this.match);
            HttpRequest request = requestFactory.buildPostRequest(url, content);
            MatchResultDto matchResult = request.execute().parseAs(MatchResultDto.class);

            Message message = this.handler.obtainMessage();
            message.obj = matchResult;
            this.handler.sendMessage(message);

        } catch (Exception ex) {
            Log.e("HTTP_REQUEST", ex.toString());
            Message message = this.failHandler.obtainMessage();
            HttpResponseException responseException = (HttpResponseException) ex;
            message.what = responseException.getStatusCode();
            this.failHandler.sendMessage(message);
        }
    }
}
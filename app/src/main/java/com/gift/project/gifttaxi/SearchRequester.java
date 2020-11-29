package com.gift.project.gifttaxi;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.gift.project.gifttaxi.models.AddressModel;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.jackson2.JacksonFactory;

import java.io.IOException;

public class SearchRequester implements Runnable {
    private Handler handler;
    private String address;
    public SearchRequester(String address,Handler handler){
        this.handler=handler;
        this.address=address;
    }


    @Override
    public void run() {
        try {
            HttpRequestFactory requestFactory=new NetHttpTransport().createRequestFactory(
                    new HttpRequestInitializer() {
                        @Override
                        public void initialize(HttpRequest request) throws IOException {
                            request.setParser(new JsonObjectParser(new JacksonFactory()));
                        }
                    });
            String urlString=String.format("https://dapi.kakao.com/v2/local/search/address.json?query=%s",this.address);
            GenericUrl url =new GenericUrl(urlString);
            HttpHeaders headers=new HttpHeaders();
            headers.setAuthorization("KakaoAK b313121b7c7901f56df87e42cb9f6524");
            HttpRequest request = requestFactory.buildGetRequest(url).setHeaders(headers);
            AddressModel addressModel =request.execute().parseAs(AddressModel.class);
            Message message = this.handler.obtainMessage();
            message.obj=addressModel;
            this.handler.sendMessage(message);
        } catch (Exception ex) {
            Log.e("HTTP_REQUST",ex.toString());
        }
    }
}

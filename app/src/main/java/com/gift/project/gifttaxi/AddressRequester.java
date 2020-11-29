package com.gift.project.gifttaxi;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.gift.project.gifttaxi.models.AddressModel;
import com.gift.project.gifttaxi.models.DisplayItem;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.jackson2.JacksonFactory;

public class AddressRequester implements Runnable {
    private Handler handler;
    private Double lat;
    private Double lng;

    public AddressRequester(Double lat, Double lng, Handler handler) {
        this.lat = lat;
        this.lng = lng;
        this.handler = handler;
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
            String urlString = String.format("https://dapi.kakao.com/v2/local/geo/coord2address.json?x=%s&y=%s", this.lng.toString(), this.lat.toString());
            GenericUrl url = new GenericUrl(urlString);
            HttpHeaders headers = new HttpHeaders();
            headers.setAuthorization("KakaoAK b313121b7c7901f56df87e42cb9f6524");
            HttpRequest request = requestFactory.buildGetRequest(url).setHeaders(headers);
            AddressModel addressModel = request.execute().parseAs(AddressModel.class);//요청 실행 후 이 형태로 바꿔줘!
            DisplayItem displayItem = new DisplayItem();
            displayItem.addressModel = addressModel;
            displayItem.latitude = this.lat;
            displayItem.longitude = this.lng;
            Message message = this.handler.obtainMessage(); // 너한테 필요한 메세지 생성해줘
            message.obj = displayItem;//객체 전달(obj = 객체) - obj는 메인과의 연결고리
            this.handler.sendMessage(message);//메인 액티비티의 핸들러 부분 작동 (전달)
        } catch (Exception ex) {
            Log.e("HTTP_REQUEST", ex.toString());
        }
    }
}

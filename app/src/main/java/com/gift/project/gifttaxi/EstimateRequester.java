package com.gift.project.gifttaxi;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.gift.project.gifttaxi.Dto.EstimateResultDto;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.jackson2.JacksonFactory;



public class EstimateRequester implements Runnable{
    private Handler handler;
    private Double startLatitude;
    private Double startLongitude;
    private Double endLatitude;
    private Double endLongitude;
    public EstimateRequester(Double startLatitude,Double startLongitude,Double endLatitude,Double endLongitude,Handler handler){
        this.handler=handler;
        this.startLatitude=startLatitude;
        this.startLongitude=startLongitude;
        this.endLatitude=endLatitude;
        this.endLongitude=endLongitude;
    }
    @Override
    public void run(){
        try {
            HttpRequestFactory requestFactory = new NetHttpTransport().createRequestFactory(
                    new HttpRequestInitializer() {
                        @Override
                        public void initialize(HttpRequest request) {
                            request.setParser(new JsonObjectParser(new JacksonFactory()));
                        }
                    });

            //ip 주소를 받아올때 : String urlString = String.format("http://"+자신의 ip 주소:MainActivity.getLocalIpAddress()+":8080/estimate?startLatitude=%s&startLongitude=%s&endLatitude=%s&endLongitude=%s", this.startLatitude.toString(),this.startLongitude.toString(),this.endLatitude.toString(),this.endLongitude.toString());
            String urlString = String.format("http://192.168.0.62:8080/estimate?startLatitude=%s&startLongitude=%s&endLatitude=%s&endLongitude=%s", this.startLatitude.toString(),this.startLongitude.toString(),this.endLatitude.toString(),this.endLongitude.toString());
            GenericUrl url = new GenericUrl(urlString);
            HttpRequest request = requestFactory.buildGetRequest(url);
            EstimateResultDto estimateResult = request.execute().parseAs(EstimateResultDto.class);//요청 실행 후 이 형태로 바꿔줘!
            Message message = this.handler.obtainMessage(); // 너한테 필요한 메세지 생성해줘
            message.obj = estimateResult;//객체 전달(obj = 객체) - obj는 메인과의 연결고리
            this.handler.sendMessage(message);//메인 액티비티의 핸들러 부분 작동 (전달)
        } catch (Exception ex) {
            Log.e("HTTP_REQUEST", ex.toString());
        }
    }
}

package com.gift.project.gifttaxi;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.gift.project.gifttaxi.Dto.EstimateResultDto;
import com.gift.project.gifttaxi.Dto.MatchDto;
import com.gift.project.gifttaxi.Dto.MatchResultDto;
import com.gift.project.gifttaxi.location.MapEventListener;
import com.gift.project.gifttaxi.models.AddressModel;
import com.gift.project.gifttaxi.models.DisplayItem;
import com.gift.project.gifttaxi.models.Documents;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_LOCATION = 10001;
    private LocationManager locationManager;
    private MapEventListener mapEventListener;
    private MapView mapView;
    private boolean isSelectDeparture=true;
    private boolean isSelectDone=false;
    private MatchDto match;

    public MainActivity() {
        //this.locationListener = new MapLocationListener(); //생성자
        this.mapEventListener = new MapEventListener(makeHandler());
        this.match = new MatchDto();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 안드로이드에서 권한 확인이 의무화 되어서 작성된 코드! 개념만 이해
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_LOCATION);
                return;
            }
        }

        this.locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //this.locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0.01f, this.locationListener);
        this.mapView = new MapView(this); //세터(?)
        //this.locationListener.setMapView(mapView);//지도 전달
        Location loc = this.locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        //this.locationListener.setMapView(mapView);//지도 전달
        mapView.setMapViewEventListener(this.mapEventListener);
        ViewGroup mapViewContainer = (ViewGroup) findViewById(R.id.map_view);
        mapViewContainer.addView(mapView);

        if (loc != null) {
            mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(loc.getLatitude(), loc.getLongitude()), true);

            MapPOIItem marker = new MapPOIItem();
            marker.setItemName("현재 위치");
            marker.setTag(0);
            marker.setMapPoint(MapPoint.mapPointWithGeoCoord(loc.getLatitude(), loc.getLongitude()));
            marker.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
            marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.

            mapView.addPOIItem(marker);

        }
    }private Handler makeHandler() {
        return new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                TextView position;//주소를 저장하는 곳

                if(isSelectDone){
                    return;
                }

                //AddressModel addressModel(AddressModel)msg.obj;
                //위도 경도 출력
                DisplayItem displayItem = (DisplayItem)msg.obj;
                AddressModel addressModel=displayItem.addressModel;

                if(isSelectDeparture){
                    position=findViewById(R.id.departure);
                    match.startLatitude=displayItem.latitude;
                    match.startLongitude=displayItem.longitude;
                }else{
                    position=findViewById(R.id.arrival);
                    match.endLatitude=displayItem.latitude;
                    match.endLongitude=displayItem.longitude;
                }

                position.setText(displayItem.latitude.toString());
                position.setText(displayItem.longitude.toString());

                //방어할수 있는 코드
                if(addressModel.documents.size() > 0){ // 위치 추척(?) 안 될 시 방어 코드(조건문)
                    Documents documents = addressModel.documents.get(0);
                    if(documents.roadaddress !=null && documents.roadaddress.addressName != null) { //도로명주소가 존재하지 않을 때
                        //position.setText(documents.roadaddress.addressName);
                        if(documents.roadaddress.buildingName != null && documents.roadaddress.buildingName.length() > 0){ //건물명이 존재하지 않을 때
                            position.setText(documents.roadaddress.buildingName);
                        }else {
                            position.setText(documents.roadaddress.addressName);
                        }
                    }
                    else{
                        position.setText(documents.address.addressName);
                    }

                    position.setText(addressModel.documents.get(0).roadaddress.buildingName);
                    position.setText(addressModel.documents.get(0).address.addressName);
                }

            }
        };
    }






    private Handler makeSearchAddressHandler() {
        return new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg){
                super.handleMessage(msg);
                AddressModel addressModel=(AddressModel)msg.obj;
                 final ListView listView=findViewById(R.id.search_result);
                listView.setVisibility(View.VISIBLE);
                AddressListAdapter adapter =new AddressListAdapter(addressModel.documents,getBaseContext());
                listView.setAdapter(adapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                        Documents documents=(Documents)parent.getItemAtPosition(position);
                        Double Latitude=Double.parseDouble(documents.latitude);
                        Double Longitude=Double.parseDouble(documents.longitude);
                        if(isSelectDone){
                            match.startLatitude=Latitude;
                            match.startLongitude=Longitude;
                        }else{
                            match.endLatitude=Latitude;
                            match.endLongitude=Longitude;
                        }
                        MapPoint mapPoint=MapPoint.mapPointWithGeoCoord(Latitude,Longitude);
                        MapPOIItem marker=mapView.findPOIItemByTag(0);
                        if(marker != null){
                            marker.setMapPoint(mapPoint);
                        }else{
                            marker=new MapPOIItem();
                            marker.setTag(0);
                            marker.setMapPoint(mapPoint);
                            marker.setMarkerType(MapPOIItem.MarkerType.BluePin);
                            mapView.addPOIItem(marker);
                        }
                        listView.setVisibility(View.GONE);
                    }
                });
            }
        };
    }

    public void searchAddress(View view){
        if(isSelectDone){
            return;
        }
        EditText search= findViewById(R.id.search);
        String inputAddress=search.getText().toString();
        if(inputAddress.length()>0){
            Handler handler =makeSearchAddressHandler();
            SearchRequester requester=new SearchRequester(inputAddress, handler);
            Thread request=new Thread(requester);
            request.start();
        }
    }

    public void toggleDepartureSelect(View clicked){
        LinearLayout estimateView=findViewById(R.id.estimate_view);
        estimateView.setVisibility(View.GONE);
        this.isSelectDone=false;
        Button arrivalButton=findViewById(R.id.arrival_select);
        Button arrivalResetButton=findViewById(R.id.arrival_select);
        if(clicked.getId()==R.id.departure_select){
            this.isSelectDeparture=false;
            Button resetButton=findViewById(R.id.departure_select_reset);
            resetButton.setVisibility(View.VISIBLE);
            arrivalButton.setVisibility(View.VISIBLE);
            clicked.setVisibility(View.GONE);
        }else{
            this.isSelectDeparture=true;
            Button departureButton=findViewById(R.id.departure_select);
            departureButton.setVisibility(View.VISIBLE);
            arrivalButton.setVisibility(View.INVISIBLE);
            arrivalResetButton.setVisibility(View.GONE);
            clicked.setVisibility(View.GONE);
        }
    }
    public void toggleArrivalSelect(View clicked){
        LinearLayout estimateView=findViewById(R.id.estimate_view);
        if(clicked.getId()==R.id.arrival_select){
            this.isSelectDone=true;
            Button arrivalResetButton=findViewById(R.id.arrival_select_reset);
            arrivalResetButton.setVisibility(View.VISIBLE);
            clicked.setVisibility(View.GONE);
            estimateView.setVisibility(View.VISIBLE);
        }else {
            this.isSelectDone=false;
            Button arrivalButton=findViewById(R.id.arrival_select);
            arrivalButton.setVisibility(View.VISIBLE);
            clicked.setVisibility(View.GONE);
            estimateView.setVisibility(View.GONE);
        }

    }
    public void estimate(View view) {
        Handler handler = this.makeEstimateHandler();
        EstimateRequester requester = new EstimateRequester(
                match.startLatitude,
                match.startLongitude,
                match.endLatitude,
                match.endLongitude,
                handler);
        Thread request = new Thread(requester);
        request.start();
    }

    public void match(View view) {
        Handler handler = this.makeMatchHandler();
        Handler failHandler = this.makeMatchFailedHandler();
        MatchRequester requester = new MatchRequester(this.match, handler, failHandler);
        Thread request = new Thread(requester);
        request.start();
    }

    private Handler makeEstimateHandler() {
        return new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                EstimateResultDto result = (EstimateResultDto) msg.obj;
                TextView distance = findViewById(R.id.estimate_distance);
                TextView time = findViewById(R.id.estimate_time);
                TextView cost = findViewById(R.id.estimate_cost);
                DecimalFormat distanceFormat = new DecimalFormat("##0.00km");
                distance.setText(distanceFormat.format(result.estimateDistance));
                time.setText(result.estimateTime + "분");
                cost.setText(result.estimateCost + "원");

                LinearLayout matchView = findViewById(R.id.match_view);
                matchView.setVisibility(View.VISIBLE);
            }
        };
    }

    private Handler makeMatchHandler() {
        return new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                MatchResultDto result = (MatchResultDto) msg.obj;
                TextView matchDriver = findViewById(R.id.match_driver);
                TextView matchTaxi = findViewById(R.id.match_taxi);
                TextView arrivalTime = findViewById(R.id.arrival_time);
                matchDriver.setText(result.driver);
                matchTaxi.setText(result.taxiNumber);
                arrivalTime.setText(result.arrivalTime);
            }
        };
    }

    private Handler makeMatchFailedHandler() {
        final Activity activity = this;
        return new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                switch (msg.what) {
                    case 401:
                        builder.setTitle("인증 실패").setMessage("올바른 사용자가 아닙니다");
                        break;
                    case 404:
                        builder.setTitle("배정 실패").setMessage("배정 가능한 택시가 없습니다");
                        break;
                    default:
                        builder.setTitle("오류").setMessage("오류가 발생했습니다");
                        break;
                }
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        };
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 권한 승인이 된 경우 다시 그리기
                    recreate();
                } else {
                    // 권한 승인이 안 된 경우 종료
                    finish();
                }
                break;
            default:
                break;
        }
    }

//ip 주소를 받아오는 클래스
//    public static String getLocalIpAddress() {
//        try {
//            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
//                NetworkInterface intf = en.nextElement();
//                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
//                    InetAddress inetAddress = enumIpAddr.nextElement();
//                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
//                        return inetAddress.getHostAddress();
//                    }
//                }
//            }
//        } catch (SocketException ex) {
//            ex.printStackTrace();
//        }
//        return null;
//    }

}

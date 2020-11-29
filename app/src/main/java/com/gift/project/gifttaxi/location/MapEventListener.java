package com.gift.project.gifttaxi.location;

import android.os.Handler;

import com.gift.project.gifttaxi.AddressRequester;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

public class MapEventListener implements MapView.MapViewEventListener {

    private Handler handler;

    public MapEventListener(final Handler handler) {
        this.handler = handler;
    }

    @Override
    public void onMapViewInitialized(MapView mapView) {

    }

    @Override
    public void onMapViewCenterPointMoved(MapView mapView, MapPoint mapPoint) {
        MapPOIItem marker = mapView.findPOIItemByTag(0);
        if (marker != null) {
            marker.setMapPoint(mapPoint);
        } else {
            marker = new MapPOIItem();
            marker.setItemName("현재 위치");
            marker.setTag(0);
            marker.setMapPoint(mapPoint);
            marker.setMarkerType(MapPOIItem.MarkerType.BluePin);
            mapView.addPOIItem(marker);
        }
    }

    @Override
    public void onMapViewZoomLevelChanged(MapView mapView, int i) {

    }

    @Override
    public void onMapViewSingleTapped(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDoubleTapped(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewLongPressed(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDragStarted(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDragEnded(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewMoveFinished(MapView mapView, MapPoint mapPoint) {
        MapPoint.GeoCoordinate geoCoordinate = mapPoint.getMapPointGeoCoord();
        Thread request = new Thread(new AddressRequester(geoCoordinate.latitude, geoCoordinate.longitude, this.handler));
        request.start();
    }
}

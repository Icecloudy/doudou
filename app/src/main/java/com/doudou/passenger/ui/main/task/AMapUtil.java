/**
 *
 */
package com.doudou.passenger.ui.main.task;

import com.amap.api.maps.model.LatLng;
import com.amap.api.services.core.LatLonPoint;

public class AMapUtil {


    /**
     * 把LatLonPoint对象转化为LatLon对象
     */
    public static LatLng convertToLatLng(LatLonPoint latLonPoint) {
        return new LatLng(latLonPoint.getLatitude(), latLonPoint.getLongitude());
    }
}

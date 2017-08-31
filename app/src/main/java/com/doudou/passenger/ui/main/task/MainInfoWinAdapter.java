package com.doudou.passenger.ui.main.task;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.doudou.passenger.MyApplication;
import com.doudou.passenger.R;

/**
 * Created by Administrator on 2017/5/13.
 */

public class MainInfoWinAdapter implements AMap.InfoWindowAdapter {
    private Context mContext = MyApplication.getInstance();
    private LatLng latLng;
    private TextView nameTV;
    private String content;

    @Override
    public View getInfoWindow(Marker marker) {
        initData(marker);
        View view = initView();
        return view;
    }
    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    private void initData(Marker marker) {
        latLng = marker.getPosition();
        content = marker.getSnippet();
    }

    @NonNull
    private View initView() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.window_marker, null);
        nameTV = (TextView) view.findViewById(R.id.markerTips);
        nameTV.setText(content);
        return view;
    }

}

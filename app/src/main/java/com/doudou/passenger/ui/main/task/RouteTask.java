/**
 * Project Name:Android_Car_Example
 * File Name:RouteTask.java
 * Package Name:com.amap.api.car.example
 * Date:2015年4月3日下午2:38:10
 */

package com.doudou.passenger.ui.main.task;

import android.content.Context;

import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.RouteSearch.DriveRouteQuery;
import com.amap.api.services.route.RouteSearch.FromAndTo;
import com.amap.api.services.route.RouteSearch.OnRouteSearchListener;
import com.amap.api.services.route.WalkRouteResult;
import com.doudou.passenger.data.models.OrderRule;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * ClassName:RouteTask <br/>
 * Function: 封装的驾车路径规划 <br/>
 * Date: 2015年4月3日 下午2:38:10 <br/>
 *
 * @author yiyi.qi
 * @version
 * @since JDK 1.6
 * @see
 */
public class RouteTask implements OnRouteSearchListener {

    private static RouteTask mRouteTask;

    private RouteSearch mRouteSearch;

    private PositionEntity mFromPoint;

    private PositionEntity mToPoint;

    private List<OnRouteCalculateListener> mListeners = new ArrayList<OnRouteCalculateListener>();

    public interface OnRouteCalculateListener {
        public void onRouteCalculate(float cost, float distance, int duration);

    }

    public static RouteTask getInstance(Context context) {
        if (mRouteTask == null) {
            mRouteTask = new RouteTask(context);
        }
        return mRouteTask;
    }

    public PositionEntity getStartPoint() {
        return mFromPoint;
    }


    public void setStartPoint(PositionEntity fromPoint) {
        mFromPoint = fromPoint;
    }

    public PositionEntity getEndPoint() {
        return mToPoint;
    }

    public void setEndPoint(PositionEntity toPoint) {
        mToPoint = toPoint;
    }

    private RouteTask(Context context) {
        mRouteSearch = new RouteSearch(context);
        mRouteSearch.setRouteSearchListener(this);
    }

    public void search() {
        if (mFromPoint == null || mToPoint == null) {
            return;
        }

        FromAndTo fromAndTo = new FromAndTo(new LatLonPoint(mFromPoint.latitude,
                mFromPoint.longitude), new LatLonPoint(mToPoint.latitude,
                mToPoint.longitude));
        DriveRouteQuery driveRouteQuery = new DriveRouteQuery(fromAndTo,
                RouteSearch.DrivingDefault, null, null, "");

        mRouteSearch.calculateDriveRouteAsyn(driveRouteQuery);
    }

    public void search(PositionEntity fromPoint, PositionEntity toPoint) {

        mFromPoint = fromPoint;
        mToPoint = toPoint;
        search();

    }

    public void addRouteCalculateListener(OnRouteCalculateListener listener) {
        synchronized (this) {
            if (mListeners.contains(listener))
                return;
            mListeners.add(listener);
        }
    }

    public void removeRouteCalculateListener(OnRouteCalculateListener listener) {
        synchronized (this) {
            mListeners.remove(listener);
        }
    }

    @Override
    public void onBusRouteSearched(BusRouteResult arg0, int arg1) {

        // TODO Auto-generated method stub

    }

    public float predictValue(float distance, int duration) {
        int BEGAN_MILEAGE = 3;      //起步价中包含的公里数
        int BEGAN_TIME = 10;        //起步价中包含的时长

        //写死计费逻辑，以后要从服务器获取
        OrderRule rule = new OrderRule();
        rule.setBegan(8);
        rule.setMileage(1.7);
        rule.setDuration(0.4);

        List <OrderRule.Remote> remoteList = new ArrayList<>();
        OrderRule.Remote remote = new OrderRule.Remote();
        remote.setDistance(10);
        remote.setExtraCost(0.7);
        remoteList.add(remote);
        rule.setRemote(remoteList);

        rule.setNight(5);
        rule.setAdditional(0);

        //开始计费
        /*A = 起步费a，固定值
        B = (实际里程S - 起步里程a1) x 里程单价b) 若B<0，则B=0
        C = (实际时长T - 起步时长a2) x 时长单价c) 若C<0，则C=0
        D = (实际里程S - 远途里程d1) x 远途单价d) 若D<0，则D=0
        E = 夜间费e，若不在特定时间段，则E=0
        I = 附加费i，固定值
        总价 = A+B+C+D+E+I*/

        double beganPrice = rule.getBegan(); //起步价
        double distancePrice = (distance - BEGAN_MILEAGE) * rule.getMileage();
        if (distancePrice < 0) {
            distancePrice = 0;
        }

        double timePrice = (duration - BEGAN_TIME) * rule.getDuration();
        if (timePrice < 0) {
            timePrice = 0;
        }

        double remotePrice = (distance - rule.getRemote().get(0).getDistance()) * rule.getRemote().get(0).getExtraCost();
        if (remotePrice < 0) {
            remotePrice = 0;
        }

        int nightPrice = 0;
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        if (hour >= 22 || hour < 6) {
            nightPrice = 0;
        }

        double totalPrice = beganPrice + distancePrice + timePrice + remotePrice + nightPrice;
        return (float)totalPrice;
    }

    @Override
    public void onDriveRouteSearched(DriveRouteResult driveRouteResult,
                                     int resultCode) {
        if (resultCode == AMapException.CODE_AMAP_SUCCESS && driveRouteResult != null) {
            synchronized (this) {
                for (OnRouteCalculateListener listener : mListeners) {
                    List<DrivePath> drivepaths = driveRouteResult.getPaths();
                    float distance = 0;
                    int duration = 0;
                    if (drivepaths.size() > 0) {
                        DrivePath drivepath = drivepaths.get(0);

                        distance = drivepath.getDistance() / 1000;

                        duration = (int) (drivepath.getDuration() / 60);
                    }

                    //float cost = driveRouteResult.getTaxiCost();
                    float cost = predictValue(distance, duration);

                    listener.onRouteCalculate(cost, distance, duration);
                }
            }
        }
        // TODO 可以根据app自身需求对查询错误情况进行相应的提示或者逻辑处理
    }

    @Override
    public void onWalkRouteSearched(WalkRouteResult arg0, int arg1) {

        // TODO Auto-generated method stub

    }

    @Override
    public void onRideRouteSearched(RideRouteResult arg0, int arg1) {

        // TODO Auto-generated method stub

    }
}

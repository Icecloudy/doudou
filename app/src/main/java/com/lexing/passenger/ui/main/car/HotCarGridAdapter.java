package com.lexing.passenger.ui.main.car;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lexing.passenger.R;
import com.lexing.passenger.data.models.CarBean;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * author zaaach on 2016/1/26.
 */
public class HotCarGridAdapter extends BaseAdapter {
    private Context mContext;
    private List<CarBean> mHotCar;

    public HotCarGridAdapter(Context mContext, List<CarBean> mHotCar) {
        this.mContext = mContext;
        this.mHotCar = mHotCar;
    }

    @Override
    public int getCount() {
        return mHotCar == null ? 0 : mHotCar.size();
    }

    @Override
    public CarBean getItem(int position) {
        return mHotCar == null ? null : mHotCar.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        HotCityViewHolder holder;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_hot_car, parent, false);
            holder = new HotCityViewHolder();
            holder.name = (TextView) view.findViewById(R.id.carName);
            holder.carIcon = (ImageView) view.findViewById(R.id.carIcon);
            view.setTag(holder);
        } else {
            holder = (HotCityViewHolder) view.getTag();
        }
        holder.setData(mHotCar, position);
        return view;
    }

    class HotCityViewHolder {
        TextView name;
        ImageView carIcon;

        private void setData(List<CarBean> mHotCar, int position) {
            name.setText(mHotCar.get(position).getBrand());
            Picasso.with(mContext)
                    .load(mHotCar.get(position).getPicture())
                    .into(carIcon);
        }
    }
}

package com.lexing.passenger.ui.main.car;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lexing.passenger.R;
import com.lexing.passenger.data.models.CarModelBean;
import com.lexing.passenger.data.models.CarModelChildBean;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by Administrator on 2017/5/22.
 */

public class CarModelAdapter extends BaseExpandableListAdapter {


    private Context mContext;
    private List<CarModelBean> mCar;

    public CarModelAdapter(Context mContext, List<CarModelBean> mCar) {
        this.mContext = mContext;
        this.mCar = mCar;

    }

    @Override
    public int getGroupCount() {
        return mCar == null ? 0 : mCar.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mCar.get(groupPosition).getModel() == null ? 0 : mCar.get(groupPosition).getModel().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mCar.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mCar.get(groupPosition).getModel().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View view, ViewGroup parent) {
        CarTitleViewHolder holder;
        if (view == null) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_car_list_title, null);
            holder = new CarTitleViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (CarTitleViewHolder) view.getTag();
        }
        holder.setData(mCar, groupPosition);
        return view;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View view, ViewGroup parent) {

        CarViewHolder holder;
        if (view == null) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_car_model, null);
            holder = new CarViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (CarViewHolder) view.getTag();
        }
        holder.setData(mCar.get(groupPosition).getModel(), childPosition);
        return view;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    class CarViewHolder {
        @BindView(R.id.carName)
        TextView name;
        @BindView(R.id.carIcon)
        ImageView carIcon;
        @BindView(R.id.carPrice)
        TextView carPrice;


        CarViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

        private void setData(List<CarModelChildBean> mCities, int position) {
            name.setText(mCities.get(position).getModel());
            Picasso.with(mContext)
                    .load(mCities.get(position).getPhoto())
                    .into(carIcon);
            if (TextUtils.isEmpty(mCities.get(position).getMin())){
                carPrice.setText(mCities.get(position).getMax()+"万");
            }else{
                carPrice.setText(mCities.get(position).getMin()+"-"+mCities.get(position).getMax()+"万");
            }
        }
    }

    class CarTitleViewHolder {
        @BindView(R.id.letter)
        TextView letter;

        CarTitleViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

        private void setData(List<CarModelBean> allCarLists, int position) {
            letter.setText(allCarLists.get(position).getType().getName());
        }
    }


}

package com.doudou.passenger.ui.main.car;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.doudou.passenger.R;
import com.doudou.passenger.data.models.AllCarList;
import com.doudou.passenger.data.models.CarBean;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by Administrator on 2017/5/22.
 */

public class CarListAdapter extends BaseExpandableListAdapter {


    private Context mContext;
    private List<AllCarList> mCar;

    private HashMap<String, Integer> letterIndexes;
    private String[] sections;
    public CarListAdapter(Context mContext, List<AllCarList> mCar) {
        this.mContext = mContext;
        this.mCar = mCar;
        mCar.add(0, new AllCarList("热", null));
        mCar.add(1, new AllCarList("选", null));
        int size = mCar.size();
        letterIndexes = new HashMap<>();
        sections = new String[size];
        for (int index = 0; index < size; index++){
            //当前城市拼音首字母
            String currentLetter = mCar.get(index).getTitle();
            //上个首字母，如果不存在设为""
            String previousLetter = index >= 1 ? mCar.get(index-1).getTitle() : "";
            if (!TextUtils.equals(currentLetter, previousLetter)){
                letterIndexes.put(currentLetter, index);
                sections[index] = currentLetter;
            }
        }

    }
    /**
     * 获取字母索引的位置
     * @param letter
     * @return
     */
    public int getLetterPosition(String letter){
        Integer integer = letterIndexes.get(letter);
        return integer == null ? -1 : integer;
    }

    @Override
    public int getGroupCount() {
        return mCar == null ? 0 : mCar.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mCar.get(groupPosition).getBrands() == null ? 0 : mCar.get(groupPosition).getBrands().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mCar.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mCar.get(groupPosition).getBrands().get(childPosition);
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
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_car_list,null);
            holder = new CarViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (CarViewHolder) view.getTag();
        }
        holder.setData(mCar.get(groupPosition).getBrands(), childPosition);
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

        CarViewHolder(View view){
            ButterKnife.bind(this,view);
        }
        private void setData(List<CarBean> mCities, int position) {
            name.setText(mCities.get(position).getBrand());
            Picasso.with(mContext)
                    .load(mCities.get(position).getPicture())
                    .into(carIcon);
        }
    }

    class CarTitleViewHolder {
        @BindView(R.id.letter)
        TextView letter;
        CarTitleViewHolder(View view){
            ButterKnife.bind(this,view);
        }
        private void setData(List<AllCarList> allCarLists, int position) {
            if (allCarLists.get(position).getBrands()!=null&&allCarLists.get(position).getBrands().size()>0){
                allCarLists.get(position).setType(AllCarList.TYPE_VISIBILITY);
            }else{
                allCarLists.get(position).setType(AllCarList.TYPE_GONE);

            }

            if (allCarLists.get(position).getType()==AllCarList.TYPE_GONE){
                letter.setVisibility(View.GONE);
            }else{
                letter.setVisibility(View.VISIBLE);
                letter.setText(allCarLists.get(position).getTitle());
            }
        }
    }


}

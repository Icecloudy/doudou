/**  
 * Project Name:Android_Car_Example  
 * File Name:RecomandAdapter.java  
 * Package Name:com.amap.api.car.example  
 * Date:2015年4月3日上午11:29:45  
 *  
 */

package com.doudou.passenger.ui.main.task;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.doudou.passenger.R;

import java.util.Arrays;
import java.util.List;


/**
 * ClassName:RecomandAdapter <br/>
 * Function: 显示的poi列表 <br/>
 * Date: 2015年4月3日 上午11:29:45 <br/>
 * 
 * @author yiyi.qi
 * @version
 * @since JDK 1.6
 * @see
 */
public class RecomandAdapter extends BaseAdapter {

	PositionEntity[] entities = new PositionEntity[] {
//			new PositionEntity(22.575149, 113.863048, "西乡(地铁站)","深圳市"),
//			new PositionEntity(22.534607, 113.972976, "世界之窗","深圳市"),
//			new PositionEntity(22.63336, 113.814549, "深圳宝安国际机场","深圳市"),
//			new PositionEntity(22.59242, 113.30797, "大梅沙海滨公园","深圳市"),
	};

	private List<PositionEntity> mPositionEntities;

	private Context mContext;

	public RecomandAdapter(Context context) {
		mContext = context;
		mPositionEntities = Arrays.asList(entities);
	
	}

	public void setPositionEntities(List<PositionEntity> entities) {
		this.mPositionEntities = entities;

	}

	@Override
	public int getCount() {

		// TODO Auto-generated method stub
		return mPositionEntities.size();
	}

	@Override
	public Object getItem(int position) {

		return mPositionEntities.get(position);
	}

	@Override
	public long getItemId(int position) {

		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			LayoutInflater inflater = LayoutInflater.from(mContext);
			convertView = inflater.inflate(R.layout.view_recommond,null);
			viewHolder.textView = (TextView) convertView.findViewById(R.id.location);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.textView.setText(mPositionEntities.get(position).address);
		return convertView;
	}

	 class ViewHolder{
		 TextView textView;
	 }
}

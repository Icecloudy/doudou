package com.doudou.passenger.ui.main.booking;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.doudou.passenger.R;
import com.doudou.passenger.data.models.CarBean;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2017/4/13.
 */

public class CarAdapter extends RecyclerView.Adapter<CarAdapter.BillViewHolder> {


    private List<CarBean> mList;
    private Context context;

    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void clickListener(View view, int i);
    }

    public CarAdapter(List<CarBean> mList, Context context) {
        this.mList = mList;
        this.context = context;
    }

    @Override
    public BillViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_car, parent, false);
        return new BillViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    @Override
    public void onBindViewHolder(BillViewHolder holder, int position) {
        holder.setData(mList.get(position),position);
    }




    class BillViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvCarName)
        TextView tvCarName;

        public BillViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        private void setData(CarBean data,int position) {
            tvCarName.setText(data.getBrand());
            tvCarName.setTag(position);
        }
        @OnClick(R.id.tvCarName)
        public void onViewClicked(View view) {
            int position = (int) view.getTag();
            if (onItemClickListener != null) {
                onItemClickListener.clickListener(view, position);
            }
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}

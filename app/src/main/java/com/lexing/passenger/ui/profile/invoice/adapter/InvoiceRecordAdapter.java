package com.lexing.passenger.ui.profile.invoice.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lexing.passenger.R;
import com.lexing.passenger.ui.profile.record.TripRecordBean;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Created by Administrator on 2017/5/22.
 */

public class InvoiceRecordAdapter extends BaseExpandableListAdapter {


    private Context mContext;
    private List<InvoiceRecordBean> mList = new ArrayList<>();


    public InvoiceRecordAdapter(Context mContext) {
        this.mContext = mContext;

    }
    public void setData(List<InvoiceRecordBean> lists){
        if (null != lists && 0 != lists.size()) {
            mList.clear();
            mList.addAll(lists);
            notifyDataSetChanged();
        }
    }
    @Override
    public int getGroupCount() {
        return mList == null ? 0 : mList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mList.get(groupPosition).getOrderList() == null ? 0 : mList.get(groupPosition).getOrderList().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mList.get(groupPosition).getOrderList().get(childPosition);
    }

    public TripRecordBean getItem(int groupPosition, int childPosition) {
        return mList.get(groupPosition).getOrderList().get(childPosition);
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
        holder.setData(mList, groupPosition);
        return view;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View view, ViewGroup parent) {

        CarViewHolder holder;
        if (view == null) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_invoice_record, null);
            holder = new CarViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (CarViewHolder) view.getTag();
        }
        holder.setData(mList.get(groupPosition).getOrderList(), groupPosition, childPosition);
        return view;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }


    class CarViewHolder {
        @BindView(R.id.setOutLocation)
        TextView setOutLocation;
        @BindView(R.id.tvInvoiceMoney)
        TextView tvInvoiceMoney;
        @BindView(R.id.EsLocation)
        TextView EsLocation;
        @BindView(R.id.orderDate)
        TextView orderDate;
        @BindView(R.id.layoutInvoice)
        RelativeLayout layoutInvoice;

        private int groupPosition;
        private int childPosition;

        CarViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

        private void setData(List<TripRecordBean> data, int groupPosition, int position) {
            this.childPosition = position;
            this.groupPosition = groupPosition;
            setOutLocation.setText(data.get(position).getAddresses());
            tvInvoiceMoney.setText(String.valueOf(data.get(position).getPrice()));
            EsLocation.setText(data.get(position).getDown());
            orderDate.setText(data.get(position).getGotime());
            layoutInvoice.setSelected(data.get(position).isChecked());
        }

        @OnClick(R.id.layoutInvoice)
        public void onViewClicked(View v) {
            getItem(groupPosition, childPosition).toggle();
            notifyDataSetChanged();
            if (onItemClickListener != null) {
                onItemClickListener.clickListener(v, groupPosition, childPosition);
            }
        }

    }

    class CarTitleViewHolder {
        @BindView(R.id.letter)
        TextView letter;

        CarTitleViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

        private void setData(List<InvoiceRecordBean> allCarLists, int position) {
            letter.setText(allCarLists.get(position).getDate());
        }
    }

    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void clickListener(View view, int j, int i);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

}

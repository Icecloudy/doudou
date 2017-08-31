package com.lexing.passenger.ui.profile.wallet.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lexing.passenger.R;
import com.lexing.passenger.ui.profile.wallet.data.MoneyBean;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2017/4/13.
 */

public class MoneyAdapter extends RecyclerView.Adapter<MoneyAdapter.MoneyViewHolder> {

    private List<MoneyBean> mList;
    private Context context;

    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void clickListener(View view, int i);
    }

    public MoneyAdapter(List<MoneyBean> mList, Context context) {
        this.mList = mList;
        this.context = context;
    }

    @Override
    public MoneyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_money, parent, false);
        return new MoneyViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    @Override
    public void onBindViewHolder(MoneyViewHolder holder, int position) {
        holder.setData(mList.get(position));
        holder.setPosition(position);
    }

    private  MoneyBean getItem(int i){
        return mList.get(i);
    }

    class MoneyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvPayMoney)
        TextView tvPayMoney;
        @BindView(R.id.tvGiveMoney)
        TextView tvGiveMoney;
        @BindView(R.id.layoutItem)
        RelativeLayout layoutItem;
        private int pos;


        public MoneyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
        private void setData(MoneyBean data){
            tvPayMoney.setText(context.getString(R.string.pay_money,(int)data.getPayMoney()));
            tvGiveMoney.setText(context.getString(R.string.give_money,(int)data.getGiveMoney()));

        }
        private void setPosition(int position) {
            this.pos = position;
            MoneyBean moneyBean = getItem(position);

            if(moneyBean.isChecked()){
                layoutItem.setBackgroundResource(R.drawable.shape_money_select_bg);
            }else{
                layoutItem.setBackgroundResource(R.drawable.shape_money_unselect_bg);
            }
        }
        @OnClick(R.id.layoutItem)
        public void onViewClicked(View v) {
            for (MoneyBean moneyBean:mList){
                moneyBean.setChecked(false);
            }
            getItem(pos).setChecked(true);
            notifyDataSetChanged();
            if (onItemClickListener != null) {
                onItemClickListener.clickListener(v, pos);
            }
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
    public void cleanItem(){
        for (MoneyBean moneyBean:mList){
            moneyBean.setChecked(false);
        }
        notifyDataSetChanged();
    }
}

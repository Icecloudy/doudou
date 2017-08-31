package com.lexing.passenger.ui.profile.wallet.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lexing.passenger.R;
import com.lexing.passenger.ui.profile.wallet.data.BillBean;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.lexing.passenger.R.id.tvPayType;

/**
 * Created by Administrator on 2017/4/13.
 */

public class BillAdapter extends RecyclerView.Adapter<BillAdapter.BillViewHolder> {


    private List<BillBean> mList;
    private Context context;

    public BillAdapter(List<BillBean> mList, Context context) {
        this.mList = mList;
        this.context = context;
    }

    @Override
    public BillViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bill, parent, false);
        return new BillViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    @Override
    public void onBindViewHolder(BillViewHolder holder, int position) {
        holder.setData(mList.get(position));
    }


    class BillViewHolder extends RecyclerView.ViewHolder {
        @BindView(tvPayType)
        TextView tvPayTitle;
        @BindView(R.id.tvPayDate)
        TextView tvPayDate;
        @BindView(R.id.plusMoney)
        TextView plusMoney;
        @BindView(R.id.reduceMoney)
        TextView reduceMoney;


        public BillViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        private void setData(BillBean data) {
            tvPayTitle.setText(data.getTitle());
            tvPayDate.setText(data.getCreatetime());
            if (data.getType()==101){
                plusMoney.setText("+"+data.getMoney());
                plusMoney.setVisibility(View.VISIBLE);
                reduceMoney.setVisibility(View.GONE);
            }else if (data.getType()==102){
                reduceMoney.setText("-"+data.getMoney());
                reduceMoney.setVisibility(View.VISIBLE);
                plusMoney.setVisibility(View.GONE);
            }

        }

    }
}

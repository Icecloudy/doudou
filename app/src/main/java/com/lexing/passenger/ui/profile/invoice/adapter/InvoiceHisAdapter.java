package com.lexing.passenger.ui.profile.invoice.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lexing.passenger.R;
import com.lexing.passenger.ui.profile.invoice.data.InvoiceHistoryBean;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017/4/13.
 */

public class InvoiceHisAdapter extends RecyclerView.Adapter<InvoiceHisAdapter.BillViewHolder> {



    private List<InvoiceHistoryBean> mList;
    private Context context;

    public InvoiceHisAdapter(List<InvoiceHistoryBean> mList, Context context) {
        this.mList = mList;
        this.context = context;
    }

    @Override
    public BillViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_invoice_history, parent, false);
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
        @BindView(R.id.orderDate)
        TextView orderDate;
        @BindView(R.id.setOutLocation)
        TextView setOutLocation;
        @BindView(R.id.EsLocation)
        TextView EsLocation;
        @BindView(R.id.tvInvoiceMoney)
        TextView tvInvoiceMoney;
        @BindView(R.id.tvIsDone)
        TextView tvIsDone;


        public BillViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        private void setData(InvoiceHistoryBean data) {
            setOutLocation.setText(data.getDeparture());
            EsLocation.setText(data.getDestination());
            orderDate.setText(data.getCreatetime());
            tvInvoiceMoney.setText(String.valueOf(data.getMoney()));
            if (data.getStatus()==1){
                tvIsDone.setText("已开票");
            }else if (data.getStatus()==2){
                tvIsDone.setText("未支付");
            }
        }


    }
}

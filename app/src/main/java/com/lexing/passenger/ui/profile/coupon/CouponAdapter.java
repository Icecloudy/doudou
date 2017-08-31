package com.lexing.passenger.ui.profile.coupon;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lexing.passenger.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017/5/22.
 */

public class CouponAdapter extends RecyclerView.Adapter<CouponAdapter.CouponViewHolder> {

    private List<CouponBean> mList;


    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void clickListener(View view, int i);
    }

    public CouponAdapter(List<CouponBean> mList) {
        this.mList = mList;
    }

    @Override
    public CouponViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_coupon, parent, false);
        return new CouponViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CouponViewHolder holder, int position) {
        holder.setData(mList.get(position),position);
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    class CouponViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.couponName)
        TextView couponName;
        @BindView(R.id.couponMoney)
        TextView couponMoney;
        @BindView(R.id.couponDeadline)
        TextView couponDeadline;
        @BindView(R.id.moneyTag)
        TextView moneyTag;
        @BindView(R.id.layoutCoupon)
        RelativeLayout layoutCoupon;

        private int pos;

        public CouponViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        /**
         * * aid(int) 优惠券id,
         * money(double) 金额,
         * type(double) 类型[1代金券,2折扣券],
         * top(int) 折扣券最高抵消金额（0不封顶）,
         * discount(double) 折,
         * starttime(string) 生效日期,
         * endtime(string) 失效日期,
         * status(int) 状态[1可用,2已用,3失效]
         */
        private void setData(CouponBean data,int i) {
            this.pos = i;
            if (data.getType() == 1) {
                couponName.setText("代金券");
                moneyTag.setVisibility(View.VISIBLE);
                couponMoney.setText(String.valueOf(data.getMoney()));
            } else if (data.getType() == 2) {
                couponName.setText("折扣券");
                moneyTag.setVisibility(View.GONE);
                couponMoney.setText(String.valueOf(data.getDiscount())+"折");
            }
            couponDeadline.setText("有效期：" + data.getStarttime() + "至" + data.getEndtime());
            if (data.getStatus()==1){
                layoutCoupon.setBackgroundResource(R.drawable.shape_coupon_bg);
            }else if (data.getStatus()==2){
                layoutCoupon.setBackgroundResource(R.drawable.shape_coupon_gray_bg);
                couponName.setText(couponName.getText().toString()+"(已用)");
            }else if (data.getStatus()==3){
                couponName.setText(couponName.getText().toString()+"(失效 )");
                layoutCoupon.setBackgroundResource(R.drawable.shape_coupon_gray_bg);
            }
        }

        @Override
        public void onClick(View v) {
            if (onItemClickListener != null) {
                onItemClickListener.clickListener(v, pos);
            }
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}

package com.lexing.passenger.ui.main.car;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.lexing.passenger.R;
import com.lexing.passenger.data.models.CarDetailsBean;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2017/5/23.
 */

public class CarDetailsAdapter extends BaseAdapter {

    private List<CarDetailsBean> mList;
    private Context context;

    public CarDetailsAdapter(List<CarDetailsBean> mList, Context context) {
        this.mList = mList;
        this.context = context;
    }


    @Override
    public int getCount() {
        return mList == null ? 0 : mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        CarViewHolder holder;
        if (view == null) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card_details, null);
            holder = new CarViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (CarViewHolder) view.getTag();
        }
        holder.setData(mList.get(position),position);
        return view;
    }


     class CarViewHolder {
        @BindView(R.id.carModel)
        TextView carModel;
        @BindView(R.id.guidePrice)
        TextView guidePrice;
        @BindView(R.id.referencePrice)
        TextView referencePrice;
        @BindView(R.id.callBusiness)
        Button callBusiness;

        CarViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

        private void setData(CarDetailsBean data,int position) {
            carModel.setText(data.getName());
            guidePrice.setText(context.getString(R.string.car_guide_price,data.getRecomprice()));
            referencePrice.setText(context.getString(R.string.car_reference_price,data.getPrice()));
            callBusiness.setTag(position);
        }

        @OnClick(R.id.callBusiness)
        public void onClick(View v) {
            int position = (int) v.getTag();
            String phone = mList.get(position).getPhone();
            showMessageDialog(phone);
        }
         /**
          * Show message dialog.
          */
         public void showMessageDialog(final String phone) {
             AlertDialog.Builder builder = new AlertDialog.Builder(context);
             builder.setTitle(R.string.nav_service);
             builder.setMessage("确定拨打客服电话:"+phone);
             builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                 @Override
                 public void onClick(DialogInterface dialog, int which) {
                     Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phone));
                     if (ActivityCompat.checkSelfPermission(context,
                             Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                         return;
                     }
                     context.startActivity(intent);
                 }
             });
             builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                 @Override
                 public void onClick(DialogInterface dialog, int which) {
                     dialog.dismiss();
                 }
             });
             builder.show();
         }
    }


}

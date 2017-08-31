package com.doudou.passenger.ui.profile.message;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.doudou.passenger.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2017/4/13.
 */

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.BillViewHolder> {


    private List<NewsBean> mList;
    private Context context;

    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void clickListener(View view, int i);
    }

    public NewsAdapter(List<NewsBean> mList, Context context) {
        this.mList = mList;
        this.context = context;
    }

    @Override
    public BillViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_news, parent, false);
        return new BillViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    @Override
    public void onBindViewHolder(BillViewHolder holder, int position) {
        holder.setData(mList.get(position), position);
    }


    class BillViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.content)
        TextView content;
        @BindView(R.id.date)
        TextView date;
        @BindView(R.id.close)
        View close;


        public BillViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        private void setData(NewsBean data, int position) {
            date.setText(data.getPropellingtime());
            if (TextUtils.isEmpty(data.getTitle())) {
                title.setText("温馨提示");
            } else {
                title.setText(data.getTitle());
            }
            content.setText(data.getContent());
            close.setTag(position);
        }

        @OnClick(R.id.close)
        public void onViewClicked(View v) {
            int pos = (int) v.getTag();
//            removeData(i);
            if (onItemClickListener != null) {
                onItemClickListener.clickListener(v, pos);
            }
        }


    }

    public void removeData(int position) {
        mList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mList.size() - position);
    }

    public void removeAllData() {
        mList.clear();
        notifyDataSetChanged();
    }
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

}

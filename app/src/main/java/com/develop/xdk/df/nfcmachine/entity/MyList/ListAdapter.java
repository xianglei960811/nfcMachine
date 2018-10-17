package com.develop.xdk.df.nfcmachine.entity.MyList;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.develop.xdk.df.nfcmachine.R;
import com.develop.xdk.df.nfcmachine.entity.AccountUser;
import com.develop.xdk.df.nfcmachine.ui.AccontListActivity;
import com.develop.xdk.df.nfcmachine.utils.ToastUntil;
import com.google.gson.Gson;

import java.util.List;

public class ListAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private List<AccountUser> mdata;
    private AccontListActivity activity;
    public ListAdapter(Context context, List<AccountUser> mdata) {
        inflater = LayoutInflater.from(context);
        this.mdata = mdata;
        activity = (AccontListActivity) context;
        Log.e("adapter", "ListAdapter: " + new Gson().toJson(mdata) + "::::");
    }

    @Override
    public int getCount() {
        return mdata.size();
    }

    @Override
    public Object getItem(int position) {
        return mdata.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
//        if (convertView == null){
        convertView = inflater.inflate(R.layout.item_list_accont, null);
        holder = new ViewHolder();
        holder.itemNumber = (TextView) convertView.findViewById(R.id.item_list_numb);
        holder.itemName = (TextView) convertView.findViewById(R.id.item_list_nume);
        holder.itemConsumeMoney = (TextView) convertView.findViewById(R.id.item_list_consume_money);
        holder.itemDate = (TextView) convertView.findViewById(R.id.item_list_date);
        try {
            AccountUser user = mdata.get(position);
            holder.itemNumber.setText(Integer.toString(position + 1));
            holder.itemName.setText(user.getName());
            holder.itemConsumeMoney.setText(Double.toString(user.getCousumeMoney()));
            holder.itemDate.setText(user.getData());
        }catch (Exception e){
            Log.d("adapter", "getView: is erro ");
            return convertView;
        }
        return convertView;
    }

    /**
     * 更新数据
     */
    public void onDataChange(List<AccountUser> list) {
        this.mdata = list;
        this.notifyDataSetChanged();
    }

    private class ViewHolder {
        TextView itemName;
        TextView itemNumber;
        TextView itemConsumeMoney;
        TextView itemDate;
    }
}

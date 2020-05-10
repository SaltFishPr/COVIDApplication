package com.saltfishpr.covidapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final Context mContext;
    private JSONArray mData;

    public MyAdapter(Context context, JSONArray data) {
        mContext = context;
        mData = data;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.info_item, parent, false);
        return new MyItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        try {
            JSONObject data = mData.getJSONObject(position);
            ((MyItemViewHolder) holder).mTvDate.setText(data.getString("time"));
            ((MyItemViewHolder) holder).mTvGate.setText(data.getString("gate"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return mData.length();
    }

    /**
     * 更新数据
     *
     * @param data 字符串Json数组，包含Json字典
     */
    public void swapData(JSONArray data) {
        mData = null;
        if (data != null) {
            mData = data;
            this.notifyDataSetChanged();
        }
    }

    class MyItemViewHolder extends RecyclerView.ViewHolder {
        final TextView mTvDate;
        final TextView mTvGate;

        MyItemViewHolder(View view) {
            super(view);
            mTvDate = view.findViewById(R.id.tv_item_1);
            mTvGate = view.findViewById(R.id.tv_item_2);
        }
    }
}

package com.saltfishpr.covidapplication;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.saltfishpr.covidapplication.data.MyContract;

public class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final Context mContext;
    private final Cursor mCursor;

    public MyAdapter(Context context, Cursor cursor) {
        mContext = context;
        mCursor = cursor;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.info_item, parent, false);
        return new MyItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        mCursor.moveToPosition(position);

        ((MyItemViewHolder) holder).mTvDate.setText(mCursor.getString(mCursor.getColumnIndex(MyContract.PassEntry.COLUMN_DATE)));
        ((MyItemViewHolder) holder).mTvGate.setText(mCursor.getString(mCursor.getColumnIndex(MyContract.PassEntry.COLUMN_GATE)));
        ((MyItemViewHolder) holder).mTvDirection.setText(mCursor.getString(mCursor.getColumnIndex(MyContract.PassEntry.COLUMN_DIR)));
    }

    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }

    class MyItemViewHolder extends RecyclerView.ViewHolder {
        final TextView mTvDate;
        final TextView mTvGate;
        final TextView mTvDirection;

        MyItemViewHolder(View view) {
            super(view);
            mTvDate = view.findViewById(R.id.tv_item_1);
            mTvGate = view.findViewById(R.id.tv_item_2);
            mTvDirection = view.findViewById(R.id.tv_item_3);
        }
    }
}

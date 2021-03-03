package com.example.blebluetoothtest;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class BLEDeviceRecyclerAdapter extends RecyclerView.Adapter<BLEDeviceRecyclerAdapter.ViewHolder> {

    private List<BLEDevice> data;

    public BLEDeviceRecyclerAdapter(List<BLEDevice> data) {
        this.data = data;
    }

    @Override
    public BLEDeviceRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_bledevice_item_view, parent, false);
        return new ViewHolder(rowItem);
    }

    @Override
    public void onBindViewHolder(BLEDeviceRecyclerAdapter.ViewHolder holder, int position) {
        holder.textView.setText(this.data.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return this.data.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView textView;

        public ViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            this.textView = view.findViewById(R.id.textview);
        }

        @Override
        public void onClick(View view) {
            Toast.makeText(view.getContext(), "position : " + getLayoutPosition() + " text : " + this.textView.getText(), Toast.LENGTH_SHORT).show();
        }
    }


}
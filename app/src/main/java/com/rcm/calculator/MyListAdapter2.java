package com.rcm.calculator;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


public class MyListAdapter2 extends RecyclerView.Adapter<MyListAdapter2.ViewHolder>{
    private ArrayList<dataModel> listdata;

    public MyListAdapter2(ArrayList<dataModel> listdata) {
        this.listdata = listdata;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.recycler_item2, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int p) {
        int position = p;
        holder.legpercent.setText(String.valueOf(listdata.get(position).totalpercent) +"%");
        holder.legamount.setText(String.valueOf(listdata.get(position).Name));
        Double nn = Double.parseDouble(listdata.get(position).Name);

        Double totalpercent = listdata.get(position).totalpercent / 100;

        holder.pb.setText(String.valueOf(round((nn*totalpercent), 2)));
    }
    public double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    @Override
    public int getItemCount() {
        return listdata.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView legpercent, legamount, pb;
        public ViewHolder(View itemView) {
            super(itemView);
            this.legpercent = itemView.findViewById(R.id.legpercent);
            this.legamount = itemView.findViewById(R.id.legamount);
            this.pb = itemView.findViewById(R.id.pb);
        }
    }
}
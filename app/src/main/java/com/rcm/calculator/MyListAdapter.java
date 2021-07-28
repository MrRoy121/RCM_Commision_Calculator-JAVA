package com.rcm.calculator;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;



public class MyListAdapter extends RecyclerView.Adapter<MyListAdapter.ViewHolder>{


    public MyListAdapter() {

    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.recycler_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int p) {
        int position = p;
        holder.txt.setText(MainActivity.hints[position]);
        holder.edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                MainActivity.myListData.set(position, new dataModel(holder.edit.getText().toString()));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }


    @Override
    public int getItemCount() {
        return MainActivity.myListData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public EditText edit;
        public TextView txt;
        public ViewHolder(View itemView) {
            super(itemView);
            this.edit = itemView.findViewById(R.id.edittext);
            this.txt = itemView.findViewById(R.id.txt);
        }
    }
}
package com.example.life_remember;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class Rcv_Adapter extends RecyclerView.Adapter<Rcv_Adapter.MyViewHolder> {


    @NonNull
    @Override
    public Rcv_Adapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;

        // This is where you inflate the layout and inflate rows
    }

    @Override
    public void onBindViewHolder(@NonNull Rcv_Adapter.MyViewHolder holder, int position) {

        // Assign values to the views we created
        // with the format / el modelo que creamos para las filas y su XML
    }

    @Override
    public int getItemCount() {
        return 0;

        // Number of items displayed on the recycler view
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        // Grab all the views for the recycler view

        // TextView tvTarea;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
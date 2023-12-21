package com.example.life_remember;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class Rcv_Adapter extends RecyclerView.Adapter<Rcv_Adapter.MyViewHolder> implements View.OnLongClickListener {

    Context context;
    ArrayList<Tarea> arrayTareas;
    private View.OnLongClickListener listener;

    public Rcv_Adapter(Context context, ArrayList<Tarea> arrayTareas){
        this.context = context;
        this.arrayTareas = arrayTareas;
    }

    @NonNull
    @Override
    public Rcv_Adapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // This is where you inflate the layout and inflate rows
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recycler_view_layout, parent, false);

        view.setOnLongClickListener(this);

        return new Rcv_Adapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Rcv_Adapter.MyViewHolder holder, int position) {
        // Assign values to the views we created
        // with the format / el modelo que creamos para las filas y su XML
        holder.tvTitulo.setText(arrayTareas.get(position).getTitulo());
        holder.tvDescripcion.setText(arrayTareas.get(position).getDescipcion());
        holder.tvRecordatorio.setText(arrayTareas.get(position).getTiempo_recuerdo());
    }

    @Override
    public int getItemCount() {
        return arrayTareas.size();
        // Number of items displayed on the recycler view
    }

    public Tarea getItemDesdeRecyclerView(int position){
        return arrayTareas.get(position);
    }

    public void addItem(Tarea tarea){
        arrayTareas.add(tarea);
        notifyItemInserted(arrayTareas.size() -1);
    }

    public void removeItem(int position){
        arrayTareas.remove(position);
        notifyItemRemoved(position);
    }

    public void remoteAllItems(){
        arrayTareas.clear();
        notifyDataSetChanged();
    }

    @Override
    public boolean onLongClick(View v) {
        if(listener != null){
            listener.onLongClick(v); // Check if listener is not null
            return true;
        } else{
            return false;
        }
    }

    public void setOnLongClickListener(View.OnLongClickListener listener){
        this.listener = listener;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        // Grab all the views / elements for the recycler view
        TextView tvTitulo, tvDescripcion, tvRecordatorio;

        public MyViewHolder(@NonNull View itemView) {

            super(itemView);

            tvTitulo = itemView.findViewById(R.id.tvTitulo);
            tvDescripcion = itemView.findViewById(R.id.tvTexto);
            tvRecordatorio = itemView.findViewById(R.id.tvRecordatorio);
        }
    }
}
package com.example.life_remember;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ArrayList<Tarea> arrayTareas = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rellenarRecyclerView(); // Creamos tareas por defecto
        // Si el usuario quiere añadir una tarea, estas se borran

        // Ahora tenemos que pasarle los datos al recycler view
        RecyclerView recyclerView = findViewById(R.id.vwRecyclerView);
        Rcv_Adapter adaptadorTareas = new Rcv_Adapter(this, arrayTareas);
        recyclerView.setAdapter(adaptadorTareas);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }


    // Cuando presiones que salga la opción de modificar ? o eliminar
    private void rellenarRecyclerView(){

        String[] arrayTitulos = {"Prueba1","Prueba2","Prueba3"};
        String[] arrayDescripciones = {"Desc1", "Desc2", "Desc3"};
        String[] arrayRecordatorios = {"Rec1", "Rec2", "Rec3"};

        for(int i=0; i<arrayTitulos.length; i++){
            arrayTareas.add(new Tarea(
                    arrayTitulos[i],
                    arrayDescripciones[i],
                    arrayRecordatorios[i]));
        } // Añadimos en el array de tareas las 3 tareas por defecto
    }
}
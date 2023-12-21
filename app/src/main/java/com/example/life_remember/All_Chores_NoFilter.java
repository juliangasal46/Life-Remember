package com.example.life_remember;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class All_Chores_NoFilter extends AppCompatActivity {


    private ArrayList<Tarea> arrayListTareas = new ArrayList<>();

    private ImageView btnGoBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_chores_no_filter);

        leerFicheroBBDD();
        generarRecyclerView(arrayListTareas);

        btnGoBack = findViewById(R.id.btnAllChores);

        btnGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    finish();
                } catch (Throwable e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }


    private boolean chequearExistenciaFicheroBBDD(String[] archivos, String buscarArchivo) {
        for (int i = 0; i < archivos.length; i++) {
            if (buscarArchivo.equals(archivos[i])) {
                return true;
            }
        }
        return false;
    }

        private void leerFicheroBBDD(){
            String[] archivos = fileList(); // Lista con todos los fichers

            if (chequearExistenciaFicheroBBDD(archivos, "bbdd_almacenar_tareas.txt")) {

                try {

                    // Si existe leemos el documento
                    InputStreamReader isr = new InputStreamReader(openFileInput("bbdd_almacenar_tareas.txt"));
                    BufferedReader br = new BufferedReader(isr);
                    String linea = br.readLine();

                    String guardarLineas = "";
                    String []tareaSpliteada;

                    // Mientras que haya algo que leer en la línea -> Sigue
                    while (linea != null) {

                        guardarLineas = linea;
                        tareaSpliteada = guardarLineas.split("_");

                        arrayListTareas.add(new Tarea(tareaSpliteada[0], tareaSpliteada[1], tareaSpliteada[2]));
                        linea = br.readLine();

                        Toast.makeText(this, tareaSpliteada[0], Toast.LENGTH_SHORT).show();
                    }

                    br.close();
                    isr.close();

                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
    }


    private void generarRecyclerView(ArrayList<Tarea> arrayListTareas){

        // Ahora tenemos que pasarle los datos al recycler view
        RecyclerView recyclerView = findViewById(R.id.vwRecyclerView_AllChores);
        Rcv_Adapter adaptadorTareas = new Rcv_Adapter(this, arrayListTareas);
        recyclerView.setAdapter(adaptadorTareas);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}
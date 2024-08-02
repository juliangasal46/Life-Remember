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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class All_Chores_NoFilter extends AppCompatActivity {


    private ArrayList<Tarea> arrayListTareas = new ArrayList<>();
    ArrayList<Tarea> filteredTareas = new ArrayList<>();
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

        ordenarTareas();
        // Ahora tenemos que pasarle los datos al recycler view
        RecyclerView recyclerView = findViewById(R.id.vwRecyclerView_AllChores);
        Rcv_Adapter adaptadorTareas = new Rcv_Adapter(this, filteredTareas);
        recyclerView.setAdapter(adaptadorTareas);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void ordenarTareas() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy-HH:mm");
        int n = arrayListTareas.size();

        // Método de Burbuja para ordenar tareas por fecha y hora
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                try {
                    // Obtener las fechas y horas de las tareas
                    Date fechaHoraActual = dateFormat.parse(arrayListTareas.get(j).getTiempo_recuerdo());
                    Date fechaHoraSiguiente = dateFormat.parse(arrayListTareas.get(j + 1).getTiempo_recuerdo());

                    // Comparar las fechas y horas
                    if (fechaHoraActual.after(fechaHoraSiguiente)) {
                        // Intercambiar las tareas si están en el orden incorrecto
                        Tarea temp = arrayListTareas.get(j);
                        arrayListTareas.set(j, arrayListTareas.get(j + 1));
                        arrayListTareas.set(j + 1, temp);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                    // Manejar errores de parsing
                }
            }
        }

        // Puedes ahora agregar las tareas ordenadas a filteredTareas si es necesario
        filteredTareas.addAll(arrayListTareas);
    }
}
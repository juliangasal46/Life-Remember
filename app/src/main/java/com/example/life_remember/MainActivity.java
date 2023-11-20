package com.example.life_remember;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.*;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ImageView btnAddRecordatorio;
    ArrayList<Tarea> arrayTareas = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Si el usuario quiere añadir una tarea, estas se borran

        leerFicheros();

        // Ahora tenemos que pasarle los datos al recycler view
        RecyclerView recyclerView = findViewById(R.id.vwRecyclerView);
        Rcv_Adapter adaptadorTareas = new Rcv_Adapter(this, arrayTareas);
        recyclerView.setAdapter(adaptadorTareas);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));



        btnAddRecordatorio = findViewById(R.id.imgSettings); // Pulsable

        // OPTIMIZAR MÁS ADELANTE, SE PUEDE HACER DE OTRA FORMAS MEJOR

        btnAddRecordatorio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                LayoutInflater inflater = getLayoutInflater();
                View dialogLayout = inflater.inflate(R.layout.alert_newchore_nombre, null);

                final EditText etTitulo = dialogLayout.findViewById(R.id.etTitulo);
                /* final EditText etDescripcion;
                final EditText etRecordatorio;*/

                // Ahora recibimos los datos introducidos

                alertDialogBuilder.setPositiveButton("Siguiente", (dialog, which) -> {

                    // guardar datos que lleguen en shared preffereneces
                    // lanzar siguiente alert dialog
                    if (etTitulo.getText().length() > 0) {

                        escribirRecordatorios(etTitulo.getText().toString(), "pito", "pito");

                    } else {
                        Toast.makeText(MainActivity.this, "No puedes relenar una tarea con el título vacío", Toast.LENGTH_SHORT).show();
                    }
                });

                alertDialogBuilder.setNegativeButton("Cancelar", (dialog, which) -> {
                });
                alertDialogBuilder.setView(dialogLayout);
                alertDialogBuilder.show();
            } // onClick
        });
    }

    // Cuando presiones que salga la opción de modificar ? o eliminar
    /* private void rellenarRecyclerView() {

        String[] arrayTitulos = {"Prueba1", "Prueba2", "Prueba3"};
        String[] arrayDescripciones = {"Desc1", "Desc2", "Desc3"};
        String[] arrayRecordatorios = {"Rec1", "Rec2", "Rec3"};

        for (int i = 0; i < arrayTitulos.length; i++) {
            arrayTareas.add(new Tarea(
                    arrayTitulos[i],
                    arrayDescripciones[i],
                    arrayRecordatorios[i]));
        } // Añadimos en el array de tareas las 3 tareas por defecto
    }*/


    private boolean cheaquearExistenciaFichero(String[] archivos, String buscarArchivo) {
        for (int i = 0; i < archivos.length; i++) {
            if (buscarArchivo.equals(archivos[i])) {
                return true;
            }
        }
        return false;
    }


    private void leerFicheros() {

        String[] archivos = fileList(); // Lista con todos los fichers

        if (cheaquearExistenciaFichero(archivos, "bbdd_almacenar_tareas.txt")) {

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

                    arrayTareas.add(new Tarea(tareaSpliteada[0], tareaSpliteada[1], tareaSpliteada[2]));
                    // Hacer split por seccion

                    // guardarLineas = guardarLineas + linea + "\n";
                    Toast.makeText(getApplicationContext(), "Nardo -> " + guardarLineas + "\n", Toast.LENGTH_SHORT).show();
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


    public void escribirRecordatorios(String tituloTarea, String descTarea, String fechaRecordar) {

        try {

            OutputStreamWriter osw = new OutputStreamWriter(openFileOutput("bbdd_almacenar_tareas.txt", Activity.MODE_PRIVATE));
            // Mode private es para que solo esta función pueda acceder a ese fichero de forma escritura

            // EL FORMATO SERÁ -> TITULO_DESC_FECHARECORDAR <- Lo guarda por lineas entonces no hace falta simbolo al final
            osw.write(tituloTarea + "_" + descTarea + "_" + fechaRecordar);
            osw.flush();
            osw.close();

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
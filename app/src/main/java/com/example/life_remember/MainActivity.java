package com.example.life_remember;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
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

        leerBBDD();

        rellenarRecyclerView(); // Creamos tareas por defecto
        // Si el usuario quiere añadir una tarea, estas se borran

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
                    if(etTitulo.getText().length() > 0){

                    } else {
                        Toast.makeText(MainActivity.this, "No puedes relenar una tarea con el título vacío", Toast.LENGTH_SHORT).show();
                    }
                });

                alertDialogBuilder.setNegativeButton("Cancelar", (dialog, which) -> {});
                alertDialogBuilder.setView(dialogLayout);
                alertDialogBuilder.show();
            } // onClick
        });
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


    private void leerBBDD(){

        try {
            BufferedReader fin = new BufferedReader(new InputStreamReader(openFileInput("bbdd_almacenar_tareas.txt")));
            String texto = fin.readLine();
            Toast.makeText(this, "TEXTO EN EL ARCHIVO: " + texto, Toast.LENGTH_LONG).show();
            fin.close();
        } catch(Exception ex) {
            // No existe el fichero
            Toast.makeText(getApplicationContext(), "Excepción fichero" , Toast.LENGTH_SHORT).show();
            ex.getMessage();
        }
    } // leerBBDD

    private void crearFicheroBBDD(){

        File file = new File("bbdd_almacenar_tareas.txt");

        try {
            FileOutputStream fos = openFileOutput("bbdd_almacenar_tareas.txt", Context.MODE_PRIVATE);
            // You can write initial content to the file if needed
            // fos.write("Initial content".getBytes());
            fos.close(); // Close the FileOutputStream when done
            Toast.makeText(getApplicationContext(), "Fichero creado correctamente", Toast.LENGTH_SHORT).show();
        } catch (Exception ex) {
            ex.printStackTrace();
            Toast.makeText(getApplicationContext(), "Error al crear el fichero", Toast.LENGTH_SHORT).show();
        }
    } // crearFicheroBBDD
}
package com.example.life_remember;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

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

                // Ahora recibimos los datos introducidos

                alertDialogBuilder.setPositiveButton("Siguiente", (dialog, which) -> {

                    // guardar datos que lleguen en shared preffereneces
                    // lanzar siguiente alert dialog
                    if (etTitulo.getText().length() > 0) {

                        String titulo = etTitulo.getText().toString();

                        AlertDialog.Builder alertDialogBuilder2 = new AlertDialog.Builder(MainActivity.this);
                        LayoutInflater inflater2 = getLayoutInflater();
                        View dialogLayout2 = inflater2.inflate(R.layout.alert_newchore_descripcion, null);

                        final EditText etDescripcion = dialogLayout2.findViewById(R.id.etDescripcion);

                        alertDialogBuilder2.setPositiveButton("Siguiente", (dialog2, which2) -> {

                            if (etDescripcion.getText().length() > 0) {

                                String descripcion = etDescripcion.getText().toString();

                                AlertDialog.Builder alertDialogBuilder3 = new AlertDialog.Builder(MainActivity.this);
                                LayoutInflater inflater3 = getLayoutInflater();
                                View dialogLayout3 = inflater3.inflate(R.layout.alert_newchore_fecha, null);

                                TextView etFechaActualSistema = dialogLayout3.findViewById(R.id.etFechaPorDefecto);
                                etFechaActualSistema.setText(cogerFechaActual());

                                alertDialogBuilder3.setPositiveButton("Siguiente", (dialog3, which3) -> {

                                    // Quiere decir que no ha cambiado la fecha, es la del día actual

                                    String fechaActual = etFechaActualSistema.getText().toString();

                                    AlertDialog.Builder alertDialogBuilder4 = new AlertDialog.Builder(MainActivity.this);
                                    LayoutInflater inflater4 = getLayoutInflater();
                                    View dialogLayout4 = inflater4.inflate(R.layout.alert_newchore_hora, null);

                                    alertDialogBuilder4.setPositiveButton("Ir a confirmación", (dialog4, which4) -> {

                                        TimePicker tpTimePicker = dialogLayout4.findViewById(R.id.tpTimePicker);
                                        int hora = tpTimePicker.getHour();
                                        int minutos = tpTimePicker.getMinute();
                                        String formatoHora = hora + ":" + minutos;

                                        Toast.makeText(MainActivity.this, formatoHora, Toast.LENGTH_SHORT).show();

                                        // Por último, antes de guardar los datos, lanzaremos un diálogo de confirmación para
                                        // el usuario , diga si quiere guardar o modificarlo de nuevo

                                        AlertDialog.Builder alertDialogBuilder5 = new AlertDialog.Builder(MainActivity.this);
                                        LayoutInflater inflater5 = getLayoutInflater();
                                        View dialogLayout5 = inflater5.inflate(R.layout.alert_confirm_new_chore_dialog, null);

                                        ArrayList<TextView> arrayTextViews = new ArrayList<>();
                                        arrayTextViews.add(dialogLayout5.findViewById(R.id.tvSubrayar1));
                                        arrayTextViews.add(dialogLayout5.findViewById(R.id.tvSubrayar2));
                                        arrayTextViews.add(dialogLayout5.findViewById(R.id.tvSubrayar3));
                                        arrayTextViews.add(dialogLayout5.findViewById(R.id.tvSubrayar4));

                                        for(TextView tv : arrayTextViews){
                                            tv.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
                                        }

                                        // Enseñamos los datos que hemos ido guardando
                                        TextView confirmTitulo = dialogLayout5.findViewById(R.id.etConfirmTitulo);
                                        TextView confirmDesc = dialogLayout5.findViewById(R.id.etConfirmDescripcion);
                                        TextView confirmFecha = dialogLayout5.findViewById(R.id.etConfirmFecha);
                                        TextView confirmHora = dialogLayout5.findViewById(R.id.etConfirmHora);

                                        Tarea tareaNueva = new Tarea(titulo, descripcion, fechaActual + " - "+ formatoHora);
                                        confirmTitulo.setText(titulo);
                                        confirmDesc.setText(descripcion);
                                        confirmFecha.setText(fechaActual);
                                        confirmHora.setText(formatoHora);


                                        alertDialogBuilder5.setPositiveButton("Finalizar", (dialog5, which5) -> {
                                            // Guardar datos
                                            adaptadorTareas.addItem(tareaNueva);
                                            escribirRecordatorios(tareaNueva);
                                        });

                                        alertDialogBuilder5.setNegativeButton("Cancelar", (dialog5, which5) -> {
                                        });
                                        alertDialogBuilder5.setView(dialogLayout5);
                                        alertDialogBuilder5.show();
                                    });


                                    alertDialogBuilder4.setNegativeButton("Cancelar", (dialog4, which4) -> {
                                    });
                                    alertDialogBuilder4.setView(dialogLayout4);
                                    alertDialogBuilder4.show();
                                });


                                alertDialogBuilder3.setNegativeButton("Cancelar", (dialog3, which3) -> {
                                });
                                alertDialogBuilder3.setView(dialogLayout3);
                                alertDialogBuilder3.show();

                            } else {
                                Toast.makeText(MainActivity.this, "No puedes relenar una tarea con el campo vacío", Toast.LENGTH_SHORT).show();
                            }
                        });

                        alertDialogBuilder2.setNegativeButton("Cancelar", (dialog2, which2) -> {
                        });
                        alertDialogBuilder2.setView(dialogLayout2);
                        alertDialogBuilder2.show();


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



        ItemTouchHelper.SimpleCallback itemTouchCallback = new ItemTouchHelper.SimpleCallback(
                0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();

                // Si está en la última posición no lo borra del array porque da un index out of bounds
                // hay que quitarlo del array creando uno
                if (position != RecyclerView.NO_POSITION) {
                    Tarea tareaEliminada = arrayTareas.get(position);

                    // Elimina el elemento del adaptador y actualiza la vista
                    adaptadorTareas.removeItem(position);
                }
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }


    private String cogerFechaActual(){

        Calendar calendar = Calendar.getInstance();
        Date fechaActual = calendar.getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String fechaFormateada = dateFormat.format(fechaActual);
        Toast.makeText(this, "Fecha actual del sistema: " + fechaFormateada, Toast.LENGTH_SHORT).show();

        return fechaFormateada;
    }


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
                    Toast.makeText(getApplicationContext(), guardarLineas + "\n", Toast.LENGTH_SHORT).show();
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


    public void escribirRecordatorios(Tarea nuevaTarea) {

        try {
            String[] archivos = fileList();

            // if existe -> no lo crees escribe en la siguiente línea, else crealo
            // EL FORMATO SERÁ -> TITULO_DESC_FECHARECORDAR <- Lo guarda por lineas entonces no hace falta simbolo al final
            if (!(cheaquearExistenciaFichero(archivos, "bbdd_almacenar_tareas.txt"))) {
                // Mode private es para que solo esta función pueda acceder a ese fichero de forma escritura
                OutputStreamWriter osw = new OutputStreamWriter(openFileOutput("bbdd_almacenar_tareas.txt", Activity.MODE_PRIVATE));
                osw.write(nuevaTarea.getTitulo() + "_" + nuevaTarea.getDescipcion() + "_" + nuevaTarea.getTiempo_recuerdo());
                Toast.makeText(this, nuevaTarea.getTitulo() + "_" + nuevaTarea.getDescipcion() + "_" + nuevaTarea.getTiempo_recuerdo() , Toast.LENGTH_SHORT).show();
                osw.flush();
                osw.close();
            } else {
                FileOutputStream fos = openFileOutput("bbdd_almacenar_tareas.txt", Context.MODE_APPEND);
                OutputStreamWriter osw = new OutputStreamWriter(fos);
                osw.write("\n");
                osw.write(nuevaTarea.getTitulo() + "_" + nuevaTarea.getDescipcion() + "_" + nuevaTarea.getTiempo_recuerdo());
                Toast.makeText(this, nuevaTarea.getTitulo() + "_" + nuevaTarea.getDescipcion() + "_" + nuevaTarea.getTiempo_recuerdo() , Toast.LENGTH_SHORT).show();
                osw.flush();
                osw.close();
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public void eliminarRecordatorios() {
        
    }
}
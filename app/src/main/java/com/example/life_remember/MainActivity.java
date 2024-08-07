package com.example.life_remember;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.app.AlarmManager;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {

    private ImageView btnAddRecordatorio, btnAbrirTodasTareas;
    private DatePicker datePicker;
    ArrayList<Tarea> arrayTareas = new ArrayList<>();
    ArrayList<Tarea> filteredTareas = new ArrayList<>();
    private String fechaModificada = "";
    private String fechaEditada = "";

    public final static String CHANNEL_ID = "NOTIFICACION";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        datePicker = findViewById(R.id.vwDatePicker);

        leerFicheros();

        // Ahora tenemos que pasarle los datos al recycler view
        RecyclerView recyclerView = findViewById(R.id.vwRecyclerView);
        Rcv_Adapter adaptadorTareas = new Rcv_Adapter(this, arrayTareas);
        recyclerView.setAdapter(adaptadorTareas);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // TODO: Esta zona es la de editado de la tarea
        adaptadorTareas.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                int posSeleccionada = recyclerView.getChildAdapterPosition(v);

                AlertDialog.Builder alertDialogBuilder_edit_chore = new AlertDialog.Builder(MainActivity.this);
                LayoutInflater inflater_edit_clone = getLayoutInflater();
                View dialogLayout_edit_chore = inflater_edit_clone.inflate(R.layout.alert_edit_chore, null);

                EditText editTitulo = dialogLayout_edit_chore.findViewById(R.id.etEditTitulo);
                EditText editDescripcion = dialogLayout_edit_chore.findViewById(R.id.etEditDescripcion);
                TextView editFecha = dialogLayout_edit_chore.findViewById(R.id.editFecha);
                TextView editHora = dialogLayout_edit_chore.findViewById(R.id.editHora);

                String titulo_edit = adaptadorTareas.getItemDesdeRecyclerView(posSeleccionada).getTitulo();
                String descrip_edit = adaptadorTareas.getItemDesdeRecyclerView(posSeleccionada).getDescipcion();
                String fecha_edit = adaptadorTareas.getItemDesdeRecyclerView(posSeleccionada).getTiempo_recuerdo().split("-")[0];
                String hora_edit = adaptadorTareas.getItemDesdeRecyclerView(posSeleccionada).getTiempo_recuerdo().split("-")[1];

                // Esta luego se usará cuando vayamos a escribir los cambios en el fichero
                String tiempo_recuerdo = fecha_edit + "-" + hora_edit;
                Tarea tarea_a_editar = new Tarea(titulo_edit, descrip_edit, tiempo_recuerdo);

                editTitulo.setText(titulo_edit);
                editDescripcion.setText(descrip_edit);
                editFecha.setText(fecha_edit);
                editHora.setText(hora_edit);

                editFecha.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder alertEditFecha = new AlertDialog.Builder(MainActivity.this);
                        LayoutInflater inflaterEditFecha = getLayoutInflater();
                        View dialogLayoutEditFecha = inflaterEditFecha.inflate(R.layout.alert_new_select_fecha, null);

                        DatePicker vwDatePicker_SelectNewFecha = dialogLayoutEditFecha.findViewById(R.id.vwDatePicker_SelectNewFecha);
                        TextView tvNewFecha = dialogLayoutEditFecha.findViewById(R.id.tvNewFecha);

                        int mes = vwDatePicker_SelectNewFecha.getMonth() + 1;

                        fechaEditada = vwDatePicker_SelectNewFecha.getDayOfMonth() + "/"
                                + mes + "/" + vwDatePicker_SelectNewFecha.getYear();

                        tvNewFecha.setText(fechaEditada);

                        // Cuando cambie el usuario la fecha, que se actualice automaticamente en el text view
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            vwDatePicker_SelectNewFecha.setOnDateChangedListener(new DatePicker.OnDateChangedListener() {
                                @Override
                                public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                                    monthOfYear = monthOfYear + 1;

                                    fechaEditada = dayOfMonth + "/" +
                                            monthOfYear + "/" +
                                            year;

                                    tvNewFecha.setText(fechaEditada);
                                }
                            });
                        } else {
                            Toast.makeText(MainActivity.this, "Tu android es muy antiguo, no se puede ejecutar el cambio de fecha", Toast.LENGTH_SHORT).show();
                        }

                        alertEditFecha.setPositiveButton("Siguiente", (dialogNewFecha, whichNewFecha) -> {
                            editFecha.setText(fechaEditada);
                        });

                        alertEditFecha.setNegativeButton("Cancelar", (dialog5, which5) -> {
                        });

                        alertEditFecha.setView(dialogLayoutEditFecha);
                        alertEditFecha.show();

                    }
                });

                editHora.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        AlertDialog.Builder alertEditHora = new AlertDialog.Builder(MainActivity.this);
                        LayoutInflater inflaterEditHora = getLayoutInflater();
                        View dialogLayoutEditHora = inflaterEditHora.inflate(R.layout.alert_newchore_hora, null);

                        TextView newEditHora = dialogLayout_edit_chore.findViewById(R.id.editHora);

                        alertEditHora.setPositiveButton("Siguiente", (dialogNewHora, whichNewHora) -> {
                            TimePicker tpTimePicker_edit = dialogLayoutEditHora.findViewById(R.id.tpTimePicker);
                            int hora_edit = tpTimePicker_edit.getHour();
                            int minutos_edit = tpTimePicker_edit.getMinute();
                            String formatoHora = hora_edit + ":" + minutos_edit;
                            String formatoHora_chequeado = ceroIzquierdaHoras(formatoHora);

                            // TODO: Vigilar
                            if (compararHoras(hora_edit, minutos_edit)) {
                                newEditHora.setText(formatoHora_chequeado);
                            }

                        });

                        alertEditHora.setNegativeButton("Cancelar", (dialog5, which5) -> {
                        });
                        alertEditHora.setView(dialogLayoutEditHora);
                        alertEditHora.show();
                    }
                });

                alertDialogBuilder_edit_chore.setPositiveButton("Guardar cambios", (dialogedit, whichedit) -> {

                    // Para editar lo que haremos es = Coger la tarea actual, eliminarla del fichero y escribirla nueva <= Re usarndo funciones
                    String formato_fecha_hora_editada = editFecha.getText().toString() + "-" + editHora.getText().toString();
                    Tarea tarea_nueva_editada = new Tarea(editTitulo.getText().toString(), editDescripcion.getText().toString(), formato_fecha_hora_editada);

                    editRecordatorios(tarea_a_editar, tarea_nueva_editada);
                    adaptadorTareas.remoteAllItems(); // Vaciamos array y por ende los recycler view elements
                    leerFicheros(); // Este ya tiene el filtrado por fecha
                });

                alertDialogBuilder_edit_chore.setNegativeButton("Cancelar", (dialogedit, whichedit) -> {
                });

                alertDialogBuilder_edit_chore.setView(dialogLayout_edit_chore);
                alertDialogBuilder_edit_chore.show();

                return true;
            }
        });

        btnAddRecordatorio = findViewById(R.id.imgSettings);
        btnAbrirTodasTareas = findViewById(R.id.imgAllChores);

        // TODO: Esta zona es la de creación de la tarea
        btnAddRecordatorio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                LayoutInflater inflater = getLayoutInflater();
                View dialogLayout = inflater.inflate(R.layout.alert_newchore_nombre, null);

                final EditText etTitulo = dialogLayout.findViewById(R.id.etTitulo);

                // Ahora recibimos los datos introducidos

                alertDialogBuilder.setPositiveButton("Siguiente", (dialog, which) -> {

                    // guardar datos que lleguen en shared preferences
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
                                etFechaActualSistema.setText(cogerFechaActual(datePicker));

                                Button btnNewFecha = dialogLayout3.findViewById(R.id.btnCambiar);

                                // Si pulsa el botón para cambiar la fecha porque la quiere en otro día
                                btnNewFecha.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        AlertDialog.Builder alertDialogSelectFecha = new AlertDialog.Builder(MainActivity.this);
                                        LayoutInflater inflaterSelectFecha = getLayoutInflater();
                                        View dialogLayoutSelectFecha = inflaterSelectFecha.inflate(R.layout.alert_new_select_fecha, null);

                                        DatePicker vwDatePicker_SelectNewFecha = dialogLayoutSelectFecha.findViewById(R.id.vwDatePicker_SelectNewFecha);

                                        // Cuando cambie el usuario la fecha, que se actualice automaticamente en el text view
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                            vwDatePicker_SelectNewFecha.setOnDateChangedListener(new DatePicker.OnDateChangedListener() {
                                                @Override
                                                public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                                                    monthOfYear = monthOfYear + 1;

                                                    fechaModificada = dayOfMonth + "/" +
                                                            monthOfYear + "/" +
                                                            year;



                                                    TextView tvNewFecha = dialogLayoutSelectFecha.findViewById(R.id.tvNewFecha);

                                                    tvNewFecha.setText(fechaModificada);
                                                }
                                            });
                                        } else {
                                            Toast.makeText(MainActivity.this, "Tu android es muy antiguo, no se puede ejecutar el cambio de fecha", Toast.LENGTH_SHORT).show();
                                        }

                                        alertDialogSelectFecha.setPositiveButton("Siguiente", (dialogNewFecha, whichNewFecha) -> {
                                            etFechaActualSistema.setText(fechaModificada); // Si termina bien que actualice la fecha que se va a meter de forma final
                                        });

                                        alertDialogSelectFecha.setNegativeButton("Cancelar", (dialog5, which5) -> {
                                        });

                                        alertDialogSelectFecha.setView(dialogLayoutSelectFecha);
                                        alertDialogSelectFecha.show();
                                    }
                                });


                                alertDialogBuilder3.setPositiveButton("Siguiente", (dialog3, which3) -> {

                                    // Quiere decir que no ha cambiado la fecha, es la del día actual
                                    String fechaActual = etFechaActualSistema.getText().toString();



                                    AlertDialog.Builder alertDialogBuilder4 = new AlertDialog.Builder(MainActivity.this);
                                    LayoutInflater inflater4 = getLayoutInflater();
                                    View dialogLayout4 = inflater4.inflate(R.layout.alert_newchore_hora, null);
                                    TimePicker tpTimePicker = dialogLayout4.findViewById(R.id.tpTimePicker);

                                    alertDialogBuilder4.setPositiveButton("Ir a confirmación", (dialog4, which4) -> {
                                        int hora = tpTimePicker.getHour();
                                        int minutos = tpTimePicker.getMinute();
                                        String formatoHora = "";
                                        String hora_chequeada_ceros = "";

                                        if (!compararHoras(hora, minutos)) {
                                            Toast.makeText(MainActivity.this, "La hora puesta es más pequeña que la hora actual, cambiala a la que quieras :D", Toast.LENGTH_LONG).show();
                                            formatoHora = "23:59";
                                            hora_chequeada_ceros = ceroIzquierdaHoras(formatoHora);
                                        } else {
                                            formatoHora = hora + ":" + minutos;
                                            hora_chequeada_ceros = ceroIzquierdaHoras(formatoHora);
                                        }

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

                                        for (TextView tv : arrayTextViews) {
                                            tv.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
                                        }

                                        // Enseñamos los datos que hemos ido guardando
                                        TextView confirmTitulo = dialogLayout5.findViewById(R.id.etConfirmTitulo);
                                        TextView confirmDesc = dialogLayout5.findViewById(R.id.etConfirmDescripcion);
                                        TextView confirmFecha = dialogLayout5.findViewById(R.id.etConfirmFecha);
                                        TextView confirmHora = dialogLayout5.findViewById(R.id.etConfirmHora);

                                        Tarea tareaNueva = new Tarea(titulo, descripcion, fechaActual + "-" + hora_chequeada_ceros);
                                        confirmTitulo.setText(titulo);
                                        confirmDesc.setText(descripcion);
                                        confirmFecha.setText(fechaActual);
                                        confirmHora.setText(hora_chequeada_ceros);

                                        alertDialogBuilder5.setPositiveButton("Finalizar", (dialog5, which5) -> {
                                            // Guardar datos
                                            adaptadorTareas.addItem(tareaNueva);
                                            escribirRecordatorios(tareaNueva);
                                            adaptadorTareas.remoteAllItems(); // Vaciamos array y por ende los recycler view elements
                                            leerFicheros(); // Este ya tiene el filtrado por fecha

                                            // Creamos las notificaciones
                                            // TODO: Crear notificaciones
                                            lanzarNotificacion();
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
                        Toast.makeText(MainActivity.this, "No puedes rellenar una tarea con el título vacío", Toast.LENGTH_SHORT).show();
                    }
                });

                alertDialogBuilder.setNegativeButton("Cancelar", (dialog, which) -> {
                });
                alertDialogBuilder.setView(dialogLayout);
                alertDialogBuilder.show();
            } // onClick
        });


        btnAbrirTodasTareas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent_TodasTareas = new Intent(MainActivity.this, All_Chores_NoFilter.class);
                // No le pasamos el array, tiene que leer el fichero de texto
                startActivity(intent_TodasTareas);
            }
        });


        // Cuando cambie la fecha el usuario que hacemos
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            datePicker.setOnDateChangedListener(new DatePicker.OnDateChangedListener() {
                @Override
                public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    adaptadorTareas.remoteAllItems(); // Vaciamos array y por ende los recycler view elements
                    leerFicheros(); // Este ya tiene el filtrado por fecha
                }
            });
        } else {
            Toast.makeText(this, "Tu android es muy antiguo, no se puede ejecutar el cambio de fecha", Toast.LENGTH_SHORT).show();
        }


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
                    adaptadorTareas.removeItem(position);
                    eliminarRecordatorios(tareaEliminada);
                    // Elimina el elemento del adaptador y actualiza la vista
                }
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

    }


    private String cogerFechaActual(DatePicker datepicker) {

        // Coger fecha del picker
        int day = datePicker.getDayOfMonth();
        int month = datePicker.getMonth();
        int year = datePicker.getYear();

        String fechaFormateada = day + "/" + (month + 1) + "/" + year;

        return fechaFormateada;
    }

    private boolean chequearExistenciaFichero(String[] archivos, String buscarArchivo) {
        for (int i = 0; i < archivos.length; i++) {
            if (buscarArchivo.equals(archivos[i])) {
                return true;
            }
        }
        return false;
    }

    private void leerFicheros() {
        String[] archivos = fileList(); // Lista con todos los fichers

        if (chequearExistenciaFichero(archivos, "bbdd_almacenar_tareas.txt")) {

            try {

                // Si existe leemos el documento
                InputStreamReader isr = new InputStreamReader(openFileInput("bbdd_almacenar_tareas.txt"));
                BufferedReader br = new BufferedReader(isr);
                String linea = br.readLine();

                String guardarLineas = "";
                String[] tareaSpliteada;
                String[] fechaSpliteada; // Porque ahora es -> fecha-hora

                // Mientras que haya algo que leer en la línea -> Sigue
                while (linea != null) {

                    guardarLineas = linea;
                    tareaSpliteada = guardarLineas.split("_");

                    // Hacer aquí el filtrado por fecha
                    fechaSpliteada = tareaSpliteada[2].split("-");

                    if (cogerFechaActual(datePicker).equals(fechaSpliteada[0])) {
                        arrayTareas.add(new Tarea(tareaSpliteada[0], tareaSpliteada[1], tareaSpliteada[2]));
                        // Hacer split por seccion
                    }

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
            if (!(chequearExistenciaFichero(archivos, "bbdd_almacenar_tareas.txt"))) {
                // TODO: Comprobar existencia de channel
                createNotificationChannel(); // Si no existe fichero == Primera apertura del programa
                // Mode private es para que solo esta función pueda acceder a ese fichero de forma escritura
                OutputStreamWriter osw = new OutputStreamWriter(openFileOutput("bbdd_almacenar_tareas.txt", Activity.MODE_PRIVATE));
                osw.write(nuevaTarea.getTitulo() + "_" + nuevaTarea.getDescipcion() + "_" + nuevaTarea.getTiempo_recuerdo());
                osw.flush();
                osw.close();
            } else {
                FileOutputStream fos = openFileOutput("bbdd_almacenar_tareas.txt", Context.MODE_APPEND);
                OutputStreamWriter osw = new OutputStreamWriter(fos);

                // Verificar si el archivo está vacío antes de escribir el salto de línea
                // Porque al eliminar tarea y luego añadir una tarea, el fichero no se elimina como el caso de arriba es
                if (fos.getChannel().size() > 0) {
                    osw.write("\n");
                }

                osw.write(nuevaTarea.getTitulo() + "_" + nuevaTarea.getDescipcion() + "_" + nuevaTarea.getTiempo_recuerdo());
                osw.flush();
                osw.close();
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public void eliminarRecordatorios(Tarea tarea) {

        String cadenaBuscarEnFichero = tarea.getTitulo() + "_" +
                tarea.getDescipcion() + "_" +
                tarea.getTiempo_recuerdo();

        try (BufferedReader bufrd = new BufferedReader(new InputStreamReader(openFileInput("bbdd_almacenar_tareas.txt")));
             BufferedWriter bw = new BufferedWriter(new FileWriter(new File(getFilesDir(), "temp_bbdd_almacenar_tareas.txt")))) {
            String line;
            while ((line = bufrd.readLine()) != null) {
                if (!(line.equals(cadenaBuscarEnFichero))) {
                    if (bufrd.ready()) {
                        bw.write(line + "\n");
                    } else {
                        bw.write(line);
                    }
                }
            }

            // Eliminamos el archivo original
            File originalFile = getFileStreamPath("bbdd_almacenar_tareas.txt");
            originalFile.delete();

            // Renombramos el archivo temporal al nombre original
            File tempFile = new File(getFilesDir(), "temp_bbdd_almacenar_tareas.txt");
            tempFile.renameTo(originalFile);

        } catch (IOException e) {
            // Manejo adecuado de la excepción (puedes registrarla, mostrar un mensaje, etc.)
            e.printStackTrace();
            // Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }


    private void editRecordatorios(Tarea tareaAntigua, Tarea tareaEditada) {

        if ((tareaAntigua.getTitulo().equals(tareaEditada.getTitulo()))
                && (tareaAntigua.getDescipcion().equals(tareaEditada.getDescipcion()))
                && (tareaAntigua.getTiempo_recuerdo().equals(tareaEditada.getTiempo_recuerdo()))) {
            // Todas son iguales no guardes nada
            Toast.makeText(this, "No se han hecho cambios, tiene los mismos campos", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Cambios realizados con éxito", Toast.LENGTH_SHORT).show();
            escribirRecordatorios(tareaEditada);
            eliminarRecordatorios(tareaAntigua);
        }
    }

    public String ceroIzquierdaHoras(String horaCompleta) {

        // Como es una hora, hay que hacer split por los dos sitios
        String[] partesHora = horaCompleta.split(":");
        int horas = Integer.parseInt(partesHora[0]);
        int minutos = Integer.parseInt(partesHora[1]);

        if (horas >= 0 && horas <= 9) {

            if (minutos >= 0 && minutos <= 9) {
                return "0" + horas + ":0" + minutos;
            }

            return "0" + horas + ":" + minutos;
        }

        if (minutos >= 0 && minutos <= 9) {
            return horas + ":0" + minutos;
        }

        return horas + ":" + minutos;
    }

    //  Mejorar porque se buguea, parece que solamente hará falta cuando se compare 
    public String ceroIzquierdaFecha(String fechaCompleta){

        fechaCompleta = fechaCompleta.split("-")[0];

        String dia = fechaCompleta.split("/")[0];
        String mes = fechaCompleta.split("/")[1];
        String ano = fechaCompleta.split("/")[2];

        if(Integer.parseInt(dia) < 9 || Integer.parseInt(dia) == 0){
            dia = "0" + dia;
        }

        if(Integer.parseInt(mes) < 9 || Integer.parseInt(mes) == 0){
            mes = "0" + mes;
        }

        if(Integer.parseInt(ano) < 9 || Integer.parseInt(ano) == 0){
            ano = "0" + ano;
        }

        return dia + "/" + mes + "/" + ano;
    }

    public String cogerHoraSistema() {
        // Cuando se crea o edita una tarea, la hora tiene que ser mayor que la hora actual

        Calendar verHorasActuales = Calendar.getInstance();
        int horaActual = verHorasActuales.getTime().getHours();
        int minutoActual = verHorasActuales.getTime().getMinutes();
        String minutoFormateado = "";

        if (minutoActual < 10) {
            minutoFormateado = "0" + minutoActual;
        } else {
            minutoFormateado = Integer.toString(minutoActual);
        }

        return horaActual + ":" + minutoFormateado;
    }

    // Si falso = mismas horas
    public boolean compararHoras(int horaCogida, int minutoCogido) {

        String horaCompletaSistema = cogerHoraSistema();
        int horaSistema = Integer.parseInt(horaCompletaSistema.split(":")[0]);
        int minutoSistema = Integer.parseInt(horaCompletaSistema.split(":")[1]);

        // Obtener la fecha actual del sistema
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String fechaSistema = dateFormat.format(new Date());

        if(ceroIzquierdaFecha(cogerFechaActual(datePicker)).equals(fechaSistema)){

            Toast.makeText(this, "La fecha es del pasado, no podemos poner una notificación :c, te la ponemos a día de hoy :D", Toast.LENGTH_SHORT).show();

            if (horaCogida < horaSistema || minutoCogido < minutoSistema) {
                return false;
            } else {
                return true;
            }
        } else{
            return true;
        }
    }


    // Funciones de las notificaciones
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "CanalJulianNotis";
            String descripcion = "El canal de notis de Julian";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("notifyLemubit", name, importance);
            channel.setDescription(descripcion);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void scheduleNotification(int anoTraido, int mesTraido, int diaTraido,
                                      int horaTraida, int minutoTraido, int segundoTraido, int milisegundoTraido) {
        Calendar calendarCambiado = Calendar.getInstance();

        calendarCambiado.set(Calendar.YEAR, anoTraido);
        calendarCambiado.set(Calendar.MONTH, mesTraido - 1); // Los meses empiezan desde 0 -> Enero = 0 Febrero = 1
        calendarCambiado.set(Calendar.DAY_OF_MONTH, diaTraido);
        calendarCambiado.set(Calendar.HOUR_OF_DAY, horaTraida);
        calendarCambiado.set(Calendar.MINUTE, minutoTraido);
        calendarCambiado.set(Calendar.SECOND, segundoTraido);
        calendarCambiado.set(Calendar.MILLISECOND, milisegundoTraido);

        Intent intent = new Intent(MainActivity.this, AlarmNotification.class);

        // Donde pone 0 ponía => AlarmNotification.NOTIFICATION_ID
        PendingIntent pendingIntent_notis = PendingIntent.getBroadcast(
                getApplicationContext(),
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
        );

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        if (alarmManager != null) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP,
                    calendarCambiado.getTimeInMillis()
                    , pendingIntent_notis);

        }
    }

    private void lanzarNotificacion() {

        /* String dia = "";
        String mes = "";
        String ano = "";
        String hora_edit = "";
        String hora = "";
        String minutos = "";

        for(Tarea tarea : arrayTareas){
            String fecha_split = tarea.getTiempo_recuerdo().split("-")[0];

            dia = fecha_split.split("/")[0];
            mes = fecha_split.split("/")[1];
            ano = fecha_split.split("/")[2];

            hora_edit = tarea.getTiempo_recuerdo().split("-")[1];
            hora = hora_edit.split(":")[0];
            minutos = hora_edit.split(":")[1];
        }

        scheduleNotification(Integer.parseInt(ano), Integer.parseInt(mes), Integer.parseInt(dia),
                Integer.parseInt(hora), Integer.parseInt(minutos), 0 ,0 );*/

        // Enseña todas las tareas sin filtrado, para ir una por una programando la notificación
        // Han de ordenarse por fecha
        /* for(Tarea tarea : arrayTareasNoFiltrado){
            Toast.makeText(this, tarea.getTitulo(), Toast.LENGTH_SHORT).show();
        }*/
        // ordenadoFechas();
    }

    private void ordenadoFechas() {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            // Fecha de referencia para comparar (por ejemplo, comparar si es después de esta fecha)
            Date fechaReferencia = dateFormat.parse("01-01-2024");

            // Limpiar filteredTareas antes de llenarla
            filteredTareas.clear();

            // Iterar y comparar fechas
            for (Tarea tarea : arrayTareas) {

                String fecha_split = tarea.getTiempo_recuerdo().split("-")[0];

                String dia = fecha_split.split("/")[0];
                String mes = fecha_split.split("/")[1];
                String ano = fecha_split.split("/")[2];

                String hora_edit = tarea.getTiempo_recuerdo().split("-")[1];
                String hora = hora_edit.split(":")[0];
                String minutos = hora_edit.split(":")[1];

                Date fechaTarea = dateFormat.parse(dia + "-" + mes + "-" + ano);

                // Comparar la fecha de la tarea con la fecha de referencia
                if (fechaTarea.after(fechaReferencia)) {
                    filteredTareas.add(tarea);
                    Toast.makeText(this, "Se ha comparado bien", Toast.LENGTH_SHORT).show();
                    Toast.makeText(this, fechaTarea + " = " + "01-01-2024", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Algo ha salido mal", Toast.LENGTH_SHORT).show();
                    Toast.makeText(this, fechaTarea + " = " + "01-01-2024", Toast.LENGTH_SHORT).show();
                }
            }

            // Mostrar mensaje de confirmación
            Toast.makeText(this, "Ya se ha ordenado todo, enseñando", Toast.LENGTH_SHORT).show();

            // Mostrar las tareas filtradas
            for (Tarea tarea : filteredTareas) {
                Toast.makeText(this, tarea.getTitulo() + " - " + tarea.getTiempo_recuerdo(), Toast.LENGTH_SHORT).show();
            }

        } catch (ParseException e) {
            // Manejar errores de parsing
            e.printStackTrace();
            Toast.makeText(this, "Error al parsear la fecha", Toast.LENGTH_SHORT).show();
        }
    }
}

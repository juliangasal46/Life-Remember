package com.example.life_remember;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
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

import java.io.*;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    private ImageView btnAddRecordatorio, btnAbrirTodasTareas;
    private DatePicker datePicker;
    ArrayList<Tarea> arrayTareas = new ArrayList<>();
    private String fechaModificada = "";
    private String fechaEditada = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        datePicker = findViewById(R.id.vwDatePicker);

        createChannel();
        leerFicheros();

        // Ahora tenemos que pasarle los datos al recycler view
        RecyclerView recyclerView = findViewById(R.id.vwRecyclerView);
        Rcv_Adapter adaptadorTareas = new Rcv_Adapter(this, arrayTareas);
        recyclerView.setAdapter(adaptadorTareas);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

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

                            newEditHora.setText(formatoHora);
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

                                        for (TextView tv : arrayTextViews) {
                                            tv.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
                                        }

                                        // Enseñamos los datos que hemos ido guardando
                                        TextView confirmTitulo = dialogLayout5.findViewById(R.id.etConfirmTitulo);
                                        TextView confirmDesc = dialogLayout5.findViewById(R.id.etConfirmDescripcion);
                                        TextView confirmFecha = dialogLayout5.findViewById(R.id.etConfirmFecha);
                                        TextView confirmHora = dialogLayout5.findViewById(R.id.etConfirmHora);

                                        Tarea tareaNueva = new Tarea(titulo, descripcion, fechaActual + "-" + formatoHora);
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

                    eliminarRecordatorios(tareaEliminada);

                    // Elimina el elemento del adaptador y actualiza la vista
                    adaptadorTareas.removeItem(position);
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
                // Mode private es para que solo esta función pueda acceder a ese fichero de forma escritura
                OutputStreamWriter osw = new OutputStreamWriter(openFileOutput("bbdd_almacenar_tareas.txt", Activity.MODE_PRIVATE));
                osw.write(nuevaTarea.getTitulo() + "_" + nuevaTarea.getDescipcion() + "_" + nuevaTarea.getTiempo_recuerdo());
                osw.flush();
                osw.close();
            } else {
                FileOutputStream fos = openFileOutput("bbdd_almacenar_tareas.txt", Context.MODE_APPEND);
                OutputStreamWriter osw = new OutputStreamWriter(fos);
                osw.write("\n");
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

        try {
            FileInputStream fis = openFileInput("bbdd_almacenar_tareas.txt");
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader bufrd = new BufferedReader(isr);

            File tempFile = new File(getFilesDir(), "temp_bbdd_almacenar_tareas.txt");
            FileWriter fw = new FileWriter(tempFile);
            BufferedWriter bw = new BufferedWriter(fw);

            StringBuilder stblr = new StringBuilder();
            String line;

            while ((line = bufrd.readLine()) != null) {
                if (!line.equals(cadenaBuscarEnFichero)) {
                    stblr.append(line).append("\n"); // el segundo appen da guerra

                } else {
                    Toast.makeText(this, line + " = " + cadenaBuscarEnFichero, Toast.LENGTH_SHORT).show();
                }
            }

            stblr.setLength(stblr.length() - 1); // Le quitamos 1 que sería el salto de línea que mete

            bw.write(stblr.toString());

            bufrd.close();
            isr.close();
            fis.close();

            bw.flush();
            bw.close();
            fw.close();

            // Eliminamos el archivo original
            File originalFile = getFileStreamPath("bbdd_almacenar_tareas.txt");
            originalFile.delete();

            // Renombramos el archivo temporal al nombre original
            tempFile.renameTo(originalFile);

        } catch (IOException e) {
            Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
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

    private static final int NOTIFICATION_ID = 1;
    public static final String MY_CHANNEL_ID = "myChannel";

    private void createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    MY_CHANNEL_ID,
                    "MySuperChannel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription("SUSCRIBETE");

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.createNotificationChannel(channel);
        }
    }

    private void createSimpleNotification(String titulo, String descripcion) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        int flag = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) ?
                PendingIntent.FLAG_IMMUTABLE : 0;

        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, 0, intent, flag);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, MY_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_background) // Cambia a tu propio recurso de icono
                .setContentTitle("¡¡ Tienes una tarea que hacer hoy !!")
                .setContentText(titulo)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(descripcion))
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true); // Cierra la notificación al hacer clic en ella

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        // Verificar permisos en tiempo de ejecución
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) !=
                PackageManager.PERMISSION_GRANTED) {
            // Solicitar permisos en tiempo de ejecución si no están otorgados
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
        } else {
            // Mostrar la notificación
            notificationManager.notify(NOTIFICATION_ID, builder.build());
        }
    }
}
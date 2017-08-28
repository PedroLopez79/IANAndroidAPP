package com.example.kamran.login;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

public class BitacoraServicio extends AppCompatActivity implements DatePickerDialog.OnDateSetListener,
        TimePickerDialog.OnTimeSetListener{
    public static final int SIGNATURE_ACTIVITY = 1;
    FloatingActionButton fab;
    Button obtenfechahora, nuevo;
    TextView txtfechahora, folio, fecha, nomusuario,estacionnumero;
    String folioApp, fechaApp, nombreusuario, idusuario, numestacion;
    EditText actividad, frecuencia, observaciones;

    Bitmap bitmap;
    ImageView img_signature;

    int day,month,year,hour,minute,second;
    int dayFinal,monthFinal,yearFinal,hourFinal,minuteFinal,secondFinal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bitacora_servicio);

        Intent intent = getIntent();
        nombreusuario = intent.getStringExtra("nombreusuario");
        idusuario= intent.getStringExtra("usuarioid");

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        numestacion = prefs.getString("numeroestacion","2601");

        nuevo = (Button) findViewById(R.id.nuevo);
        obtenfechahora = (Button) findViewById(R.id.obtenfechahora);
        txtfechahora = (TextView) findViewById(R.id.txtfechahora);
        nomusuario = (TextView) findViewById(R.id.lblusuario);
        estacionnumero = (TextView) findViewById(R.id.lblEstacion);
        actividad = (EditText) findViewById(R.id.actividad);
        frecuencia = (EditText) findViewById(R.id.frecuencia);
        observaciones = (EditText) findViewById(R.id.observaciones);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        folio = (TextView) findViewById(R.id.folio);
        fecha = (TextView) findViewById(R.id.fecha);

        nuevo.setEnabled(true);
        obtenfechahora.setEnabled(false);
        actividad.setEnabled(false);
        frecuencia.setEnabled(false);
        observaciones.setEnabled(false);
        fab.setEnabled(false);

        nomusuario.setText("Usuario.- "+nombreusuario);
        estacionnumero.setText("Estacion #.- " + numestacion);


        //obtenfechahora
        //img_signature = (ImageView) findViewById(R.id.img_signature);

        nuevo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                folioApp = String.format("%02d",c.get(Calendar.DAY_OF_MONTH))+
                           String.format("%02d",c.get(Calendar.MONTH)+1)+
                           String.format("%04d",c.get(Calendar.YEAR));
                //SQL SERVER ESPAñOL//
                fechaApp = String.format("%02d",c.get(Calendar.DAY_OF_MONTH)) + "/" + String.format("%02d",c.get(Calendar.MONTH)+1) + "/" + String.format("%04d",c.get(Calendar.YEAR));
                //SQL SERVER INGLES//
                //fechaApp = String.format("%02d",c.get(Calendar.MONTH)+1) + "/" + String.format("%02d",c.get(Calendar.DAY_OF_MONTH)) + "/" + String.format("%04d",c.get(Calendar.YEAR));

                nuevo.setEnabled(false);
                obtenfechahora.setEnabled(true);
                actividad.setEnabled(true);
                frecuencia.setEnabled(true);
                observaciones.setEnabled(true);
                fab.setEnabled(true);

                folio.setText("Folio.- "+folioApp);
                fecha.setText("Fecha.- "+fechaApp);
            }
        });

        obtenfechahora.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                year = c.get(Calendar.YEAR);
                month= c.get(Calendar.MONTH);
                day  = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(BitacoraServicio.this, BitacoraServicio.this,
                        year, month, day);
                datePickerDialog.show();
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //img_signature.setImageResource(0);
                Intent intent = new Intent(getApplicationContext(), Signature.class);
                intent.putExtra("Actividad",actividad.getText().toString());
                intent.putExtra("Frecuencia",frecuencia.getText().toString());
                intent.putExtra("Observaciones",observaciones.getText().toString());
                intent.putExtra("FechaHora", txtfechahora.getText().toString());
                intent.putExtra("Folio", folioApp);
                intent.putExtra("Fecha", fechaApp);
                intent.putExtra("UsuarioID", idusuario);
                intent.putExtra("EstacionID", numestacion);

                startActivityForResult(intent, SIGNATURE_ACTIVITY);

                Calendar c = Calendar.getInstance();
                folioApp = String.format("%02d",c.get(Calendar.MONTH)+1)+
                        String.format("%02d",c.get(Calendar.DAY_OF_MONTH))+
                        String.format("%04d",c.get(Calendar.YEAR));
                //SQL SERVER ESPAñOL//
                fechaApp = String.format("%02d",c.get(Calendar.DAY_OF_MONTH)) + "/" + String.format("%02d",c.get(Calendar.MONTH)+1) + "/" + String.format("%04d",c.get(Calendar.YEAR));
                //SQL SERVER INGLES//
                //fechaApp = String.format("%02d",c.get(Calendar.MONTH)+1) + "/" + String.format("%02d",c.get(Calendar.DAY_OF_MONTH)) + "/" + String.format("%04d",c.get(Calendar.YEAR));

                nuevo.setEnabled(true);
                obtenfechahora.setEnabled(false);
                actividad.setEnabled(false);
                frecuencia.setEnabled(false);
                observaciones.setEnabled(false);
                fab.setEnabled(false);

                actividad.setText("");
                frecuencia.setText("");
                observaciones.setText("");

                folio.setText("Folio.- "+folioApp);
                fecha.setText("Fecha.- "+fechaApp);
            }
        });
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        yearFinal = year;
        monthFinal = month + 1;
        dayFinal = dayOfMonth;

        Calendar c = Calendar.getInstance();
        hour  = c.get(Calendar.HOUR);
        minute= c.get(Calendar.MINUTE);
        second= c.get(Calendar.SECOND);

        TimePickerDialog timePickerDialog = new TimePickerDialog(BitacoraServicio.this, BitacoraServicio.this,
                hour, minute, DateFormat.is24HourFormat(this));

        timePickerDialog.show();
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        hourFinal = hourOfDay;
        minuteFinal = minute;

        //SQL SERVER ESPAñOL//
        txtfechahora.setText(String.format("%02d",dayFinal) + "/" + String.format("%02d",monthFinal) + "/" + String.format("%04d",yearFinal) + " " + String.format("%02d",hourFinal) + ":" + String.format("%02d",minuteFinal) + ":" + "00");
        //SQL SERVER INGLES//
        //txtfechahora.setText(String.format("%02d",monthFinal) + "/" + String.format("%02d",dayFinal) + "/" + String.format("%04d",yearFinal) + " " + String.format("%02d",hourFinal) + ":" + String.format("%02d",minuteFinal) + ":" + "00");
    }
}

package com.example.kamran.login;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;
import java.util.Calendar;

public class compras extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, buscaproducto.ISelectedData {

    String TAG = "Response";

    String resultString, ip, numestacion, productos, rtnproductos, fol, Maestro, Detalle;

    ImageButton btnfecha, btnbuscarproducto, btnagregarproducto, btnnuevo, btnguardar;
    Spinner spTipoMovimiento, spAlmacen, spProveedor, spEstacionDestino, spAlmacenDestino, spCantidad;
    TextView nomusuario,estacionnumero, fecha, txtidproducto, txtdescproducto, folio;
    EditText edReferencia;

    ListView lvdetalleproductos;

    int mDay,mMonth,mYear;
    int day,month,year,hour,minute,second;
    int dayFinal,monthFinal,yearFinal;

    ArrayList<String> lsTipomovimiento = new ArrayList<>();
    ArrayList<String> lsAlmacen = new ArrayList<>();
    ArrayList<String> lsProveedor = new ArrayList<>();
    ArrayList<String> lsEstacionDestino = new ArrayList<>();
    ArrayList<String> lsAlmacenDestino = new ArrayList<>();
    ArrayList<String> lsCantidad = new ArrayList<>();
    ArrayList<String> lsdetalleproductos = new ArrayList<>();

    ArrayAdapter<String> adTipoMovimiento;
    ArrayAdapter<String> adAlmacen;
    ArrayAdapter<String> adProveedor;
    ArrayAdapter<String> adEstacionDestino;
    ArrayAdapter<String> adAlmacenDestino;
    ArrayAdapter<String> adCantidad;
    ArrayAdapter<String> addetalleprodcutos;

    String nombreusuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compras);

        TextView texto1 = (TextView) findViewById(R.id.textView1);
        TextView texto2 = (TextView) findViewById(R.id.textView2);

        Intent intent = getIntent();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        nombreusuario = intent.getStringExtra("nombreusuario");
        ip = prefs.getString("ipservidor", "192.168.0.6");
        numestacion = prefs.getString("numeroestacion","2601");
        texto1.setText("NOMBRE DE USUARIO: "+ nombreusuario);
        texto2.setText("ESTACION NUMERO: " + prefs.getString("numeroestacion","2601"));

        spTipoMovimiento = (Spinner) findViewById(R.id.sptipomovimiento);
        spAlmacen = (Spinner) findViewById(R.id.spalmacen);
        spProveedor = (Spinner) findViewById(R.id.spproveedor);
        spEstacionDestino = (Spinner) findViewById(R.id.spestaciond);
        spAlmacenDestino = (Spinner) findViewById(R.id.spalmacend);
        spCantidad = (Spinner) findViewById(R.id.spcantidad);
        edReferencia = (EditText) findViewById(R.id.edtReferencia);

        nomusuario = (TextView) findViewById(R.id.textView1);
        estacionnumero = (TextView) findViewById(R.id.textView2);
        txtidproducto = (TextView) findViewById(R.id.txtidproducto);
        txtdescproducto = (TextView) findViewById(R.id.txtdescproducto);
        fecha = (TextView) findViewById(R.id.txtfecha);
        folio = (TextView) findViewById(R.id.txtFolio);

        btnfecha = (ImageButton) findViewById(R.id.btnFecha);
        btnagregarproducto = (ImageButton) findViewById(R.id.btnagregarproducto);
        btnbuscarproducto = (ImageButton) findViewById(R.id.btnbuscarproducto);
        btnnuevo = (ImageButton) findViewById(R.id.btnnuevo);
        btnguardar = (ImageButton) findViewById(R.id.btnguardar);
        lvdetalleproductos = (ListView) findViewById(R.id.lvdetalleproductos);

        final android.app.FragmentManager fm = getFragmentManager();
        final buscaproducto p = new buscaproducto();
        addetalleprodcutos = new ArrayAdapter<String>(compras.this, android.R.layout.simple_spinner_item, lsdetalleproductos);

        /*desabilitar componentes hasta que se de nuevo-------------------------------------------*/
        spTipoMovimiento.setEnabled(false);
        spAlmacen.setEnabled(false);
        spProveedor.setEnabled(false);
        spEstacionDestino.setEnabled(false);
        spAlmacenDestino.setEnabled(false);
        spCantidad.setEnabled(false);
        edReferencia.setEnabled(false);
        fecha.setEnabled(false);
        btnfecha.setEnabled(false);
        btnagregarproducto.setEnabled(false);
        btnbuscarproducto.setEnabled(false);
        btnguardar.setEnabled(false);

        /*generar cadena maestra y detalle para enviar a guardar*/
        /*----------------------------------------------------------------------------------------*/

        Calendar calendario = Calendar.getInstance();
        year = calendario.get(Calendar.YEAR);
        month= calendario.get(Calendar.MONTH)+1;
        day  = calendario.get(Calendar.DAY_OF_MONTH);
        //fechaingles
        /*fecha.setText(
                new StringBuilder()
                        // Month is 0 based so add 1
                        .append(String.format("%02d",day)).append("-")
                        .append(String.format("%02d",month)).append("-")
                        .append(String.format("%04d",year)).append(""));*/
        //fechaespañol
        fecha.setText(
                new StringBuilder()
                        // Month is 0 based so add 1
                        .append(String.format("%02d",month)).append("-")
                        .append(String.format("%02d",day)).append("-")
                        .append(String.format("%04d",year)).append(""));

        compras.AsyncCallWS task = new compras.AsyncCallWS();
        task.execute();

        btnnuevo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*abilitar componentes hasta que se de nuevo--------------------------------------*/
                spTipoMovimiento.setEnabled(true);
                spAlmacen.setEnabled(true);
                spProveedor.setEnabled(true);
                spEstacionDestino.setEnabled(true);
                spAlmacenDestino.setEnabled(true);
                spCantidad.setEnabled(true);
                edReferencia.setEnabled(true);
                fecha.setEnabled(true);
                btnfecha.setEnabled(true);
                btnagregarproducto.setEnabled(true);
                btnbuscarproducto.setEnabled(true);
                btnguardar.setEnabled(true);
                btnnuevo.setEnabled(false);
                btnguardar.setImageResource(R.drawable.ic_menu_save_color1);
                btnnuevo.setImageResource(R.drawable.stat_notify_more_color2);

        /*----------------------------------------------------------------------------------------*/
            }
        });

        btnguardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*abilitar componentes hasta que se de nuevo--------------------------------------*/
                spTipoMovimiento.setEnabled(false);
                spAlmacen.setEnabled(false);
                spProveedor.setEnabled(false);
                spEstacionDestino.setEnabled(false);
                spAlmacenDestino.setEnabled(false);
                spCantidad.setEnabled(false);
                edReferencia.setEnabled(false);
                fecha.setEnabled(false);
                btnfecha.setEnabled(false);
                btnagregarproducto.setEnabled(false);
                btnbuscarproducto.setEnabled(false);
                btnnuevo.setEnabled(true);
                btnguardar.setEnabled(false);
                btnguardar.setImageResource(R.drawable.ic_menu_save_color2);
                btnnuevo.setImageResource(R.drawable.stat_notify_more_color1);

                Maestro = "";
                Detalle = "";
                Maestro = numestacion + "|" + fecha.getText().toString() + "|" + folio.getText().toString() + "|" + spTipoMovimiento.getSelectedItem().toString() + "|" + spAlmacen.getSelectedItem().toString()
                         +"|"+spProveedor.getSelectedItem().toString() + "|" + edReferencia.getText().toString() + "|" + spEstacionDestino.getSelectedItem().toString()+"|"
                         +spAlmacenDestino.getSelectedItem().toString() + "|";

                for (String object: lsdetalleproductos) {
                    String[] h =object.split("-");
                    Detalle = Detalle + h[0] + "|" + h[5].substring(h[5].indexOf(":")+1,h[5].length()-1) + "#";
                }

                /*ejecuta el guardado del inventario*/
                compras.AsyncCallWS2 task2 = new compras.AsyncCallWS2();
                task2.execute();
            }
        });

        btnfecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                year = c.get(Calendar.YEAR);
                month= c.get(Calendar.MONTH);
                day  = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(compras.this, compras.this,
                        year, month, day);
                datePickerDialog.show();
            }
        });

        btnbuscarproducto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                p.varproductos = productos;
                p.show(fm ,productos);
            }
        });

        btnagregarproducto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lsdetalleproductos.add(rtnproductos + "----[cantidad:"+spCantidad.getSelectedItem().toString()+"]");
                lvdetalleproductos.setAdapter(addetalleprodcutos);
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

        //fechaingles
        //fecha.setText(String.format("%02d",dayFinal) + "-" + String.format("%02d",monthFinal) + "-" + String.format("%04d",yearFinal));
        //fechaespañol
        fecha.setText(String.format("%02d",monthFinal) + "-" + String.format("%02d",dayFinal) + "-" + String.format("%04d",yearFinal));
    }

    public String obtencomboscompras(String EstacionID) {
        String SOAP_ACTION = "urn:androidserviceIntf-Iandroidservice#obtencomboscompras";
        String METHOD_NAME = "obtencomboscompras";
        String NAMESPACE = "urn:androidserviceIntf";
        String URL = "http://" + ip + ":8070/soap/Iandroidservice";

        try {
            SoapObject Request = new SoapObject(NAMESPACE, METHOD_NAME);
            Request.addProperty("estacionid", EstacionID);

            SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            soapEnvelope.dotNet = true;
            soapEnvelope.setOutputSoapObject(Request);

            HttpTransportSE transport = new HttpTransportSE(URL, 80000);

            transport.call(SOAP_ACTION, soapEnvelope);
            Object response = (Object) soapEnvelope.getResponse();
            resultString = response.toString();

            Log.i(TAG, "Result Celsius: " + resultString);
        } catch (Exception ex) {
            Log.e(TAG, "Error: " + ex.getMessage());
        }

        return resultString;
    }

    public String IANMovimientoAlmacenGuarda(String MovimientoAlmacenMaestro, String MovimientoAlmacenDetalle)
    {
        String res = "";

        String SOAP_ACTION = "urn:androidserviceIntf-Iandroidservice#IANmovimientoalmacenguarda";
        String METHOD_NAME = "IANmovimientoalmacenguarda";
        String NAMESPACE = "urn:androidserviceIntf";
        String URL = "http://" + ip + ":8070/soap/Iandroidservice";

        try {
            SoapObject Request = new SoapObject(NAMESPACE, METHOD_NAME);
            Request.addProperty("MovimientoAlmacenMaestro", MovimientoAlmacenMaestro);
            Request.addProperty("MovimientoAlmacenDetalle", MovimientoAlmacenDetalle);

            SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            soapEnvelope.dotNet = true;
            soapEnvelope.setOutputSoapObject(Request);

            HttpTransportSE transport = new HttpTransportSE(URL, 80000);

            transport.call(SOAP_ACTION, soapEnvelope);
            Object response = (Object) soapEnvelope.getResponse();
            res = response.toString();

            Log.i(TAG, "Result Celsius: " + res);
        } catch (Exception ex) {
            Log.e(TAG, "Error: " + ex.getMessage());
        }

        return res;
    }

    @Override
    public void onSelectedData(String string) {
        rtnproductos = string;

        String[] parts6 = rtnproductos.split("-");
        txtidproducto.setText(parts6[0]);
        txtdescproducto.setText(parts6[1]);
    }


    private class AsyncCallWS extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            Log.i(TAG, "onPreExecute");
        }

        @Override
        protected Void doInBackground(Void... params) {
            Log.i(TAG, "doInBackground");

            adTipoMovimiento = new ArrayAdapter<String>(compras.this, R.layout.spinner_item_compras, lsTipomovimiento);
            adAlmacen = new ArrayAdapter<String>(compras.this, R.layout.spinner_item_compras, lsAlmacen);
            adProveedor = new ArrayAdapter<String>(compras.this, R.layout.spinner_item_compras, lsProveedor);
            adEstacionDestino = new ArrayAdapter<String>(compras.this, R.layout.spinner_item_compras, lsEstacionDestino);
            adAlmacenDestino = new ArrayAdapter<String>(compras.this, R.layout.spinner_item_compras, lsAlmacenDestino);
            adCantidad = new ArrayAdapter<String>(compras.this, R.layout.spinner_item_compras, lsCantidad);

            String comboscompras = obtencomboscompras(numestacion);

            if (!comboscompras.equals("-1")) {
                String[] parts1 = comboscompras.split("%");
                //listado1.clear();
                /**rutina para poblar combo box o android splitter**/
                int x = 1;
                if (parts1.length == 7)
                {
                    String[] parts2 = parts1[x].split("@");
                    String[] parts3 = parts1[x+1].split("@");
                    String[] parts4 = parts1[x+2].split("@");
                    String[] parts5 = parts1[x+3].split("@");
                    productos = parts1[x+4];

                    x = 0;
                    while (x < parts2.length) {
                        lsAlmacen.add(parts2[x]);
                        lsAlmacenDestino.add(parts2[x]);
                        x++;
                    }
                    x = 0;
                    while (x < parts3.length) {
                        lsTipomovimiento.add(parts3[x]);
                        x++;
                    }
                    x = 0;
                    while (x < parts4.length) {
                        lsProveedor.add(parts4[x]);
                        x++;
                    }
                    x = 0;
                    while (x < parts5.length) {
                        lsEstacionDestino.add(parts5[x]);
                        x++;
                    }
                    x = 1;
                    while (x <= 10) {
                        lsCantidad.add(Integer.toString(x));
                        x++;
                    }
                    lsCantidad.add("15");
                    lsCantidad.add("20");
                    lsCantidad.add("30");
                    lsCantidad.add("50");
                    lsCantidad.add("100");
                    lsCantidad.add("1000");

                    fol = (parts1[6].toString());
                }
                /***************************************************/
            } else {
                //z = "Usuario o Clave incorrecta";
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            Log.i(TAG, "onPostExecute");
            adTipoMovimiento.setDropDownViewResource(R.layout.spinner_item_compras);
            spTipoMovimiento.setAdapter(adTipoMovimiento);

            adAlmacen.setDropDownViewResource(R.layout.spinner_item_compras);
            spAlmacen.setAdapter(adAlmacen);

            adProveedor.setDropDownViewResource(R.layout.spinner_item_compras);
            spProveedor.setAdapter(adProveedor);

            adEstacionDestino.setDropDownViewResource(R.layout.spinner_item_compras);
            spEstacionDestino.setAdapter(adEstacionDestino);

            adAlmacenDestino.setDropDownViewResource(R.layout.spinner_item_compras);
            spAlmacenDestino.setAdapter(adAlmacenDestino);

            adCantidad.setDropDownViewResource(R.layout.spinner_item_compras);
            spCantidad.setAdapter(adCantidad);

            folio.setText(fol);
            //Toast.makeText(parametrosinventarios.this, "Response" + resultString.toString(), Toast.LENGTH_LONG).show();
        }
    }

    private class AsyncCallWS2 extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            Log.i(TAG, "onPreExecute");
        }

        @Override
        protected Void doInBackground(Void... params) {
            Log.i(TAG, "doInBackground");

            String IANresultado = IANMovimientoAlmacenGuarda(Maestro,Detalle);

            resultString = IANresultado;

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            Log.i(TAG, "onPostExecute");

            Toast.makeText(compras.this, "Respuesta: " + resultString.toString(), Toast.LENGTH_LONG).show();
            finish();
        }
    }
}

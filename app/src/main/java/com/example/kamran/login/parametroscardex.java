package com.example.kamran.login;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;
import java.util.Calendar;

import static com.example.kamran.login.MainActivity.SIGNATURE_ACTIVITY;

public class parametroscardex extends AppCompatActivity {

    String TAG = "Response";
    String resultString, ip, numestacion, gfechaini, gfechafin, gidalmacen;
    static final int START_DATE_DIALOG_ID = 0;
    static final int END_DATE_DIALOG_ID = 1;

    int day,month,year;
    int mDay,mMonth,mYear;
    DatePickerDialog.OnDateSetListener from_dateListener,to_dateListener;

    Button btnfechainicial, btnfechafinal, btnAceptarcardex;
    TextView txtfechahoraini, txtfechahorafin, nomusuario,estacionnumero;

    Spinner spinner;
    ArrayList<String> listado = new ArrayList<>();
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parametroscardex);

        spinner = (Spinner) findViewById(R.id.spinnercardex);
        btnfechafinal = (Button) findViewById(R.id.obtenfechafinal);
        btnfechainicial = (Button) findViewById(R.id.obtenfechainicio);
        btnAceptarcardex = (Button) findViewById(R.id.btnaceptarcardex);
        txtfechahoraini = (TextView) findViewById(R.id.txtfechaini);
        txtfechahorafin = (TextView) findViewById(R.id.txtfechafinal);
        nomusuario = (TextView) findViewById(R.id.textView1);
        estacionnumero = (TextView) findViewById(R.id.textView2);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        ip = prefs.getString("ipservidor", "192.168.0.6");
        numestacion = prefs.getString("numeroestacion","2601");

        Intent intent = getIntent();
        nomusuario.setText("NOMBRE DE USUARIO: "+ intent.getStringExtra("nombreusuario"));
        estacionnumero.setText("ESTACION NUMERO: " + numestacion);

        Calendar calendario = Calendar.getInstance();
        year = calendario.get(Calendar.YEAR);
        month= calendario.get(Calendar.MONTH)+1;
        day  = calendario.get(Calendar.DAY_OF_MONTH);
        txtfechahoraini.setText(
                new StringBuilder()
                        // Month is 0 based so add 1
                        .append(String.format("%04d",year)).append("-")
                        .append(String.format("%02d",month)).append("-")
                        .append(String.format("%02d",day)).append(""));
        txtfechahorafin.setText(
                new StringBuilder()
                        // Month is 0 based so add 1
                        .append(String.format("%04d",year)).append("-")
                        .append(String.format("%02d",month)).append("-")
                        .append(String.format("%02d",day)).append(""));
        parametroscardex.AsyncCallWS task = new parametroscardex.AsyncCallWS();
        task.execute();

        btnfechainicial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                year = c.get(Calendar.YEAR);
                month= c.get(Calendar.MONTH);
                day  = c.get(Calendar.DAY_OF_MONTH);

                showDialog(START_DATE_DIALOG_ID);
            }
        });

        btnfechafinal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                year = c.get(Calendar.YEAR);
                month= c.get(Calendar.MONTH);
                day  = c.get(Calendar.DAY_OF_MONTH);

                showDialog(END_DATE_DIALOG_ID);
            }
        });

        btnAceptarcardex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gfechaini = txtfechahoraini.getText().toString();
                gfechafin = txtfechahorafin.getText().toString();
                gidalmacen= spinner.getSelectedItem().toString();

                parametroscardex.AsyncCallWSGeneraReporte generaReporte = new parametroscardex.AsyncCallWSGeneraReporte();
                generaReporte.execute();
            }
        });

        from_dateListener =
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        mYear = year;
                        mMonth = monthOfYear+1;
                        mDay = dayOfMonth;
                        updateStartDisplay();
                    }
                };

        to_dateListener =
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        mYear = year;
                        mMonth= monthOfYear+1;
                        mDay = dayOfMonth;
                        updateEndDisplay();
                    }
                };
    }

    private DatePickerDialog.OnDateSetListener mDateSetListener =
            new DatePickerDialog.OnDateSetListener() {

                public void onDateSet(DatePicker view, int year,
                                      int monthOfYear, int dayOfMonth) {
                    mYear = year;
                    mMonth = monthOfYear+1;
                    mDay = dayOfMonth;
                    updateStartDisplay();
                }
            };

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case START_DATE_DIALOG_ID:
                return new DatePickerDialog(this,
                        from_dateListener,
                        year, month, day);
            case END_DATE_DIALOG_ID:
                return new DatePickerDialog(this,
                        to_dateListener,
                        year, month, day);
        }
        return null;
    }

    private void updateStartDisplay() {
        txtfechahoraini.setText(
                new StringBuilder()
                        // Month is 0 based so add 1
                        .append(String.format("%04d",mYear)).append("-")
                        .append(String.format("%02d",mMonth)).append("-")
                        .append(String.format("%02d",mDay)).append(""));


    }

    private void updateEndDisplay() {
        txtfechahorafin.setText(
                new StringBuilder()
                        // Month is 0 based so add 1
                        .append(String.format("%04d",mYear)).append("-")
                        .append(String.format("%02d",mMonth)).append("-")
                        .append(String.format("%02d",mDay)).append(""));


    }

    public String obtenAlmacenEstacion(String EstacionID) {
        String SOAP_ACTION = "urn:androidserviceIntf-Iandroidservice#comboalmacen";
        String METHOD_NAME = "comboalmacen";
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

    public String generaReportePDF(String fechaini, String fechafin, String numeroestacion, String idalmacen)
    {
        String SOAP_ACTION = "urn:androidserviceIntf-Iandroidservice#generareporte";
        String METHOD_NAME = "generareporte";
        String NAMESPACE = "urn:androidserviceIntf";
        String URL = "http://" + ip + ":8070/soap/Iandroidservice";

        try {
            SoapObject Request = new SoapObject(NAMESPACE, METHOD_NAME);
            Request.addProperty("nombrereporte", "CARDEX INVENTARIOS");
            Request.addProperty("fechaini", fechaini);
            Request.addProperty("fechafin", fechafin);
            Request.addProperty("numestacion", numeroestacion);
            Request.addProperty("idalmacen", idalmacen);

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

    private class AsyncCallWS extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            Log.i(TAG, "onPreExecute");
        }

        @Override
        protected Void doInBackground(Void... params) {
            Log.i(TAG, "doInBackground");

            adapter = new ArrayAdapter<String>(parametroscardex.this, R.layout.spinner_item, listado);

            //adapter = new ArrayAdapter.createFromResource(this,listado,android.R.layout.simple_expandable_list_item_1);

            String comboestacionalmacen = obtenAlmacenEstacion(numestacion);

            if (!comboestacionalmacen.equals("-1")) {
                String[] parts1 = comboestacionalmacen.split("%");
                listado.clear();
                /**rutina para poblar combo box o android splitter**/
                int x = 0;
                while (x < parts1.length) {
                    String[] parts2 = parts1[x].split("@");
                    listado.add(parts2[1]);
                    x++;
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
            adapter.setDropDownViewResource(R.layout.spinner_item);
            spinner.setAdapter(adapter);
            //Toast.makeText(parametrosinventarios.this, "Response" + resultString.toString(), Toast.LENGTH_LONG).show();
        }
    }

    private class AsyncCallWSGeneraReporte extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            Log.i(TAG, "onPreExecute");
        }

        @Override
        protected Void doInBackground(Void... params) {
            Log.i(TAG, "doInBackground");

            /**rutina de generacion de reporte**/
            String urlreporte = generaReportePDF(gfechaini,gfechafin,numestacion,gidalmacen);
            /***********************************/

            Intent intent = new Intent(getApplicationContext(), PDFReader.class);
            intent.putExtra("urlpdffile", urlreporte);
            intent.putExtra("fechaini", gfechaini);
            intent.putExtra("fechafin", gfechafin);
            intent.putExtra("idalmacen", gidalmacen);
            intent.putExtra("NOMBREREPORTE", "CARDEX INVENTARIOS");
            startActivityForResult(intent, SIGNATURE_ACTIVITY);

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            Log.i(TAG, "onPostExecute");
            Toast.makeText(parametroscardex.this, "Response" + resultString.toString(), Toast.LENGTH_LONG).show();
        }
    }
}

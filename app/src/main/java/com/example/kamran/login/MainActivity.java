package com.example.kamran.login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.sql.Connection;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;


public class MainActivity extends ActionBarActivity
{
    //
    public static final int SIGNATURE_ACTIVITY = 1;
    String TAG = "Response";
    String resultString;

    Button login;
    EditText username,password;
    ProgressBar progressBar;
    // End Declaring layout button, edit texts

    // Declaring connection variables
    Connection con;
    String un,pass,db,ip,usuarioid, nombreusuario, numestacion;

    String usr, psw;
    //End Declaring connection variables

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        MenuInflater mMenuInflater = getMenuInflater();
        mMenuInflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings)
        {
            Intent intent = new Intent(getApplicationContext(), Configuraciones_Activitie.class);
            startActivityForResult(intent, SIGNATURE_ACTIVITY);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Getting values from button, texts and progress bar
        login = (Button) findViewById(R.id.button);
        username = (EditText) findViewById(R.id.editText);
        password = (EditText) findViewById(R.id.editText2);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        // End Getting values from button, texts and progress bar

        // Declaring Server ip, username, database name and password
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);


        ip = prefs.getString("ipservidor","192.168.0.6");
        db = prefs.getString("dbservidor","GAUSS_VER1.1");
        un = prefs.getString("usuarioservidor","sa");
        pass = prefs.getString("passwordservidor","Cistem32");

        numestacion = prefs.getString("numeroestacion","2601");

        // Declaring Server ip, username, database name and password


        // Setting up the function when button login is clicked
        login.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //ocultar teclado despues de precionar boton***************//
                InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(username.getWindowToken(), 0);
                inputMethodManager.hideSoftInputFromWindow(password.getWindowToken(), 0);

                CheckLogin checkLogin = new CheckLogin();// this is the Asynctask, which is used to process in background to reduce load on app process
                checkLogin.execute("");
            }
        });
    }

    public String conectar() {
        /*urn:LibraryIngresos-ServiceIngresos#Login*/
        //String SOAP_ACTION = "urn:LibraryIngresos-ServiceIngresos#LoginAndroid";
        //String METHOD_NAME = "LoginAndroid";
       // String NAMESPACE = "urn:LibraryIngresos-ServiceIngresos";
        //String URL = "http://"+ip+":8001/soap/ServiceIngresos";
        //String URL ="http://"+ip+":8001/SOAP?service=ServiceIngresos";
        String SOAP_ACTION = "urn:androidserviceIntf-Iandroidservice#login";
        String METHOD_NAME = "login";
        String NAMESPACE = "urn:androidserviceIntf";
        String URL = "http://"+ip+":8070/soap/Iandroidservice";
        String z;
        boolean isSuccess;

        try {
            SoapObject Request = new SoapObject(NAMESPACE, METHOD_NAME);
            Request.addProperty("usr", usr);
            Request.addProperty("password", psw);

            SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            soapEnvelope.dotNet = true;
            soapEnvelope.setOutputSoapObject(Request);

            HttpTransportSE transport = new HttpTransportSE(URL,80000);

            transport.call(SOAP_ACTION, soapEnvelope);
            Object  response = (Object) soapEnvelope.getResponse();
            resultString = response.toString();

            return resultString;

            //Log.i(TAG, "Result Celsius: " + resultString);
        } catch (Exception ex) {
            resultString = "-1";
            Log.e(TAG, "Error: " + ex.getMessage());
        }

        if (resultString.equals("-1"))
            return resultString;
        else
            return SOAP_ACTION;
    }

    public class CheckLogin extends AsyncTask<String,String,String>
    {
        String z = "";
        Boolean isSuccess = false;

        @Override
        protected void onPreExecute()
        {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String r)
        {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(MainActivity.this, r, Toast.LENGTH_SHORT).show();
            if(isSuccess)
            {
                Toast.makeText(MainActivity.this , "Login Exitoso" , Toast.LENGTH_LONG).show();
                //finish();
            }
            if(!isSuccess)
            {
                Toast.makeText(MainActivity.this , "No se pudo establecer coneccion con el servidor" , Toast.LENGTH_LONG).show();
            }
        }
        @Override
        protected String doInBackground(String... params)
        {
            usr = username.getText().toString();
            psw = password.getText().toString();
            if(usr.trim().equals("")|| psw.trim().equals(""))
                z = "Por favor ingrese usuario y password";
            else
            {
                resultString = conectar();

                if (!resultString.equals("-1"))
                {
                        String[] parts = resultString.split("@");
                        usuarioid = parts[0];
                        nombreusuario = parts[1];
                        z = "Login Exitoso";
                        isSuccess = true;

                        Intent intent = new Intent(getApplicationContext(), Inventario.class);
                        intent.putExtra("usuarioid", usuarioid);
                        intent.putExtra("nombreusuario", nombreusuario);
                        startActivityForResult(intent, SIGNATURE_ACTIVITY);
                }
                else
                {
                    z = "Usuario o Clave incorrecta";
                    isSuccess = false;
                }
            }
            return z;
        }
    }
}

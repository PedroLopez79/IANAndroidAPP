package com.example.kamran.login;

/**
 * Created by RedPacifico on 2/7/2017.
 */
import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.ByteArrayOutputStream;

public class FotoUnidad extends AppCompatActivity {
    private ImageSurfaceView mImageSurfaceView;
    private Camera camera;

    private FrameLayout cameraPreviewLayout;
    private ImageView capturedImageHolder;
    private static final int REQUEST_CAMERARESULT=201;

    String TAG = "Response";
    String resultString,rotarpantalla;
    Boolean mandaimagen;

    String encodedImage, firmadefault, encodedImage2, camarafrontal, rotarimagen;
    String ip,Actividad,Frecuencia,Observaciones,FechaHora,Folio,Fecha,usuarioid,estacionid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fotounidad);

        try {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            rotarpantalla = prefs.getString("rotarpantalla", "NO");
            if (rotarpantalla.equals("SI"))
            {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
            }
            else
            {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
            ip = prefs.getString("ipservidor", "192.168.0.6");
            camarafrontal = prefs.getString("camarafrontal", "NO");
            rotarimagen = prefs.getString("rotarimagen", "90");
            Intent intent = getIntent();
            Actividad = intent.getStringExtra("Actividad");
            Frecuencia = intent.getStringExtra("Frecuencia");
            Observaciones = intent.getStringExtra("Observaciones");
            FechaHora = intent.getStringExtra("FechaHora");
            Folio = intent.getStringExtra("Folio");
            Fecha = intent.getStringExtra("Fecha");
            usuarioid = intent.getStringExtra("UsuarioID");
            estacionid = intent.getStringExtra("EstacionID");
            encodedImage = intent.getStringExtra("encodedimage");
            firmadefault = intent.getStringExtra("firmadefault");

            /*VER SI EL USUARIO OTORGO PERMISOS****************************************************/
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_DENIED)
            {
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, REQUEST_CAMERARESULT);
            }
            /**************************************************************************************/
            cameraPreviewLayout = (FrameLayout) findViewById(R.id.camera_preview);
            capturedImageHolder = (ImageView) findViewById(R.id.captured_image);
            camera = checkDeviceCamera();

            mImageSurfaceView = new ImageSurfaceView(FotoUnidad.this, camera);
            cameraPreviewLayout.addView(mImageSurfaceView);
        }
        catch(Exception e)
        {
            //e.printStackTrace();
            //Toast.makeText(this, "Error camara: "+ e.getMessage(), Toast.LENGTH_LONG).show();
        }

        Button captureButton = (Button)findViewById(R.id.button);
        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                camera.takePicture(null, null, pictureCallback);
            }
        });

        Button boton2 = (Button)findViewById(R.id.button2);
        boton2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                camera.stopPreview();
                camera.startPreview();
            }
        });

        Button btnguardar = (Button)findViewById(R.id.btnguardar);
        btnguardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                byte[] byteArray;

                mandaimagen = true;

                Bitmap image = null;
                try
                {
                    image = ((BitmapDrawable) capturedImageHolder.getDrawable()).getBitmap();
                }
                catch(Exception e)
                {
                    encodedImage2 = "";

                    mandaimagen = false;

                    if (firmadefault.equals("NO")) {
                        AsyncCallWS task = new AsyncCallWS();
                        task.execute();
                    }
                    if (firmadefault.equals("SI")) {
                        AsyncCallWS2 task = new AsyncCallWS2();
                        task.execute();
                    }
                    Bundle b = new Bundle();
                    b.putString("status", "Hecho");
                    Intent intent = new Intent();
                    intent.putExtras(b);
                    setResult(RESULT_OK, intent);

                    finish();
                }

                if (mandaimagen) {
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    image.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
                    byteArray = byteArrayOutputStream.toByteArray();
                    encodedImage2 = Base64.encodeToString(byteArray, Base64.DEFAULT);

                    if (firmadefault.equals("NO")) {
                        AsyncCallWS task = new AsyncCallWS();
                        task.execute();
                    }
                    if (firmadefault.equals("SI")) {
                        AsyncCallWS2 task = new AsyncCallWS2();
                        task.execute();
                    }
                    Bundle b = new Bundle();
                    b.putString("status", "Hecho");
                    Intent intent = new Intent();
                    intent.putExtras(b);
                    setResult(RESULT_OK, intent);

                    finish();
                }
            }
        });
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

    private Camera checkDeviceCamera(){
        Camera mCamera = null;
        try {
            if (camarafrontal.equals("SI"))
            {
                mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);

                mCamera.stopPreview();
                mCamera.setDisplayOrientation(Integer.parseInt(rotarimagen));
                mCamera.startPreview();
            }
            else
            {
                mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
                mCamera.stopPreview();
                mCamera.setDisplayOrientation(Integer.parseInt(rotarimagen));
                mCamera.startPreview();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mCamera;
    }

    PictureCallback pictureCallback = new PictureCallback()  {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            if(bitmap==null){
                Toast.makeText(FotoUnidad.this, "Captured image is empty", Toast.LENGTH_LONG).show();
                return;
            }
            if (camarafrontal.equals("SI")) {

                //bitmap = rotateImage(bitmap, 270);
                bitmap = rotateImage(bitmap, Integer.parseInt(rotarimagen));
            }
            else
            {
                //bitmap = rotateImage(bitmap, 90);
                bitmap = rotateImage(bitmap, Integer.parseInt(rotarimagen));
            }
            capturedImageHolder.setImageBitmap(scaleDownBitmapImage(bitmap, 300, 300 ));
        }
    };

    private Bitmap scaleDownBitmapImage(Bitmap bitmap, int newWidth, int newHeight){
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);
        return resizedBitmap;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class AsyncCallWS extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            Log.i(TAG, "onPreExecute");
        }

        @Override
        protected Void doInBackground(Void... params) {
            Log.i(TAG, "doInBackground");
            calculate();

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            Log.i(TAG, "onPostExecute");
            Toast.makeText(FotoUnidad.this, "Response" + resultString.toString(), Toast.LENGTH_LONG).show();
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
            resultString = obtenfimrmadefault(usuarioid);

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            Log.i(TAG, "onPostExecute");
            Toast.makeText(FotoUnidad.this, "Response" + resultString.toString(), Toast.LENGTH_LONG).show();
        }

    }

    public String obtenfimrmadefault(String usrid)
    {
        String SOAP_ACTION = "urn:androidserviceIntf-Iandroidservice#firmadefault";
        String METHOD_NAME = "firmadefault";
        String NAMESPACE = "urn:androidserviceIntf";
        String URL = "http://"+ip+":8070/soap/Iandroidservice";

        try {
            SoapObject Request = new SoapObject(NAMESPACE, METHOD_NAME);
            Request.addProperty("Actividad", Actividad);
            Request.addProperty("Frecuencia", Frecuencia);
            Request.addProperty("Observaciones", Observaciones);
            Request.addProperty("FechaHora", FechaHora);
            Request.addProperty("Folio", Folio);
            Request.addProperty("Fecha", Fecha);
            Request.addProperty("estacionid", estacionid);
            Request.addProperty("usuarioid", usrid);
            Request.addProperty("fotobase64", encodedImage2);

            SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            soapEnvelope.dotNet = true;
            soapEnvelope.setOutputSoapObject(Request);

            HttpTransportSE transport = new HttpTransportSE(URL,100000);

            transport.call(SOAP_ACTION, soapEnvelope);
            //resultString = (SoapPrimitive) soapEnvelope.getResponse();
            Object  response = (Object) soapEnvelope.getResponse();
            resultString = response.toString();

            Log.i(TAG, "Result Celsius: " + resultString);
        } catch (Exception ex) {
            Log.e(TAG, "Error: " + ex.getMessage());
        }

        return resultString;
    }

    public void calculate() {
        String SOAP_ACTION = "urn:androidserviceIntf-Iandroidservice#guardadatosfirma";
        String METHOD_NAME = "guardadatosfirma";
        String NAMESPACE = "urn:androidserviceIntf";
        String URL = "http://"+ip+":8070/soap/Iandroidservice";

        try {
            SoapObject Request = new SoapObject(NAMESPACE, METHOD_NAME);
            Request.addProperty("Actividad", Actividad);
            Request.addProperty("Frecuencia", Frecuencia);
            Request.addProperty("Observaciones", Observaciones);
            Request.addProperty("FechaHora", FechaHora);
            Request.addProperty("Folio", Folio);
            Request.addProperty("Fecha", Fecha);
            Request.addProperty("usuarioid", usuarioid);
            Request.addProperty("estacionid", estacionid);
            Request.addProperty("firmabase64", encodedImage);
            Request.addProperty("fotobase64", encodedImage2);

            SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            soapEnvelope.dotNet = true;
            soapEnvelope.setOutputSoapObject(Request);

            HttpTransportSE transport = new HttpTransportSE(URL,200000);

            transport.call(SOAP_ACTION, soapEnvelope);
            //resultString = (SoapPrimitive) soapEnvelope.getResponse();
            Object  response = (Object) soapEnvelope.getResponse();
            resultString = response.toString();

            Log.i(TAG, "Result Celsius: " + resultString);
        } catch (Exception ex) {
            Log.e(TAG, "Error: " + ex.getMessage());
        }
    }
}

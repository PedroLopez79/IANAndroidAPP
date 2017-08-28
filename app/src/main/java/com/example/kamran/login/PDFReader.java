package com.example.kamran.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.File;
import java.io.IOException;

import static com.example.kamran.login.MainActivity.SIGNATURE_ACTIVITY;

public class PDFReader extends AppCompatActivity {

    private ImageView imageView;
    private int currentPage = 0;
    private Button next,previous,print;
    private LinearLayout lin;

    String TAG = "Response";
    String resultString, ip, numestacion, gfechaini, gfechafin, gidalmacen, REPNOMBRE;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdfreader);

        next = (Button) findViewById(R.id.next);
        previous = (Button) findViewById(R.id.previous);
        print = (Button) findViewById(R.id.print);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        ip = prefs.getString("ipservidor", "192.168.0.6");
        numestacion = prefs.getString("numeroestacion","2601");

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentPage++;
                lin.removeAllViews();
                render();
            }
        });

        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentPage--;
                lin.removeAllViews();
                render();
            }
        });

        print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PDFReader.AsyncCallWSGeneraReporte generaReporte = new PDFReader.AsyncCallWSGeneraReporte();
                generaReporte.execute();
            }
        });

        Intent intent = getIntent();
        String urlpdf = intent.getStringExtra("urlpdffile");
        gfechaini= intent.getStringExtra("fechaini");
        gfechafin= intent.getStringExtra("fechafin");
        gidalmacen=intent.getStringExtra("idalmacen");
        REPNOMBRE= intent.getStringExtra("NOMBREREPORTE");
        new DownloadFile().execute("http://ianservices.ddns.net/"+urlpdf, "viewpdf.pdf");
    }

    private void render()
    {
        try {
            File file = new File(Environment.getExternalStorageDirectory().toString()+"/PDF DOWNLOAD/viewpdf.pdf");
            //File file = new File("/storage/emulated/0/PDF DOWNLOAD/viewpdf.pdf");

            ParcelFileDescriptor fileDescriptor = null;
            fileDescriptor = ParcelFileDescriptor.open(
                    file, ParcelFileDescriptor.MODE_READ_ONLY);

            //min. API Level 21
            PdfRenderer pdfRenderer = null;
            pdfRenderer = new PdfRenderer(fileDescriptor);

            final int pageCount = pdfRenderer.getPageCount();
            Toast.makeText(this,
                    "pageCount = " + pageCount,
                    Toast.LENGTH_LONG).show();

            //Display page 0
            if (currentPage < 0) currentPage++;
            if (currentPage > pageCount-1) currentPage--;

            PdfRenderer.Page rendererPage = pdfRenderer.openPage(currentPage);
            //int rendererPageWidth = rendererPage.getWidth();
            //int rendererPageHeight = rendererPage.getHeight();
            int rendererPageWidth = 1200;
            int rendererPageHeight = 1500;

            Bitmap bitmap = Bitmap.createBitmap(
                    rendererPageWidth,
                    rendererPageHeight,
                    Bitmap.Config.ARGB_8888);
            rendererPage.render(bitmap, null, null,
                    PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);

            TouchImageView i = new TouchImageView(this);
            i.setImageBitmap(bitmap);
            i.setMaxZoom(4f);
            lin = (LinearLayout) findViewById(R.id.layoutBase);
            lin.addView(i);

            //imageView.setImageBitmap(bitmap);
            rendererPage.close();

            pdfRenderer.close();
            fileDescriptor.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class DownloadFile extends AsyncTask<String, Void, Void>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... strings) {
            String fileUrl = strings[0];
            String fileName = strings[1];

            String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
            //File file = new File(extStorageDirectory);
            //boolean deleted = file.delete();

            File folder = new File(extStorageDirectory, "PDF DOWNLOAD");
            folder.mkdir();

            File file = new File(folder+"/"+fileName);
            boolean delete = file.delete();

            File pdfFile = new File(folder, fileName);

            try{
                pdfFile.createNewFile();
            }catch (IOException e){
                e.printStackTrace();
            }
            FileDownloader.downloadFile(fileUrl, pdfFile);
            return null;

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Toast.makeText(getApplicationContext(), "Descarga PDF Exitosa", Toast.LENGTH_SHORT).show();

            Log.d("Descarga Completada", "----------");
            render();
        }
    }

    /**************************LLAMADA A IMPRIMIR EL REPORTE CON SERVCIO SOAP**********************/
    /**********************************************************************************************/
    public String generaReportePDF(String fechaini, String fechafin, String numeroestacion, String idalmacen, String NOMBREREPORTE)
    {
        String SOAP_ACTION = "urn:androidserviceIntf-Iandroidservice#generareporte";
        String METHOD_NAME = "imprimereporte";
        String NAMESPACE = "urn:androidserviceIntf";
        String URL = "http://" + ip + ":8070/soap/Iandroidservice";

        try {
            SoapObject Request = new SoapObject(NAMESPACE, METHOD_NAME);
            Request.addProperty("nombrereporte", NOMBREREPORTE);
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

    private class AsyncCallWSGeneraReporte extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            Log.i(TAG, "onPreExecute");
        }

        @Override
        protected Void doInBackground(Void... params) {
            Log.i(TAG, "doInBackground");

            /**rutina de generacion de reporte**/
            String urlreporte = generaReportePDF(gfechaini,gfechafin,numestacion,gidalmacen,REPNOMBRE);
            /***********************************/

            //Intent intent = new Intent(getApplicationContext(), PDFReader.class);
            //intent.putExtra("urlpdffile", urlreporte);
            //startActivityForResult(intent, SIGNATURE_ACTIVITY);

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            Log.i(TAG, "onPostExecute");
            Toast.makeText(PDFReader.this, "Response" + resultString.toString(), Toast.LENGTH_LONG).show();
            finish();
        }
    }
    /**************************LLAMADA A IMPRIMIR EL REPORTE CON SERVCIO SOAP**********************/
    /**********************************************************************************************/
}

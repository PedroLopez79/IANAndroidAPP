package com.example.kamran.login;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

/**
 * Created by Kuncoro on 14/01/2016.
 */
public class Signature extends AppCompatActivity {

    public static final int SIGNATURE_ACTIVITY = 1;
    String TAG = "Response";
    String resultString;
    boolean obtenfirmausuario;

    LinearLayout mContent;
    signature mSignature;
    Button mClear, mGetSign, frmdefault;
    public static String tempDir;
    public int count = 1;
    public String current = null;
    private Bitmap mBitmap;
    View mView;
    File mypath;
    String path_image;
    ProgressBar progressBar;
    ImageView imagebox;

    private String uniqueId;
    String encodedImage;
    String ip,Actividad,Frecuencia,Observaciones,FechaHora,Folio,Fecha,usuarioid,estacionid;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signature);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Dibuje Firma");

        obtenfirmausuario = false;
        tempDir = Environment.getExternalStorageDirectory() + "/" + getResources().getString(R.string.external_dir) + "/";
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getDir(getResources().getString(R.string.external_dir), Context.MODE_PRIVATE);

        prepareDirectory();
        uniqueId = getTodaysDate() + "_" + getCurrentTime() + "_" + Math.random();
        current = uniqueId + ".png";
        mypath= new File(directory,current);

        progressBar = (ProgressBar) findViewById(R.id.progressBar2);
        progressBar.setVisibility((View.GONE));
        Intent intent = getIntent();
        Actividad = intent.getStringExtra("Actividad");
        Frecuencia= intent.getStringExtra("Frecuencia");
        Observaciones= intent.getStringExtra("Observaciones");
        FechaHora= intent.getStringExtra("FechaHora");
        Folio= intent.getStringExtra("Folio");
        Fecha= intent.getStringExtra("Fecha");
        usuarioid = intent.getStringExtra("UsuarioID");
        estacionid= intent.getStringExtra("EstacionID");

        imagebox = (ImageView) findViewById(R.id.imageView);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        ip = prefs.getString("ipservidor","192.168.0.6");

        mContent        = (LinearLayout) findViewById(R.id.canvas_sign);
        mSignature      = new signature(this, null);
        mSignature.setBackgroundColor(Color.WHITE);
        mContent.addView(mSignature, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mClear          = (Button)findViewById(R.id.clear);
        mGetSign        = (Button)findViewById(R.id.save);
        frmdefault      = (Button)findViewById(R.id.frmdefault);
        mGetSign.setEnabled(false);
        mView           = mContent;

        mClear.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v) {
                Log.d("log_tag", "Panel Limpio");
                mSignature.clear();
                mGetSign.setEnabled(false);
            }
        });

        mGetSign.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                Log.v("log_tag", "Firma Guardada");
                mView.setDrawingCacheEnabled(true);
                mSignature.save(mView);

                Intent intent2 = new Intent(getApplicationContext(), FotoUnidad.class);
                intent2.putExtra("Actividad",Actividad);
                intent2.putExtra("Frecuencia",Frecuencia);
                intent2.putExtra("Observaciones",Observaciones);
                intent2.putExtra("FechaHora", FechaHora);
                intent2.putExtra("Folio", Folio);
                intent2.putExtra("Fecha", Fecha);
                intent2.putExtra("UsuarioID", usuarioid);
                intent2.putExtra("EstacionID", estacionid);
                intent2.putExtra("encodedimage", encodedImage);
                intent2.putExtra("firmadefault", "NO");
                startActivityForResult(intent2, SIGNATURE_ACTIVITY);
                /*DESABILITADO PARA ENVIAR A FOTO UNIDAD*/
                //Bundle b = new Bundle();
                //b.putString("status", "Hecho");
                //b.putString("image", path_image);
                //Intent intent = new Intent();
                //intent.putExtras(b);
                //setResult(RESULT_OK, intent);

                finish();
            }
        });

        frmdefault.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), FotoUnidad.class);
                intent.putExtra("Actividad",Actividad);
                intent.putExtra("Frecuencia",Frecuencia);
                intent.putExtra("Observaciones",Observaciones);
                intent.putExtra("FechaHora", FechaHora);
                intent.putExtra("Folio", Folio);
                intent.putExtra("Fecha", Fecha);
                intent.putExtra("UsuarioID", usuarioid);
                intent.putExtra("EstacionID", estacionid);
                intent.putExtra("encodedimage", encodedImage);
                intent.putExtra("firmadefault", "SI");
                startActivityForResult(intent, SIGNATURE_ACTIVITY);
                /*DESABILITAR PARA ENVIAR A FOTO UNIDAD*/
                //obtenfirmausuario = true;
                //AsyncCallWS2 task = new AsyncCallWS2();
                //task.execute();
                //Bundle b = new Bundle();
                //b.putString("status", "Hecho");
                //b.putString("image", path_image);
                //Intent intent = new Intent();
                //intent.putExtras(b);
                //setResult(RESULT_OK, intent);


                finish();
            }
        });

    }

    @Override
    protected void onDestroy() {
        Log.w("GetSignature", "onDestory");
        super.onDestroy();
    }

    private String getTodaysDate() {

        final Calendar c = Calendar.getInstance();
        int todaysDate =     (c.get(Calendar.YEAR) * 10000) +
                ((c.get(Calendar.MONTH) + 1) * 100) +
                (c.get(Calendar.DAY_OF_MONTH));
        Log.w("DATE:",String.valueOf(todaysDate));
        return(String.valueOf(todaysDate));

    }

    private String getCurrentTime() {

        final Calendar c = Calendar.getInstance();
        int currentTime =     (c.get(Calendar.HOUR_OF_DAY) * 10000) +
                (c.get(Calendar.MINUTE) * 100) +
                (c.get(Calendar.SECOND));
        Log.w("TIME:", String.valueOf(currentTime));
        return(String.valueOf(currentTime));

    }


    private boolean prepareDirectory()
    {
        try
        {
            if (makedirs())
            {
                return true;
            } else {
                return false;
            }
        } catch (Exception e)
        {
            e.printStackTrace();
            Toast.makeText(this, "No se pudo inicializar sistema de archivos.. esta correctamente instalada la memoria SD?", Toast.LENGTH_LONG).show();
            return false;
        }
    }

    private boolean makedirs()
    {
        File tempdir = new File(tempDir);
        if (!tempdir.exists())
            tempdir.mkdirs();

        if (tempdir.isDirectory())
        {
            File[] files = tempdir.listFiles();
            for (File file : files)
            {
                if (!file.delete())
                {
                    System.out.println("Failed to delete " + file);
                }
            }
        }
        return (tempdir.isDirectory());
    }

    public class signature extends View
    {
        private static final float STROKE_WIDTH = 5f;
        private static final float HALF_STROKE_WIDTH = STROKE_WIDTH / 2;
        private Paint paint = new Paint();
        private Path path = new Path();

        private float lastTouchX;
        private float lastTouchY;
        private final RectF dirtyRect = new RectF();

        public signature(Context context, AttributeSet attrs)
        {
            super(context, attrs);
            paint.setAntiAlias(true);
            paint.setColor(Color.BLUE);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStrokeWidth(STROKE_WIDTH);
        }

        public void save(View v)
        {
            progressBar.setVisibility((View.VISIBLE));
            Log.v("log_tag", "Width: " + v.getWidth());
            Log.v("log_tag", "Height: " + v.getHeight());
            if((mBitmap == null))
            {
                mBitmap =  Bitmap.createBitmap (mContent.getWidth(), mContent.getHeight(), Bitmap.Config.RGB_565);
            }

            Canvas canvas = new Canvas(mBitmap);

            try
            {
                    FileOutputStream mFileOutStream = new FileOutputStream(mypath);

                    v.draw(canvas);
                    mBitmap.compress(Bitmap.CompressFormat.PNG, 100, mFileOutStream);

                    mFileOutStream.flush();
                    mFileOutStream.close();
                    path_image = MediaStore.Images.Media.insertImage(getContentResolver(), mBitmap, "title", null);
                    Log.v("log_tag","url: " + path_image);
                    //In case you want to delete the file
                    //boolean deleted = mypath.delete();
                    //Log.v("log_tag","deleted: " + mypath.toString() + deleted);
                    //If you want to convert the image to string use base64 converter

                    imagebox.setImageResource(0);
                    Bitmap originBitmap = null;
                    Uri filePath        = Uri.parse(path_image);
                    originBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                        //BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                        //MediaStore.Images.Media.getBitmap(getContentResolver(), path_image);
                    //Setting the Bitmap to ImageView
                    imagebox.setImageBitmap(originBitmap);
                    imagebox.setVisibility(INVISIBLE);


                /*ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                mBitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
                byteArray = byteArrayOutputStream.toByteArray();
                encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT);*/

                /*ImageView imagebox = (ImageView) findViewById(R.id.imageView);*/


                byte[] byteArray;
                imagebox.setImageBitmap(mBitmap);
                Bitmap image = ((BitmapDrawable) imagebox.getDrawable()).getBitmap();
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                image.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
                byteArray = byteArrayOutputStream.toByteArray();
                encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT);

                /*DESABILITADO PARA ENVIARLO A FOTO UNIDAD*/
                //AsyncCallWS task = new AsyncCallWS();
                //task.execute();
            }
            catch(Exception e)
            {
                Log.v("log_tag", e.toString());
            }
        }

        public void clear()
        {
            path.reset();
            invalidate();
        }

        @Override
        protected void onDraw(Canvas canvas)
        {
            canvas.drawPath(path, paint);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event)
        {
            float eventX = event.getX();
            float eventY = event.getY();
            mGetSign.setEnabled(true);

            switch (event.getAction())
            {
                case MotionEvent.ACTION_DOWN:
                    path.moveTo(eventX, eventY);
                    lastTouchX = eventX;
                    lastTouchY = eventY;
                    return true;

                case MotionEvent.ACTION_MOVE:

                case MotionEvent.ACTION_UP:

                    resetDirtyRect(eventX, eventY);
                    int historySize = event.getHistorySize();
                    for (int i = 0; i < historySize; i++)
                    {
                        float historicalX = event.getHistoricalX(i);
                        float historicalY = event.getHistoricalY(i);
                        expandDirtyRect(historicalX, historicalY);
                        path.lineTo(historicalX, historicalY);
                    }
                    path.lineTo(eventX, eventY);
                    break;

                default:
                    debug("Ignored touch event: " + event.toString());
                    return false;
            }

            invalidate((int) (dirtyRect.left - HALF_STROKE_WIDTH),
                    (int) (dirtyRect.top - HALF_STROKE_WIDTH),
                    (int) (dirtyRect.right + HALF_STROKE_WIDTH),
                    (int) (dirtyRect.bottom + HALF_STROKE_WIDTH));

            lastTouchX = eventX;
            lastTouchY = eventY;

            return true;
        }

        private void debug(String string){
        }

        private void expandDirtyRect(float historicalX, float historicalY)
        {
            if (historicalX < dirtyRect.left)
            {
                dirtyRect.left = historicalX;
            }
            else if (historicalX > dirtyRect.right)
            {
                dirtyRect.right = historicalX;
            }

            if (historicalY < dirtyRect.top)
            {
                dirtyRect.top = historicalY;
            }
            else if (historicalY > dirtyRect.bottom)
            {
                dirtyRect.bottom = historicalY;
            }
        }

        private void resetDirtyRect(float eventX, float eventY)
        {
            dirtyRect.left = Math.min(lastTouchX, eventX);
            dirtyRect.right = Math.max(lastTouchX, eventX);
            dirtyRect.top = Math.min(lastTouchY, eventY);
            dirtyRect.bottom = Math.max(lastTouchY, eventY);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @SuppressLint("NewApi")
    public Connection connectionclass(String user, String password, String database, String server)
    {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Connection connection = null;
        String ConnectionURL = null;
        try
        {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            ConnectionURL = "jdbc:jtds:sqlserver://" + server + "/" + database + ";user=" + user+ ";password=" + password + ";";
            connection = DriverManager.getConnection(ConnectionURL);
        }
        catch (SQLException se)
        {
            Log.e("error here 1 : ", se.getMessage());
        }
        catch (ClassNotFoundException e)
        {
            Log.e("error here 2 : ", e.getMessage());
        }
        catch (Exception e)
        {
            Log.e("error here 3 : ", e.getMessage());
        }
        return connection;
    }

}
package com.example.kamran.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TabHost;
import android.widget.TextView;

import static com.example.kamran.login.MainActivity.SIGNATURE_ACTIVITY;

public class Inventario extends AppCompatActivity {

    TabHost tabHost;
    String nombreusuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventario);
        TextView texto1 = (TextView) findViewById(R.id.textView1);
        TextView texto2 = (TextView) findViewById(R.id.textView2);
        Button btnInventarios = (Button) findViewById(R.id.btnInventarios);
        Button btnCardexInventarios = (Button) findViewById(R.id.btncardexinventarios);
        Button btnCompras = (Button) findViewById(R.id.btncompras);

        Intent intent = getIntent();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        nombreusuario = intent.getStringExtra("nombreusuario");
        texto1.setText("NOMBRE DE USUARIO: "+ nombreusuario);
        texto2.setText("ESTACION NUMERO: " + prefs.getString("numeroestacion","2601"));

        tabHost = (TabHost) findViewById(R.id.tabhost);
        tabHost.setup();

        TabHost.TabSpec ts = tabHost.newTabSpec("tag1");
        ts.setContent(R.id.INVENTARIO);
        ts.setIndicator("INVENTARIO");
        ts.setIndicator("",getResources().getDrawable(R.drawable.reporteinventariosicon));
        tabHost.addTab(ts);

        ts = tabHost.newTabSpec("tag2");
        ts.setContent(R.id.MOVIMIENTOS);
        ts.setIndicator("MOVIMIENTOS");
        ts.setIndicator("",getResources().getDrawable(R.drawable.movinentario));
        tabHost.addTab(ts);

        ts= tabHost.newTabSpec("tag3");
        ts.setContent(R.id.ll_tab3);
        ts.setIndicator("TERCER-TAB");
        tabHost.addTab(ts);

        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {

            @Override
            public void onTabChanged(String arg0) {

                setTabColor(tabHost);
            }
        });
        setTabColor(tabHost);

        btnInventarios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), parametrosinventarios.class);
                intent.putExtra("nombreusuario", nombreusuario);
                startActivityForResult(intent, SIGNATURE_ACTIVITY);
            }
        });

        btnCardexInventarios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), parametroscardex.class);
                intent.putExtra("nombreusuario", nombreusuario);
                startActivityForResult(intent, SIGNATURE_ACTIVITY);
            }
        });

        btnCompras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), compras.class);
                intent.putExtra("nombreusuario", nombreusuario);
                startActivityForResult(intent, SIGNATURE_ACTIVITY);
            }
        });
    }

    //Change The Backgournd Color of Tabs
    public void setTabColor(TabHost tabhost) {

        int COLOR_TRANSPARENT = 0;

        for(int i=0;i<tabhost.getTabWidget().getChildCount();i++)
            tabhost.getTabWidget().getChildAt(i).setBackgroundColor(COLOR_TRANSPARENT); //unselected

        if(tabhost.getCurrentTab()==0)
            tabhost.getTabWidget().getChildAt(tabhost.getCurrentTab()).setBackgroundColor(R.color.darkgreen); //1st tab selected
        else
            tabhost.getTabWidget().getChildAt(tabhost.getCurrentTab()).setBackgroundColor(R.color.darkgreen); //2nd tab selected
    }
}

package com.example.kamran.login;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;

public class buscaproducto extends android.app.DialogFragment {

    Button btn;
    ListView lv;
    SearchView sv;

    ArrayAdapter<String> adapter;
    String producto;
    public String varproductos = "";

    private ISelectedData mCallback;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootview = inflater.inflate(R.layout.activity_buscaproducto, container, false);

        getDialog().setTitle("Busca Productos");

        lv = (ListView) rootview.findViewById(R.id.buscaproductolv);
        btn= (Button) rootview.findViewById(R.id.buscaproductobtn);
        sv = (SearchView) rootview.findViewById(R.id.buscaproductosv);

        String[] prd = varproductos.split("@");

        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, prd);
        lv.setAdapter(adapter);
        sv.setQueryHint("Busqueda...");

        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String txt) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String txt) {
                adapter.getFilter().filter(txt);
                return false;
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCallback != null)
                    mCallback.onSelectedData(producto);
                dismiss();
            }
        });

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                producto = lv.getItemAtPosition(position).toString();
            }});

        return rootview;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mCallback = (ISelectedData) activity;
        }
        catch (ClassCastException e) {
            Log.d("MyDialog", "Activity doesn't implement the ISelectedData interface");
        }
    }

    public interface ISelectedData
    {
        void onSelectedData(String string);
    }
}

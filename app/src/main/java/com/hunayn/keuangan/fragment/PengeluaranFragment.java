package com.hunayn.keuangan.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hunayn.keuangan.DBHelper;
import com.hunayn.keuangan.R;
import com.hunayn.keuangan.activity.DeleteActivity;
import com.hunayn.keuangan.activity.MainActivity;
import com.hunayn.keuangan.activity.UpdateActivity;
import com.hunayn.keuangan.adapter.TransaksiAdapter;
import com.hunayn.keuangan.model.Transaksi;
import com.hunayn.keuangan.model.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class PengeluaranFragment extends Fragment {

    String[] daftar;
    String[] daftar2;
    String[] daftar3;
    ListView ListView01;
    DBHelper dbcenter;
    Transaksi transaksi;

    protected Cursor cursor;

    public PengeluaranFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_pengeluaran, container, false);

        TextView pengeluaran = (TextView) rootView.findViewById(R.id.pengeluaran);
        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.main.viewPager.setCurrentItem(1);
                Toast.makeText(getActivity(), "Refreshed",Toast.LENGTH_LONG).show();
            }
        });

        dbcenter = new DBHelper(getActivity());
        SQLiteDatabase db = dbcenter.getReadableDatabase();
        cursor = db.rawQuery("SELECT * From tb_keuangan where type LIKE '%Keluar%'", null);
        daftar = new String[cursor.getCount()];
        daftar2 = new String[cursor.getCount()];
        daftar3 = new String[cursor.getCount()];
        cursor.moveToFirst();
        for (int cc = 0; cc < cursor.getCount(); cc++) {
            cursor.moveToPosition(cc);
            daftar3[cc] = cursor.getString(3).toString();
            daftar2[cc] = cursor.getString(0).toString();
            daftar[cc] = cursor.getString(1).toString();
        }

        transaksi = new Transaksi(getActivity());
        ArrayList<HashMap<String, String>> trxList = transaksi.getList3();
        TransaksiAdapter adapter = new TransaksiAdapter(getActivity(), trxList);
        ListView01 = (ListView) rootView.findViewById(R.id.list_transaksi);
        ListView01.setAdapter(adapter);
        ListView01.setSelected(true);
        ListView01.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView arg0, View arg1, int arg2, long arg3) {
                final String selection = daftar2[arg2]; //.getItemAtPosition(arg2).toString();
                final CharSequence[] dialogitem = {" Update Data Keuangan ", " Delete Data Keuangan "};
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Pilihan");
                builder.setItems(dialogitem, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        switch (item) {
                            case 0:
                                Intent i = new Intent(getActivity(), UpdateActivity.class);
                                i.putExtra("id", selection);
                                startActivity(i);
                                break;
                            case 1:
                                Intent in = new Intent(getActivity(), DeleteActivity.class);
                                in.putExtra("id", selection);
                                startActivity(in);
                                break;
                        }
                    }
                });
                builder.create().show();
            }
        });
        ((ArrayAdapter) ListView01.getAdapter()).notifyDataSetInvalidated();

        User dataUser = new User(this.getActivity());
        JSONObject user = dataUser.getUser();
        try {
            Double balance3 = Double.parseDouble(user.getString("pengeluaran"));
            pengeluaran.setText("Rp " + String.format("%,.2f", balance3));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return rootView;
    }
}

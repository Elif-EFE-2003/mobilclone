package com.softwarelif.kurtakip;

import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.BufferedInputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class MainActivity extends AppCompatActivity {
    private List<String> dovizOranList;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        dovizOranList=getDovizOranList();
        adapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,dovizOranList);
        ListView dovizOranLV=(ListView) findViewById(R.id.dovizoranlistView);
        dovizOranLV.setAdapter(adapter);

        Button yenileButon=(Button) findViewById(R.id.yenilebutton);

        yenileButon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dovizOranList=getDovizOranList();
                adapter.notifyDataSetChanged();
            }
        });
    }

    private List<String> getDovizOranList(){
        HttpURLConnection uC=null;
        try{
            StrictMode.ThreadPolicy policy=new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            URL url=new URL(getResources().getString(R.string.doviz_takip_url));
            uC=(HttpURLConnection) url.openConnection();
            int sonuckodu= uC.getResponseCode();

            if(sonuckodu==HttpURLConnection.HTTP_OK){
                BufferedInputStream stream=new BufferedInputStream(uC.getInputStream());
                return getDovizOranListFromInputStream(stream);
            }
        }catch (Exception e){
            Log.d("DovizTakip","HTTP baglantisi kurulurken hata olustu",e);
        }finally {
            if(uC!=null)
                uC.disconnect();
        }
        return new ArrayList<String>();
    }

    private List<String> getDovizOranListFromInputStream(BufferedInputStream stream){
        List<String> dovizOranList=new ArrayList<String>();
        if(stream==null)
            return dovizOranList;
        try{
            DocumentBuilderFactory dbf=DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder=dbf.newDocumentBuilder();
            Document d=docBuilder.parse(stream);
            Element fCube=(Element) d.getElementsByTagName("Cube").item(0);
            Element sCube=(Element) fCube.getElementsByTagName("Cube").item(0);
            NodeList dovizOranNL=sCube.getElementsByTagName("Cube");

            for(int i=0;i< dovizOranNL.getLength();i++){
                Element dovizOranE =(Element) dovizOranNL.item(i);
                String paraBirirmi =dovizOranE.getAttribute("currency");
                String euroyaOrani=dovizOranE.getAttribute("rate");
                dovizOranList.add(paraBirirmi + " /€ = "+euroyaOrani);
            }
        }catch (Exception e){
            Log.d("DovizTakip","XML parse edilirken hata olustu");
        }
        return dovizOranList;
    }
}
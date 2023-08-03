package com.radaee.reader;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.radaee.pdf.Page;
import com.radaee.pdf.adv.*;
import com.radaee.pdf.Document;
import com.radaee.pdf.Global;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by radaee on 2015/3/30.
 */
public class AdvanceAct extends Activity
{
    static private final String[] m_types = new String[]{"null", "boolean", "int", "real", "string", "name", "array", "dictionary", "reference", "stream"};
    static private String get_type_name(int type)
    {
        if(type >= 0 && type < m_types.length) return m_types[type];
        else return "unknown";
    }
    private void onFail(Document doc, String msg)
    {
        doc.Close();
        Toast.makeText(this, msg, Toast.LENGTH_LONG);
        finish();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Global.Init(this);
        File file = new File("/sdcard/test.pdf");
        if(!file.exists() || !file.isFile())
        {
            Toast.makeText(this, getString(R.string.file_not_exist_error, "/sdcard/test.pdf"), Toast.LENGTH_LONG);
            finish();
            return;
        }
        Document doc = new Document();
        if( doc.Open("/sdcard/test.pdf", null) != 0 )
        {
            onFail(doc, getString(R.string.file_not_opened, "/sdcard/test.pdf"));
            return;
        }
        Ref ref = doc.Advance_GetRef();
        if(ref == null)
        {
            onFail(doc, getString(R.string.edit_catalog_failed));
            return;
        }
        Obj root_obj = doc.Advance_GetObj(ref);
        if(root_obj.GetType() != 7)
        {
            onFail(doc, getString(R.string.catalog_not_found));
            return;
        }
        String text = "all tags in catalog:\n";
        int count = root_obj.DictGetItemCount();
        int cur = 0;
        for(cur = 0; cur < count; cur++)
        {
            String tag = root_obj.DictGetItemTag(cur);
            Obj item = root_obj.DictGetItem(cur);
            String type_name = get_type_name(item.GetType());
            text += tag + ":" + type_name + "\n";
        }
        text += "\nall tags in page 0:\n";
        Page page = doc.GetPage(0);
        ref = page.Advance_GetRef();
        Obj page_obj = doc.Advance_GetObj(ref);
        count = page_obj.DictGetItemCount();
        for(cur = 0; cur < count; cur++)
        {
            String tag = page_obj.DictGetItemTag(cur);
            Obj item = page_obj.DictGetItem(cur);
            String type_name = get_type_name(item.GetType());
            text += tag + ":" + type_name + "\n";
        }
        page.Close();
        //doc.Save();//save it?
        doc.Close();
        TextView tview = new TextView(this);
        tview.setText(text);
        setContentView(tview);
        /*
        int it;
        for(it = 0; it < 512; it++) {
            try {
                InputStream str = getAssets().open("eula.PDF");
                byte data[] = new byte[115557];
                str.read(data);
                Document doc1 = new Document();
                doc1.OpenMem(data, null);
                doc1.Close();
                str.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        */
    }
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
    }
}
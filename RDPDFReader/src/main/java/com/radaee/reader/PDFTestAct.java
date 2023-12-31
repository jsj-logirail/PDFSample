package com.radaee.reader;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.os.Bundle;
import android.widget.Toast;

import com.radaee.pdf.Document;
import com.radaee.pdf.Document.DocFont;
import com.radaee.pdf.Document.DocForm;
import com.radaee.pdf.Document.DocGState;
import com.radaee.pdf.Document.DocImage;
import com.radaee.pdf.Document.ImportContext;
import com.radaee.pdf.Document.Outline;
import com.radaee.pdf.Global;
import com.radaee.pdf.Matrix;
import com.radaee.pdf.Page;
import com.radaee.pdf.PageContent;
import com.radaee.pdf.Path;
import com.radaee.pdf.ResFont;
import com.radaee.pdf.ResForm;
import com.radaee.pdf.ResGState;
import com.radaee.pdf.ResImage;

import java.io.File;

public class PDFTestAct extends Activity
{
	private Document m_doc;
	private PDFSimple m_view;
	private void NewImagePage(int pageno, float w, float h, String img_path)
	{
		Page page = m_doc.NewPage(pageno, w, h);//create A4 paper
		DocImage dimg = m_doc.NewImageJPEG(img_path);
		ResImage rimg = page.AddResImage(dimg);
		PageContent content = new PageContent();
		content.Create();//create content, do not forget.
		content.GSSave();
		Matrix mat = new Matrix( w, h, 0, 0 );
		content.GSSetMatrix(mat);
		mat.Destroy();
		content.DrawImage(rimg);
		content.GSRestore();
		//add content to page
		page.AddContent(content, true);
		content.Destroy();
		page.Close();
	}
    DocForm gen_form1(DocFont dfont)
    {
        DocForm form = m_doc.NewForm();
        PageContent sform = new PageContent();
        sform.Create();
        Path path1 = new Path();
        path1.MoveTo(0, 0);
        path1.LineTo(320, 0);
        path1.LineTo(320, 100);
        path1.LineTo(0, 100);
        path1.ClosePath();
        sform.SetFillColor(0xFFFF0000);
        sform.FillPath(path1, true);

        ResFont res_font = form.AddResFont(dfont);
        sform.SetFillColor(0xFF0000FF);
        sform.TextBegin();
        sform.TextSetFont(res_font, 20);
        sform.TextMove(20, 50);
        sform.DrawText("This is form1, child of form2");
        sform.TextEnd();

        form.SetContent(sform, 0, 0, 320, 100);
        return form;
    }
    DocForm gen_form2(DocFont dfont, DocForm sub)
    {
        DocForm form = m_doc.NewForm();
        ResForm res_sub = form.AddResForm(sub);
        PageContent sform = new PageContent();
        sform.Create();
        Path path = new Path();
        path.MoveTo(0, 0);
        path.LineTo(400, 0);
        path.LineTo(400, 200);
        path.LineTo(0, 200);
        path.ClosePath();
        sform.SetFillColor(0xFFFFFF40);
        sform.FillPath(path, true);
        ResFont res_font = form.AddResFont(dfont);
        sform.SetFillColor(0xFF0000FF);
        sform.TextBegin();
        sform.TextSetFont(res_font, 20);
        sform.TextMove(20, 20);
        sform.DrawText("this is form2, parent of form1");
        sform.TextEnd();

        Matrix mat = new Matrix(1, 1, 10, 40);
        sform.GSSetMatrix(mat);
        sform.DrawForm(res_sub);

        form.SetContent(sform, 0, 0, 400, 200);
        return form;
    }
    void test_form(Page page)
    {
        DocFont dfont = m_doc.NewFontCID("DroidSansFallback", 8);
		if(dfont == null)
		{
			dfont = m_doc.NewFontCID("Roboto-Regular", 8);
			if(dfont == null)
				dfont = m_doc.NewFontCID("DroidSans", 8);
		}

        DocForm sub = gen_form1(dfont);
        DocForm form = gen_form2(dfont, sub);
        ResForm res_form = page.AddResForm(form);
        PageContent cs = new PageContent();
        cs.Create();
        Matrix mat = new Matrix(1, 1, 250, 50);
        cs.GSSave();
        cs.GSSetMatrix(mat);
        cs.DrawForm(res_form);
        cs.GSRestore();

        mat = new Matrix(1, 1, 250, 300);
        cs.GSSave();
        cs.GSSetMatrix(mat);
        cs.DrawForm(res_form);
        cs.GSRestore();

        mat = new Matrix(1, 1, 250, 550);
        cs.GSSave();
        cs.GSSetMatrix(mat);
        cs.DrawForm(res_form);
        cs.GSRestore();

        page.AddContent(cs, true);
    }

    private DocForm new_form_grid()
    {
        DocForm form = m_doc.NewForm();
        PageContent cs = new PageContent();
        cs.Create();
        cs.SetStrokeColor(0xffc6deff);
        cs.SetStrokeWidth(1);
        Path path = new Path();//just draw a rectangle, bounding form area.
        path.MoveTo(0, 0);
        path.LineTo(32, 0);
        path.LineTo(32,32);
        path.LineTo(0, 32);
        path.ClosePath();
        cs.StrokePath(path);
        path.Destroy();
        form.SetContent(cs, 0, 0, 32, 32);
        cs.Destroy();
        return form;
    }

    private void test_grid(Page page, float w, float h)
    {
        DocForm dform = new_form_grid();
        ResForm rform = page.AddResForm(dform);
        PageContent content = new PageContent();
        content.Create();//create content, do not forget.
        float y = h;
        while(y >= 0)
        {
            float x = 0;
            while(x <= w)
            {
                Matrix mat = new Matrix(10.0f/32, 10.0f/32, x, y);
                content.GSSave();
                content.GSSetMatrix(mat);
                content.DrawForm(rform);//repeat draw this form.
                content.GSRestore();
                x += 10;
            }
            y -= 10;
        }
        page.AddContent(content, true);
        content.Destroy();
    }
    private void NewTextPage(int pageno, float w, float h)
	{
		DocFont dfont = m_doc.NewFontCID("Roboto-Regular", 8);
		Page page = m_doc.NewPage(pageno, w, h);//create A4 paper
		ResFont rfont = page.AddResFont(dfont);
		PageContent content = new PageContent();
		content.Create();
		content.TextBegin();
		content.TextMove(100, 300);
		content.TextSetFont(rfont, 16);
		content.DrawText("Test");
		content.TextEnd();
		page.AddContent(content, false);
		content.Destroy();

		page.Close();
	}
	private void NewPage(int pageno, float w, float h)
	{
		Page page = m_doc.NewPage(pageno, w, h);//create A4 paper
        test_grid(page, w, h);
        test_form(page);

        PageContent content = new PageContent();
		content.Create();//create content, do not forget.

		//set alpha for both fill and stroke
		DocGState dgs = m_doc.NewGState();
		dgs.SetFillAlpha(0x80);//set alpha value to 0.5
		dgs.SetStrokeAlpha(0x80);//set alpha value to 0.5
		ResGState rgs = page.AddResGState(dgs);
		content.GSSet(rgs);

		//set matrix for filling
		content.GSSave();
		Matrix mat = new Matrix( 1, 1, 40, 100 );
		content.GSSetMatrix(mat);
		mat.Destroy();

		//build a path object
		Path path = new Path();
		path.MoveTo(10, 10);
		path.LineTo(20, 12);
		path.CurveTo(30, 20, 20, 30, -10, 50);
		path.ClosePath();

		//fill it
		content.SetFillColor(0xFF0000);//set red color;
		content.FillPath(path, true);//using winding fill rule
		content.GSRestore();

		
		//set matrix for stroke
		content.GSSave();
		mat = new Matrix( 1, 1, 40, 200 );
		content.GSSetMatrix(mat);
		mat.Destroy();
		
		//stroke it
		content.SetStrokeColor(0xFF);//set blue color;
		content.SetStrokeWidth(4);//set stroke width
		content.StrokePath(path);
		content.GSRestore();

		//destroy the path
		path.Destroy();

		
		//prepare to write texts
		content.GSSave();
		mat = new Matrix( 1, 1, 80, 200 );
		content.GSSetMatrix(mat);
		mat.Destroy();

		DocFont dfont = m_doc.NewFontCID("DroidSansFallback", 3 | 8);//bold-italic and embed in horizontal writing
        if(dfont == null)
        {
            dfont = m_doc.NewFontCID("Roboto-Regular", 3 | 8);
            if(dfont == null)
                dfont = m_doc.NewFontCID("DroidSans", 3 | 8);
        }
		ResFont rfont = page.AddResFont(dfont);

		content.TextBegin();
		content.TextSetFont(rfont, 16);//set font and size
		content.SetFillColor(0x8000);//set fill color to black-green.
		content.SetStrokeColor(0x80);//set stroke color to black-red.
		content.TextSetCharSpace(0);
		content.TextSetWordSpace(0.2f);
		content.TextSetLeading(16);
		content.TextSetRenderMode(2);//fill and stroke
		content.TextSetHScale(120);//set horizontal scale
		content.DrawText("Hello word!\rNice to meet you!");
		content.TextEnd();
		content.GSRestore();

		//write a bitmap
		content.GSSave();
		dgs = m_doc.NewGState();
		dgs.SetFillAlpha(255);//set alpha value to 1
		dgs.SetStrokeAlpha(255);//set alpha value to 1
		dgs.SetBlendMode(3);
		rgs = page.AddResGState(dgs);
		content.GSSet(rgs);
		Bitmap bmp = Bitmap.createBitmap(100, 100, Config.ARGB_8888);//must be ARGB_8888
		bmp.eraseColor(0xc0000080);
		DocImage dimg = m_doc.NewImage(bmp, 0);
		bmp.recycle();
		ResImage rimg = page.AddResImage(dimg);
		mat = new Matrix( 80, 80, 80, 400 );
		content.GSSetMatrix(mat);
		mat.Destroy();
		content.DrawImage(rimg);
		content.GSRestore();


		//add content to page
		page.AddContent(content, true);
		content.Destroy();

		page.Close();
	}
	private void concat_pdf( String dst, String src )
	{
		Document doc_dst = new Document();
		Document doc_src = new Document();
		doc_dst.Open(dst, null);
		doc_dst.SetCache(Global.tmp_path + "/ttt.dat");
		doc_src.Open(src, null);
		ImportContext ctx = doc_dst.ImportStart(doc_src);
		int dstno = doc_dst.GetPageCount();
		int srccnt = doc_src.GetPageCount();
		int srcno = 0;
		while( srcno < srccnt )
		{
			ctx.ImportPage(srcno, dstno);
			dstno++;
			srcno++;
		}
		if(ctx != null) ctx.Destroy();
		doc_src.Close();
		doc_dst.Save();
		doc_dst.Close();
	}

	@Override
    public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
        //plz set this line to Activity in AndroidManifes.xml:
        //    android:configChanges="orientation|keyboardHidden|screenSize"
        //otherwise, APP shall destroy this Activity and re-create a new Activity when rotate. 
        Global.Init( this );
        //concat_pdf( "/sdcard/testImport.pdf", "/sdcard/test_images.pdf" );
        
		String file = "/sdcard/test.pdf";

		m_doc = new Document();
		m_doc.Create( file );

		String file_cache = Global.tmp_path + "/temp.dat";
		m_doc.SetCache( file_cache );//set temporary cache for editing.
		NewPage(0, (210 * 72)/25.4f, (297 * 72)/25.4f);
		NewTextPage(1, 600, 600);
		//NewImagePage(1, 323, 239, "/sdcard/0001.jpg");
		//NewImagePage(2, 323, 239, "/sdcard/0002.jpg");

		//to new outline item for first page
		float top = m_doc.GetPageHeight(0);
		boolean ret = m_doc.NewRootOutline("Root", 0, top);
		if(ret)
		{
			Outline ol = m_doc.GetOutlines();
			ol.AddChild("Child1", 0, top * 5 / 6);
			Outline ol1 = ol.GetChild();
			ol1.AddNext("Child2", 0, top / 2);
		}

		m_doc.Save();
        byte[] id = "123456789abcdefghijklmnopqrstuvw".getBytes();
        m_doc.EncryptAs("/sdcard/test_enc.pdf", "user", "owner", 3, 3, id);

		m_doc.Save();
		m_doc.Close();
		m_doc = null;
		m_view = new PDFSimple(this);
		m_view.Open(file);
		setContentView( m_view );
	}
	@Override
    protected void onDestroy()
	{
    	if( m_view != null )
    		m_view.Close();
    	Global.RemoveTmp();
		super.onDestroy();
	}
}

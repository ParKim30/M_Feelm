package com.example.m_feelm;

import android.Manifest;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.provider.BaseColumns;
import android.provider.DocumentsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfDocument;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.codec.Base64;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.itextpdf.text.pdf.draw.VerticalPositionMark;
import com.itextpdf.tool.xml.XMLWorker;
import com.itextpdf.tool.xml.XMLWorkerFontProvider;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import com.itextpdf.tool.xml.css.CssFile;
import com.itextpdf.tool.xml.css.StyleAttrCSSResolver;
import com.itextpdf.tool.xml.html.CssAppliers;
import com.itextpdf.tool.xml.html.CssAppliersImpl;
import com.itextpdf.tool.xml.html.Tags;
import com.itextpdf.tool.xml.parser.XMLParser;
import com.itextpdf.tool.xml.pipeline.css.CSSResolver;
import com.itextpdf.tool.xml.pipeline.css.CssResolverPipeline;
import com.itextpdf.tool.xml.pipeline.end.PdfWriterPipeline;
import com.itextpdf.tool.xml.pipeline.html.HtmlPipeline;
import com.itextpdf.tool.xml.pipeline.html.HtmlPipelineContext;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class FragmentPrint extends AppCompatActivity {

    Button btn_create_pdf;

    protected void onCreate(Bundle savedInstanceState) {
        {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.fragment_print);

            btn_create_pdf = (Button) findViewById(R.id.btn_create_btn);

            com.gun0912.tedpermission.PermissionListener permissionListener=new PermissionListener(){

                @Override
                public void onPermissionGranted() {
                    Log.d("ahoh", "yap");
                    btn_create_pdf.setOnClickListener(new View.OnClickListener() {

                        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                        @Override
                        public void onClick(View v) {
                            Log.d("ahoh", "why1");
                            createPDFFile(Common.getAppPath(FragmentPrint.this) + "test_pdf.pdf");
                            Log.d("ahoh", "why2");
                        }
                    });
                }

                @Override
                public void onPermissionDenied(ArrayList<String> deniedPermissions) {

                }
            };
            TedPermission.with(this)
                    .setPermissionListener(permissionListener)
                    .setRationaleMessage("구글 로그인을 하기 위해서는 주소록 접근 권한이 필요해요")
                    .setDeniedMessage("왜 거부하셨어요...\n하지만 [설정] > [권한] 에서 권한을 허용할 수 있어요.")
                    .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .check();
        }


    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void createPDFFile(String path) {
        Log.d("ahoh", "why!!");
        if (new File(path).exists())
            new File(path).delete();
        try {
//            Document document = new Document();
//            PdfDocument document1=new PdfDocument();
//            PdfWriter.getInstance(document, new FileOutputStream(path));
//
//            document.open();
//
//            document.setPageSize(PageSize.A4);
//            document.addCreationDate();
//            document.addAuthor("EDNTDev");
//            document.addCreator("Eddy Lee");
//
//            BaseColor colorAccent = new BaseColor(0, 153, 204, 155);
//
//            float fontSize = 20.0f;
//            float valueFontSize = 26.0f;
//
//            BaseFont fontName = BaseFont.createFont("assets/fonts/brandon_medium.otf", "UTF-8", BaseFont.EMBEDDED);
//
//            Font titleFont = new Font(fontName, 36.0f, Font.NORMAL, BaseColor.BLACK);
//            addNewItem(document, "Order Details", Element.ALIGN_CENTER, titleFont);
//
//            Font orderNumberFont = new Font(fontName, fontSize, Font.NORMAL, colorAccent);
//            addNewItem(document, "Order No", Element.ALIGN_LEFT, orderNumberFont);
//
//            Font orderNumberValueFont = new Font(fontName, fontSize, Font.NORMAL, colorAccent);
//            addNewItem(document, "#717171", Element.ALIGN_LEFT, orderNumberFont);
//
//            addLineSeparator(document);
//
//            addNewItem(document, "Oreder Date", Element.ALIGN_LEFT, orderNumberFont);
//            addNewItem(document, "3/8/2019", Element.ALIGN_LEFT, orderNumberValueFont);
//
//            addLineSeparator(document);
//
//            addNewItem(document, "Account Name", Element.ALIGN_LEFT, orderNumberFont);
//            addNewItem(document, "Endy Lee", Element.ALIGN_LEFT, orderNumberValueFont);
//
//            addLineSeparator(document);
//
//            addLineSpace(document);
//            addNewItem(document, "Product Detail", Element.ALIGN_CENTER, titleFont);
//
//            addLineSeparator(document);
//
//            addNewItemWithLeftAndRight(document, "Pizza 25", "(0.0%)", titleFont, orderNumberValueFont);
//            addNewItemWithLeftAndRight(document, "12.0*1000", "12000.0", titleFont, orderNumberValueFont);
//
//            addLineSeparator(document);
//
//            addNewItemWithLeftAndRight(document, "Pizza 25", "(0.0%)", titleFont, orderNumberValueFont);
//            addNewItemWithLeftAndRight(document, "12.0*1000", "12000.0", titleFont, orderNumberValueFont);
//
//            addLineSeparator(document);
//
//            addLineSpace(document);
//            addLineSpace(document);
//
//            addNewItemWithLeftAndRight(document, "Total", "24000.0", titleFont, orderNumberValueFont);
//
//            Image image = Image.getInstance ("feelm.png");
//            document.add(image);
//            document.close();
//            Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();
            Document document = new Document();
            PdfWriter writer = PdfWriter.getInstance(document,  new FileOutputStream(path));
            ((PdfWriter) writer).setInitialLeading(12.5f);

// Document 오픈
            document.open();
            XMLWorkerHelper helper = XMLWorkerHelper.getInstance();

// CSS
            CSSResolver cssResolver = new StyleAttrCSSResolver();
            CssFile cssFile = helper.getCSS(new FileInputStream("D:/pdf.css"));
            cssResolver.addCss(cssFile);
// HTML, 폰트 설정
            XMLWorkerFontProvider fontProvider = new XMLWorkerFontProvider(XMLWorkerFontProvider.DONTLOOKFORFONTS);
//            fontProvider.register("C:/eGovFrame/workspace/projectName/src/main/webapp/font/MALGUN.TTF", "MalgunGothic"); // MalgunGothic은 alias,
            CssAppliers cssAppliers = new CssAppliersImpl(fontProvider);

            HtmlPipelineContext htmlContext = new HtmlPipelineContext(cssAppliers);
            htmlContext.setTagFactory(Tags.getHtmlTagProcessorFactory());

// Pipelines
            PdfWriterPipeline pdf = new PdfWriterPipeline(document, writer);
            HtmlPipeline html = new HtmlPipeline(htmlContext, pdf);
            CssResolverPipeline css = new CssResolverPipeline(cssResolver, html);

            XMLWorker worker = new XMLWorker(css, true);
            XMLParser xmlParser = new XMLParser(worker, Charset.forName("UTF-8"));

// 폰트 설정에서 별칭으로 줬던 "MalgunGothic"을 html 안에 폰트로 지정한다.
            String htmlStr = "<html><head><body style='font-family: MalgunGothic;'>"
                    + "<p>PDF 안에 들어갈 내용입니다.</p>"
                    + "<h3>한글, English, 漢字.</h3>"
                    + "</body></head></html>";

            StringReader strReader = new StringReader(htmlStr);
            xmlParser.parse(strReader);

            document.close();
            printPDF();
            writer.close();



            //17:57
        } catch (FileNotFoundException e) {
            Log.d("이런","1");
            e.printStackTrace();
        } catch (IOException e) {
            Log.d("이런","2");
            e.printStackTrace();
        } catch (DocumentException e) {
            Log.d("이런","3");
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void printPDF() {
        PrintManager printManager = (PrintManager) getSystemService(Context.PRINT_SERVICE);
        try {
            Log.d("omg","1");
            PrintDocumentAdapter printDocumentAdapter = new PdfDocumentAdapter(FragmentPrint.this, Common.getAppPath(FragmentPrint.this) + "test_pdf.pdf");
            printManager.print("Document", printDocumentAdapter, new PrintAttributes.Builder().build());
        } catch (Exception ex) {
            Log.d("이런","4");
            Log.e("EDMTDev", "" + ex.getMessage());
        }
    }

    private void addNewItemWithLeftAndRight(Document document, String textLeft, String textRight, Font textLeftFont, Font textRightFont) throws DocumentException {
        Chunk chunkTextLeft = new Chunk(textLeft, textLeftFont);
        Chunk chunckTextRight = new Chunk(textRight, textRightFont);

        Paragraph p = new Paragraph((chunkTextLeft));
        p.add(new Chunk(new VerticalPositionMark()));
        p.add(chunckTextRight);
        document.add(p);
    }


    private void addLineSeparator(Document document) throws DocumentException {
        LineSeparator lineSeparator = new LineSeparator();
        lineSeparator.setLineColor(new BaseColor(0, 0, 0, 68));
        addLineSpace(document);
        document.add(new Chunk(lineSeparator));
        addLineSpace(document);
    }

    private void addLineSpace(Document document) throws DocumentException {
        document.add(new Paragraph(""));
    }

    private void addNewItem(Document document, String text, int align, Font font) throws DocumentException {
        Chunk chunk = new Chunk(text, font);
        Paragraph paragraph = new Paragraph(chunk);
        paragraph.setAlignment(align);
        document.add(paragraph);
    }
}


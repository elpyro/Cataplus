package com.cataplus.facturacionycatalogospdf;

//https://medium.com/@raveesh08/creating-pdf-table-using-itextpdf-in-android-eb7e00163ab0


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.util.Log;

import androidx.annotation.NonNull;

import com.cataplus.facturacionycatalogospdf.Utils.Fragmento_reporte_compra_venta;
import com.cataplus.facturacionycatalogospdf.modelos.InformeComprayventaModelo;
import com.cataplus.facturacionycatalogospdf.ui.factura_mayor.fragmentos.Fragmento_factura_mayor_detalle;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.Barcode128;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

//CATALOGO LLAMADO DEL MAIN ACTIVITY
final class PDFUtility_compraYventa
{
    private static final String TAG = PDFUtility_compraYventa.class.getSimpleName();
    private static Font FONT_TITLE     = new Font(Font.FontFamily.TIMES_ROMAN, 20, Font.BOLD);
    private static Font FONT_SUBTITLE      = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);

    private static Font FONT_CELL      = new Font(Font.FontFamily.TIMES_ROMAN,  12, Font.NORMAL);
    private static Font FONT_COLUMN    = new Font(Font.FontFamily.TIMES_ROMAN,  14, Font.NORMAL);

    private static String codigo_empresa, nombre,documento,correo,dominio,telefono1,telefono2,pais,ciudad,direccion,logo;


    public interface OnDocumentClose
    {
        void onPDFDocumentClose(File file);
    }

    static void createPdf(@NonNull Context mContext, OnDocumentClose mCallback, List<InformeComprayventaModelo> temp2, @NonNull String filePath, boolean isPortrait) throws Exception
    {

        if(filePath.equals(""))
        {
            throw new NullPointerException("PDF File Name can't be null or blank. PDF File can't be created");
        }

        File file = new File(filePath);

        if(file.exists())
        {
            file.delete();
            Thread.sleep(50);
        }

        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(mContext.getApplicationContext(), "basedatos",null,1);
        SQLiteDatabase BD= admin.getWritableDatabase();
        Cursor cursor=BD.rawQuery("SELECT * FROM empresa",null);


        codigo_empresa=Preferencias_app.datos_empresa.get(0).getId_empresa();
        nombre=Preferencias_app.datos_empresa.get(0).getNombre();
        documento=Preferencias_app.datos_empresa.get(0).getDocumento();
        correo=Preferencias_app.datos_empresa.get(0).getCorreo();
        dominio=Preferencias_app.datos_empresa.get(0).getDominio();
        telefono1=Preferencias_app.datos_empresa.get(0).getTelefono1();
        telefono2=Preferencias_app.datos_empresa.get(0).getTelefono2();
        pais=Preferencias_app.datos_empresa.get(0).getPais();
        ciudad=Preferencias_app.datos_empresa.get(0).getCiudad();
        direccion=Preferencias_app.datos_empresa.get(0).getDireccion();
        logo=Preferencias_app.datos_empresa.get(0).getLogo();

        Document document = new Document();
        document.setMargins(24f,24f,32f,32f);
        document.setPageSize(isPortrait? PageSize.A4:PageSize.A4.rotate());

        PdfWriter pdfWriter = PdfWriter.getInstance(document, new FileOutputStream(filePath));
        pdfWriter.setFullCompression();
        pdfWriter.setPageEvent(new PageNumeration());

        document.open();

        setMetaData(document);

        //addHeader(mContext,document);
        //AÑADIR CABECERA

        PdfPTable table = new PdfPTable(3);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{4,7,4});
        table.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
        table.getDefaultCell().setVerticalAlignment(Element.ALIGN_CENTER);
        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);

        PdfPCell cell;
        {

            /*LEFT TOP text*/


            String date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());

            PdfPTable logoTable=new PdfPTable(1);
            logoTable.setWidthPercentage(100);
            logoTable.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
            logoTable.setHorizontalAlignment(Element.ALIGN_LEFT);
            logoTable.getDefaultCell().setVerticalAlignment(Element.ALIGN_LEFT);


            PdfPCell logoCell = new PdfPCell(new Phrase(nombre+"\n"+mContext.getResources().getString(R.string.Documento)+": "+documento+"\n"+mContext.getResources().getString(R.string.Telefono1)+": "+telefono1+"\n"+telefono2,FONT_SUBTITLE));
            logoCell.setHorizontalAlignment(Element.ALIGN_LEFT);
            logoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            logoCell.setBorder(PdfPCell.NO_BORDER);
            logoCell.setPadding(6f);
            logoTable.addCell(logoCell);

            cell = new PdfPCell(logoTable);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setUseAscender(true);
            cell.setBorder(PdfPCell.NO_BORDER);
            cell.setPadding(6f);
            table.addCell(cell);

        }

        {
            /*MIDDLE TEXT*/
            cell = new PdfPCell();
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(PdfPCell.NO_BORDER);
            cell.setPadding(8f);
            cell.setUseAscender(true);
            String fecha_desde="";
            String fecha_hasta="";
            if (Fragmento_reporte_compra_venta.fecha_desde_busqueda!=null) {
                fecha_desde= mContext.getResources().getString(R.string.Desde)+": "+Fragmento_reporte_compra_venta.fecha_desde_busqueda ;
            }else{
                fecha_desde="";
            }
            if(Fragmento_reporte_compra_venta.fecha_hasta!=null){
                fecha_hasta=mContext.getResources().getString(R.string.Hasta)+": "+Fragmento_reporte_compra_venta.fecha_hasta;
            }

            String titulo="";
            if (Fragmento_reporte_compra_venta.tipo_busqueda.equals("Mayor")||Fragmento_reporte_compra_venta.tipo_busqueda.equals("Wholesale")) {
                titulo=mContext.getResources().getString(R.string.Reporte_ventas_mayoristas);
            }else if (Fragmento_reporte_compra_venta.tipo_busqueda.equals("Detal")||Fragmento_reporte_compra_venta.tipo_busqueda.equals("Retail")){
                titulo=mContext.getResources().getString(R.string.Reporte_ventas_detal);
            }else if(Fragmento_reporte_compra_venta.tipo_busqueda.equals("Compra")||Fragmento_reporte_compra_venta.tipo_busqueda.equals("Buy")){
                titulo=mContext.getResources().getString(R.string.Reporte_compra);
            }

            Paragraph temp = new Paragraph(titulo ,FONT_TITLE);
            temp.setAlignment(Element.ALIGN_CENTER);
            cell.addElement(temp);

            temp = new Paragraph(fecha_desde+fecha_hasta ,FONT_SUBTITLE);
            temp.setAlignment(Element.ALIGN_CENTER);
            cell.addElement(temp);

            table.addCell(cell);
        }
        /* RIGHT TOP LOGO*/
        {

            //COLOCAR FOTO POR DEFECTO FOTO VACIA EN BD
            try {


                cell = new PdfPCell(getImage(Preferencias_app.datos_empresa.get(0).getImagen(),false));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setUseAscender(true);
                cell.setBorder(PdfPCell.NO_BORDER);
                cell.setPadding(2f);
                table.addCell(cell);

            }catch (Exception e){
                cell = new PdfPCell(new Phrase(mContext.getResources().getString(R.string.Logo), FONT_CELL));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setUseAscender(true);
                cell.setBorder(PdfPCell.NO_BORDER);
                cell.setPadding(2f);
                table.addCell(cell);

            }

        }

//        Paragraph paragraph=new Paragraph("",new Font(Font.FontFamily.TIMES_ROMAN, 2.0f, Font.NORMAL, BaseColor.BLACK));
//        paragraph.add(table);
//        document.add(paragraph);

        document.add(table);
        addEmptyLine(document,1);

        PdfPTable tabla_costos = new PdfPTable(1);
        tabla_costos.setWidthPercentage(100);
        tabla_costos.setWidths(new float[]{9F});
        tabla_costos.getDefaultCell().setBorder(PdfPCell.NO_BORDER);

        cell = new PdfPCell(new Paragraph(Fragmento_reporte_compra_venta.tipo_busqueda+": "+Principal.format.format(Fragmento_reporte_compra_venta.total),FONT_TITLE));
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setBorder(PdfPCell.NO_BORDER);
        tabla_costos.addCell(cell);

        document.add(tabla_costos);


        addEmptyLine(document, 2);

        //ELEMENTOS A LAS TABLAS
        PdfPTable table1 = new PdfPTable(6);
        table1.setWidthPercentage(100);
        table1.setWidths(new float[]{1f,0.5f,2f,0.5f,1F,1F});
        table1.setHeaderRows(1);
        table1.getDefaultCell().setVerticalAlignment(Element.ALIGN_CENTER);
        table1.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);


       // PdfPCell cell;
        {
            cell = new PdfPCell(new Phrase(mContext.getResources().getString(R.string.Fecha), FONT_COLUMN));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setPadding(1f);
            table1.addCell(cell);

            cell = new PdfPCell(new Phrase(mContext.getResources().getString(R.string.Factura_), FONT_COLUMN));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setPadding(1f);
            table1.addCell(cell);

            cell = new PdfPCell(new Phrase(mContext.getResources().getString(R.string.nombre), FONT_COLUMN));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setPadding(3f);
            table1.addCell(cell);

            cell = new PdfPCell(new Phrase(mContext.getResources().getString(R.string.Cant), FONT_COLUMN));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setPadding(0.5f);
            table1.addCell(cell);


            cell = new PdfPCell(new Phrase(mContext.getResources().getString(R.string.Precio), FONT_COLUMN));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setPadding(1f);
            table1.addCell(cell);

            cell = new PdfPCell(new Phrase(mContext.getResources().getString(R.string.SubTotal), FONT_COLUMN));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setPadding(0.5f);
            table1.addCell(cell);
        }
//
        float top_bottom_Padding = 8f;
        float left_right_Padding = 4f;
        boolean alternate = false;

        BaseColor lt_gray = new BaseColor(221,221,221); //#DDDDDD
        BaseColor cell_color;

        int size = temp2.size();
//
        for (int i = 0; i < size; i++)
        {
            cell_color = alternate ? lt_gray : BaseColor.WHITE;
//            String[] temp = items.get(i);
//            byte[] imagen_temp = imagen_byte.get(i);


                cell = new PdfPCell(new Phrase(temp2.get(i).getFecha(), FONT_CELL));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setPaddingLeft(left_right_Padding);
                cell.setPaddingRight(left_right_Padding);
                cell.setPaddingTop(top_bottom_Padding);
                cell.setPaddingBottom(top_bottom_Padding);
                cell.setBackgroundColor(cell_color);
                table1.addCell(cell);

            cell = new PdfPCell(new Phrase(temp2.get(i).getId_factura_bd(), FONT_CELL));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setPaddingLeft(left_right_Padding);
            cell.setPaddingRight(left_right_Padding);
            cell.setPaddingTop(top_bottom_Padding);
            cell.setPaddingBottom(top_bottom_Padding);
            cell.setBackgroundColor(cell_color);
            table1.addCell(cell);


            cell = new PdfPCell(new Phrase(  temp2.get(i).getNombre_producto(), FONT_CELL));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setPaddingLeft(left_right_Padding);
            cell.setPaddingRight(left_right_Padding);
            cell.setPaddingTop(top_bottom_Padding);
            cell.setPaddingBottom(top_bottom_Padding);
            cell.setBackgroundColor(cell_color);
            table1.addCell(cell);


            cell = new PdfPCell(new Phrase(temp2.get(i).getCantidad(), FONT_CELL));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setPaddingLeft(left_right_Padding);
            cell.setPaddingRight(left_right_Padding);
            cell.setPaddingTop(top_bottom_Padding);
            cell.setPaddingBottom(top_bottom_Padding);
            cell.setBackgroundColor(cell_color);
            table1.addCell(cell);

            double descuento=(Double.valueOf(temp2.get(i).getDescuento())/100);
            double precio= Double.valueOf(temp2.get(i).getP_costo())-(Double.valueOf(temp2.get(i).getP_costo())* descuento) ;

            cell = new PdfPCell(new Phrase(Principal.format.format(precio), FONT_CELL));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setPaddingLeft(left_right_Padding);
            cell.setPaddingRight(left_right_Padding);
            cell.setPaddingTop(top_bottom_Padding);
            cell.setPaddingBottom(top_bottom_Padding);
            cell.setBackgroundColor(cell_color);
            table1.addCell(cell);

            Double subTotal= Double.valueOf(temp2.get(i).getCantidad())*precio;
            cell = new PdfPCell(new Phrase(Principal.format.format(subTotal)+"", FONT_CELL));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setPaddingLeft(left_right_Padding);
            cell.setPaddingRight(left_right_Padding);
            cell.setPaddingTop(top_bottom_Padding);
            cell.setPaddingBottom(top_bottom_Padding);
            cell.setBackgroundColor(cell_color);
            table1.addCell(cell);

            alternate = !alternate;
        }

        temp2.clear();
        temp2=null;



        document.add(table1);

        addEmptyLine(document, 1);

        tabla_costos = new PdfPTable(1);
        tabla_costos.setWidthPercentage(100);
        tabla_costos.setWidths(new float[]{9F});
        tabla_costos.getDefaultCell().setBorder(PdfPCell.NO_BORDER);

        cell = new PdfPCell(new Paragraph(Fragmento_reporte_compra_venta.tipo_busqueda+": "+Principal.format.format(Fragmento_reporte_compra_venta.total),FONT_TITLE));
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setBorder(PdfPCell.NO_BORDER);
        tabla_costos.addCell(cell);

        document.add(tabla_costos);

        document.close();

        Fragmento_reporte_compra_venta.total=0.00;
        try
        {
            pdfWriter.close();
        }
        catch (Exception ex)
        {
            Log.e(TAG,"Error While Closing pdfWriter : "+ex.toString());
        }

        if(mCallback!=null)
        {
            mCallback.onPDFDocumentClose(file);
        }
    }

    private static  void addEmptyLine(Document document, int number) throws DocumentException
    {
        for (int i = 0; i < number; i++)
        {
            document.add(new Paragraph(" "));
        }
    }

    private static void setMetaData(Document document)
    {
        document.addCreationDate();
        //document.add(new Meta("",""));
        document.addAuthor( nombre);
        document.addCreator(nombre);
        document.addHeader("","");
    }








    private static Image getImage(byte[] imageByte, boolean isTintingRequired) throws Exception
    {

        Paint paint=new Paint();
        if(isTintingRequired)
        {
            paint.setColorFilter(new PorterDuffColorFilter(Color.BLUE, PorterDuff.Mode.SRC_IN));
        }
        Bitmap input  = BitmapFactory.decodeByteArray(imageByte, 0,imageByte.length);
        Bitmap output = Bitmap.createBitmap(input.getWidth(),input.getHeight(),Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        canvas.drawBitmap(input,0,0,paint);

        ByteArrayOutputStream stream=new ByteArrayOutputStream();
        output.compress(Bitmap.CompressFormat.JPEG,30,stream);
        Image image=Image.getInstance(stream.toByteArray());
        image.setWidthPercentage(80);
        image.scaleToFit(165,75);
        return image;
    }

    private static Image getimagen_grande(byte[] imageByte, boolean isTintingRequired) throws Exception
    {

        Paint paint=new Paint();
        if(isTintingRequired)
        {
            paint.setColorFilter(new PorterDuffColorFilter(Color.BLUE, PorterDuff.Mode.SRC_IN));
        }
        Bitmap input  = BitmapFactory.decodeByteArray(imageByte, 0,imageByte.length);
        Bitmap output = Bitmap.createBitmap(input.getWidth(),input.getHeight(),Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        canvas.drawBitmap(input,0,0,paint);

        ByteArrayOutputStream stream=new ByteArrayOutputStream();
        output.compress(Bitmap.CompressFormat.JPEG,30,stream);
        Image image=Image.getInstance(stream.toByteArray());
        image.setWidthPercentage(380);
        image.scaleToFit(565,275);
        return image;
    }

}
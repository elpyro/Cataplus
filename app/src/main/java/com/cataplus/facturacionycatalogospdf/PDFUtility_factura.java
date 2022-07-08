package com.cataplus.facturacionycatalogospdf;


//https://medium.com/@raveesh08/creating-pdf-table-using-itextpdf-in-android-eb7e00163ab0


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.util.Log;

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
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;

final class PDFUtility_factura
{
    private static final String TAG = PDFUtility_factura.class.getSimpleName();
    private static Font FONT_TITLE     = new Font(Font.FontFamily.TIMES_ROMAN, 20, Font.BOLD);
    private static Font FONT_SUBTITLE      = new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD);
    private static Font FONT_SUBTITLE2      = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);

    private static Font FONT_CELL      = new Font(Font.FontFamily.TIMES_ROMAN,  12, Font.NORMAL);
    private static Font FONT_COLUMN    = new Font(Font.FontFamily.TIMES_ROMAN,  14, Font.NORMAL);

    private static String codigo_empresa, nombre,documento,correo,dominio,telefono1,telefono2,pais,ciudad,direccion,logo;


    public interface OnDocumentClose
    {
        void onPDFDocumentClose(File file);
    }

    static void createPdf(@NonNull Context mContext, PDFUtility_factura.OnDocumentClose mCallback, List<byte[]> imagen_byte,List<String[]> items,List<String[]> cliente,List<String[]> cabezera, @NonNull String filePath, boolean isPortrait) throws Exception
    {
        String[] temp_cliente = cliente.get(0);
        String[] temp_cabezera = cabezera.get(0);
        DecimalFormat format = new DecimalFormat();
        format.setMaximumFractionDigits(2); //Define 2 decimales.

        //VERIFICA SI EN EL PDF VA A APARECER IMAGENES Y CODIGO BARRAS
        String celda_nueva1="";
        String celda_nueva2="";
        int columnas=0;
        if(Fragment_factura_pdf.checkBoximagenes.isChecked()&&Fragment_factura_pdf.checkBox_barras.isChecked()){
          columnas=7;
          celda_nueva1= mContext.getResources().getString(R.string.Imagen);
          celda_nueva2= mContext.getResources().getString(R.string.Barras);
        }else if (Fragment_factura_pdf.checkBoximagenes.isChecked()){
          columnas=6;
          celda_nueva1=mContext.getResources().getString(R.string.Imagen);
        }else if(Fragment_factura_pdf.checkBox_barras.isChecked()){
           columnas=6;
           celda_nueva1=mContext.getResources().getString(R.string.Barras);
        }else{
           columnas=5;
        }

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
        //AÑADIR DATOS EMPRESA

        PdfPTable table = new PdfPTable(3);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{4,7,4});
        table.getDefaultCell().setBorder(PdfPCell.NO_BORDER);


        PdfPCell cell;
        {

            /*LEFT TOP text*/


            String date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());

            PdfPTable logoTable=new PdfPTable(1);
            logoTable.setWidthPercentage(100);
            logoTable.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
            logoTable.setHorizontalAlignment(Element.ALIGN_LEFT);
            logoTable.getDefaultCell().setVerticalAlignment(Element.ALIGN_LEFT);


           // PdfPCell logoCell = new PdfPCell(logo);
            PdfPCell logoCell = new PdfPCell(new Phrase("Fecha: "+temp_cliente[3]+"\n"+"Factura: "+temp_cliente[0]));
            logoCell = new PdfPCell(new Phrase(mContext.getResources().getString(R.string.Fecha)+": "+temp_cliente[3]+"\n"+mContext.getResources().getString(R.string.Factura_)+": "+temp_cliente[0],FONT_SUBTITLE));
            logoCell.setHorizontalAlignment(Element.ALIGN_LEFT);
            logoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            logoCell.setBorder(PdfPCell.NO_BORDER);
            logoCell.setPadding(6f);
            logoTable.addCell(logoCell);



            logoCell = new PdfPCell(new Phrase(nombre+"\n"+""+documento+"\n"+""+telefono1+"   "+telefono2+"\n",FONT_SUBTITLE2));
            logoCell.setHorizontalAlignment(Element.ALIGN_LEFT);
            logoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            logoCell.setBorder(PdfPCell.NO_BORDER);
            logoCell.setPadding(6f);
            logoTable.addCell(logoCell);

            cell = new PdfPCell(logoTable);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
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

            Paragraph temp = new Paragraph(mContext.getResources().getString(R.string.Factura_),FONT_TITLE);
            temp.setAlignment(Element.ALIGN_CENTER);
            cell.addElement(temp);

            temp = new Paragraph(temp_cliente[2] ,FONT_SUBTITLE);
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

        document.add(table);

        //AGREGAR PREFERENCIA INFORMACION SUPERIOR
        if (Fragment_factura_pdf.checkBox_informacion_superior.isChecked()){
            document.add(new Paragraph(Fragment_factura_pdf.informacion_superior.getText().toString().trim()));
        }
        addEmptyLine(document,1);
        if (temp_cliente[2].equals("Compra")||temp_cliente[2].equals("Buy")) {

            document.add(new Paragraph( temp_cliente[8] ));
        }else{
            //AGReGAR INFORMACION DEL CLIENTE
            document.add(new Paragraph(mContext.getResources().getString(R.string.Cliente)+": " + temp_cliente[8] + "   " + "CI: " + temp_cliente[9] + "\n" + mContext.getResources().getString(R.string.Telefono1)+": " + temp_cliente[12] + "  " + temp_cliente[11] + "  " + temp_cliente[10] + "\n" + mContext.getResources().getString(R.string.Direccion) + temp_cliente[13] + " " + temp_cliente[14] + " " + temp_cliente[15]));
        }
        addEmptyLine(document, 1);

        //ELEMENTOS A LAS TABLAS (ENCABEZADO)
        PdfPTable table1 = new PdfPTable(columnas);
        table1.setWidthPercentage(100);

        //EL TAMAÑO DE LAS TABLAS SEGUN LA CANTIDAD DE ELEMENTOS EN LA LISTA
        if (columnas==6){
            table1.setWidths(new float[]{0.5f,2.3f,0.5f,1f,1F,1.7F});
        }else if(columnas==7){
            table1.setWidths(new float[]{0.5f,2f,0.5f,1F,1F,1.3f,1.7f});
        }else{
            table1.setWidths(new float[]{0.5f,2f,0.5f,1F,1F});
        }

        table1.setHeaderRows(1);
        table1.getDefaultCell().setVerticalAlignment(Element.ALIGN_CENTER);
        table1.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);

        // PdfPCell cell;
        {

            cell = new PdfPCell(new Phrase("ID", FONT_SUBTITLE));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setPadding(3f);
            table1.addCell(cell);

            cell = new PdfPCell(new Phrase(temp_cabezera[1], FONT_SUBTITLE));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setPadding(0.5f);
            table1.addCell(cell);



            cell = new PdfPCell(new Phrase(temp_cabezera[5], FONT_SUBTITLE));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setPadding(1f);
            table1.addCell(cell);

            cell = new PdfPCell(new Phrase(temp_cabezera[2], FONT_SUBTITLE));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setPadding(1f);
            table1.addCell(cell);

            cell = new PdfPCell(new Phrase(temp_cabezera[6], FONT_SUBTITLE));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setPadding(0.5f);
            table1.addCell(cell);


            //AGRAGAR CABEZERA SI HAY MAS DE 5 COLUMNAS
            if (columnas==6){
                cell = new PdfPCell(new Phrase(celda_nueva1, FONT_SUBTITLE));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setPadding(1f);
                table1.addCell(cell);
            }

            if (columnas==7){
                cell = new PdfPCell(new Phrase(celda_nueva1, FONT_SUBTITLE));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setPadding(1f);
                table1.addCell(cell);

                cell = new PdfPCell(new Phrase(celda_nueva2, FONT_SUBTITLE));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setPadding(1f);
                table1.addCell(cell);
            }
        }

        //CONTENIDO DE LA TABLA.
        float top_bottom_Padding = 8f;
        float left_right_Padding = 4f;
        boolean alternate = false;

        BaseColor lt_gray = new BaseColor(221,221,221); //#DDDDDD
        BaseColor cell_color;

        double total=0;
        int size = items.size();

        for (int i = 0; i < size; i++)
        {
            cell_color = alternate ? lt_gray : BaseColor.WHITE;
            String[] temp = items.get(i);
            byte[] imagen_temp = imagen_byte.get(i);

            cell = new PdfPCell(new Phrase(temp[0], FONT_CELL));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setPaddingLeft(left_right_Padding);
            cell.setPaddingRight(left_right_Padding);
            cell.setPaddingTop(top_bottom_Padding);
            cell.setPaddingBottom(top_bottom_Padding);
            cell.setBackgroundColor(cell_color);
            table1.addCell(cell);

            cell = new PdfPCell(new Phrase(temp[1], FONT_CELL));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setPaddingLeft(left_right_Padding);
            cell.setPaddingRight(left_right_Padding);
            cell.setPaddingTop(top_bottom_Padding);
            cell.setPaddingBottom(top_bottom_Padding);
            cell.setBackgroundColor(cell_color);
            table1.addCell(cell);



            cell = new PdfPCell(new Phrase(temp[2], FONT_CELL));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setPaddingLeft(left_right_Padding);
            cell.setPaddingRight(left_right_Padding);
            cell.setPaddingTop(top_bottom_Padding);
            cell.setPaddingBottom(top_bottom_Padding);
            cell.setBackgroundColor(cell_color);
            table1.addCell(cell);

            cell = new PdfPCell(new Phrase(format.format(Double.parseDouble(temp[3])), FONT_CELL));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setPaddingLeft(left_right_Padding);
            cell.setPaddingRight(left_right_Padding);
            cell.setPaddingTop(top_bottom_Padding);
            cell.setPaddingBottom(top_bottom_Padding);
            cell.setBackgroundColor(cell_color);
            table1.addCell(cell);

            cell = new PdfPCell(new Phrase(format.format(Double.parseDouble(temp[4])), FONT_CELL));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setPaddingLeft(left_right_Padding);
            cell.setPaddingRight(left_right_Padding);
            cell.setPaddingTop(top_bottom_Padding);
            cell.setPaddingBottom(top_bottom_Padding);
            cell.setBackgroundColor(cell_color);
            table1.addCell(cell);

            if (columnas==6){
                if (Fragment_factura_pdf.checkBoximagenes.isChecked()) {
                    try {


//                        Uri imageUri = Uri.parse(temp[5]);
//                        Bitmap bmp = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), imageUri);
//                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
//                        //MOSTRAR FOTO SEGUN FORMATO
//                        if( Factura_pdf.foto_vacia.equals(temp[5])){
//                            bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
//                        }else{
//                            bmp.compress(Bitmap.CompressFormat.JPEG, 100, stream);
//                        }
//                        Image logo = Image.getInstance(stream.toByteArray());
//                        logo.setWidthPercentage(80);
//                        logo.scaleToFit(80, 80);

                        PdfPCell logoCell = new PdfPCell(getImage(imagen_temp,false));
                        logoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        logoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        logoCell.setPaddingLeft(left_right_Padding);
                        logoCell.setPaddingRight(left_right_Padding);
                        logoCell.setPaddingTop(top_bottom_Padding);
                        logoCell.setPaddingBottom(top_bottom_Padding);
                        logoCell.setBackgroundColor(cell_color);
                        table1.addCell(logoCell);

                    } catch (Exception e) {
                        cell = new PdfPCell(new Phrase(mContext.getResources().getString(R.string.Imagen), FONT_CELL));
                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        cell.setPaddingLeft(left_right_Padding);
                        cell.setPaddingRight(left_right_Padding);
                        cell.setPaddingTop(top_bottom_Padding);
                        cell.setPaddingBottom(top_bottom_Padding);
                        cell.setBackgroundColor(cell_color);
                        table1.addCell(cell);
                    }
                }else{
                    //colocar codigo de barras
                    if (temp[5].equals("")||temp[5].equals(null)) {
                        cell = new PdfPCell(new Phrase("", FONT_CELL));
                    }else{
                        cell = new PdfPCell(getBarcodeImage(pdfWriter, temp[5]));
                    }

                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    cell.setPaddingLeft(left_right_Padding);
                    cell.setPaddingRight(left_right_Padding);
                    cell.setPaddingTop(top_bottom_Padding);
                    cell.setPaddingBottom(top_bottom_Padding);
                    cell.setBackgroundColor(cell_color);
                    table1.addCell(cell);
                }
            }


            if (columnas==7){


                try {
//                    Uri imageUri = Uri.parse(temp[5]);
//                    Bitmap bmp = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), imageUri);
//                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
//                    if( Factura_pdf.foto_vacia.equals(temp[5])){
//                        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
//                    }else{
//                        bmp.compress(Bitmap.CompressFormat.JPEG, 100, stream);
//                    }
//                    Image logo = Image.getInstance(stream.toByteArray());
//                    logo.setWidthPercentage(80);
//                    logo.scaleToFit(80, 80);

                    PdfPCell logoCell = new PdfPCell(getImage(imagen_temp,false));
                    logoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    logoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    logoCell.setPaddingLeft(left_right_Padding);
                    logoCell.setPaddingRight(left_right_Padding);
                    logoCell.setPaddingTop(top_bottom_Padding);
                    logoCell.setPaddingBottom(top_bottom_Padding);
                    logoCell.setBackgroundColor(cell_color);
                    table1.addCell(logoCell);

                } catch (Exception e) {
                    cell = new PdfPCell(new Phrase(mContext.getResources().getString(R.string.Barras), FONT_CELL));
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    cell.setPaddingLeft(left_right_Padding);
                    cell.setPaddingRight(left_right_Padding);
                    cell.setPaddingTop(top_bottom_Padding);
                    cell.setPaddingBottom(top_bottom_Padding);
                    cell.setBackgroundColor(cell_color);
                    table1.addCell(cell);
                }
                //colocar codigo de barras
                if (temp[6].equals("")||temp[6].equals(null)) {
                    cell = new PdfPCell(new Phrase("", FONT_CELL));
                }else{
                    cell = new PdfPCell(getBarcodeImage(pdfWriter, temp[6]));
                }
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setPaddingLeft(left_right_Padding);
                cell.setPaddingRight(left_right_Padding);
                cell.setPaddingTop(top_bottom_Padding);
                cell.setPaddingBottom(top_bottom_Padding);
                cell.setBackgroundColor(cell_color);
                table1.addCell(cell);

            }

            total= Double.parseDouble(temp[4])+total;
            alternate = !alternate;
        }


        //parte inferior
        document.add(table1);
        addEmptyLine(document, 1);

        PdfPTable tabla_costos = new PdfPTable(2);
        tabla_costos.setWidthPercentage(100);
        tabla_costos.setWidths(new float[]{9F,1.8F});
        tabla_costos.getDefaultCell().setBorder(PdfPCell.NO_BORDER);

        if (!temp_cliente[2].equals(mContext.getResources().getString(R.string.Inventario_existencia))) {
        if (!temp_cliente[5].equals("0.0")&&!temp_cliente[5].equals("0")){
            cell = new PdfPCell(new Phrase(mContext.getResources().getString(R.string.Descuento), FONT_SUBTITLE));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBorder(PdfPCell.NO_BORDER);
            tabla_costos.addCell(cell);

            cell = new PdfPCell(new Phrase(format.format(Double.parseDouble(temp_cliente[5])), FONT_SUBTITLE));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBorder(PdfPCell.NO_BORDER);
            tabla_costos.addCell(cell);
        }
            if (!temp_cliente[6].equals("0.0")&&!temp_cliente[6].equals("0")){
                cell = new PdfPCell(new Phrase(mContext.getResources().getString(R.string.Impuesto), FONT_SUBTITLE));
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setBorder(PdfPCell.NO_BORDER);
                tabla_costos.addCell(cell);

                cell = new PdfPCell(new Phrase(format.format(Double.parseDouble(temp_cliente[6])), FONT_SUBTITLE));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setBorder(PdfPCell.NO_BORDER);
                tabla_costos.addCell(cell);
            }

            if (!temp_cliente[7].equals("0.0")&&!temp_cliente[7].equals("0")){
                cell = new PdfPCell(new Phrase(mContext.getResources().getString(R.string.Envio), FONT_SUBTITLE));
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setBorder(PdfPCell.NO_BORDER);
                tabla_costos.addCell(cell);

                cell = new PdfPCell(new Phrase(format.format(Double.parseDouble(temp_cliente[7])), FONT_SUBTITLE));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setBorder(PdfPCell.NO_BORDER);
                tabla_costos.addCell(cell);
            }

        }


        cell = new PdfPCell(new Phrase( mContext.getResources().getString(R.string.Total), FONT_SUBTITLE));
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setBorder(PdfPCell.NO_BORDER);
        tabla_costos.addCell(cell);

        total=total-((total*Double.parseDouble(temp_cliente[5]))/100);
        total=total+((total*Double.parseDouble(temp_cliente[6]))/100);
        total=total+Double.parseDouble(temp_cliente[7]);

        cell = new PdfPCell(new Phrase(format.format(total)+"", FONT_SUBTITLE));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setBorder(PdfPCell.NO_BORDER);
        tabla_costos.addCell(cell);

        document.add(tabla_costos);

        addEmptyLine(document,1);


        if (Fragment_factura_pdf.checkBox_informacion_inferior.isChecked()){
            document.add(new Paragraph(Fragment_factura_pdf.informacion_inferior.getText().toString().trim()));
        }

        document.close();

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
        output.compress(Bitmap.CompressFormat.JPEG,100,stream);
        Image image=Image.getInstance(stream.toByteArray());
        image.setWidthPercentage(80);
        image.scaleToFit(165,75);
        return image;
    }

    private static Image getBarcodeImage(PdfWriter pdfWriter, String barcodeText)
    {
        Barcode128 barcode=new Barcode128();
        //barcode.setBaseline(-1); //BARCODE TEXT ABOVE
        barcode.setFont(null);
        barcode.setCode(barcodeText);
        barcode.setCodeType(Barcode128.CODE128);
        barcode.setTextAlignment(Element.ALIGN_BASELINE);
        return barcode.createImageWithBarcode(pdfWriter.getDirectContent(),BaseColor.BLACK,null);
    }
}
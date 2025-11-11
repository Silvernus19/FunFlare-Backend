// src/main/java/com/funflare/funflare/util/PdfTicketGenerator.java
package com.funflare.funflare.util;

import com.funflare.funflare.model.TicketPurchase;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.VerticalAlignment;
import com.itextpdf.io.image.ImageDataFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class PdfTicketGenerator {

    public static byte[] generateTicketPdf(TicketPurchase tp) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf, PageSize.A6);
        document.setMargins(20, 20, 20, 20);

        // Header
        document.add(new Paragraph("FUNFLARE TICKET")
                .setFontSize(20)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER)
                .setBackgroundColor(ColorConstants.ORANGE)
                .setPadding(10));

        // Event & Ticket Info
        document.add(new Paragraph("Event: " + tp.getTicket().getEvent().getName())
                .setFontSize(14).setBold());
        document.add(new Paragraph("Type: " + tp.getTicket().getType())
                .setFontSize(12));
        document.add(new Paragraph("Date: " + tp.getTicket().getEvent().getEventStartDate())
                .setFontSize(12));
        document.add(new Paragraph("Price: KES " + tp.getTicketPrice())
                .setFontSize(12));

        // Guest Info
        document.add(new Paragraph("\nGuest Details")
                .setFontSize(12).setBold().setUnderline());
        document.add(new Paragraph("Name: " + tp.getGuestName()));
        document.add(new Paragraph("Email: " + tp.getGuestEmail()));
        document.add(new Paragraph("Phone: " + tp.getGuestPhone()));

        // QR Code
        if (tp.getQrCodeImage() != null && tp.getQrCodeImage().length > 0) {
            Image qrImage = new Image(ImageDataFactory.create(tp.getQrCodeImage()));
            qrImage.setWidth(150);
            qrImage.setHeight(150);
            qrImage.setAutoScale(true);

            Cell qrCell = new Cell()
                    .add(qrImage)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setVerticalAlignment(VerticalAlignment.MIDDLE)
                    .setBorder(new SolidBorder(ColorConstants.BLACK, 2))
                    .setPadding(10);

            Table qrTable = new Table(1).setWidth(200).setFixedLayout();
            qrTable.addCell(qrCell);
            document.add(new Paragraph("\nScan to Validate")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(10));
            document.add(qrTable.setHorizontalAlignment(com.itextpdf.layout.properties.HorizontalAlignment.CENTER));
        }

        // Footer
        document.add(new Paragraph("\nTicket ID: " + tp.getQrCodeUid())
                .setFontSize(9)
                .setTextAlignment(TextAlignment.CENTER));
        document.add(new Paragraph("Valid for one entry only")
                .setFontSize(8)
                .setItalic()
                .setTextAlignment(TextAlignment.CENTER));

        document.close();
        return baos.toByteArray();
    }
}
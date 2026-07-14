/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import entity.Invoice;
import java.awt.Color;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;
import com.lowagie.text.Phrase;

/**
 *
 * @author DELL
 */
public final class InvoicePdfUtil {

    private InvoicePdfUtil() {
    }

    public static void download(Invoice inv) {
        FacesContext fc = FacesContext.getCurrentInstance();
        HttpServletResponse response = (HttpServletResponse) fc.getExternalContext().getResponse();

        try {
            Document document = new Document(PageSize.A4, 40, 40, 50, 50);

            response.reset();
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=\"invoice-" + inv.getId() + ".pdf\"");

            OutputStream out = response.getOutputStream();
            PdfWriter.getInstance(document, out);
            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22, new Color(37, 99, 235));
            Font labelFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, Color.DARK_GRAY);
            Font valueFont = FontFactory.getFont(FontFactory.HELVETICA, 11, Color.BLACK);
            Font smallFont = FontFactory.getFont(FontFactory.HELVETICA, 9, Color.GRAY);
            Font thFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, Color.WHITE);
            Font totalFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, new Color(37, 99, 235));

            Paragraph title = new Paragraph("INVOICE", titleFont);
            document.add(title);

            Paragraph invNumber = new Paragraph("Invoice #" + inv.getId(), smallFont);
            document.add(invNumber);
            document.add(Chunk.NEWLINE);

            SimpleDateFormat fmt = new SimpleDateFormat("dd MMM yyyy, HH:mm");

            PdfPTable infoTable = new PdfPTable(2);
            infoTable.setWidthPercentage(100);
            infoTable.setSpacingAfter(20);

            addInfoRow(infoTable, "Order Date:", fmt.format(inv.getOrderDate()), labelFont, valueFont);
            addInfoRow(infoTable, "Buyer:", inv.getBuyer() != null ? inv.getBuyer().getUsername() : "-", labelFont, valueFont);
            addInfoRow(infoTable, "Buyer Email:", inv.getBuyer() != null ? inv.getBuyer().getEmail() : "-", labelFont, valueFont);
            addInfoRow(infoTable, "Seller:", inv.getSellerUsername(), labelFont, valueFont);
            document.add(infoTable);

            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{4f, 2f, 1f, 2f});
            Color headerBg = new Color(44, 62, 80);

            addHeaderCell(table, "Product", thFont, headerBg);
            addHeaderCell(table, "Unit Price", thFont, headerBg);
            addHeaderCell(table, "Qty", thFont, headerBg);
            addHeaderCell(table, "Total", thFont, headerBg);

            addBodyCell(table, inv.getProductName(), valueFont, Element.ALIGN_LEFT);
            addBodyCell(table, "Rs " + String.format("%.2f", inv.getUnitPrice()), valueFont, Element.ALIGN_RIGHT);
            addBodyCell(table, String.valueOf(inv.getQuantity()), valueFont, Element.ALIGN_CENTER);
            addBodyCell(table, "Rs " + String.format("%.2f", inv.getTotalPrice()), valueFont, Element.ALIGN_RIGHT);

            document.add(table);
            document.add(Chunk.NEWLINE);

            Paragraph total = new Paragraph("Grand Total: Rs " + String.format("%.2f", inv.getTotalPrice()), totalFont);
            total.setAlignment(Element.ALIGN_RIGHT);
            document.add(total);

            document.add(Chunk.NEWLINE);
            Paragraph footer = new Paragraph("Thank you for your purchase!", smallFont);
            footer.setAlignment(Element.ALIGN_CENTER);
            document.add(footer);

            document.close();
            out.flush();
        } catch (Exception e) {
            throw new RuntimeException("Could not generate invoice PDF", e);
        }

        fc.responseComplete();
    }

    private static void addInfoRow(PdfPTable table, String label, String value, Font labelFont, Font valueFont) {
        PdfPCell l = new PdfPCell(new Phrase(label, labelFont));
        l.setBorder(0);
        PdfPCell v = new PdfPCell(new Phrase(value, valueFont));
        v.setBorder(0);
        table.addCell(l);
        table.addCell(v);
    }

    private static void addHeaderCell(PdfPTable table, String text, Font font, Color bg) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(bg);
        cell.setPadding(6);
        table.addCell(cell);
    }

    private static void addBodyCell(PdfPTable table, String text, Font font, int align) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setPadding(6);
        cell.setHorizontalAlignment(align);
        table.addCell(cell);
    }
}

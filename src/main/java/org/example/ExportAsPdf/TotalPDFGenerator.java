package org.example.ExportAsPdf;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import jakarta.servlet.http.HttpServletResponse;
import org.example.Pojos.ConsolidatedStatementWithLocations;
import org.example.Services.FileServices;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class TotalPDFGenerator {

    @Autowired
    FileServices fileServices;
    public void generate(HashMap<Long, List<ConsolidatedStatementWithLocations>> terminalsFromStatementThatMatchTerminalFromSiteLocation, HttpServletResponse response) throws DocumentException, IOException {
        // Creating the Object of Document
        Document document = new Document(PageSize.A4.rotate());
        // Getting instance of PdfWriter
        PdfWriter.getInstance(document, response.getOutputStream());
        // Opening the created document to change it
        document.open();
        // Creating font
        // Setting font style and size
        Font fontTiltle = FontFactory.getFont(FontFactory.TIMES_ROMAN);
        fontTiltle.setSize(20);

        Image logo = Image.getInstance("uploads/Fnb_logo_latest.jpg");
        logo.setAlignment(Image.ALIGN_RIGHT);
        logo.scaleAbsolute(75,60);
        document.add(logo);

        // Creating paragraph
        Paragraph paragraph1 = new Paragraph("Test Statement", fontTiltle);
        // Aligning the paragraph in the document
        paragraph1.setAlignment(Paragraph.ALIGN_CENTER);
        // Adding the created paragraph in the document
        document.add(paragraph1);

        // Creating a table of the 4 columns
        PdfPTable table = new PdfPTable(7);
        // Setting width of the table, its columns and spacing
        table.setWidthPercentage(100);
        table.setWidths(new int[] {1,5,5,3,5,3,3});
        table.setSpacingBefore(5);
        // Create Table Cells for the table header
        PdfPCell cell = new PdfPCell();
        // Setting the background color and padding of the table cell
        cell.setBackgroundColor(new RGBColor(.0f,.172f,.193f));
        cell.setPadding(5);
        // Creating font
        // Setting font style and size
        Font font = FontFactory.getFont(FontFactory.TIMES_ROMAN);
        font.setColor(CMYKColor.WHITE);
        // Adding headings in the created table cell or  header
        // Adding Cell to table
        cell.setPhrase(new Phrase("ID", font));
        table.addCell(cell);
        cell.setPhrase(new Phrase("Site Name", font));
        table.addCell(cell);
        cell.setPhrase(new Phrase("Outlet Number", font));
        table.addCell(cell);
        cell.setPhrase(new Phrase("Terminal Number", font));
        table.addCell(cell);
        cell.setPhrase(new Phrase("Card Number", font));
        table.addCell(cell);
        cell.setPhrase(new Phrase("Transaction Amount", font));
        table.addCell(cell);
        cell.setPhrase(new Phrase("Location", font));
        table.addCell(cell);

        terminalsFromStatementThatMatchTerminalFromSiteLocation.forEach((key,value)->{

//            if (terminalsFromStatementThatMatchTerminalFromSiteLocation.containsKey(key)) {

            for (ConsolidatedStatementWithLocations consolidatedStatementWithLocations: value) {

                table.addCell("#");
                // Adding student name
                table.addCell(consolidatedStatementWithLocations.getCustomerName());
                // Adding student email
                table.addCell(String.valueOf(consolidatedStatementWithLocations.getOutletNumber()));
                // Adding student mobile
                table.addCell(String.valueOf(consolidatedStatementWithLocations.getTerminalNumber()));

                table.addCell(consolidatedStatementWithLocations.getCardNumber());

                table.addCell(consolidatedStatementWithLocations.getTransactionAmount());

                table.addCell(consolidatedStatementWithLocations.getLocation());
            }

        });

       // Adding the created table to the document
        document.add(table);
        // Closing the document
        document.close();
    }


    public void generateHeaderAndFooterPage(HttpServletResponse response) throws DocumentException, IOException{


        // important step setting margins. based on the header and footer conent change the margin size
        Document document = new Document(PageSize.A4, 36, 36, 65, 36);

        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream("HeaderAndFooterExample.pdf"));
        writer.setPageEvent(new PdfPageEventHelper());
        document.open();

        Paragraph page1Body = new Paragraph("Page one content.");
        page1Body.setAlignment(Element.ALIGN_CENTER);
        document.add(page1Body);

        document.newPage();
        Paragraph page2Body = new Paragraph("Page two content.");
        page2Body.setAlignment(Element.ALIGN_CENTER);
        document.add(page2Body);

        document.close();
        writer.close();


        /* Header */
        PdfPTable table = new PdfPTable(3);
        table.setTotalWidth(510);
        table.setWidths(new int[]{38, 36, 36});
        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        table.getDefaultCell().setPaddingBottom(5);
        table.getDefaultCell().setBorder(Rectangle.BOTTOM);

        PdfPCell emptyCell = new PdfPCell(new Paragraph(""));
        emptyCell.setBorder(Rectangle.NO_BORDER);

        // Row#1 having 1 empty cell, 1 title cell and empty cell.
        table.addCell(emptyCell);
        Paragraph title =  new Paragraph("Grogu Inc.", new Font(Font.COURIER, 20, Font.BOLD));
        PdfPCell titleCell = new PdfPCell(title);
        titleCell.setPaddingBottom(10);
        titleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        titleCell.setBorder(Rectangle.NO_BORDER);
        table.addCell(titleCell);
        table.addCell(emptyCell);

        //Row#2 having 3 cells
        Font cellFont = new Font(Font.HELVETICA, 8);
        table.addCell(new Paragraph("Phone Number: 888-999-0000", cellFont));
        table.addCell(new Paragraph("Address : 333, Manhattan, New York", cellFont));
        table.addCell(new Paragraph("Website : http://grogu-yoda.com", cellFont));

        // write the table on PDF
        table.writeSelectedRows(0, -1, 34, 828, writer.getDirectContent());




    }
}

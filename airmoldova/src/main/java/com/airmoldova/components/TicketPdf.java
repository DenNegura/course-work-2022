package com.airmoldova.components;

import com.airmoldova.entity.Passenger;
import com.airmoldova.entity.Ticket;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.FileOutputStream;

public class TicketPdf {
    private final Document document;
    private final Ticket ticket;
    private final boolean status;
    public final String NAME_DOCUMENT = "ticket.pdf";
     public TicketPdf(Ticket ticket) {
         this.ticket = ticket;
        document = new Document();
         try {
             PdfWriter.getInstance(document, new FileOutputStream(NAME_DOCUMENT));
             document.open();
             infoFlight();
             infoPassenger();
             infoCost();
             infoMoreDate();
         } catch (Exception e) {
             e.printStackTrace();
             document.close();
             status = false;
             return;
         }
         document.close();
         status = true;
    }
    private void infoFlight() throws Exception {
         Font font = FontFactory.getFont(FontFactory.COURIER_BOLD, 16, BaseColor.BLACK);
         Chunk chunk = new Chunk("Flight ticket : " +
                 ticket.getFlight().getAirportDep().getCity() + " - " +
                 ticket.getFlight().getAirportArrival().getCity(), font);
         document.add(new Paragraph(chunk));
         font = FontFactory.getFont(FontFactory.COURIER, 12, BaseColor.BLACK);
         chunk = new Chunk("Airport departure : " +
                 ticket.getFlight().getAirportDep().getTitle() + " / " +
                 ticket.getFlight().getAirportDep().getCodeIATA(), font);
         document.add(chunk);
         chunk = new  Chunk("Airport arrival : " +
                 ticket.getFlight().getAirportArrival().getTitle() + " / " +
                 ticket.getFlight().getAirportArrival().getCodeIATA(), font);
         document.add(new Paragraph(chunk));
         chunk = new Chunk("Date departure : " + ticket.getFlight().getFormatDateDeparture(), font);
         document.add(new Paragraph(chunk));
         chunk = new Chunk("Date arrival : " + ticket.getFlight().getFormatDateArrival(), font);
         document.add(new Paragraph(chunk));
         document.add(new Paragraph("\n"));
    }
    private void infoPassenger() throws Exception {
        Font font = FontFactory.getFont(FontFactory.COURIER, 16, BaseColor.BLACK);
        document.add(new Paragraph(new Chunk("Passenger date : ", font)));
        font = FontFactory.getFont(FontFactory.COURIER, 12, BaseColor.BLACK);
        document.add(new Paragraph(new Chunk("First name : " + ticket.getPassenger().getFirstName(), font)));
        document.add(new Paragraph(new Chunk("Last name : " + ticket.getPassenger().getLastName(), font)));
        document.add(new Paragraph(new Chunk("Date born : " + ticket.getPassenger().getDateBornFormatDate(), font)));
        document.add(new Paragraph(new Chunk("Type document : " + ticket.getPassenger().documentType(), font)));
        document.add(new Paragraph("\n"));
    }
    private void infoCost() throws Exception {
        Font font = FontFactory.getFont(FontFactory.COURIER, 12, BaseColor.BLACK);
        document.add(new Paragraph(new Chunk("Class : " + ticket.getTypeClass(), font)));
        if(ticket.getTypeClass().equals("economy")) {
            document.add(new Paragraph(new Chunk("Cost class : " + ticket.getFlight().getPricingPolicy().getPriceEconomy(), font)));
        }
        else if(ticket.getTypeClass().equals("comfort")) {
            document.add(new Paragraph(new Chunk("Cost class : " + ticket.getFlight().getPricingPolicy().getPriceComfort(), font)));
        }
        else {
            document.add(new Paragraph(new Chunk("Cost class : " + ticket.getFlight().getPricingPolicy().getPriceBusiness(), font)));
        }
        document.add(new Paragraph(new Chunk("Kg baggage : " + ticket.getBaggage().getWeight(), font)));
        document.add(new Paragraph(new Chunk("Cost baggage : " + ticket.getBaggage().getAdditionalPrice(), font)));
        document.add(new Paragraph(new Chunk("Total cost : " + ticket.getPrice(), font)));
        document.add(new Paragraph("\n"));
    }
    private void infoMoreDate() throws Exception {
        Font font = FontFactory.getFont(FontFactory.COURIER, 12, BaseColor.BLACK);
        document.add(new Paragraph(new Chunk("Seat : " + ticket.getSeat(), font)));
        document.add(new Paragraph("\n"));
        document.add(new Paragraph("Help center : 022-567-135", font));
    }
    public boolean getStatus() {
         return status;
    }
}

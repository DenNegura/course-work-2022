package com.airmoldova.view;


import com.airmoldova.DAO.PassengerDAO;
import com.airmoldova.DAO.TicketDAO;
import com.airmoldova.components.TicketPdf;
import com.airmoldova.components.email.Email;
import com.airmoldova.entity.Passenger;
import com.airmoldova.entity.Ticket;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import org.hibernate.SessionFactory;

@AnonymousAllowed
@Route(value = "", layout = MainLayout.class)
@PageTitle("main | AIR MOLDOVA")
public class MainView extends VerticalLayout {
    public MainView(SessionFactory sessionFactory) {
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.addClassName("main-div");
        StreamResource imageResource = new StreamResource("airmoldova.jpg",
                () -> getClass().getResourceAsStream("/images/airmoldova.jpg"));
        Image image = new Image(imageResource, "Air Moldova");
        image.setClassName("main-image");
        Label title = new Label("AIR MOLDOVA");
        title.setClassName("main-title");
        Label text = new Label("Welcome! Get today's flight status and customize your visit.");
        text.setClassName("main-subtitle");
        VerticalLayout info = new VerticalLayout();
        info.add(new Label("KIV Hours of Operation and Access"),
                new Label("Entry to KIV is only allowed for airline passengers and persons meeting, " +
                        "accompanying or assisting them, and airport personnel whose employment requires " +
                        "their presence. KIV is closed to the general public 24 hours a day, 7 days a week."),
                new Label("Opening hours for passengers and personnel vary between terminals; persons " +
                        "are advised to check with their airline or employer."));
        info.setClassName("main-info");

        VerticalLayout contacts = new VerticalLayout();
        contacts.add(new Label("Contacts:"),
                new Label(" +373 22 817 817"),
                new Label("+373 22 525 111"),
                new Label("Dacia boulevard 80/3, Chisinau 2026"),
                new Label("Republic of Moldova"),
                new Label("hotline@airport.md"));
        contacts.setClassName("main-contacts");
        contacts.setSpacing(false);
        verticalLayout.add(title, text, image, info, contacts);
        add(verticalLayout);
    }
}

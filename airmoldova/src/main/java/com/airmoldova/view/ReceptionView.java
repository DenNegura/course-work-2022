package com.airmoldova.view;

import com.airmoldova.DAO.PassengerDAO;
import com.airmoldova.DAO.TicketDAO;
import com.airmoldova.components.DatabaseConnection;
import com.airmoldova.entity.*;
import com.airmoldova.security.SecurityService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.security.RolesAllowed;
import java.time.format.DateTimeFormatter;

@RolesAllowed({ "OPERATOR" })
@Route(value = "reception", layout = MainLayout.class)
@PageTitle("reception | AIR MOLDOVA")
public class ReceptionView extends HorizontalLayout {
    private final SessionFactory sessionFactory;
    private final DatabaseConnection dbConnection;
    private final PassengerDAO passengerDAO;
    private final TextField searchField = new TextField();
    private VerticalLayout flightDetails = new VerticalLayout();
    private final Grid<Passenger> grid = new Grid<>(Passenger.class, false);
    @Autowired
    public ReceptionView(SecurityService securityService, SessionFactory sessionFactory) {
        setHeight("100%");
        this.sessionFactory = sessionFactory;
        dbConnection = new DatabaseConnection("jdbc:postgresql://127.0.0.1/AirMoldova",
                securityService.getAuthenticatedUser().getUsername(),
                getPassword(securityService.getAuthenticatedUser().getUsername()));
        dbConnection.openSessionFactory();
        dbConnection.addAnnotatedClass(Passenger.class);
        dbConnection.addAnnotatedClass(TypeDocument.class);
        dbConnection.addAnnotatedClass(Ticket.class);
        dbConnection.addAnnotatedClass(Flight.class);
        dbConnection.addAnnotatedClass(Airport.class);
        dbConnection.addAnnotatedClass(Airplane.class);
        dbConnection.addAnnotatedClass(Baggage.class);
        dbConnection.addAnnotatedClass(PricingPolicy.class);
        dbConnection.buildSessionFactory();
        passengerDAO = new PassengerDAO(dbConnection.getSessionFactory());


        grid.addColumn(Passenger::getFirstName).setHeader("First name").setAutoWidth(true);
        grid.addColumn(Passenger::getLastName).setHeader("Last name").setAutoWidth(true);
        grid.addColumn(Passenger::getDateBorn).setHeader("Date born").setAutoWidth(true);
        grid.addColumn(Passenger::documentType).setHeader("Type document").setAutoWidth(true);
        grid.addColumn(Passenger::getPersonalCode).setHeader("Personal code").setAutoWidth(true);
        grid.addColumn(Passenger::getDocumentCode).setHeader("Document code").setAutoWidth(true);
        grid.addColumn(Passenger::getCountryDoc).setHeader("Issuing country").setAutoWidth(true);
        grid.addColumn(Passenger::getDateExp).setHeader("Date of expiry").setAutoWidth(true);
        grid.addColumn(setPermission()).setHeader("Status").setAutoWidth(true);
        grid.addColumn(getFlight()).setHeader("").setAutoWidth(true);

        grid.setSelectionMode(Grid.SelectionMode.NONE);
        GridListDataView<Passenger> dataView = grid.setItems(passengerDAO.getAll(false));

        searchField.setWidth("50%");
        searchField.setPlaceholder("Search");
        searchField.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        searchField.setValueChangeMode(ValueChangeMode.EAGER);
        searchField.addValueChangeListener(e -> dataView.refreshAll());

        dataView.addFilter(passenger -> {
            String searchTerm = searchField.getValue().trim();

            if (searchTerm.isEmpty())
                return true;

            boolean matchesFirstName = matchesTerm(passenger.getFirstName(),
                    searchTerm);
            boolean matchesLastName = matchesTerm(passenger.getLastName(), searchTerm);
            boolean matchesDateBorn = matchesTerm(passenger.getDateBorn().toString(),
                    searchTerm);
            boolean matchesPersonalCode = matchesTerm(passenger.getPersonalCode(), searchTerm);
            boolean matchesDocumentCode = matchesTerm(passenger.getDocumentCode(), searchTerm);
            return matchesFirstName || matchesLastName || matchesDateBorn ||
                    matchesPersonalCode || matchesDocumentCode;
        });

        VerticalLayout dataLayout = new VerticalLayout();
        dataLayout.add(searchField, grid);
        setFlexGrow(3, dataLayout);
        setFlexGrow(0, flightDetails);
        flightDetails.setVisible(false);
        add(dataLayout, flightDetails);
    }
    private boolean matchesTerm(String value, String searchTerm) {
        return value.toLowerCase().contains(searchTerm.toLowerCase());
    }
    private String getPassword(String login) {
        try(Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            Query query = session.createSQLQuery("select passwd from emp_login where login = '" + login + "';");
            session.getTransaction().commit();
            return (String)query.getSingleResult();
        }
    }
    private ComponentRenderer<Button, Passenger> setPermission() {
        return new ComponentRenderer<>(passenger -> {
            Button permissionButton = new Button("Permission");
            if(passenger.isRegistration()) {
                permissionButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
            }
            else {
                permissionButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
            }
            permissionButton.addClickListener( e -> {
                if(passenger.isRegistration()) {
                    passenger.setRegistration(false);
                    permissionButton.removeThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
                    permissionButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
                    Notification notification = Notification.show(passenger.getFirstName() + " " + passenger.getLastName() + " is not allowed on the flight", 2000, Notification.Position.TOP_END);
                    notification.addThemeVariants(NotificationVariant.LUMO_PRIMARY, NotificationVariant.LUMO_ERROR);
                    flightDetails.setVisible(false);
                    flightDetails.removeAll();
                }
                else {
                    passenger.setRegistration(true);
                    permissionButton.removeThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
                    permissionButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
                    Notification notification = Notification.show(passenger.getFirstName() + " " + passenger.getLastName() + " is allowed on the flight", 2000, Notification.Position.TOP_END);
                    notification.addThemeVariants(NotificationVariant.LUMO_PRIMARY, NotificationVariant.LUMO_SUCCESS);
                    flightDetails.setVisible(false);
                    flightDetails.removeAll();
                }
                passengerDAO.update(passenger);
            });
            return permissionButton;
        });
    }
    private ComponentRenderer<Button, Passenger> getFlight() {
        return new ComponentRenderer<>(passenger -> {
           return new Button("Flight", e-> {
               if(flightDetails.isVisible()) {
                   flightDetails.removeAll();
               }
               flightDetailsView(passenger.getIdPassenger());
           });
        });
    }
    private void flightDetailsView(int idPassenger) {
        //flightDetails = new VerticalLayout();
        int idTicket;
        try(Session session = dbConnection.openSession()) {
            session.beginTransaction();
            Query query = session.createSQLQuery("select id_ticket from ticket where id_passanger = " + idPassenger + ";");
            session.getTransaction().commit();
            idTicket = (Integer)query.getSingleResult();
        } catch (Exception e){
            e.printStackTrace();
            return;
        }
        TicketDAO ticketDAO = new TicketDAO(dbConnection.getSessionFactory());
        Ticket ticket = ticketDAO.read(idTicket);
        if(ticket == null) {
            return;
        }
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        TextField airportArrival = new TextField();
        airportArrival.setReadOnly(true);
        airportArrival.setLabel("Airport arrival");
        airportArrival.setValue(ticket.getFlight().getPositionAirportArrival());

        TextField dateDeparture = new TextField();
        dateDeparture.setReadOnly(true);
        dateDeparture.setLabel("Date departure");
        dateDeparture.setValue(ticket.getFlight().getDateDeparture().format(dateTimeFormatter));
        dateDeparture.setClassName("lower");

        TextField dateArrival = new TextField();
        dateArrival.setReadOnly(true);
        dateArrival.setLabel("Date arrival");
        dateArrival.setValue(ticket.getFlight().getDateArrival().format(dateTimeFormatter));

        TextField typeClass = new TextField();
        typeClass.setReadOnly(true);
        typeClass.setLabel("Class");
        typeClass.setValue(ticket.getTypeClass());

        TextField cost = new TextField();
        cost.setReadOnly(true);
        cost.setLabel("Cost");
        cost.setValue(Double.toString(ticket.getPrice()));

        TextField seat = new TextField();
        seat.setReadOnly(true);
        seat.setLabel("Seat");
        seat.setValue(Integer.toString(ticket.getSeat()));

        TextField datePay = new TextField();
        datePay.setReadOnly(true);
        datePay.setLabel("Date pay");
        datePay.setValue(ticket.getDateSale().format(dateTimeFormatter));

        Button closeButton = new Button("close", e-> {
            flightDetails.setVisible(false);
            flightDetails.removeAll();
        });
        flightDetails.add(airportArrival, dateDeparture, dateArrival, typeClass, cost, seat, datePay, closeButton);
        flightDetails.setVisible(true);
        flightDetails.setWidth("20%");
    }
}

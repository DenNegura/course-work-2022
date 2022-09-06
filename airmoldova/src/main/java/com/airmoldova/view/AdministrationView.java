package com.airmoldova.view;

import com.airmoldova.DAO.PassengerDAO;
import com.airmoldova.components.DatabaseConnection;
import com.airmoldova.components.EditForm;
import com.airmoldova.entity.*;
import com.airmoldova.repository.TypeDocumentRepo;
import com.airmoldova.security.SecurityService;
import com.vaadin.flow.component.HasText;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.StringToDateConverter;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.security.RolesAllowed;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

@RolesAllowed({ "ADMINISTRATOR" })
@Route(value = "administration", layout = MainLayout.class)
@PageTitle("administration | AIR MOLDOVA")
public class AdministrationView extends VerticalLayout {
    DatabaseConnection dbConnection;
    SessionFactory sessionFactory;
    PassengerDAO passengerDAO;
    private final Grid<Passenger> grid = new Grid<>(Passenger.class, false);
    Editor<Passenger> editor = grid.getEditor();
    private final TextField searchField = new TextField();
    private EditForm editForm;

    @Autowired
    public AdministrationView(SecurityService securityService, SessionFactory sessionFactory, TypeDocumentRepo typeDocumentRepo) {
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
        editForm = new EditForm(passengerDAO, typeDocumentRepo);
        editForm.setChangerHandler(() -> {
            editForm.setVisible(false);
            grid.setItems(passengerDAO.getAll(false));
        });

        Grid.Column<Passenger> firstNameColumn =
                grid.addColumn(Passenger::getFirstName).setHeader("First name").setAutoWidth(true);
        Grid.Column<Passenger> lastNameColumn =
                grid.addColumn(Passenger::getLastName).setHeader("Last name").setAutoWidth(true);
        Grid.Column<Passenger> dateBornColumn =
                grid.addColumn(Passenger::getDateBorn).setHeader("Date born").setAutoWidth(true);
        Grid.Column<Passenger> typeDocumentColumn =
            grid.addColumn(Passenger::documentType).setHeader("Type document").setAutoWidth(true);
        Grid.Column<Passenger> personalCodeColumn =
            grid.addColumn(Passenger::getPersonalCode).setHeader("Personal code").setAutoWidth(true);
        Grid.Column<Passenger> documentCodeColumn =
            grid.addColumn(Passenger::getDocumentCode).setHeader("Document code").setAutoWidth(true);
        Grid.Column<Passenger> countryColumn =
            grid.addColumn(Passenger::getCountryDoc).setHeader("Issuing country").setAutoWidth(true);
        Grid.Column<Passenger> dateExpColumn =
            grid.addColumn(Passenger::getDateExp).setHeader("Date of expiry").setAutoWidth(true);
        // edit
        Grid.Column<Passenger> editColumn = grid.addComponentColumn(passenger -> {
            Button editButton = new Button("Edit");
            editButton.addClickListener(e -> {
                editForm.editPassenger(passenger);
            });
            return editButton;
        }).setAutoWidth(true);

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

        add(searchField, grid, editForm);
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
}

package com.airmoldova.view;


import com.airmoldova.components.RegistrationForm;

import com.airmoldova.entity.Flight;
import com.airmoldova.repository.FlightRepo;
import com.airmoldova.repository.TypeDocumentRepo;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.html.Label;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import org.springframework.beans.factory.annotation.Autowired;



import javax.persistence.Query;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;

@AnonymousAllowed
@Route(value = "flights", layout = MainLayout.class)
@PageTitle("flights | AIR MOLDOVA")
public class FlightsView extends VerticalLayout {
//    private final HorizontalLayout
    private Grid<Flight> grid = new Grid<>(Flight.class, false);
    private final SessionFactory sessionFactory;
    private final Button openRegistrationForm = new Button("ONLINE CHECK-IN");

    RegistrationForm registrationForm;
    @Autowired
    public FlightsView(TypeDocumentRepo typeDocumentRepo, FlightRepo flightRepo, SessionFactory sessionFactory) {
        setHeight("100%");
        this.sessionFactory = sessionFactory;
        registrationForm = new RegistrationForm(typeDocumentRepo, sessionFactory);
        Grid.Column<Flight> airplane =
                grid.addColumn(Flight::getModelAirplane)
                .setAutoWidth(true);
        Grid.Column<Flight> titleAirportArrival = grid.addColumn(
                Flight::getTitleAirportArrival)
                .setAutoWidth(true);
        Grid.Column<Flight> positionAirportArrival = grid.addColumn(
                Flight::getPositionAirportArrival)
                .setAutoWidth(true);
        Grid.Column<Flight> dateDeparture = grid.addColumn(
                Flight::getFormatDateDeparture)
                .setAutoWidth(true);
        Grid.Column<Flight> dateArrival = grid.addColumn(
                Flight::getFormatDateArrival)
                .setAutoWidth(true);
        Grid.Column<Flight> duration = grid.addColumn(
                Flight::getFormatTime)
                .setAutoWidth(true);
        grid.addColumn(createCostRenderer(grid));

        grid.setDetailsVisibleOnClick(false);
        grid.setItemDetailsRenderer(createFlightCostRenderer());
        grid.addClassName("upper");
        grid.setHeight("100%");

        GridListDataView<Flight> dataView = grid.setItems(flightRepo.findAll());
        FlightFilter flightFilter = new FlightFilter(dataView);
        grid.getHeaderRows().clear();
        HeaderRow headerRow = grid.appendHeaderRow();

        headerRow.getCell(airplane).setComponent(
                createFilterHeader("Airplane", flightFilter::setAirplane));
        headerRow.getCell(titleAirportArrival).setComponent(
                createFilterHeader("Airport arrival", flightFilter::setTitleAirportArrival));
        headerRow.getCell(positionAirportArrival).setComponent(
                createFilterHeader("City", flightFilter::setPositionAirportArrival));
        headerRow.getCell(dateDeparture).setComponent(
                createFilterHeader("Date departure", flightFilter::setDateDeparture));
        headerRow.getCell(dateArrival).setComponent(
                createFilterHeader("Date arrival", flightFilter::setDateArrival));
        headerRow.getCell(duration).setComponent(
                createFilterHeader("Duration", flightFilter::setDuration));

        registrationForm.setVisible(false);
        add(grid, openRegistrationForm, registrationForm);
        openRegistrationForm.addClickListener(
                e -> registrationForm.setVisible(openRegistrationForm, false, true)
        );

        grid.addSelectionListener(selection -> {
            Optional<Flight> optionalFlight = selection.getFirstSelectedItem();
            if(optionalFlight.isPresent()) {
                registrationForm.setFlight(optionalFlight.get());
            }
        });
    }
    private ComponentRenderer<FlightCostFormLayout, Flight> createFlightCostRenderer() {
        return new ComponentRenderer<>(FlightCostFormLayout::new, FlightCostFormLayout::setFlight);
    }
    private Renderer<Flight> createCostRenderer(Grid<Flight> grid) {
        return LitRenderer.<Flight>of(
                "<vaadin-button theme=\"tertiary\" @click=\"${handleClick}\">Cost</vaadin-button>")
                .withFunction("handleClick", person -> grid
                        .setDetailsVisible(person,
                                !grid.isDetailsVisible(person))
                );
    }
    private Component createFilterHeader(String labelText, Consumer<String> filterChangeConsumer) {
        Label label = new Label(labelText);
        label.getStyle().set("padding-top", "var(--lumo-space-m)")
                .set("font-size", "var(--lumo-font-size-xs)");
        TextField textField = new TextField();
        textField.setValueChangeMode(ValueChangeMode.EAGER);
        textField.setClearButtonVisible(true);
        textField.addThemeVariants(TextFieldVariant.LUMO_SMALL);
        textField.setWidthFull();
        textField.getStyle().set("max-width", "100%");
        textField.addValueChangeListener(
                e -> filterChangeConsumer.accept(e.getValue()));
        VerticalLayout layout = new VerticalLayout(label, textField);
        layout.getThemeList().clear();
        layout.getThemeList().add("spacing-xs");

        return layout;
    }
    public class FlightCostFormLayout extends FormLayout {
        private final TextField economyClass = new TextField("Economy class");
        private final TextField comfortClass = new TextField("Comfort class");
        private final TextField businessClass = new TextField("Business class");
        private final TextField freeEconomySeats = new TextField("Seats");
        private final TextField freeComfortSeats = new TextField("Seats");
        private final TextField freeBusinessSeats = new TextField("Seats");

        public FlightCostFormLayout() {
            Stream.of(economyClass, comfortClass, businessClass,
                            freeEconomySeats, freeComfortSeats, freeBusinessSeats)
                    .forEach(field -> {
                        field.setReadOnly(true);
                        add(field);
                    });
            setResponsiveSteps(new ResponsiveStep("0", 3));
            setColspan(economyClass,1);
            setColspan(comfortClass, 1);
            setColspan(businessClass, 1);
            setColspan(freeEconomySeats, 1);
            setColspan(freeComfortSeats, 1);
            setColspan(freeBusinessSeats, 1);

        }

        public void setFlight(Flight flight) {
            economyClass.setValue(Double.toString(flight.getPricingPolicy().getPriceEconomy()) + " EUR");
            comfortClass.setValue(Double.toString(flight.getPricingPolicy().getPriceComfort()) + " EUR");
            businessClass.setValue(Double.toString(flight.getPricingPolicy().getPriceBusiness()) + " EUR");
            freeEconomySeats.setValue(getFreeSeats("economy",flight.getIdFlight()));
            freeComfortSeats.setValue(getFreeSeats("comfort",flight.getIdFlight()));
            freeBusinessSeats.setValue(getFreeSeats("business",flight.getIdFlight()));


        }
        private String getFreeSeats(String class_type, int id_flight) {
            try(Session session = sessionFactory.openSession()) {
                session.beginTransaction();
                Query query = session.createSQLQuery("select get_free_seats('" + class_type + "', " + id_flight + ");");
                session.getTransaction().commit();
                if(query.getSingleResult() == null) return "NO SEATS";
                int value = (Integer)query.getSingleResult();
                if(value == 0) {
                    return "NO SEATS";
                }
                else {

                    return Integer.toString(value);
                }
            }
        }
    }
    private static class FlightFilter {
        private final GridListDataView<Flight> dataView;

        private String airplane;
        private String titleAirportArrival;
        private String positionAirportArrival;
        private String dateDeparture;
        private String dateArrival;
        private String duration;

        public FlightFilter(GridListDataView<Flight> dataView) {
            this.dataView = dataView;
            this.dataView.addFilter(this::search);
        }

        public void setAirplane(String airplane) {
            this.airplane = airplane;
            this.dataView.refreshAll();
        }

        public void setTitleAirportArrival(String airportArrival) {
            this.titleAirportArrival = airportArrival;
            this.dataView.refreshAll();
        }

        public void setPositionAirportArrival(String city) {
            this.positionAirportArrival = city;
            this.dataView.refreshAll();
        }

        public void setDateDeparture(String dateDeparture) {
            this.dateDeparture = dateDeparture;
            this.dataView.refreshAll();
        }

        public void setDateArrival(String dateArrival) {
            this.dateArrival = dateArrival;
            this.dataView.refreshAll();
        }

        public void setDuration(String duration) {
            this.duration = duration;
            this.dataView.refreshAll();
        }

        public boolean search(Flight flight) {
            boolean matchesAirplane = matches(
                    flight.getModelAirplane(), airplane);
            boolean matchesAirportArrival = matches(
                    flight.getTitleAirportArrival(), titleAirportArrival);
            boolean matchesCity = matches(
                    flight.getPositionAirportArrival(), positionAirportArrival);
            boolean matchesDateDeparture = matches(
                    flight.getFormatDateDeparture(), dateDeparture);
            boolean matchesDateArrival = matches(
                    flight.getFormatDateArrival(), dateArrival);
            boolean matchesDuration = matches(
                    flight.getFormatTime(), duration);

            return matchesAirplane
                    && matchesAirportArrival
                    && matchesCity
                    && matchesDateDeparture
                    && matchesDateArrival
                    && matchesDuration;
        }
        private boolean matches(String value, String searchTerm) {
            return searchTerm == null || searchTerm.isEmpty() || value.
                    toLowerCase().contains(searchTerm.toLowerCase());
        }
    }
}


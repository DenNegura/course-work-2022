package com.airmoldova.components;
import com.airmoldova.DAO.TicketDAO;
import com.airmoldova.components.email.Email;
import com.airmoldova.entity.*;
import com.airmoldova.repository.TypeDocumentRepo;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.accordion.AccordionPanel;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.radiobutton.RadioGroupVariant;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.splitlayout.SplitLayoutVariant;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import javax.persistence.Query;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.IntStream;

public class RegistrationForm extends VerticalLayout {

    private Flight flight;
    SessionFactory sessionFactory;
    private final TextField firstName = new TextField("first name");
    private final TextField lastName = new TextField("last name");
    private final ComboBox<Integer> years;
    private final ComboBox<Month> months;
    private final ComboBox<Integer> days;
    private final EmailField email = new EmailField("e-mail");
    private final TextField telephone = new TextField("phone number");
    private final ComboBox<TypeDocument> typeDocument = new ComboBox<>("type document");
    private final TextField personalCode = new TextField("personal code");
    private final TextField documentCode = new TextField("document code");
    private final TextField countryDocument = new TextField("issuing country");
    private final DatePicker dateExp = new DatePicker("date of expiry");

    private final TextField freeHandLuggage = new TextField("hand luggage");
    private final TextField freeBaggage = new TextField("baggage");
    private final IntegerField addBaggage = new IntegerField("more kg");

    private Button closeForm;

    private final RadioButtonGroup<String> travelClass = new RadioButtonGroup<>();

    private final Accordion registration = new Accordion();
    private final AccordionPanel personalData;
    private final AccordionPanel documentDetails;
    private final AccordionPanel flightBooking;
    private final AccordionPanel payment;

    TextField cardNumber = new TextField("Card number");
    TextField cardOwner = new TextField("Card owner");

    public RegistrationForm(TypeDocumentRepo typeDocumentRepo, SessionFactory sessionFactory) {
        setWidth("100%");
        setClassName("upper");
        this.sessionFactory = sessionFactory;

        FormLayout personalDataForm = new FormLayout();
        SplitLayout personalDataSplit = new SplitLayout(personalDataForm, new Div());
        personalData = registration.add("Personal data", personalDataSplit);

        FormLayout documentDetailsForm = new FormLayout();
        SplitLayout documentDetailSplint = new SplitLayout(documentDetailsForm, new Div());
        documentDetails = registration.add("Travel document details", documentDetailSplint);

        FormLayout flightBookingForm = new FormLayout();
        SplitLayout flightBookingSplit = new SplitLayout(flightBookingForm, new Div());
        flightBooking = registration.add("Flight booking", flightBookingSplit);

        FormLayout paymentForm = new FormLayout();
        SplitLayout paymentSplit = new SplitLayout(paymentForm, new Div());
        payment = registration.add("payment", paymentSplit);

        firstName.setPattern("^[A-Z]{1}[a-z]{1,23}\\w+$");
        lastName.setPattern("^[A-Z]{1}[a-z]{1,23}\\w+$");
        LocalDate now = LocalDate.now(ZoneId.systemDefault());
        List<Integer> selectableYears = IntStream.range(
                        now.getYear() - 99,
                        now.getYear() + 1)
                .boxed().toList();
        years = new ComboBox<>("Year", selectableYears);
        months = new ComboBox<>("Month", Month.values());
        months.setItemLabelGenerator(m -> m.getDisplayName(
                TextStyle.FULL,
                Locale.ENGLISH
        ));
        months.setClassName("upper");
        days = new ComboBox<>("Day");
        days.setEnabled(false);
        months.addValueChangeListener(e -> updateDays());
        telephone.setPattern("^[+]?[(]?[0-9]{3}[)]?[-s.]?[0-9]{3}[-s.]?[0-9]{5}$");
        telephone.setHelperText("Format: +(373)676-18822");
        Button personalDataButton = new Button("Continue", (e) -> documentDetails.setOpened(true));
        personalDataForm.add(firstName, lastName, years, months, days, email, telephone, personalDataButton);
        personalDataForm.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("250px", 2),
                new FormLayout.ResponsiveStep("500px", 3),
                new FormLayout.ResponsiveStep("750px", 4),
                new FormLayout.ResponsiveStep("1000px", 5),
                new FormLayout.ResponsiveStep("1250px", 6),
                new FormLayout.ResponsiveStep("1500px", 7)

        );
        personalDataForm.setColspan(personalDataButton, 1);
        personalDataSplit.addThemeVariants(SplitLayoutVariant.LUMO_MINIMAL);

        typeDocument.setItems(typeDocumentRepo.findAll());
        typeDocument.setItemLabelGenerator(TypeDocument::getTypeDocument);
        personalCode.setPattern("[0-9]{12}");
        documentCode.setPattern("^[A-Z]{1}[0-9]{8}");
        countryDocument.setPattern("^[A-Z]{1}[a-z]{1,23}\\w+$");
        dateExp.setMin(now.plusDays(30));
        dateExp.setAutoOpen(false);
        Button documentDetailsButton = new Button("Continue", (e) -> flightBooking.setOpened(true));
        documentDetailsForm.add(typeDocument, personalCode, documentCode, countryDocument, dateExp, documentDetailsButton);
        documentDetailsForm.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("250px", 2),
                new FormLayout.ResponsiveStep("500px", 3),
                new FormLayout.ResponsiveStep("750px", 4),
                new FormLayout.ResponsiveStep("1000px", 5)
        );
        documentDetailsForm.setColspan(documentDetailsButton, 1);
        documentDetailSplint.addThemeVariants(SplitLayoutVariant.LUMO_MINIMAL);

        Button flightBookingButton = new Button("Continue", (e) -> payment.setOpened(true));
        insertInfoTravelClass();
        VerticalLayout baggage = new VerticalLayout(freeBaggage);
        baggage.add(freeHandLuggage, freeBaggage, addBaggage);
        baggage.setFlexGrow(0);
        flightBookingForm.add(travelClass, baggage, flightBookingButton);
        flightBookingForm.setResponsiveSteps(
                new FormLayout.ResponsiveStep("500px", 2)
        );
        flightBookingForm.setColspan(flightBookingButton, 1);
        flightBookingSplit.addThemeVariants(SplitLayoutVariant.LUMO_MINIMAL);


        Button confirmPaymentButton = new Button("Confirm payment", (e) -> {
            if(!dialogConstructor()) return;
            if(!insertNewPassenger()) {
                Dialog errorDialog = new Dialog();
                Text errorMassage = new Text("I'm sorry, we were unable to book you a ticket.");
                Button closeDialog = new Button("close", (error) -> errorDialog.close());
                errorDialog.add(new VerticalLayout(errorMassage, closeDialog));
                errorDialog.open();
                return;
            }
            Dialog successDialog = new Dialog();
            Text successMassage = new Text("Payment was successful. " +
                    "A latter containing a ticket has been sent to you Email. " +
                    "If there is no email, please contact support : 022-567-135." +
                    " Thank you for being with us!");
            Button closeDialog = new Button("close", (success) -> successDialog.close());
            successDialog.add(new VerticalLayout(successMassage, closeDialog));
            successDialog.open();
            cardNumber.clear();
            cardOwner.clear();
        });
        cardNumber.setPattern("[0-9]{4}-[0-9]{4}-[0-9]{4}-[0-9]{4}");
        cardNumber.setHelperText("Format : 0000-1111-2222-3333");
        cardOwner.setPattern("^[A-Z]{1,23}\\s[A-Z]{1,23}$");
        cardOwner.setHelperText("Formal : Rick Duglas");
        paymentForm.add(cardNumber, cardOwner, confirmPaymentButton);
        paymentForm.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("250px", 2)
        );
        paymentForm.setColspan(confirmPaymentButton, 1);
        paymentSplit.addThemeVariants(SplitLayoutVariant.LUMO_MINIMAL);

        Button close = new Button("close", (e) -> setVisible(closeForm, true, false));
        Button clear = new Button("clear", (e) -> {
           firstName.clear();
           lastName.clear();
           years.clear();
           months.clear();
           days.clear();
           email.clear();
           telephone.clear();
           typeDocument.clear();
           personalCode.clear();
           documentCode.clear();
           countryDocument.clear();
           dateExp.clear();
           travelClass.clear();
           addBaggage.clear();
           cardNumber.clear();
           cardOwner.clear();
        });
        HorizontalLayout buttons = new HorizontalLayout();
        buttons.add(close, clear);
        buttons.setFlexGrow(0);
        add(registration, buttons);


    }
    private boolean insertNewPassenger() {
        Passenger passenger = new Passenger();

        passenger.setFirstName(firstName.getValue());
        passenger.setLastName(lastName.getValue());
        passenger.setEMail(email.getValue());
        passenger.setTelephone(telephone.getValue());
        passenger.setDateBorn(LocalDate.of(
                years.getValue(),
                months.getValue().getValue(),
                days.getValue())
        );
        passenger.setTypeDocument(typeDocument.getValue());
        passenger.setPersonalCode(personalCode.getValue());
        passenger.setDocumentCode(documentCode.getValue());
        passenger.setCountryDoc(countryDocument.getValue());
        passenger.setDateExp(dateExp.getValue());
        passenger.setRegistration(false);

        Baggage baggage = new Baggage();
        baggage.setWeight(flight.getPricingPolicy().getFreeBaggage() + addBaggage.getValue());
        baggage.setAdditionalPrice(addBaggage.getValue() * flight.getPricingPolicy().getPriceKgBaggage());

        Ticket ticket = new Ticket();
        ticket.setFlight(flight);
        ticket.setPassenger(passenger);
        ticket.setBaggage(baggage);
        ticket.setTypeClass(travelClass.getValue());
        if(travelClass.getValue().equalsIgnoreCase("economy")) {
            ticket.setPrice(flight.getPricingPolicy().getPriceEconomy() + baggage.getAdditionalPrice());
            ticket.setSeat(getNextSeat("economy", flight.getIdFlight()));
        }
        else if(travelClass.getValue().equalsIgnoreCase("comfort")) {
            ticket.setPrice(flight.getPricingPolicy().getPriceComfort() + baggage.getAdditionalPrice());
            ticket.setSeat(getNextSeat("comfort", flight.getIdFlight()));
        }
        else {
            ticket.setPrice(flight.getPricingPolicy().getPriceBusiness() + baggage.getAdditionalPrice());
            ticket.setSeat(getNextSeat("business", flight.getIdFlight()));
        }
        ticket.setDateSale(LocalDateTime.now());

        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.save(ticket);
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        TicketPdf ticketPdf = new TicketPdf(ticket);
        if(ticketPdf.getStatus()) {
            new Email(ticketPdf.NAME_DOCUMENT,
                    "noreply@baeldung.com",
                    passenger.getEMail(),
                    "Flight Ticket",
                    "Hello, AirMoldova sends you the check-in information for your flight");
        }
        return true;
    }
    private boolean dialogConstructor() {
        String nameField = " ";
        if(firstName.isInvalid() || firstName.isEmpty()) nameField = "first name";
        else if(lastName.isInvalid() || lastName.isEmpty()) nameField = "last name";
        else if(years.isEmpty()) nameField = "year";
        else if(months.isEmpty()) nameField = "month";
        else if(days.isEmpty()) nameField = "day";
        else if(email.isInvalid() || email.isEmpty()) nameField = "email";
        else if(telephone.isInvalid() || telephone.isEmpty()) nameField = "telephone";
        else if(typeDocument.isEmpty()) nameField = "type document";
        else if(personalCode.isInvalid() || personalCode.isEmpty()) nameField = "personal code";
        else if(documentCode.isInvalid() || documentCode.isEmpty()) nameField = "document code";
        else if(countryDocument.isInvalid() || countryDocument.isEmpty()) nameField = "issuing country";
        else if(dateExp.isInvalid() || dateExp.isEmpty()) nameField = "date of expiry";
        else if(travelClass.isInvalid() || travelClass.isEmpty()) nameField = "travel class";
        else if(cardNumber.isInvalid() || cardNumber.isEmpty()) nameField = "card number";
        else if(cardOwner.isInvalid() || cardOwner.isEmpty()) nameField = "card owner";
        if(!nameField.equals(" ")) {
            Dialog errorDialog = new Dialog();
            Text errorMassage = new Text("Incorrect data entered in the \"" + nameField + "\" field.");
            Button closeDialog = new Button("close", (e) -> errorDialog.close());
            errorDialog.add(new VerticalLayout(errorMassage, closeDialog));
            errorDialog.open();
            return false;
        }
        else {
            return true;
        }
    }
    private int getNextSeat(String class_type, int id_flight) {
        try(Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            Query query = session.createSQLQuery("select get_next_seat('" + class_type + "', " + id_flight + ");");
            session.getTransaction().commit();
            if(query.getSingleResult() == null) return 0;
            int value = (Integer)query.getSingleResult();
            if(value == 0) {
                return 0;
            }
            else {
                return value;
            }
        }
    }
    private int getFreeSeats(String class_type, int id_flight) {
        try(Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            Query query = session.createSQLQuery("select get_free_seats('" + class_type + "', " + id_flight + ");");
            session.getTransaction().commit();
            if(query.getSingleResult() == null) return 0;
            int value = (Integer)query.getSingleResult();
            if(value == 0) {
                return 0;
            }
            else {
                return value;
            }
        }
    }
    private void insertInfoTravelClass() {
        List<String> classes = new ArrayList<>();
        classes.add("economy");
        classes.add("comfort");
        classes.add("business");
        travelClass.setItems(classes);
        travelClass.addThemeVariants(RadioGroupVariant.LUMO_VERTICAL);
        travelClass.setLabel("Travel class");
        if(flight == null) {
            return;
        }
        travelClass.setRenderer(new ComponentRenderer<>(typeClass -> {
            Text type = new Text(typeClass);
            int seats = getFreeSeats(typeClass, flight.getIdFlight());
            Text classDetails = new Text("");
            if(seats == 0) {
                classDetails.setText("NO SEATS");
            }
            else {
                if(typeClass.equalsIgnoreCase("economy")) {
                    classDetails.setText("Seats : " + seats + ".  Cost : " + flight.getPricingPolicy().getPriceEconomy() + " EUR");
                }
                else if(typeClass.equalsIgnoreCase("comfort")) {
                    classDetails.setText("Seats : " + seats + ".  Cost : " + flight.getPricingPolicy().getPriceComfort() + " EUR");
                }
                else {
                    classDetails.setText("Seats : " + seats + ".  Cost : " + flight.getPricingPolicy().getPriceBusiness() + " EUR");
                }
            }
            return new Div(new Div(type), new Div(classDetails));
        }));
        travelClass.setItemEnabledProvider(typeClass -> {
            int seats = getFreeSeats(typeClass, flight.getIdFlight());
            return !(seats == 0);
        });
        freeHandLuggage.setValue(flight.getPricingPolicy().getHandLuggage() + " KG");
        freeHandLuggage.setReadOnly(true);
        freeBaggage.setValue(flight.getPricingPolicy().getFreeBaggage() + " KG");
        freeBaggage.setReadOnly(true);
        addBaggage.setValue(0);
        addBaggage.setHasControls(true);
        addBaggage.setMin(0);
        addBaggage.setMax(20);
        addBaggage.setHelperText("Cost kg : " + flight.getPricingPolicy().getPriceKgBaggage() + " eur");
    }
    private void updateDays() {
        days.setValue(null);
        days.setEnabled(true);
        days.setItems(IntStream.range(1, months.getValue().maxLength() + 1).boxed().toList());
    }
    public void setVisible(Button button, boolean buttonVisible, boolean state) {
        this.closeForm = button;
        if(flight == null) return;
        closeForm.setVisible(buttonVisible);
        setVisible(state);
    }
    public void setFlight(Flight flight) {
        this.flight = flight;
        insertInfoTravelClass();
    }
}

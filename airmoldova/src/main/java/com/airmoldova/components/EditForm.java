package com.airmoldova.components;

import com.airmoldova.DAO.PassengerDAO;
import com.airmoldova.entity.Passenger;
import com.airmoldova.entity.TypeDocument;
import com.airmoldova.repository.TypeDocumentRepo;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyNotifier;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.converter.StringToDateConverter;

public class EditForm extends VerticalLayout {
    private PassengerDAO passengerDAO;
    private Passenger passenger;
    private final Binder<Passenger> binder = new Binder<>(Passenger.class);
    private final TextField firstName = new TextField("First name");
    private final TextField lastName = new TextField("Last name");
    private final TextField dateBorn = new TextField("Date born");
    ComboBox<TypeDocument> typeDocument;

    private final TextField personalCode = new TextField("Personal code");
    private final TextField documentCode = new TextField("Document code");
    private final TextField country = new TextField("Issuing country");
    private final TextField dateExp = new TextField("Date of expiry");
    private final Button save = new Button("Save", VaadinIcon.CHECK.create());
    private final Button close = new Button("Close", VaadinIcon.CLOSE.create());
    private ChangerHandler changerHandler;

    public void setChangerHandler(ChangerHandler changerHandler) {
        this.changerHandler = changerHandler;
    }
    public interface ChangerHandler {
        void onChange();
    }
    public EditForm(PassengerDAO passengerDAO, TypeDocumentRepo typeDocumentRepo) {
        this.passengerDAO = passengerDAO;

        typeDocument = new ComboBox<TypeDocument>("Type document");
        typeDocument.setItems(typeDocumentRepo.findAll());
        typeDocument.setItemLabelGenerator(TypeDocument::getTypeDocument);

        firstName.setPattern("^[A-Z]{1}[a-z]{1,23}$\\w+");
        lastName.setPattern("^[A-Z]{1}[a-z]{1,23}$\\w+");
        dateBorn.setPattern("^((19[0-9]{2})|(20[0-2]{1}[0-9]{1}))-((0[1-9]{1})|(1[0-2]{1}))-(([0-2]{1}[0-9]{1})|(3[0-1]{1}))$");
        personalCode.setPattern("[0-9]{12}");
        documentCode.setPattern("^[A-Z]{1}[0-9]{8}");
        country.setPattern("^[A-Z]{1}[a-z]{1,23}$\\w+");
        dateExp.setPattern("^202[2-9]{1}-((0[1-9]{1})|(1[0-2]{1}))-(([0-2]{1}[0-9]{1})|(3[0-1]{1}))$");

        binder.bind(firstName, Passenger::getFirstName, Passenger::setFirstName);
        binder.bind(lastName, Passenger::getLastName, Passenger::setLastName);
        binder.bind(dateBorn, Passenger::getDateBornFormatDate, Passenger::setDateBornFormatDate);
        binder.bind(typeDocument, Passenger::getTypeDocument, Passenger::setTypeDocument);
        binder.bind(personalCode, Passenger::getPersonalCode, Passenger::setPersonalCode);
        binder.bind(documentCode, Passenger::getDocumentCode, Passenger::setDocumentCode);
        binder.bind(country, Passenger::getCountryDoc, Passenger::setCountryDoc);
        binder.bind(dateExp, Passenger::getDateExpFormatDate, Passenger::setDateExpFormatDate);

        HorizontalLayout date1 = new HorizontalLayout();
        date1.add(firstName, lastName, dateBorn, typeDocument);
        HorizontalLayout date2 = new HorizontalLayout();
        date2.add(personalCode, documentCode, country, dateExp);
        HorizontalLayout buttons = new HorizontalLayout();
        buttons.add(save, close);
        add(date1, date2, buttons);

        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
        close.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        save.addClickListener(e -> save());
        close.addClickListener(e -> {
            setVisible(false);
        });
        setVisible(false);

    }
    private void save() {
        String nameField = " ";
        if(firstName.isInvalid() || firstName.isEmpty()) nameField = "first name";
        else if(lastName.isInvalid() || lastName.isEmpty()) nameField = "last name";
        else if(typeDocument.isEmpty()) nameField = "type document";
        else if(personalCode.isInvalid() || personalCode.isEmpty()) nameField = "personal code";
        else if(documentCode.isInvalid() || documentCode.isEmpty()) nameField = "document code";
        else if(country.isInvalid() || country.isEmpty()) nameField = "issuing country";
        else if(dateExp.isInvalid() || dateExp.isEmpty()) nameField = "date of expiry";
        if(!nameField.equals(" ")) {
            Dialog errorDialog = new Dialog();
            Text errorMassage = new Text("Incorrect data entered in the \"" + nameField + "\" field.");
            Button closeDialog = new Button("close", (e) -> errorDialog.close());
            errorDialog.add(new VerticalLayout(errorMassage, closeDialog));
            errorDialog.open();
            return;
        }
        passengerDAO.update(passenger);
        changerHandler.onChange();
        setVisible(false);
        Notification notification = Notification.show("Successfully changed", 2000, Notification.Position.TOP_END);
        notification.addThemeVariants(NotificationVariant.LUMO_PRIMARY, NotificationVariant.LUMO_SUCCESS);
    }
    public void editPassenger(Passenger passenger) {
        if(passenger == null) {
            setVisible(false);
            return;
        }
        this.passenger = passenger;
        binder.setBean(passenger);
        setVisible(true);
    }
}

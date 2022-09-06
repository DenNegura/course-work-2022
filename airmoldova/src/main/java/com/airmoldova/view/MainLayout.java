package com.airmoldova.view;

import com.airmoldova.security.SecurityService;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.HighlightConditions;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import org.springframework.security.core.GrantedAuthority;
public class MainLayout extends AppLayout {
    private final SecurityService securityService;
    public MainLayout(SecurityService securityService) {
        this.securityService = securityService;
        createHeader();
        createDrawer();
    }
    private void createHeader() {
        H1 logo = new H1("AIR MOLDOVA");
        logo.addClassName("text-l");
       // logo.addClassNames("text-l", "m-m");

        Button logout = new Button("Log out", e -> securityService.logout());
        logout.setVisible(false);

        RouterLink loginLink = new RouterLink("Login", LoginView.class);
        Button login = new Button(loginLink);

        if(securityService.getAuthenticatedUser() != null) {
            logout.setVisible(true);
            login.setVisible(false);
        } else {
            logout.setVisible(false);
            login.setVisible(true);
        }
        HorizontalLayout header = new HorizontalLayout(new DrawerToggle(), logo, login, logout);
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.expand(logo);
        header.setWidth("100%");
        header.addClassNames("py-0", "px-m");
        addToNavbar(header);
    }

    private void createDrawer() {
        RouterLink listLink = new RouterLink("Flights", FlightsView.class);
        listLink.setHighlightCondition(HighlightConditions.sameLocation());
        addToDrawer(new VerticalLayout(listLink, new RouterLink("Main page", MainView.class)));
        if( securityService.getAuthenticatedUser() != null) {
            for (GrantedAuthority authority : securityService.getAuthenticatedUser().getAuthorities()) {
                if (authority.getAuthority().equals("ROLE_OPERATOR")) {
                    addToDrawer(new VerticalLayout(listLink, new RouterLink("Reception", ReceptionView.class)));
                    break;
                }
                if (authority.getAuthority().equals("ROLE_ADMINISTRATOR")) {
                    addToDrawer(new VerticalLayout(listLink, new RouterLink("Administration", AdministrationView.class)));
                    break;
                }
            }
        }
    }

}

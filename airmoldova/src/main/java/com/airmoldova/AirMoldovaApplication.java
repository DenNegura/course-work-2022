package com.airmoldova;

import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.theme.NoTheme;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication(exclude = ErrorMvcAutoConfiguration.class)
@Theme(value = "airmoldova")
//@Theme(themeClass = Lumo.class, variant = Lumo.LIGHT)
//@CssImport("./frontend/themes/main-layoult.css")
public class AirMoldovaApplication implements AppShellConfigurator {

	public static void main(String[] args) {
		SpringApplication.run(AirMoldovaApplication.class, args);
	}

}

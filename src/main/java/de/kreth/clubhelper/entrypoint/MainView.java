package de.kreth.clubhelper.entrypoint;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.PWA;

@Route
@PWA(name = "Clubhelper Übersicht", shortName = "Übersicht", description = "Dies ist der Einstiegspunkt und Übersicht über alle Clubhelper Apps.", enableInstallPrompt = false)
@CssImport("./styles/shared-styles.css")
@CssImport(value = "./styles/vaadin-text-field-styles.css", themeFor = "vaadin-text-field")
@CssImport(value = "./styles/vaadin-text-field-styles.css", themeFor = "vaadin-text-field")
public class MainView extends Div {

    private static final long serialVersionUID = 1L;
    private final List<ClubhelperApp> apps = new ArrayList<>();
    private final AppService service;

    public MainView(@Autowired AppService service) {
	this.service = service;
	this.setSizeFull();
	doRefresh();
    }

    public void doRefresh() {
	this.removeAll();
	apps.clear();
	apps.addAll(service.getAllRegisteredApps());
	apps.stream().map(ClubhelperAppButton::new).forEach(MainView.this::add);
    }
}

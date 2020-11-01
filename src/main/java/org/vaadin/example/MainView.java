package org.vaadin.example;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.PWA;

@Route
@PWA(name = "Vaadin Application", shortName = "Vaadin App", description = "This is an example Vaadin application.", enableInstallPrompt = false)
@CssImport("./styles/shared-styles.css")
@CssImport(value = "./styles/vaadin-text-field-styles.css", themeFor = "vaadin-text-field")
public class MainView extends Div {

	private static final long serialVersionUID = 1L;
	private List<ClubhelperApp> apps;

	public MainView(@Autowired AppService service) {
		apps = service.getAllRegisteredApps();
		apps.stream().map(ClubhelperAppButton::new).forEach(MainView.this::add);
	}

}

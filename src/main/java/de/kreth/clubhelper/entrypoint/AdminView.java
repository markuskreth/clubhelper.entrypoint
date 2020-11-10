package de.kreth.clubhelper.entrypoint;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.router.Route;

@Route("admin")
public class AdminView extends VerticalLayout {

    private static final long serialVersionUID = 3103369226745336926L;
    private List<ClubhelperApp> apps;
    private AppService service;
    private Grid<ClubhelperApp> grid;

    public AdminView(@Autowired AppService service) {
	if (service.isEditable()) {
	    this.service = service;
	    apps = service.getAllRegisteredApps();
	    createUi();
	} else {
	    add(new H1(service.getClass() + " not supported"));
	}
    }

    private void createUi() {
	add(new H1("Konfiguration der Apps"));
	grid = new Grid<>();
	grid.setDataProvider(DataProvider.ofCollection(apps));
	grid.addColumn(ClubhelperApp::getName).setHeader("Bezeichnung");
	grid.addColumn(ClubhelperApp::getUrl).setHeader("Adresse");
	grid.addComponentColumn(this::deleteComponent);
	Button add = new Button(VaadinIcon.PLUS.create(), ev -> {
	    AppEditor editor = new AppEditor(new ClubhelperApp("App Bezeichnung", "https://"));
	    editor.open();
	});
	Button store = new Button("Speichern", ev -> doStore());
	add(new HorizontalLayout(add, store));

    }

    private void doStore() {
	service.update(apps);
    }

    private Component deleteComponent(ClubhelperApp app) {
	Button b = new Button(VaadinIcon.DEL.create(), ev -> {
	    apps.remove(app);
	    grid.getDataProvider().refreshAll();
	});

	return b;
    }

    class AppEditor extends Dialog {

	private static final long serialVersionUID = -2676053237413822608L;

	private final TextField name;
	private final TextField uri;
	private final Binder<ClubhelperApp> binder;
	private final int originalIndex;

	public AppEditor(ClubhelperApp app) {
	    this.name = new TextField();
	    this.uri = new TextField();

	    originalIndex = apps.indexOf(app);
	    FormLayout layout = new FormLayout();
	    layout.addFormItem(name, "App Bezeichnung");
	    layout.addFormItem(uri, "App Adresse");

	    binder = new Binder<>();
	    binder.forField(name).asRequired().withValidator((value, context) -> {
		if (value != null && value.length() > 3) {
		    return ValidationResult.ok();
		} else {
		    return ValidationResult.error("Appname muss mindestens 4 Buchstaben haben");
		}
	    });
	    binder.forField(uri).asRequired().withValidator((value, context) -> {
		try {
		    new URL(value);
		    return ValidationResult.ok();
		} catch (MalformedURLException e) {
		    return ValidationResult.error("Die Adresse muss eine gültige http Adresse sein.");
		}
	    });
	    binder.readBean(app);

	    Button okButton = new Button("Speichern", this::onOkClick);
	    Button cancelButton = new Button("Abbrechen", ev -> AppEditor.this.close());

	    HorizontalLayout buttonLayout = new HorizontalLayout(okButton, cancelButton);
	    layout.add(buttonLayout);
	    layout.setColspan(buttonLayout, 2);

	    add(layout);
	}

	private void onOkClick(ClickEvent<Button> ev) {

	    if (binder.validate().isOk()) {
		ClubhelperApp app = new ClubhelperApp(name.getValue(), uri.getValue());
		if (originalIndex >= 0) {
		    apps.remove(originalIndex);
		    apps.add(originalIndex, app);
		} else {
		    apps.add(app);
		}
		grid.getDataProvider().refreshAll();
	    }
	}
    }

}

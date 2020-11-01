package de.kreth.clubhelper.entrypoint;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.page.Page;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.i18n.I18NProvider;

public class ClubhelperAppButton extends Button {

    private static final long serialVersionUID = 1L;
    private final Logger logger;
    private final ClubhelperApp app;

    public ClubhelperAppButton(ClubhelperApp app) {
	super(app.getName());
	setWidth("150px");
	setHeight("150px");
	Style style = getStyle();
	style.set("margin", "10px");
	this.addClickListener(this::onClick);
	this.app = Objects.requireNonNull(app);
	this.logger = LoggerFactory.getLogger(getClass());
	I18NProvider p;
    }

    private void onClick(ClickEvent<Button> ev) {
	getUI().ifPresent(ui -> {
	    Page page = ui.getPage();
	    logger.info("opening {} with uri {}", app.getName(), app.getUrl());
//	    page.open(app.getUrl(), "_blank");
	    String js = "window.open(\"" + app.getUrl()
		    + "\", \"_self\");";
	    logger.debug("executing js {}", js);
	    page.executeJs(js);
	});
    }

}

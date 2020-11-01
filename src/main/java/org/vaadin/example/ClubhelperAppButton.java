package org.vaadin.example;

import com.vaadin.flow.component.button.Button;

public class ClubhelperAppButton extends Button {

	private ClubhelperApp app;

	public ClubhelperAppButton(ClubhelperApp app) {
		super(app.getName());
		setWidth("150px");
		setHeight("150px");
	}

}

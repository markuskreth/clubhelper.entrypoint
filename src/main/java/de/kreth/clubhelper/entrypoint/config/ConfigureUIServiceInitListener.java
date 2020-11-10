package de.kreth.clubhelper.entrypoint.config;

import org.springframework.stereotype.Component;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinServiceInitListener;

import de.kreth.clubhelper.entrypoint.AdminView;

@Component
public class ConfigureUIServiceInitListener implements VaadinServiceInitListener {

    private static final long serialVersionUID = 1L;

    @Override
    public void serviceInit(ServiceInitEvent event) {
	event.getSource().addUIInitListener(uiEvent -> {
	    final UI ui = uiEvent.getUI();
	    ui.addBeforeEnterListener(this::beforeEnter); //
	});
    }

    /**
     * Reroutes the user if (s)he is not authorized to access the view.
     *
     * @param event before navigation event with event details
     */
    private void beforeEnter(BeforeEnterEvent event) {
	Class<?> navigationTarget = event.getNavigationTarget();

	if (isSecureAndNotAuthentificated(navigationTarget)) {
	    event.rerouteTo("");
	}
    }

    private boolean isSecureAndNotAuthentificated(Class<?> navigationTarget) {
	boolean userLoggedIn = SecurityUtils.isUserLoggedIn();
	return AdminView.class.equals(navigationTarget)
		&& !userLoggedIn;
    }
}

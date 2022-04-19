package de.kreth.clubhelper.entrypoint;

import java.util.ArrayList;
import java.util.List;

import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.representations.AccessToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
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

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null && authentication.isAuthenticated()) {

			Object principal = authentication.getPrincipal();
			if (principal instanceof KeycloakPrincipal) {
				KeycloakPrincipal<KeycloakSecurityContext> keycloak = (KeycloakPrincipal<KeycloakSecurityContext>) principal;
				KeycloakSecurityContext context = keycloak.getKeycloakSecurityContext();
				AccessToken token = context.getToken();
				StringBuilder text = new StringBuilder("Angemeldet: ");
				text.append(token.getGivenName()).append(" ")
				.append(token.getFamilyName()).append(" (")
				.append(token.getEmail()).append(")");
				add(new H2(text.toString()));
			} else {
				add(new H2("Angemeldet: " + authentication.getName()));
			}
		}
		apps.clear();
		apps.addAll(service.getAllRegisteredApps());
		apps.stream().map(ClubhelperAppButton::new).forEach(MainView.this::add);
	}
}

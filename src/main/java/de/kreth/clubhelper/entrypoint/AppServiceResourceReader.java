package de.kreth.clubhelper.entrypoint;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.stream.Collectors;

public class AppServiceResourceReader implements AppService {

	@Override
	public List<ClubhelperApp> getAllRegisteredApps() {
		try (BufferedReader in = new BufferedReader(
				new InputStreamReader(getClass().getResourceAsStream("/clubhelper.apps.properties")))) {
			return in.lines().map(this::lineToApp).collect(Collectors.toList());
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	private ClubhelperApp lineToApp(String line) {
		String[] values = line.split(";");
		return new ClubhelperApp(values[1], values[0]);
	}
}

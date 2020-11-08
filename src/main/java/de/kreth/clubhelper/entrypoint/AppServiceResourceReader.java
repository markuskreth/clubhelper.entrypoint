package de.kreth.clubhelper.entrypoint;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(value = "docker.client.enabled", havingValue = "false", matchIfMissing = true)
public class AppServiceResourceReader implements AppService {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    @Value("${resource:./clubhelper.apps.properties}")
    private String propertyPath;

    @Override
    public List<ClubhelperApp> getAllRegisteredApps() {

	File file = new File(propertyPath);
	logger.info("Loading resource: {}", file.getAbsolutePath());
	try (BufferedReader in = new BufferedReader(
		new FileReader(file))) {

	    return in.lines()
		    .filter(l -> !l.isBlank() && !l.startsWith("#") && !l.startsWith(";"))
		    .map(this::lineToApp)
		    .collect(Collectors.toList());

	} catch (IOException e) {
	    throw new UncheckedIOException(e);
	}
    }

    private ClubhelperApp lineToApp(String line) {
	String[] values = line.split(";");
	return new ClubhelperApp(values[1], values[0]);
    }
}

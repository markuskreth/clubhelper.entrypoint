package de.kreth.clubhelper.entrypoint;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.ContainerConfig;
import com.github.dockerjava.api.model.ContainerPort;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;

@Service
public class AppServiceDockerClient implements AppService {

    private final Logger logger;
    private final DockerClient dockerClient;

    public AppServiceDockerClient() {
	DefaultDockerClientConfig.Builder config = DefaultDockerClientConfig.createDefaultConfigBuilder();
	dockerClient = DockerClientBuilder.getInstance(config.build()).build();
	logger = LoggerFactory.getLogger(getClass());
    }

    @Override
    public List<ClubhelperApp> getAllRegisteredApps() {
	List<Container> containers = dockerClient.listContainersCmd().exec();
	List<ClubhelperApp> collect = containers.stream()
		.filter(this::filterClubhelperContainer)
		.map(this::createApp)
		.collect(Collectors.toList());
	logger.info("found apps: {}", collect.size());
	return collect;
    }

    ClubhelperApp createApp(Container c) {
	String title = extractName(c);
	InspectContainerResponse inspect = dockerClient.inspectContainerCmd(c.getId()).exec();

	String url = extractVirtualHost(c, inspect);

	return new ClubhelperApp(url, title);
    }

    private String extractVirtualHost(Container c, InspectContainerResponse inspect) {

	ContainerConfig config = inspect.getConfig();
	String[] env = config.getEnv();
	for (String string : env) {
	    if (string.startsWith("VIRTUAL_HOST")) {
		return string.substring("VIRTUAL_HOST".length() + 1).trim();
	    }
	}
	String url = "https://" + getHost();
	ContainerPort[] ports = c.getPorts();
	if (ports.length > 0) {
	    url += ":" + ports[0].getPublicPort();
	}
	return url;
    }

    private String getHost() {
	String host = "localhost";
	try (final DatagramSocket socket = new DatagramSocket()) {
	    socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
	    host = socket.getLocalAddress().getHostAddress();
	} catch (UnknownHostException | SocketException e) {
	    e.printStackTrace();
	}
	return host;
    }

    private String extractName(Container c) {
	if (c.getLabels().containsKey("title")) {
	    return c.getLabels().get("title");
	}

	String[] names = c.getNames();
	if (names.length > 0) {
	    return names[0];
	}
	return null;
    }

    boolean filterClubhelperContainer(Container c) {
	String[] names = c.getNames();
	for (String name : names) {
	    if (name.toLowerCase().contains("clubhelper")) {
		return true;
	    }
	}
	return false;
    }

}

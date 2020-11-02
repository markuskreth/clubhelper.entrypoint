package de.kreth.clubhelper.entrypoint;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.function.Function;

import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.ContainerConfig;
import com.github.dockerjava.api.model.ContainerPort;

public class ContainerWrapper {

    private static final String ENV_VIRTUAL_HOST = "VIRTUAL_HOST";
    private static final String LABEL_TITLE = "TITLE";
    private static final String LABEL_APP_URL = "APP_URL";

    private static final String host = getHost();

    private final Container container;

    public ContainerWrapper(Container container) {
	super();
	this.container = container;
    }

    public String getTitle() {
	return container.getLabels().get(LABEL_TITLE);
    }

    public boolean isClubhelper() {
	return container.getLabels().containsKey(LABEL_TITLE)
		&& container.getLabels().containsKey(LABEL_APP_URL);
    }

    public String getUrl(Function<String, InspectContainerResponse> accessor) {

	if (container.getLabels().containsKey(LABEL_APP_URL)) {
	    return container.getLabels().get(LABEL_APP_URL);
	}

	InspectContainerResponse inspect = accessor.apply(getId());
	ContainerConfig config = inspect.getConfig();
	String[] env = config.getEnv();
	for (String string : env) {
	    if (string.startsWith(ENV_VIRTUAL_HOST)) {
		return "http://" + string.substring(ENV_VIRTUAL_HOST.length() + 1).trim();
	    }
	}

	String url;
	if ("localhost".equals(host)) {
	    url = "http://" + host;
	} else {
	    url = "https://" + host;
	}
	ContainerPort[] ports = container.getPorts();
	if (ports.length > 0) {
	    url += ":" + ports[0].getPublicPort();
	}
	return url;
    }

    private static String getHost() {
	String host = "localhost";
	try (final DatagramSocket socket = new DatagramSocket()) {
	    socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
	    host = socket.getLocalAddress().getHostAddress();
	} catch (UnknownHostException | SocketException e) {
	    e.printStackTrace();
	}
	return host;
    }

    public String getId() {
	return container.getId();
    }

}

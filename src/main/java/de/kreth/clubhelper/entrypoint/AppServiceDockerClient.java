package de.kreth.clubhelper.entrypoint;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DefaultDockerClientConfig.Builder;
import com.github.dockerjava.core.DockerClientBuilder;

@Service
@ConditionalOnProperty(value = "docker.client.enabled", havingValue = "true", matchIfMissing = false)
public class AppServiceDockerClient implements AppService {

    private final Logger logger;
    private final DockerClient dockerClient;

    @Value("${host:'unix://var/run/docker.sock'}")
    private String host;

    public AppServiceDockerClient() {
	logger = LoggerFactory.getLogger(getClass());
	logger.info("Using docker host: {}", host);
	Builder configBuilder = DefaultDockerClientConfig.createDefaultConfigBuilder();
	if (host != null && !host.trim().isEmpty()) {
	    configBuilder.withDockerHost(host);
	}
	DefaultDockerClientConfig config = configBuilder.build();
	dockerClient = DockerClientBuilder.getInstance(config).build();

    }

    @Override
    public List<ClubhelperApp> getAllRegisteredApps() {
	List<Container> containers = dockerClient.listContainersCmd().exec();
	List<ClubhelperApp> collect = containers.stream()
		.map(ContainerWrapper::new)
		.filter(ContainerWrapper::isClubhelper)
		.map(this::createApp)
		.collect(Collectors.toList());
	logger.info("found apps: {}", collect.size());
	return collect;
    }

    ClubhelperApp createApp(ContainerWrapper c) {
	Function<String, InspectContainerResponse> inspectAccessor = id -> dockerClient.inspectContainerCmd(id).exec();
	return new ClubhelperApp(c.getUrl(inspectAccessor), c.getTitle());
    }

}

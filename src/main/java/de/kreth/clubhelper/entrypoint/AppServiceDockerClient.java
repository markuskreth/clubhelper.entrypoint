package de.kreth.clubhelper.entrypoint;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.model.Container;
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

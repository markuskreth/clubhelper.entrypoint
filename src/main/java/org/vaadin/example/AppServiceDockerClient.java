package org.vaadin.example;

import java.util.List;

import org.springframework.stereotype.Service;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;

@Service
public class AppServiceDockerClient implements AppService {

	private DockerClient dockerClient;

	public AppServiceDockerClient() {
		DefaultDockerClientConfig.Builder config = DefaultDockerClientConfig.createDefaultConfigBuilder();
		dockerClient = DockerClientBuilder.getInstance(config.build()).build();

	}

	@Override
	public List<ClubhelperApp> getAllRegisteredApps() {
		List<Container> containers = dockerClient.listContainersCmd().exec();
		return null;
	}

}

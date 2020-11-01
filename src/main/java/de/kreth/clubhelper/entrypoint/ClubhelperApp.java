package de.kreth.clubhelper.entrypoint;

public class ClubhelperApp {

	private final String url;
	private final String name;

	public ClubhelperApp(String url, String name) {
		super();
		this.url = url;
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public String getName() {
		return name;
	}

}

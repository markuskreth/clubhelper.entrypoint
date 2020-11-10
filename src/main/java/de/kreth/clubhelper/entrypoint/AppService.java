package de.kreth.clubhelper.entrypoint;

import java.util.List;

public interface AppService {

    List<ClubhelperApp> getAllRegisteredApps();

    boolean isEditable();

    void update(List<ClubhelperApp> apps);

}
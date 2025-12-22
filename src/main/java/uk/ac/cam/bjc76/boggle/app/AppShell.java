package uk.ac.cam.bjc76.boggle.app;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.server.PWA;

@Push
@PWA(name = "Boggle Game", shortName = "Boggle")
public class AppShell implements AppShellConfigurator {

}
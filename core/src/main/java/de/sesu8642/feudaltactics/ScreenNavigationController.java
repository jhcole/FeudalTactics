// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics;

import java.util.stream.Stream;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import de.sesu8642.feudaltactics.editor.EditorInputHandler;
import de.sesu8642.feudaltactics.events.ScreenTransitionTriggerEvent;
import de.sesu8642.feudaltactics.ingame.ui.IngameScreen;
import de.sesu8642.feudaltactics.ingame.ui.IngameScreenEventHandler;
import de.sesu8642.feudaltactics.input.LocalIngameInputHandler;
import de.sesu8642.feudaltactics.menu.about.dagger.AboutScreen;
import de.sesu8642.feudaltactics.menu.changelog.dagger.ChangelogScreen;
import de.sesu8642.feudaltactics.menu.common.ui.GameScreen;
import de.sesu8642.feudaltactics.menu.crashreporting.dagger.CrashReportScreenInMainMenu;
import de.sesu8642.feudaltactics.menu.crashreporting.dagger.CrashReportScreenOnStartup;
import de.sesu8642.feudaltactics.menu.crashreporting.ui.CrashReportScreen;
import de.sesu8642.feudaltactics.menu.information.dagger.DependencyLicensesScreen;
import de.sesu8642.feudaltactics.menu.information.dagger.InformationMenuScreen;
import de.sesu8642.feudaltactics.menu.preferences.ui.PreferencesScreen;
import de.sesu8642.feudaltactics.menu.preferences.ui.PreferencesScreenEventHandler;
import de.sesu8642.feudaltactics.menu.splashscreen.ui.SplashScreen;
import de.sesu8642.feudaltactics.menu.tutorial.dagger.TutorialScreen;

/**
 * Controller for navigating between screens.
 */
@Singleton
public class ScreenNavigationController {

	private EventBus eventBus;
	private LocalIngameInputHandler localIngameInputHandler;
	private EditorInputHandler editorInputHandler;
	private SplashScreen splashScreen;
	private IngameScreen ingameScreen;
	private GameScreen mainMenuScreen;
	private GameScreen tutorialScreen;
	private GameScreen aboutScreen;
	private GameScreen preferencesScreen;
	private GameScreen informationMenuScreen;
	private GameScreen dependencyLicensesScreen;
	private GameScreen changelogScreen;
	private CrashReportScreen crashReportScreenInMainMenu;
	private CrashReportScreen crashReportScreenOnStartup;
	private de.sesu8642.feudaltactics.lib.ingame.GameControllerEventHandler gameLogicEventHandler;
	private de.sesu8642.feudaltactics.editor.EventHandler editorEventHandler;
	private de.sesu8642.feudaltactics.ingame.IngameRendererEventHandler rendererEventHandler;
	private IngameScreenEventHandler ingameScreenEventHandler;
	private PreferencesScreenEventHandler preferencesScreenEventHandler;

	/**
	 * Constructor.
	 * 
	 * @param eventBus                      event bus to register and unregister
	 *                                      to/with
	 * @param localIngameInputHandler       local ingame input handler
	 * @param gameLogicEventHandler         game logic event handler
	 * @param splashScreen                  splash screen
	 * @param ingameScreen                  ingame screen
	 * @param mainMenuScreen                main menu screen
	 * @param rendererEventHandler          renderer event handler
	 * @param ingameScreenEventHandler      ingame screen event handler
	 * @param preferencesScreenEventHandler preferences screen event handler
	 */
	@Inject
	public ScreenNavigationController(EventBus eventBus, LocalIngameInputHandler localIngameInputHandler,
			EditorInputHandler editorInputHandler, SplashScreen splashScreen, IngameScreen ingameScreen,
			de.sesu8642.feudaltactics.menu.mainmenu.ui.MainMenuScreen mainMenuScreen,
			@TutorialScreen GameScreen tutorialScreen, @AboutScreen GameScreen aboutScreen,
			PreferencesScreen preferencesScreen, @InformationMenuScreen GameScreen informationMenuScreen,
			@DependencyLicensesScreen GameScreen dependencyLicensesScreen, @ChangelogScreen GameScreen changelogScreen,
			@CrashReportScreenInMainMenu CrashReportScreen crashReportScreenInMainMenu,
			@CrashReportScreenOnStartup CrashReportScreen crashReportScreenOnStartup,
			de.sesu8642.feudaltactics.lib.ingame.GameControllerEventHandler gameLogicEventHandler,
			de.sesu8642.feudaltactics.editor.EventHandler editorEventHandler,
			de.sesu8642.feudaltactics.ingame.IngameRendererEventHandler rendererEventHandler,
			IngameScreenEventHandler ingameScreenEventHandler,
			PreferencesScreenEventHandler preferencesScreenEventHandler) {
		this.eventBus = eventBus;
		this.localIngameInputHandler = localIngameInputHandler;
		this.editorInputHandler = editorInputHandler;
		this.splashScreen = splashScreen;
		this.ingameScreen = ingameScreen;
		this.mainMenuScreen = mainMenuScreen;
		this.tutorialScreen = tutorialScreen;
		this.aboutScreen = aboutScreen;
		this.preferencesScreen = preferencesScreen;
		this.informationMenuScreen = informationMenuScreen;
		this.dependencyLicensesScreen = dependencyLicensesScreen;
		this.changelogScreen = changelogScreen;
		this.crashReportScreenInMainMenu = crashReportScreenInMainMenu;
		this.crashReportScreenOnStartup = crashReportScreenOnStartup;
		this.gameLogicEventHandler = gameLogicEventHandler;
		this.editorEventHandler = editorEventHandler;
		this.rendererEventHandler = rendererEventHandler;
		this.ingameScreenEventHandler = ingameScreenEventHandler;
		this.preferencesScreenEventHandler = preferencesScreenEventHandler;
	}

	private void unregisterAllEventHandlers() {
		Stream.of(localIngameInputHandler, gameLogicEventHandler, ingameScreenEventHandler, rendererEventHandler,
				preferencesScreenEventHandler).forEach(object -> {
					try {
						eventBus.unregister(object);
					} catch (IllegalArgumentException e) {
						// noop: expected; not all of the objects were registered in the first place
					}
				});
	}

	/**
	 * Event handler for Screen transition events.
	 * 
	 * @param event event to handle
	 */
	@Subscribe
	public void handleScreenTransitionTrigger(ScreenTransitionTriggerEvent event) {
		switch (event.getTransitionTarget()) {
		case SPLASH_SCREEN:
			changeScreen(splashScreen);
			break;
		case MAIN_MENU_SCREEN:
			changeScreen(mainMenuScreen);
			break;
		case INGAME_SCREEN:
			transitionToIngameScreen();
			break;
		case EDITOR_SCREEN:
			transitionToEditorScreen();
			break;
		case TUTORIAL_SCREEN:
			changeScreen(tutorialScreen);
			break;
		case ABOUT_SCREEN:
			changeScreen(aboutScreen);
			break;
		case PREFERENCES_SCREEN:
			transitionToPreferencesScreen();
			break;
		case INFORMATION_MENU_SCREEN:
			changeScreen(informationMenuScreen);
			break;
		case DEPENDENCY_LICENSES_SCREEN:
			changeScreen(dependencyLicensesScreen);
			break;
		case CHANGELOG_SCREEN:
			changeScreen(changelogScreen);
			break;
		case CRASH_REPORT_SCREEN_IN_MAIN_MENU:
			changeScreen(crashReportScreenInMainMenu);
			break;
		case CRASH_REPORT_SCREEN_ON_STARTUP:
			changeScreen(crashReportScreenOnStartup);
			break;
		default:
			throw new AssertionError("Unimplemented transition target: " + event.getTransitionTarget());
		}
	}

	private void transitionToIngameScreen() {
		changeScreen(ingameScreen);
		Stream.of(localIngameInputHandler, gameLogicEventHandler, ingameScreenEventHandler, rendererEventHandler)
				.forEach(object -> eventBus.register(object));
	}

	private void transitionToEditorScreen() {
		changeScreen(ingameScreen);
		Stream.of(editorInputHandler, editorEventHandler, ingameScreen, rendererEventHandler)
				.forEach(object -> eventBus.register(object));
	}

	private void changeScreen(Screen screen) {
		unregisterAllEventHandlers();
		// changing the screen needs to happen in the UI thread, otherwise there can be
		// some exception in native code
		Gdx.app.postRunnable(() -> FeudalTactics.game.setScreen(screen));
	}

	private void transitionToPreferencesScreen() {
		changeScreen(preferencesScreen);
		eventBus.register(preferencesScreenEventHandler);
	}

}

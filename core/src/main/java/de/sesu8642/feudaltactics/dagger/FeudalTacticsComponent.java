// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.dagger;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

import javax.inject.Singleton;

import dagger.Component;
import de.sesu8642.feudaltactics.GameInitializer;
import de.sesu8642.feudaltactics.editor.dagger.EditorDaggerModule;
import de.sesu8642.feudaltactics.ingame.dagger.IngameDaggerModule;
import de.sesu8642.feudaltactics.menu.about.dagger.AboutDaggerModule;
import de.sesu8642.feudaltactics.menu.changelog.dagger.ChangelogDaggerModule;
import de.sesu8642.feudaltactics.menu.common.dagger.MenuDaggerModule;
import de.sesu8642.feudaltactics.menu.crashreporting.GameCrasher;
import de.sesu8642.feudaltactics.menu.crashreporting.dagger.CrashReportingDaggerModule;
import de.sesu8642.feudaltactics.menu.information.dagger.InformationMenuDaggerModule;
import de.sesu8642.feudaltactics.menu.mainmenu.dagger.MainMenuDaggerModule;
import de.sesu8642.feudaltactics.menu.preferences.dagger.GamePrefsDaggerModule;
import de.sesu8642.feudaltactics.menu.splashscreen.dagger.SplashScreenDaggerModule;
import de.sesu8642.feudaltactics.menu.tutorial.dagger.TutorialDaggerModule;
import de.sesu8642.feudaltactics.renderer.dagger.RendererDaggerModule;

/** Dagger component. **/
@Component(modules = { MainDaggerModule.class, ConfigDaggerModule.class, CrashReportingDaggerModule.class,
		EditorDaggerModule.class, IngameDaggerModule.class, MenuDaggerModule.class, AboutDaggerModule.class,
		ChangelogDaggerModule.class, InformationMenuDaggerModule.class, MainMenuDaggerModule.class,
		GamePrefsDaggerModule.class, SplashScreenDaggerModule.class, TutorialDaggerModule.class,
		RendererDaggerModule.class })
@Singleton
public interface FeudalTacticsComponent {

	GameInitializer getGameInitializer();

	ExecutorService getBotAiExecutor();

	ScheduledExecutorService getCopyButtonExecutor();

	GameCrasher getGameCrasher();

}

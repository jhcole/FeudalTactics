// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.splashscreen.ui;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.google.common.eventbus.EventBus;

import de.sesu8642.feudaltactics.events.ScreenTransitionTriggerEvent;
import de.sesu8642.feudaltactics.events.ScreenTransitionTriggerEvent.ScreenTransitionTarget;
import de.sesu8642.feudaltactics.menu.common.dagger.MenuCamera;
import de.sesu8642.feudaltactics.menu.common.dagger.MenuViewport;
import de.sesu8642.feudaltactics.menu.common.ui.GameScreen;
import de.sesu8642.feudaltactics.menu.common.ui.ResizableResettableStage;
import de.sesu8642.feudaltactics.menu.splashscreen.dagger.SplashScreenStage;

/** {@link Screen} for displaying a splash image. */
@Singleton
public class SplashScreen extends GameScreen {

	private long startTime;
	private EventBus eventBus;

	@Inject
	public SplashScreen(EventBus eventBus, @MenuCamera OrthographicCamera camera, @MenuViewport Viewport viewport,
			@SplashScreenStage ResizableResettableStage stage) {
		super(camera, viewport, stage);
		this.eventBus = eventBus;
	}

	@Override
	public void render(float delta) {
		super.render(delta);
		if (TimeUtils.timeSinceMillis(startTime) > 1000) {
			eventBus.post(new ScreenTransitionTriggerEvent(ScreenTransitionTarget.MAIN_MENU_SCREEN));
			this.hide();
		}
	}

	@Override
	public void show() {
		startTime = TimeUtils.millis();
		super.show();
	}

	@Override
	public void hide() {
		super.hide();
		// TODO: disposing here causes error "buffer not allocated with
		// newUnsafeByteBuffer or already
		// disposed"; maybe because the call is caused by the render method
	}

}

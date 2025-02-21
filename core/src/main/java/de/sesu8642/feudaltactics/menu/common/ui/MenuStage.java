// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.common.ui;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.Viewport;

import de.sesu8642.feudaltactics.renderer.MapRenderer;

/**
 * Generic {@link Stage} for displaying an in-game menu.
 */
public class MenuStage extends ResizableResettableStage {

	private Table rootTable;
	private List<TextButton> buttons = new ArrayList<>();
	private Label bottomLeftLabel;
	private Label bottomRightLabel;
	Set<Disposable> disposables = new HashSet<>();
	private MapRenderer mapRenderer;
	private Skin skin;
	private OrthographicCamera camera;

	public MenuStage(Viewport viewport, OrthographicCamera camera, MapRenderer mapRenderer, Skin skin) {
		this(viewport, new LinkedHashMap<>(), camera, mapRenderer, skin);
	}

	/**
	 * Constructor.
	 * 
	 * @param viewport    viewport for the stage
	 * @param buttonData  map of button titles and callbacks that are executed when
	 *                    the buttons are clicked
	 * @param camera      camera to use
	 * @param mapRenderer renderer for the sea background
	 * @param skin        game skin
	 */
	public MenuStage(Viewport viewport, Map<String, Runnable> buttonData, OrthographicCamera camera,
			MapRenderer mapRenderer, Skin skin) {
		super(viewport);
		this.camera = camera;
		this.mapRenderer = mapRenderer;
		this.skin = skin;
		initUi(buttonData);
	}

	private void initUi(Map<String, Runnable> buttonData) {
		Texture logoTexture = new Texture(Gdx.files.internal("logo.png"));
		disposables.add(logoTexture);
		for (Entry<String, Runnable> buttonDataPoint : buttonData.entrySet()) {
			TextButton button = new TextButton(buttonDataPoint.getKey(), skin);
			button.addListener(new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					buttonDataPoint.getValue().run();
				}
			});
			buttons.add(button);
		}
		bottomLeftLabel = new Label("", skin);
		bottomRightLabel = new Label("", skin);

		rootTable = new Table();
		rootTable.setFillParent(true);
		rootTable.defaults().minSize(0).fillX().expandY().colspan(2);
		Image logo = new Image(logoTexture);
		rootTable.add(logo).prefHeight(Value.percentWidth(0.51F, rootTable)).width(Value.percentHeight(1.91F));
		rootTable.row();
		rootTable.defaults().minHeight(100).pad(5);
		for (TextButton button : buttons) {
			rootTable.add(button).prefWidth(Value.percentWidth(0.5F, rootTable));
			rootTable.row();
		}
		rootTable.row();
		rootTable.add(bottomLeftLabel).fill(false).left().bottom().pad(10).minHeight(0).colspan(1);
		rootTable.add(bottomRightLabel).fill(false).right().bottom().pad(10).minHeight(0).colspan(1);

		this.addActor(rootTable);
	}

	/**
	 * Adds a button to this menu.
	 * 
	 * @param text     button text
	 * @param callback callback that is executed when the button is clicked
	 */
	public void addButton(String text, Runnable callback) {
		TextButton button = new TextButton(text, skin);
		button.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				callback.run();
			}
		});
		rootTable.removeActor(bottomLeftLabel);
		rootTable.removeActor(bottomRightLabel);
		rootTable.row();
		rootTable.add(button).prefWidth(Value.percentWidth(0.5F, rootTable));
		rootTable.row();
		rootTable.add(bottomLeftLabel).fill(false).left().bottom().pad(5).minHeight(0).colspan(1);
		rootTable.add(bottomRightLabel).fill(false).right().bottom().pad(5).minHeight(0).colspan(1);
	}

	public void setBottomLeftLabelText(String text) {
		bottomLeftLabel.setText(text);
	}

	/**
	 * Makes the bottom label a clickable link to the given URI.
	 * 
	 * @param uri link target
	 */
	public void setBottomLeftLabelLink(String uri) {
		bottomLeftLabel.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Gdx.net.openURI(uri);
			}
		});
	}

	public void setBottomRightLabelText(String text) {
		bottomRightLabel.setText(text);
	}

	public List<TextButton> getButtons() {
		return buttons;
	}

	@Override
	public void updateOnResize(int width, int height) {
		rootTable.pack();
		camera.viewportHeight = height;
		camera.viewportWidth = width;
		camera.update();
	}

	@Override
	public void draw() {
		mapRenderer.render();
		super.draw();
	}

	@Override
	public void dispose() {
		super.dispose();
		for (Disposable disposable : disposables) {
			disposable.dispose();
		}
	}

	@Override
	public void reset() {
		// nothing to reset
	}
}

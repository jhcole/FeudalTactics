// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.ingame.ui;

import java.util.stream.Stream;

import javax.inject.Inject;

import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldFilter.DigitsOnlyFilter;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.google.common.eventbus.EventBus;

import de.sesu8642.feudaltactics.events.RegenerateMapEvent;
import de.sesu8642.feudaltactics.events.moves.GameStartEvent;
import de.sesu8642.feudaltactics.ingame.MapParameters;
import de.sesu8642.feudaltactics.ingame.NewGamePreferences;
import de.sesu8642.feudaltactics.ingame.NewGamePreferences.Densities;
import de.sesu8642.feudaltactics.ingame.NewGamePreferences.MapSizes;
import de.sesu8642.feudaltactics.ingame.NewGamePreferencesDao;
import de.sesu8642.feudaltactics.lib.ingame.botai.Intelligence;
import de.sesu8642.feudaltactics.menu.common.dagger.MenuViewport;
import de.sesu8642.feudaltactics.menu.common.ui.ResizableResettableStage;

/**
 * {@link Stage} for displaying the input mask for a new game.
 */
public class ParameterInputStage extends ResizableResettableStage {

	private static final long INPUT_HEIGHT_PX = 79;
	private static final int INPUT_PADDING_PX = 20;

	// for map centering calculation
	/** Outer padding around all the inputs. */
	public static final int OUTER_PADDING_PX = 10;

	/** Height of the play button + bottom padding. */
	public static final long BUTTON_HEIGHT_PX = 114;

	/** Height of all parameter inputs combined. */
	public static final long TOTAL_INPUT_HEIGHT = 4 * (INPUT_HEIGHT_PX + INPUT_PADDING_PX) + BUTTON_HEIGHT_PX
			+ OUTER_PADDING_PX;

	/**
	 * Width of all parameter inputs combined; depends on label texts and used font.
	 */
	public static final long TOTAL_INPUT_WIDTH = 519;

	private EventBus eventBus;
	private NewGamePreferencesDao newGamePrefDao;
	private TextureAtlas textureAtlas;
	private Skin skin;

	private Table rootTable;
	private SelectBox<String> sizeSelect;
	private SelectBox<String> densitySelect;
	private SelectBox<String> difficultySelect;
	private ImageButton randomButton;
	private TextButton playButton;
	private TextField seedTextField;

	/**
	 * Constructor.
	 * 
	 * @param eventBus     event bus
	 * @param viewport     viewport for the stage
	 * @param textureAtlas texture atlas containing the button textures
	 * @param skin         game skin
	 */
	@Inject
	public ParameterInputStage(EventBus eventBus, NewGamePreferencesDao newGamePrefDao, @MenuViewport Viewport viewport,
			TextureAtlas textureAtlas, Skin skin) {
		super(viewport);
		this.eventBus = eventBus;
		this.newGamePrefDao = newGamePrefDao;
		this.textureAtlas = textureAtlas;
		this.skin = skin;
		initUi();
	}

	private void initUi() {
		// note about checktyle: widgets are declared in the order they appear in the UI
		NewGamePreferences prefs = newGamePrefDao.getNewGamePreferences();
		Label difficultyLabel = new Label("CPU\nDifficulty", skin);
		difficultySelect = new SelectBox<>(skin);
		String[] difficulties = { "Easy", "Medium", "Hard", "Very hard" };
		difficultySelect.setItems(difficulties);
		difficultySelect.setSelectedIndex(prefs.getBotIntelligence().ordinal());
		Label sizeLabel = new Label("Map\nSize", skin);
		sizeSelect = new SelectBox<>(skin);
		// xxlarge is temporarily disabled because of performance problems
		String[] sizes = { "Small", "Medium   ", "Large", "XLarge", /* "XXLarge" */
		};
		sizeSelect.setItems(sizes);
		sizeSelect.setSelectedIndex(prefs.getMapSize().ordinal());
		Label densityLabel = new Label("Map\nDensity", skin);
		densitySelect = new SelectBox<>(skin);
		String[] densities = { "Dense", "Medium   ", "Loose" };
		densitySelect.setItems(densities);
		densitySelect.setSelectedIndex(prefs.getDensity().ordinal());
		Label seedLabel = new Label("Seed", skin);
		seedTextField = new TextField(String.valueOf(System.currentTimeMillis()), skin);
		seedTextField.setTextFieldFilter(new DigitsOnlyFilter());
		seedTextField.setMaxLength(18);
		randomButton = new ImageButton(skin);
		randomButton.getStyle().imageUp = new SpriteDrawable(textureAtlas.createSprite("die"));
		randomButton.getStyle().imageDown = new SpriteDrawable(textureAtlas.createSprite("die_pressed"));
		randomButton.getImageCell().expand().fill();
		playButton = new TextButton("Play", skin);

		/*
		 * The longest text on the screen is the seed text field. It allows for 18
		 * characters at max and 7 is the widest number. Scrolling the text is possible.
		 * Generated seeds are normally much shorter than the worst case (only 7s).
		 */
		float maxSeedNumberWidth = new GlyphLayout(seedTextField.getStyle().font, "7").width;
		float seedTextFieldWidth = maxSeedNumberWidth * 20;

		rootTable = new Table();
		rootTable.setFillParent(true);
		rootTable.defaults().left().pad(INPUT_PADDING_PX / 2F, 0, INPUT_PADDING_PX / 2F, 0);
		rootTable.columnDefaults(0).pad(0, OUTER_PADDING_PX, 0, OUTER_PADDING_PX);
		rootTable.add().expandY();
		rootTable.row();
		rootTable.add(difficultyLabel);
		rootTable.add(difficultySelect).colspan(2).fillX();
		rootTable.add().expandX();
		rootTable.row();
		rootTable.add(sizeLabel);
		rootTable.add(sizeSelect).colspan(2).fillX();
		rootTable.row();
		rootTable.add(densityLabel);
		rootTable.add(densitySelect).colspan(2).fillX();
		rootTable.row();
		rootTable.add(seedLabel);
		rootTable.add(seedTextField).minWidth(seedTextFieldWidth);
		rootTable.add(randomButton).height(Value.percentHeight(1, seedTextField)).width(Value.percentHeight(1))
				.padLeft(INPUT_PADDING_PX);
		rootTable.row();
		rootTable.add(playButton).colspan(4).fillX().pad(INPUT_PADDING_PX / 2F, OUTER_PADDING_PX, OUTER_PADDING_PX,
				OUTER_PADDING_PX);
		this.addActor(rootTable);

		registerEventListeners();
	}

	private void registerEventListeners() {

		randomButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				seedTextField.setText(String.valueOf(System.currentTimeMillis()));
			}
		});

		Stream.of(seedTextField, randomButton, difficultySelect, sizeSelect, densitySelect)
				.forEach(actor -> actor.addListener(new ChangeListener() {
					@Override
					public void changed(ChangeEvent event, Actor actor) {
						eventBus.post(new RegenerateMapEvent(getBotIntelligence(), new MapParameters(getSeedParam(),
								getMapSizeParam().getAmountOfTiles(), getMapDensityParam().getDensityFloat())));
						newGamePrefDao.saveNewGamePreferences(
								new NewGamePreferences(getBotIntelligence(), getMapSizeParam(), getMapDensityParam()));
					}
				}));

		playButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				eventBus.post(new GameStartEvent());
			}
		});
	}

	/**
	 * Updates the seed.
	 * 
	 * @param seed map seed to display
	 */
	public void updateSeed(Long seed) {
		seedTextField.setText(seed.toString());
	}

	/**
	 * Getter for map seed.
	 * 
	 * @return map seed input by the user
	 */
	public Long getSeedParam() {
		try {
			return Long.valueOf(seedTextField.getText());
		} catch (NumberFormatException e) {
			return 0L;
		}
	}

	public MapSizes getMapSizeParam() {
		return MapSizes.values()[sizeSelect.getSelectedIndex()];
	}

	public Densities getMapDensityParam() {
		return Densities.values()[densitySelect.getSelectedIndex()];
	}

	public Intelligence getBotIntelligence() {
		return Intelligence.values()[difficultySelect.getSelectedIndex()];
	}

	@Override
	public void updateOnResize(int width, int height) {
		// VERY IMPORTANT!!! makes everything scale correctly on startup and going
		// fullscreen etc.; took me hours to find out
		rootTable.pack();
	}

	@Override
	public void reset() {
		// nothing to reset
	}

}

package de.deverror.dsw;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import de.deverror.dsw.game.GameScreen;
import de.deverror.dsw.ui.MainMenuScreen;
import de.deverror.dsw.util.Assets;
import de.deverror.dsw.util.StaticUtil;
import de.deverror.dsw.util.ThreadUtil;

import java.util.HashMap;

public class Main extends Game {
	public AssetManager assets;

	public GameScreen game;
	public MainMenuScreen mainMenu;
	
	@Override
	public void create () {

		assets = new AssetManager();

		loadAssets();

		mainMenu = new MainMenuScreen(this);

		setScreen(mainMenu);
	}

	@Override
	public void render () {
		super.render();
	}
	
	@Override
	public void dispose () {
		assets.dispose();
	}

	public void loadAssets() {
		assets.load(Assets.TILESET, Texture.class);

		assets.load(Assets.MENUSKIN, Skin.class);
		assets.load(Assets.MENUATLAS, TextureAtlas.class);
		assets.load(Assets.MENUBACKGROUND, Texture.class);
		assets.load(Assets.MENUATLAS_TEXTURE, Texture.class);

		assets.load(Assets.ATLAS, TextureAtlas.class);
		assets.load(Assets.ATLAS_TEXTURE, Texture.class);

		assets.load(Assets.CHEFATLAS, TextureAtlas.class);
		assets.load(Assets.CHEFATLAS_TEXTURE, Texture.class);

		assets.finishLoading(); //ToDo: Put in loading screen-thread
	}

    public void changeScreen(final Screen screen) {
		ThreadUtil.startDelayedInMain(() -> { setScreen(screen); }, 500);
    }

    public void resetGame() {
		game = new GameScreen(assets, this);
		setScreen(game);
    }
}

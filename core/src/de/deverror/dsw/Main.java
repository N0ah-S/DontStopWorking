package de.deverror.dsw;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import de.deverror.dsw.game.GameScreen;
import de.deverror.dsw.ui.MainMenuScreen;
import de.deverror.dsw.util.Assets;

public class Main extends Game {
	public AssetManager assets;

	GameScreen game;
	MainMenuScreen mainMenu;
	
	@Override
	public void create () {

		assets = new AssetManager();

		loadAssets();

		mainMenu = new MainMenuScreen(this);
		game = new GameScreen(assets);

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
		assets.load(Assets.CHEF, Texture.class);
		assets.load(Assets.OK, Texture.class);
		assets.load(Assets.NOT_OK, Texture.class);

		assets.load(Assets.MENUSKIN, Skin.class);
		assets.load(Assets.MENUATLAS, TextureAtlas.class);
		assets.load(Assets.MENUBACKGROUND, Texture.class);

		assets.load(Assets.warning, Texture.class);
		assets.load(Assets.ATLAS, TextureAtlas.class);
		assets.load(Assets.ATLAS_TEXTURE, Texture.class);

		assets.finishLoading(); //ToDo: Put in loading screen-thread
	}
}

package de.deverror.dsw.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import de.deverror.dsw.Main;
import de.deverror.dsw.game.particles.ParticleRenderer;
import de.deverror.dsw.game.particles.ParticleType;
import de.deverror.dsw.util.Assets;
import de.deverror.dsw.util.ThreadUtil;

import static de.deverror.dsw.util.StaticUtil.*;

public class MainMenuScreen implements Screen {

    Main main;
    Skin menuSkin;
    TextureAtlas menuAtlas;
    Stage stage;
    Table menuTable;
    Texture background;

    ParticleRenderer particleRenderer;

    public MainMenuScreen(Main main){
        this.main = main;
        menuSkin = main.assets.get(Assets.MENUSKIN);
        menuAtlas = new TextureAtlas(Assets.MENUATLAS);
        particleRenderer = new ParticleRenderer();
    }

    @Override
    public void show() {
        loadParticles();
        background = main.assets.get(Assets.MENUBACKGROUND);
        Image backgroundImage = new Image(background);
        backgroundImage.setSize(width(),height());
        stage = new Stage(new FitViewport(width(), height(), new OrthographicCamera()));
        menuTable = new Table();
        Skin menuSkin = main.assets.get(Assets.MENUSKIN), menuAtlas;

        ImageButton playButton = new ImageButton(menuSkin, "play");
        ImageButton settingsButton = new ImageButton(menuSkin, "settings");
        ImageButton exitButton = new ImageButton(menuSkin, "exit");

        menuTable.bottom().add(playButton).size( 250, 145).pad(40);
        menuTable.bottom().add(settingsButton).size( 250, 145).pad(40);
        menuTable.bottom().add(exitButton).size( 250, 145).pad(40);
        stage.addActor(backgroundImage);
        stage.addActor(menuTable);
        Gdx.input.setInputProcessor(stage);

        menuTable.addAction(Actions.sequence(Actions.moveBy(0.0F, -250F), Actions.delay(1.0F), Actions.moveBy(0.0F, 250F, 1.0F, Interpolation.swing)));
        backgroundImage.addAction(Actions.sequence(Actions.alpha(0.0F), Actions.fadeIn(1.0F)));

        playButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("PLAY!");
                particleRenderer.spawn(0, 90, 180, event.getStageX(),event.getStageY(), 300, 180, 30, 3f);
                main.changeScreen(main.game);
            }
        });
        settingsButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("SETTINGS!");
                particleRenderer.spawn(0, 90, 180, event.getStageX(),event.getStageY(), 300, 180, 30, 3f);
            }
        });
        exitButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("EXIT!");
                particleRenderer.spawn(0, 90, 180, event.getStageX(),event.getStageY(), 300, 180, 30, 3f);
                ThreadUtil.startDelayedInMain(() -> { Gdx.app.exit(); }, 500);
            }
        });
    }


    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.0F, 0.0F, 0.0F, 0.0F);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        particleRenderer.update(delta);
        stage.act();
        stage.draw();
        stage.getBatch().begin();
        particleRenderer.floorrender(stage.getBatch());
        particleRenderer.ceilrender(stage.getBatch());
        stage.getBatch().end();
    }


    @Override
    public void resize(int width, int height) {

        stage.getViewport().update(width,height,true);


        menuTable.invalidateHierarchy();
        menuTable.setSize(width, height);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
        dispose();    }

    @Override
    public void dispose() {

        stage.dispose();
        background.dispose();
        menuSkin.dispose();

    }

    public void loadParticles(){
        particleRenderer.addParticleType(0, new ParticleType(menuAtlas.findRegion("Papier"), 1, true));
    }
}

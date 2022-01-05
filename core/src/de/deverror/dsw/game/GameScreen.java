package de.deverror.dsw.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.crashinvaders.vfx.VfxManager;
import com.crashinvaders.vfx.effects.BloomEffect;
import com.crashinvaders.vfx.effects.FxaaEffect;
import com.crashinvaders.vfx.effects.GaussianBlurEffect;
import de.deverror.dsw.Main;
import de.deverror.dsw.game.objects.Entity;
import de.deverror.dsw.game.objects.WorldManager;
import de.deverror.dsw.game.objects.moving.Player;
import de.deverror.dsw.game.objects.moving.Worker;
import de.deverror.dsw.game.objects.stationary.CoffeeMachine;
import de.deverror.dsw.game.objects.stationary.Decoration;
import de.deverror.dsw.game.objects.stationary.Microphone;
import de.deverror.dsw.game.objects.stationary.eyecandy.Coffee;
import de.deverror.dsw.game.objects.stationary.eyecandy.PaperHeap;
import de.deverror.dsw.game.objects.stationary.eyecandy.Trashcan;
import de.deverror.dsw.game.particles.ParticleRenderer;
import de.deverror.dsw.game.particles.ParticleType;
import de.deverror.dsw.util.Assets;
import de.deverror.dsw.util.ShapeUtils;
import de.deverror.dsw.util.StaticUtil;
import org.graalvm.compiler.phases.common.NodeCounterPhase;

import static de.deverror.dsw.util.StaticUtil.*;
import static de.deverror.dsw.util.GameSettings.*;

import java.util.ArrayList;

public class GameScreen implements Screen {
    public ArrayList<Entity> entities;
    Player player;
    public World physicsWorld;
    public WorldManager worldManager;
    Main main;

    Box2DDebugRenderer debugRenderer;
    OrthographicCamera cam;
    SpriteBatch batch;
    SpriteBatch HUDBatch;

    TiledMap tiledMap;
    public AssetManager assets;
    public TextureAtlas textureAtlas;

    public ParticleRenderer particles;

    SortRenderer renderer;
    int points = 0;
    float shakeTime = 0;

    private VfxManager vfx;
    private BloomEffect bloom;
    private GaussianBlurEffect blur;

    boolean paused;
    public static int menu; //0 = dead, 1 = won, 2 = menu
    Stage menuStage;
    TextureAtlas menuAtlas;
    Image menuBackground;
    ImageButton[] buttons;
    Label[] labels;
    Skin menuSkin;

    public GameScreen(AssetManager assets, Main main) {
        entities = new ArrayList<>();
        this.assets = assets;
        textureAtlas = new TextureAtlas(Assets.ATLAS);

        physicsWorld = new World(new Vector2(0, 0), true);
        worldManager = new WorldManager(this);

        debugRenderer = new Box2DDebugRenderer();
        cam = new OrthographicCamera();
        batch = new SpriteBatch();
        HUDBatch = new SpriteBatch();

        tiledMap = new TmxMapLoader().load("map.tmx");

        this.main = main;

        renderer = new SortRenderer(this);

        player = new Player(this);
        entities.add(player);

        worldManager.registerWorker(new Worker(280, 128 + 64, this));
        worldManager.registerWorker(new Worker(500, 128 + 64, this));
        worldManager.registerWorker(new Worker(500, 256 + 64, this));
        worldManager.registerWorker(new Worker(270, 500 + 64, this));
        worldManager.registerWorker(new Worker(280, 256 + 64, this));

        generateColliders();
        loadParticles();
        generateEntities();
        loadMenus();
    }
    @Override
    public void show() {
        vfx = new VfxManager(Pixmap.Format.RGBA8888);

        bloom = new BloomEffect();
        bloom.setBloomIntensity(2);
        vfx.addEffect(bloom);
        //fxaa = new FxaaEffect(0.0078125F, 0.125F, 99.0F, true);
        //vfx.addEffect(fxaa);
        Gdx.input.setInputProcessor(menuStage);
        labels[0].setVisible(false);
        labels[1].setVisible(false);
        labels[2].setVisible(false);
    }

    @Override
    public void render(float delta) {
        if(keyjust(MENU)){
            if(paused && menu == 2){
                paused = false;
                buttons[0].addAction(Actions.sequence(Actions.moveBy(0.0F, -500), Actions.moveBy(0.0F, 500, 0.5F, Interpolation.swing)));
                buttons[1].addAction(Actions.sequence(Actions.moveBy(0.0F, -500), Actions.delay(0.2f), Actions.moveBy(0.0F, 500, 0.5F, Interpolation.swing)));
                buttons[2].addAction(Actions.sequence(Actions.moveBy(0.0F, -500), Actions.delay(0.4f), Actions.moveBy(0.0F, 500, 0.5F, Interpolation.swing)));
            }else{
                menu = 2;
                paused = true;
                labels[menu].setVisible(true);
            }
        }
        if(!paused){
            bloom.setBloomIntensity(2.6f + (float) Math.random() * 1.4f);

            shakeTime -= delta * 5;

            physicsWorld.step(delta, 6, 2);
            for(Entity entity : entities) entity.update(delta);
            worldManager.update(delta);
            particles.update(delta);
        }


        updateCamera();
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT |
                (Gdx.graphics.getBufferFormat().coverageSampling?GL20.GL_COVERAGE_BUFFER_BIT_NV:0));
        vfx.cleanUpBuffers();

        vfx.beginInputCapture();
        batch.begin();
        renderer.render(batch);
        particles.ceilrender(batch);
        worldManager.renderTransformed(batch);
        batch.end();

        vfx.endInputCapture();
        vfx.applyEffects();
        vfx.renderToScreen();

        if(StaticUtil.key(Input.Keys.G)) debugRenderer.render(physicsWorld, cam.combined);

        HUDBatch.begin();
        worldManager.renderUntransformed(HUDBatch);
        player.renderHUD(HUDBatch);
        HUDBatch.end();
        if(paused) {
            menuStage.act(delta);
            menuStage.draw();
        }
    }

    @Override
    public void resize(int w, int h) {
        vfx.resize(w, h);
        updateCamera();

        buttons[0].setX(w/2f, Align.center);
        buttons[1].setX(w/2f, Align.center);
        buttons[2].setX(w/2f, Align.center);

        buttons[0].setY(h/2f, Align.center);
        buttons[1].setY(h/2f-150, Align.center);
        buttons[2].setY(h/2f-300, Align.center);

        labels[0].setX(w/2f, Align.center);
        labels[1].setX(w/2f, Align.center);
        labels[2].setX(w/2f, Align.center);

        labels[0].setY(h/2f+200);
        labels[1].setY(h/2f+200);
        labels[2].setY(h/2f+200);

        menuStage.getViewport().update(w,h,true);
    }

    @Override
    public void dispose() {
        batch.dispose();
        HUDBatch.dispose();
        physicsWorld.dispose();
        debugRenderer.dispose();
        vfx.dispose();
        bloom.dispose();

        menuStage.dispose();
        menuAtlas.dispose();
    }

    private void updateCamera() {
        cam.viewportWidth = width();
        cam.viewportHeight = height();
        if(shakeTime > 0 && !paused) {
            cam.zoom = (64f*TILESINVIEW)/width() - (float) Math.random() * 0.05f;
            cam.position.x = (float) (player.getX() + Math.sin(Math.max(shakeTime, 0) * 15) * 4 + Math.random() * 3);
            cam.position.y = (float) (player.getY() + Math.cos(Math.max(shakeTime + Math.random() * 5, 0) * 10) * 4);
        } else {
            cam.zoom = (64f*TILESINVIEW)/width();
            cam.position.x = player.getX();
            cam.position.y = player.getY();
        }
        cam.update();
        batch.setProjectionMatrix(cam.combined);
    }

    public void generateColliders() {
        for (MapObject mapObject : tiledMap.getLayers().get(2).getObjects()) {
            Shape shape = ShapeUtils.tmxToBox2D(mapObject);

            BodyDef bodyDef = new BodyDef();
            bodyDef.fixedRotation = true;
            bodyDef.type = BodyDef.BodyType.StaticBody;
            Body body = physicsWorld.createBody(bodyDef);

            FixtureDef fixtureDef = new FixtureDef();
            fixtureDef.shape = shape;

            body.createFixture(fixtureDef);
            shape.dispose();
        }
    }

    public void generateEntities(){
        for (MapObject mapObject : tiledMap.getLayers().get(3).getObjects()) {
            float x = ((RectangleMapObject) mapObject).getRectangle().getX();
            float y = ((RectangleMapObject) mapObject).getRectangle().getY();
            switch (mapObject.getName()){
                case "coffeemachine":
                    worldManager.coffee = new CoffeeMachine(x, y, this);
                    break;
                case "kaffee":
                    entities.add(new Coffee(x, y, this));
                    break;
                case "trashcan":
                    entities.add(new Trashcan(x, y, this));
                    break;
                case"paperheap":
                    entities.add(new PaperHeap(x, y, this));
                    break;
                case "microphone":
                    entities.add(new Microphone(this, x, y));
                    break;
                default:
                    TextureRegion region = textureAtlas.findRegion(mapObject.getName());
                    if(region != null){
                        entities.add(new Decoration(x, y, region));
                    }
            }
        }
    }

    private void loadParticles(){
        particles = new ParticleRenderer();
        particles.addParticleType(0, new ParticleType(textureAtlas.findRegion("Chef"), 0.05f, true));
        particles.addParticleType(1, new ParticleType(textureAtlas.findRegion("smoke"), 0.3f, false));
        particles.addParticleType(2, new ParticleType(textureAtlas.findRegion("smoke"), 1.5f, false));
        particles.addParticleType(3, new ParticleType(textureAtlas.findRegion("fire/3"), 0.2f, false));
        particles.addParticleType(4, new ParticleType(textureAtlas.findRegion("Blatt_partikel"), 1f, true));
    }

    public void renderFloorParticles(SpriteBatch batch){
        particles.floorrender(batch);
    }

    public void shake() {
        shakeTime = 1;
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {
        Gdx.input.setInputProcessor(menuStage);
    }

    @Override
    public void hide() {}

    private void loadMenus(){
        menuSkin = assets.get(Assets.MENUSKIN);
        menuAtlas = new TextureAtlas(Assets.MENUATLAS);
        TextureRegion background = menuAtlas.findRegion("dark");
        menuBackground = new Image(background);
        menuBackground.setSize(width(),height());
        menuStage = new Stage(new FitViewport(width(), height(), new OrthographicCamera()));

        ImageButton playButton = new ImageButton(menuSkin, "play");
        ImageButton settingsButton = new ImageButton(menuSkin, "settings");
        ImageButton exitButton = new ImageButton(menuSkin, "exit");
        Label won = new Label("Day Finished", menuSkin, "font", Color.GREEN);
        Label loss = new Label("You're fired", menuSkin, "font", Color.RED);
        Label menu = new Label("Pause", menuSkin, "font", Color.WHITE);

        playButton.setWidth(200);
        playButton.setHeight(120);
        settingsButton.setWidth(200);
        settingsButton.setHeight(120);
        exitButton.setWidth(200);
        exitButton.setHeight(120);

        won.setVisible(false);
        loss.setVisible(false);
        menu.setVisible(false);

        buttons = new ImageButton[] {playButton, settingsButton, exitButton};
        labels = new Label[] {loss, won, menu};

        menuStage.addActor(menuBackground);
        menuStage.addActor(playButton);
        menuStage.addActor(settingsButton);
        menuStage.addActor(exitButton);
        menuStage.addActor(won);
        menuStage.addActor(loss);
        menuStage.addActor(menu);
        Gdx.input.setInputProcessor(menuStage);

        playButton.addAction(Actions.sequence(Actions.moveBy(0.0F, -500), Actions.moveBy(0.0F, 500, 0.5F, Interpolation.swing)));
        settingsButton.addAction(Actions.sequence(Actions.moveBy(0.0F, -500), Actions.delay(0.2f), Actions.moveBy(0.0F, 500, 0.5F, Interpolation.swing)));
        exitButton.addAction(Actions.sequence(Actions.moveBy(0.0F, -500), Actions.delay(0.4f), Actions.moveBy(0.0F, 500, 0.5F, Interpolation.swing)));
        menuBackground.addAction(Actions.sequence(Actions.alpha(0.0F), Actions.fadeIn(1.0F)));

        playButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("PLAY!");
                if(GameScreen.menu == 2) paused = false;
            }
        });
        settingsButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("SETTINGS!");
            }
        });
        exitButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("EXIT!");
                main.setScreen(main.mainMenu);
            }
        });
    }

    public void openMenu(int nr){
        paused = true;
        menu = nr;
    }
}

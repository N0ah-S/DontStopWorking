package de.deverror.dsw.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.ai.GdxAI;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.ScreenUtils;
import com.crashinvaders.vfx.VfxManager;
import com.crashinvaders.vfx.effects.BloomEffect;
import com.crashinvaders.vfx.effects.FxaaEffect;
import de.deverror.dsw.game.objects.Entity;
import de.deverror.dsw.game.objects.WorldManager;
import de.deverror.dsw.game.objects.moving.Player;
import de.deverror.dsw.game.objects.moving.Worker;
import de.deverror.dsw.game.objects.stationary.CoffeeMachine;
import de.deverror.dsw.game.particles.ParticleRenderer;
import de.deverror.dsw.game.particles.ParticleType;
import de.deverror.dsw.util.Assets;
import de.deverror.dsw.util.ShapeUtils;
import de.deverror.dsw.util.StaticUtil;
import com.crashinvaders.vfx.effects.GaussianBlurEffect;

import static de.deverror.dsw.util.StaticUtil.*;
import static de.deverror.dsw.util.GameSettings.*;

import java.util.ArrayList;

public class GameScreen implements Screen {
    public ArrayList<Entity> entities;
    Player player;
    public World physicsWorld;
    public WorldManager worldManager;

    Box2DDebugRenderer debugRenderer;
    OrthographicCamera cam;
    SpriteBatch batch;
    SpriteBatch HUDBatch;

    TiledMap tiledMap;
    public AssetManager assets;
    public TextureAtlas textureAtlas;

    public ParticleRenderer particles;

    SortRenderer renderer;
    BitmapFont font;
    int points = 0;

    private VfxManager vfx;
    private BloomEffect bloom;
    private FxaaEffect fxaa;

    public GameScreen(AssetManager assets){
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



        renderer = new SortRenderer(this);

        player = new Player(this);
        entities.add(player);

        worldManager.registerWorker(new Worker(280, 128, this));
        worldManager.registerWorker(new Worker(500, 128, this));
        worldManager.registerWorker(new Worker(500, 256, this));
        worldManager.registerWorker(new Worker(270, 500, this));
        worldManager.registerWorker(new Worker(280, 256, this));

        generateColliders();
        loadParticles();
        generateEntities();

        font = new BitmapFont(Gdx.files.internal("skin/Unnamed.fnt"));
        font.setColor(0.25f, 1f, 0.12f, 1);
    }
    @Override
    public void show() {
        vfx = new VfxManager(Pixmap.Format.RGBA8888);

        bloom = new BloomEffect();
        bloom.setBloomIntensity(2);
        vfx.addEffect(bloom);
        //fxaa = new FxaaEffect(0.0078125F, 0.125F, 99.0F, true);
        //vfx.addEffect(fxaa);
    }

    @Override
    public void render(float delta) {
        System.out.println(1f/delta);

        physicsWorld.step(delta, 6, 2);
        for(Entity entity : entities) entity.update(delta);
        worldManager.updateInterest();

        updateCamera();
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT |
                (Gdx.graphics.getBufferFormat().coverageSampling?GL20.GL_COVERAGE_BUFFER_BIT_NV:0));
        // Clean up internal buffers, as we don't need any information from the last render.
        vfx.cleanUpBuffers();

        // Begin render to an off-screen buffer.
        vfx.beginInputCapture();
        batch.begin();
        renderer.render(batch);
        batch.end();

        vfx.endInputCapture();

        vfx.applyEffects();

        // Render result to the screen.
        vfx.renderToScreen();

        if(StaticUtil.key(Input.Keys.G)) debugRenderer.render(physicsWorld, cam.combined);

        HUDBatch.begin();
        worldManager.render(HUDBatch);
        String text = Integer.toString(points);
        font.draw(HUDBatch, text, vfx.getWidth() - 30 - text.length() * 75, vfx.getHeight() - 20);
        HUDBatch.end();
    }

    @Override
    public void resize(int w, int h) {
        vfx.resize(w, h);
        updateCamera();
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        batch.dispose();
        HUDBatch.dispose();
        physicsWorld.dispose();
        debugRenderer.dispose();
        vfx.dispose();
        bloom.dispose();
    }

    private void updateCamera(){
        cam.viewportWidth = width();
        cam.zoom = (64f*TILESINVIEW)/width();
        cam.viewportHeight = height();
        cam.position.x = player.getX();
        cam.position.y = player.getY();
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
            switch (mapObject.getName()){
                case "coffee":
                    worldManager.coffee = new CoffeeMachine(((RectangleMapObject) mapObject).getRectangle().getX(), ((RectangleMapObject) mapObject).getRectangle().getY(), this);
                    break;
                case "mug":
                    break;
                case "bin":
                    break;
            }
        }
    }

    private void loadParticles(){
        particles = new ParticleRenderer();
        particles.addParticleType(0, new ParticleType(textureAtlas.findRegion("Chef"), 0.2f));
    }
}

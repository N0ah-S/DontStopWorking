package de.deverror.dsw.game;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import de.deverror.dsw.game.objects.Entity;
import de.deverror.dsw.game.objects.moving.Player;

import static de.deverror.dsw.util.StaticUtil.*;

import java.util.ArrayList;

public class GameScreen implements Screen {
    public ArrayList<Entity> entities;
    Player player;
    public World physicsWorld;

    Box2DDebugRenderer debugRenderer;
    OrthographicCamera cam;
    SpriteBatch batch;

    TiledMap tiledMap;
    AssetManager assets;
    TextureAtlas textureAtlas;

    public GameScreen(AssetManager assets){
        entities = new ArrayList<>();

        physicsWorld = new World(new Vector2(0, 0), true);

        debugRenderer = new Box2DDebugRenderer();
        cam = new OrthographicCamera();
        batch = new SpriteBatch();

        tiledMap = new TiledMap();
        player = new Player(this);

        this.assets = assets;
        textureAtlas = new TextureAtlas();
    }
    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        updateCamera();
        physicsWorld.step(delta, 6, 2);
        for(Entity entity : entities) entity.update(delta);
    }

    @Override
    public void resize(int i, int i1) {
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
        physicsWorld.dispose();
        debugRenderer.dispose();
    }

    private void updateCamera(){
        cam.viewportWidth = width();
        cam.viewportHeight = height();
        cam.position.x = player.getX();
        cam.position.y = player.getY();
        cam.update();
        batch.setProjectionMatrix(cam.combined);
    }
}

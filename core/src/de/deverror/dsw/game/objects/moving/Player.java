package de.deverror.dsw.game.objects.moving;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import de.deverror.dsw.game.GameScreen;
import de.deverror.dsw.game.objects.Entity;
import de.deverror.dsw.game.objects.stationary.Worker;
import de.deverror.dsw.util.Assets;

import static de.deverror.dsw.util.StaticUtil.*;
import static de.deverror.dsw.util.GameSettings.*;

public class Player implements Entity {
    Body body;
    GameScreen main;
    Texture texture;
    public Player(GameScreen main){
        this.main = main;
        BodyDef bodyDef = new BodyDef();
        bodyDef.linearDamping = 0f;
        bodyDef.fixedRotation = true;
        bodyDef.type = BodyDef.BodyType.KinematicBody;
        this.body = main.physicsWorld.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setRadius(20);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1f;

        body.createFixture(fixtureDef);
        shape.dispose();
        texture = main.assets.get(Assets.chef);
    }
    @Override
    public float getX() {
        return body.getPosition().x;
    }

    @Override
    public float getY() {
        return body.getPosition().y;
    }

    @Override
    public void render(SpriteBatch batch) {
        batch.draw(texture, getX(), getY());
    }

    @Override
    public void update(float delta) {
        Vector2 velocity = new Vector2(0, 0);
        if(key(UP)){
            velocity.y += PLAYERSPEED;
        }
        if(key(DOWN)){
            velocity.y -= PLAYERSPEED;
        }
        if(key(LEFT)){
            velocity.x -= PLAYERSPEED;
        }
        if(key(RIGHT)){
            velocity.x += PLAYERSPEED;
        }
        if(key(SCREAM)){
            scream();
        }

        body.setLinearVelocity(velocity);
    }

    private void scream(){
        for(Entity entity : main.entities){
            if(entity instanceof Worker){
                float dist = len(entity.getX()-getX(), entity.getY()-getY());
                if(dist < 192) ((Worker) entity).motivate(15);
            }
        }
    }
}

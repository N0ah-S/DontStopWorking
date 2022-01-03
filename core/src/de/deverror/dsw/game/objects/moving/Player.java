package de.deverror.dsw.game.objects.moving;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import de.deverror.dsw.game.GameScreen;
import de.deverror.dsw.game.objects.Entity;

import static de.deverror.dsw.util.StaticUtil.*;
import static de.deverror.dsw.util.GameSettings.*;

public class Player implements Entity {
    Body body;
    GameScreen main;
    public Player(GameScreen main){
        this.main = main;
        BodyDef bodyDef = new BodyDef();
        bodyDef.linearDamping = 0f;
        bodyDef.fixedRotation = true;
        bodyDef.type = BodyDef.BodyType.KinematicBody;
        this.body = main.physicsWorld.createBody(bodyDef);
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

    }

    @Override
    public void update(float delta) {
        Vector2 velocity = new Vector2(0, 0);
        if(key(UP)){
            velocity.y += PLAYERSPEED;
        }else if(key(DOWN)){
            velocity.y -= PLAYERSPEED;
        }else if(key(LEFT)){
            velocity.x -= PLAYERSPEED;
        }else if(key(RIGHT)){
            velocity.x += PLAYERSPEED;
        }

        body.setLinearVelocity(velocity);
    }
}

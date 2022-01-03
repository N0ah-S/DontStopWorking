package de.deverror.dsw.game.objects.moving;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import de.deverror.dsw.game.objects.Entity;

public class Player implements Entity {
    Body body;
    public Player(Body body){
        this.body = body;
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

    }
}

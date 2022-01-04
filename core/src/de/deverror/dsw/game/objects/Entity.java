package de.deverror.dsw.game.objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;

public interface Entity {
    public float getX();
    public float getY();
    public float getSortY();
    public void render(SpriteBatch batch);
    public void update(float delta);
}

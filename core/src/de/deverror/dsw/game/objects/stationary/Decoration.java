package de.deverror.dsw.game.objects.stationary;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import de.deverror.dsw.game.objects.Entity;

public class Decoration implements Entity {
    TextureRegion texture;
    float x, y;

    public Decoration(float x, float y, TextureRegion texture){
        this.x = x;
        this.y = y;
        this.texture = texture;
    }

    @Override
    public float getX() {
        return x;
    }

    @Override
    public float getY() {
        return y;
    }

    @Override
    public float getSortY(){
        return y-40;
    }

    @Override
    public void render(SpriteBatch batch) {
        batch.setColor(Color.LIGHT_GRAY);
        batch.draw(texture, x-64, y-64);
        batch.setColor(Color.WHITE);
    }

    @Override
    public void update(float delta) {}
}

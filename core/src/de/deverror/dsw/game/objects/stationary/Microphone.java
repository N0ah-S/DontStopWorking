package de.deverror.dsw.game.objects.stationary;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import de.deverror.dsw.game.GameScreen;
import de.deverror.dsw.game.objects.Entity;
import de.deverror.dsw.game.objects.Reciever;
import de.deverror.dsw.ui.abilities.Interactive;

public class Microphone implements Interactive {
    float cooldown, current, x, y;
    GameScreen main;
    TextureRegion texture;

    public Microphone(GameScreen game, float x, float y){
        cooldown = 20;
        main = game;
        this.x = x;
        this.y = y;
        texture = main.textureAtlas.findRegion("Computer_side");
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
    public float getSortY() {
        return y-40;
    }

    @Override
    public void render(SpriteBatch batch) {
        batch.draw(texture, x, y);
        if(current == 0) main.particles.spawn(0, 90, 20, x, y, 200, 0, 1, 4);
    }

    @Override
    public void update(float delta) {
        current-=delta;
        if (current < 0) current = 0;
    }

    @Override
    public void activate() {
        for(Entity entity : main.entities){
            if(entity instanceof Reciever){
                ((Reciever) entity).engage(0, 10);
            }
        }
    }

    @Override
    public boolean isReady() {
        return current == 0;
    }

    @Override
    public TextureRegion getIcon() {
        return null;
    }
}

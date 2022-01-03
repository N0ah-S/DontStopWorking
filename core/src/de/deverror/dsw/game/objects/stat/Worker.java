package de.deverror.dsw.game.objects.stat;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import de.deverror.dsw.game.objects.Entity;

import static de.deverror.dsw.util.GameSettings.*;

public class Worker implements Entity {
    float interest;
    float x, y;

    public Worker(float x, float y){
        interest = MAXINTEREST;
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
    public void render(SpriteBatch batch) {

    }

    @Override
    public void update(float delta) {
        interest -= delta;
        if(interest < 0) interest = 0;
    }

    public void motivate(float amount){
        interest += amount;
        if(interest > MAXINTEREST) interest = MAXINTEREST;
    }
}

package de.deverror.dsw.game.objects.stationary;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import de.deverror.dsw.game.GameScreen;
import de.deverror.dsw.game.objects.Entity;
import de.deverror.dsw.util.Assets;

import static de.deverror.dsw.util.GameSettings.*;

public class Worker implements Entity {
    float interest;
    float x, y;
    Texture ok;
    Texture notOk;
    GameScreen main;

    public Worker(float x, float y, GameScreen main){
        this.x = x;
        this.y = y;
        interest = MAXINTEREST;
        this.main = main;
        ok = main.assets.get(Assets.ok);
        notOk = main.assets.get(Assets.notOk);
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
        float pY = y+100;
        float percentage = interest/MAXINTEREST;

        batch.draw(ok, x, pY, 50*percentage, 15);
        batch.draw(notOk, x+50*percentage, pY, 50*(1-percentage), 15);
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

package de.deverror.dsw.game.objects.moving;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import de.deverror.dsw.game.GameScreen;
import de.deverror.dsw.game.objects.Entity;
import de.deverror.dsw.util.Assets;
import de.deverror.dsw.util.StaticUtil;

import static de.deverror.dsw.util.GameSettings.*;

public class Worker implements Entity {

    GameScreen game;

    TextureRegion tex, ok, notOk;
    Color color;

    public float interest;
    float hx, hy, x, y;

    public Worker(float x, float y, GameScreen game){
        this.hx = x;
        this.hy = y;
        this.x  = x;
        this.y  = y;

        interest = MAXINTEREST;
        this.game = game;

        tex     = game.textureAtlas.findRegion("generic_worker");
        notOk   = game.textureAtlas.findRegion("notOk");
        ok      = game.textureAtlas.findRegion("ok");

        color = StaticUtil.randomColor();
    }

    @Override
    public void render(SpriteBatch batch) {
        batch.setColor(color);
        batch.draw(tex, x, y);
        batch.setColor(Color.WHITE);

        //Pop Elements
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

    @Override
    public float getX() {
        return x;
    }

    @Override
    public float getY() {
        return y;
    }
}

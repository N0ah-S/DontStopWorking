package de.deverror.dsw.game.objects.stationary;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import de.deverror.dsw.game.GameScreen;
import de.deverror.dsw.game.objects.Entity;
import de.deverror.dsw.game.objects.Reciever;

public class Eyecandy implements Entity, Reciever {
    protected TextureRegion[] good;
    protected TextureRegion[] destroy;
    protected TextureRegion[] dead;
    protected float current, speed, destroyCooldown;
    protected int state;
    protected boolean dim;

    protected GameScreen main;

    protected float x, y;

    public Eyecandy(float x, float y, GameScreen main){
        this.main = main;
        this.x = x;
        this.y = y;
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
        if(dim) batch.setColor(Color.LIGHT_GRAY);
        switch (state){
            case 0:
                batch.draw(good[(int) current] , x, y);
                break;
            case 1:
                batch.draw(destroy[(int) current] , x, y);
                break;
            case 2:
                batch.draw(dead[(int) current] , x, y);
                break;
        }
        batch.setColor(Color.WHITE);
    }

    @Override
    public void update(float delta) {
        current += delta*speed;
        switch (state){
            case 0:
                if(current > good.length-0.1) current = 0;
                break;
            case 1:
                if(current > destroy.length-0.1) {
                    state = 2;
                    current = 0;
                }
                break;
            case 2:
                if(current > dead.length-0.1) current = 0;
                break;
        }
    }

    public void destroy(){
        destroyCooldown = 3f;
        if(state > 0) return;
        state = 1;
        current = 0;
    }

    @Override
    public void engage(int intensity, int amount) {}
}

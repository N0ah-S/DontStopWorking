package de.deverror.dsw.game.objects.stationary;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import de.deverror.dsw.game.GameScreen;
import de.deverror.dsw.game.objects.Entity;
import de.deverror.dsw.game.objects.Reciever;

public class Eyecandy implements Entity, Reciever {
    TextureRegion[] good;
    TextureRegion[] destroy;
    TextureRegion[] dead;
    float current, speed, destroyCooldown;
    int state, particleid;
    int toughness; //0 dies of everything, 1 required Screaming, 2 requires harder stuff, 3 is indestructable

    GameScreen main;

    float x, y;

    public Eyecandy(TextureRegion[] first, TextureRegion[] second, TextureRegion[] third, float speed, int tough){
        good = first;
        destroy = second;
        dead = third;
        current = 0;
        state = 0;
        this.speed = speed;

        toughness = tough;
        particleid = -1;
    }
    public Eyecandy(TextureRegion[] first, TextureRegion[] second, TextureRegion[] third, float speed, int tough, GameScreen main, int particletype){
        good = first;
        destroy = second;
        dead = third;
        current = 0;
        state = 0;
        this.speed = speed;

        toughness = tough;
        this.particleid = particletype;
        this.main = main;
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

        main.particles.spawn(particleid, 0, 180, x, y, 20, 0, 10, 1);
    }

    @Override
    public void engage(int intensity, int amount) {
        if(intensity >= toughness) destroy();
    }
}

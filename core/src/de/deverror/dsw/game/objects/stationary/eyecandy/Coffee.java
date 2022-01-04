package de.deverror.dsw.game.objects.stationary.eyecandy;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import de.deverror.dsw.game.GameScreen;
import de.deverror.dsw.game.objects.stationary.Eyecandy;

import static de.deverror.dsw.util.StaticUtil.getAnimation;

public class Coffee extends Eyecandy {
    float particleDelay;
    public Coffee(float x, float y, GameScreen main) {
        super(x, y, main);
        good = getAnimation("kaffee", 3, main.textureAtlas);
        destroy = getAnimation("kaffee", 3,3,  main.textureAtlas);
        dead = getAnimation("kaffee", 1,6,  main.textureAtlas);
        current = 0;
        state = 0;
        speed = 4f;
    }

    @Override
    public void render(SpriteBatch batch){
        super.render(batch);
        if(state == 0 && particleDelay < 0){
            main.particles.spawn(1, 90, 20, x-25, y, 40, 30, 1, 5f);
            particleDelay = 1f;
        }
    }

    @Override
    public void update(float delta){
        super.update(delta);
        particleDelay -= delta;
    }

    @Override
    public void engage(int intensity, int amount) {
        destroy();
    }
}

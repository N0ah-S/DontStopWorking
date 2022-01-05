package de.deverror.dsw.game.objects.stationary.eyecandy;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import de.deverror.dsw.game.GameScreen;
import de.deverror.dsw.game.objects.stationary.Eyecandy;

import static de.deverror.dsw.util.StaticUtil.getAnimation;

public class Trashcan extends Eyecandy {
    float particleDelay;
    public Trashcan(float x, float y, GameScreen main) {
        super(x, y, main);
        good = getAnimation("trashcan_p", 1, 10, main.textureAtlas);
        destroy = getAnimation("trashcan_f", 3, main.textureAtlas);
        dead = getAnimation("trashcan_f", 25, main.textureAtlas);
        current = 0;
        state = 0;
        speed = 20f;
        dim = false;
    }
    @Override
    public void destroy(){
        super.destroy();
        main.particles.spawn(3, 0, 180, x, y, 100, 200,40, 0.5f);
    }

    @Override
    public void render(SpriteBatch batch){
        super.render(batch);
        if(state >  0 && particleDelay < 0){
            main.particles.spawn(2, 90, 20, x, y, 40, 30, 3, 2f);
            particleDelay = 2f;
        }
    }

    @Override
    public void update(float delta){
        super.update(delta);
        particleDelay -= delta;
    }

    @Override
    public void engage(int intensity, int amount){
        if(intensity > 0) destroy();
    }
}

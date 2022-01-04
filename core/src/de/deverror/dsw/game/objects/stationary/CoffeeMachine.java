package de.deverror.dsw.game.objects.stationary;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import de.deverror.dsw.game.GameScreen;
import de.deverror.dsw.game.objects.Entity;
import de.deverror.dsw.game.objects.moving.Worker;
import de.deverror.dsw.util.StaticUtil;

public class CoffeeMachine implements Entity {

    TextureRegion[] tex;
    int state = 0;
    float step = 0;

    GameScreen game;
    Vector2 pos;

    Worker[] queue;


    public CoffeeMachine(float x, float y, GameScreen game) {
        this.game = game;

        queue = new Worker[2];

        pos = new Vector2(x, y);
        tex = new TextureRegion[12];
        for (int i = 0; i < tex.length; i++) {
            tex[i] = game.textureAtlas.findRegion("coffee_machine/" + i); //dirty...
        }
        //StaticUtil.getAnimation("coffee_machine/", 12, game.textureAtlas);

        game.entities.add(this);
    }

    @Override
    public void render(SpriteBatch batch) {
        batch.draw(tex[state * 4 + (int) step], pos.x, pos.y, 75, 75);
    }

    @Override
    public void update(float delta) {
        if(state == 2) {
            // Making Coffee
            step += delta;
            if(step >= 4) {
                state = 0;
                step = 0;
                if(queue[0] != null) {
                    queue[0].backToWork();
                    if(queue[1] != null) {
                        queue[0] = queue[1];
                        queue[1] = null;
                        state = 2;
                    }
                }
            }

        } else {
            step += delta * 3;
            if(step >= 4) step = 0;
        }

    }

    /**
     *
     * @param worker
     * @return 0 => No coffee
     * 1 => get coffee
     * 2 => talking
     */
    public int orderCoffee(Worker worker) {
        return (queue[0] != null && queue[1] != null) ? 0 : 1;
    }
    public int makeCoffee(Worker worker) {
        if(queue[0] == null) queue[0] = worker;
        else if(queue[1] == null) queue[1] = worker;
        state = 2;
        return 0;
    }

    @Override
    public float getX() {
        return pos.x;
    }

    @Override
    public float getY() {
        return pos.y - 40;
    }
}

package de.deverror.dsw.util;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.HashMap;

public class Animator {
    HashMap<Integer, Animation> animations;
    int current;

    public Animator(){
        animations = new HashMap<>();
    }

    public void addAnimation(int id, Animation animation){
        animations.put(id, animation);
    }

    public void tick(float delta){
        animations.get(current).tick(delta);
    }

    public void start(int id){
        if(id == current) return;
        animations.get(current).reset();
        current = id;
    }

    public boolean isLocking(){
        return animations.get(current).locking;
    }

    public TextureRegion getTexture(){
        return animations.get(current).getTexture();
    }
}

package de.deverror.dsw.util;

import com.badlogic.gdx.graphics.Texture;

public class Animation {
    Animator animator;
    Texture[] textures;
    int following;
    public boolean locking;
    float length, current;

    public Animation(Animator animator, Texture[] images, float length, int following){
        textures = images;
        this.length = length;
        this.following = following;
        current = 0f;
        locking = false;
        this.animator = animator;
    }

    public Animation(Animator animator, Texture[] images, float length, int following, boolean lock){
        textures = images;
        this.length = length;
        this.following = following;
        current = 0f;
        locking = lock;
        this.animator = animator;
    }

    public void tick(float delta){
        if((current += delta) >= length){
            current = 0;
            animator.start(following);
        }
    }

    public Texture getTexture(){
        return textures[(int) Math.floor((textures.length+1)*(current/length))];
    }

    public void reset(){
        current = 0;
    }
}

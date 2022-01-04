package de.deverror.dsw.util;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Animation {
    Animator animator;
    TextureRegion[] textures;
    int following;
    public boolean locking;
    float length, current;

    public Animation(Animator animator, TextureRegion[] images, float length, int following){
        textures = images;
        this.length = length;
        this.following = following;
        current = 0f;
        locking = false;
        this.animator = animator;
    }

    public Animation(Animator animator, TextureRegion[] images, float length, int following, boolean lock){
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

    public TextureRegion getTexture(){
        return textures[(int) Math.floor((textures.length-0.1f)*(current/length))];
    }

    public void reset(){
        current = 0;
    }
}

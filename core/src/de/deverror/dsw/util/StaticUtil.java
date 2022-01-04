package de.deverror.dsw.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import java.util.Random;

public class StaticUtil {

    public static final Color KINDA_DARK = new Color(0.9f, 0.9f, 0.9f, 1);

    public static Random random = new Random();

    public static boolean key(int key){
        return Gdx.input.isKeyPressed(key);
    }

    public static int width(){
        return Gdx.graphics.getWidth();
    }

    public static int height(){
        return Gdx.graphics.getHeight();
    }

    public static float len(float x, float y){
        return (float) Math.sqrt(x*x+y*y);
    }

    public static float vectorToAngle (Vector2 vector) {
        return (float)Math.atan2(-vector.x, vector.y);
    }

    public static Vector2 angleToVector (Vector2 outVector, float angle) {
        outVector.x = -(float)Math.sin(angle);
        outVector.y = (float)Math.cos(angle);
        return outVector;
    }

    public static Color randomColor() {
        return new Color(random.nextFloat(), random.nextFloat(), random.nextFloat(), 1f);
    }

    public static TextureRegion[] getIndexAnimation(String name, int frames, TextureAtlas atlas){
        TextureRegion[] out = new TextureRegion[frames];

        for(int i = 0; i < frames; i++){
            out[i] = atlas.findRegion(name, i);
            if(out[i] == null) System.out.println("Unable to find: "+name+" with index "+i);
        }
        return out;
    }

    public static TextureRegion[] getAnimation(String name, int frames, TextureAtlas atlas){
        TextureRegion[] out = new TextureRegion[frames];

        for(int i = 0; i < frames; i++){
            out[i] = atlas.findRegion(name+"/"+i);
            if(out[i] == null) System.out.println("Unable to find: "+name+"/"+i);
        }
        return out;
    }
    public static TextureRegion[] getAnimation(String name, int frames, int start, TextureAtlas atlas){
        TextureRegion[] out = new TextureRegion[frames];

        for(int i = start; i < frames+start; i++){
            out[i-start] = atlas.findRegion(name+"/"+i);
            if(out[i-start] == null) System.out.println("Unable to find: "+name+"/"+i);
        }
        return out;
    }
    public static TextureRegion[] getImage(String name, TextureAtlas atlas){
        return new TextureRegion[]{atlas.findRegion(name)};
    }
}

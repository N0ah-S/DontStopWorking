package de.deverror.dsw.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;

import java.util.Random;

public class StaticUtil {

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

    public static Color randomColor() {
        return new Color(random.nextFloat(), random.nextFloat(), random.nextFloat(), 1f);
    }
}

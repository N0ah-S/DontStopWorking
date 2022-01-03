package de.deverror.dsw.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

public class StaticUtil {
    public static boolean key(int key){
        return Gdx.input.isKeyPressed(key);
    }

    public static int width(){
        return Gdx.graphics.getWidth();
    }

    public static int height(){
        return Gdx.graphics.getHeight();
    }
}

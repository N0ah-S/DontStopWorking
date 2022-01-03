package de.deverror.dsw.util;

import com.badlogic.gdx.Gdx;

public class StaticUtil {
    public static boolean key(int key){
        return Gdx.input.isKeyPressed(key);
    }
}

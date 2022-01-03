package de.deverror.dsw.util;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class TileUtils {

    public static int TILE_SIZE = 64;

    public static void renderTile(SpriteBatch batch, Texture tileset, int x, int y, int id) {
        batch.draw(tileset, x * 64, y * 64,
                (id % 16) * TILE_SIZE, Math.floorDiv(id, 16) * TILE_SIZE,
                TILE_SIZE, TILE_SIZE); //ToDo performance?
    }

}

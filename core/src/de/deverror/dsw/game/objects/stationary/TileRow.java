package de.deverror.dsw.game.objects.stationary;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import de.deverror.dsw.game.objects.Entity;
import de.deverror.dsw.util.StaticUtil;
import de.deverror.dsw.util.TileUtils;

public class TileRow implements Entity {

    final int TILE_SIZE = 64;

    private Texture tileset; //reference

    private int[] ids;
    private int y;

    public TileRow(Texture tileset, int y, int width) {
        this.tileset = tileset;
        this.y = y;

        ids = new int[width];
    }

    @Override
    public void render(SpriteBatch batch) {
        batch.setColor(StaticUtil.KINDA_DARK);
        for (int x = 0; x < ids.length; x++) {
            TileUtils.renderTile(batch, tileset, x, y, ids[x]);
        }
        batch.setColor(Color.WHITE);
    }

    public void set(int x, int cell) {
        ids[x] = cell;
    }

    @Override
    public void update(float delta) {}

    @Override
    public float getY() {
        return y * 64;
    }

    @Override
    public float getX() {
        return -1;
    }

}

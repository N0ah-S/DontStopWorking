package de.deverror.dsw.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import de.deverror.dsw.game.objects.Entity;
import de.deverror.dsw.game.objects.EntitySortComparator;
import de.deverror.dsw.game.objects.stationary.TileRow;
import de.deverror.dsw.util.Assets;
import de.deverror.dsw.util.StaticUtil;
import de.deverror.dsw.util.TileUtils;

import java.util.ArrayList;

public class SortRenderer {

    private GameScreen game;
    private ArrayList<Entity> entities;

    private TiledMapTileLayer fg_layer;
    private TiledMapTileLayer bg_layer;
    private TiledMap map;

    private Texture tileset;

    public SortRenderer(GameScreen game) {
        this.game   = game;
        entities    = game.entities;
        map         = game.tiledMap;

        tileset     = game.assets.get(Assets.TILESET);

        bg_layer    = (TiledMapTileLayer) map.getLayers().get(0);
        fg_layer    = (TiledMapTileLayer) map.getLayers().get(1);
        compileTileObjects();
    }

    public void render(SpriteBatch batch) {
        batch.setColor(StaticUtil.KINDA_DARK);
        for (int y = bg_layer.getHeight()-1; y >= 0 ; y--) {
            for (int x = 0; x < bg_layer.getWidth(); x++) {
                TileUtils.renderTile(batch, tileset, x, y, tileID(bg_layer, x, y));
            }
        }
        batch.setColor(Color.WHITE);

        entities.sort(EntitySortComparator.INSTANCE); //ToDo think about performance
        game.renderFloorParticles(batch);
        for(int i = 0; i < entities.size(); i++) {
            entities.get(i).render(batch);
        }
    }

    public void compileTileObjects() {
        for(int y = fg_layer.getHeight()-1; y >= 0; y--) {
            TileRow row = new TileRow(tileset, y, fg_layer.getWidth());
            for(int x = 0; x < fg_layer.getWidth(); x++) {
                row.set(x, tileID(fg_layer, x, y));
            }
            entities.add(row);
        }
    }

    private int tileID(TiledMapTileLayer layer, int x, int y) {
        TiledMapTileLayer.Cell c = layer.getCell(x, y);
        return c == null ? 0 : c.getTile().getId() - 1;
    }

}

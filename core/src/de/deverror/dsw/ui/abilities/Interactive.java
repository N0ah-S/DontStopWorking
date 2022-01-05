package de.deverror.dsw.ui.abilities;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import de.deverror.dsw.game.objects.Entity;

public interface Interactive extends Entity {
    public void activate();
    public void isReady();
    public TextureRegion getIcon();
}

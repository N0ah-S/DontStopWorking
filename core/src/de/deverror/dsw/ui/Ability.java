package de.deverror.dsw.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import de.deverror.dsw.game.objects.Entity;
import de.deverror.dsw.game.objects.moving.Player;
import de.deverror.dsw.ui.abilities.Interactive;

import static de.deverror.dsw.util.StaticUtil.len;

public class Ability {
    int key;
    float cooldown, current;

    float iconPosX, iconPosY;
    TextureRegion icon;
    TextureRegion interactiveIcon;

    boolean proximity;
    Player player;

    public Ability(Player owner, float x, float y, TextureRegion icon, float cooldown, int key){
        this.key = key;
        iconPosX = x;
        iconPosY = y;
        current = cooldown;
        this.cooldown = cooldown;
        this.icon = icon;
        proximity = false;
        player = owner;
    }
    public Ability(Player owner, float x, float y, TextureRegion icon, float cooldown, int key, boolean proximity){
        this.key = key;
        iconPosX = x;
        iconPosY = y;
        current = cooldown;
        this.cooldown = cooldown;
        this.icon = icon;
        this.proximity = proximity;
        player = owner;
    }

    public void render(SpriteBatch batch){
        if(!isReady()) batch.setColor(Color.GRAY);
        batch.draw(icon, iconPosX, iconPosY);
        batch.setColor(Color.WHITE);
        if(interactiveIcon != null) batch.draw(interactiveIcon, iconPosX+40, iconPosY-40);
    }
    public void update(float delta){
        current -= cooldown;
        if(current < 0) current = 0;
    }

    public boolean isReady(){
        if(proximity && current == 0){
            for(Entity entity : player.main.entities){
                if(entity instanceof Interactive){
                    float dist = len(entity.getX()-player.getX(), entity.getY()-player.getY());
                    if(dist < 120){
                        interactiveIcon = ((Interactive) entity).getIcon();
                        return true;
                    }
                }
            }
            interactiveIcon = null;
            return false;
        }else{
            return current == 0;
        }
    }

    public void use(){
        current = cooldown;
    }
}

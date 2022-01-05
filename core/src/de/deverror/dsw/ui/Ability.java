package de.deverror.dsw.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import de.deverror.dsw.game.GameScreen;
import de.deverror.dsw.game.objects.Entity;
import de.deverror.dsw.game.objects.Reciever;
import de.deverror.dsw.game.objects.moving.Player;

import static de.deverror.dsw.util.StaticUtil.len;

public class Ability {
    int key;
    float cooldown, current;

    float iconPosX, iconPosY;
    TextureRegion icon;

    boolean proximity;
    Class<Entity> required;
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
    public Ability(Player owner, float x, float y, TextureRegion icon, float cooldown, int key, Class<Entity> required){
        this.key = key;
        iconPosX = x;
        iconPosY = y;
        current = cooldown;
        this.cooldown = cooldown;
        this.icon = icon;
        proximity = false;
        this.required = required;
        player = owner;
    }

    public void render(SpriteBatch batch){
        if(!isReady()) batch.setColor(Color.GRAY);
        batch.draw(icon, iconPosX, iconPosY);
        batch.setColor(Color.WHITE);
    }
    public void update(float delta){
        current -= cooldown;
        if(current < 0) current = 0;
    }

    public boolean isReady(){
        if(proximity && current == 0){
            for(Entity entity : player.main.entities){
                if(required.isInstance(entity)){
                    float dist = len(entity.getX()-player.getX(), entity.getY()-player.getY());
                    if(dist < 120) return true;
                }
            }
            return false;
        }else{
            return current == 0;
        }
    }

    public void use(){
        current = cooldown;
    }
}

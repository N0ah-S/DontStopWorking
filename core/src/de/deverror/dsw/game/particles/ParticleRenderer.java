package de.deverror.dsw.game.particles;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ParticleRenderer {
    HashMap<Integer, ParticleType> particles;

    public ParticleRenderer(){
        particles = new HashMap<>();
    }

    public void addParticleType(int id, ParticleType type){
        particles.put(id, type);
    }

    public void update(float delta){
        for(Map.Entry<Integer, ParticleType> type : particles.entrySet()){
            type.getValue().update(delta);
        }
    }

    public void render(Batch batch){
        for(Map.Entry<Integer, ParticleType> entry : particles.entrySet()){
            ParticleType type = entry.getValue();
            float centerX = type.texture.getRegionWidth()/2f;
            float centerY = type.texture.getRegionHeight()/2f;
            for(float[] particle : type.getParticles())
            batch.draw(type.texture, particle[0], particle[1], centerX, centerY, centerX*2, centerY*2, type.scale, type.scale, particle[4]);
        }
    }

    public void spawn(int id, int direction, int distribution, float x, float y, float speed, float rotSpeed, int amount, float duration){
        ParticleType type = particles.get(id);
        for(int i = 0; i < amount; i++) {
            double speedMultiplier = Math.random();
            double dir = (direction+distribution -2*distribution*Math.random())*(2*Math.PI)/360;
            type.addParticle(x, y, (float) (Math.cos(dir)*speed*speedMultiplier), (float) (Math.sin(dir)*speed*speedMultiplier), 0, (float)(rotSpeed*speedMultiplier), (float) (duration*Math.random()));
        }
    }
}

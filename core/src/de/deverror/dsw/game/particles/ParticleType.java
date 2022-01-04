package de.deverror.dsw.game.particles;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.ArrayList;

public class ParticleType {
    public TextureRegion texture; //0  1   2       3       4              5         6
    ArrayList<float[]> particles; //x, y, speedX, speedY, rotation, rotationSpeed, age
    final float DAMPING = 1;
    public float scale;

    public ParticleType(TextureRegion texture, float scale){
        particles = new ArrayList<>();
        this.texture = texture;
        this.scale = scale;
    }

    public void addParticle(float x, float y, float speedX, float speedY, float rotation, float rSpeed, float duration){
        particles.add(new float[] {x, y, speedX, speedY, rotation, rSpeed, duration});
    }

    public void update(float delta){
        for(int i = 0; i < particles.size(); i++){
            float[] old = particles.get(i);
            if(old[6] < 0){
                particles.remove(i);
                i--;
            }else{
                particles.set(i, new float[] {
                        old[0]+old[2]*delta,
                        old[1]+old[3]*delta,
                        old[2]-(old[2]/DAMPING)*delta,
                        old[3]-(old[3]/DAMPING)*delta,
                        old[4]+old[5]*delta,
                        old[5]-(old[5]/DAMPING)*delta,
                        old[6]-delta});
            }

        }
    }

    public ArrayList<float[]> getParticles(){
        return particles;
    }
}

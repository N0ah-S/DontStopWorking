package de.deverror.dsw.game.objects.moving;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import de.deverror.dsw.game.GameScreen;
import de.deverror.dsw.game.objects.Entity;
import de.deverror.dsw.game.objects.Reciever;
import de.deverror.dsw.ui.Ability;
import de.deverror.dsw.ui.abilities.Interactive;
import de.deverror.dsw.util.Animation;
import de.deverror.dsw.util.Animator;
import de.deverror.dsw.util.Assets;
import de.deverror.dsw.util.StaticUtil;

import java.util.HashMap;
import java.util.Map;

import static de.deverror.dsw.util.StaticUtil.*;
import static de.deverror.dsw.util.GameSettings.*;

public class Player implements Entity {
    Body body;
    public GameScreen main;

    Animator animator;
    int dir;
    float walkcooldown;

    HashMap<Integer, Ability> abilities;
    Sound[] screams;

    public Player(GameScreen main){
        this.main = main;
        BodyDef bodyDef = new BodyDef();
        bodyDef.linearDamping = 0f;
        bodyDef.fixedRotation = true;
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        body = main.physicsWorld.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(40, 8, new Vector2(32, 3), 0);

        /*CircleShape shape = new CircleShape();
        shape.setPosition(new Vector2(32, 10));
        shape.setRadius(20f);*/

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1f;

        body.createFixture(fixtureDef);
        body.setTransform(64 * 8, 64 * 7f, 0);
        shape.dispose();

        animator = new Animator();
        loadAnimations();
        loadAbilities();

        screams = new Sound[4];
        for (int i = 0; i < screams.length; i++) {
            screams[i] = Gdx.audio.newSound(Gdx.files.internal("sounds/dsw/" + i + ".wav"));
        }
    }

    @Override
    public float getX() {
        return body.getPosition().x;
    }

    @Override
    public float getY() {
        return body.getPosition().y;
    }
    @Override
    public float getSortY(){
        return getY();
    }

    @Override
    public void render(SpriteBatch batch) {
        batch.draw(animator.getTexture(), getX()-70, getY()-15);
    }

    public void renderHUD(SpriteBatch batch){
        for(Map.Entry<Integer, Ability> entry : abilities.entrySet()){
            entry.getValue().render(batch);
        }
    }

    @Override
    public void update(float delta) {
        for(Map.Entry<Integer, Ability> entry : abilities.entrySet()){
            entry.getValue().update(delta);
        }
        walkcooldown -= delta;
        Vector2 velocity = new Vector2(0, 0);
        if(key(UP)) {
            velocity.y += PLAYERSPEED;
            dir = 0;
        }
        if(key(DOWN)) {
            velocity.y -= PLAYERSPEED;
            dir = 1;
        }
        if(key(LEFT)) {
            velocity.x -= PLAYERSPEED;
            dir = 2;
        }
        if(key(RIGHT)) {
            velocity.x += PLAYERSPEED;
            dir = 3;
        }
        if(key(SCREAM) && abilities.get(0).isReady()) {
            scream();
            abilities.get(0).use();
        }
        if(key(INTERACT) && abilities.get(0).isReady()){
            for(Entity entity : main.entities){
                if(entity instanceof Interactive){
                    float dist = len(entity.getX()-getX(), entity.getY()-getY());
                    if(dist < 120 && ((Interactive) entity).isReady()){
                        ((Interactive) entity).activate();
                        abilities.get(0).use();
                    }
                }
            }
        }
        if(velocity.x == 0 && velocity.y == 0) {
            animator.start(dir);
        } else {
            animator.start(4 + dir);
            if(walkcooldown <= 0){
                for(Entity entity : main.entities){
                    if(entity instanceof Reciever){
                        float dist = len(entity.getX()-getX(), entity.getY()-getY());
                        if(dist < 100) ((Reciever) entity).engage(0, 4);
                    }
                }
                walkcooldown = 2;
            }
        }

        body.setLinearVelocity(velocity);
        animator.tick(delta);
    }

    private void scream() {
        screams[random.nextInt(screams.length)].play();
        main.shake();
        for(Entity entity : main.entities){
            if(entity instanceof Reciever){
                float dist = len(entity.getX()-getX(), entity.getY()-getY());
                if(dist < 120) ((Reciever) entity).engage(1, 10);
            }
        }
    }

    private void loadAnimations(){
        TextureAtlas atlas = new TextureAtlas(Assets.CHEFATLAS);
        animator.addAnimation(0, new Animation(animator, getIndexAnimation("chef_idleback", 11, atlas), 1f, 0));
        animator.addAnimation(1, new Animation(animator, getIndexAnimation("chef_idlefront", 11, atlas), 1f, 1));
        animator.addAnimation(2, new Animation(animator, getIndexAnimation("chef_idleleft", 11, atlas), 1f, 2));
        animator.addAnimation(3, new Animation(animator, getIndexAnimation("chef_idleright", 11, atlas), 1f, 3));
        animator.addAnimation(4, new Animation(animator, getIndexAnimation("chef_walkback", 7, atlas), 0.7f, 0));
        animator.addAnimation(5, new Animation(animator, getIndexAnimation("chef_walkfront", 11, atlas), 0.7f, 1));
        animator.addAnimation(6, new Animation(animator, getIndexAnimation("chef_walkleft", 8, atlas), 0.7f, 2));
        animator.addAnimation(7, new Animation(animator, getIndexAnimation("chef_walkright", 8, atlas), 0.7f, 3));
    }

    private void loadAbilities(){
        abilities = new HashMap<>();
        abilities.put(0, new Ability(this, 150, 30, main.textureAtlas.findRegion("Icon_Megaphon"), 2f, SCREAM));
        abilities.put(1, new Ability(this, 300, 30, main.textureAtlas.findRegion("icon_interact"), 0f, INTERACT));
    }
}


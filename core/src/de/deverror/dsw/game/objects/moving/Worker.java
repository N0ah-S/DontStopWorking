package de.deverror.dsw.game.objects.moving;

import com.badlogic.gdx.ai.steer.SteerableAdapter;
import com.badlogic.gdx.ai.steer.SteeringAcceleration;
import com.badlogic.gdx.ai.steer.behaviors.*;
import com.badlogic.gdx.ai.steer.utils.rays.CentralRayWithWhiskersConfiguration;
import com.badlogic.gdx.ai.steer.utils.rays.ParallelSideRayConfiguration;
import com.badlogic.gdx.ai.steer.utils.rays.RayConfigurationBase;
import com.badlogic.gdx.ai.steer.utils.rays.SingleRayConfiguration;
import com.badlogic.gdx.ai.utils.Collision;
import com.badlogic.gdx.ai.utils.Ray;
import com.badlogic.gdx.ai.utils.RaycastCollisionDetector;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import de.deverror.dsw.game.GameScreen;
import de.deverror.dsw.game.objects.Entity;
import de.deverror.dsw.game.objects.Reciever;
import de.deverror.dsw.game.objects.moving.ai.Box2dRaycastCollisionDetector;
import de.deverror.dsw.util.Animation;
import de.deverror.dsw.util.Animator;
import de.deverror.dsw.util.StaticUtil;

import java.util.ArrayList;

import static de.deverror.dsw.util.GameSettings.*;
import static de.deverror.dsw.util.StaticUtil.getAnimation;
import static de.deverror.dsw.util.StaticUtil.getIndexAnimation;

public class Worker extends SteerableAdapter<Vector2> implements Entity, Reciever {

    GameScreen game;

    TextureRegion tex, ok, notOk;
    Color color;


    public float interest;
    float hx, hy, x, y;
    Body body;

    float tolerance, speed; //target specifications
    ArrayList<Vector2> targets;
    State state = State.Working;

    TextureRegion[] bubbles;
    float talkyTime, waitTime;
    int bubbleState;

    Animator animator;
    boolean flip;

    public Worker(float x, float y, GameScreen game) {
        this.hx = x;
        this.hy = y;
        this.x = x;
        this.y = y;

        interest = (float) (MAXINTEREST * (1 + Math.random()));
        this.game = game;

        tex = game.textureAtlas.findRegion("generic_worker");
        notOk = game.textureAtlas.findRegion("notOk");
        ok = game.textureAtlas.findRegion("ok");

        color = new Color(1,1,1,1).sub(StaticUtil.randomColor().mul(0.2f)); //darkening against glow
        color.a = 1;
        createBody();

        targets = new ArrayList<>();

        speed = 100;
        tolerance = 10;

        animator = new Animator();
        loadAnimations();

        bubbles = StaticUtil.getAnimation("speech", 6, game.textureAtlas);
    }

    public void createBody() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.linearDamping = 0f;
        bodyDef.fixedRotation = true;
        bodyDef.type = BodyDef.BodyType.KinematicBody;
        body = game.physicsWorld.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(16, 8, new Vector2(16, 3), 0);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1f;

        body.createFixture(fixtureDef);
        body.setTransform(x, y - 26, 0);
        shape.dispose();

        interest = 12; //StaticUtil.random.nextInt(4); //UnToDo actual values

    }

    @Override
    public void render(SpriteBatch batch) {
        batch.setColor(color);
        if(animator.getTexture().isFlipX() != flip) animator.getTexture().flip(true, false);
        batch.draw(animator.getTexture(), (int) body.getPosition().x, (int) body.getPosition().y);
        batch.setColor(Color.LIGHT_GRAY);

        //Pop Elements
        float pY = body.getPosition().y + 100;
        float percentage = Math.min(interest, MAXINTEREST) / MAXINTEREST;

        if(state != State.Talking) {
            batch.draw(ok, (int) body.getPosition().x - 5, pY, 50 * percentage, 15);
            batch.draw(notOk, body.getPosition().x + 50 * percentage - 5, pY, 50 * (1 - percentage), 15);
        } else if(waitTime < 0){
            batch.setColor(0.75f, 0.75f, 0.75f, Math.min(0.3f, talkyTime) / 0.3f);
            batch.draw(bubbles[bubbleState], (int) body.getPosition().x - 20, pY, 45, 45);
        }
        batch.setColor(Color.WHITE);
    }

    @Override
    public void update(float delta) {
        flip = false;
        animator.tick(delta);

        if(state == State.Talking) {
            if(talkyTime < 0) {
                bubbleState = StaticUtil.random.nextInt(6);
                talkyTime   = StaticUtil.random.nextFloat() * 3.5f + 0.3f;
                waitTime    = StaticUtil.random.nextFloat() * 5;
            }
            if(waitTime > 0) waitTime -= delta;
            else {
                talkyTime -= delta;
            }
        }

        x = body.getPosition().x;
        y = body.getPosition().y;
        if(!targets.isEmpty()) {
            float tx = targets.get(0).x;
            float ty = targets.get(0).y;

            boolean left = (tx + tolerance < x);
            boolean right = (tx - tolerance > x);
            boolean up = (ty + tolerance < y);
            boolean down = (ty - tolerance > y);

            if(state == State.Walking || state == (State.Talking)) {
                float dstC = getPosition().dst(game.worldManager.coffee.getX(), game.worldManager.coffee.getY());
                for (Worker w : game.worldManager.workers) {
                    float dst = getPosition().dst(w.getPosition());

                    if(dst > 2 && dst < (65 - dstC * 0.1f)) {
                        state = State.Talking;
                        body.setLinearVelocity(0, 0);
                        Vector2 dir = getPosition().sub(w.getPosition()).nor();
                        if(Math.abs(dir.x) > Math.abs(dir.y)) {
                            animator.start(3);
                            flip = dir.x < 0;
                        } else {
                            if (dir.y < 0) {
                                animator.start(0);
                            } else {
                                animator.start(1);
                            }
                        }
                        return;
                    }
                }
            }

            Vector2 direction = body.getLinearVelocity();
            if(direction.len() > 0.1f) {
                if(Math.abs(direction.x) > Math.abs(direction.y)){
                    if(direction.x > 0){
                        animator.start(7);
                    }else{
                        animator.start(6);
                        flip = true;
                    }
                }else{
                    if(direction.y > 0){
                        animator.start(4);
                    }else{
                        animator.start(5);
                    }
                }
            } else if(state == State.Working){
                animator.start(2);
            } else {
                animator.start(8);
            }

            if (left && !right)         body.setLinearVelocity(-speed, 0);
            else if (!left && right)    body.setLinearVelocity( speed, 0);
            else if (up && !down)       body.setLinearVelocity(0, -speed);
            else if (!up && down)       body.setLinearVelocity(0,  speed);
            else {
                targets.remove(0);
                body.setLinearVelocity(0, 0);
                if(!targets.isEmpty()) return;
                switch (state) {
                    case Walking:
                        state = State.Coffee;
                        game.worldManager.coffee.makeCoffee(this);
                        break;
                    case GoingBack:
                        state = State.Working;
                        break;
                }
            }
        }

        if(state != State.Working && state == State.Talking) return;
        //Pop Elements
        interest -= delta;
        if (interest < 0) {
            interest = 0;
            if(Math.random() > 0.97f) makeCoffeeBreak();
        }
    }

    public void engage(int intensity, int amount) {
        interest += amount;
        if (interest > MAXINTEREST) interest = MAXINTEREST;
        if(state == State.Talking) backToWork(1);
    }

    @Override
    public Vector2 getPosition() {
        return body.getPosition();
    }

    @Override
    public Vector2 getLinearVelocity() {
        return body.getLinearVelocity();
    }

    @Override
    public float getMaxLinearAcceleration() {
        return 200;
    }

    @Override
    public float vectorToAngle(Vector2 vector) {
        return StaticUtil.vectorToAngle(vector);
    }

    @Override
    public Vector2 angleToVector(Vector2 outVector, float angle) {
        return StaticUtil.angleToVector(outVector, angle);
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

    public void backToWork(int amount) {
        if(state == State.GoingBack) return;
        state = State.GoingBack;
        engage(1, amount);

        target(x + 50, y - 60);
        target(hx + 134,  y - 60);
        target(hx + 134, hy - 40);
        target(hx, hy - 40);
        target(hx, hy);
    }

    public void makeCoffeeBreak() {
        if(game.worldManager.coffee.orderCoffee(this) != 0) {
            state = State.Walking;
            target(x, y - 40);
            target(x + 134, y - 40);
            target(x + 134, game.worldManager.coffee.getY() - 90);
            target(game.worldManager.coffee.getX(), game.worldManager.coffee.getY() - 90);
            target(game.worldManager.coffee.getX(), game.worldManager.coffee.getY() - 20);
        }
    }

    public float getWorkEfficiency() {
        return state == State.Working ? interest : 0;
    }

    enum State {
        Working, Walking, Coffee, Talking, GoingBack
    }

    public void target(float x, float y) {
        targets.add(new Vector2(x, y));
    }

    public void loadAnimations(){
        TextureAtlas atlas = game.textureAtlas;
        animator.addAnimation(0, new Animation(animator, getAnimation("worker/sleep_back", 20, atlas), 1f, 0));
        animator.addAnimation(1, new Animation(animator, getAnimation("worker/sleep_front", 12, atlas), 1f, 1));
        animator.addAnimation(2, new Animation(animator, getAnimation("worker/tap_back", 12, atlas), 1f, 2));
        animator.addAnimation(3, new Animation(animator, getAnimation("worker/tap_front", 8, atlas), 1f, 3));
        animator.addAnimation(4, new Animation(animator, getAnimation("worker/walk_back", 3, atlas), 0.7f, 0));
        animator.addAnimation(5, new Animation(animator, getAnimation("worker/walk_front", 3, atlas), 0.7f, 1));
        animator.addAnimation(6, new Animation(animator, getAnimation("worker/walk_left", 3, atlas), 0.7f, 2));
        animator.addAnimation(7, new Animation(animator, getAnimation("worker/walk_right", 3, atlas), 0.7f, 3));
        animator.addAnimation(8, new Animation(animator, getAnimation("worker/sleep_front", 1, atlas), 5f, 8));
    }
}

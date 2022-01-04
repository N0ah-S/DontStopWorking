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
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import de.deverror.dsw.game.GameScreen;
import de.deverror.dsw.game.objects.Entity;
import de.deverror.dsw.game.objects.moving.ai.Box2dRaycastCollisionDetector;
import de.deverror.dsw.util.StaticUtil;

import java.util.ArrayList;

import static de.deverror.dsw.util.GameSettings.*;

public class Worker extends SteerableAdapter<Vector2> implements Entity {

    GameScreen game;

    TextureRegion tex, ok, notOk;
    Color color;


    public float interest;
    float hx, hy, x, y;
    private RayConfigurationBase<Vector2>[] rayConfigurations;
    Body body;

    RaycastCollisionDetector<Vector2> raycastCollisionDetector;
    float tolerance, speed; //target specifications
    ArrayList<Vector2> targets;
    State state = State.Working;

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

        color = StaticUtil.randomColor();
        createBody();
        initActor();

        targets = new ArrayList<>();

        speed = 64;
        tolerance = 10;

    }

    public void initActor() {

        rayConfigurations = (RayConfigurationBase<Vector2>[]) new RayConfigurationBase[]{
                new SingleRayConfiguration<Vector2>(this, 1000),
                new ParallelSideRayConfiguration<Vector2>(this, 1000, this.getBoundingRadius()),
                new CentralRayWithWhiskersConfiguration<Vector2>(this, 1000,
                        40, 35 * MathUtils.degreesToRadians)};
        raycastCollisionDetector = new Box2dRaycastCollisionDetector(game.physicsWorld);

    }

    public void createBody() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.linearDamping = 0f;
        bodyDef.fixedRotation = true;
        bodyDef.type = BodyDef.BodyType.KinematicBody;
        body = game.physicsWorld.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(16, 8, new Vector2(16, 3), 0);

        /*CircleShape shape = new CircleShape();
        shape.setPosition(new Vector2(32, 10));
        shape.setRadius(20f);*/

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1f;

        body.createFixture(fixtureDef);
        body.setTransform(x, y - 26, 0);
        shape.dispose();

        if(y == 500) interest = 0;
    }

    @Override
    public void render(SpriteBatch batch) {
        batch.setColor(color);
        batch.draw(tex, (int) body.getPosition().x, (int) body.getPosition().y);
        batch.setColor(Color.WHITE);

        //Pop Elements
        float pY = body.getPosition().y + 100;
        float percentage = Math.min(interest, MAXINTEREST) / MAXINTEREST;

        batch.draw(ok, (int) body.getPosition().x, (int) pY, 50 * percentage, 15);
        batch.draw(notOk, body.getPosition().x + 50 * percentage, pY, 50 * (1 - percentage), 15);
    }

    @Override
    public void update(float delta) {
        x = body.getPosition().x;
        y = body.getPosition().y;
        if(!targets.isEmpty()) {
            float tx = targets.get(0).x;
            float ty = targets.get(0).y;

            Collision<Vector2> collision = null;
            boolean hit = raycastCollisionDetector.findCollision(collision,
                    new Ray<Vector2>(body.getPosition(), targets.get(0)));
            if(collision != null && body.getPosition().dst(collision.point) < body.getPosition().dst(targets.get(0))) {
                /*Collision ahead
                System.out.println("Wooohoooooo");
                body.setTransform(2000, 300, 45);
                targets.clear();
                target(collision.point.x - 60, collision.point.y);*/
            }

            boolean left = (tx + tolerance < x);
            boolean right = (tx - tolerance > x);
            boolean up = (ty + tolerance < y);
            boolean down = (ty - tolerance > y);

            if(state == State.Walking && getPosition().dst(game.worldManager.coffee.getX(),
                    game.worldManager.coffee.getY()) < 190) {
                for (Worker w : game.worldManager.workers) {
                    float dst = getPosition().dst(w.getPosition());
                    body.setLinearVelocity(0, 0);
                    if(dst > 1 && dst < 40) return; //größer 1 notwendig, weil zu faul *this* rauszufiltern
                }
            }

            if (left && !right) body.setLinearVelocity(-speed, 0);
            else if (!left && right) body.setLinearVelocity(speed, 0);
            else if (up && !down) body.setLinearVelocity(0, -speed);
            else if (!up && down) body.setLinearVelocity(0, speed);
            else {
                targets.remove(0);
                body.setLinearVelocity(0, 0);
                if(!targets.isEmpty()) return;
                switch (state) {
                    case Walking:
                        state = State.Coffee;
                        game.worldManager.coffee.makeCoffee(this);
                        System.out.println("Make Coffee ");
                        break;
                    case GoingBack:
                        state = State.Working;
                        break;
                }
            }
        }

        if(state != State.Working) return;
        //Pop Elements
        interest -= delta * 2;
        if (interest < 0) {
            interest = 0;
            if(Math.random() > 0.9f) makeCoffeeBreak();
        }
    }

    public void motivate(float amount) {
        interest += amount;
        if (interest > MAXINTEREST) interest = MAXINTEREST;
        backToWork();
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

    public void backToWork() {
        if(state == State.GoingBack) return;
        state = State.GoingBack;
        motivate(2);

        target(x + 50, y - 60);
        target(hx + 134,  y - 60);
        target(hx + 134, hy - 40);
        target(hx, hy - 40);
        target(hx, hy);
    }

    public void makeCoffeeBreak() {
        System.out.println("Coffee Break");
        if(game.worldManager.coffee.orderCoffee(this) != 0) {
            System.out.println("> Coffee Break");
            state = State.Walking;
            target(x, y - 40);
            target(x + 134, y - 40);
            target(x + 134, game.worldManager.coffee.getY() - 90);
            target(game.worldManager.coffee.getX(), game.worldManager.coffee.getY() - 90);
            target(game.worldManager.coffee.getX(), game.worldManager.coffee.getY() - 20);
        }
    }

    enum State {
        Working, Walking, Coffee, Talking, GoingBack
    }

    public void target(float x, float y) {
        targets.add(new Vector2(x, y));
    }


}
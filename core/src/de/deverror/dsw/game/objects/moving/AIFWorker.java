package de.deverror.dsw.game.objects.moving;

import com.badlogic.gdx.ai.steer.SteerableAdapter;
import com.badlogic.gdx.ai.steer.SteeringAcceleration;
import com.badlogic.gdx.ai.steer.behaviors.Arrive;
import com.badlogic.gdx.ai.steer.behaviors.PrioritySteering;
import com.badlogic.gdx.ai.steer.behaviors.RaycastObstacleAvoidance;
import com.badlogic.gdx.ai.steer.proximities.RadiusProximity;
import com.badlogic.gdx.ai.steer.utils.rays.CentralRayWithWhiskersConfiguration;
import com.badlogic.gdx.ai.steer.utils.rays.ParallelSideRayConfiguration;
import com.badlogic.gdx.ai.steer.utils.rays.RayConfigurationBase;
import com.badlogic.gdx.ai.steer.utils.rays.SingleRayConfiguration;
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
import de.deverror.dsw.game.objects.moving.ai.Target;
import de.deverror.dsw.util.StaticUtil;

import static de.deverror.dsw.util.GameSettings.MAXINTEREST;

public class AIFWorker extends SteerableAdapter<Vector2> implements Entity {

    GameScreen game;

    TextureRegion tex, ok, notOk;
    Color color;

    public float interest;
    float hx, hy, x, y;
    private static final SteeringAcceleration<Vector2> steeringOutput =
            new SteeringAcceleration<Vector2>(new Vector2());
    private RayConfigurationBase<Vector2>[] rayConfigurations;
    private RaycastObstacleAvoidance<Vector2> raycastObstacleAvoidanceSB;
    PrioritySteering<Vector2> prioritySteeringSB;
    Body body;

    public AIFWorker(float x, float y, GameScreen game){
        this.hx = x;
        this.hy = y;
        this.x  = x;
        this.y  = y;

        interest = MAXINTEREST;
        this.game = game;

        tex     = game.textureAtlas.findRegion("generic_worker");
        notOk   = game.textureAtlas.findRegion("notOk");
        ok      = game.textureAtlas.findRegion("ok");

        color = StaticUtil.randomColor();
        createBody();
        initActor();
    }

    public void initActor() {

        RadiusProximity<Vector2> proximity = new RadiusProximity<Vector2>(this, game.worldManager.workers,
                this.getBoundingRadius() * 4);
        RayConfigurationBase<Vector2>[] localRayConfigurations = new RayConfigurationBase[] {
                new SingleRayConfiguration<Vector2>(this, 100),
                new ParallelSideRayConfiguration<Vector2>(this, 100, this.getBoundingRadius()),
                new CentralRayWithWhiskersConfiguration<Vector2>(this, 100, 40, 35 * MathUtils.degreesToRadians)};
        rayConfigurations = localRayConfigurations;
        int rayConfigurationIndex = 0;
        RaycastCollisionDetector<Vector2> raycastCollisionDetector = new Box2dRaycastCollisionDetector(game.physicsWorld);
        raycastObstacleAvoidanceSB = new RaycastObstacleAvoidance<Vector2>(this, rayConfigurations[rayConfigurationIndex],
                raycastCollisionDetector, 40);

        prioritySteeringSB = new PrioritySteering<Vector2>(this, 0.0001f);
        //prioritySteeringSB.add(raycastObstacleAvoidanceSB);
        prioritySteeringSB.add(new Arrive<Vector2>(this, new Target(x, y + 103)));
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
        body.setTransform(x, y -26, 0);
        shape.dispose();
    }

    @Override
    public void render(SpriteBatch batch) {
        batch.setColor(color);
        batch.draw(tex, (int) body.getPosition().x, (int) body.getPosition().y);
        batch.setColor(Color.WHITE);

        //Pop Elements
        float pY = body.getPosition().y + 100;
        float percentage = interest/MAXINTEREST;

        batch.draw(ok, (int)body.getPosition().x, (int)pY, 50 * percentage, 15);
        batch.draw(notOk, body.getPosition().x + 50 * percentage, pY, 50 * (1 - percentage), 15);
    }

    @Override
    public void update(float delta) {
        prioritySteeringSB.calculateSteering(steeringOutput);
        System.out.println(steeringOutput.linear.x + "/" + steeringOutput.linear.y);
        body.setLinearVelocity(steeringOutput.linear);

        //Pop Elements
        interest -= delta;
        if(interest < 0) interest = 0;
    }

    public void motivate(float amount){
        interest += amount;
        if(interest > MAXINTEREST) interest = MAXINTEREST;
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
    public float vectorToAngle (Vector2 vector) {
        return StaticUtil.vectorToAngle(vector);
    }

    @Override
    public Vector2 angleToVector (Vector2 outVector, float angle) {
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

}

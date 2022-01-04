package de.deverror.dsw.game.objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import de.deverror.dsw.game.GameScreen;
import de.deverror.dsw.game.objects.stationary.CoffeeMachine;
import de.deverror.dsw.util.Assets;
import de.deverror.dsw.game.objects.moving.Worker;

import java.util.ArrayList;

import static de.deverror.dsw.util.GameSettings.*;

public class WorldManager {

    public ArrayList<Worker> workers;
    public CoffeeMachine coffee;


    float interest, interestMax;
    TextureRegion ok, notOk;
    Texture warning;
    GameScreen main;

    public WorldManager(GameScreen main){
        workers = new ArrayList<>();
        this.main = main;

        ok = main.textureAtlas.findRegion("ok");
        notOk = main.textureAtlas.findRegion("notOk");
        warning = main.assets.get(Assets.warning);

        coffee = new CoffeeMachine(810, 740, main);

    }

    public Worker registerWorker(Worker worker) {
        workers.add(worker);
        main.entities.add(worker);
        interestMax = MAXINTEREST*workers.size();

        interest += worker.interest;
        return worker;
    }

    public void updateInterest() {
        interest = 0;
        for(Worker worker : workers) interest += worker.interest;

        if(interest/interestMax < MININTEREST) System.out.println("You are dead!");
    }

    public void render(SpriteBatch batch) {
        float percentage = interest/interestMax;

        batch.draw(ok, 0, -30, 0, 30, 500*percentage, 30, 1, 1, 90);
        batch.draw(notOk, 0, 500*percentage-30, 0, 30, 500*(1-percentage), 30, 1, 1, 90);
        batch.draw(warning, 0, 500*MININTEREST-17);
    }
}

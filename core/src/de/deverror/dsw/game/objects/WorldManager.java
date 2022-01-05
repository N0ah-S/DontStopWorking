package de.deverror.dsw.game.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import de.deverror.dsw.game.GameScreen;
import de.deverror.dsw.game.objects.stationary.CoffeeMachine;
import de.deverror.dsw.util.Assets;
import de.deverror.dsw.game.objects.moving.Worker;

import java.util.ArrayList;

import static de.deverror.dsw.util.GameSettings.*;

public class WorldManager {

    private static final int POINTS = 0, X = 1, Y = 2;

    public ArrayList<Worker> workers;
    public CoffeeMachine coffee;


    float interest, interestMax;
    TextureRegion ok, notOk;
    Texture warning;
    GameScreen main;


    BitmapFont popupFont;
    BitmapFont font;
    float fade, points = 0, untilNextCheck = 1;
    String text;

    float[][] popups;

    public WorldManager(GameScreen main){
        workers = new ArrayList<>();
        this.main = main;

        ok = main.textureAtlas.findRegion("ok");
        notOk = main.textureAtlas.findRegion("notOk");
        warning = main.assets.get(Assets.warning);


        font = new BitmapFont(Gdx.files.internal("skin/Unnamed.fnt"));
        font.setColor(0.25f, 1f, 0.12f, 1);
        popupFont = new BitmapFont(Gdx.files.internal("skin/Unnamed.fnt"));
        popupFont.getData().setScale(0.3f);

        text = Integer.toString((int) points);
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
    }

    public void renderUntransformed(SpriteBatch batch) {
        float percentage = interest/interestMax;

        batch.draw(ok, 0, -30, 0, 30, 500 * percentage, 30, 1, 1, 90);
        batch.draw(notOk, 0, 500*percentage-30, 0, 30, 500*(1-percentage), 30, 1, 1, 90);
        batch.draw(warning, 0, 500 * MININTEREST - 17);

        font.draw(batch, text, Gdx.graphics.getWidth() - 30 - text.length() * 75, Gdx.graphics.getHeight() - 20);
    }

    public void renderTransformed(SpriteBatch batch) {
        if(popups != null) {
            popupFont.setColor(0.3f, 1f, 0.15f, fade);
            for (int i = 0; i < popups.length; i++) {
                System.out.println(" > Y: " + popups[i][Y]);
                popupFont.draw(batch, "+" + ((int) popups[i][POINTS] / 3), popups[i][X], popups[i][Y] - untilNextCheck * 4);
            }
        }
    }

    public void update(float delta) {
        updateInterest();

        untilNextCheck -= delta;
        fade -= delta;
        if(fade < 0) popups = null;
        if(untilNextCheck <= 0) {
            untilNextCheck = 5 + (float) Math.random() * 8;
            text = Integer.toString((int) points);
            fade = 2;
            popups = new float[workers.size()][];
            for(int i = 0; i < workers.size(); i++) {
                Worker w = workers.get(i);
                popups[i] = new float[] {w.interest, w.getX() + 5, w.getY() + 120};
            }
        }
        for(int i = 0; i < workers.size(); i++) {
            points += workers.get(i).interest * 0.001f;
        }
    }
}

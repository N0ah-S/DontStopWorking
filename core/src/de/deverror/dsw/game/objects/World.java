package de.deverror.dsw.game.objects;

import de.deverror.dsw.game.objects.stationary.Worker;

import java.util.ArrayList;

import static de.deverror.dsw.util.GameSettings.*;

public class World {
    ArrayList<Worker> workers;
    float interest, interestMax;

    public World(){
        workers = new ArrayList<>();
    }

    public void registerWorker(Worker worker){
        workers.add(worker);
        interestMax = MAXINTEREST*workers.size();
        interest += worker
    }
}

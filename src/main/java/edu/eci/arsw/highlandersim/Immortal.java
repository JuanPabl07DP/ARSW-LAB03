package edu.eci.arsw.highlandersim;

import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.concurrent.atomic.AtomicInteger;

public class Immortal extends Thread {

    private ImmortalUpdateReportCallback updateCallback = null;
    private final AtomicInteger health;
    private int defaultDamageValue;
    private final List<Immortal> immortalsPopulation;
    private final String name;
    private final Random r = new Random(System.currentTimeMillis());
    private volatile boolean execution;
    private volatile boolean alive;

    public Immortal(String name, List<Immortal> immortalsPopulation, int health, int defaultDamageValue,
                    ImmortalUpdateReportCallback ucb) {
        super(name);
        this.updateCallback = ucb;
        this.name = name;
        this.immortalsPopulation = immortalsPopulation;
        this.health = new AtomicInteger(health);
        this.defaultDamageValue = defaultDamageValue;
        this.execution = true;
        this.alive = true;
    }

    public void run() {
        while (alive && this.getHealth() > 0 && !immortalsPopulation.isEmpty()) {
            // Bloque de pausa
            synchronized (immortalsPopulation) {
                while (!execution) {
                    try {
                        immortalsPopulation.wait();
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Immortal.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }

            // Verificar condiciones de terminación
            if (!alive || immortalsPopulation.size() <= 1) {
                break;
            }

            Immortal opponent;
            synchronized(immortalsPopulation) {
                int myIndex = immortalsPopulation.indexOf(this);
                if (myIndex == -1) {
                    continue;
                }

                int nextFighterIndex = r.nextInt(immortalsPopulation.size());
                if (nextFighterIndex == myIndex) {
                    nextFighterIndex = ((nextFighterIndex + 1) % immortalsPopulation.size());
                }

                try {
                    opponent = immortalsPopulation.get(nextFighterIndex);
                } catch (IndexOutOfBoundsException e) {
                    continue;
                }
            }

            if (opponent != null) {
                fight(opponent);
            }

            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void fight(Immortal i2) {
        Immortal firstLock = getLockOrderImmortal(i2);
        Immortal secondLock = firstLock == this ? i2 : this;

        synchronized (firstLock) {
            synchronized (secondLock) {
                if (this.getHealth() > 0 && i2.getHealth() > 0) {
                    int currentHealth = i2.getHealth();
                    if (currentHealth > 0) {
                        i2.changeHealth(currentHealth - defaultDamageValue);
                        this.health.addAndGet(defaultDamageValue);
                        updateCallback.processReport("Fight: " + this + " vs " + i2 + "\n");

                        if (i2.getHealth() <= 0) {
                            synchronized(immortalsPopulation) {
                                immortalsPopulation.remove(i2);
                            }
                        }
                    }
                }
            }
        }
    }

    public Immortal getLockOrderImmortal(Immortal i2) {
        int immortalOneHash = System.identityHashCode(this);
        int immortalTwoHash = System.identityHashCode(i2);
        return immortalOneHash < immortalTwoHash ? this : i2;
    }

    public void changeHealth(int v) {
        health.set(v);
    }

    public int getHealth() {
        return health.get();
    }

    public void setExecution(boolean execution) {
        this.execution = execution;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    @Override
    public String toString() {
        return name + "[" + health.get() + "]";
    }
}
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arst.concprg.prodcons;

import java.util.Queue;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.concurrent.BlockingQueue;

/**
 *
 * @author hcadavid
 */
public class Producer extends Thread {

    private BlockingQueue<Integer> queue;
    private int dataSeed = 0;
    private Random rand=null;

    public Producer(BlockingQueue<Integer> queue) {
        this.queue = queue;
        rand = new Random(System.currentTimeMillis());
    }

    @Override
    public void run() {
        while (true) {
            try {
                dataSeed += rand.nextInt(100);
                System.out.println("Producer added " + dataSeed);
                queue.add(dataSeed);
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}

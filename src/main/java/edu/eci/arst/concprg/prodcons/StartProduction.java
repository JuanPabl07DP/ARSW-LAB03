/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arst.concprg.prodcons;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.concurrent.BlockingQueue;

public class StartProduction {

    public static void main(String[] args) {

        int stockLimit = 5;
        BlockingQueue<Integer> queue = new LinkedBlockingQueue<>(stockLimit);

        new Producer(queue, stockLimit).start();
        new Consumer(queue).start();
    }
}
package com.project;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        Process.readFile();

        //FirstComeFirstServe fcfs = FirstComeFirstServe.createFCFS();
        //fcfs.calculateFCFS();

        /*RoundRobin rr = RoundRobin.createRR();
        rr.calculateRR();
        rr.display();*/
        ShortestJobFirst sjf = ShortestJobFirst.createSJF();
        sjf.calculateSJF();
        sjf.display();

        //System.out.println(Process.processes);


        //fcfs.display();

    }
}
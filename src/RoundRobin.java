package com.project;

import java.util.ArrayList;

public class RoundRobin {
    private int totalTime;
    private int idleTime;
    private double CPU_Utilization;
    private double throughput;
    private Process burstingProcess;

    private ArrayList<Process> preProcesses;
    private ArrayList<Process> waitingProcesses;
    private ArrayList<Process> ioProcesses;
    private ArrayList<Process> doneProcesses;

    private RoundRobin() {
        preProcesses = new ArrayList<>(Process.processes);
        waitingProcesses = new ArrayList<>();
        ioProcesses = new ArrayList<>();
        doneProcesses = new ArrayList<>();
        burstingProcess = null;
        CPU_Utilization = 0;
        throughput = 0;
    }

    public static RoundRobin createRR() {
        RoundRobin newRR = new RoundRobin();
        return newRR;
    }

    public int getTotalTime() {
        return totalTime;
    }

    public int getIdleTime() {
        return idleTime;
    }

    public double getCPU_Utilization() {
        return CPU_Utilization;
    }

    public double getThroughput() {
        return throughput;
    }

    public void calculateRR() {

        final int processSize = preProcesses.size();

        int timeQuantum = 5;
        int timeQuantumCounter = 0;
        int currentTime = 0;
        while (doneProcesses.size() != processSize) {

            //System.out.println(doneProcesses);
            for (Process process : Process.processes) {
                if (process.getArrivalTime() == currentTime) {
                    waitingProcesses.add(process);
                }
            }

            if (burstingProcess != null) {
                if (!burstingProcess.burstState()) { //first CPU time
                    if (burstingProcess.getCpuTotalBurstRemaining() == burstingProcess.getCpuTime2()) {
                        burstingProcess.setEndTime1(currentTime);
                        ioProcesses.add(burstingProcess);
                        timeQuantumCounter = 0;
                        burstingProcess = null;

                    }
                } else { // second CPU time
                    if (burstingProcess.getCpuTotalBurstRemaining() == 0) {
                        burstingProcess.setEndTime2(currentTime);
                        timeQuantumCounter = 0;
                        doneProcesses.add(burstingProcess);
                        burstingProcess = null;
                    }
                }
                if (burstingProcess != null) {
                    if (timeQuantum == timeQuantumCounter) {
                        waitingProcesses.remove(burstingProcess);
                        waitingProcesses.add(burstingProcess);
                        burstingProcess = null;
                        timeQuantumCounter = 0;
                    } else {
                        burstingProcess.setCpuTotalBurstRemaining(burstingProcess.getCpuTotalBurstRemaining() - 1);
                }}

            }

            if (burstingProcess == null) {
                if (!waitingProcesses.isEmpty()) {

                    if (waitingProcesses.get(0).getArrivalTime() <= currentTime) {
                        burstingProcess = waitingProcesses.get(0);
                        if (!burstingProcess.burstState()) {
                            if (burstingProcess.getStartTime1() == -1) { // if the start time in not set already
                                burstingProcess.setStartTime1(currentTime);
                            }
                        } else {
                            if (burstingProcess.getStartTime2() == -1) { // if the start time in not set already
                                burstingProcess.setStartTime2(currentTime);
                            }
                        }
                        waitingProcesses.remove(0);
                        burstingProcess.setCpuTotalBurstRemaining(burstingProcess.getCpuTotalBurstRemaining() - 1);
                    }
                } else {
                    idleTime++;
                }
            }

            for (Process process : ioProcesses) {
                if (process.getEndTime1() + process.getIoTime() == currentTime) {
                    waitingProcesses.add(process);
                    process.setBurstState(true);

                }
            }

            currentTime++;
            timeQuantumCounter++;
        }


        totalTime = currentTime - 1;
        idleTime--;
        calculateCPU_Utilization();
        calculateThroughput();
        calculateProcessStuff();
    }

    private void calculateProcessStuff() {
        for (Process process: Process.processes) {
            process.calculateResponseTime();
            process.calculateTurnAroundTime();
            process.calculateWaitingTime();
        }
    }

    public void calculateCPU_Utilization() {
        ArrayList<Process> processes = new ArrayList<>(Process.processes);
        double sum = 0;
        for (Process process : processes) {
            sum += process.getCpuTime1()+process.getCpuTime2();
        }
        CPU_Utilization = sum/getTotalTime();
    }

    public void calculateThroughput() {
        double numberOfProcesses = Process.processes.size();
        throughput = (numberOfProcesses / getTotalTime()) * 1000;
    }


    public void display() {
        System.out.println("\t\t -----------------------------------------");
        System.out.println("\t\t ---------  RoundRobin  ---------");
        System.out.println("\t\t -----------------------------------------");
        System.out.println("\t\t    ST \t\t   ET \t\t   RT \t\t   TAT \t\t   WT");
        for (Process process: Process.processes) {
            System.out.println("P" + process.getId() + "\t\t\t" + process.getStartTime1()
                    + "\t\t\t" + process.getEndTime2() + "\t\t\t" + process.getResponseTime() +
                    "\t\t\t" + process.getTurnAroundTime() + "\t\t\t" + process.getWaitingTime());
        }

        System.out.println("\t\t -----------------------------------------");
        System.out.println("\t\t -----------------------------------------");

        System.out.println("Avg response time  is: " + Process.calculateAvgResponseTime());
        System.out.println("Avg turn-around time is: " + Process.calculateAvgTATTime());
        System.out.println("Avg waiting time is: " + Process.calculateAvgWaitingTime());

        System.out.println("\t\t -----------------------------------------");
        System.out.println("\t\t -----------------------------------------");

        System.out.println("Total time is: " + getTotalTime());
        System.out.println("Idle time is: " + getIdleTime());
        System.out.println("Total Burst time is: " + Process.calculateBurstTime());
        System.out.println("CPU Util. is " + getCPU_Utilization());
        System.out.println("Throughput is " + getThroughput());

    }
}
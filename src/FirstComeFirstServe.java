package com.project;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class FirstComeFirstServe {

    private int totalTime;
    private int idleTime;
    private double CPU_Utilization;
    private double throughput;
    private Process burstingProcess;

    private ArrayList<Process> preProcesses;
    private ArrayList<Process> waitingProcesses;
    private ArrayList<Process> ioProcesses;
    private ArrayList<Process> doneProcesses;

    private FirstComeFirstServe() {
        preProcesses = new ArrayList<>(Process.processes);
        waitingProcesses = new ArrayList<>();
        ioProcesses = new ArrayList<>();
        doneProcesses = new ArrayList<>();
        burstingProcess = null;
        CPU_Utilization = 0;
        throughput = 0;
    }

    public static FirstComeFirstServe createFCFS() {
        FirstComeFirstServe newFCFS = new FirstComeFirstServe();
        return newFCFS;
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

    public void calculateFCFS(){

        final int processSize = preProcesses.size();
        ArrayList<Process> sortedProcesses = sortProcessesBasedOnArrival();

        int currentTime = 0;
        while (doneProcesses.size() != processSize) {

            for (Process process: sortedProcesses) {
                if (process.getArrivalTime() == currentTime) {
                    waitingProcesses.add(process);
                }
            }

            if (burstingProcess != null) {

                if (!burstingProcess.burstState()) { //first CPU time
                    if (burstingProcess.getStartTime1() + burstingProcess.getCpuTime1() == currentTime) {
                        burstingProcess.setEndTime1(currentTime);
                        ioProcesses.add(burstingProcess);
                        burstingProcess = null;
                    }
                } else { // second CPU time
                    if (burstingProcess.getStartTime2() + burstingProcess.getCpuTime2() == currentTime) {
                        burstingProcess.setEndTime2(currentTime);
                        doneProcesses.add(burstingProcess);
                        burstingProcess = null;
                    }
                }
            }

            if (burstingProcess == null) {
                if (!waitingProcesses.isEmpty()) {

                    if (waitingProcesses.get(0).getArrivalTime() <= currentTime) {
                        burstingProcess = waitingProcesses.get(0);
                        if (!burstingProcess.burstState()) {
                            burstingProcess.setStartTime1(currentTime);
                        } else {
                            burstingProcess.setStartTime2(currentTime);
                        }
                        waitingProcesses.remove(0);
                    }
                } else {
                    idleTime++;
                }
            }

            for (Process process: ioProcesses) {
                if (process.getEndTime1() + process.getIoTime() == currentTime) {
                    waitingProcesses.add(process);
                    waitingProcesses = sortBasedOnArrival(waitingProcesses);
                    process.setBurstState(true);

                }
            }

            for (Process p: waitingProcesses) {
                        p.setWaitingTime(p.getWaitingTime()+1);
            }

            currentTime++;
        }

        totalTime = currentTime-1;
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

    private ArrayList<Process> sortProcessesBasedOnArrival() { //sorting processes based on arrival time

        ArrayList<Process> sortedProcesses = new ArrayList<Process>(preProcesses);

        sortedProcesses.sort((o1, o2) -> {
            if (o1.getArrivalTime() < o2.getArrivalTime()) {
                return -1;
            } else if (o1.getArrivalTime() > o2.getArrivalTime()) {
                return 1;
            }
            return 0;
        });
        return sortedProcesses;
    }

    private ArrayList<Process> sortBasedOnArrival(ArrayList<Process> processes) {
        ArrayList<Process> sortedProcesses = new ArrayList<Process>(processes);

        sortedProcesses.sort((o1, o2) -> {
            if (o1.getArrivalTime() < o2.getArrivalTime()) {
                return -1;
            } else if (o1.getArrivalTime() > o2.getArrivalTime()) {
                return 1;
            }
            return 0;
        });
        return sortedProcesses;
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
        System.out.println("\t\t ---------  FirstComeFirstServe  ---------");
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


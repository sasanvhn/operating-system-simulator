package com.project;

import java.io.*;
import java.util.ArrayList;

public class Process {

    private final int id, arrivalTime, cpuTime1, ioTime, cpuTime2;
    private int responseTime, waitingTime, turnAroundTime, startTime1, startTime2, endTime1, endTime2;

    private boolean burstState;
    private int cpuTotalBurstRemaining;

    private Process(int id, int arrivalTime, int cpuTime1, int ioTime, int cpuTime2) {
        this.id = id;
        this.arrivalTime = arrivalTime;
        this.cpuTime1 = cpuTime1;
        this.ioTime = ioTime;
        this.cpuTime2 = cpuTime2;
        this.burstState = false;
        this.waitingTime = 0;
        this.cpuTotalBurstRemaining = cpuTime1 + cpuTime2;
        this.startTime1 = -1;
        this.startTime2 = -1;
    }

    public static ArrayList<Process> processes = new ArrayList<>();
    public static void createProcess(int id, int arrivalTime1, int cpuTime1, int ioTime, int cpuTime2) {
        Process newProcess = new Process(id, arrivalTime1, cpuTime1, ioTime, cpuTime2);
        processes.add(newProcess);
    }

    public int getCpuTotalBurstRemaining() {
        return cpuTotalBurstRemaining;
    }

    public void setCpuTotalBurstRemaining(int cpuTotalBurstRemaining) {
        this.cpuTotalBurstRemaining = cpuTotalBurstRemaining;
    }

    public int getEndTime1() {
        return endTime1;
    }

    public void setEndTime1(int endTime1) {
        this.endTime1 = endTime1;
    }

    public int getStartTime2() {
        return startTime2;
    }

    public void setStartTime2(int startTime2) {
        this.startTime2 = startTime2;
    }

    public boolean isBurstState() {
        return burstState;
    }

    public boolean burstState() {
        return burstState;
    }

    public void setBurstState(boolean burstState) {
        this.burstState = burstState;
    }

    public static ArrayList<Process> getProcesses() {
        return processes;
    }

    public static void setProcesses(ArrayList<Process> processes) {
        Process.processes = processes;
    }

    public int getId() {
        return id;
    }

    public int getArrivalTime() {
        return arrivalTime;
    }

    public int getCpuTime1() {
        return cpuTime1;
    }

    public int getIoTime() {
        return ioTime;
    }

    public int getCpuTime2() {
        return cpuTime2;
    }

    public int getResponseTime() {
        return responseTime;
    }

    public int getWaitingTime() {
        return waitingTime;
    }

    public int getTurnAroundTime() {
        return turnAroundTime;
    }

    public int getStartTime1() {
        return startTime1;
    }

    public int getEndTime2() {
        return endTime2;
    }

    public void setResponseTime(int responseTime) {
        this.responseTime = responseTime;
    }

    public void setWaitingTime(int waitingTime) {
        this.waitingTime = waitingTime;
    }

    public void setTurnAroundTime(int turnAroundTime) {
        this.turnAroundTime = turnAroundTime;
    }

    public void setStartTime1(int startTime1) {
        this.startTime1 = startTime1;
    }

    public void setEndTime2(int endTime2) {
        this.endTime2 = endTime2;
    }

    @Override
    public String toString() {
        return id + " " + arrivalTime + " " +
                cpuTime1 + " " + ioTime + " " + cpuTime2;
    }

    public static void readFile() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader("O:\\7th\\OS\\project\\Project\\\\proces_inputs.csv"));
        ArrayList<String> lines = new ArrayList<>();
        String line = null;
        while ((line = reader.readLine()) != null) {
            lines.add(line);
        }

        for (String i : lines) {
            if (Character.isDigit(i.charAt(0))) {
                String[] processStuff = i.split(",", 5);
                createProcess(Integer.parseInt(processStuff[0]), Integer.parseInt(processStuff[1]),
                        Integer.parseInt(processStuff[2]), Integer.parseInt(processStuff[3]), Integer.parseInt(processStuff[4]));
            }
        }
    }

    public void calculateResponseTime() {
        responseTime = getStartTime1() - getArrivalTime();
    }

    public void calculateTurnAroundTime() {
        turnAroundTime = getEndTime2() - getArrivalTime();
    }

    public void calculateWaitingTime() {
        waitingTime = getTurnAroundTime() - (getCpuTime1() + getCpuTime2()) - getIoTime();
    }

    public static double calculateAvgResponseTime() {

        double sum = 0;
        for (Process process: processes) {
            sum += process.getResponseTime();
        }
        return sum/processes.size();
    }

    public static double calculateAvgWaitingTime() {

        double sum = 0;
        for (Process process: processes) {
            sum += process.getWaitingTime();
        }
        return sum/processes.size();
    }

    public static double calculateAvgTATTime() {

        double sum = 0;
        for (Process process: processes) {
            sum += process.getTurnAroundTime();
        }
        return sum/processes.size();
    }

    public static int calculateBurstTime() {

        int sum = 0;
        for (Process process: processes) {
            sum += process.getCpuTime1() + process.getCpuTime2();
        }
        return sum;
    }

}

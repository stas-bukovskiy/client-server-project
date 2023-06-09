package edu.clientserver;

public class Main {
    public static void main(String[] args) throws InterruptedException {

        Data d = new Data();

        Worker w2 = new Worker(2, d);
        Worker w1 = new Worker(1, d);

        w2.join();

        System.out.println("end of main...");
    }
}

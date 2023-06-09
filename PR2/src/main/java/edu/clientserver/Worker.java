package edu.clientserver;

public class Worker extends Thread {

    private final int id;
    private final Data data;

    public Worker(int id, Data d) {
        this.id = id;
        data = d;
        this.start();
    }

    @Override
    public void run() {
        super.run();
        synchronized (data) {
            for (int i = 0; i < 5; i++) {
                if (id == 1) {
                    while (data.getState() != 1) {
                        try {
                            data.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    data.Tic();
                    data.notify();
                } else {
                    while (data.getState() != 2) {
                        try {
                            data.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    data.Tak();
                    data.notify();
                }
            }
        }
    }
}
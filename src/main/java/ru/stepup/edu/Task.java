package ru.stepup.edu;

import java.util.concurrent.TimeUnit;

public class Task implements Runnable {
    private String name;

    public Task(String s)
    {
        this.name = s;
    }

    @Override
    public void run() {
        try {
            // иммитируем работу в 10 секунд.
            TimeUnit.SECONDS.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Обработан запрос пользователя №" + this.name + " на потоке " + Thread.currentThread().getName());
    }
}
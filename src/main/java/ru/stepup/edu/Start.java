package ru.stepup.edu;

import java.util.concurrent.TimeUnit;

public class Start {
    static final int MAX_T = 3;

    public static void main(String[] args) throws InterruptedException {

            // ТЗ: "В качестве аргументов конструктора пулу передается его емкость (количество рабочих потоков)"
            Processor pool = new Processor(MAX_T);  // создаем пул потоков ( одновлеменно работающих потоков MAX_T = 3)
            pool.start();           // запускаем потоки пула.

            // передаем пять Task в пул потоков
            pool.execute(new Task("task 1"));
            pool.execute(new Task("task 2"));
            pool.execute(new Task("task 3"));
            pool.execute(new Task("task 4"));
            pool.execute(new Task("task 5"));

            TimeUnit.SECONDS.sleep(2);

            // после задержки добавляем ещё два Task
            pool.execute(new Task("task 6"));
            pool.execute(new Task("task 7"));

            TimeUnit.SECONDS.sleep(10);

            pool.execute(new Task("task 8"));

            // ТЗ: "Дополнительно можно добавить метод awaitTermination() без таймаута, работающий аналогично стандартным пулам потоков"
            // PS. добавил с таймаутом
            if (pool.awaitTermination(1000)) {
                System.out.println("Все задания выполнены!");
            }
            else {
                System.out.println("Не все задания выполнены!");
            }

            pool.shutdown();    // принудительно останавливаем заведение потоков в пуле

            // !!! Будет exception т.к. согласно заданию
            // ТЗ: "Также необходимо реализовать метод shutdown(), после выполнения которого новые задачи
            // больше не принимаются пулом (при попытке добавить задачу можно бросать IllegalStateException)"
            pool.execute(new Task("task 9"));   // т.к. здесь будет exception, программа вернет "Process finished with exit code 1"
    }
}
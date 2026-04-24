public class WaitNotifyDemo {

    static class SharedResource {
        private int value;
        private boolean bChanged = false;

        public synchronized void set(int newValue) {
            value = newValue;
            bChanged = true;
            notify();
        }

        public synchronized int get() {
            while (!bChanged) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            bChanged = false;
            return value;
        }
    }

    static class Producer implements Runnable {
        private final SharedResource resource;

        public Producer(SharedResource resource) {
            this.resource = resource;
        }

        @Override
        public void run() {
            for (int i = 1; i <= 5; i++) {
                resource.set(i);
                System.out.println("Producer set: " + i);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    static class Consumer implements Runnable {
        private final SharedResource resource;

        public Consumer(SharedResource resource) {
            this.resource = resource;
        }

        @Override
        public void run() {
            for (int i = 1; i <= 5; i++) {
                int received = resource.get();
                System.out.println("Consumer got: " + received);
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        SharedResource resource = new SharedResource();

        Thread producer = new Thread(new Producer(resource));
        Thread consumer = new Thread(new Consumer(resource));

        consumer.start();
        producer.start();

        producer.join();
        consumer.join();

        System.out.println("Done.");
    }
}
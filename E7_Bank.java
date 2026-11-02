import java.util.*;
class Acc {
    int bal = 2500;
    synchronized void dep(int amt) {
        bal += amt;
        System.out.println("Deposited: " + amt + " Balance: " + bal);
        notifyAll();
    }
    synchronized void wdr(int amt){
        long end = System.currentTimeMillis() + 2000;
        while (bal < amt){
            long rem = end - System.currentTimeMillis();
            if (rem <= 0){
                System.out.println("Withdrawal cancelled (timeout)");
                return;
            }
            System.out.println("Less balance, waiting for deposit");
            try{wait(rem);}
            catch(InterruptedException e){
                Thread.currentThread().interrupt();
                return;
            }
        }
        bal -= amt;
        System.out.println("Withdrawn: " + amt + " Balance: " + bal);
    }
}
public class E7_Bank {
    public static void main(String[] arg) {
        Acc obj1=new Acc();
        Scanner sc=new Scanner(System.in);
        int i= 1;
        do{
            System.out.print("Enter deposit amount: ");
            int d = sc.nextInt();
            System.out.print("Enter withdrawal amount: ");
            int w = sc.nextInt();
            Thread th1 = new Thread(new Runnable(){
                public void run(){obj1.dep(d);}
            });
            Thread th2 = new Thread(new Runnable(){
                public void run(){obj1.wdr(w);}
            });
            th1.start();
            th2.start();
            try {
                th1.join();
                th2.join();
            } catch (InterruptedException err) {
                Thread.currentThread().interrupt();
            }
            System.out.print("Continue? \n1:yes, 0:no ");
            i = sc.nextInt();
        } while (i == 1);
    }
}
 
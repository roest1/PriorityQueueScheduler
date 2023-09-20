
/**
 * A application to simulate a non-preemptive scheduler for a single-core CPU
 * using a heap-based implementation of a priority queue
 * @author William Duncan, Riley Oest
 * @see PQueue.java, PCB.java
 * <pre>
 * DATE: 9/21/2022
 * File:SingleCoreScheduler.java
 * Course: csc 3102
 * Programming Project # 1
 * Instructor: Dr. Duncan
 * Usage: SingleCoreScheduler <number of cylces> <-R or -r> <probability of a  process being created per cycle>  or,
 *        SingleCoreScheduler <number of cylces> <-F or -f> <file name of file containing processes>,
 *        The simulator runs in either random (-R or -r) or file (-F or -f) mode 
 * </pre>
 */

import java.io.File;
import java.util.Comparator;
import java.util.Random;
import java.util.Scanner;

public class SingleCoreScheduler {
    /**
     * Single-core processor with non-preemptive scheduling simulator
     * 
     * @param args an array of strings containing command line arguments
     *             args[0] - number of cyles to run the simulation
     *             args[1] - the mode: -r or -R for random mode and -f or -F for
     *             file mode
     *             args[2] - if the mode is random, this entry contains the
     *             probability that
     *             a process is created per cycle and if the simulator is running in
     *             file mode, this entry contains the name of the file containing
     *             the
     *             the simulated jobs. In file mode, each line of the input file is
     *             in this format:
     *             <process ID> <priority value> <cycle of process creation> <time
     *             required to execute>
     */
    @SuppressWarnings("unchecked")
    public static void main(String[] args) throws Exception {
        Random random = new Random(System.currentTimeMillis());
        int cycles = Integer.parseInt(args[0]); // number of cycles to run the simulation
        String mode = args[1]; //mode of simiulation (-r or -R for random and -f or -F for file) 
        if (mode.equalsIgnoreCase("-r")) { // random mode
            Comparator<PCB> cmp = (x, y) -> {
                if (y.isExecuting()) return 0;
                if (x.getPriority() < y.getPriority()) {
                    return -1;
                }
                if (x.getPriority() == y.getPriority()) {
                    if (x.getPid() < y.getPid()) return -1;
                }
                return 0;
            };
            PQueue rq = new PQueue<PCB>(cmp);
            int cycle = 0;
            double process_num = 0;
            double waitTime = 0;
            double turnAround = 0;
            double p = Double.parseDouble(args[2]); // The probability of a process being created per cycle ∈ [0.01, 1.00] 
            if (Math.abs(p) > 1) throw new IllegalArgumentException("Probability " + p + " is outside of the range [0.01, 1,00]");
            int pid = 1;
            
            while (cycle <= cycles) {
                System.out.println("*** Cycle # : " + cycle);
                if (rq.isEmpty()) System.out.println("The CPU is idle");
                else {
                    PCB headProcess = (PCB) rq.peek();
                    if (!headProcess.isExecuting()) {
                        headProcess.execute();
                        headProcess.setStart(cycle);
                        headProcess.setWait(headProcess.getStart() - headProcess.getWait());
                        waitTime += headProcess.getWait();
                    }
                    int PID = headProcess.getPid();
                    turnAround = headProcess.getBurst() + headProcess.getStart();
                    if (turnAround <= cycle) {
                        rq.remove();
                        System.out.println("Process " + PID + " has just terminated.");
                    } else {
                        System.out.println("Process " + PID + " is executing. ");
                    }
                }
                int q = random.nextInt(2); // random probability ∈ [0, 1]
                if (q <= p) {
                    process_num++;
                    int pVal = (int) Math.floor(q * (20 - (-19) + 1) - 19);
                    int len = (int) Math.floor(q * (100 - (1) + 1) + 1);
                    PCB process = new PCB(pid, pVal, 0, cycle, len);
                    pid++;
                    rq.insert(process);
                    process.setWait(cycle);
                    System.out.println("Adding job with pid " + process.getPid() + " and priority " + process.getPriority() + " and burst  " + process.getBurst());
                } else { 
                    System.out.println("No new job this cycle");
                }
                cycle ++;
            }
             System.out.printf("The average number of processes created per cycle is %.5f\n", process_num / cycle);
             System.out.printf("The average turnaround time per process is %10f\n", turnAround / process_num);
             System.out.printf("The average wait time per process is %.10f cycles.\n", waitTime / process_num);
        } else if (mode.equalsIgnoreCase("-f")) { // file mode
            Scanner scan = new Scanner(new File(args[2])); // name of the .txt file containing the simulated jobs
            Comparator<PCB> comp = (job1, job2) -> {
                if (job1.getPid() != job2.getPid()) {
                    return job1.getPid() < job2.getPid() ? -1 : 1;
                }
                return 0;
            };
            Comparator<PCB> comp2 = (x, y) -> {
                if (y.isExecuting()) return 0;
            
                if (x.getPriority() < y.getPriority()) return -1;

                if (x.getPriority() == y.getPriority()) {
                    return x.getPid() < y.getPid() ? -1 : 0;
                }
                return 0;
            };
            PQueue waitq = new PQueue<PCB>(comp);
            PQueue readyq = new PQueue<PCB>(comp2);
            while (scan.hasNextLine()) {
                PCB block = new PCB(scan.nextInt(), scan.nextInt(), 0, scan.nextInt(), scan.nextInt());
                waitq.insert(block);
            }
            float process_num = waitq.size();
            int cycle = 0;
            double waitTime = 0; 
            double turnAround = 0;
            while (cycle <= cycles) {
                System.out.println("*** Cycle # : " + cycle);
                if (readyq.isEmpty()) System.out.println("The CPU is idle");
                else {
                    PCB headProcess = (PCB) readyq.peek();
                    if (!headProcess.isExecuting()) {
                        headProcess.execute();
                        headProcess.setStart(cycle);
                        waitTime += headProcess.getWait();
                    }
                    int PID = headProcess.getPid();
                    turnAround = headProcess.getBurst() + headProcess.getStart();
                    if (turnAround <= cycle) {
                        readyq.remove();
                        System.out.println("Process " + PID + " has just terminated.");
                        if (!waitq.isEmpty()) {
                            PCB nextProcess = (PCB) readyq.peek();
                            nextProcess.setWait(cycle);
                        }
                    } else {
                        System.out.println("Process " + PID + " is executing.");
                    }
                }
                if (!waitq.isEmpty()) {
                    PCB waitProcess = (PCB) waitq.peek();
                }
                PCB waitProcess;
                int createCycle = 0;
                if (!waitq.isEmpty()) {
                    waitProcess = (PCB) waitq.peek();
                    createCycle = waitProcess.getArrival();
                } 
                if (createCycle == cycle) {
                    PCB process = (PCB) waitq.remove();
                    readyq.insert(process);
                    System.out.println("Adding job with pid " + process.getPid() + " and priority " + process.getPriority() + " and burst " + process.getBurst());
                    process.setWait(0);
                } else {
                    System.out.println("No new job this cycle ");
                }
                cycle ++;
            }
            System.out.printf("The average number of process created per cycle is %.5f\n", (float) (process_num / cycle));
            System.out.printf("The average turnaround time per process is %10f\n", turnAround / process_num);
            System.out.printf("The average wait time per process is %.10f cycles.\n", waitTime / process_num);
        }
    }
}

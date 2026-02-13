/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ur_os;

/**
 * @author prestamour
 */
public class SJF_NP extends Scheduler {

  SJF_NP(OS os) {
    super(os);
  }

  @Override
  public void getNext(boolean cpuEmpty) {
    if (cpuEmpty && !processes.isEmpty()) {
      Process shortest = processes.get(0);

      // Find the process with the shortest remaining CPU time
      for (Process p : processes) {
        int pRemaining = p.getRemainingCPUTime();
        int shortestRemaining = shortest.getRemainingCPUTime();

        if (pRemaining < shortestRemaining
            || (pRemaining == shortestRemaining && tieBreaker(p, shortest) == p)) {
          shortest = p;
        }
      }

      os.interrupt(InterruptType.SCHEDULER_RQ_TO_CPU, shortest);
      processes.remove(shortest);
    }
  }

  @Override
  public void newProcess(boolean cpuEmpty) {} // Non-preemtive

  @Override
  public void IOReturningProcess(boolean cpuEmpty) {} // Non-preemtive
}

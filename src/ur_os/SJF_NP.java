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
      for (Process p : processes) {
        if (p.getBurstTime() < shortest.getBurstTime()
            || (p.getBurstTime() == shortest.getBurstTime() && tieBreaker(p, shortest) == p)) {
          shortest = p;
        }
      }
      // Basicamente Encontrar el proceso con el menor tiempo de burst  para continuar;
      os.cpu.addProcess(shortest);
      processes.remove(shortest);
      addContextSwitch();
    }
  }

  @Override
  public void newProcess(boolean cpuEmpty) {} // Non-preemtive

  @Override
  public void IOReturningProcess(boolean cpuEmpty) {} // Non-preemtive
}

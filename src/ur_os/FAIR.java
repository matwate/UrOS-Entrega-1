package ur_os;

import java.util.PriorityQueue;
import java.util.Comparator;
import java.util.ArrayList;
import java.util.List;

public class FAIR extends Scheduler {

    private int someValue;
    private List<Integer> groupRotation;
    private int rotationIndex;
    private int cont;

    public FAIR(OS os, int someValue) {
        super(os);
        this.someValue = (someValue > 0) ? someValue : 4;
        this.groupRotation = new ArrayList<>();
        this.rotationIndex = 0;
        this.cont = 0;
    }

    @Override
    public void newProcess(boolean cpuEmpty) {

    }

    @Override
    public void IOReturningProcess(boolean cpuEmpty) {

    }

    @Override
    public void getNext(boolean cpuEmpty) {
        if (processes.isEmpty()) {
            cont++;
            return;
        }

        syncGroupRotation();

        if (!cpuEmpty) {
            if (cont >= someValue - 1) {
                advanceRotation();
                Process next = pickFromGroup(getCurrentGroup());

                if (next == null) {
                    for (int i = 0; i < groupRotation.size(); i++) {
                        advanceRotation();
                        next = pickFromGroup(getCurrentGroup());
                        if (next != null) break;
                    }
                }

                if (next != null) {
                    processes.remove(next);
                    os.interrupt(InterruptType.SCHEDULER_CPU_TO_RQ, next);
                }
            } else {
                cont++;
            }
        } else {
            Process next = pickFromGroup(getCurrentGroup());

            if (next == null) {
                for (int i = 0; i < groupRotation.size(); i++) {
                    advanceRotation();
                    next = pickFromGroup(getCurrentGroup());
                    if (next != null) break;
                }
            }

            if (next != null) {
                processes.remove(next);
                os.interrupt(InterruptType.SCHEDULER_RQ_TO_CPU, next);
                addContextSwitch();
                cont = 0;
            }
        }
    }

    private void syncGroupRotation() {
        List<Integer> active = new ArrayList<>();
        for (Process p : processes) {
            if (!active.contains(p.getPriority())) active.add(p.getPriority());
        }
        if (!os.isCPUEmpty()) {
            int cpuGroup = os.getProcessInCPU().getPriority();
            if (!active.contains(cpuGroup)) active.add(cpuGroup);
        }
        groupRotation.retainAll(active);
        for (int g : active) {
            if (!groupRotation.contains(g)) groupRotation.add(g);
        }
        if (!groupRotation.isEmpty()) {
            rotationIndex = rotationIndex % groupRotation.size();
        }
    }

    private int getCurrentGroup() {
        if (groupRotation.isEmpty()) return -1;
        return groupRotation.get(rotationIndex);
    }

    private void advanceRotation() {
        if (groupRotation.isEmpty()) return;
        rotationIndex = (rotationIndex + 1) % groupRotation.size();
        cont = 0;
    }

    private Process pickFromGroup(int group) {
        Process chosen = null;
        for (Process p : processes) {
            if (p.getPriority() == group)
                chosen = (chosen == null) ? p : tieBreaker(chosen, p);
        }
        return chosen;
    }
}

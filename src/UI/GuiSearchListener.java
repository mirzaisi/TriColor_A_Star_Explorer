package UI;

import search.SearchListener;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import search.Node;

/**
 * Streams A* expansions into the search tree dialog with some small delays.
 */
public class GuiSearchListener implements SearchListener {

    private SearchTreeDialog treeDialog;
    private final LinkedBlockingQueue<Runnable> logQueue = new LinkedBlockingQueue<>();
    private final Thread worker;

    public GuiSearchListener() {
        worker = new Thread(() -> {
            while (true) {
                try {
                    Runnable task = logQueue.take();
                    task.run();
                } catch (InterruptedException ignored) { }
            }
        }, "GuiLogWorker");
        worker.setDaemon(true);
        worker.start();
    }

    public void attachDialog(SearchTreeDialog dialog) { this.treeDialog = dialog; }

    @Override
    public void onNodeExpanded(Node chosen, List<Node> successors) {
        logQueue.offer(() -> {
            if (treeDialog != null) {
                treeDialog.ensureRoot(chosen);
                treeDialog.addExpansion(chosen, successors);
            }
            sleep(40);

            if (!successors.isEmpty()) {
                sleep(30L * successors.size());
            }
        });
    }

    private void sleep(long millis) {
        try { Thread.sleep(millis); } catch (InterruptedException ignored) { }
    }
}

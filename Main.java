import model.CalculatorModel;
import model.HistoryManager;
import view.CalculatorView;
import view.MenuView;
import controller.CalculatorController;

public class Main {
    public static void main(String[] args) {
        CalculatorModel model = new CalculatorModel();
        CalculatorView view = new CalculatorView();
        MenuView menuView = new MenuView();
        HistoryManager historyManager = new HistoryManager();

        CalculatorController controller = new CalculatorController(model, view, menuView, historyManager);
        controller.run();
    }
}

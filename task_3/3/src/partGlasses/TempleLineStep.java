package partGlasses;

import productAssemblyLine.ILineStep;
import productAssemblyLine.IProductPart;

public class TempleLineStep implements ILineStep {
    public TempleLineStep() {
        System.out.println("Линия сборки дужек запущена");
    }

    @Override
    public IProductPart buildProductPart() {
        System.out.println("Изготовление дужек...");
        return new Temple();
    }
}

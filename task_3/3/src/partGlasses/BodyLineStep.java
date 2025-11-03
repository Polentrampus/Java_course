package partGlasses;

import productAssemblyLine.ILineStep;
import productAssemblyLine.IProductPart;

public class BodyLineStep implements ILineStep {
    public BodyLineStep() {
        System.out.println("Линия сборки корпуса запущена");
    }

    @Override
    public IProductPart buildProductPart() {
        System.out.println("Изготовление корпуса...");
        return new Body();
    }
}

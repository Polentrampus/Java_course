package partGlasses;

import productAssemblyLine.ILineStep;
import productAssemblyLine.IProductPart;

public class LensLineStep implements ILineStep {
    public LensLineStep() {
        System.out.println("Линия сборки линз запущена");
    }

    @Override
    public IProductPart buildProductPart() {
        System.out.println("Изготовление линз...");
        return new Lens();
    }
}

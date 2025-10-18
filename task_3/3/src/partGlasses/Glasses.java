package partGlasses;

import productAssemblyLine.IProduct;
import productAssemblyLine.IProductPart;

public class Glasses implements IProduct {
    private IProductPart body;
    private IProductPart temple;
    private IProductPart lens;

    public Glasses() {
        System.out.println("Создана заготовка для очков");
    }

    @Override
    public void installFirstPart(IProductPart body) {
        this.body = body;
        System.out.println("Корпус установлен!");
    }

    @Override
    public void installSecondPart(IProductPart temple) {
        this.temple = temple;
        System.out.println("Дужки установлены!");
    }

    @Override
    public void installThirdPart(IProductPart lens) {
        this.lens = lens;
        System.out.println("Линзы установлены!");
    }
}

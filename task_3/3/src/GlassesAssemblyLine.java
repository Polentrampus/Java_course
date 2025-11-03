import partGlasses.Glasses;
import productAssemblyLine.IAssemblyLine;
import productAssemblyLine.ILineStep;
import productAssemblyLine.IProduct;

public class GlassesAssemblyLine implements IAssemblyLine {
    ILineStep firstLineStep;
    ILineStep secondLineStep;
    ILineStep thirdLineStep;

    public GlassesAssemblyLine(ILineStep firstLineStep, ILineStep secondLineStep, ILineStep thirdLineStep) {
        this.firstLineStep = firstLineStep;
        this.secondLineStep = secondLineStep;
        this.thirdLineStep = thirdLineStep;
        System.out.println("Сборочная линия начала работу:");
    }

    @Override
    public IProduct assembleProduct(IProduct product) {
        product.installFirstPart(firstLineStep.buildProductPart());
        product.installSecondPart(secondLineStep.buildProductPart());
        product.installThirdPart(thirdLineStep.buildProductPart());
        System.out.println("Итоговый продукт: " + Glasses.class.getName());
        return product;
    }
}

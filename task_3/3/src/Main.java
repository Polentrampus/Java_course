import partGlasses.BodyLineStep;
import partGlasses.Glasses;
import partGlasses.LensLineStep;
import partGlasses.TempleLineStep;
import productAssemblyLine.ILineStep;
import productAssemblyLine.IProduct;

public class Main {
    public static void main(String[] args) {
        System.out.println("Начало работы сборочной линии очков: ");
        ILineStep body = new BodyLineStep();
        ILineStep temple = new TempleLineStep();
        ILineStep lenses = new LensLineStep();

        IProduct glasses = new Glasses();

        GlassesAssemblyLine assemblyLine = new GlassesAssemblyLine(body, temple, lenses);

        IProduct assembledGlasses = assemblyLine.assembleProduct(glasses);
    }
}
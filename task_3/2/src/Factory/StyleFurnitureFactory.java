package Factory;

import Furniture.Chair;
import Furniture.Sofa;
import Furniture.Table;

public interface StyleFurnitureFactory {
    Chair createChair();

    Sofa createSofa();

    Table createTable();
}

package hero.manufacture;

public class CategoryManager
{
    public static String[] getCategoryStrByManufactureType (
            ManufactureType _mtype)
    {
        if (_mtype == ManufactureType.BLACKSMITH)
            return BlacksmithCategory.categorys;
        if (_mtype == ManufactureType.CRAFTSMAN)
            return CraftsmanCategory.categorys;
        if (_mtype == ManufactureType.DISPENSER)
            return DispenserCategory.categorys;
        if(_mtype == ManufactureType.GRATHER)
            return GatherCategory.categorys;
        if(_mtype == ManufactureType.PURIFY)
            return PurifyCategory.categorys;
        return JewelerCategory.categorys;
    }
}

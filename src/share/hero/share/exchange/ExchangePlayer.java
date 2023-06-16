package hero.share.exchange;

/**
 * 交易玩家类 Description:<br>
 * 
 * @author Lulin
 * @version 0.1
 */
public class ExchangePlayer
{
    public String            nickname;

    public byte              state;

    /**
     * 物品在背包里的位置
     * 下架物品和列表过滤时使用
     */
    public short[]           gridIndex;

    public int[]             goodsID;

    public short[]           goodsNum;

    public int               money;
    
    public byte[]           goodsType;
    

    /**
     * 玩家是否锁定交易
     */
    public boolean locked = false;

    private static final int MAX_SIZE = 6;

    protected ExchangePlayer(String _nickname)
    {
        nickname = _nickname;
        state = Exchange.NORMAL;
        gridIndex = new short[MAX_SIZE];
        goodsID = new int[MAX_SIZE];
        goodsNum = new short[MAX_SIZE];
        money = 0;
        goodsType = new byte[MAX_SIZE];
    }

    public boolean addExchangeGoods (short _index, int _goodsID, short _goodsNum, byte _goodsType)
    {
        for (int i = 0; i < goodsID.length; i++)
        {
            if (goodsID[i] == 0)
            {
                gridIndex[i] = _index;
                goodsID[i] = _goodsID;
                goodsNum[i] = _goodsNum;
                goodsType[i] = _goodsType;
                return true;
            }
        }
        return false;
    }

    /**
     * 撤消物品
     * 把架上所有物品都撤消
     * @return
     */
    public boolean removeExchangeGoods(){
        for(int i=0; i<goodsID.length; i++){
            goodsID[i] = 0;
            gridIndex[i] = 0;
            goodsNum[i] = 0;
            goodsType[i] = 0;
            return true;
        }
        return false;
    } 
    
    /**
     * 撤消单个上架的物品
     * @param index //物品在架上的位置
     * @return
     */
    public boolean removeSingleExchangeGoods(int index, int goodsid){
    	for(int i=0; i<goodsID.length; i++){
    		if(i==index && goodsID[i] == goodsid){
    			goodsID[i] = 0;
                gridIndex[i] = 0;
                goodsNum[i] = 0;
                goodsType[i] = 0;
                return true;
    		}
    	}
    	
    	return false;
    }
}

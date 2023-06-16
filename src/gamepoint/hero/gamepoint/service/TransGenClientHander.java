package hero.gamepoint.service;

import me2.core.handler.ClientHandler;

/**
 * Description:<br>
 * 
 * @author JOJO
 * @version 0.1
 */
public class TransGenClientHander extends ClientHandler
{

    public void run ()
    {
        //客户端现在不主动发送请求流水号的请求
//        try
//        {
//            String msidn = input.readUTF();
//            int money = input.readInt();
//            log.info("充值金额:" + money);
//            String transID = GamePointService.getInstance()
//                    .getTransIDGen(msidn);
//            NavyPlayer player = (NavyPlayer) PlayerServiceImpl.getInstance()
//                    .getPlayerBySession(session);
//            if (player == null) return;
//            String _touchPoint = "200";
//            if (player.getPlatformInfo().is139()) _touchPoint = "300";
//            SessionUtil.getInstance().addUserInfo(transID, player.getUserID(),
//                    _touchPoint,player.getPlatformInfo().getPublisher());
//            OutMesgQ.getInstance().put(session,
//                    new TransGenResultMessage(transID));
//        }
//        catch (IOException e)
//        {
//            e.printStackTrace();
//        }
    }

    @Override
    public void read () throws Exception
    {
        // TODO Auto-generated method stub
        
    }

}

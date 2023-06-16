package hero.npc.service;

import hero.map.Map;
import hero.share.Direction;

import java.util.Vector;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2005
 * </p>
 * 
 * @file AStar.java
 * @author Ding Chu
 * @version 1.0
 * @date 2008-10-27 下午04:02:08
 * 
 * <pre>
 *      Description:A*循路算法，该功能只返回游戏中前5个动向
 * </pre>
 */

public class AStar
{
    private static class cNode
    {
        byte  F;

        int   G;

        byte  H;

        short X;

        short Y;

        byte  D;

        cNode parentNode;

        private void setNode (short _x, short _y, short _endX, short _endY,
                byte _d, cNode _fNode)
        {
            X = _x;
            Y = _y;
            D = _d;
            H = (byte) (Math.abs(X - _endX) + Math.abs(Y - _endY));
            parentNode = _fNode;
        }

        private int getG ()
        {
            if (parentNode == null)
            {
                G = 1;
                return G;
            }
            G = parentNode.G + 1;
            return G;
        }

        private int getF ()
        {
            return getG() + H;
        }
    }

    public static byte[] getPath (short _startX, short _startY, short _endX,
            short _endY, short _moveMaxGridPerTime, short _minGridOfTarget,
            Map _map)
    {
        if (_startX == _endX && _startY == _endY)
        {
            return new byte[]{0 };
        }
        else
        {
            Vector<cNode> vOpen = new Vector<cNode>(9, 1), vClose = new Vector<cNode>(
                    9, 1);

            cNode cn = new cNode();
            cn.setNode(_startX, _startY, _endX, _endY, Direction.DOWN, null);
            vOpen.addElement(cn);

            boolean isFind = false;
            cNode minNode = null;
            int num = 0;

            while (vOpen.size() != 0)
            {
                minNode = (cNode) vOpen.firstElement();
                if (minNode.X == _endX && minNode.Y == _endY)
                {
                    isFind = true;
                    break;
                }
                vOpen.removeElement(minNode);
                vClose.addElement(minNode);
                addOpen(minNode, vOpen, vClose, _endX, _endY, _map);
                taxis(vOpen, vClose);
                num++;
            }

            vOpen.clear();
            vOpen = null;
            vClose.clear();
            vClose = null;

            if (isFind)
            {
                byte[] completeActionDirection = new byte[minNode.getG()];

                completeActionDirection[completeActionDirection.length - 1] = minNode.D;

                for (int i = completeActionDirection.length - 2; i >= 0; i--)
                {
                    minNode = minNode.parentNode;
                    completeActionDirection[i] = minNode.D;
                }

                minNode = null;

                int actionDirectionLength = completeActionDirection.length
                        - _minGridOfTarget;

                if (actionDirectionLength <= 0)
                {
                    return new byte[]{0 };
                }
                else
                {
                    if (actionDirectionLength > _moveMaxGridPerTime)
                    {
                        actionDirectionLength = _moveMaxGridPerTime;
                    }

                    byte[] actionDirection = new byte[actionDirectionLength];
                    System.arraycopy(completeActionDirection, 0,
                            actionDirection, 0, actionDirectionLength);

                    return actionDirection;
                }
            }

            return null;
        }
    }

    private AStar()
    {
    }

    /**
     * 检索四周的点
     * 
     * @param _node
     */
    private static void addOpen (cNode _node, Vector<cNode> _vOpen,
            Vector<cNode> _vClose, short _endX, short _endY, Map _map)
    {
        cNode nodeNext;
        cNode nodeHave;
        byte _dx = 0;
        byte _dy = 0;
        byte _D = 0;
        boolean _isPass;

        for (byte i = 0; i < 4; i++)
        {
            P:
            {
                _isPass = true;
                nodeNext = new cNode();
                if (i == 0)
                {
                    _dx = (byte) (_node.X + 1);
                    _dy = (byte) _node.Y;
                    _D = Direction.RIGHT;
                }
                else if (i == 1)
                {
                    _dx = (byte) (_node.X - 1);
                    _dy = (byte) _node.Y;
                    _D = Direction.LEFT;
                }
                else if (i == 2)
                {
                    _dx = (byte) _node.X;
                    _dy = (byte) (_node.Y + 1);
                    _D = Direction.DOWN;
                }
                else if (i == 3)
                {
                    _dx = (byte) _node.X;
                    _dy = (byte) (_node.Y - 1);
                    _D = Direction.UP;
                }

                _isPass = _map.isRoad(_dx, _dy);
                if (_isPass == false)
                {
                    break P;
                }

                {
                    for (int l = 0; l < _vClose.size(); l++)
                    {
                        nodeHave = (cNode) _vClose.elementAt(l);
                        if (_dx == nodeHave.X && _dy == nodeHave.Y)
                        {
                            break P;
                        }
                    }

                    nodeNext.setNode(_dx, _dy, _endX, _endY, _D, _node);
                    for (int l = 0; l < _vOpen.size(); l++)
                    {
                        nodeHave = (cNode) _vOpen.elementAt(l);
                        if (_dx == nodeHave.X && _dy == nodeHave.Y)
                        {
                            if (nodeHave.getG() > nodeNext.getG())
                            {
                                nodeHave.parentNode = _node;
                                nodeHave.D = nodeNext.D;
                            }

                            break P;
                        }
                    }
                    nodeNext.G = nodeNext.getG();
                    _vOpen.addElement(nodeNext);
                }
            }
        }
    }

    /**
     * 排序，最小点提前
     */
    private static void taxis (Vector<cNode> _vOpen, Vector<cNode> _vClose)
    {
        int minF = 0;
        int id = -1;
        int tempF = 0;

        for (int i = _vOpen.size() - 1; i >= 0; i--)
        {
            tempF = ((cNode) _vOpen.elementAt(i)).getF();
            if (tempF < minF || minF == 0)
            {
                minF = tempF;
                id = i;
            }
        }

        if (id != -1)
        {
            cNode _tempcNode = (cNode) _vOpen.elementAt(id);
            _vOpen.removeElementAt(id);
            _vOpen.insertElementAt(_tempcNode, 0);
        }
    }
}

package sweetdream;

import java.util.Enumeration;
import java.util.Vector;
import javax.swing.tree.DefaultMutableTreeNode;

public class CheckNode extends DefaultMutableTreeNode
{

    public static final int SINGLE_SELECTION = 0;
    public static final int DIG_IN_SELECTION = 4;
    protected int selectionMode;
    protected boolean isSelected;

    public CheckNode()
    {
        this(null);
    }

    public CheckNode(Object userObject)
    {
        this(userObject, true, false);
    }

    public CheckNode(Object userObject, boolean allowsChildren, boolean isSelected)
    {
        super(userObject, allowsChildren);
        this.isSelected = isSelected;
        setSelectionMode(4);
    }

    public void setSelectionMode(int mode)
    {
        selectionMode = mode;
    }

    public int getSelectionMode()
    {
        return selectionMode;
    }

    public void setSelected(boolean isSelected)
    {
        this.isSelected = isSelected;
        if(selectionMode == 4 && children != null)
        {
            CheckNode node;
            for(Enumeration enum2 = children.elements(); enum2.hasMoreElements(); node.setSelected(isSelected))
            {
                node = (CheckNode)enum2.nextElement();
            }

        }
    }

    public boolean isSelected()
    {
        return isSelected;
    }
}


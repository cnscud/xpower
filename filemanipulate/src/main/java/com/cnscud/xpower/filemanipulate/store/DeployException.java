package com.cnscud.xpower.filemanipulate.store;

/**
 * Deploy Exception.
 *
 * @author Felix Zhang    Date: 2008-5-25 13:57:16
 * @Version: 1.0.0
 */
public class DeployException extends Exception
{

    public DeployException(String msg)
    {
        super(msg);
    }


    public DeployException(String msg, Throwable cause)
    {
        super(msg, cause);
    }
}

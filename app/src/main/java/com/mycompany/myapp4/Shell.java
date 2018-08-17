package com.mycompany.myapp4;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class Shell
{
    private Process process;//shell进程
    private Callback callback;//回调接口
    private OutputStream output;//输入流
    private InputStream input;//输出流
    private InputStream errput;//错误输出流
    private boolean OutputErrors=true;//是否输出错误
    private boolean exit=false;//是否已经停止
    private static HashMap<Integer, String> error;

    public Shell() throws Throwable
    {
        process = Runtime.getRuntime().exec("sh");
        input = process.getInputStream();
        errput = process.getErrorStream();
        output = process.getOutputStream();
        new Read(input).start();//读取正常输出
        new Read(errput).start();//读取错误输出
        new Thread()
        {
            public void run()
            {
                try
                {
                    int retu=process.waitFor();//获取返回值，将一直阻塞到结束运行
                    stop();
                    if (callback != null)
                    {
                        if (retu != 0)//若非正常返回值
                        {
                            callback.onRuesult("Error: " + error.get(retu));//返回错误码
                        }
                        callback.onisok(retu == 0);//返回运行结果
                    }
                    setCallback(null);
                }
                catch (Throwable e)
                {}
            }
        }.start();
    }
    //设置是否输出错误
    public void setOutputErrors(boolean outputErrors)
    {
        OutputErrors = outputErrors;
    }
    //执行shell
    public void shell(String cmd)
    {
        if (isExit())return;
        try
        {
            output.write((cmd + "\n").getBytes());//写入数据
            output.flush();//立马发送并从缓存区清空
        }
        catch (Throwable e)
        {}
    }
    //退出
    public void exit()
    {
        try
        {
            shell("exit");//退出
        }
        catch (Throwable e)
        {}
    }
    //是否已经退出
    public boolean isExit()
    {
        return exit;
    }
    //强制停止
    public void stop()
    {
        try
        {
            output.close();//关闭输入流
            input.close();//关闭输出流
            errput.close();//关闭错误输出流
            process.destroy();//停止进程
        }
        catch (Throwable e)
        {}
        exit = true;
    }
    //进入SU模式
    public void EnterSu()
    {
       /* if (!isExit() && SuFileExists() && isRoot())//如果su文件存在并已授权
        {*/
            shell("su");//进入su模式
    //    }
    }
    //设置监听回调
    public void setCallback(Callback callback)
    {
        this.callback = callback;
    }
    //接口
    public static interface Callback
    {
        public void onRuesult(String StrResult);
        public void onisok(boolean isok);
    }
    //是否具有root
    public static boolean isRoot()
    {
        try
        {
            Process run=Runtime.getRuntime().exec("su");
            run.getOutputStream().write("exit\n".getBytes());
            boolean ok=run.waitFor() == 0;
            return ok;
        }
        catch (Throwable e)
        {}
        return false;
    }
    //SU文件是否存在
    public static boolean SuFileExists()
    {
        final File dd=new File("/system/bin/su");
        final File dd2=new File("/system/xbin/su");
        return (dd.exists() || dd2.exists());
    }
    private class Read extends Thread
    {
        private InputStream input;

        public Read(InputStream input)
        {
            this.input = input;
        }
        @Override
        public void run()
        {
            super.run();
            try
            {
                ArrayList<Byte> array=new ArrayList<Byte>();//动态数组以方便读取
                while (true)
                {
                    if (!OutputErrors && input == errput)
                    {
                        break;
                    }
                    int by=input.read();//阅读1Byte
                    if (by == -1 || by == 10)//如果是\n换行符
                    {
                        if (by == -1)
                        {
                            input.close();
                        }
                        if (callback != null)
                        {
                            synchronized (callback)//同步锁，保证输出不会乱套
                            {
                                byte[] bytes=new byte[array.size()];//新建长度等同array的数组
                                for (int i=0;i < array.size();i++)
                                {
                                    bytes[i] = array.get(i);//赋值
                                }
                                array.clear();//清空动态数组
                                callback.onRuesult(new String(bytes));//返回数据
                            }
                        }
                        if (by == -1)
                        {
                            break;
                        }
                    }
                    else
                    {
                        array.add((byte)by);//向动态数组添加数据
                    }
                }
            }
            catch (Throwable e)
            {}
        }
    }
    /*
     单句shell直接返回执行结果
     */
    private static String result;
    private static boolean flag;
    public synchronized static String cmd(final String cmd)
    {
        result = "";
        flag = false;
        synchronized (result)
        {
            new Thread()
            {
                public void run()
                {
                    try
                    {
                        final Shell she=new Shell();
                        she.EnterSu();
                        she.setCallback(new Shell.Callback()
                            {
                                @Override
                                public void onRuesult(String StrResult)
                                {
                                    if (StrResult.matches("(?s)Error:.+"))return;
                                    if (StrResult.equals("bugok"))
                                    {
                                        flag = true;
                                        she.stop();
                                        return;
                                    }
                                    result += (result.length() == 0 ?"": "\n") + StrResult;
                                }
                                @Override
                                public void onisok(boolean isok)
                                {}
                            });
                        she.shell(cmd);
                        she.shell("echo bugok;");
                        she.exit();
                    }
                    catch (Throwable e)
                    {}
                }
            }.start();
            while (true)
            {
                if (flag)
                {
                    return result;
                }
            }
        }
    }
    /*
     *错误码映射
     */
    static
    {
        if (error == null)
        {
            error = new HashMap<Integer,String>();
            error.put(1, "Operation not permitted");
            error.put(2, "No such file or directory");
            error.put(3, "No such process");
            error.put(4, "Interrupted system call");
            error.put(5, "Input/output error");
            error.put(6, "No such device or address");
            error.put(7, "Argument list too long");
            error.put(8, "Exec format error");
            error.put(9, "Bad file descriptor");
            error.put(10, "No child processes");
            error.put(11, "Resource temporarily unavailable");
            error.put(12, "Cannot allocate memory");
            error.put(13, "Permission denied");
            error.put(14, "Bad address");
            error.put(15, "Block device required");
            error.put(16, "Device or resource busy");
            error.put(17, "File exists");
            error.put(18, "Invalid cross-device link");
            error.put(19, "No such device");
            error.put(20, "Not a directory");
            error.put(21, "Is a directory");
            error.put(22, "Invalid argument");
            error.put(23, "Too many open files in system");
            error.put(24, "Too many open files");
            error.put(25, "Inappropriate ioctl for device");
            error.put(26, "Text file busy");
            error.put(27, "File too large");
            error.put(28, "No space left on device");
            error.put(29, "Illegal seek");
            error.put(30, "Read-only file system");
            error.put(31, "Too many links");
            error.put(32, "Broken pipe");
            error.put(33, "Numerical argument out of domain");
            error.put(34, "Numerical result out of range");
            error.put(35, "Resource deadlock avoided");
            error.put(36, "File name too long");
            error.put(37, "No locks available");
            error.put(38, "Function not implemented");
            error.put(39, "Directory not empty");
            error.put(40, "Too many levels of symbolic links");
            error.put(42, "No message of desired type");
            error.put(43, "Identifier removed");
            error.put(44, "Channel number out of range");
            error.put(45, "Level 2 not synchronized");
            error.put(46, "Level 3 halted");
            error.put(47, "Level 3 reset");
            error.put(48, "Link number out of range");
            error.put(49, "Protocol driver not attached");
            error.put(50, "No CSI structure available");
            error.put(51, "Level 2 halted");
            error.put(52, "Invalid exchange");
            error.put(53, "Invalid request descriptor");
            error.put(54, "Exchange full");
            error.put(55, "No anode");
            error.put(56, "Invalid request code");
            error.put(57, "Invalid slot");
            error.put(59, "Bad font file format");
            error.put(60, "Device not a stream");
            error.put(61, "No data available");
            error.put(62, "Timer expired");
            error.put(63, "Out of streams resources");
            error.put(64, "Machine is not on the network");
            error.put(65, "Package not installed");
            error.put(66, "Object is remote");
            error.put(67, "Link has been severed");
            error.put(68, "Advertise error");
            error.put(69, "Srmount error");
            error.put(70, "Communication error on send");
            error.put(71, "Protocol error");
            error.put(72, "Multihop attempted");
            error.put(73, "RFS specific error");
            error.put(74, "Bad message");
            error.put(75, "Value too large for defined data type");
            error.put(76, "Name not unique on network");
            error.put(77, "File descriptor in bad state");
            error.put(78, "Remote address changed");
            error.put(79, "Can not access a needed shared library");
            error.put(80, "Accessing a corrupted shared library");
            error.put(81, ".lib section in a.out corrupted");
            error.put(82, "Attempting to link in too many shared libraries");
            error.put(83, "Cannot exec a shared library directly");
            error.put(84, "Invalid or incomplete multibyte or wide character");
            error.put(85, "Interrupted system call should be restarted");
            error.put(86, "Streams pipe error");
            error.put(87, "Too many users");
            error.put(88, "Socket operation on non-socket");
            error.put(89, "Destination address required");
            error.put(90, "Message too long");
            error.put(91, "Protocol wrong type for socket");
            error.put(92, "Protocol not available");
            error.put(93, "Protocol not supported");
            error.put(94, "Socket type not supported");
            error.put(95, "Operation not supported");
            error.put(96, "Protocol family not supported");
            error.put(97, "Address family not supported by protocol");
            error.put(98, "Address already in use");
            error.put(99, "Cannot assign requested address");
            error.put(100, "Network is down");
            error.put(101, "Network is unreachable");
            error.put(102, "Network dropped connection on reset");
            error.put(103, "Software caused connection abort");
            error.put(104, "Connection reset by peer");
            error.put(105, "No buffer space available");
            error.put(106, "Transport endpoint is already connected");
            error.put(107, "Transport endpoint is not connected");
            error.put(108, "Cannot send after transport endpoint shutdown");
            error.put(109, "Too many references: cannot splice");
            error.put(110, "Connection timed out");
            error.put(111, "Connection refused");
            error.put(112, "Host is down");
            error.put(113, "No route to host");
            error.put(114, "Operation already in progress");
            error.put(115, "Operation now in progress");
            error.put(116, "Stale NFS file handle");
            error.put(117, "Structure needs cleaning");
            error.put(118, "Not a XENIX named type file");
            error.put(119, "No XENIX semaphores available");
            error.put(120, "Is a named type file");
            error.put(121, "Remote I/O error");
            error.put(122, "Disk quota exceeded");
            error.put(123, "No medium found");
            error.put(124, "Wrong medium type");
            error.put(125, "Operation canceled");
            error.put(126, "Required key not available");
            error.put(127, "Key has expired");
            error.put(128, "Key has been revoked");
            error.put(129, "Key was rejected by service");
            error.put(130, "Owner died");
            error.put(131, "State not recoverable");
        }
    }
}
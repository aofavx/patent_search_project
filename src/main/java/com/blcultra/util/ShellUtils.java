package com.blcultra.util;

import com.jcraft.jsch.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Vector;

public class ShellUtils {
    private String ipAddress;

    private String username;

    private String password;

    public static final int DEFAULT_SSH_PORT = 22;  //默认是22

    private Vector<String> stdout;

    public ShellUtils(final String ipAddress, final String username, final String password) {
        this.ipAddress = ipAddress;
        this.username = username;
        this.password = password;
        stdout = new Vector<String>();
    }

    public int execute(final String command) {
        int returnCode = 0;
        JSch jsch = new JSch();
//        SSHUserInfo userInfo = new SSHUserInfo();

        try {
            // Create and connect session.
            Session session = jsch.getSession(username, ipAddress, DEFAULT_SSH_PORT);
            session.setPassword(password);
//            session.setUserInfo(userInfo);
            session.setConfig("StrictHostKeyChecking", "no");//去掉连接确认的
            session.connect(30000);

            // Create and connect channel.
            Channel channel = session.openChannel("exec");
            ((ChannelExec) channel).setCommand(command);

            channel.setInputStream(null);
            BufferedReader input = new BufferedReader(new InputStreamReader(channel
                    .getInputStream()));

            channel.connect();
            System.out.println("The remote command is: " + command);

            // Get the output of remote command.
            String line;
            while ((line = input.readLine()) != null) {
                stdout.add(line);
            }
            input.close();

            // Get the return code only after the channel is closed.
            if (channel.isClosed()) {
                returnCode = channel.getExitStatus();
            }

            // Disconnect the channel and session.
            channel.disconnect();
            session.disconnect();
        } catch (JSchException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnCode;
    }

    public static boolean executeRemoteShell(String ip, String username, String passwd, String command) {
        ShellUtils sshExecutor = new ShellUtils(ip, username, passwd);
        int result = sshExecutor.execute(command);
        Vector<String> stdout = sshExecutor.getStandardOutput();
        for (String str : stdout) {
            System.out.println(str);
        }
        if (result == 0)
            return true;
        else
            return false;
    }
    public Vector<String> getStandardOutput() {
        return stdout;
    }

    public static void main(){
//        String ip = "";
//        String username = "";
//        String passwd = "";
//        String
    }
}

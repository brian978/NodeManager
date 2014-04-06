package nodemanager.node;

import java.io.*;

/**
 * NodeManager
 *
 * @link https://github.com/brian978/NodeManager
 * @copyright Copyright (c) 2014
 * @license Creative Commons Attribution-ShareAlike 3.0
 */
public class NodeRunner extends Thread
{
    private String nodeName;
    private ProcessBuilder processBuilder = null;
    private Process process = null;
    final private Logger stdLogger = new Logger();
    final private Logger errLogger = new Logger();

    public NodeRunner(String name, String path, File executable)
    {
        nodeName = name;
        processBuilder = new ProcessBuilder(executable.getAbsolutePath(), new File(path).getAbsolutePath());
        stdLogger.setFile(new File("log_" + name + ".output.txt"));
        errLogger.setFile(new File("log_" + name + ".error.txt"));
    }

    @Override
    public void run()
    {
        // We need to do some stuff when the thread is closing
        Runtime.getRuntime().addShutdownHook(new Cleaner());

        System.out.println("Starting node process: " + nodeName);

        startProcess();
    }

    private void startProcess()
    {
        try {
            process = processBuilder.start();
            handleOutput(process.getInputStream());
            handleErrorOutput(process.getErrorStream());
            process.waitFor();
        } catch (IOException e) {
            System.out.println("Failed to start the node process");
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void handleOutput(InputStream inputStream)
    {
        synchronized (stdLogger) {
            handleOutput(stdLogger, inputStream, false);
        }
    }

    private void handleErrorOutput(InputStream inputStream)
    {
        synchronized (errLogger) {
            handleOutput(errLogger, inputStream, true);
        }
    }

    private void handleOutput(Logger logger, InputStream inputStream, boolean errorHandler)
    {
        boolean errorOccurred = false;
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        try {
            String line = reader.readLine();
            while (line != null) {
                if (errorHandler && !errorOccurred) {
                    errorOccurred = hasErrorOccurred(line);
                }

                logger.log(line);
                line = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (errorHandler && errorOccurred) {
            System.out.println("An error occurred. Restarting the node process: " + nodeName);
            stdLogger.flush();
            errLogger.flush();
            startProcess();
        }
    }

    private boolean hasErrorOccurred(String line)
    {
        boolean result = false;

        if (line.contains("Error:")) {
            result = true;
        }

        return result;
    }

    private class Cleaner extends Thread
    {
        @Override
        public void run()
        {
            System.out.println("Cleaning up");

            process.destroy();

            synchronized (stdLogger) {
                stdLogger.close();
            }

            synchronized (errLogger) {
                errLogger.close();
            }
        }
    }
}

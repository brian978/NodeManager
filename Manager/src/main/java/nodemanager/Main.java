package nodemanager;

import com.acamar.util.Properties;
import com.acamar.util.StringUtil;
import nodemanager.node.NodeServer;
import nodemanager.node.NodeRunner;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 * NodeManager
 *
 * @link https://github.com/brian978/NodeManager
 * @copyright Copyright (c) 2014
 * @license Creative Commons Attribution-ShareAlike 3.0
 */
public class Main
{
    private Properties properties = new Properties("application.config");
    File config = new File("nodes.xml");
    Document xmlConfig = null;
    ExecutableResolver executableResolver = new ExecutableResolver();
    ArrayList<NodeRunner> nodeRunners = new ArrayList<>();

    public static void main(String[] args)
    {
        try {
            new Main();
        } catch (IOException | SAXException | ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    public Main() throws IOException, SAXException, ParserConfigurationException
    {
        Runtime.getRuntime().addShutdownHook(new Cleaner());

        // Loading the properties
        if (properties.checkAndLoad()) {

            File nodeExecutable = getNodeJsExecutableFile();

            // Reading the XML with the nodes
            if (nodeExecutable != null) {
                DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
                xmlConfig = documentBuilder.parse(config);

                NodeList nodeList = xmlConfig.getFirstChild().getChildNodes();
                Node node;
                NodeList nodeConfig;
                String domNodeName, methodName;
                NodeRunner nodeRunner;
                NodeServer nodeServer;
                Method classMethod;

                for (int i = 0; i < nodeList.getLength(); i++) {
                    node = nodeList.item(i);

                    // We only care about node elements (not texts)
                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        nodeConfig = node.getChildNodes();
                        nodeServer = new NodeServer();

                        for (int j = 0; j < nodeConfig.getLength(); j++) {
                            node = nodeConfig.item(j);

                            // We only care about node elements (not texts)
                            if (node.getNodeType() == Node.ELEMENT_NODE) {
                                domNodeName = node.getNodeName();

                                try {
                                    methodName = "set" + StringUtil.ucFirst(domNodeName);
                                    classMethod = NodeServer.class.getMethod(methodName, String.class);
                                } catch (NoSuchMethodException e) {
                                    classMethod = null;
                                    e.printStackTrace();
                                }

                                if(classMethod != null) {
                                    try {
                                        classMethod.invoke(nodeServer, node.getTextContent());
                                    } catch (IllegalAccessException | InvocationTargetException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }

                        // Configuring the node runner object
                        nodeRunner = new NodeRunner(nodeServer, nodeExecutable);

                        // Starting the thread
                        new Thread(nodeRunner).start();

                        // Tracking the runner
                        nodeRunners.add(nodeRunner);
                    }
                }
            } else {
                System.out.println("Could not find the node.js executable file (or an invalid one was provided)");
            }
        } else {
            System.out.println("Failed to load (or create) the properties file");
        }
    }

    private File getNodeJsExecutableFile()
    {
        String nodePath = "";

        // Loading the properties for the Runnable objects
        // Or trying to create them
        String nodeExecutablePath = properties.getProperty("nodePath");
        File nodeExecutable = null;
        if (nodeExecutablePath != null) {
            nodeExecutable = new File(nodeExecutablePath);
        }

        if (nodeExecutable == null || !nodeExecutable.exists()) {
            // Trying to find the node.js executable without asking the user
            nodeExecutable = executableResolver.getFile();

            // Last resort
            // Prompting the user for the executable is not found
            if(nodeExecutable == null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                System.out.println("Please provide the path to the node.js executable: ");
                try {
                    nodeExecutable = new File(reader.readLine());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        if (nodeExecutable != null && nodeExecutable.exists()) {
            nodePath = nodeExecutable.getAbsolutePath();
        } else {
            nodeExecutable = null;
        }

        properties.setProperty("nodePath", nodePath);

        return nodeExecutable;
    }

    private class Cleaner extends Thread
    {
        @Override
        public void run()
        {
            // When the application closes we want to store the configuration to avoid searching for different
            // stuff again
            try {
                properties.store();
            } catch (IOException e) {
                e.printStackTrace();
            }

            System.out.println("Shutting down. Goodbye!");
        }
    }
}

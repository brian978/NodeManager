package nodemanager;

import com.acamar.util.Properties;
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
import java.util.Vector;

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
                String nodeName, nodePath;
                NodeRunner nodeRunner;

                for (int i = 0; i < nodeList.getLength(); i++) {
                    node = nodeList.item(i);

                    // We only care about node elements (not texts)
                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        // Getting the node.js app name
                        node = node.getFirstChild().getNextSibling();
                        nodeName = node.getTextContent();

                        // Getting the node.js app path
                        node = node.getNextSibling().getNextSibling();
                        nodePath = node.getTextContent();

                        // Configuring the node runner object
                        nodeRunner = new NodeRunner(nodeName, nodePath, nodeExecutable);

                        // Starting the thread
                        new Thread(nodeRunner).start();
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
            // Trying to find the node.js executable
            nodeExecutable = executableResolver.getFile();

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

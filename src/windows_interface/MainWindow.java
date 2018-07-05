package windows_interface;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputMethodEvent;
import java.awt.event.InputMethodListener;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.Scanner;
import java.util.Vector;

import business_logic.Checker;
import business_logic.Graph;

/**
 * Start Window
 * @author Grigoriev Ivan
 * @version 1.1
 */

public class MainWindow extends JFrame {
    private JPanel panel;
    private JTextField textFieldEdgeNum;
    private JTextField textFieldVertexNum;
    private JButton getGraphFromFileButton;
    private JButton generateRandomGraphWithButton;


    public MainWindow() {
        super("Topological sorting of a graph");

        this.getContentPane().add(panel);

        getGraphFromFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Graph graph = getGraphFromFile();
                if(graph != null) {
                    Checker checker = new Checker(graph);
                    if(checker.CheckCycle())
                        goToGraphVisualizationWindow(graph);
                    else {
                        JOptionPane.showMessageDialog(panel,
                                "Graph contains cycles",
                                "Graph error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        generateRandomGraphWithButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Graph graph = createRandomGraph();
                goToGraphVisualizationWindow(graph);
            }
        });
    }

    private Graph getGraphFromFile() {
        JFileChooser file_chooser = new JFileChooser();

        file_chooser.setCurrentDirectory(new File("."));

        file_chooser.setFileFilter(new FileNameExtensionFilter(
                "Text files", "txt"));

        int ret = file_chooser.showOpenDialog(this);
        if (ret != JFileChooser.APPROVE_OPTION) {
            return null;
        }
        File file = file_chooser.getSelectedFile();

        try {
            Scanner sc = new Scanner(file);

            Vector<Vector<String>> incidencde_list = new Vector<Vector<String>>();

            while (sc.hasNextLine()) {
                String[] vertex_names = sc.nextLine().split("\\s");
                Vector<String> tmp_vector = new Vector<String>();
                for(String name : vertex_names) {
                    tmp_vector.add(name);
                }
                incidencde_list.add(tmp_vector);
            }
            return createGraph(incidencde_list);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                    "File is incorrect.",
                    "Format error",
                    JOptionPane.ERROR_MESSAGE);
        }
        return null;
    }

    private Graph createGraph(Vector<Vector<String>> incidencde_list) {
        int m_size = incidencde_list.size(); //размер квадратной матрицы

        if(m_size > 10) {
            JOptionPane.showMessageDialog(this,
                    "File is incorrect.",
                    "Format error",
                    JOptionPane.ERROR_MESSAGE);
            return null;
        }

        int[][] inc_matrix = new int[m_size][m_size]; //матрица инц.
        String[] node_names = new String[m_size]; //массив имен вершин

        for (int i = 0; i < m_size; i++) //получение массива имен
            node_names[i] = incidencde_list.get(i).firstElement();

        for (int i = 0; i < m_size; i++) { //получение матрицы инц.
            for (int j = 0; j < incidencde_list.get(i).size(); j++)
                inc_matrix[i][getIndexOfNode(node_names, incidencde_list.get(i).get(j))] = 1;
            inc_matrix[i][i] = 0;
        }

        return new Graph(inc_matrix, node_names);

    }

    private int getIndexOfNode(String[] arr, String node_name) {
        for (int i = 0; i < arr.length; i++) {
            if(arr[i].equals(node_name))
                return i;
        }
        return -1;
    }

    private Graph createRandomGraph() {
        int vertexNum = Integer.parseInt(textFieldEdgeNum.getText());
        int edgeNum = Integer.parseInt(textFieldVertexNum.getText());

        if(!checkForCorrectInput(vertexNum, edgeNum)) {
            return null;
        }

        int[][] inc_matrix = new int[vertexNum][vertexNum]; //матрица инц.
        String[] node_names = new String[vertexNum]; //массив имен вершин

        String alphabet = new String("abcdefghijklmnopqrstuvwxyz");
        for (int i = 0; i < vertexNum; i++) //получаем имена
            node_names[i] = String.valueOf(alphabet.charAt(i));

        Checker checker;
        Graph graph;
        Random rnd = new Random(System.currentTimeMillis());
        do {
            for (int i = 0; i < vertexNum; i++) {
                for (int j = 0; j < vertexNum; j++) {
                    int rand_int = rnd.nextInt(101) % 2; //0 or 1
                    if(rand_int == 1 && edgeNum != 0 && i != j) {
                        inc_matrix[i][j] = rand_int;
                        edgeNum--;
                    }
                }
            }
            graph = new Graph(inc_matrix, node_names);
            checker = new Checker(graph);
        } while (!checker.CheckCycle());

        return graph;
    }

    private boolean checkForCorrectInput(int vertexNum, int edgeNum) {
        int maxEdges = vertexNum * (vertexNum - 1) / 2;
        int minEdges = vertexNum - 1;
        if (vertexNum > 10 || vertexNum <= 0) {
            JOptionPane.showMessageDialog(this,
                    "0 < Vertex number <= 10.",
                    "Incorrect input",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (edgeNum > 20 || edgeNum <= 0) {
            JOptionPane.showMessageDialog(this,
                    "0 < Edge number <= 20.",
                    "Incorrect input",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (edgeNum > maxEdges) {
            JOptionPane.showMessageDialog(this,
                    "Too many edges for " + vertexNum + " vertexes.",
                    "Incorrect input",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (edgeNum < minEdges) {
            JOptionPane.showMessageDialog(this,
                    "Too few edges for " + vertexNum + " vertexes.",
                    "Incorrect input",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    private void goToGraphVisualizationWindow(Graph graph) {
        closeMainWindow();

        GraphVisualizationWindow graphVisualizationWindow = new GraphVisualizationWindow(graph);

        graphVisualizationWindow.pack(); //упаковываем все элементы с нашей формы
        graphVisualizationWindow.setSize(new Dimension(700, 600));
        graphVisualizationWindow.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int locationX = (screenSize.width - 700) / 2;
        int locationY = (screenSize.height - 600) / 2;
        graphVisualizationWindow.setBounds(locationX, locationY, 700, 600);

        graphVisualizationWindow.setVisible(true);
    }

    private void closeMainWindow() {
        setVisible(false);
        dispose();
    }
}
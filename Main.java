import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class Main extends JFrame implements MouseListener {
    private static int BASESIZE = 30;
    private static int tx = 50;
    private static int ty = 60;
    private boolean who = true;
    private boolean enable = true;
    private int[][] board;
    private ServerSocket ss;
    private InputStream input;
    private String host;
    private int port1;
    private int port2;

    public Main(String title, String host, int port1, int port2) {
        super(title);
        this.host = host;
        this.port1 = port1;
        this.port2 = port2;
        board = new int[15][15];
        //�����̸����������ʼ��Ϊ-1
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                board[i][j] = - 1;
            }
        }
        try {
            ss = new ServerSocket(port1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //  �½�һ�������߳�
        new Thread() {
            @Override
            public void run() {
                try {
                    while (true) {//ѭ���ȴ�����
                        Socket s = ss.accept();
                        input = s.getInputStream();
                        byte[] data = new byte[1024];
                        int length = input.read(data);
                        String string = new String(data, 0, length);
                        String[] point = string.split("-");
                        int x = Integer.parseInt(point[0]);
                        int y = Integer.parseInt(point[1]);
                        //�������
                        board[y][x] = who?1:0;
                        paintChess(x, y, who); //���ö�������
                        who = ! who; //����ɫ�л�����
                        enable = true;//��������������
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
        setBackground(Color.lightGray);
        setSize(520, 520);
        setVisible(true);
        setResizable(false);
        this.addMouseListener(this);
    }

    @Override
    public void mouseReleased(MouseEvent mouseEvent) {
        if (! enable)
            return;
        int x = mouseEvent.getX();
        int y = mouseEvent.getY();
        x = (int) Math.round((1.0 * x - tx) / BASESIZE);
        y = (int) Math.round((1.0 * y - ty) / BASESIZE);
        //Խ���ж�
        if (y >= 15 || x >= 15 || y < 0 || x < 0)
            return;
        if (board[y][x] == 1 || board[y][x] == 0)
            return;

        //�������ݵ�����
        try {
            Socket s = new Socket(host, port2);
            OutputStream output = s.getOutputStream();
            output.write((x + "-" + y).getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        //�������
        board[y][x] = who ? 1 : 0;
        //�����������
        paintChess(x, y, who);
        who = ! who;//�л���ɫ
        enable = false;//����һ�����Ӻ��������£�ֱ����������
    }


    @Override
    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        paintMap(g2d);
    }

    //������
    public void paintMap(Graphics g) {
        g.translate(tx, ty);//ת������ϵ
        for (int i = 0; i < 15; i++) {
            g.drawLine(0, i * BASESIZE, 14 * BASESIZE, i * BASESIZE);
            g.drawLine(i * BASESIZE, 0, i * BASESIZE, 14 * BASESIZE);
        }
    }

    public void paintChess(int x, int y, boolean who) {
        Graphics g = getGraphics();
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(who ? Color.black : Color.white);
        g2d.fillOval(x * BASESIZE + tx - BASESIZE / 2, y * BASESIZE + ty - BASESIZE / 2, BASESIZE, BASESIZE);
        checkWiner();
    }

    public void checkWiner() {// �ж�ʤ��
        int black_count = 0;
        int white_count = 0;
        for (int i = 0; i < 15; i++) {// �����ж�
            black_count = 0;
            white_count = 0;
            for (int j = 0; j < 15; j++) {
                if (board[i][j] == 1) {
                    black_count++;
                    if (black_count == 5) {
                        messageBox("����ʤ��");
                        return;
                    }
                } else {
                    black_count = 0;
                }
                if (board[i][j] == 0) {
                    white_count++;
                    if (white_count == 5) {
                        messageBox("����ʤ��");
                        return;
                    }
                } else {
                    white_count = 0;
                }
            }
        }
        for (int i = 0; i < 15; i++) {// �����ж�
            black_count = 0;
            white_count = 0;
            for (int j = 0; j < 15; j++) {
                if (board[j][i] == 1) {
                    black_count++;
                    if (black_count == 5) {
                        messageBox("����ʤ��");
                        return;
                    }
                } else {
                    black_count = 0;
                }
                if (board[j][i] == 0) {
                    white_count++;
                    if (white_count == 5) {
                        messageBox("����ʤ��");
                        return;
                    }
                } else {
                    white_count = 0;
                }
            }
        }
        for (int i = 0; i <= 10; i++) {// ������б�ж�
            for (int j = 0; j <= 10; j++) {
                black_count = 0;
                white_count = 0;
                for (int k = 0; k < 5; k++) {
                    if (board[i + k][j + k] == 1) {
                        black_count++;
                        if (black_count == 5) {
                            messageBox("����ʤ��");
                            return;
                        }
                    } else {
                        black_count = 0;
                    }
                    if (board[i + k][j + k] == 0) {
                        white_count++;
                        if (white_count == 5) {
                            messageBox("����ʤ��");
                            return;
                        }
                    } else {
                        white_count = 0;
                    }
                }
            }
        }
        for (int i = 4; i < 15; i++) {// ������б�ж� 11->12
            for (int j = 0; j <= 10; j++) {
                black_count = 0;
                white_count = 0;
                for (int k = 0; k < 5; k++) {
                    if (board[i - k][j + k] == 1) {
                        black_count++;
                        if (black_count == 5) {
                            messageBox("����ʤ��");
                            return;
                        }
                    } else {
                        black_count = 0;
                    }
                    if (board[i - k][j + k] == 0) {
                        white_count++;
                        if (white_count == 5) {
                            messageBox("����ʤ��");
                            return;
                        }
                    } else {
                        white_count = 0;
                    }
                }
            }
        }
    }
    public void messageBox(String message){
        JOptionPane.showMessageDialog(this,message,"��Ϸ����",JOptionPane.PLAIN_MESSAGE);
        System.exit(1);
    }
    @Override
    public void mouseEntered(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseExited(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseClicked(MouseEvent mouseEvent) {

    }

    @Override
    public void mousePressed(MouseEvent mouseEvent) {

    }

    public static void main(String[] args) {
        Main app1 = new Main("������������ɫA", "localhost", 7878, 7879);
        app1.setDefaultCloseOperation(EXIT_ON_CLOSE);
        Main app2 = new Main("������������ɫB", "localhost", 7879, 7878);
        app1.setLocation(200,100);
        app2.setLocation(800,100);
    }
}
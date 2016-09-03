
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.Reader;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;

/**
 *         模板说明：该模板主要提供依赖Swing组件提供的JPanle，JFrame，JButton等提供的GUI
 *         。使用“监听器”模式监听各个Button的事件，从而根据具体事件执行不同方法。 Tomasulo算法核心需同学们自行完成，见说明（4）
 *         对于界面必须修改部分，见说明(1),(2),(3)
 *
 *         (1)说明：根据你的设计完善指令设置中的下拉框内容
 *         (2)说明：请根据你的设计指定各个面板（指令状态，保留站，Load部件，寄存器部件）的大小 (3)说明：设置界面默认指令 (4)说明：
 *         Tomasulo算法实现
 */

public class Tomasulo extends JFrame implements ActionListener {
	/*
	 * 界面上有六个面板： panel1 : 指令设置 panel2 : 执行时间设置 panel3 : 指令状态 panel4 : 保留站状态
	 * panel5 : Load部件 panel6 : 寄存器状态
	 */
	private JPanel panel1, panel2, panel3, panel4, panel5, panel6;

	/*
	 * 四个操作按钮：步进，进5步，重置，执行
	 */
	private JButton stepbut, step5but, resetbut, startbut;

	/*
	 * 指令选择框
	 */
	private JComboBox instbox[] = new JComboBox[24];

	/*
	 * 每个面板的名称
	 */
	private JLabel instl, timel, tl1, tl2, tl3, tl4, resl, regl, ldl, insl,
			stepsl;
	private int time[] = new int[4];

	/*
	 * 部件执行时间的输入框
	 */
	private JTextField tt1, tt2, tt3, tt4;

	private int intv[][] = new int[6][4], cnow, instnow = 0;
	private int cal[][] = { { -1, 0, 0 }, { -1, 0, 0 }, { -1, 0, 0 },
			{ -1, 0, 0 }, { -1, 0, 0 } };
	private int ld[][] = { { 0, 0 }, { 0, 0 }, { 0, 0 } };
	private int ff[] = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };

	/*
	 * (1)说明：根据你的设计完善指令设置中的下拉框内容 inst：
	 * 指令下拉框内容:"NOP","L.D","ADD.D","SUB.D","MULT.D","DIV.D"………… fx：
	 * 目的寄存器下拉框内容:"F0","F2","F4","F6","F8" ………… rx：
	 * 源操作数寄存器内容:"R0","R1","R2","R3","R4","R5","R6","R7","R8","R9" ………… ix：
	 * 立即数下拉框内容:"0","1","2","3","4","5","6","7","8","9" …………
	 */
	private String inst[] = { "NOP", "L.D", "ADD.D", "SUB.D", "MULT.D", "DIV.D" },
			fx[] = { "F0", "F2", "F4", "F6", "F8", "F10", "F12", "F14", "F16",
					"F18", "F20", "F22", "F24", "F26", "F28", "F30" },
			rx[] = { "R0", "R1", "R2", "R3", "R4", "R5", "R6", "R7", "R8",
					"R9", "R10", "R11", "R12", "R13", "R14", "R15", "R16",
					"R17", "R18", "R19", "R20", "R21", "R22", "R23", "R24",
					"R25", "R26", "R27", "R28", "R29", "R30", "R31" },
			ix[] = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10",
					"11", "12", "13", "14", "15", "16", "17", "18", "19", "20",
					"21", "22", "23", "24", "25", "26", "27", "28", "29", "30",
					"31" };

	/*
	 * (2)说明：请根据你的设计指定各个面板（指令状态，保留站，Load部件，寄存器部件）的大小 指令状态 面板 保留站 面板 Load部件 面板
	 * 寄存器 面板 的大小
	 */
	private String instst[][] = new String[7][4], resst[][] = new String[6][8],
			ldst[][] = new String[4][4], regst[][] = new String[3][17];
	private JLabel instjl[][] = new JLabel[7][4], resjl[][] = new JLabel[6][8],
			ldjl[][] = new JLabel[4][4], regjl[][] = new JLabel[3][17];

	// 构造方法
	public Tomasulo() {
		super("Tomasulo Simulator");

		// 设置布局
		Container cp = getContentPane();
		FlowLayout layout = new FlowLayout();
		cp.setLayout(layout);

		// 指令设置。GridLayout(int 指令条数, int 操作码+操作数, int hgap, int vgap)
		instl = new JLabel("指令设置");
		panel1 = new JPanel(new GridLayout(6, 4, 0, 0));
		panel1.setPreferredSize(new Dimension(350, 150));
		panel1.setBorder(new EtchedBorder(EtchedBorder.RAISED));

		// 操作按钮:执行，重设，步进，步进5步
		timel = new JLabel("执行时间设置");
		panel2 = new JPanel(new GridLayout(2, 4, 0, 0));
		panel2.setPreferredSize(new Dimension(280, 80));
		panel2.setBorder(new EtchedBorder(EtchedBorder.RAISED));

		// 指令状态
		insl = new JLabel("指令状态");
		panel3 = new JPanel(new GridLayout(7, 4, 0, 0));
		panel3.setPreferredSize(new Dimension(600, 175));// 420,175
		panel3.setBorder(new EtchedBorder(EtchedBorder.RAISED));

		// 保留站
		resl = new JLabel("保留站");
		panel4 = new JPanel(new GridLayout(6, 7, 0, 0));
		panel4.setPreferredSize(new Dimension(1350, 150));// 420, 150
		panel4.setBorder(new EtchedBorder(EtchedBorder.RAISED));

		// Load部件
		ldl = new JLabel("Load部件");
		panel5 = new JPanel(new GridLayout(4, 4, 0, 0));
		panel5.setPreferredSize(new Dimension(600, 100));// 200,100
		panel5.setBorder(new EtchedBorder(EtchedBorder.RAISED));

		// 寄存器状态
		regl = new JLabel("寄存器");
		panel6 = new JPanel(new GridLayout(3, 17, 0, 0));
		panel6.setPreferredSize(new Dimension(1350, 75));// 740,75
		panel6.setBorder(new EtchedBorder(EtchedBorder.RAISED));

		tl1 = new JLabel("Load");
		tl2 = new JLabel("加/减");
		tl3 = new JLabel("乘法");
		tl4 = new JLabel("除法");

		// 操作按钮:执行，重设，步进，步进5步
		stepsl = new JLabel();
		stepsl.setPreferredSize(new Dimension(200, 30));
		stepsl.setHorizontalAlignment(SwingConstants.CENTER);
		stepsl.setBorder(new EtchedBorder(EtchedBorder.RAISED));
		stepbut = new JButton("步进");
		stepbut.addActionListener(this);
		step5but = new JButton("步进5步");
		step5but.addActionListener(this);
		startbut = new JButton("执行");
		startbut.addActionListener(this);
		resetbut = new JButton("重设");
		resetbut.addActionListener(this);
		tt1 = new JTextField("2");
		tt2 = new JTextField("2");
		tt3 = new JTextField("10");
		tt4 = new JTextField("40");

		// 指令设置
		/*
		 * 设置指令选择框（操作码，操作数，立即数等）的default选择
		 */// private JComboBox instbox[]=new JComboBox[24];
		for (int i = 0; i < 2; i++)
			for (int j = 0; j < 4; j++) {
				if (j == 0) {
					instbox[i * 4 + j] = new JComboBox(inst);
				} else if (j == 1) {
					instbox[i * 4 + j] = new JComboBox(fx);
				} else if (j == 2) {
					instbox[i * 4 + j] = new JComboBox(ix);
				} else {
					instbox[i * 4 + j] = new JComboBox(rx);
				}
				instbox[i * 4 + j].addActionListener(this);
				panel1.add(instbox[i * 4 + j]);
			}
		for (int i = 2; i < 6; i++)
			for (int j = 0; j < 4; j++) {
				if (j == 0) {
					instbox[i * 4 + j] = new JComboBox(inst);
				} else {
					instbox[i * 4 + j] = new JComboBox(fx);
				}
				instbox[i * 4 + j].addActionListener(this);
				panel1.add(instbox[i * 4 + j]);
			}
		/*
		 * (3)说明：设置界面默认指令，根据你设计的指令，操作数等的选择范围进行设置。 默认6条指令。待修改
		 */
		instbox[0].setSelectedIndex(1);
		instbox[1].setSelectedIndex(4);
		instbox[2].setSelectedIndex(21);
		instbox[3].setSelectedIndex(3);

		instbox[4].setSelectedIndex(1);
		instbox[5].setSelectedIndex(2);
		instbox[6].setSelectedIndex(16);
		instbox[7].setSelectedIndex(4);

		instbox[8].setSelectedIndex(4);
		instbox[9].setSelectedIndex(1);
		instbox[10].setSelectedIndex(2);
		instbox[11].setSelectedIndex(3);

		instbox[12].setSelectedIndex(3);
		instbox[13].setSelectedIndex(5);
		instbox[14].setSelectedIndex(4);
		instbox[15].setSelectedIndex(2);

		instbox[16].setSelectedIndex(5);
		instbox[17].setSelectedIndex(6);
		instbox[18].setSelectedIndex(1);
		instbox[19].setSelectedIndex(4);

		instbox[20].setSelectedIndex(2);
		instbox[21].setSelectedIndex(4);
		instbox[22].setSelectedIndex(5);
		instbox[23].setSelectedIndex(2);

		// 执行时间设置
		panel2.add(tl1);// tl1 = new JLabel("Load");
		panel2.add(tt1);// tt1 = new JTextField("2");
		panel2.add(tl2);
		panel2.add(tt2);
		panel2.add(tl3);
		panel2.add(tt3);
		panel2.add(tl4);
		panel2.add(tt4);

		// 指令状态设置
		for (int i = 0; i < 7; i++) {
			for (int j = 0; j < 4; j++) {
				instjl[i][j] = new JLabel(instst[i][j]);// string[][]
				instjl[i][j].setBorder(new EtchedBorder(EtchedBorder.RAISED));
				panel3.add(instjl[i][j]);
			}
		}
		// 保留站设置
		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < 8; j++) {
				resjl[i][j] = new JLabel(resst[i][j]);
				resjl[i][j].setBorder(new EtchedBorder(EtchedBorder.RAISED));
				panel4.add(resjl[i][j]);
			}
		}
		// Load部件设置
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				ldjl[i][j] = new JLabel(ldst[i][j]);
				ldjl[i][j].setBorder(new EtchedBorder(EtchedBorder.RAISED));
				panel5.add(ldjl[i][j]);
			}
		}
		// 寄存器设置
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 17; j++) {
				regjl[i][j] = new JLabel(regst[i][j]);
				regjl[i][j].setBorder(new EtchedBorder(EtchedBorder.RAISED));
				panel6.add(regjl[i][j]);
			}
		}

		// 向容器添加以上部件
		cp.add(instl);
		cp.add(panel1);
		cp.add(timel);
		cp.add(panel2);

		cp.add(startbut);
		cp.add(resetbut);
		cp.add(stepbut);
		cp.add(step5but);

		cp.add(panel3);
		cp.add(insl);
		cp.add(panel5);
		cp.add(ldl);
		cp.add(panel4);
		cp.add(resl);
		cp.add(stepsl);
		cp.add(panel6);
		cp.add(regl);

		stepbut.setEnabled(false);
		step5but.setEnabled(false);
		panel3.setVisible(false);
		insl.setVisible(false);
		panel4.setVisible(false);
		ldl.setVisible(false);
		panel5.setVisible(false);
		resl.setVisible(false);
		stepsl.setVisible(false);
		panel6.setVisible(false);
		regl.setVisible(false);
		// setSize(820,620);
		setSize(1400, 700);
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	/*
	 * 点击”执行“按钮后，根据选择的指令，初始化其他几个面板
	 */
	public void init() {
		// get value
		for (int i = 0; i < 6; i++) {
			intv[i][0] = instbox[i * 4].getSelectedIndex();
			if (intv[i][0] != 0) {
				intv[i][1] = 2 * instbox[i * 4 + 1].getSelectedIndex();
				if (intv[i][0] == 1) {
					intv[i][2] = instbox[i * 4 + 2].getSelectedIndex();
					intv[i][3] = instbox[i * 4 + 3].getSelectedIndex();
				} else {
					intv[i][2] = 2 * instbox[i * 4 + 2].getSelectedIndex();
					intv[i][3] = 2 * instbox[i * 4 + 3].getSelectedIndex();
				}
			}
		}
		time[0] = Integer.parseInt(tt1.getText());
		time[1] = Integer.parseInt(tt2.getText());
		time[2] = Integer.parseInt(tt3.getText());
		time[3] = Integer.parseInt(tt4.getText());
		// System.out.println(time[0]);
		// set 0
		instst[0][0] = "指令";
		instst[0][1] = "流出";
		instst[0][2] = "执行";
		instst[0][3] = "写回";

		/*
		 * private String instst[][]=new String[7][4], resst[][]=new
		 * String[6][8], ldst[][]=new String[4][4], regst[][]=new String[3][17];
		 * private JLabel instjl[][]=new JLabel[7][4], resjl[][]=new
		 * JLabel[6][8], ldjl[][]=new JLabel[4][4], regjl[][]=new JLabel[3][17];
		 */
		ldst[0][0] = "名称";
		ldst[0][1] = "Busy";
		ldst[0][2] = "地址";
		ldst[0][3] = "值";
		ldst[1][0] = "Load1";
		ldst[2][0] = "Load2";
		ldst[3][0] = "Load3";
		ldst[1][1] = "no";
		ldst[2][1] = "no";
		ldst[3][1] = "no";

		resst[0][0] = "Time";
		resst[0][1] = "名称";
		resst[0][2] = "Busy";
		resst[0][3] = "Op";
		resst[0][4] = "Vj";
		resst[0][5] = "Vk";
		resst[0][6] = "Qj";
		resst[0][7] = "Qk";
		resst[1][1] = "Add1";
		resst[2][1] = "Add2";
		resst[3][1] = "Add3";
		resst[4][1] = "Mult1";
		resst[5][1] = "Mult2";
		resst[1][2] = "no";
		resst[2][2] = "no";
		resst[3][2] = "no";
		resst[4][2] = "no";
		resst[5][2] = "no";

		regst[0][0] = "字段";
		for (int i = 1; i < 17; i++) {
			// System.out.print(i+" "+fx[i-1];
			regst[0][i] = fx[i - 1];// fx[]={"F0","F2","F4","F6","F8"}
		}
		regst[1][0] = "状态";

		regst[2][0] = "值";

		for (int i = 1; i < 7; i++)
			for (int j = 0; j < 4; j++) {
				if (j == 0) {
					int temp = i - 1;
					String disp;
					disp = inst[instbox[temp * 4].getSelectedIndex()] + " ";
					if (instbox[temp * 4].getSelectedIndex() == 0)
						disp = disp;
					else if (instbox[temp * 4].getSelectedIndex() == 1) {
						disp = disp
								+ fx[instbox[temp * 4 + 1].getSelectedIndex()]
								+ ','
								+ ix[instbox[temp * 4 + 2].getSelectedIndex()]
								+ '('
								+ rx[instbox[temp * 4 + 3].getSelectedIndex()]
								+ ')';
					} else {
						disp = disp
								+ fx[instbox[temp * 4 + 1].getSelectedIndex()]
								+ ','
								+ fx[instbox[temp * 4 + 2].getSelectedIndex()]
								+ ','
								+ fx[instbox[temp * 4 + 3].getSelectedIndex()];
					}
					instst[i][j] = disp;
				}// if(j==0)
				else
					instst[i][j] = "";
			}
		for (int i = 1; i < 6; i++)
			for (int j = 0; j < 8; j++)
				if (j != 1 && j != 2) {
					resst[i][j] = "";
				}
		for (int i = 1; i < 4; i++)
			for (int j = 2; j < 4; j++) {
				ldst[i][j] = "";
			}
		for (int i = 1; i < 3; i++)
			for (int j = 1; j < 17; j++) {
				regst[i][j] = "";
			}

		for (int i = 1; i < 17; i++) {
			// 将每个寄存器其的初值都设置为1，保证当指令最先使用某个寄存器值时，会有初值；
			regst[1][i] = "0";
		}
		for (int i = 1; i < 17; i++) {
			regst[2][i] = "1";
		}

		instnow = 0;
		for (int i = 0; i < 5; i++) {
			for (int j = 1; j < 3; j++)
				cal[i][j] = 0;
			cal[i][0] = -1;
		}
		for (int i = 0; i < 3; i++)
			for (int j = 0; j < 2; j++)
				ld[i][j] = 0;
		for (int i = 0; i < 17; i++)
			ff[i] = 0;
	}

	/*
	 * 点击操作按钮后，用于显示结果
	 */
	public void display() {
		/*
		 * private String instst[][]=new String[7][4], resst[][]=new
		 * String[6][8], ldst[][]=new String[4][4], regst[][]=new String[3][17];
		 * private JLabel instjl[][]=new JLabel[7][4], resjl[][]=new
		 * JLabel[6][8], ldjl[][]=new JLabel[4][4], regjl[][]=new JLabel[3][17];
		 */
		for (int i = 0; i < 7; i++)
			for (int j = 0; j < 4; j++) {
				instjl[i][j].setText(instst[i][j]);
			}
		for (int i = 0; i < 6; i++)
			for (int j = 0; j < 8; j++) {
				resjl[i][j].setText(resst[i][j]);
			}
		for (int i = 0; i < 4; i++)
			for (int j = 0; j < 4; j++) {
				ldjl[i][j].setText(ldst[i][j]);
			}
		for (int i = 0; i < 3; i++)
			for (int j = 0; j < 17; j++) {
				regjl[i][j].setText(regst[i][j]);
			}
		
		/*
		 * for(int i = 0;i<3;i++){
			for(int j = 0;j<8;j++){
				regjl_1[i][j].setText(regst[i][j]);
			}
		}*/
		stepsl.setText("当前周期：" + String.valueOf(cnow - 1));
	}

	public void actionPerformed(ActionEvent e) {
		// 点击“执行”按钮的监听器
		if (e.getSource() == startbut) {
			for (int i = 0; i < 24; i++)
				instbox[i].setEnabled(false);
			tt1.setEnabled(false);
			tt2.setEnabled(false);
			tt3.setEnabled(false);
			tt4.setEnabled(false);
			stepbut.setEnabled(true);
			step5but.setEnabled(true);
			startbut.setEnabled(false);
			// 根据指令设置的指令初始化其他的面板
			init();
			cnow = 1;
			// 展示其他面板
			display();
			panel3.setVisible(true);
			panel4.setVisible(true);
			panel5.setVisible(true);
			panel6.setVisible(true);
			insl.setVisible(true);
			ldl.setVisible(true);
			resl.setVisible(true);
			stepsl.setVisible(true);
			regl.setVisible(true);
		}
		// 点击“重置”按钮的监听器
		if (e.getSource() == resetbut) {
			for (int i = 0; i < 24; i++)
				instbox[i].setEnabled(true);
			tt1.setEnabled(true);
			tt2.setEnabled(true);
			tt3.setEnabled(true);
			tt4.setEnabled(true);
			stepbut.setEnabled(false);
			step5but.setEnabled(false);
			startbut.setEnabled(true);
			panel3.setVisible(false);
			insl.setVisible(false);
			panel4.setVisible(false);
			ldl.setVisible(false);
			panel5.setVisible(false);
			resl.setVisible(false);
			stepsl.setVisible(false);
			panel6.setVisible(false);
			regl.setVisible(false);
		}
		// 点击“步进”按钮的监听器
		if (e.getSource() == stepbut) {
			core();
			cnow++;
			display();
		}
		// 点击“进5步”按钮的监听器
		if (e.getSource() == step5but) {
			for (int i = 0; i < 5; i++) {
				core();
				cnow++;
			}
			display();
		}

		for (int i = 0; i < 24; i = i + 4) {//
			if (e.getSource() == instbox[i]) {
				if (instbox[i].getSelectedIndex() == 1) {
					instbox[i + 2].removeAllItems();
					for (int j = 0; j < ix.length; j++)
						instbox[i + 2].addItem(ix[j]);
					instbox[i + 3].removeAllItems();
					for (int j = 0; j < rx.length; j++)
						instbox[i + 3].addItem(rx[j]);
				} else {
					instbox[i + 2].removeAllItems();
					for (int j = 0; j < fx.length; j++)
						instbox[i + 2].addItem(fx[j]);
					instbox[i + 3].removeAllItems();
					for (int j = 0; j < fx.length; j++)
						instbox[i + 3].addItem(fx[j]);
				}// if-else
			}// if
		}// for
	}// actionPerformed

	/*
	 * (4)说明： Tomasulo算法实现
	 */

	boolean instIssue(String op, String rd, String rs, String rt) {
		int IsdoSomething = -1;
		int whichr = -1;
		// 选择空闲的保留站
		if (op.equals("ADD") || op.equals("SUB")) {
			for (int i = 1; i < 4; i++) {
				if (resst[i][2].equals("no")) {
					whichr = i;
					break;
				}
			}
		} else {
			for (int i = 4; i < 6; i++) {
				if (resst[i][2].equals("no")) {
					whichr = i;
					break;
				}
			}
		}

		if (whichr > 0) {
			// 成功找到一个空闲的保留站
			String r = resst[whichr][1];
			for (int i = 1; i < 17; i++) {
				// 检测第一个操作数是否就绪
				if (regst[0][i].equals(rs)) {
					if (regst[1][i].equals("0")) {
						// 第一个操作数就绪，把寄存器rs中的操作数取到当前的保留站Vj
						resst[whichr][4] = regst[2][i];

						// 置Qj为0，表示当前保留站的Vj中的操作数就绪
						resst[whichr][6] = "0";
					} else {
						// 第一个操作数没有就绪
						// 进行寄存器换名，即把将产生该操作数的保留站的编号放入当前保留站的Qj
						resst[whichr][6] = regst[1][i];
					}// if-else
				}

				// ------------------------------------------------------
				if (regst[0][i].equals(rt)) {
					if (regst[1][i].equals("0")) {
						// 第二个操作数就绪，把寄存器rs中的操作数取到当前的保留站Vj
						resst[whichr][5] = regst[2][i];

						// 置Qk为0，表示当前保留站的Vk中的操作数就绪
						resst[whichr][7] = "0";
					} else {
						// 第二个操作数没有就绪
						// 进行寄存器换名，即把将产生该操作数的保留站的编号放入当前保留站的Qk
						resst[whichr][7] = regst[1][i];
					}// if-else
				}
			}

			resst[whichr][2] = "Yes";
			resst[whichr][3] = op;
			for (int i = 1; i < 17; i++) {
				if (regst[0][i].equals(rd)) {
					regst[1][i] = r;
				}
			}

			IsdoSomething = 1;
			return true;

		} else {
			System.out.println("there is a wrong.");
		}

		if (IsdoSomething > 0) {
			return true;
		} else {
			return false;
		}
	}

	boolean instExcute(String op, String rd, String rs, String rt) {
		// 指令开始执行
		// 这里是要计算执行时间的
		int whichToRun = -1;
		for (int i = 1; i < 6; i++) {
			
			if (resst[i][3] == op) {
				whichToRun = i;
			}
		}
		// Time为空，则判断运算是否符合 进入执行的条件
		if (resst[whichToRun][6] == "0" && resst[whichToRun][7] == "0") {
			// 准备就
			// 判断指令类型，挂上时间
			if (op == "ADD") {
				resst[whichToRun][0] = Integer.toString(time[0]-1);
			} else if (op == "SUB") {
				resst[whichToRun][0] = Integer.toString(time[1]-1);
			} else if (op == "MULT") {
				resst[whichToRun][0] = Integer.toString(time[2]-1);
			} else if (op == "DIV") {
				resst[whichToRun][0] = Integer.toString(time[3]-1);
			}// if-else
			return true;
		} else {
			return false;
		}
	}

	void instWB(String op, String rd, String rs, String rt) {
		int whichToWB = -1;
		for (int i = 1; i < 6; i++) {
			if (resst[i][3] == op) {
				whichToWB = i;
			}
		}

		String r = resst[whichToWB][1];

		for (int i = 1; i < 17; i++) {
			// 对于任何一个正在等该结果的浮点寄存器x
			if (regst[1][i].equals(r)) {
				// 向该寄存器写入结果
				if (op == "ADD") {
					regst[2][i] = resst[whichToWB][4] + "+"
							+ resst[whichToWB][5];
				} else if (op == "SUB") {
					regst[2][i] = resst[whichToWB][4] + "-"
							+ resst[whichToWB][5];
				} else if (op == "MULT") {
					regst[2][i] = resst[whichToWB][4] + "*"
							+ resst[whichToWB][5];
				} else if (op == "DIV") {
					regst[2][i] = resst[whichToWB][4] + "/"
							+ resst[whichToWB][5];
				}// if-else

				// 并把该寄存器的状态置为数据就绪
				regst[1][i] = "0";
			}// ---------------------------------------------
		}

		for (int i = 1; i < 6; i++) {// 对任何一个正在等该结果作为第一个操作数的保留站x
			if (resst[i][6].equals(r)) {
				// 向该保留站的Vj写入结果
				if (op == "ADD") {
					resst[i][4] = resst[whichToWB][4] + "+"
							+ resst[whichToWB][5];
				} else if (op == "SUB") {
					resst[i][4] = resst[whichToWB][4] + "-"
							+ resst[whichToWB][5];
				} else if (op == "MULT") {
					resst[i][4] = resst[whichToWB][4] + "/"
							+ resst[whichToWB][5];
				} else if (op == "DIV") {
					resst[i][4] = resst[whichToWB][4] + "*"
							+ resst[whichToWB][5];
				}// if-else

				// 置Qj为0，表示该保留站的Vj中操作数就绪
				resst[i][6] = "0";
			}
		}

		for (int i = 1; i < 6; i++) {// 对任何一个正在等该结果作为第二个操作数的保留站x
			if (resst[i][7].equals(r)) {
				// 向该保留站的Vk写入结果
				if (op == "ADD") {
					resst[i][5] = resst[whichToWB][4] + "+"
							+ resst[whichToWB][5];
				} else if (op == "SUB") {
					resst[i][5] = resst[whichToWB][4] + "-"
							+ resst[whichToWB][5];
				} else if (op == "MULT") {
					resst[i][5] = resst[whichToWB][4] + "/"
							+ resst[whichToWB][5];
				} else if (op == "DIV") {
					resst[i][5] = resst[whichToWB][4] + "*"
							+ resst[whichToWB][5];
				}// if-else

				// 置Qk为0，表示该保留站的Vk中操作数就绪
				resst[i][7] = "0";
			}
		}

		// 释放当前的保留站，将它置为空闲状态
		resst[whichToWB][0] = "";
		// resst[whichToWB][1] = "?";
		resst[whichToWB][2] = "no";
		resst[whichToWB][3] = "";
		resst[whichToWB][4] = "";
		resst[whichToWB][5] = "";
		resst[whichToWB][6] = "";
		resst[whichToWB][7] = "";
	}
	
	boolean startDo(String op, String rd, String rs, String rt,int i){
		int IsDoIssue = -1;
		
		if (instst[i + 1][1] == "") {
			// 若没有流出
			instIssue(op, rd, rs, rt);
			instst[i + 1][1] = "" + cnow;// 发射成功
			IsDoIssue = 1;
		}else if(instst[i + 1][2] == ""){
			if(instExcute(op, rd, rs, rt)){
				instst[i + 1][2] = "" + cnow;
			}else{
				//没有准备好，什么也不做
			}
		}else if(instst[i + 1][2].length() < 4){
			//执行中
			// 修改执行的状态
			int whichToRun = -1;
			for(int k = 1;k<6;k++){
				if(resst[k][3]==op){
					System.out.println("k: "+k);
					whichToRun = k;
				}
			}
			int remain_time = Integer.parseInt(resst[whichToRun][0]);
			if((remain_time--)>0){
				resst[whichToRun][0] = remain_time+"";
				if(remain_time == 0){
					instst[i + 1][2] = instst[i + 1][2] + "->" + cnow;
				}
			}
		} else if (instst[i + 1][3] == "") {
			instWB(op, rd, rs, rt);
			instst[i + 1][3] = "" + cnow;// 写入成功
		}
		
		if(IsDoIssue > 0){
			return true;
		}else{
			return false;
		}
	}

	public void core() {
		/*
		 * private String instst[][]=new String[7][4], resst[][]=new
		 * String[6][8], ldst[][]=new String[4][4], regst[][]=new String[3][17];
		 */
		// 思路：对指令状态数组逐行遍历，判断每条指令此时的状态；从左至右---->
		// 首先判断是否流出？若没有，则是否满足条件流出；
		// 若流出，再则查看剩余时间判断是否执行完毕，若没有，则剩余时间-1，
		// 若执行完成，再判断是否写回，若没有，则写回
		// 若已经写回了。则什么都不做。
		int IsdoSomething = -1;
		for (int i = 0; i < 6; i++) {
			String rd = "F" + (instbox[i * 4 + 1].getSelectedIndex() * 2);
			String rs = "F" + (instbox[i * 4 + 2].getSelectedIndex() * 2);
			String rt = "F" + (instbox[i * 4 + 3].getSelectedIndex() * 2);
			
			if(true){
				System.out.println("time: "+ cnow);	
			}
			switch (instbox[i * 4].getSelectedIndex()) {
			case 1:// L.D F2 , 45 (R3)
					// rt imm rs
					// k j
				if (instst[i + 1][1] == "") {
					// 若没流出
					// 则判断是否满足条件流出
					int whichRemain = -1;
					for (int j = 1; j < 4; j++) {
						if (ldst[j][1] == "no") {
							whichRemain = j;// find a remian ldst
							break;
						}
					}
					if (whichRemain >= 0) {
						ldst[whichRemain][1] = "Yes";
						ldst[whichRemain][2] = instbox[i * 4 + 2]
								.getSelectedIndex() + "";
						// 把当前缓冲器单元的编号r放入load指令的目标寄存器rt所对应的寄存器状态表项，
						// 以便rt将来接收所取得数据
						regst[1][instbox[i * 4 + 1].getSelectedIndex() + 1] = ldst[whichRemain][0];
						instst[i + 1][1] = "" + cnow;// 发射成功
						IsdoSomething = 1;
					} else {
						System.out.println("ldst is full."
								+ "there is a wrong.");
					}// if-else
				} else {
					if (instst[i + 1][2] == "") {
						// 进入的条件(RS[r].Qj ==0) and r 成为load/store和缓冲队列的头部
						// 操作和状态表内容的修改 1）RS[r].A=RS[r].Vj+RS[r].A
						// 对于load指令，在完成有效地址计算后，还要从内存Mem[RS[r].A]读取数据
						int whichline = -1;
						for (int j = 1; j < 4; j++) {
							if (ldst[j][1] == "Yes"
									&& (ldst[j][2].length() <= 2)) {// "21"的长度为2
								whichline = j;
								break;
							}
						}
						ldst[whichline][2] = "R[R"
								+ instbox[i * 4 + 3].getSelectedIndex() + "]+"
								+ instbox[i * 4 + 2].getSelectedIndex();
						instst[i + 1][2] = cnow + "";
					} else if (instst[i + 1][2].length() == 1) {
						instst[i + 1][2] = instst[i + 1][2] + "->" + cnow;
						int whichline = -1;
						for (int j = 1; j < 4; j++) {
							if (ldst[j][1] == "Yes") {
								whichline = j;
								break;
							}
						}

						ldst[whichline][3] = "M[" + ldst[whichline][2]
								+ "]";
					} else if (instst[i + 1][3] == "") {
						// 保留站r执行结束，且CDB就绪
						instst[i + 1][3] = "" + cnow;
						int whichline = -1;
						for (int j = 1; j < 4; j++) {
							if (ldst[j][1] == "Yes") {
								whichline = j;
								break;
							}
						}
						// System.out.println("whichline: "+whichline);
						// String r = "Load" + whichline;
						for (int j = 1; j < 17; j++) {
							if (regst[1][j].equals(ldst[whichline][0])) {
								// 向该寄存器写入结果
								// 把该寄存器的状态置为数据就绪
								
								regst[2][j] = ldst[whichline][3];
								regst[1][j] = "0";
							}
						}
						
						String r = ldst[whichline][0];
						// 通过CDB总线广播

						for (int j = 1; j < 17; j++) {// 对于任何一个正在等该结果的浮点寄存器x
							if (regst[1][j].equals(r)) {
								regst[2][j] = ldst[whichline][3];// 向该寄存器写入结果
								regst[1][j] = "0";// 把该寄存器的状态置为数据状态
							}
						}

						for (int j = 1; j < 6; j++) {
							// 对于任何一个正在等待该结果，作为第一操作数的保留站x
							// 向该保留站的Vj写入结果，置Qj为0，表示该保留站的Vj的操作数就绪
							if (resst[j][6].equals(r)) {
								resst[j][4] = ldst[whichline][3];
								resst[j][6] = "0";
							}
						}

						for (int j = 1; j < 6; j++) {
							// 对于任何一个正在等该结果作为第二操作数的保留站x
							// 向该保留站的Vk写入结果，置Qk为0，表示该保留站的Vk的操作数就绪
							if (resst[j][7].equals(r)) {
								resst[j][5] = ldst[whichline][3];
								resst[j][7] = "0";
							}
						}
						ldst[whichline][1] = "no";
						ldst[whichline][2] = "";
						ldst[whichline][3] = "";
					}// else-if
				}
				break;
			case 2:// ADD.D F2,F0,F1
					// rd rs rt
					// i j k
				if(startDo("ADD", rd, rs, rt, i)){
					IsdoSomething = 1;
				}
				break;
			case 3:// SUB.D
				if(startDo("SUB", rd, rs, rt, i)){
					IsdoSomething = 1;
				}
				break;
			case 4:// MULT.D
				if(startDo("MULT", rd, rs, rt, i)){
					IsdoSomething = 1;
				}
				break;
			case 5:// DIV.D
				if(startDo("DIV", rd, rs, rt, i)){
					IsdoSomething = 1;
				}
				break;

			default:
				System.out.println("NOP");
				break;
			}
			if (IsdoSomething > 0) {
				break;
			}
		}
	}

	public static void main(String[] args) {
		new Tomasulo();
	}

}

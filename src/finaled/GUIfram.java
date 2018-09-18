package finaled;

import java.awt.event.ActionListener;
import java.awt.event.KeyListener;

import javax.swing.JFrame;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.text.ParseException;
import java.util.Random;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.text.MaskFormatter;
import gnu.io.*;


import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.TooManyListenersException;
public class GUIfram extends JFrame implements KeyListener, ActionListener,SerialPortEventListener{
	private JPanel panel = new JPanel();
	private ImageIcon cute = new ImageIcon("cute.jpg");
	private ImageIcon cute1 = new ImageIcon("play.png");
	private JLabel cutes = new JLabel();
	private JLabel cute1s = new JLabel();

	JTextArea txtLog = new JTextArea(5,30);
	
	JScrollPane Scorll = new JScrollPane(txtLog);
	
	JComboBox cbox = new JComboBox();
	private Enumeration ports = null;
    private HashMap portMap = new HashMap();
    private CommPortIdentifier selectedPortIdentifier = null;//找到Avaliable 的 port ,using getPortIdentifiers
    private SerialPort serialPort = null;
    final static int TIMEOUT = 2000;
    boolean bConnected = false;
    private InputStream input = null;
    private OutputStream output = null;
    int i=0;
    int store[];
    int temper;
    
	private JLabel avaliable = new JLabel();
	JButton start,exit,connect,disconnect;
	Font font = new Font("OK", 1, 15);
	Font font1 = new Font("OK", 1, 10);
	
	String logText = "";
	String temp ="";
	
	
	
	
	public GUIfram() {
		panel = new JPanel();// 設panel
		panel.setLayout(null);
		panel.setOpaque(false);
		add(panel);
		searchForPorts();
		GroundGUI();
		
	}
	private void searchForPorts()
    {
        ports = CommPortIdentifier.getPortIdentifiers();
       
        while (ports.hasMoreElements())
        {
        	
            CommPortIdentifier curPort = (CommPortIdentifier)ports.nextElement();
            //get only serial ports
            if (curPort.getPortType() == CommPortIdentifier.PORT_SERIAL)//com的type是serial?
            {
            	
                cbox.addItem(curPort.getName());//curPort.getName()==com**//把name放到combo box java(輪軸)
                portMap.put(curPort.getName(), curPort);//如果有很多PORT 放進MAP裡面暫存
            }
        }
    }
	private void connected()
    {
        String selectedPort = (String)cbox.getSelectedItem();
        selectedPortIdentifier = (CommPortIdentifier)portMap.get(selectedPort);//取出選取COM的值
       // System.out.println(selectedPortIdentifier);

        CommPort commPort = null;

        try
        {
            
        	//回傳commPort型態
            commPort = selectedPortIdentifier.open("逗貓機", TIMEOUT);
            //System.out.println(selectedPortIdentifier.open("TigerControlPanel", TIMEOUT));
            
            //the CommPort object can be casted to a SerialPort object
            serialPort = (SerialPort)commPort;
            
            //for controlling GUI elements
            
            setConnected(true);//已連線

            logText = "逗貓機啟動囉!~";
            txtLog.setForeground(Color.black);
            txtLog.append(logText + "\n");
        }
        catch (PortInUseException e)
        {
            logText = selectedPort + " is in use. (" + e.toString() + ")";
            
           txtLog.setForeground(Color.RED);
            txtLog.append(logText + "\n");
        }
        catch (Exception e)
        {
            logText = "Failed to open " + selectedPort + "(" + e.toString() + ")";
            txtLog.append(logText + "\n");
            txtLog.setForeground(Color.RED);
        }
    }
	private void GroundGUI()
	{	
		cbox.setSelectedItem(null);
		cbox.setBounds(170,100,100,50);
		panel.add(cbox);
		cbox.setVisible(false);
		
		start = new JButton("開始!!!");
		start.setBounds(140, 225, 125, 75);
		start.setFont(font);
		panel.add(start);
		start.addActionListener(this);
		
		exit = new JButton("離開~!");
		exit.setBounds(140, 300, 125, 75);
		exit.setFont(font);
		panel.add(exit);
		exit.addActionListener(this);
		
		cute1s.setIcon(cute1);
		cute1s.setBounds(150, 300, cute1.getIconWidth(), cute1.getIconHeight());
		
		cutes.setIcon(cute);
		panel.add(cutes);
		cutes.setBounds(0, 0, cute.getIconWidth(), cute.getIconHeight());
		
	}
	private void Ground1GUI() 
	{
		panel.removeAll();
		panel.add(cbox);
		cbox.setVisible(true);
		
		txtLog.setEditable(false);
		txtLog.setLineWrap(true);
		txtLog.setFocusable(false);
		/*txtLog.setBounds(200,200,100,50);
		
		panel.add(txtLog);*/
		
		Scorll.setBounds(0,200,300,100);
		panel.add(Scorll);
		
		
		avaliable.setText("請選取要連結的port:");
		avaliable.setBounds(20, 100, 150, 50);
		//avaliable.setForeground(Color.yellow);
		avaliable.setFont(font);
		panel.add(avaliable);
		
		connect = new JButton("connect!");
		connect.setBounds(300, 60, 100, 50);
		connect.setFont(font1);
		panel.add(connect);
		connect.addActionListener(this);
		
		disconnect = new JButton("disconnect!");
		disconnect.setBounds(300, 110, 100, 50);
		disconnect.setFont(font1);
		panel.add(disconnect);
		disconnect.addActionListener(this);
		
		panel.add(cutes);
		
		panel.repaint();
		
	}
	public void initListener()
	    {
	        try
	        {
	            serialPort.addEventListener(this);
	            serialPort.notifyOnDataAvailable(true);
	        }
	        catch (TooManyListenersException e)
	        {
	            logText = "Too many listeners. (" + e.toString() + ")";
	            txtLog.setForeground(Color.red);
	            txtLog.append(logText + "\n");
	        }
	    }
	public boolean initIOStream()
    {
        //return value for whather opening the streams is successful or not
        boolean successful = false;

        try {
            //
            input = serialPort.getInputStream();
            output = serialPort.getOutputStream();
            
            successful = true;
            return successful;
        }
        catch (IOException e) {
            logText = "I/O Streams failed to open. (" + e.toString() + ")";
            txtLog.setForeground(Color.red);
            txtLog.append(logText + "\n");
            return successful;
        }
    }
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource() == start)
		{
			Ground1GUI() ;
		}
		if(e.getSource() == connect)
		{
			
			String value = (String)cbox.getSelectedItem();
			System.out.println(value);
			connect.setEnabled(false);
			connected();
			if (getConnected() == true)
	        {
	            if (initIOStream() == true)
	            {
	                initListener();
	            }
	        }
		}
		if(e.getSource() == disconnect)
		{
			connect.setEnabled(true);
			disconnected();
		}
		
	}
	final public boolean getConnected()
    {
        return bConnected;
    }

    public void setConnected(boolean bConnected)
    {
        this.bConnected = bConnected;
    }
    public void disconnected()
    {
        //close the serial port
        try
        {
            serialPort.removeEventListener();
            serialPort.close();
            input.close();
            output.close();
            setConnected(false);
            logText = "Disconnected.";
            txtLog.setForeground(Color.red);
            txtLog.append(logText + "\n");
        }
        catch (Exception e)
        {
            logText = "Failed to close " + serialPort.getName() + "(" + e.toString() + ")";
            txtLog.setForeground(Color.red);
            txtLog.append(logText + "\n");
        }
    }
	@Override
	public void keyPressed(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	public class bgm {
		public bgm(String song){
			File music=new File(song);
			Playsound(music);
		}
		public void Playsound(File sound)
		{
			try{
				Clip clip=AudioSystem.getClip();
				clip.open(AudioSystem.getAudioInputStream(sound));
				clip.start();
				
				//Thread.sleep(clip.getMicrosecondLength()/1000);

			}catch(Exception e)
			{
				
			}
		}
	}
	//@Override
	public void serialEvent(SerialPortEvent evt) {
		// TODO Auto-generated method stub
		 if (evt.getEventType() == SerialPortEvent.DATA_AVAILABLE)
	        {
	            try
	            {
	            
	                byte singleData = (byte)input.read();
	                //System.out.println(singleData);
	                //
	                if (singleData != 10)
	                {
	                    logText = new String(new byte[] {singleData});
	                    temp=logText;
	                    //System.out.println(temp);
	                    if(singleData !=13){
	                    	temper=temper+Integer.parseInt(temp);
	                    	if(i!=4)
	                    		temper=temper*10;
	                    	if(i==4){
	                    		System.out.println(temper);
	                    		if(temper>60000){
	                    			txtLog.append("現在沒有貓咪在玩喔~~><");
	         	                   txtLog.setCaretPosition(txtLog.getDocument().getLength());
	         	                   panel.remove(cute1s);
	         	                   panel.repaint();
	                    		}
	                    		else
	                    		{
	                    			txtLog.append("貓咪看起來玩得很開心呢!");
		         	                txtLog.setCaretPosition(txtLog.getDocument().getLength());
		         	                panel.remove(cutes);
		         	                panel.add(cute1s);
		         	                panel.add(cutes);
		         	                panel.repaint();
	                    			//Toolkit.getDefaultToolkit().beep();
	                    			new bgm("貓叫.wav");
	                    			
	                    		}
	                    			///Toolkit.getDefaultToolkit().notify();
	                    	}
	                    	i++;
	                    	if(i==5)
	                    	{
	                    		i=0;
	                    		temper=0;
	                    	}
	                    	
	                    }
	                   
	                }
	               // }
	                else
	                {	
	                    txtLog.append("\n");
	                }
	            }
	            catch (Exception e)
	            {
	                logText = "Failed to read data. (" + e.toString() + ")";
	                txtLog.setForeground(Color.red);
	                txtLog.append(logText + "\n");
	            }
	        }
		
	}
}

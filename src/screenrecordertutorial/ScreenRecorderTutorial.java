/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package screenrecordertutorial;

import java.util.concurrent.TimeUnit;
import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IContainer;
import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JRootPane;

/**
 *
 * @author Marcelo Aikawa
 */
public class ScreenRecorderTutorial implements Runnable {

    /**
     * @param args the command line arguments
     */
     
    JFrame frame;
    JButton button;
    Rectangle rect;
    boolean isThreadRunning; 
    
    String outputFileName = "C:\\Gravações\\recording.mp4";
                        
    public ScreenRecorderTutorial() {
       
         frame = new JFrame();  
         frame.setUndecorated(true);
         frame.getRootPane().setWindowDecorationStyle(JRootPane.FRAME);
         frame.getRootPane().setBorder(BorderFactory.createDashedBorder(Color.RED, 3 ,4 ,5 , false));
         frame.setSize(401 , 300);
         frame.setOpacity((float) 0.5);
         frame.setLocationRelativeTo(null);
         frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
         frame.getRootPane().addComponentListener(new ComponentAdapter() {
    
              public void componentResized(ComponentEvent e)
              { 
                frame.setTitle("Screen recorder (W: " + frame.getBounds().width + " H:" + frame.getBounds().height + " )");
              }
         });
         button = new JButton("Start Capture");
         button.setForeground(Color.red);
         button.addActionListener(new ActionListener() {
             @Override
             public void actionPerformed(ActionEvent e) {
                  buttonAction(e);
             }

            
         });
         frame.add(button, BorderLayout.SOUTH);
         frame.setVisible(true);
    }
    
     private void buttonAction(ActionEvent e) {
              rect =  frame.getBounds();
              if(button.getText().equalsIgnoreCase("Start Capture"))
              {
                  button.setText("Stop Capture");
                  System.out.println("Value: x:" + rect.x + " y:" + rect.y + " w:" + rect.width + " h:" + rect.height);
                  rect.width = (int) (Math.ceil(rect.width/2)*2);
                  rect.height = (int) (Math.ceil(rect.height/2)*2);
                  System.out.println("Value After: x:" + rect.x + " y:" + rect.y + " w:" + rect.width + " h:" + rect.height);
                  isThreadRunning = true;
                  Thread th = new Thread(this);
                  th.start();
                  frame.setState(JFrame.ICONIFIED);
                  
              }   
              
              else
              {
                 isThreadRunning = false; 
                 button.setText("Start Capture");
                 
              }
        
            }
      @Override
    public void run() {
        
        IMediaWriter writer = ToolFactory.makeWriter(outputFileName);
        writer.addVideoStream(0 , 0 , ICodec.ID.CODEC_ID_H264, rect.width, rect.height );
        long startTime = System.nanoTime();
        while(true)
         {
            try {
                Robot robot = new Robot();
                BufferedImage image = robot.createScreenCapture(rect);
                BufferedImage capImage;
                if(image.getType() == BufferedImage.TYPE_3BYTE_BGR)
                {
                    capImage = image;
                }
                else
                {
                    capImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
                    capImage.getGraphics().drawImage(image, 0, 0, null);   
                }                                    
                
                writer.encodeVideo(0, capImage, System.nanoTime()-startTime, TimeUnit.NANOSECONDS);

            } catch (Exception ex) {
                 System.out.println("Error: " + ex.getMessage());
              }
             if(isThreadRunning == false)
             {
                  break;   
             }
         }    
             writer.close();
             System.out.println("File Saved");
             IContainer container = IContainer.make();
             int res = container.open(outputFileName, IContainer.Type.READ, null);
             if(res < 0)
             {
                System.out.println("Cannot open file");
             }
             else
             {
                 JOptionPane.showMessageDialog(frame, "Created File" + outputFileName + "(Size: " + container.getFileSize() + " Duration: " + container.getDuration() + " Bitrate: " + container.getBitRate() + " )" );
             }
         
    }

        
    public static void main(String[] args) {
        // TODO code application logic here
       new ScreenRecorderTutorial();
  }

   
    
}

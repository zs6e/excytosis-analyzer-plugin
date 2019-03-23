package ExocytosisAnalyzer.detection;


import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.border.EmptyBorder;

public class ProgressWin {
    private JDialog jd;
    private JPanel contentPane;
    private JProgressBar Bar;
    private JLabel Status;
    
    public ProgressWin() {
        jd = new JDialog();
        jd.setTitle("Processing...");
        jd.setBounds(100, 100, 400, 100);
        jd.setVisible(true);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        jd.setContentPane(contentPane);

        final JPanel contentMessage = new JPanel();
        contentMessage.setBorder(new EmptyBorder(5, 5, 5, 5));
        Status = new JLabel("Please wait...");
        contentMessage.add(Status);
        contentPane.add(contentMessage);



        final JProgressBar pbar = new JProgressBar();
        pbar.setStringPainted(true);
        Bar = pbar;
        contentPane.add(pbar);
    }

    public void setNote(String Message) {
        Status.setText(Message);
        if (contentPane.getGraphics() == null) {
            jd.setVisible(true);
        }
        
    }

    public void setProgress(int p) {
    	Bar.setString(((Integer) p).toString() + " %");
    	Bar.setValue(p);
    }

    public void close() {
        jd.dispose();
    }
    
    
}

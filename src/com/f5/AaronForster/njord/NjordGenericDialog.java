package com.f5.AaronForster.njord;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

// Currently this code will produce a bunch of different dialogs. I need to add one that will produce the prefs dialog I am going for.

public class NjordGenericDialog extends JDialog implements ActionListener, PropertyChangeListener {
	private String logPrefix = "NjordGenericDialog: ";
	
	private String typedText = null;
    private JTextField textField;
    private MainGuiWindow mainGuiWindow;
    public JOptionPane optionPane;

    private static String btnButton1DefaultString = "Yes";
    private static String btnButton2DefaultString = "Cancel";
    private static String defaultDialogMessage = "Are you sure?";

    /**
     * Returns null if the typed string was invalid;
     * otherwise, returns the string as the user entered it.
     */
    public String getValidatedText() {
        return typedText;
    }
    
    /**
     * SUPER generic dialog will default to "Are you Sure?" "Yes" "Cancel"
     * @param aFrame
     * @param parent
     */
    public NjordGenericDialog(Frame aFrame, MainGuiWindow parent) {
        this(aFrame, defaultDialogMessage, btnButton1DefaultString, btnButton2DefaultString, parent);
    }

    
    /**
     * Actual method for creating the dialog requires text for the buttons and a dialog message
     *
     *
     */
    public NjordGenericDialog(Frame aFrame, String dialogMessage, String buttonOneText, String buttonTwoText, MainGuiWindow parent) {
        super(aFrame, true);
        mainGuiWindow = parent;

//        textField = new JTextField(10);
//        String msgString1 = "String 1";
//        String msgString2 = "String 2";
        Object[] array = {dialogMessage};

        //Create an array specifying the number of dialog buttons
        //and their text.
        Object[] options = {buttonOneText, buttonTwoText};

        //Create the JOptionPane.
        optionPane = new JOptionPane(array,
                                    JOptionPane.QUESTION_MESSAGE,
                                    JOptionPane.YES_NO_OPTION,
                                    null,
                                    options,
                                    options[0]);

        //Make this dialog display it.
        setContentPane(optionPane);
//        setContentPane(this);
        
        //Handle window closing correctly.
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
//        addWindowListener(new WindowAdapter() {
//                public void windowClosing(WindowEvent we) {
//                /*
//                 * Instead of directly closing the window,
//                 * we're going to change the JOptionPane's
//                 * value property.
//                 */
//                    optionPane.setValue(new Integer(
//                                        JOptionPane.CLOSED_OPTION));
//            }
//        });

//        //Ensure the text field always gets the first focus.
//        addComponentListener(new ComponentAdapter() {
//            public void componentShown(ComponentEvent ce) {
//                textField.requestFocusInWindow();
//            }
//        });
//
//        //Register an event handler that puts the text into the option pane.
//        textField.addActionListener(this);
//
//        //Register an event handler that reacts to option pane state changes.
//        optionPane.addPropertyChangeListener(this);
    }

    /** I believe this is what happens if you hit enter from the text field. Which should be gone now. Hrm...
     * This method handles events for the text field. 
     * */
    public void actionPerformed(ActionEvent e) {
        optionPane.setValue(btnButton1DefaultString);
    }

    /** This method reacts to state changes in the option pane. */
    public void propertyChange(PropertyChangeEvent e) {
        String prop = e.getPropertyName();

        if (isVisible()
         && (e.getSource() == optionPane)
         && (JOptionPane.VALUE_PROPERTY.equals(prop) ||
             JOptionPane.INPUT_VALUE_PROPERTY.equals(prop))) {
            Object value = optionPane.getValue();

            if (value == JOptionPane.UNINITIALIZED_VALUE) {
                //ignore reset
                return;
            }

            //Reset the JOptionPane's value.
            //If you don't do this, then if the user
            //presses the same button next time, no
            //property change event will be fired.
            optionPane.setValue(
                    JOptionPane.UNINITIALIZED_VALUE);

        }
    }

    /** This method clears the dialog and hides it. */
    public void clearAndHide() {
        textField.setText(null);
        setVisible(false);
    }
	
	
	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	private JFrame frame;
//	private JTextField ConnPreffsHostTextField;
//	private JTextField ConnPreffsPortTextField;
//	private JTextField ConnPreffsUserTextField;
//	private JTextField ConnPreffsPasswordTextField;
//
//	/**
//	 * Launch the application.
//	 */
//	public static void main(String[] args) {
//		EventQueue.invokeLater(new Runnable() {
//			public void run() {
//				try {
//					NjordGenericDialog window = new NjordGenericDialog();
//					window.frame.setVisible(true);
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		});
//	}
//
//	/**
//	 * Create the application.
//	 */
//	public NjordGenericDialog() {
//		initialize();
//	}
//
//	/**
//	 * Initialize the contents of the frame.
//	 */
//	private void initialize() {
//		frame = new JFrame();
//		frame.setBounds(100, 100, 450, 300);
//		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		frame.getContentPane().setLayout(new BorderLayout(0, 0));
//		
//		JPanel panel_1 = new JPanel();
//		frame.getContentPane().add(panel_1);
//		panel_1.setLayout(new BorderLayout(10, 10));
//		
//		Component verticalStrut = Box.createVerticalStrut(20);
//		panel_1.add(verticalStrut, BorderLayout.NORTH);
//		
//		Component horizontalStrut = Box.createHorizontalStrut(20);
//		panel_1.add(horizontalStrut, BorderLayout.WEST);
//		
//		JPanel panel = new JPanel();
//		panel_1.add(panel);
//		panel.setLayout(new FormLayout(new ColumnSpec[] {
//				FormFactory.RELATED_GAP_COLSPEC,
//				FormFactory.DEFAULT_COLSPEC,
//				FormFactory.RELATED_GAP_COLSPEC,
//				ColumnSpec.decode("default:grow"),
//				FormFactory.RELATED_GAP_COLSPEC,
//				FormFactory.DEFAULT_COLSPEC,
//				FormFactory.RELATED_GAP_COLSPEC,
//				ColumnSpec.decode("default:grow"),},
//			new RowSpec[] {
//				FormFactory.RELATED_GAP_ROWSPEC,
//				FormFactory.DEFAULT_ROWSPEC,
//				FormFactory.RELATED_GAP_ROWSPEC,
//				FormFactory.DEFAULT_ROWSPEC,
//				FormFactory.RELATED_GAP_ROWSPEC,
//				FormFactory.DEFAULT_ROWSPEC,
//				FormFactory.RELATED_GAP_ROWSPEC,
//				FormFactory.DEFAULT_ROWSPEC,
//				FormFactory.RELATED_GAP_ROWSPEC,
//				FormFactory.DEFAULT_ROWSPEC,
//				FormFactory.RELATED_GAP_ROWSPEC,
//				FormFactory.DEFAULT_ROWSPEC,
//				FormFactory.RELATED_GAP_ROWSPEC,
//				RowSpec.decode("default:grow"),
//				FormFactory.RELATED_GAP_ROWSPEC,
//				RowSpec.decode("default:grow"),}));
//		
//		//TODO: Write these four things to a preferences file
//		JLabel lblBigipAddress = new JLabel("Host:");
//		lblBigipAddress.setToolTipText("IP Address or DNS name");
//		panel.add(lblBigipAddress, "4, 4");
//		
//		ConnPreffsHostTextField = new JTextField();
//		ConnPreffsHostTextField.setText("192.168.215.251");
//		panel.add(ConnPreffsHostTextField, "8, 4, fill, default");
//		ConnPreffsHostTextField.setColumns(10);
//		
//		JLabel lblBigipConnectionPort = new JLabel("Port:");
//		lblBigipConnectionPort.setToolTipText("Likely 443");
//		panel.add(lblBigipConnectionPort, "4, 6");
//		
//		ConnPreffsPortTextField = new JTextField();
//		ConnPreffsPortTextField.setText("443");
//		panel.add(ConnPreffsPortTextField, "8, 6, fill, default");
//		ConnPreffsPortTextField.setColumns(10);
//		
//		JLabel lblUnsername = new JLabel("Unsername:");
//		lblUnsername.setToolTipText("A user with Administrative access to the BigIP System");
//		panel.add(lblUnsername, "4, 8");
//		
//		ConnPreffsUserTextField = new JTextField();
//		ConnPreffsUserTextField.setText("admin");
//		panel.add(ConnPreffsUserTextField, "8, 8, fill, default");
//		ConnPreffsUserTextField.setColumns(10);
//		
//		//TODO: Make this contain asterisks
//		//TODO: Potentially add a shoulder surfing replace display text as you type with asterisks.
//		//TODO: Make sure not to write asterisks to the password variable stored in preferences
//		JLabel lblPassword = new JLabel("Password");
//		lblPassword.setToolTipText("The password for the above account");
//		panel.add(lblPassword, "4, 10");
//		
//		ConnPreffsPasswordTextField = new JTextField();
//		ConnPreffsPasswordTextField.setText("G!mmef5");
//		panel.add(ConnPreffsPasswordTextField, "8, 10, fill, default");
//		ConnPreffsPasswordTextField.setColumns(10);
//		
//		JPanel ConnPreffsSaveCancelButtonPanel = new JPanel();
//		panel.add(ConnPreffsSaveCancelButtonPanel, "4, 14, 5, 1, fill, fill");
//		
//		JButton ConnPreffsSaveButton = new JButton("Save");
//		ConnPreffsSaveCancelButtonPanel.add(ConnPreffsSaveButton);
//		
//		Component horizontalStrut_2 = Box.createHorizontalStrut(10);
//		ConnPreffsSaveCancelButtonPanel.add(horizontalStrut_2);
//		
//		JButton ConnPreffsCancelButton = new JButton("Cancel");
//		ConnPreffsSaveCancelButtonPanel.add(ConnPreffsCancelButton);
//		
//		JPanel SomeBogusPanel = new JPanel();
//		panel.add(SomeBogusPanel, "4, 16, fill, fill");
//		
//		Component horizontalStrut_1 = Box.createHorizontalStrut(20);
//		panel_1.add(horizontalStrut_1, BorderLayout.EAST);
//		
//		Component verticalStrut_1 = Box.createVerticalStrut(20);
//		panel_1.add(verticalStrut_1, BorderLayout.SOUTH);
//	}

}

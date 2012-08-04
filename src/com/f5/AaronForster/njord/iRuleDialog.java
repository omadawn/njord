package com.f5.AaronForster.njord;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Box;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

// Currently this code will produce a bunch of different dialogs. I need to add one that will produce the prefs dialog I am going for.

public class iRuleDialog extends JDialog implements ActionListener, PropertyChangeListener {
	private String typedText = null;
    private JTextField textField1;
    private JTextField textField2;
    private MainGuiWindow mainGuiWindow;

    private String magicWord;
    private JOptionPane optionPane;

    private String btnString1 = "Create";
    private String btnString2 = "Cancel";

	public JFrame frame;
	public JTextField txtNewiRuleName;
	public JTextField txtNewiRulePartition;
	public JPanel panel_1;
    
    //TODO: Get rid of this method.
    /**
     * Returns null if the typed string was invalid;
     * otherwise, returns the string as the user entered it.
     */
    public String getValidatedText() {
        return typedText;
    }

    /** Creates the reusable dialog. */
    public iRuleDialog(Frame aFrame, MainGuiWindow parent) {
        super(aFrame, true);
        mainGuiWindow = parent;

        setTitle("New iRule");

        textField1 = new JTextField(10);
        textField2 = new JTextField(10);

        //Create an array of the text and components to be displayed.
        String msgString1 = "What was Dr. SEUSS's real last name?";
        String msgString2 = "(The answer is \"" + magicWord
        	      + "\".)";
        String msgString3 = "Some more text";
        String msgString4 = "nuthin";
        
        Object[] array1 = {msgString1, msgString2, textField1};
        Object[] array2 = {msgString2, msgString3, textField2};

        Object[] options = {btnString1, btnString2};
   
        JTextField xField = new JTextField(5);
        JTextField yField = new JTextField(5);

        frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout(0, 0));
		
		panel_1 = new JPanel();
		frame.getContentPane().add(panel_1);
		panel_1.setLayout(new BorderLayout(10, 10));
		
		Component verticalStrut = Box.createVerticalStrut(20);
		panel_1.add(verticalStrut, BorderLayout.NORTH);
		
		Component horizontalStrut = Box.createHorizontalStrut(20);
		panel_1.add(horizontalStrut, BorderLayout.WEST);
		
		JPanel panel = new JPanel();
		panel_1.add(panel);
		panel.setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),},
				new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"),}));
		
		JLabel lblNewiRule = new JLabel("New iRule");
		panel.add(lblNewiRule, "4, 4, 5, 1");
		
		JLabel lblRuleName = new JLabel("New iRule Name");
		lblRuleName.setToolTipText("The short name of the iRule");
		panel.add(lblRuleName, "4, 8");
		
		txtNewiRuleName = new JTextField();
		txtNewiRuleName.setText("new_rule");
		panel.add(txtNewiRuleName, "8, 8, fill, default");
		txtNewiRuleName.setColumns(10);
		
		//TODO: Make this contain asterisks
		//TODO: Potentially add a shoulder surfing replace display text as you type with asterisks.
		//TODO: Make sure not to write asterisks to the password variable stored in preferences
		JLabel lblPartition = new JLabel("Partition");
		lblPartition.setToolTipText("The administrative partition  to place in. We assume /Common");
		panel.add(lblPartition, "4, 10");
		
		txtNewiRulePartition = new JTextField();
		txtNewiRulePartition.setText("/Common");
		panel.add(txtNewiRulePartition, "8, 10, fill, default");
		txtNewiRulePartition.setColumns(10);
		
		Component horizontalStrut_1 = Box.createHorizontalStrut(20);
		panel_1.add(horizontalStrut_1, BorderLayout.EAST);
		
		Component verticalStrut_1 = Box.createVerticalStrut(20);
		panel_1.add(verticalStrut_1, BorderLayout.SOUTH);
        
        optionPane = new JOptionPane(array1,
                                    JOptionPane.QUESTION_MESSAGE,
                                    JOptionPane.YES_NO_OPTION,
                                    null,
                                    options,
                                    options[0]);

        //Make this dialog display it.
        setContentPane(optionPane); // Testing the real quick multi option one
        
        //Handle window closing correctly.
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent we) {
                /*
                 * Instead of directly closing the window,
                 * we're going to change the JOptionPane's
                 * value property.
                 */
                    optionPane.setValue(new Integer(
                                        JOptionPane.CLOSED_OPTION));
            }
        });

        //Ensure the text field always gets the first focus.
        addComponentListener(new ComponentAdapter() {
            public void componentShown(ComponentEvent ce) {
                textField1.requestFocusInWindow();
            }
        });

        //Register an event handler that puts the text into the option pane.
        textField1.addActionListener(this);

        //Register an event handler that reacts to option pane state changes.
        optionPane.addPropertyChangeListener(this);
    }

    /** This method handles events for the text field. */
    public void actionPerformed(ActionEvent e) {
        optionPane.setValue(btnString1);
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

            if (btnString1.equals(value)) {
                    typedText = textField1.getText();
                String ucText = typedText.toUpperCase();
                if (magicWord.equals(ucText)) {
                    //we're done; clear and dismiss the dialog
                    clearAndHide();
                } else {
                    //text was invalid
                    textField1.selectAll();
                    JOptionPane.showMessageDialog(
                                    iRuleDialog.this,
                                    "Sorry, \"" + typedText + "\" "
                                    + "isn't a valid response.\n"
                                    + "Please enter "
                                    + magicWord + ".",
                                    "Try again",
                                    JOptionPane.ERROR_MESSAGE);
                    typedText = null;
                    textField1.requestFocusInWindow();
                }
            } else { //user closed dialog or clicked cancel
                mainGuiWindow.setLabel("It's OK.");
                typedText = null;
                clearAndHide();
            }
        }
    }

    /** This method clears the dialog and hides it. */
    public void clearAndHide() {
        textField1.setText(null);
        setVisible(false);
    }
}

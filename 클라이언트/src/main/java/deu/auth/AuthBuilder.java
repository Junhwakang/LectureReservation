package deu.auth;

import deu.view.custom.ButtonRound;
import deu.view.custom.PasswordFieldRound;
import deu.view.custom.TextFieldRound;
import javax.swing.*;
import java.awt.Color;
import java.awt.FlowLayout;

/**
 * Builder pattern implementation for authentication components
 */
public class AuthBuilder {
    private JPanel panel;
    private TextFieldRound idField;
    private PasswordFieldRound passwordField;
    private ButtonRound actionButton;
    private JLabel titleLabel;
    private JLabel descriptionLabel;
    private JLabel logoLabel;
    private JLabel switchLabel;
    private JButton switchButton;
    private String title;
    private String description;
    private String buttonText;
    private String switchText;
    private String switchButtonText;

    public AuthBuilder() {
        // Default values
        this.title = "Welcome Back";
        this.description = "Please sign in to continue";
        this.buttonText = "Login";
        this.switchText = "Don't have an account?";
        this.switchButtonText = "Sign up";
    }

    public AuthBuilder withTitle(String title) {
        this.title = title;
        return this;
    }

    public AuthBuilder withDescription(String description) {
        this.description = description;
        return this;
    }

    public AuthBuilder withButtonText(String buttonText) {
        this.buttonText = buttonText;
        return this;
    }

    public AuthBuilder withSwitchText(String switchText, String switchButtonText) {
        this.switchText = switchText;
        this.switchButtonText = switchButtonText;
        return this;
    }

    public AuthBuilder buildPanel() {
        // Create and configure the main panel
        panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(Color.WHITE);
        panel.setPreferredSize(new java.awt.Dimension(400, 600));

        // Initialize and add components
        titleLabel = new JLabel(title);
        titleLabel.setFont(new java.awt.Font("맑은 고딕", 1, 24));
        titleLabel.setBounds(30, 250, 340, 40);
        
        descriptionLabel = new JLabel(description);
        descriptionLabel.setFont(new java.awt.Font("맑은 고딕", 0, 14));
        descriptionLabel.setForeground(new java.awt.Color(102, 102, 102));
        descriptionLabel.setBounds(30, 290, 340, 20);

        idField = new TextFieldRound();
        idField.setBounds(30, 340, 340, 40);
        idField.setPlaceholder("아이디");

        passwordField = new PasswordFieldRound();
        passwordField.setBounds(30, 390, 340, 40);
        passwordField.setPlaceholder("비밀번호");

        actionButton = new ButtonRound();
        actionButton.setText(buttonText);
        actionButton.setBounds(30, 450, 340, 45);
        actionButton.setBackground(new java.awt.Color(0, 102, 204));
        actionButton.setForeground(Color.WHITE);

        JPanel switchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        switchPanel.setBounds(0, 510, 400, 30);
        switchPanel.setOpaque(false);
        
        switchLabel = new JLabel(switchText);
        switchButton = new JButton(switchButtonText);
        switchButton.setBorderPainted(false);
        switchButton.setContentAreaFilled(false);
        switchButton.setForeground(new java.awt.Color(0, 102, 204));
        switchButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        
        switchPanel.add(switchLabel);
        switchPanel.add(switchButton);

        // Add components to panel
        panel.add(titleLabel);
        panel.add(descriptionLabel);
        panel.add(idField);
        panel.add(passwordField);
        panel.add(actionButton);
        panel.add(switchPanel);

        return this;
    }

    // Getters for components
    public JPanel getPanel() {
        return panel;
    }

    public TextFieldRound getIdField() {
        return idField;
    }

    public PasswordFieldRound getPasswordField() {
        return passwordField;
    }

    public ButtonRound getActionButton() {
        return actionButton;
    }

    public JButton getSwitchButton() {
        return switchButton;
    }
}

package com.soulsspeedruns.organizer.games.config;


import java.awt.Dialog;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

import com.github.weisj.darklaf.ui.button.DarkButtonUI;
import com.soulsspeedruns.organizer.data.OrganizerManager;
import com.soulsspeedruns.organizer.games.Game;

import jiconfont.icons.FontAwesome;
import jiconfont.swing.IconFontSwing;


public class GameCreationWindow extends JDialog
{

	private final ScrollableGamesConfigPane pane;
	private final Game game;

	private JTextField gameNameField;
	private JTextField saveNameField;


	public GameCreationWindow(ScrollableGamesConfigPane pane, Game game)
	{
		super(SwingUtilities.windowForComponent(pane), game == null ? "Create Custom Game" : "Edit Custom Game",
				Dialog.ModalityType.APPLICATION_MODAL);

		this.pane = pane;
		this.game = game;

		initLayout();
		initProperties();

		setVisible(true);
	}


	/**
	 * Sets the properties of the window.
	 */
	private void initProperties()
	{
		pack();
		setResizable(false);
		setLocationRelativeTo(SwingUtilities.windowForComponent(pane));
		setIconImage(OrganizerManager.soulsspeedrunsIcon);
		setAlwaysOnTop(OrganizerManager.isAlwaysOnTop());

		addWindowListener(new WindowAdapter()
		{

			@Override
			public void windowActivated(WindowEvent e)
			{
				SwingUtilities.invokeLater(() -> {
					SwingUtilities.updateComponentTreeUI(GameCreationWindow.this);
				});
			}
		});
	}


	private void initLayout()
	{
		JPanel guiPanel = new JPanel();
		guiPanel.setLayout(new BoxLayout(guiPanel, BoxLayout.PAGE_AXIS));

		guiPanel.add(createSettingsPanel());
		guiPanel.add(createButtonPanel());

		guiPanel.addMouseListener(new MouseAdapter()
		{

			@Override
			public void mouseClicked(MouseEvent e)
			{
				requestFocusInWindow();
			}
		});

		add(guiPanel);
	}


	private JPanel createSettingsPanel()
	{
		JPanel settingsPanel = new JPanel();
		GroupLayout layout = new GroupLayout(settingsPanel);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);

		JLabel gameNameLabel = new JLabel("Name of your custom game:");
		JLabel saveNameLabel = new JLabel("Name of the savefile:");

		gameNameField = createTextField(game == null ? "" : game.getCaption());
		saveNameField = createTextField(game == null ? "" : game.getSaveName());

		JButton gameNameHelpButton = createHelpButton("Enter the name of the game you would like to add.");
		JButton saveNameHelpButton = createHelpButton(
				"<html>Enter the name of the game's savefile. <br> For Dark Souls this is e.g. 'DRAKS0005.sl2'. Not case-sensitive. <br> Games that use two or more files to store their save data are not supported.</html>");

		GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();

		hGroup.addGroup(layout.createParallelGroup().addComponent(gameNameLabel)
				.addComponent(gameNameField, GroupLayout.PREFERRED_SIZE, 250, GroupLayout.PREFERRED_SIZE).addComponent(saveNameLabel)
				.addComponent(saveNameField, GroupLayout.PREFERRED_SIZE, 250, GroupLayout.PREFERRED_SIZE));
		hGroup.addGroup(layout.createParallelGroup().addComponent(gameNameHelpButton).addComponent(saveNameHelpButton));

		GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();

		vGroup.addGroup(layout.createParallelGroup(Alignment.CENTER).addComponent(gameNameLabel));
		vGroup.addGroup(layout.createParallelGroup(Alignment.CENTER)
				.addComponent(gameNameField, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE).addComponent(gameNameHelpButton));
		vGroup.addGroup(layout.createParallelGroup(Alignment.CENTER).addComponent(saveNameLabel));
		vGroup.addGroup(layout.createParallelGroup(Alignment.CENTER)
				.addComponent(saveNameField, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE).addComponent(saveNameHelpButton));

		layout.setHorizontalGroup(hGroup);
		layout.setVerticalGroup(vGroup);

		settingsPanel.setLayout(layout);
		return settingsPanel;
	}


	private JTextField createTextField(String text)
	{
		JTextField textField = new JTextField(text);

		textField.addKeyListener(new KeyListener()
		{

			@Override
			public void keyTyped(KeyEvent e)
			{
			}


			@Override
			public void keyReleased(KeyEvent e)
			{
				if (e.getKeyCode() == KeyEvent.VK_ENTER)
					createOrUpdateGame();
			}


			@Override
			public void keyPressed(KeyEvent e)
			{
			}
		});

		AbstractDocument document = (AbstractDocument) textField.getDocument();
		document.setDocumentFilter(new DocumentFilter()
		{

			private int limit = 70;


			@Override
			public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException
			{
				int currentLength = fb.getDocument().getLength();
				int overLimit = (currentLength + text.length()) - limit - length;
				if (overLimit > 0)
				{
					text = text.substring(0, text.length() - overLimit);
				}
				if (text.length() > 0)
				{
					super.replace(fb, offset, length, text, attrs);
				}
			}
		});

		return textField;
	}


	private JButton createHelpButton(String tooltip)
	{
		JButton helpButton = new JButton(IconFontSwing.buildIcon(FontAwesome.QUESTION, 20, UIManager.getColor("gameConfigButtonColors")));
		helpButton.putClientProperty(DarkButtonUI.KEY_SQUARE, false);
		helpButton.putClientProperty(DarkButtonUI.KEY_ROUND, true);
		helpButton.putClientProperty(DarkButtonUI.KEY_NO_BORDERLESS_OVERWRITE, false);
		helpButton.setFocusable(false);

		helpButton.setToolTipText(tooltip);

		return helpButton;
	}


	private JPanel createButtonPanel()
	{
		JButton createButton = new JButton(game == null ? "Create" : "Update");
		createButton.addActionListener(e -> {
			createOrUpdateGame();
		});

		JPanel buttonPanel = new JPanel();
		buttonPanel.add(createButton);
		return buttonPanel;
	}


	private void createOrUpdateGame()
	{
		String gameName = gameNameField.getText();
		String saveName = saveNameField.getText();
		if (gameName.length() <= 0 || saveName.length() <= 0)
		{
			JOptionPane.showMessageDialog(GameCreationWindow.this, "Please enter a game name and a savefile name!", "Invalid Input",
					JOptionPane.WARNING_MESSAGE);
			return;
		}

		if (game == null)
			pane.addEntry(gameNameField.getText(), saveNameField.getText());
		else
			pane.updateEntry(gameNameField.getText(), saveNameField.getText());
		dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
	}
}

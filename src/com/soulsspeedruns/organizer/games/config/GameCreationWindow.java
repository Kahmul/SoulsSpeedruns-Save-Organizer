package com.soulsspeedruns.organizer.games.config;


import java.awt.Dialog;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

import com.github.weisj.darklaf.ui.button.DarkButtonUI;
import com.soulsspeedruns.organizer.games.Game;
import com.soulsspeedruns.organizer.managers.IconsAndFontsManager;
import com.soulsspeedruns.organizer.managers.SettingsManager;

import jiconfont.icons.FontAwesome;
import jiconfont.swing.IconFontSwing;


public class GameCreationWindow extends JDialog
{

	private GameList list;
	private final Game game;

	private JTextField gameNameField;
	private JTextField saveNameField;


	public GameCreationWindow(GameList list, Game game)
	{
		super(SwingUtilities.windowForComponent(list), game == null ? "Create Custom Game" : "Edit Custom Game",
				Dialog.ModalityType.APPLICATION_MODAL);

		this.list = list;
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
		setLocationRelativeTo(SwingUtilities.windowForComponent(list));
		setIconImage(IconsAndFontsManager.getSoulsSpeedrunsImage(IconsAndFontsManager.ICON_SIZE_MEDIUM));
		setAlwaysOnTop(SettingsManager.isAlwaysOnTop());

		addWindowListener(new WindowAdapter()
		{

			@Override
			public void windowActivated(WindowEvent e)
			{
				requestFocusInWindow();
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

		JLabel explanationLabel = new JLabel("<html>Here you can add support for other games to the organizer.<br>"
				+ "Please enter the name of the game you would like to add as well as<br>" + "the file name of the savefile the game uses.<html>");

		JLabel gameNameLabel = createHeaderLabel("Name of the game:");
		JLabel saveNameLabel = createHeaderLabel("Name of the game's savefile:");

		gameNameField = createTextField(game == null ? "" : game.getCaption());
		saveNameField = createTextField(game == null ? "" : game.getSaveName());

		layout.linkSize(SwingConstants.HORIZONTAL, explanationLabel, gameNameField);
		layout.linkSize(SwingConstants.HORIZONTAL, explanationLabel, saveNameField);

		JButton gameNameHelpButton = createHelpButton("Enter the name of the game you would like to add.");
		JButton saveNameHelpButton = createHelpButton(
				"<html>Enter the name of the game's savefile. <br> For Dark Souls this is e.g. 'DRAKS0005.sl2'. Not case-sensitive. <br> Games that use two or more files to store their save data are not supported.</html>");

		GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();

		hGroup.addGroup(layout.createParallelGroup().addComponent(explanationLabel).addComponent(gameNameLabel)
				.addComponent(gameNameField, GroupLayout.PREFERRED_SIZE, 250, GroupLayout.PREFERRED_SIZE).addComponent(saveNameLabel)
				.addComponent(saveNameField, GroupLayout.PREFERRED_SIZE, 250, GroupLayout.PREFERRED_SIZE));
		hGroup.addGroup(layout.createParallelGroup().addComponent(gameNameHelpButton).addComponent(saveNameHelpButton));

		GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();

		vGroup.addGroup(layout.createParallelGroup(Alignment.CENTER).addComponent(explanationLabel));
		vGroup.addGap(20);
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


	private JLabel createHeaderLabel(String text)
	{
		JLabel label = new JLabel(text);
		label.addPropertyChangeListener(new PropertyChangeListener()
		{

			@Override
			public void propertyChange(PropertyChangeEvent evt)
			{
				label.setFont(label.getFont().deriveFont(Font.BOLD));
			}
		});

		return label;
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
		String gameName = gameNameField.getText().trim();
		String saveName = saveNameField.getText().trim();
		if (gameName.length() <= 0 || saveName.length() <= 0)
		{
			JOptionPane.showMessageDialog(GameCreationWindow.this, "Please enter a game name and a savefile name!", "Invalid Input",
					JOptionPane.WARNING_MESSAGE);
			return;
		}

		for (Game game : Game.GAMES)
		{
			if (game.getCaption().equalsIgnoreCase(gameName) && !game.equals(this.game))
			{
				JOptionPane.showMessageDialog(GameCreationWindow.this, "A name with the given name already exists!", "Name Already Exists",
						JOptionPane.WARNING_MESSAGE);
				return;
			}
		}

		if (game == null)
			list.addEntry(gameNameField.getText(), saveNameField.getText());
		else
			list.updateEntry(gameNameField.getText(), saveNameField.getText());
		dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
	}
}

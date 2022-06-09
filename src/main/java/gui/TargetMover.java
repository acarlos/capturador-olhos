package gui;

import javax.swing.*;
import java.awt.*;

public class TargetMover extends JFrame {
	/**
	 * SERIAL.
	 */
	private static final long serialVersionUID = -2917253903171888567L;
	private MoverPanel movPanel;
	private JLabel letraEscolhida;
	private JLabel palavraFormada;

	public TargetMover(int winWidth, int winHeight) {
		super("Target Mover");

		letraEscolhida = new JLabel("Letra: ");
		letraEscolhida.setVisible(true);
		letraEscolhida.setFont(new Font("Arial", Font.BOLD, 26));
		palavraFormada = new JLabel("Letra: ");
		palavraFormada.setVisible(true);
		palavraFormada.setFont(new Font("Arial", Font.BOLD, 16));
		Container c = getContentPane();
		movPanel = new MoverPanel(winWidth, winHeight);
		c.add(movPanel);
		movPanel.add(letraEscolhida);
		movPanel.add(palavraFormada);

		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		pack();
		setResizable(false);
		setVisible(true);
	}

	public void setTarget(double x, double y) {
		movPanel.setTarget(x, y);
	}

	public void setTargetInt(int x, int y) {
		movPanel.setTargetInt(x, y);
	}

	public void setLetra(String letra) {
		this.letraEscolhida.setText(letra);
	}

	public void setPalavraFormada(String palavraFormada) {
		this.palavraFormada.setText(palavraFormada);
	}
}
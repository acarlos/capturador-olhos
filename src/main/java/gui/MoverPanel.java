package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import javax.swing.JPanel;

public class MoverPanel extends JPanel {

	/**
	 * SERIAL.
	 */
	private static final long serialVersionUID = 2493383146805218905L;

	private static final String TARGET_FNM = "/crosshairs.png";

	private int xCenter, yCenter; // posicao do centro da imagem de alvo

	private BufferedImage targetIm;

	private int pWidth, pHeight, imWidth, imHeight;
	// dimensoes do panel (p) e da imagem de alvo (im)

	public MoverPanel(int w, int h) {
		this.pWidth = w;
		this.pHeight = h;
		this.setPreferredSize(new Dimension(this.pWidth, this.pHeight));

		// inicializar imagem alvo
		this.targetIm = this.loadImage(MoverPanel.TARGET_FNM);
		if (this.targetIm == null) {
			System.exit(1);
		}
		this.imWidth = this.targetIm.getWidth();
		this.imHeight = this.targetIm.getHeight();

		this.xCenter = this.pWidth / 2;
		this.yCenter = this.pHeight / 2;
	} 

	private BufferedImage loadImage(String fnm) {
		BufferedImage image = null;
		try {
			image = ImageIO.read(new File(fnm));
		} catch (IOException e) {
			System.out.println("Unable to load " + fnm);
		}
		return image;
	} 

	@Override
	public void paintComponent(Graphics g){

		super.paintComponent(g);
		g.setColor(Color.ORANGE);
		g.fillRect(0, 0, 100, pHeight);
        g.setColor(Color.GREEN);
        g.fillRect(((pWidth/2) - 100), 0, ((pWidth/2) - 100), (pHeight/2) - 50);
        g.setColor(Color.CYAN);
        g.fillRect((pWidth - 100), 0, 100, pHeight);
		g.drawImage(this.targetIm, this.xCenter - (this.imWidth / 2), this.yCenter - (this.imHeight / 2), null);
	} 

	public void setTarget(double x, double y){

		this.xCenter = (int) Math.round(x * this.pWidth);
		this.yCenter = (int) Math.round(y * this.pHeight);

		// Mantem o alvo visivel na tela
		if (this.xCenter < 0) {
			this.xCenter = 0;
		} else if (this.xCenter >= this.pWidth) {
			this.xCenter = this.pWidth - 1;
		}

		// inverte xCenter logo esq-do-centro <--> direita-do-centro
		this.xCenter = this.pWidth - this.xCenter;

		if (this.yCenter < 0) {
			this.yCenter = 0;
		} else if (this.yCenter >= this.pHeight) {
			this.yCenter = this.pHeight - 1;
		}
		
		this.repaint();
	}
	
	public void setTargetInt(int x, int y){

		this.xCenter = x;
		this.yCenter = y;

		if (this.xCenter < 0) {
			this.xCenter = 0;
		} else if (this.xCenter >= this.pWidth) {
			this.xCenter = this.pWidth - 1;
		}

		this.xCenter = this.pWidth - this.xCenter;

		if (this.yCenter < 0) {
			this.yCenter = 0;
		} else if (this.yCenter >= this.pHeight) {
			this.yCenter = this.pHeight - 1;
		}

		this.repaint();
	}

}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// Author: Antonio Carlos de Lima Mendes Junior
//
// Youtube Cahnnel : https://www.youtube.com/channel/UCevummVI0uUiflvEiCaUkDw
// E-mail: acarlos.mendesjr@gmail.com
//
//                   Real time eye tracking using OpenCV with Java
//
////////////////////////////////////////////////////////////////////////////////////////////////////////////////

package gui;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;

import javax.imageio.ImageIO;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

/**
 *
 * @author Antonio Carlos de Lima Mendes Junior
 */
public class CapturadorOlhos extends javax.swing.JFrame {

	/**
	 * SERIAL.
	 */
	private static final long serialVersionUID = 1353469977315914234L;

	// dimensoes da janela
	private static final int TARGET_WIDTH = 800;

	private static final int TARGET_HEIGHT = 600;
	///

	private DaemonThread myThread = null;

	int count = 0;

	int fotoContagem = 0;

	VideoCapture webSource = null;

	Mat frame = new Mat();

	MatOfByte mem = new MatOfByte();

	MatOfByte memOlhos = new MatOfByte();

	Point centerLeftEye = null;

	CascadeClassifier faceDetector = new CascadeClassifier("/home/acarlos/haarcascade_frontalface_alt.xml");

	CascadeClassifier olhosDetector = new CascadeClassifier("/home/acarlos/haarcascade_eye_tree_eyeglasses.xml");

	MatOfRect faceDetections = new MatOfRect();
	
	class DaemonThread implements Runnable {

		private final Size MAX_SIZE_CONTORNO_FACE = new Size(400, 400);

		private final Size MIN_SIZE_CONTORNO_FACE = new Size(200, 200);
		
		private final Size MAX_SIZE_CONTORNO_OLHOS = new Size(100, 100);

		private final Size MIN_SIZE_CONTORNO_OLHOS = new Size(40, 40);

		private final Scalar COLOR_CONTORNO_OLHO = new Scalar(0, 255, 255);

		private final Scalar COLOR_EYE_BORDER = new Scalar(0, 0, 255);

		private final Scalar COLOR_EYE_CENTER = new Scalar(0, 255, 0);

		protected volatile boolean runnable = false;

		TargetMover targetFrame = new TargetMover(CapturadorOlhos.TARGET_WIDTH, CapturadorOlhos.TARGET_HEIGHT);

		int contadorOlhosMovimento = 0;

		int xInEye[] = new int[5];

		int yInEye[] = new int[5];
		
		String letraEscolhida = "";
		
		String palavraFormada = "";
		
		String alfabeto[] = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "_"};
		
		int posicaoX = CapturadorOlhos.TARGET_WIDTH/2;
		
		int posicaoY = CapturadorOlhos.TARGET_HEIGHT/2;
		
		int contador = 0;
			
		int ultimaPosicao = 0;
		
		boolean bateuLetra = false;
		Mat gray = new Mat();
		Mat edges = new Mat();
		Mat circles = new Mat();
		public void run() {
			Graphics g = jPanel1.getGraphics();			
			synchronized (this) {
				while (this.runnable) {
					if (webSource.grab()) {
						try {
							webSource.retrieve(frame);
							faceDetector.detectMultiScale(frame, faceDetections, 1.1, 1, 0, MIN_SIZE_CONTORNO_FACE, MAX_SIZE_CONTORNO_FACE);
							Rect[] faces = faceDetections.toArray();
							Mat faceROI = null;
							MatOfRect eyes = new MatOfRect();
							for (Rect face : faces) {
							
								Imgproc.rectangle(frame, new Point(face.x, face.y), new Point(face.x + face.width, face.y + face.height), COLOR_CONTORNO_OLHO);
								// Procurar por olhos do rosto detectado
								faceROI = frame.submat(face);
								olhosDetector.detectMultiScale(faceROI, eyes, 1.1, 1, 0, MIN_SIZE_CONTORNO_OLHOS, MAX_SIZE_CONTORNO_OLHOS);
								Rect[] eyesArray = eyes.toArray();
								for (int i = 0; i < eyesArray.length; i++) {
									for (int j = i + 1; j < eyesArray.length; j++) {
										Rect leftEye = eyesArray[i];
										Rect rightEye = eyesArray[j];
										if ((leftEye.x + (leftEye.x + leftEye.width)) > (rightEye.x + (rightEye.x + rightEye.width))) {
											Rect newLeftEye = rightEye;
											Rect newRightEye = leftEye;
											leftEye = newLeftEye.clone();
											rightEye = newRightEye.clone();
										}
										
										Mat pupilaEsquerda = this.getIris(faceROI.submat(leftEye), gray, edges, circles);
										//this.drawLeftEyeSquare(face, leftEye, faceROI);
										if ((null != pupilaEsquerda) && (pupilaEsquerda.rows() > 0)) {
											if (this.contadorOlhosMovimento == 5) {
												this.contadorOlhosMovimento = 0;
											}
											this.contadorOlhosMovimento++;
											//int x = leftEye.x;
											//int y = leftEye.y;
											//pupilaEsquerda.submat(new Rect(0, 0, leftEye.width, leftEye.height)).copyTo(frame
											//				.submat(new Rect((face.x + x), (face.y + y), leftEye.width, leftEye.height)));
											// pega media dos olhos e retangulo das pupilas
											Rect eyeRect = leftEye;
											Rect pupilRect = new Rect();
											double values[] = { centerLeftEye.x, centerLeftEye.y };
											pupilRect.set(values);
											if (pupilRect != null) {
												// calcula a distancia da pupila do centro do retangulo do olho (cartesiano)
												int xDist = (pupilRect.x + (pupilRect.width / 2)) - (eyeRect.width / 2);
												int yDist = (pupilRect.y + (pupilRect.height / 2)) - (eyeRect.height / 2);
												
												this.xInEye[this.contadorOlhosMovimento -1] = xDist;
												this.yInEye[this.contadorOlhosMovimento -1] = yDist;

												if (this.contadorOlhosMovimento == 4) {
													int sumX = 0;
													for (int d : this.xInEye) {
														sumX += d;
													}
													int averageX = sumX / this.xInEye.length;

													int sumY = 0;
													for (double e : this.yInEye) {
														sumY += e;
													}
													double averageY = sumY / this.yInEye.length;
													
													if (averageX < -1) {
														posicaoX = (posicaoX - 80);
														if (posicaoX > TARGET_WIDTH) {
															posicaoX = TARGET_WIDTH;
														}
													} else if (averageX > 3) {
														posicaoX = (posicaoX + 60);
														if (posicaoX < 0) {
															posicaoX = 0;
														}
													} else if (averageX > 6) {
														posicaoX = (posicaoX + 100);
														if (posicaoX < 0) {
															posicaoX = 0;
														}
													}
													if (averageY < 0) {
														//posicaoY = (posicaoY - 10);
													} else if (yDist > 0) {
														//posicaoY = (posicaoY + 10);
													}
													this.targetFrame.setTargetInt( posicaoX, posicaoY);
													//System.out.println("avX " + averageX);
													//System.out.println("avY " + averageY);

													if (ultimaPosicao > alfabeto.length){
														ultimaPosicao = 0;
													}
													if (posicaoX >= CapturadorOlhos.TARGET_WIDTH){
														if (ultimaPosicao == alfabeto.length) {
															ultimaPosicao = 0;
														}
														letraEscolhida = alfabeto[ultimaPosicao];
														ultimaPosicao++ ;
														posicaoX = posicaoX - 200;
														bateuLetra = true;
													} 
													if (posicaoX <= 0){
														if (palavraFormada.length() == 1) {
															palavraFormada = "";
														} else if (palavraFormada.length() > 1) {
															palavraFormada = palavraFormada.substring(0, palavraFormada.length()-1);
														}
														posicaoX = posicaoX + 200;
													}
													if (
															(averageY < -2) && 
															((posicaoX <= ((CapturadorOlhos.TARGET_WIDTH/2) + 100)) && (posicaoX  >= ((CapturadorOlhos.TARGET_WIDTH/2) - 100)))
													) {
														if (bateuLetra) {
															palavraFormada += letraEscolhida;
															bateuLetra = false;
														}
													}
													this.targetFrame.setLetra(letraEscolhida);
													this.targetFrame.setPalavraFormada(palavraFormada);
													this.contadorOlhosMovimento = 0;
												}
											}
										}
										//olhoEsq = pupila;

										//olhoEsq.cross(pupila);

										/*
										 * Mat pupilaDireita = this.drawRightEyeSquare(face, rightEye, faceROI,
										 * CapturadorOlhos.this.imageFrameOlhoDireito);
										 * if (null != pupilaDireita) {
										 * Highgui.imwrite("/imagem/after_circle_right.jpg", pupilaDireita);
										 * int x1 = rightEye.x;
										 * int y1 = rightEye.y;
										 * pupilaDireita.submat(new Rect(0, 0, rightEye.width, rightEye.height)).copyTo(CapturadorOlhos.this.frame
										 * .submat(new Rect((face.x + x1), (face.y + y1), rightEye.width, rightEye.height)));
										 * }
										 */
										//Mat olhoDir = CapturadorOlhos.this.frame.submat(rightEye);
										//this.circles(olhoEsq);
										//this.drawRightEye(face, rightEye);
										//this.circles(olhoDir);

										break;
									}
									break;
								}
							}
							Imgcodecs.imencode(".png", frame, mem);

							Image im = ImageIO.read(new ByteArrayInputStream(mem.toArray()));

							BufferedImage buff = (BufferedImage) im;
							if (g.drawImage(buff, 0, 0, getWidth(), getHeight() - 150, 0, 0,
											buff.getWidth(), buff.getHeight(), null)) {
								if (this.runnable == false) {
									this.wait();
								}
							}
						} catch (

						Exception ex)

						{
							ex.printStackTrace();
						}

					}

				}
			}
		}

		public Mat getIris(Mat olho, Mat gray, Mat edges, Mat circles) {
			
			Imgproc.cvtColor(olho, gray, Imgproc.COLOR_BGR2GRAY);
			
			int lowThreshold = 25;
			int ratio = 20;
			Imgproc.Canny(gray, edges, lowThreshold, lowThreshold * ratio);
			//Imgproc.Canny(olho, edges, lowThreshold, lowThreshold * ratio);
			
			Imgproc.HoughCircles(edges, circles, Imgproc.CV_HOUGH_GRADIENT, 1, edges.rows() / 8, 20, 10, 10, 14);
			double x = 0.0;
			double y = 0.0;
			int r = 0;
			for (int i = 0; i < circles.rows(); i++) {
				double[] data = circles.get(i, 0);
				for (int j = 0; j < data.length; j++) {
					x = data[0];
					y = data[1];
					r = (int) data[2];
				}
				centerLeftEye = new Point(x, y);
				// circle center
				Imgproc.circle(olho, centerLeftEye, 1, COLOR_EYE_CENTER, -1);
				// circle outline
				Imgproc.circle(olho, centerLeftEye, r, COLOR_EYE_BORDER, 2);
				return olho;
			}
			return null;
		}

		public void drawLeftEyeSquare(Rect face, Rect leftEye, Mat faceROI) {
			//Core.rectangle(frame, new Point(face.x + leftEye.x + (leftEye.width), face.y + leftEye.y + (leftEye.height)),
			//		new Point((face.x + leftEye.x + (leftEye.width)) - leftEye.width,
			//						(face.y + leftEye.y + (leftEye.height)) - leftEye.height),
			//		COLOR_CONTORNO_OLHO);
		}
		
		public void drawSquare(Rect face, Rect leftEye, Mat faceROI) {
			//Core.rectangle(frame, new Point(face.x + leftEye.x + (leftEye.width), face.y + leftEye.y + (leftEye.height)),
			//		new Point((face.x + leftEye.x + (leftEye.width)) - leftEye.width,
			//						(face.y + leftEye.y + (leftEye.height)) - leftEye.height),
			//		COLOR_CONTORNO_OLHO);
		}
	}

	/**
	 * Creates new form CapturadorOlhos
	 */
	public CapturadorOlhos() {
		this.initComponents();
	}

	private void initComponents() {

		this.jPanel1 = new javax.swing.JPanel();
		this.jButton1 = new javax.swing.JButton();
		this.jButton2 = new javax.swing.JButton();

		this.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

		javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(this.jPanel1);
		this.jPanel1.setLayout(jPanel1Layout);
		jPanel1Layout.setHorizontalGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 0, Short.MAX_VALUE));
		jPanel1Layout.setVerticalGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 376, Short.MAX_VALUE));

		this.jButton1.setText("Ativar");
		this.jButton1.addActionListener(new java.awt.event.ActionListener() {

			public void actionPerformed(java.awt.event.ActionEvent evt) {

				jButton1ActionPerformed(evt);
			}
		});

		this.jButton2.setText("Desativar");
		this.jButton2.addActionListener(new java.awt.event.ActionListener() {

			public void actionPerformed(java.awt.event.ActionEvent evt) {

				jButton2ActionPerformed(evt);
			}
		});

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this.getContentPane());
		this.getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(layout.createSequentialGroup().addGap(24, 24, 24)
										.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
														.addGroup(layout.createSequentialGroup()
																		.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																		.addGap(39, 39, 39).addComponent(this.jButton1).addGap(86, 86, 86)
																		.addComponent(this.jButton2).addGap(0, 211, Short.MAX_VALUE))
														.addComponent(this.jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE,
																		javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
										.addContainerGap()));
		layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(layout.createSequentialGroup().addContainerGap()
										.addComponent(this.jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
														javax.swing.GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
														.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
																		.addComponent(this.jButton1).addComponent(this.jButton2))
										.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
														))
						.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));

		this.pack();
	}

	private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {

		this.myThread.runnable = false; // stop thread
		this.jButton2.setEnabled(false); // activate start button
		this.jButton1.setEnabled(true); // deactivate stop button

		this.webSource.release(); // libera a camera.

	}

	private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {

		this.webSource = new VideoCapture(0); // captura video da camera zero
		this.myThread = new DaemonThread(); //create object of threat class
		Thread t = new Thread(this.myThread);
		t.setDaemon(true);
		this.myThread.runnable = true;
		t.start(); //start thread
		this.jButton1.setEnabled(false); // deactivate start button
		this.jButton2.setEnabled(true); //  activate stop button

	}

	public static void loadOpenCV() {

		System.load("C:\\kdi-BBTS-4.5\\opencv\\build\\java\\x64\\");
	}

	
	private javax.swing.JButton jButton1;

	private javax.swing.JButton jButton2;

	private javax.swing.JPanel jPanel1;

}

package gui;

import org.opencv.core.Core;

public class RastreadorOlhos {

	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String args[]) {

		//System.out.println(System.getProperty("java.library.path"));
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

		try {
			for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					javax.swing.UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (ClassNotFoundException ex) {
			java.util.logging.Logger.getLogger(CapturadorOlhos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		} catch (InstantiationException ex) {
			java.util.logging.Logger.getLogger(CapturadorOlhos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		} catch (IllegalAccessException ex) {
			java.util.logging.Logger.getLogger(CapturadorOlhos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		} catch (javax.swing.UnsupportedLookAndFeelException ex) {
			java.util.logging.Logger.getLogger(CapturadorOlhos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		}

		/* Cria e exibe o form */
		java.awt.EventQueue.invokeLater(new Runnable() {

			public void run() {

				CapturadorOlhos capturadorOlhos = new CapturadorOlhos();
				capturadorOlhos.setVisible(true);
			}
		});
	}

}

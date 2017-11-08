import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import javax.swing.*;

public class FenetreDijkstra implements ActionListener{

	private Panneau panneau;
	private Game game;
	private JFrame f;


	private int grilleCourante;

	private int width = 40;			// taille en pixel d'une case
	private int height = 40;

	private File nomfichier;

	private JButton nextStepButton = new JButton("Next Step");
	private JButton finalStepButton = new JButton("Final Step");
	private JButton methodeRHC = new JButton("methode RHC");
	private JButton methodeRHM = new JButton("methode RHM");
	private JButton choisirFichier = new JButton("choisir Fichier");
	private JButton restart = new JButton("restart");

	private JLabel imageLabel = new JLabel();


	public Panneau getPanneau()
	{
		return this.panneau;
	}

	public FenetreDijkstra()
	{
		this.f = new JFrame("Rush Hour --- Dijkstra");
		this.panneau = new Panneau();		
		this.game = new Game();

		this.panneau.setBackground(Color.gray);


		//ajout des 2 boutons
		this.nextStepButton.setBorderPainted(false);
		this.finalStepButton.setBorderPainted(false);
		this.restart.setBorderPainted(false);
		this.methodeRHM.setBorderPainted(false);
		this.methodeRHC.setBorderPainted(false);
		this.choisirFichier.setBorderPainted(false);
		this.nextStepButton.addActionListener(this);
		this.finalStepButton.addActionListener(this);
		this.restart.addActionListener(this);
		this.methodeRHM.addActionListener(this);
		this.methodeRHC.addActionListener(this);
		this.choisirFichier.addActionListener(this);
		this.panneau.add(nextStepButton);
		this.panneau.add(finalStepButton);
		this.panneau.add(methodeRHM);
		this.panneau.add(methodeRHC);
		this.panneau.add(choisirFichier);
		this.panneau.add(restart);


		nextStepButton.setEnabled(false);
		finalStepButton.setEnabled(false);
		methodeRHM.setEnabled(false);
		methodeRHC.setEnabled(false);

		this.f.setBounds(300, 200, 500, 500);
		this.f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.f.setLocationRelativeTo(null);

		this.f.setContentPane(this.panneau);
		this.f.setVisible(true);


		ImageIcon ii = new ImageIcon("floaties.gif");
		imageLabel.setIcon(ii);
		imageLabel.setBounds(0,0,ii.getIconWidth(),ii.getIconHeight());
		panneau.setLayout(null);

	}



	public void afficherGrilleFenetre(int numeroGrille)
	{
		this.getPanneau().addRectangle(120, 100, this.width*this.game.getTailleY(), this.height*this.game.getTailleX(), Color.WHITE); // fond blanc de la grille
		this.getPanneau().addRectangle(120+this.game.getTailleY()*this.width, 100+2*this.height, 200, this.height, Color.WHITE); // le rectangle de la sortie

		for(int i=0;i<this.game.getTailleX()+1;i++)	// ajout lignes horizontales
			this.getPanneau().addLine(120, 100+40*i, 360, 100+40*i);
		for(int j=0;j<this.game.getTailleY()+1;j++)	// ajout lignes verticales	
			this.getPanneau().addLine(120+40*j, 100, 120+40*j, 100 + 40*(this.game.getTailleY()));

		for(int i=0; i<this.game.getTailleX(); i++)
			for(int j=0; j<this.game.getTailleY(); j++)
				if(!this.game.getGrillesConfig(numeroGrille)[i*this.game.getTailleX()+j].equals("0"))
					this.getPanneau().addRectangle(120 + (j)*this.width, 100 +i*this.height, this.width, this.height, this.game.getColor(this.game.getGrillesConfig(numeroGrille)[i*this.game.getTailleX()+j]));

		this.f.repaint();
	}

	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == this.nextStepButton) {
			if(this.grilleCourante>0)
				this.grilleCourante--;
			if(this.game.getPlusCourtChemin().size()>0)
				afficherGrilleFenetre(this.game.getPlusCourtChemin().get(this.grilleCourante));
			if(this.grilleCourante==0){
				panneau.add(imageLabel);
				nextStepButton.setEnabled(false);
				finalStepButton.setEnabled(false);
			}
		}
		else if(e.getSource() == this.finalStepButton){
			this.grilleCourante = 0;
			if(this.game.getPlusCourtChemin().size()>0)
				afficherGrilleFenetre(this.game.getPlusCourtChemin().get(0));
			panneau.add(imageLabel);
			nextStepButton.setEnabled(false);
			finalStepButton.setEnabled(false);
		}

		else if(e.getSource() == this.methodeRHC || e.getSource() == this.methodeRHM){
			methodeRHC.setEnabled(false);
			methodeRHM.setEnabled(false);

			if(e.getSource() == this.methodeRHC)
				this.game.resoudreProblemeDijkstra();
			else{
				this.game.setMethodeRHM(true);
				this.game.resoudreProblemeDijkstra();
			}
			this.grilleCourante = this.game.getPlusCourtChemin().size()-1;
			nextStepButton.setEnabled(true);
			finalStepButton.setEnabled(true);

		}

		else if(e.getSource() == this.choisirFichier) {
			File repertoireCourant = null;
			try {
				repertoireCourant = new File(".").getCanonicalFile();
			} catch(IOException ex) {}

			JFileChooser dialogue = new JFileChooser(repertoireCourant);

			dialogue.showOpenDialog(null);
			this.nomfichier = dialogue.getSelectedFile();

			this.methodeRHC.setEnabled(true);
			this.methodeRHM.setEnabled(true);
			this.choisirFichier.setEnabled(false);
			
			this.game.init(this.nomfichier);
			afficherGrilleFenetre(0);
		}
		else if(e.getSource() == this.restart)
		{
			this.nextStepButton.setEnabled(false);
			this.finalStepButton.setEnabled(false);
			this.methodeRHM.setEnabled(false);
			this.methodeRHC.setEnabled(false);
			this.choisirFichier.setEnabled(true);
			this.panneau.remove(imageLabel);
			this.game=new Game();
			
		}
	}



}

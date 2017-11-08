import java.awt.Color;

public class Vehicule{
	
	    private String nom;
	    private int taille;
	    
	    private int posMarqueur;	// pour gurobi

		public String getNom() {
			return nom;
		}

		public void setNom(String nom) {
			this.nom = nom;
		}

		public int getTaille() {
			return taille;
		}

		public void setTaille(int taille) {
			this.taille = taille;
		}
		
		public int getPosMarqueur() {
			return posMarqueur;
		}

		public void setPosMarqueur(int marqueur) {
			this.posMarqueur = marqueur;
		}

		public Color getColor() {
			return color;
		}

		public void setColor(Color color) {
			this.color = color;
		}

		public int getDirectionDeplacement() {
			return directionDeplacement;
		}

		public void setDirectionDeplacement(int directionDeplacement) {
			this.directionDeplacement = directionDeplacement;
		}

		Color color;
	    int directionDeplacement;   // 0 horizontale et 1 verticale

	    public Vehicule(String nom, Color color, int directionDeplacement, int marqueur) {
	    	this.nom = nom;
	    	if(nom.startsWith("c") || nom.startsWith("g"))
	    		this.taille=2;
	    	else
	    		this.taille=3;
	        this.color = color;
	        this.directionDeplacement = directionDeplacement;
	        this.posMarqueur = marqueur;
	    }
	    
	    public Vehicule(Vehicule v)
	    {
	    	this.nom = v.nom;
	    	this.taille = v.taille;
	    	this.color = v.color;
	    	this.directionDeplacement = v.directionDeplacement;
	    	
	    }
	    
	    public String toString()
	    {
	    	return "Vehicule "+this.nom+ "-- Deplacement: "+ this.directionDeplacement+ "   "+ this.color+"   posMarqueur: "+this.posMarqueur;
	    }
	}
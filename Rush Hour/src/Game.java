import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class Game {

	private int tailleX = 0;		// nombre de cases de la grille
	private int tailleY = 0;
	private boolean resolu = false;
	private boolean methodeRHM = false;
	private int sommetFin = 0;

	private ArrayList<String[]> grillesConfig = new ArrayList<String[]>();
	private ArrayList<ArrayList<Pair>> matGrillesConfig = new ArrayList<ArrayList<Pair>>();
	private ArrayList<Integer> configurationFin = new ArrayList<Integer>();
	private ArrayList<Integer> plusCourtChemin = new ArrayList<Integer>();
	private ArrayList<Vehicule> vehicules = new ArrayList<Vehicule>();

	public int getTailleX(){return this.tailleX;}
	public int getTailleY() {return this.tailleY;}
	public String[] getGrillesConfig(int numeroGrille) {return this.grillesConfig.get(numeroGrille);}
	public ArrayList<Integer> getPlusCourtChemin() {return this.plusCourtChemin;}
	public boolean getResolu() {return this.resolu;}
	public ArrayList<Vehicule> getVehicules() {return this.vehicules;}
	public void setMethodeRHM(boolean b){this.methodeRHM = b;}
	public Color getColor(String nom)
	{
		for(Vehicule v: this.vehicules)
			if (v.getNom().equals(nom))
				return v.getColor();
		return null;
	}

	public Game(){}

	public void init(File fichier)
	{
		this.grillesConfig.add(new String[this.tailleX*this.tailleY]);
		chargerNiveau(fichier.getAbsolutePath());

		this.matGrillesConfig.add(new ArrayList<Pair>());
		this.matGrillesConfig.get(0).add(new Pair(0,0));

		initVehicules();

	}

	public void chargerNiveau(String nomFichier)
	{
		Scanner scanner = null;
		try {
			scanner = new Scanner(new FileReader(nomFichier));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		String mot = null;
		int i = 0,j = 0;
		if(scanner.hasNext())
			this.tailleX = Integer.parseInt(scanner.next());
		if(scanner.hasNext())
			this.tailleY = Integer.parseInt(scanner.next());
		this.grillesConfig.set(0, new String[this.tailleX*this.tailleY]);

		while (scanner.hasNext()) {
			mot = scanner.next();
			this.grillesConfig.get(0)[coordToInt(i, j)]=mot;
			if(j<this.tailleY-1)
				j++;
			else
			{
				j=0;
				i++;
			}
		}
	}

	public void resoudreProblemeDijkstra()
	{

		long start = System.currentTimeMillis();
		genererConfig();
		System.out.println("temps d'execution genererConfig: "+(System.currentTimeMillis()-start)+" ms");

		if(this.configurationFin.size()>0)
		{
			long debut = System.currentTimeMillis();

			dijkstra();

			System.out.println("temps d'execution dijkstra: "+(System.currentTimeMillis()-debut)+" ms");


			this.resolu = true;

			System.out.println("Valeur de la fonction objectif: "+this.matGrillesConfig.get(this.sommetFin).get(0).getR());

			System.out.println("configuration fin possible "+this.configurationFin.size());
			System.out.println("Nb configurations possibles: "+this.grillesConfig.size());


		}
		else
			System.out.println("La configuration de départ ne donne aucune solution au problème, voiture rouge perdu à tout jamais dans la circulation");


	}

	public void afficherConfig(int numero)
	{
		for(int i = 0; i<6; i++){
			System.out.println();
			for(int j=0; j<6; j++)
			{
				System.out.print("   "+this.grillesConfig.get(numero)[coordToInt(i, j)]);
			}
			System.out.println();
		}
	}

	public void afficherGrille(String grille[])
	{
		for(int i = 0; i<6; i++){
			System.out.println();
			for(int j=0; j<6; j++)
			{
				System.out.print("   "+grille[coordToInt(i, j)]);
			}
			System.out.println();
		}
	}

	public Vehicule recupV(String s)
	{
		for(Vehicule v : this.vehicules)
			if(v.getNom().equals(s))
				return v;
		return null;
	}

	public void	initVehicules()
	{
		Random rand = new Random();
		int directionVehicule;

		for(int i=0; i<this.tailleX*this.tailleY; i++ ){
			Color color = null;
			if(!(this.grillesConfig.get(0)[i].equals("0")))
			{
				Vehicule v = recupV(this.grillesConfig.get(0)[i]);
				if(v == null)
				{
					if(this.grillesConfig.get(0)[i].equals("g")){
						color = Color.red;
					}
					else
						color = new Color(rand.nextFloat(), rand.nextFloat(), rand.nextFloat());

					if(i+1<this.tailleX*this.tailleY && this.grillesConfig.get(0)[i+1].equals(this.grillesConfig.get(0)[i])) //determiner direction vehicule
						directionVehicule = 0;
					else
						directionVehicule = 1;

					v = new Vehicule(this.grillesConfig.get(0)[i], color, directionVehicule, i);
					this.vehicules.add(v);
				}
			}
		}

	}

	public void genererConfig()
	{
		int posIvehicule;
		int posJvehicule;
		for(int i=0; i<this.grillesConfig.size();i++)
		{
			for(int v=0; v< this.vehicules.size();v++)
			{

				posIvehicule = nomToCoordI(this.vehicules.get(v).getNom(), i);
				posJvehicule = nomToCoordJ(this.vehicules.get(v).getNom(), i);


				if(this.vehicules.get(v).getDirectionDeplacement() == 0){
					gauche(posIvehicule, posJvehicule, this.vehicules.get(v).getTaille(), this.vehicules.get(v).getNom() , i);
					droite(posIvehicule, posJvehicule, this.vehicules.get(v).getTaille(), this.vehicules.get(v).getNom() , i);

				}
				else 
				{
					haut(posIvehicule, posJvehicule, this.vehicules.get(v).getTaille(), this.vehicules.get(v).getNom() , i);
					bas(posIvehicule, posJvehicule, this.vehicules.get(v).getTaille(), this.vehicules.get(v).getNom() , i);
				}

			}
		}
	}

	public int nomToPos(String nom, int grille)
	{
		for(int i=0; i<this.tailleX; i++)
			if(this.grillesConfig.get(grille)[i].equals(nom))
				return i;
		return -1;
	}

	public int nomToCoordI(String nom, int grille)
	{
		for(int i=0; i<this.tailleX; i++)
			for(int j=0; j<this.tailleY; j++)
				if(this.grillesConfig.get(grille)[coordToInt(i, j)].equals(nom))
					return i;
		return -1;
	}

	public int nomToCoordJ(String nom, int grille)
	{
		for(int i=0; i<this.tailleX; i++)
			for(int j=0; j<this.tailleY; j++)
				if(this.grillesConfig.get(grille)[coordToInt(i, j)].equals(nom))
					return j;
		return -1;
	}

	public void gauche(int posIvehicule, int posJvehicule, int taille, String nom, int grilleCourrante)
	{
		int cout = 1;
		String temporaire[] = new String[this.tailleX*tailleY];


		while(caseVide(posIvehicule, posJvehicule-cout, this.grillesConfig.get(grilleCourrante))) // tant que case de gauche libre
		{
			temporaire = new String[this.tailleX*tailleY];
			System.arraycopy(this.grillesConfig.get(grilleCourrante), 0, temporaire, 0, this.tailleX*this.tailleY);

			for(int t=0; t<taille;t++){
				temporaire[coordToInt(posIvehicule, posJvehicule+t)]="0";
			}

			for(int t=0; t<taille;t++){
				temporaire[coordToInt(posIvehicule, posJvehicule-cout+t)]=nom;
			}

			int res = existeDeja(temporaire);

			if(res ==-1)
			{
				this.matGrillesConfig.add(new ArrayList<Pair>());
				this.matGrillesConfig.get(this.matGrillesConfig.size()-1).add(new Pair(this.matGrillesConfig.size()-1,(int) Float.POSITIVE_INFINITY));
				this.grillesConfig.add(temporaire);

				if(this.methodeRHM)
					ajoutLien(grilleCourrante, this.grillesConfig.size()-1, 1);
				else
					ajoutLien(grilleCourrante, this.grillesConfig.size()-1, cout);
				if(temporaire[17].equals("g")){
					configurationFin.add(this.grillesConfig.size()-1);
				}
			}
			else
			{
				ajoutLien(grilleCourrante, res, cout);
			}

			cout++;

		}

	}

	public void droite(int posIvehicule, int posJvehicule, int taille, String nom, int grilleCourrante)
	{
		int cout = 1;
		String temporaire[] = new String[this.tailleX*tailleY];

		while(caseVide(posIvehicule, posJvehicule+taille+cout-1, this.grillesConfig.get(grilleCourrante))) // tant que case de droite est libre
		{
			temporaire = new String[this.tailleX*tailleY];
			System.arraycopy(this.grillesConfig.get(grilleCourrante), 0, temporaire, 0, this.tailleX*this.tailleY);

			for(int t=0; t<taille;t++){
				temporaire[coordToInt(posIvehicule, posJvehicule+t)]="0";
			}

			for(int t=0; t<taille;t++){
				temporaire[coordToInt(posIvehicule, posJvehicule+cout+t)]=nom;
			}


			int res = existeDeja(temporaire);


			if(res ==-1)
			{
				this.grillesConfig.add(temporaire);
				this.matGrillesConfig.add(new ArrayList<Pair>());
				this.matGrillesConfig.get(this.matGrillesConfig.size()-1).add(new Pair(this.matGrillesConfig.size()-1,(int) Float.POSITIVE_INFINITY));
				if(this.methodeRHM)
					ajoutLien(grilleCourrante, this.grillesConfig.size()-1, 1);
				else
					ajoutLien(grilleCourrante, this.grillesConfig.size()-1, cout);
				if(temporaire[17].equals("g"))
					configurationFin.add(this.grillesConfig.size()-1);
			}
			else
			{
				ajoutLien(grilleCourrante, res, cout);
			}
			cout++;
		}
	}
	public void haut(int posIvehicule, int posJvehicule, int taille, String nom, int grilleCourrante)
	{
		int cout = 1;
		String temporaire[] = new String[this.tailleX*tailleY];


		while(caseVide(posIvehicule-cout, posJvehicule, this.grillesConfig.get(grilleCourrante))) // tant que case du haut libre
		{
			temporaire = new String[this.tailleX*tailleY];
			System.arraycopy(this.grillesConfig.get(grilleCourrante), 0, temporaire, 0, this.tailleX*this.tailleY);

			for(int t=0; t<taille;t++){
				temporaire[coordToInt(posIvehicule+t, posJvehicule)]="0";
			}

			for(int t=0; t<taille;t++){
				temporaire[coordToInt(posIvehicule-cout+t, posJvehicule)]=nom;
			}

			int res = existeDeja(temporaire);
			if(res ==-1)
			{
				this.matGrillesConfig.add(new ArrayList<Pair>());
				this.matGrillesConfig.get(this.matGrillesConfig.size()-1).add(new Pair(this.matGrillesConfig.size()-1,(int) Float.POSITIVE_INFINITY));
				this.grillesConfig.add(temporaire);
				if(this.methodeRHM)
					ajoutLien(grilleCourrante, this.grillesConfig.size()-1 , 1);
				else
					ajoutLien(grilleCourrante, this.grillesConfig.size()-1 , cout);
				if(temporaire[17].equals("g"))
					configurationFin.add(this.grillesConfig.size()-1);
			}
			else
			{
				ajoutLien(grilleCourrante, res, cout);
			}
			cout++;
		}
	}
	public void bas(int posIvehicule, int posJvehicule, int taille, String nom, int grilleCourrante)
	{

		int cout = 1;
		String temporaire[] = new String[this.tailleX*tailleY];

		while(caseVide(posIvehicule+cout+taille-1, posJvehicule, this.grillesConfig.get(grilleCourrante)))
		{
			temporaire= new String[this.tailleX*tailleY];
			System.arraycopy(this.grillesConfig.get(grilleCourrante), 0, temporaire, 0, this.tailleX*this.tailleY);


			for(int t=0; t<taille;t++){
				temporaire[coordToInt(posIvehicule+t, posJvehicule)]="0";
			}

			for(int t=0; t<taille;t++){
				temporaire[coordToInt(posIvehicule+cout+t, posJvehicule)]=nom;
			}


			int res = existeDeja(temporaire);
			if(res ==-1)
			{
				this.matGrillesConfig.add(new ArrayList<Pair>());
				this.matGrillesConfig.get(this.matGrillesConfig.size()-1).add(new Pair(this.matGrillesConfig.size()-1,(int) Float.POSITIVE_INFINITY));
				this.grillesConfig.add(temporaire);
				if(this.methodeRHM)
					ajoutLien(grilleCourrante, this.grillesConfig.size()-1, 1);
				else
					ajoutLien(grilleCourrante, this.grillesConfig.size()-1, cout);
				if(temporaire[17].equals("g"))
					configurationFin.add(this.grillesConfig.size()-1);
			}
			else
			{
				ajoutLien(grilleCourrante, res, cout);
			}
			cout++;

		}
	}

	public void ajoutLien(int sommet1, int sommet2, int cout)
	{
		this.matGrillesConfig.get(sommet1).add(new Pair(sommet2, cout));
	}




	// retourne le numero de la grille identique sinon -1
	public int existeDeja(String temporaire[])
	{	
		for(int z=0; z<this.grillesConfig.size();z++){
			if(comparer2Grilles(this.grillesConfig.get(z), temporaire)){

				return z;
			}
		}
		return -1;

	}

	public boolean comparer2Grilles(String grille1[], String grille2[])
	{
		for(int i=0; i<this.tailleX*this.tailleY; i++)
			if(!grille1[i].equals(grille2[i]))
				return false;
		return true;
	}

	public boolean caseVide(int posI, int posJ, String temporaire[])
	{
		if(posI>this.tailleX-1 || posI<0 || posJ>this.tailleY-1 || posJ<0)
		{
			return false;
		}


		if(temporaire[coordToInt(posI, posJ)].equals("0"))
			return true;
		else 
			return false;
	}

	public void dijkstra(){

		int[] tabPredecesseur = new int[this.matGrillesConfig.size()];
		int[] dejaParcouru = new int[this.matGrillesConfig.size()];

		for(int i =0; i<this.matGrillesConfig.size(); i++){
			dejaParcouru[i]=0;
			tabPredecesseur[i]=i;
		}


		int sommetCourant=0;
		dejaParcouru[sommetCourant]=1;

		int compte = 0;
		while(!configurationFin.contains(sommetCourant))
		{
			compte++;
			for(Pair fils : this.matGrillesConfig.get(sommetCourant)){

				if(dejaParcouru[fils.getL()]==0 && fils.getL() != sommetCourant){
					if(fils.getR() + this.matGrillesConfig.get(sommetCourant).get(0).getR() < this.matGrillesConfig.get(fils.getL()).get(0).getR())
					{
						this.matGrillesConfig.get(fils.getL()).get(0).setR(fils.getR() + this.matGrillesConfig.get(sommetCourant).get(0).getR());

						tabPredecesseur[fils.getL()]=sommetCourant;
					}
				}
			}

			sommetCourant=choisirSommet(dejaParcouru);

			if(sommetCourant!=-1){
				dejaParcouru[sommetCourant]=1;
			}
		}
		this.sommetFin = sommetCourant;
		System.out.println("compte: "+compte);

		//reconstruction du chemin

		do		
		{
			this.plusCourtChemin.add(sommetCourant);
			sommetCourant = tabPredecesseur[sommetCourant];
		}
		while(tabPredecesseur[sommetCourant]!=0);
		this.plusCourtChemin.add(sommetCourant);
		this.plusCourtChemin.add(0);

	}

	public int getSommetcout(int sommetCourant, int sommetArrive)
	{
		for(Pair p: this.matGrillesConfig.get(sommetCourant)){
			if(p.getL()==sommetArrive)
			{
				return p.getR();
			}
		}
		return 0;
	}

	public int choisirSommet(int[] dejaParcouru)
	{	
		int minimum=(int) Float.POSITIVE_INFINITY;
		int indice = -1;
		for(int i=0; i<this.matGrillesConfig.size(); i++){
			if(dejaParcouru[i]==0 && this.matGrillesConfig.get(i).get(0).getR() < minimum)
			{
				indice = i;
				minimum = this.matGrillesConfig.get(i).get(0).getR();
			}
		}
		return indice;
	}

	public int coordToInt(int i, int j)
	{
		return (i*this.tailleX)+j;
	}
}
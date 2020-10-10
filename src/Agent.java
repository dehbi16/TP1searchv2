import java.util.ArrayList;
import java.util.List;

public class Agent implements Runnable {
	protected int x;
	protected int y;
	protected Game g;
	protected Thread t;
	private State[][] L;
	protected static List <List<Direction>> solution;
	protected static List<Integer> heuristique;
	protected int h;
	protected int hmax;
	protected int nbaspirer = 0;
	protected int nbbijoux = 0;
	protected int cout = 0;
	protected int erreur = 0;


	public Agent(int x, int y, Game g) {
		t = new Thread(this);
		this.x = x;
		this.y = y;
		this.g = g;
		init();	
		t.start();
	}

	public void run() {
		while(g.isRunning) {
			try {
				Thread.sleep(300);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			int index;
			switch(g.mode) {
			case 0:
				index = goal0();
				if(index>=0) { 
					move(index);
					init();
					if (isclean())	g.isRunning = false;
				}
				else {
					List <Direction> a;
					List <Direction> position;
					int size = solution.size();

					for (int i=0; i<size; i++) {
						System.out.println(solution.get(0)+" ");
						a = solution.get(0);
						solution.remove(0);
						position = positions(a);
						for (int j=0; j<position.size(); j++) {
							List <Direction> b = new ArrayList<Direction>();
							b.addAll(a);
							b.add(position.get(j));
							solution.add(b);
						}
					}

				}
				break;

			case 1:
				index = goal1();
				if (index>= 0) {
					move(index); 
					init();
					if (isclean()) g.isRunning=false;
				}
				else {
					List <Direction> a;
					List <Direction> position;
					int size = solution.size();
					int x;
					for (int i=0; i<size; i++) {
						a = solution.get(0);
						solution.remove(0);
						heuristique.remove(0); 
						position = positions(a);
						for (int j=0; j<position.size(); j++) {
							List <Direction> b = new ArrayList<Direction>();
							b.addAll(a);
							b.add(position.get(j));
							x = calculH(b);
							if (x>=h) {
								solution.add(b);
								heuristique.add(x);
								h = x;
							}
						}
					}
				}
				break; 
			}
		}
	}

	private int calculH(List<Direction> b) {
		int hbis=0;
		int positionx = this.x;
		int positiony = this.y;
		int [] resultat;
		for(int i=0; i<b.size(); i++) {
			resultat = calculeP(b.get(i), positiony, positionx);
			positiony = resultat[0];
			positionx = resultat[1];
			if (this.L[positiony][positionx] == State.dust || this.L[positiony][positionx] == State.dustjewelry || this.L[positiony][positionx] == State.jewelry ) {
				hbis++;
			}
		}
		return hbis;
	}

	private int goal1() {
		for(int i=0; i<heuristique.size(); i++) {
			if(heuristique.get(i) == hmax ) {
				return i;  
			}
		}
		return -1;
	}

	private List<Direction> positions(List<Direction> a) {
		int newl = this.y;
		int newc = this.x;
		int [] resultat = new int[2];
		resultat[0] = newl;
		resultat[1] = newc;
		List<Direction> directionPossible = new ArrayList<Direction>();
		List<int[]> visited = new ArrayList<int[]>();
		visited.add(resultat);
		int i;
		for (i=0; i<a.size(); i++) {
			resultat = calculeP(a.get(i),newl, newc);
			newl = resultat[0];
			newc = resultat[1];
			visited.add(resultat);
		}
		i--;
		if (newl!=0 && !contains(visited, newc, newl-1)) { // && !contains(visited, col, lig) col=newc, lig=newl-1
			directionPossible.add(Direction.haut);
		} 
		if (newl!=4 && !contains(visited, newc, newl+1)) directionPossible.add(Direction.bas);
		if (newc!=0 && !contains(visited, newc-1, newl)) directionPossible.add(Direction.gauche);
		if (newc!=4 && !contains(visited, newc+1, newl)) directionPossible.add(Direction.droite);


		return directionPossible;
	}

	private boolean contains(List<int[]> visited, int col, int lig) {
		for(int i=0; i<visited.size(); i++) {
			if (visited.get(i)[0]== lig && visited.get(i)[1]==col) {
				return true;
			}
		}


		return false;
	}

	private int[] calculeP(Direction direction, int newl, int newc) {
		int [] resultat = new int [2];
		if (direction == Direction.bas) newl++;
		else if (direction == Direction.droite) newc++;
		else if (direction == Direction.gauche) newc--;
		else newl--;  
		resultat[0] = newl;
		resultat[1] = newc;
		return resultat;
	}

	private void move(int index) {

		g.env.L[this.y][this.x] = State.empty;
		int newl = this.y;
		int newc = this.x;
		int [] resultat;
		for (int i=0; i<solution.get(index).size(); i++) {
			g.env.L[newl][newc] = State.empty;
			resultat = calculeP(solution.get(index).get(i), newl, newc);
			newl = resultat[0];
			newc = resultat[1];
			cout++;
			if (this.L[newl][newc] == State.dust) {
				nbaspirer++;
				cout++;
				if (g.env.L[newl][newc] == State.jewelry || g.env.L[newl][newc] == State.dustjewelry) erreur++;
				g.env.L[newl][newc] = State.empty;
			}
			else if (this.L[newl][newc] == State.jewelry) {
				nbbijoux++;
				cout++;
				g.env.L[newl][newc] = State.empty;
			}
			else if (this.L[newl][newc] == State.dustjewelry) {
				nbbijoux++;
				nbaspirer++;
				cout+=2;
				g.env.L[newl][newc] = State.empty; 
			}
			g.env.L[newl][newc] = State.robot;
			try {
				Thread.sleep(300);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		this.y = newl;
		this.x = newc;
	}

	private int goal0() {
		int [] resultat;
		int newl;
		int newc;
		for(int i=0; i<solution.size(); i++) {
			newl = this.y;
			newc = this.x;
			for (int j=0; j<solution.get(i).size(); j++) {
				resultat = calculeP(solution.get(i).get(j), newl, newc);
				newl = resultat[0];
				newc = resultat[1];
			}
			if (this.L[newl][newc] == State.dust || this.L[newl][newc] == State.jewelry || this.L[newl][newc] == State.dustjewelry) {
				return i;
			}
		}
		return -1;
	}

	private void init() {
		this.L = new State[5][5];
		for(int i=0; i<5; i++) {
			for(int j=0; j<5; j++) {
				this.L[i][j] = g.env.L[i][j];
			}
		}

		if (g.mode==1) {
			hmax = 0;
			for(int i=0; i<5; i++) {
				for(int j=0; j<5; j++) {
					if (this.L[i][j] == State.dust || this.L[i][j] == State.jewelry || this.L[i][j] == State.dustjewelry) {
						hmax++;
					}
				}
			}
		}
		//System.out.println(hmax);
		solution = new ArrayList<List<Direction>>();
		if (g.mode==1) heuristique = new ArrayList<Integer>();
		List <Direction> a;
		h=0;
		if (this.x != 0) {
			a = new ArrayList<Direction>();
			a.add(Direction.gauche);
			solution.add(a);
			if (g.mode==1) heuristique.add(0);
		}
		if (this.x != 4) { 
			a = new ArrayList<Direction>();
			a.add(Direction.droite);
			solution.add(a);
			if (g.mode==1) heuristique.add(calculH(a));
		}
		if (this.y != 0) {
			a = new ArrayList<Direction>();
			a.add(Direction.haut);
			solution.add(a);
			if (g.mode==1) heuristique.add(calculH(a));
		}
		if (this.y != 4) {
			a = new ArrayList<Direction>();
			a.add(Direction.bas);
			solution.add(a);
			if (g.mode==1) heuristique.add(calculH(a));
		}
	}

	private boolean isclean() {
		for(int i=0; i<5; i++) {
			for(int j=0; j<5; j++) {
				if (this.L[i][j] == State.dust || this.L[i][j] == State.jewelry || this.L[i][j] == State.dustjewelry) 
					return false;
			}
		}
		return true;
	}

}
